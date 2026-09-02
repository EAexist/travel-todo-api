from string import Template
from typing import Dict

from .adapters.prometheus_adapter import TimeRange, get_mean_value


def get_spring_process_cpu_usage(
    test_id: str, stages: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average process cpu usage per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'avg_over_time(process_cpu_usage{{test_id="{test_id}"}}[$duration_string])'
                ),
                stages=stages,
            )
        ),
        "unit": "cpu",
    }


def get_spring_memory_used_avg(test_id: str, stages: list[TimeRange]):
    """
    Calculates average memory usage (jvm_memory_used_bytes, jvm_gc_memory_allocated_bytes_total) per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'avg_over_time((sum(jvm_memory_used_bytes{{area="heap", test_id="{test_id}"}}))[$duration_string:10s])'
                ),
                stages=stages,
            )
        ),
        "unit": "cpu",
    }


def get_spring_memory_used_peak(test_id: str, stages: list[TimeRange]):
    """
    Calculates average memory usage (jvm_memory_used_bytes, jvm_gc_memory_allocated_bytes_total) per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'max_over_time((sum(jvm_memory_used_bytes{{area="heap", test_id="{test_id}"}}))[$duration_string:10s])'
                ),
                stages=stages,
            )
        ),
        "unit": "cpu",
    }


def get_spring_gc_memory_allocated_avg(
    test_id: str, stages: list[TimeRange]
) -> Dict[str, float]:
    """
    Calculates average memory usage (jvm_memory_used_bytes, jvm_gc_memory_allocated_bytes_total) per iteration for a given stage,
    then returns the grand mean and sample standard deviation across all iterations.
    """
    return {
        **(
            get_mean_value(
                template=Template(
                    f'avg_over_time(rate(jvm_gc_memory_allocated_bytes_total{{test_id="{test_id}"}}[40s])[$duration_string:])'
                ),
                stages=stages,
            )
        ),
        "unit": "cpu",
    }
