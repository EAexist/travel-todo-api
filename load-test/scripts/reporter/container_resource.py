from string import Template
from typing import Dict

from .adapters.prometheus_adapter import TimeRange, get_mean_value


def get_container_cpu_usage(
    container_name: str, stages: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'rate(container_cpu_usage_seconds_total{{name="{container_name}"}}[$duration_string])'
                ),
                stages=stages,
            )
        ),
        "unit": "cpu",
    }


def get_container_memory_working_set_avg(
    container_name: str, stages: list[TimeRange]
) -> Dict[str, any]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'avg_over_time(container_memory_working_set_bytes{{name="{container_name}"}}[$duration_string])'
                ),
                stages=stages,
            )
        ),
        "unit": "bytes",
    }


def get_container_memory_working_set_peak(
    container_name: str, stages: list[TimeRange]
) -> Dict[str, any]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'max_over_time(container_memory_working_set_bytes{{name="{container_name}"}}[$duration_string])'
                ),
                stages=stages,
            )
        ),
        "unit": "bytes",
    }
