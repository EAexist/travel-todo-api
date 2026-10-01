from string import Template
from typing import Dict

from shared.adapters.prometheus_adapter import TimeRange
from shared.reporting.utils import get_mean_value


def get_container_resource_usage(
    container_service_id: str, iterations: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        "cpu": get_container_cpu_usage(
            container_service_id=container_service_id, iterations=iterations
        ),
        "memory_avg": get_container_memory_working_set_avg(
            container_service_id=container_service_id, iterations=iterations
        ),
        "memory_peak": get_container_memory_working_set_peak(
            container_service_id=container_service_id, iterations=iterations
        ),
    }


def get_container_cpu_usage(
    container_service_id: str, iterations: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'rate(container_cpu_usage_seconds_total{{ container_label_com_docker_compose_service="{container_service_id}"}}[$duration_string])'
                ),
                iterations=iterations,
            )
        ),
        "unit": "cpu",
    }


def get_container_memory_working_set_avg(
    container_service_id: str, iterations: list[TimeRange]
) -> Dict[str, any]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'avg_over_time(container_memory_working_set_bytes{{ container_label_com_docker_compose_service="{container_service_id}"}}[$duration_string])'
                ),
                iterations=iterations,
            )
        ),
        "unit": "bytes",
    }


def get_container_memory_working_set_peak(
    container_service_id: str, iterations: list[TimeRange]
) -> Dict[str, any]:
    """
    Calculates average container cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'max_over_time(container_memory_working_set_bytes{{ container_label_com_docker_compose_service="{container_service_id}"}}[$duration_string])'
                ),
                iterations=iterations,
            )
        ),
        "unit": "bytes",
    }
