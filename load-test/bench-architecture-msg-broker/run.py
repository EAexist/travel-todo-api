import argparse
import json
import os
import subprocess
import sys
import time
from pathlib import Path

from dotenv import load_dotenv

from shared.utils import (
    LoadTestRun,
    launch_host_memory_metrics_tracking,
    run_cmd,
    terminate_host_memory_metrics_tracking,
    verify_container_cpu_isolation_config,
    verify_containers_resource_config,
)

from .config import config
from .ingest import ingest
from .report import report_benchmark_result

cpu_map = {0: ["db"], 1: ["db"], 2: ["target-spring-app"], 3: ["target-spring-app"]}

required_env_var_keys = [
    "PROMETHEUS_REMOTE_WRITE_URL",
    "PROMETHEUS_REMOTE_WRITE_USERNAME",
    "PROMETHEUS_REMOTE_WRITE_PASSWORD",
]


def run_single_load_test(run: LoadTestRun, target_image_tag: str):

    print(f"Starting infrastructure (Target Tag: {target_image_tag})...")

    env = {
        **os.environ.copy(),
        "TARGET_TAG": target_image_tag,
        "MANAGEMENT_METRICS_TAGS_TEST_ID": run.test_id,
        "MANAGEMENT_METRICS_TAGS_ITERATION": str(run.iteration),
        "INIT_SCRIPT_DIR": (
            Path(__file__).resolve().parent
            / "localstack"
            / "init-scripts"
            / "init-sqs.sh"
        ).as_posix(),
    }

    project_id = f"{run.test_id}_{run.variant}_{run.iteration}"

    if (
        subprocess.run(
            f"docker compose \
                -p {project_id} \
                -f compose.loadtest.yml \
                -f compose.llm-mock-server.yml \
                -f ./bench-architecture-msg-broker/compose.{run.variant}.yml \
                up -d --wait",
            shell=True,
            env=env,
        ).returncode
        != 0
    ):
        print("Failed to start docker compose.")
        sys.exit(1)

    output_dir = Path(__file__).resolve().parent / run.output_path
    output_dir.mkdir(parents=True, exist_ok=True)

    print("Reporting VM internal resource configuration...")
    resource_data = verify_containers_resource_config(project_id)
    with open(output_dir / "resource_config.json", "w") as f:
        json.dump(resource_data, f, indent=2)

    try:
        print("Launching Host Memory Metrics Trackings...")
        process = launch_host_memory_metrics_tracking(
            exe_path=os.environ["WINDOWS_EXPORTER_EXE_PATH"]
        )

        container_mount_root = Path("/etc/grafana/k6")

        host_script_dir = config.k6_script_path.parent
        container_script_path = (
            container_mount_root
            / config.k6_script_path.relative_to(Path(__file__).resolve().parent / "k6")
        )
        container_script_dir = container_script_path.parent

        host_data_dir = (
            Path(__file__).resolve().parent.parent.parent / "data" / "fixtures"
        )
        container_data_dir = container_mount_root / "data" / "fixtures"

        host_output_dir = output_dir
        container_output_dir = container_mount_root / run.output_path

        container_summary_path = container_mount_root / run.test_summary_path

        print(f"Executing k6 with scenario {config.k6_scenario}...")
        # https://grafana.com/docs/k6/latest/results-output/real-time/prometheus-remote-write/#send-test-metrics-to-a-remote-write-endpoint

        run_cmd(
            f'docker run\
            --name {project_id} \
            --network load-test-network \
            -i \
            -e BASE_URL=http://target-spring-app:8080 \
            -e FIXTURES_ROOT=../data/fixtures/ \
            -e SUMMARY_PATH={container_summary_path.as_posix()} \
            -e K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
            -e K6_PROMETHEUS_RW_TREND_STATS="" \
            -v {host_script_dir}:{container_script_dir.as_posix()} \
            -v {host_data_dir}:{container_data_dir.as_posix()} \
            -v {host_output_dir}:{container_output_dir.as_posix()} \
            --cpuset-cpus=4,5,6,7 \
            --memory=1536m \
            grafana/k6 run \
            {container_script_path.as_posix()} \
            -o experimental-prometheus-rw'
        )

    except subprocess.CalledProcessError as e:
        # Exit code 99 = Threshold breached (expected behavior)
        if e.returncode == 99:
            print("[INFO] k6 completed with threshold breaches. Continuing...")
            if e.stderr:
                print(e.stderr)
            time.sleep(15)
        else:
            print(f"Load test execution failed (Exit code {e.returncode}):\n{e.stderr}")
            sys.exit(1)

    finally:
        time.sleep(15)
        terminate_host_memory_metrics_tracking(process)
        subprocess.run(
            f"docker compose \
                -p {project_id} \
                -f compose.loadtest.yml \
                -f compose.llm-mock-server.yml \
                -f ./bench-architecture-msg-broker/compose.{run.variant}.yml \
                stop --timeout 5",
            shell=True,
            check=True,
            env=env,
        )

        try:
            subprocess.run(
                'wsl -d docker-desktop -u root sh -c "sync && sysctl -w vm.drop_caches=3"',
                shell=True,
                check=False,
            )
        except Exception as e:
            print(f"Warning: Failed to flush Docker Desktop VM cache: {e}")

            time.sleep(15)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run load tests and validate resource configuration."
    )
    load_dotenv()
    env = os.environ.copy()
    variants = ["baseline", "candidate"] if config.is_benchmark else [config.variant]

    # Verify required credentials exist in environment
    missing_keys = [key for key in required_env_var_keys if key not in env]
    if missing_keys:
        exit(1)

    print("Verifying VM internal CPU isolation configuration...")
    try:
        verify_container_cpu_isolation_config()
    except Exception as e:
        print(f"CPU isolation config validation failed: {e}")
        sys.exit(1)

    if (
        subprocess.run(
            "docker compose \
                -f compose.prometheus.yml \
                up -d --wait",
            shell=True,
            env=env,
        ).returncode
        != 0
    ):
        print("Failed to start docker compose.")
        sys.exit(1)

    for iteration in range(1, config.n_iterations + 1):
        for variant in variants:
            print(f"Running iteration {iteration}/{config.n_iterations}")
            run = LoadTestRun(config.test_id, variant, iteration)
            iamge_tag = f"{config.iamge_tag_base}-{variant}"
            run_single_load_test(run, f"{config.iamge_tag_base}-{variant}")

    for variant in variants:
        ingest(config.test_id, variant, config.n_iterations)

    if not config.is_benchmark:
        exit(1)

    print("Reporting.")
    report_benchmark_result(
        base_dir=config.base_output_dir,
        candidate_dir=config.candidate_output_dir,
        output_path=config.output_dir / "report.md",
    )
