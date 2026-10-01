import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

from dotenv import load_dotenv

from shared.utils import (
    launch_host_memory_metrics_tracking,
    run_cmd,
    terminate_host_memory_metrics_tracking,
    verify_container_cpu_isolation_config,
    verify_containers_resource_config,
)

from .report import report

required_env_var_keys = [
    "PROMETHEUS_REMOTE_WRITE_URL",
    "PROMETHEUS_REMOTE_WRITE_USERNAME",
    "PROMETHEUS_REMOTE_WRITE_PASSWORD",
]

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run load tests and validate resource configuration."
    )
    parser.add_argument(
        "--test-id", help="test id", default="bench-data-model-reservation"
    )
    parser.add_argument("--target-tag", help="Docker image tag for target application")
    parser.add_argument(
        "--grafana-cloud", action="store_true", help="grafana-cloud usage"
    )
    args = parser.parse_args()
    test_id = args.test_id
    target_tag = args.target_tag
    script_filename = "000-insert-and-read.js"

    output_path = f"output/{test_id}"
    summary_path = f"{output_path}/test-summary.json"

    load_dotenv(".env")
    env_file = ".env.grafana-cloud" if args.grafana_cloud else ".env.local"
    load_dotenv(env_file)
    env = os.environ.copy()

    missing_keys = [key for key in required_env_var_keys if key not in env]
    if missing_keys:
        print(f"Missing environment variables: {missing_keys}")
        sys.exit(1)

    # Verify VM internal CPU isolation config
    print("Verifying VM internal CPU isolation configuration...")
    try:
        verify_container_cpu_isolation_config()
    except Exception as e:
        print(f"CPU isolation config validation failed: {e}")
        sys.exit(1)

    print(f"Starting infrastructure (Target Tag: {target_tag})...")

    env = {
        **os.environ.copy(),
        "TARGET_TAG": target_tag,
        "MANAGEMENT_METRICS_TAGS_TEST_ID": test_id,
        "MANAGEMENT_METRICS_TAGS_RUN_ID": "",
    }

    # Run docker compose with prepared environment
    if (
        subprocess.run(
            f"docker compose \
                -p benchmark-{target_tag}  \
                -f compose.loadtest.yml \
                -f ./bench-data-model-reservation/compose.extend.yml \
                up -d --wait",
            shell=True,
            env=env,
        ).returncode
        != 0
    ):
        print("Failed to start docker compose.")
        sys.exit(1)

    output_dir = Path(__file__).resolve().parent / output_path
    output_dir.mkdir(parents=True, exist_ok=True)

    # Report VM internal resource config
    print("Reporting VM internal resource configuration...")
    resource_data = verify_containers_resource_config(f"benchmark-{target_tag}")
    with open(output_dir / "resource_config.json", "w") as f:
        json.dump(resource_data, f, indent=2)

    # Start Host Memory Metrics Tracking
    print("Launching Host Memory Metrics Trackings...")
    try:
        process = launch_host_memory_metrics_tracking(
            exe_path=os.environ["WINDOWS_EXPORTER_EXE_PATH"]
        )

        # Run k6
        host_script_dir = Path(__file__).resolve().parent / "k6" / "scripts"
        app_script_dir = "/etc/grafana/k6/scripts"
        app_script_path = f"{app_script_dir}/{script_filename}"

        host_output_dir = output_dir
        app_output_dir = f"/etc/grafana/k6/{output_path}"
        app_summary_path = f"/etc/grafana/k6/{summary_path}"

        print(f"Executing k6 load test: {script_filename}...")

        # https://grafana.com/docs/k6/latest/results-output/real-time/prometheus-remote-write/#send-test-metrics-to-a-remote-write-endpoint
        run_cmd(
            f'docker run\
            --network load-test-network \
            -i \
            -e BASE_URL=http://target-spring-app:8080 \
            -e SUMMARY_PATH={app_summary_path} \
            -e K6_PROMETHEUS_RW_SERVER_URL=http://prometheus:9090/api/v1/write \
            -e K6_PROMETHEUS_RW_TREND_STATS="" \
            -v {host_script_dir}:{app_script_dir} \
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

    print("Load Test Complete.")

    print("Starting Analysis.")

    report(test_id)
