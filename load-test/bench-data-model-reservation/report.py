import argparse
import json
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv

from shared.adapters.prometheus_adapter import TimeRange
from shared.reporting.generate_report import generate_report
from shared.reporting.latency import (
    get_reservation_repository_find_all_by_id_latency,
    get_reservation_repository_save_all_latency,
)
from shared.reporting.spring_resource import get_spring_resource_usage


def report(test_id):

    output_dir = Path(__file__).resolve().parent / "output" / f"{test_id}"

    summary_file_path = output_dir / "test-summary.json"

    with open(summary_file_path, "r") as f:
        test_summary = json.load(f)
        iterations = test_summary["iterations"]

    # Report Performance Metrics
    time_ranges: list[TimeRange] = []

    for iteration in iterations:
        is_target = iteration.get("is_target", False)
        if not is_target:
            continue
        time_ranges.append(
            TimeRange(
                start_time=datetime.fromisoformat(iteration["start_time"]),
                end_time=datetime.fromisoformat(iteration["end_time"]),
            )
        )

    result = {
        "latencies": {
            "reservation_repository_save_all_latency": get_reservation_repository_save_all_latency(
                test_id=test_id, iterations=time_ranges
            ),
            "reservation_repository_find_all_by_id_latency": get_reservation_repository_find_all_by_id_latency(
                test_id=test_id, iterations=time_ranges
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

    print(f"result :\n{'\n'.join([f'\t{k}: {v}' for k, v in result.items()])}")

    result_path = output_dir / "result.json"
    with open(result_path, "w", encoding="utf-8") as f:
        json.dump(result, f, indent=2)
    print(f"Result saved to {result_path}")

    generate_report(output_dir, f"Benchmark Single Run Report - {test_id}")


def main():
    parser = argparse.ArgumentParser(description="Load Test reporting")
    parser.add_argument("--test-id", required=True, help="Test ID")

    args = parser.parse_args()

    load_dotenv()

    test_id = args.test_id
    output_dir = Path(__file__).resolve().parent / "output" / f"{test_id}"
    generate_report(output_dir, f"Benchmark Single Run Report - {test_id}")


if __name__ == "__main__":
    main()
