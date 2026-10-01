import os
from datetime import timedelta
from typing import Dict

from shared.adapters.prometheus_adapter import TimeRange, fetch_metrics


def parse_buckets_from_response(metrics_response: list) -> dict:
    """Extracts {le_string: count_float} from Prometheus instant query response."""
    buckets = {}
    if not metrics_response:
        return buckets

    for series in metrics_response:
        metric = series.get("metric", {})
        le = metric.get("le")
        value_tuple = series.get("value", [None, "0"])

        if le is not None:
            try:
                count = float(value_tuple[1])
            except (ValueError, TypeError):
                count = 0.0
            buckets[le] = count

    return buckets


def get_api_latency(test_id: str, stage_id: str, iterations: list[TimeRange]) -> dict:
    """
    Fetches raw bucket data for http_server_requests_seconds_bucket,
    calculates aggregate and per-iteration P95/P50/P99.
    """
    return fetch_and_calculate_histogram(
        test_id=test_id,
        stage_id=stage_id,
        iterations=iterations,
        metric_name="http_server_requests_seconds_bucket",
    )


def get_reservation_analysis_e2e_latency(
    test_id: str, stage_id: str, iterations: list[TimeRange]
) -> dict:
    """
    Fetches raw bucket data for reservation_analysis_e2e_duration_seconds_bucket,
    calculates aggregate and per-iteration P95/P50/P99.
    """
    return fetch_and_calculate_histogram(
        test_id=test_id,
        stage_id=stage_id,
        iterations=iterations,
        metric_name="reservation_analysis_e2e_duration_seconds_bucket",
    )


def get_reservation_repository_save_all_latency(
    test_id: str, iterations: list[TimeRange]
) -> dict:
    """
    Fetches raw bucket data for reservation_repository_save_all_duration_seconds_bucket,
    calculates aggregate and per-iteration P95/P50/P99.
    """
    return fetch_and_calculate_histogram(
        test_id=test_id,
        iterations=iterations,
        metric_name="reservation_repository_save_all_duration_seconds_bucket",
    )


def get_reservation_repository_find_all_by_id_latency(
    test_id: str, iterations: list[TimeRange]
) -> dict:
    """
    Fetches raw bucket data for reservation_repository_find_all_by_id_duration_seconds_bucket,
    calculates aggregate and per-iteration P95/P50/P99.
    """
    return fetch_and_calculate_histogram(
        test_id=test_id,
        iterations=iterations,
        metric_name="reservation_repository_find_all_by_id_duration_seconds_bucket",
    )


def fetch_and_calculate_histogram(
    test_id: str,
    iterations: list[TimeRange],
    metric_name: str,
    stage_id: str = None,
) -> dict:
    """Helper to fetch and calculate histogram metrics for different repository operations."""
    merged_buckets: Dict[str, float] = {}
    iteration_stats = []

    STAGE_START_BUFFER_SECONDS = int(os.getenv("STAGE_START_BUFFER_SECONDS", 0))
    for iteration in iterations:
        query_start_time = iteration.start_time + timedelta(
            seconds=STAGE_START_BUFFER_SECONDS
        )
        query_end_time = iteration.end_time

        query = (
            metric_name
            if metric_name == "reservation_analysis_e2e_duration_seconds_bucket"
            else (
                f'{metric_name}{{test_id="{test_id}", stage_id="{stage_id}"}}'
                if stage_id is not None
                else f'{metric_name}{{test_id="{test_id}"}}'
            )
        )

        start_metrics = fetch_metrics(
            query, params={"time": query_start_time.timestamp()}
        )
        end_metrics = fetch_metrics(query, params={"time": query_end_time.timestamp()})

        start_bucket = parse_buckets_from_response(start_metrics)
        end_bucket = parse_buckets_from_response(end_metrics)

        iter_buckets: Dict[str, float] = {}
        for le, end_val in end_bucket.items():
            start_val = start_bucket.get(le, 0.0)
            iter_delta = max(0.0, end_val - start_val)
            iter_buckets[le] = iter_delta
            merged_buckets[le] = merged_buckets.get(le, 0.0) + iter_delta

        iteration_stats.append(
            {
                "p95": calculate_histogram_quantile(0.95, iter_buckets),
                "p50": calculate_histogram_quantile(0.50, iter_buckets),
                "p99": calculate_histogram_quantile(0.99, iter_buckets),
            }
        )

    result = {
        "aggregate": {
            "p95": calculate_histogram_quantile(0.95, merged_buckets),
            "p50": calculate_histogram_quantile(0.50, merged_buckets),
            "p99": calculate_histogram_quantile(0.99, merged_buckets),
        },
        "iterations": iteration_stats,
    }

    return result


def calculate_histogram_quantile(phi: float, buckets: Dict[str, float]) -> float:
    """
    Prometheus histogram_quantile calculation on merged cumulative histogram buckets.
    """
    if not buckets or phi < 0.0 or phi > 1.0:
        return 0.0

    # 1. Parse and sort numerical buckets
    parsed = []
    for k, v in buckets.items():
        if k != "+Inf":
            try:
                parsed.append((float(k), float(v)))
            except ValueError:
                continue

    parsed.sort(key=lambda x: x[0])

    if "+Inf" in buckets:
        parsed.append((float("inf"), float(buckets["+Inf"])))

    if not parsed:
        return 0.0

    total_count = parsed[-1][1]
    if total_count <= 0.0:
        return 0.0

    target_rank = phi * total_count
    prev_bound = 0.0
    prev_count = 0.0

    for bound, count in parsed:
        # Enforce monotonic cumulative bounds to prevent negative interpolation
        count = max(count, prev_count)

        if bound == float("inf"):
            return prev_bound

        if count >= target_rank:
            bucket_count = count - prev_count
            if bucket_count <= 0:
                return bound

            rank_in_bucket = target_rank - prev_count

            # First bucket edge case: if prev_bound is 0.0 and bound > 0
            # Prometheus interpolates within [0, bound]
            lower = prev_bound if prev_bound >= 0.0 else bound
            return lower + (bound - lower) * (rank_in_bucket / bucket_count)

        prev_bound = bound
        prev_count = count

    return prev_bound
