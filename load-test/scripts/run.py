import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

from dotenv import load_dotenv

from .reporter.reporter import report
from .utils import (
    LoadTestRun,
    get_running_containers,
    launch_host_memory_metrics_tracking,
    run_cmd,
    terminate_host_memory_metrics_tracking,
    verify_container_cpu_isolation_config,
    verify_containers_resource_config,
)

cpu_map = {0: ["db"], 1: ["db"], 2: ["target-spring-app"], 3: ["target-spring-app"]}

required_env_var_keys = [
    "PROMETHEUS_REMOTE_WRITE_URL",
    "PROMETHEUS_REMOTE_WRITE_USERNAME",
    "PROMETHEUS_REMOTE_WRITE_PASSWORD",
]


def run_single_load_test(run: LoadTestRun, target_tag: str):

    print(f"Starting infrastructure (Target Tag: {target_tag})...")

    env = {
        **os.environ.copy(),
        "TARGET_TAG": target_tag,
        "MANAGEMENT_METRICS_TAGS_TEST_ID": run.test_id,
        "MANAGEMENT_METRICS_TAGS_ITERATION": str(run.iteration),
    }

    # Run docker compose with prepared environment
    if (
        subprocess.run(
            f"docker compose \
                -f compose.loadtest.yml \
                -f compose.{run.test_id}.yml \
                up -d --wait",
            shell=True,
            env=env,
        ).returncode
        != 0
    ):
        print("Failed to start docker compose.")
        sys.exit(1)

    container_names = get_running_containers()
    if not container_names:
        print("No running containers found.")
        sys.exit(1)

    output_dir = Path(__file__).resolve().parent.parent / run.output_path
    output_dir.mkdir(parents=True, exist_ok=True)

    # Report VM internal resource config
    print("Reporting VM internal resource configuration...")
    resource_data = verify_containers_resource_config()
    with open(output_dir / "resource_config.json", "w") as f:
        json.dump(resource_data, f, indent=2)

    # Start Host Memory Metrics Tracking
    print("Launching Host Memory Metrics Trackings...")
    try:
        process = launch_host_memory_metrics_tracking(
            exe_path=os.environ["WINDOWS_EXPORTER_EXE_PATH"]
        )

        # Run k6
        host_script_dir = Path(__file__).resolve().parent.parent / "k6" / "scripts"
        app_script_dir = "/etc/grafana/k6/scripts"
        app_script_path = f"{app_script_dir}/{args.script}"

        host_data_dir = (
            Path(__file__).resolve().parent.parent.parent / "data" / "fixtures"
        )
        app_data_dir = "/etc/grafana/k6/data/fixtures"

        host_output_dir = output_dir
        app_output_dir = f"/etc/grafana/k6/{run.output_path}"
        summary_path = f"/etc/grafana/k6/{run.test_summary_path}"

        print(f"Executing k6 load test: {args.script}...")

        # https://grafana.com/docs/k6/latest/results-output/real-time/prometheus-remote-write/#send-test-metrics-to-a-remote-write-endpoint
        run_cmd(
            f'docker run\
            --network load-test-network \
            -i \
            -e BASE_URL=http://target-spring-app:8080 \
            -e FIXTURES_ROOT=../data/fixtures/ \
            -e SUMMARY_PATH={summary_path} \
            -e K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
            -e K6_PROMETHEUS_RW_TREND_STATS="" \
            -v {host_script_dir}:{app_script_dir} \
            -v {host_data_dir}:{app_data_dir} \
            -v {host_output_dir}:{app_output_dir} \
            grafana/k6 \
            run {app_script_path} \
            -o experimental-prometheus-rw'
        )
    except Exception as e:
        print(f"Host Memory Metrics Tracking failed: {e}")
        sys.exit(1)
    finally:
        terminate_host_memory_metrics_tracking(process)

    print(f"\nResults saved to: {summary_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run load tests and validate resource configuration."
    )
    parser.add_argument("--test-id", help="test id")
    parser.add_argument("--n-iterations", type=int, help="iterations")
    parser.add_argument(
        "--script",
        help="k6 script filename (must be in load-test/k6/)",
    )
    parser.add_argument("--target-tag", help="Docker image tag for target application")
    parser.add_argument(
        "--grafana-cloud", action="store_true", help="grafana-cloud usage"
    )
    args = parser.parse_args()
    test_id = args.test_id
    n_iterations = args.n_iterations
    target_tag = args.target_tag

    load_dotenv(".env")
    env_file = ".env.grafana-cloud" if args.grafana_cloud else ".env.local"
    load_dotenv(env_file)
    env = os.environ.copy()

    # Verify required credentials exist in environment
    missing_keys = [key for key in required_env_var_keys if key not in env]
    if missing_keys:
        exit(1)

    # Verify VM internal CPU isolation config
    print("Verifying VM internal CPU isolation configuration...")
    try:
        verify_container_cpu_isolation_config()
    except Exception as e:
        print(f"CPU isolation config validation failed: {e}")
        sys.exit(1)

    for iteration in range(1, n_iterations + 1):
        # run_id = datetime.now().strftime("%Y%m%d-%H%M%S")
        print(f"Running iteration {iteration}/{n_iterations}")
        run = LoadTestRun(test_id, iteration)
        run_single_load_test(run, target_tag)

    print("Load Test Complete.")

    print("Starting Analysis.")
    report(test_id, n_iterations)
