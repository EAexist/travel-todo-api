import os
from datetime import timedelta
from typing import Dict

from .adapters.prometheus_adapter import TimeRange, extract_value, fetch_metrics
from .utils import get_mean_and_std


def get_api_throughput(
    test_id: str, stage_id: str, method: str, stages: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average throughput (RPS) per iteration for a given stage, then returns
    the grand mean and sample standard deviation across all iterations.
    """

    STAGE_START_BUFFER_SECONDS = int(os.getenv("STAGE_START_BUFFER_SECONDS", 0))
    iteration_avg_rps = []

    for s in stages:
        query_start_time = s.start_time + timedelta(seconds=STAGE_START_BUFFER_SECONDS)
        query_end_time = s.end_time

        query = f'sum(http_server_requests_seconds_count{{method="{method}", test_id="{test_id}", stage_id="{stage_id}"}})'

        start_metrics = fetch_metrics(
            query, params={"time": query_start_time.timestamp()}
        )
        end_metrics = fetch_metrics(query, params={"time": query_end_time.timestamp()})

        start_count = extract_value(start_metrics)
        end_count = extract_value(end_metrics)

        total_requests = max(0.0, end_count - start_count)

        delta_sec = (query_end_time - query_start_time).total_seconds()
        if delta_sec <= 0:
            raise RuntimeError(
                f"Invalid stage time delta, query_start_time={query_start_time} query_end_time={query_end_time} "
            )

        iter_mean_rps = total_requests / delta_sec

        iteration_avg_rps.append(iter_mean_rps)

    return get_mean_and_std(iteration_avg_rps)
