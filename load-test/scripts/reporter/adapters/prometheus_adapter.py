import os
from dataclasses import dataclass
from datetime import datetime
from string import Template

from prometheus_api_client import PrometheusConnect

from ..utils import get_mean_and_std

# PROMETHEUS_URL = os.environ["PROMETHEUS_URL"]
PROMETHEUS_URL = "http://localhost:9090"


def fetch_metrics(query, params: dict = None):
    """Fetches metrics from Prometheus filtered by test_id."""
    prom = PrometheusConnect(url=PROMETHEUS_URL, disable_ssl=True)

    print(f"Querying Prometheus.\n\tQuery: {query}\n\tparams:{params}")

    metrics = prom.custom_query(
        query=query,
        params=params,
    )

    if metrics == []:
        raise RuntimeError(f"Metric not found.\n\tQuery: {query}\n\tparams: {params}")

    return metrics


def fetch_metrics_range(query, start_time, end_time, step=10):
    """Fetches metrics from Prometheus filtered by test_id."""
    prom = PrometheusConnect(url=PROMETHEUS_URL, disable_ssl=True)

    print(
        f"Querying Prometheus.\n\tQuery: {query}\n\tstart_time:{start_time}\n\tend_time:{end_time}\n\tstep={step}"
    )
    metrics = prom.custom_query_range(
        query=query,
        start_time=start_time,
        end_time=end_time,
        step=step,  # Configurable step
    )
    return metrics


@dataclass
class TimeRange:
    start_time: datetime
    end_time: datetime


def extract_value(metrics_response):
    if not metrics_response:
        return 0.0
    series = metrics_response[0]
    val = series.get("value", [None, 0])[1]
    return float(val) if val != "NaN" else 0.0


def get_buffered_stage_interval(start_time, end_time):

    STAGE_START_BUFFER_SECONDS = int(os.getenv("STAGE_START_BUFFER_SECONDS", 0))
    delta_sec = (end_time - start_time).total_seconds() - STAGE_START_BUFFER_SECONDS
    if delta_sec <= 0:
        raise RuntimeError(
            f"Invalid stage time delta, start_time={start_time} end_time={end_time}, stage_start_buffer={STAGE_START_BUFFER_SECONDS} "
        )
    return delta_sec


def get_mean_value(template: Template, stages: list[TimeRange]) -> dict[str, float]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    results = []

    for s in stages:
        delta_sec = int(get_buffered_stage_interval(s.start_time, s.end_time))
        query = template.substitute(
            duration_string=f"{delta_sec}s",
        )
        metrics = fetch_metrics(query, params={"time": s.end_time.timestamp()})
        results.append(extract_value(metrics))

    return get_mean_and_std(results)
