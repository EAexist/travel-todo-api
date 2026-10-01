import argparse
import json
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv

from shared.adapters.prometheus_adapter import TimeRange
from shared.reporting.latency import get_reservation_analysis_e2e_latency
from shared.reporting.spring_resource import get_spring_resource_usage
from shared.reporting.throughput import get_api_throughput
from shared.utils import LoadTestRun


def report(test_id, n_iterations):

    # Get Stages
    run = LoadTestRun(test_id, 1)
    stages = []
    with open(Path(__file__).resolve().parent / run.test_summary_path, "r") as f:
        test_summary = json.load(f)
        stages = test_summary["stages"]

    # Report Performance Metrics
    stages_summary: dict[str, dict[str, TimeRange]] = {}
    target_rps = {}

    for iteration in range(n_iterations):
        run = LoadTestRun(test_id, iteration + 1)
        with open(Path(__file__).resolve().parent / run.test_summary_path, "r") as f:
            test_summary = json.load(f)
            stages = test_summary["stages"]
            for stage in stages:
                is_target = stage["is_target"]
                if not is_target:
                    continue
                stage_id = stage["stage_id"]
                target_rps[stage_id] = stage["target_rps"]
                if stage_id not in stages_summary:
                    stages_summary[stage_id] = []
                stages_summary[stage_id].append(
                    TimeRange(
                        start_time=datetime.fromisoformat(stage["start_time"]),
                        end_time=datetime.fromisoformat(stage["end_time"]),
                    )
                )

    stage_reports = {}

    print(stages_summary)
    for stage_id, time_ranges in stages_summary.items():
        stage_reports[stage_id] = {
            "iterations": len(time_ranges),
            "target_rps": target_rps[stage_id],
            "latencies": {
                "reservation_analysis_e2e_latency": get_reservation_analysis_e2e_latency(
                    test_id=test_id, stage_id=stage_id, iterations=time_ranges
                ),
            },
            "throughputs": {
                "e2e": get_api_throughput(
                    test_id=test_id,
                    stage_id=stage_id,
                    method="POST",
                    iterations=time_ranges,
                ),
            },
            "resources": {
                "spring": get_spring_resource_usage(
                    test_id=test_id, iterations=time_ranges
                ),
                # "db": get_container_resource_usage(
                #     container_service_id="db", iterations=time_ranges
                # ),
            },
        }

    print(
        f"stage_reports :\n{'\n'.join([f'\t{k}: {v}' for k, v in stage_reports.items()])}"
    )
    perf_result_path = (
        Path(__file__).resolve().parent / "output" / test_id / "result.json"
    )
    with open(perf_result_path, "w", encoding="utf-8") as f:
        json.dump(stage_reports, f, indent=2)


def main():
    parser = argparse.ArgumentParser(description="Load Test reporting")
    parser.add_argument("--test-id", required=True, help="Test ID filter")
    parser.add_argument(
        "--n-iterations", type=int, required=True, help="Number of iterations"
    )

    args = parser.parse_args()

    load_dotenv()
    test_id = args.test_id
    output_dir = Path(__file__).resolve().parent / "output" / f"{test_id}"
    # generate_report(output_dir, f"Benchmark Single Run Report - {test_id}")


if __name__ == "__main__":
    main()
