import math

from .adapters.prometheus_adapter import fetch_metrics_range


def get_cpu_steal(start_time, end_time):
    query = """
    100 *
    sum by (cpu) (
    increase(node_cpu_seconds_total{
        mode="steal",
        cpu=~"0|1|2|3"
    }[5m])
    )
    /
    sum by (cpu) (
    increase(node_cpu_seconds_total{
        cpu=~"0|1|2|3"
    }[5m])
    )
    """
    metrics = fetch_metrics_range(query, start_time, end_time)
    print(metrics)


def fetch_iteration_stage_stats(
    query: str, start_time: str, end_time: str
) -> dict[str, float]:
    """
    Fetches raw Prometheus metrics for a single [start_time, end_time] window
    (one iteration of one stage) and returns the min, max, and avg.
    """
    metrics = fetch_metrics_range(query, start_time, end_time)
    values = [float(val[1]) for series in metrics for val in series["value"]]

    if not values:
        return {}

    return {"min": min(values), "max": max(values), "avg": sum(values) / len(values)}


def aggregate_metric_by_stage(
    query: str, iterations: list[dict[str, str]], extreme_type: str = "min"
) -> dict[str, float]:
    """
    Aggregates metrics for a single stage across multiple iteration time windows.

    :param iterations: list of dicts with timestamps for this stage, e.g.,
                       [{'start_time': ..., 'end_time': ...}, ...]
    :param extreme_type: 'min' or 'max' depending on SLA direction.
    """
    mins, maxs, avgs = [], [], []

    for item in iterations:
        stats = fetch_iteration_stage_stats(query, item["start_time"], item["end_time"])
        if stats:
            mins.append(stats["min"])
            maxs.append(stats["max"])
            avgs.append(stats["avg"])

    n = len(avgs)
    if n == 0:
        return {}

    # 1. Global Extreme across all iterations for this stage
    if extreme_type.lower() == "max":
        extreme_val = max(maxs)
        extreme_key = "global_max"
    else:
        extreme_val = min(mins)
        extreme_key = "global_min"

    # 2. Grand Mean of iteration averages
    mean_avg = sum(avgs) / n

    # 3. Sample Standard Deviation of iteration averages
    if n > 1:
        variance = sum((x - mean_avg) ** 2 for x in avgs) / (n - 1)
        std_dev = math.sqrt(variance)
    else:
        std_dev = 0.0

    return {
        extreme_key: extreme_val,
        "mean_of_iteration_avgs": mean_avg,
        "std_dev_of_iteration_avgs": std_dev,
        "iterations_counted": n,
    }


def get_vm_guest_visible_physical_memory_bytes(iteration_windows):
    return aggregate_metric_by_stage(
        query="windows_hyperv_dynamic_memory_vm_guest_visible_physical_memory_bytes",
        iterations=iteration_windows,
        extreme_type="min",
    )


def get_vm_current_pressure(iteration_windows):
    return aggregate_metric_by_stage(
        query="windows_hyperv_dynamic_memory_vm_pressure_current_ratio",
        iterations=iteration_windows,
        extreme_type="max",
    )


def get_host_available_bytes(iteration_windows):
    return aggregate_metric_by_stage(
        query="windows_memory_available_bytes",
        iterations=iteration_windows,
        extreme_type="min",
    )

    # for run_metadata in manifest["runs"]:
    #     run = LoadTestRun(test_id, run_metadata.iteration)
    #     with open(run.test_summary_path, "r") as f:
    #         test_summary = json.load(f)
    #         iteration_windows.append(
    #             {
    #                 "iteration": run.iteration,
    #                 "start_time": test_summary.start_time,
    #                 "end_time": test_summary.end_time,
    #             }
    #         )

    # # Report Resource Metrics
    # resource_metrics = {
    #     "current_pressure": get_vm_current_pressure(iteration_windows),
    #     "guest_visible_physical_memory_bytes": get_vm_guest_visible_physical_memory_bytes(
    #         iteration_windows
    #     ),
    #     "host_available_bytes": get_host_available_bytes(iteration_windows),
    # }

    # print(f"resource_metrics:\n{resource_metrics}")
    # with open(get_output_dir(test_id), "w", encoding="utf-8") as f:
    #     json.dump(resource_metrics, f, indent=2)
