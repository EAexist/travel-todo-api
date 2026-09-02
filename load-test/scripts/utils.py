import json
import os
import subprocess
import time
import urllib
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List

docker_compose_env = {"COMPOSE_FILE": "compose.loadtest.yml"}


@dataclass
class LoadTestRun:
    test_id: str
    iteration: int

    @property
    def output_path(self):
        return f"output/{self.test_id}/{self.iteration}"

    @property
    def test_summary_path(self):
        return f"{self.output_path}/test-summary.json"


def get_output_dir(test_id: str):
    output_path = Path(__file__).resolve().parent.parent / "output" / test_id
    return output_path


def get_perf_result_path(test_id: str):
    perf_result_path = get_output_dir(test_id=test_id) / "result.json"
    return perf_result_path


def get_manifest_path(test_id: str):
    manifest_path = get_output_dir(test_id=test_id) / "manifest.json"
    return manifest_path


def record_run(test_id: str, run: LoadTestRun) -> None:

    manifest_path = get_manifest_path(test_id)
    if manifest_path.exists():
        with open(manifest_path, "r", encoding="utf-8") as f:
            manifest = json.load(f)
    else:
        manifest = {"test_id": test_id, "runs": []}

    new_entry = {
        "iteration": run.iteration,
        "path": f"{run.output_path}",
    }

    manifest["runs"].append(new_entry)
    manifest["runs"].sort(key=lambda x: x["iteration"])

    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2)


def run_cmd(cmd, check=True, env={}):
    try:
        result = subprocess.run(
            cmd,
            shell=True,
            capture_output=True,
            text=True,
            check=check,
            env={**os.environ.copy(), **env},
        )
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        print(f"Error executing: {cmd}\n{e.stderr}")
        return None


def run_wsl_cmd(command: str) -> str:
    """Executes a Linux command inside WSL2 from the Windows host."""
    result = subprocess.run(
        ["wsl.exe", "-d", "docker-desktop", "-e", "/bin/sh", "-c", command],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )

    if result.returncode != 0:
        raise RuntimeError(
            f"WSL docker-desktop command failed "
            f"(exit {result.returncode}): {result.stderr.strip()}"
        )

    return result.stdout.strip()


def verify_container_cpu_isolation_config():
    print("=== Verifying CPU Isolation Config ===")

    # 1. Validate /proc/cmdline
    print("- Checking /proc/cmdline...")
    cmdline = run_wsl_cmd("cat /proc/cmdline")
    if "isolcpus=" not in cmdline:
        raise RuntimeError(f"isolcpus= not found in /proc/cmdline: {cmdline}")
    print("  - isolcpus= present: True")


def get_running_containers():
    # Get container names managed by the current docker-compose project
    output = run_cmd("docker compose ps --format json", env={**docker_compose_env})
    if not output:
        return []

    try:
        # docker compose ps --format json returns a list of objects as strings
        containers = [json.loads(line) for line in output.splitlines() if line.strip()]
        container_names = [c["Name"] for c in containers]
        return container_names
    except (json.JSONDecodeError, KeyError):
        print("Failed to parse container names from docker compose.")
        return []


def verify_containers_resource_config():
    container_names = get_running_containers()
    if not container_names:
        print("No running containers found.")
        return
    results = {}
    for name in container_names:
        raw_inspect = run_cmd(f"docker inspect {name}")
        if not raw_inspect:
            continue

        try:
            data = json.loads(raw_inspect)[0]
            host_config = data.get("HostConfig", {})

            mem_res = host_config.get("MemoryReservation", 0) / (1024 * 1024)  # MB
            cpu_set = host_config.get("CpusetCpus", "Not Set")
            mem_limit = host_config.get("Memory", 0) / (1024 * 1024)  # MB

            print(f"[{name}]")
            print(f"  - Memory Reservation: {mem_res} MB (Limit: {mem_limit} MB)")
            print(f"  - CpusetCpus: {cpu_set}")

            results[name] = {
                "MemoryReservationMB": mem_res,
                "MemoryLimitMB": mem_limit,
                "CpusetCpus": cpu_set,
            }
        except (json.JSONDecodeError, KeyError) as e:
            print(f"Failed to inspect {name}: {e}")
            continue
    return results


def is_exporter_running(
    host: str = "127.0.0.1", port: int = 9182, timeout: float = 1.0
) -> bool:
    """Checks if windows_exporter is already responding on the expected port."""
    url = f"http://{host}:{port}/metrics"
    try:
        with urllib.request.urlopen(url, timeout=timeout) as resp:
            return resp.status == 200
    except Exception:
        return False


def wait_for_exporter(
    url: str = "http://localhost:9182/metrics", timeout: int = 15
) -> bool:
    """Polls the exporter until it returns HTTP 200 or times out."""
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            with urllib.request.urlopen(url, timeout=1) as response:
                if response.status == 200:
                    return True
        except (urllib.error.URLError, TimeoutError, ConnectionResetError):
            pass
        time.sleep(1)
    return False


def launch_host_memory_metrics_tracking(
    exe_path: str,
) -> subprocess.Popen:
    """Launches Prometheus Windows Exporter on the Windows host.

    Args:
        config_path: Path to the host Prometheus Windows Exporter configuration file.

    Returns:
        subprocess.Popen instance managing the background Prometheus Windows Exporter process.
    """
    if is_exporter_running():
        print(
            "[+] Prometheus Windows Exporter is already running on port 9182. Skipping launch."
        )
        return None

    config_file = (Path.cwd().resolve() / "windows_exporter.config.yml").as_posix()

    cmd = [
        exe_path,
        f"--config.file={config_file}",
        "--web.listen-address=0.0.0.0:9182",
    ]

    # Launch Prometheus Windows Exporter as an independent background process
    process = subprocess.Popen(
        cmd,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.PIPE,
        text=True,
        creationflags=subprocess.CREATE_NEW_CONSOLE,  # Opens in a separate console window on Windows
    )

    if not wait_for_exporter("http://localhost:9182/metrics", timeout=15):
        if process.poll() is not None:
            _, stderr = process.communicate()
            raise RuntimeError(f"Exporter process exited prematurely:\n{stderr}")
        else:
            process.kill()
            raise TimeoutError(
                "Prometheus Windows Exporter failed to respond within 15 seconds."
            )

    print(f"[+] Prometheus Windows Exporter started successfully (PID: {process.pid})")
    return process


def terminate_host_memory_metrics_tracking(process: subprocess.Popen | None) -> None:
    """Terminates the Prometheus Windows Exporter process if managed by Python."""
    if process is None or process.poll() is not None:
        print("[+] No active exporter process to terminate.")
        return

    print(f"[+] Terminating Prometheus Windows Exporter (PID: {process.pid})...")
    process.terminate()  # Sends SIGTERM signal

    try:
        process.wait(timeout=5)
        print("[+] Exporter terminated successfully.")
    except subprocess.TimeoutExpired:
        print("[!] Exporter did not exit gracefully; forcing kill...")
        process.kill()  # Sends SIGKILL signal
        process.wait()
        print("[+] Exporter killed.")


def launch_host_memory_metrics_tracking_alloy(
    config_path: str = "config.host.alloy",
) -> subprocess.Popen:
    """Launches Grafana Alloy CLI on the Windows host using config.host.alloy.

    Args:
        config_path: Path to the host Alloy configuration file.

    Returns:
        subprocess.Popen instance managing the background Alloy process.
    """
    config_path = os.path.join(os.getcwd(), config_path)

    if not os.path.exists(config_path):
        raise FileNotFoundError(f"Alloy config not found at: {config_path}")
    cmd = ["alloy", "run", config_path]

    # Launch Alloy as an independent background process
    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        creationflags=subprocess.CREATE_NEW_CONSOLE,  # Opens in a separate console window on Windows
    )

    # Brief delay to allow Alloy to initialize and validate config
    time.sleep(2)

    # Check if Alloy terminated immediately due to bad arguments or invalid config
    if process.poll() is not None:
        _, stderr = process.communicate()
        raise RuntimeError(f"Failed to start Grafana Alloy:\n{stderr}")

    print(f"[+] Grafana Alloy started successfully (PID: {process.pid})")
    return process

    # !Deprecated!
    # Do not try to validate with CPU affinity.
    # # CPU affinity is higher layer than the cpuisol configuration, so even with cpuisol, the CPU affinity always shows all available CPUs of WSL (e.g. affinity 0~4 even when cpuisol=0,1,2)
    #     print("\n=== 2. Verifying Internal CPU Pinning (taskset) ===")
    #     for name in container_names:
    #         try:
    #             affinity = run_cmd(f"docker exec {name} taskset -c -p 1").strip()
    #
    #             print(f"[{name}] {affinity}")
    #             results[name]["TasksetAffinity"] = affinity
    #         except Exception as e:
    #             print(f"[{name}] Error running taskset command: {e}")
    #             results[name]["TasksetAffinity"] = f"Error: {e}"

    return results


def get_container_id(name: str) -> str:
    return run_cmd(f"docker inspect -f '{{{{.Id}}}}' {name}")


def read_cgroup_ns(path: str) -> List[int]:
    """Reads total nanoseconds from a cgroup v1 cpuacct file."""
    return [int(v) for v in run_cmd(f"wsl cat {path}").split()]


def find_container_cgroup_v1_path(container_id: str) -> str:
    """Locates the cpuacct.usage path for cgroupfs or systemd drivers."""
    candidates = [
        f"/sys/fs/cgroup/cpuacct/docker/{container_id}/cpuacct.usage_percpu",
        f"/sys/fs/cgroup/cpuacct/system.slice/docker-{container_id}.scope/cpuacct.usage_percpu",
        f"/sys/fs/cgroup/cpuacct/docker-{container_id}.scope/cpuacct.usage_percpu",
    ]
    for path in candidates:
        if subprocess.run(f"wsl test -f {path}", shell=True).returncode == 0:
            return path
    raise FileNotFoundError(
        f"Could not locate cpuacct.usage for container {container_id}"
    )


def take_snapshot(container_names: List[str]) -> Dict[str, int]:
    snap = {"system": read_cgroup_ns("/sys/fs/cgroup/cpuacct/cpuacct.usage_percpu")}
    for name in container_names:
        cid = get_container_id(name)
        cgroup_path = find_container_cgroup_v1_path(cid)
        snap[name] = read_cgroup_ns(cgroup_path)
    print(f"snap:\n {'\n'.join([s for s in snap])}")
    return snap


def verify_cpu_isolation(
    pre_snap: Dict[str, List[int]],
    post_snap: Dict[str, List[int]],
    cpu_map: Dict[int, List[str]],
    tolerance_ms: float = 1.0,
) -> bool:
    """
    Audits isolation based on exact CPU-to-Container mappings.
    cpu_map format: { cpu_id: ["assigned_container_1", "assigned_container_2"] }
    """
    tolerance_ns = int(tolerance_ms * 1_000_000)
    cpu_results = {}
    all_passed = True

    print("\n=== Mapped CPU Isolation Audit ===")
    for cpu, assigned_containers in cpu_map.items():
        # System total delta on this specific CPU
        sys_delta = post_snap["system"][cpu] - pre_snap["system"][cpu]
        sys_delta_ms = sys_delta / 1e6

        # Sum deltas ONLY from containers assigned to this specific CPU
        assigned_containers_delta = sum(
            post_snap[c][cpu] - pre_snap[c][cpu] for c in assigned_containers
        )
        assigned_containers_delta_ms = assigned_containers_delta / 1e6

        # Any leftover delta on this CPU came from external/unassigned processes
        leakage_ns = sys_delta - assigned_containers_delta
        leakage_ms = leakage_ns / 1e6
        passed = leakage_ns <= tolerance_ns

        cpu_results[cpu] = {
            "leakage_ms": leakage_ms,
            "sys_delta_ms": sys_delta_ms,
            "containers_delta_ms": assigned_containers_delta_ms,
            "passed": passed,
        }

        all_passed = all_passed and passed

        status = "PASS" if passed else "FAIL"
        print(f"CPU [{cpu}]: Non-target Leakage = {leakage_ms:6.2f} ms [{status}]")

        container_str = ", ".join(assigned_containers)
        print(f"CPU [{cpu}] (Assigned: [{container_str}]):")
        print(f"  ├─ System Total Delta    : {sys_delta_ms:8.2f} ms")
        print(f"  ├─ Assigned Usage Delta  : {assigned_containers_delta_ms:8.2f} ms")
        print(f"  └─ Non-Target Leakage    : {leakage_ms:8.2f} ms [{status}]")

    print("===================================\n")

    return {"all_passed": all_passed, "cpu_results": cpu_results}
