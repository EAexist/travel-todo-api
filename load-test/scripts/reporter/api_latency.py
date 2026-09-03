import os
from datetime import timedelta
from typing import Dict

from .adapters.prometheus_adapter import TimeRange, fetch_metrics


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


def get_reservation_analysis_e2e_latency(
    test_id: str, stage_id: str, stages: list[TimeRange]
) -> float:
    """
    1. Fetches raw rate(bucket) data across all iterations for (test_id, stage_id).
    2. Merges histogram buckets across iterations by summing counts per 'le'.
    3. Calculates a single, scientifically accurate P95 on the merged histogram.
    """
    merged_buckets: Dict[str, float] = {}

    STAGE_START_BUFFER_SECONDS = int(os.getenv("STAGE_START_BUFFER_SECONDS", 0))
    for s in stages:
        query_start_time = s.start_time + timedelta(seconds=STAGE_START_BUFFER_SECONDS)
        query_end_time = s.end_time

        print(query_start_time, query_end_time)

        query = f'reservation_analysis_e2e_duration_seconds_bucket{{test_id="{test_id}", stage_id="{stage_id}"}}'

        start_metrics = fetch_metrics(
            query, params={"time": query_start_time.timestamp()}
        )
        end_metrics = fetch_metrics(query, params={"time": query_end_time.timestamp()})

        start_bucket = parse_buckets_from_response(start_metrics)
        end_bucket = parse_buckets_from_response(end_metrics)

        for le, end_val in end_bucket.items():
            start_val = start_bucket.get(le, 0.0)
            iter_delta = max(0.0, end_val - start_val)
            merged_buckets[le] = merged_buckets.get(le, 0.0) + iter_delta

    result = {
        "p95": calculate_histogram_quantile(0.95, merged_buckets),
        "p50": calculate_histogram_quantile(0.50, merged_buckets),
        "p99": calculate_histogram_quantile(0.99, merged_buckets),
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
