import json
from datetime import datetime
from pathlib import Path

from shared.adapters.prometheus_adapter import TimeRange
from shared.reporting.container_resource import get_container_resource_usage
from shared.reporting.latency import (
    get_api_latency,
    get_reservation_analysis_e2e_latency,
)
from shared.reporting.spring_resource import get_spring_resource_usage
from shared.reporting.throughput import get_api_throughput, get_worker_throughput
from shared.utils import LoadTestRun


def ingest(test_id, variant, n_iterations):

    run = LoadTestRun(test_id, variant, 1)
    stages = []
    with open(Path(__file__).resolve().parent / run.test_summary_path, "r") as f:
        test_summary = json.load(f)
        stages = test_summary["stages"]

    # Report Performance Metrics
    stages_summary: dict[str, dict[str, TimeRange]] = {}
    target_rps = {}

    for iteration in range(n_iterations):
        run = LoadTestRun(test_id, variant, iteration + 1)
        with open(Path(__file__).resolve().parent / run.test_summary_path, "r") as f:
            test_summary = json.load(f)
            stages = test_summary["stages"]
            for stage in stages:
                is_target = stage["is_target"]
                if not is_target:
                    continue
                failed_or_skipped = stage["failed"] or stage["skipped"]
                if failed_or_skipped:
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

    result = {}

    for stage_id, time_ranges in stages_summary.items():
        api_throughput = get_api_throughput(
            test_id=test_id,
            stage_id=stage_id,
            method="POST",
            iterations=time_ranges,
        )

        result[stage_id] = {
            "iterations": len(time_ranges),
            "target_rps": target_rps[stage_id],
            "latencies": {
                "api": get_api_latency(
                    test_id=test_id, stage_id=stage_id, iterations=time_ranges
                ),
                "e2e": get_reservation_analysis_e2e_latency(
                    test_id=test_id, stage_id=stage_id, iterations=time_ranges
                ),
            },
            "throughputs": {
                "api": api_throughput,
                "e2e": api_throughput
                if variant == "baseline"
                else get_worker_throughput(
                    test_id=test_id,
                    stage_id=stage_id,
                    method="POST",
                    iterations=time_ranges,
                ),
            },
            "resources": {
                "api-server": get_spring_resource_usage(
                    test_id=test_id, iterations=time_ranges
                ),
                "worker": get_container_resource_usage(
                    container_service_id="reservation-data-processor",
                    iterations=time_ranges,
                )
                if variant == "candidate"
                else {},
                "localstack": get_container_resource_usage(
                    container_service_id="localstack",
                    iterations=time_ranges,
                )
                if variant == "candidate"
                else {},
                # "db": get_container_resource_usage(
                #     container_service_id="db", iterations=time_ranges
                # ),
            },
        }

    output_path = (
        Path(__file__).resolve().parent / "output" / test_id / variant / "result.json"
    )
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(result, f, indent=2)
