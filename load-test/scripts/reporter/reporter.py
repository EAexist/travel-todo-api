import argparse
import json
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv

from ..utils import LoadTestRun, get_perf_result_path
from .adapters.prometheus_adapter import TimeRange
from .api_latency import get_reservation_analysis_e2e_latency
from .container_resource import (
    get_container_cpu_usage,
    get_container_memory_working_set_avg,
    get_container_memory_working_set_peak,
)
from .spring_resource import (
    get_spring_memory_used_avg,
    get_spring_memory_used_peak,
    get_spring_process_cpu_usage,
)
from .throughput import get_api_throughput


def report(test_id, n_iterations):

    # Get Stages
    run = LoadTestRun(test_id, 1)
    stages = []
    with open(
        Path(__file__).resolve().parent.parent.parent / run.test_summary_path, "r"
    ) as f:
        test_summary = json.load(f)
        stages = test_summary["stages"]

    # Report Performance Metrics
    stages_summary: dict[str, dict[str, TimeRange]] = {}
    vus = {}

    for iteration in range(n_iterations):
        run = LoadTestRun(test_id, iteration + 1)
        with open(run.test_summary_path, "r") as f:
            test_summary = json.load(f)
            stages = test_summary["stages"]
            for stage in stages:
                is_target = stage["is_target"]
                if not is_target:
                    continue
                stage_id = stage["stage_id"]
                vus[stage_id] = stage["target_vus"]
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
    for stage_id, stages in stages_summary.items():
        stage_reports[stage_id] = {
            "iterations": len(stages),
            "vus": vus[stage_id],
            "reservation_analysis_e2e_latency": get_reservation_analysis_e2e_latency(
                test_id=test_id, stage_id=stage_id, stages=stages
            ),
            "throughput": get_api_throughput(
                test_id=test_id, stage_id=stage_id, method="POST", stages=stages
            ),
            "spring_cpu": get_spring_process_cpu_usage(test_id=test_id, stages=stages),
            "spring_memory_avg": get_spring_memory_used_avg(
                test_id=test_id, stages=stages
            ),
            "spring_memory_peak": get_spring_memory_used_peak(
                test_id=test_id, stages=stages
            ),
            "db_cpu": get_container_cpu_usage(container_name="db", stages=stages),
            "db_memory_avg": get_container_memory_working_set_avg(
                container_name="db", stages=stages
            ),
            "db_memory_peak": get_container_memory_working_set_peak(
                container_name="db", stages=stages
            ),
        }

    print(
        f"stage_reports :\n{'\n'.join([f'\t{k}: {v}' for k, v in stage_reports.items()])}"
    )
    perf_result_path = get_perf_result_path(test_id)
    with open(perf_result_path, "w", encoding="utf-8") as f:
        json.dump(stage_reports, f, indent=2)


def main():
    parser = argparse.ArgumentParser(description="Load Test Reporter")
    parser.add_argument("--test-id", required=True, help="Test ID filter")
    parser.add_argument("--output", default="report.csv", help="Output CSV file")

    args = parser.parse_args()

    load_dotenv()
    report(
        args.test_id,
        args.output,
    )


if __name__ == "__main__":
    main()
