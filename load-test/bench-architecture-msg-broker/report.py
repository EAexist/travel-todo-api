import argparse
import json
from pathlib import Path


def load_json(path):
    with open(path, "r") as f:
        return json.load(f)


def get_data(dir_path):
    # Assumes staged format, result.json is at the root
    res = load_json(dir_path / "result.json")
    # Config/Summary seem to be inside a '1' sub-folder in the examples?
    # Actually example/bench-architecture-msg-broker-baseline/1/ contains resource_config.json
    # The current report.py seems to be designed for either flat or staged.
    # Let's try to find them

    # Based on example structure:
    # example/bench-architecture-msg-broker-baseline/1/test-summary.json
    # example/bench-architecture-msg-broker-baseline/result.json

    config = load_json(dir_path / "1" / "resource_config.json")
    summary = load_json(dir_path / "1" / "test-summary.json")
    return res, config, summary


def report_benchmark_result(base_dir, candidate_dir, output_path):
    base_res, base_cfg, base_sum = get_data(Path(base_dir))
    candidate_res, candidate_cfg, target_sum = get_data(Path(candidate_dir))

    # Helper for formatting
    def fmt_val(val, fmt="{:.2f}"):
        return fmt.format(val) if val is not None else "-"

    def fmt_lat(val):
        if val is None:
            return "-"
        if val >= 10.0:
            return ">= 10"
        return f"{val:.4f}"

    lines = [
        "# Reservation Analysis Message Broker Architecture Benchmark\n",
        "## Context",
        "| Architecture | Description | Commit SHA |",
        "| --- | --- | --- |",
        "| Base (Baseline) | Reservation analysis handled by blocking API server | --- |",
        "| Candidate | Reservation analysis handled by message broker + seperate worker | --- |\n",
        "## Performance\n",
        "*Note: Values displayed as '-' for latency differences indicate a failure to meet the P95 latency threshold (< 10 seconds).\n",
    ]

    # Iterate over union of stages
    all_keys = set(base_res.keys()) | set(candidate_res.keys())
    stages = sorted(all_keys, key=lambda s: int(s.split("_")[1]))

    # Get metrics from the first available stage
    first_stage = stages[0]
    first_res = base_res.get(first_stage) or candidate_res.get(first_stage)
    throughput_metrics = list(first_res["throughputs"].keys())
    latency_metrics = list(first_res["latencies"].keys())
    # resource_services = list(first_res["resources"].keys())

    # 1. Throughput
    for metric in throughput_metrics:
        lines.append(f"### Throughput: {metric.title()} (rps)")
        lines.append("| Target RPS | Base | Candidate | Diff (Abs) | Diff (%) |")
        lines.append("| --- | --- | --- | --- | --- |")
        for stage_id in stages:
            b_data = base_res.get(stage_id)
            c_data = candidate_res.get(stage_id)
            target_rps = (b_data or c_data)["target_rps"]

            b_str = "-"
            b_mean = None
            if b_data and metric in b_data.get("throughputs", {}):
                b_thr = b_data["throughputs"][metric]
                b_mean = b_thr['mean']
                b_str = f"{b_mean:.2f} ± {b_thr['std_dev']:.2f}"

            c_str = "-"
            c_mean = None
            if c_data and metric in c_data.get("throughputs", {}):
                c_thr = c_data["throughputs"][metric]
                c_mean = c_thr['mean']
                c_str = f"{c_mean:.2f} ± {c_thr['std_dev']:.2f}"

            diff_str = "-"
            diff_pct_str = "-"
            if b_mean is not None and c_mean is not None:
                diff = c_mean - b_mean
                diff_pct = (diff / b_mean) * 100 if b_mean != 0 else 0
                diff_str = f"{diff:.2f}"
                diff_pct_str = f"{diff_pct:.1f}%"

            lines.append(
                f"| {target_rps} | {b_str} | {c_str} | {diff_str} | {diff_pct_str} |"
            )
        lines.append("")

    # 2. Latency
    for metric in latency_metrics:
        lines.append(f"### Latency: {metric.title()} (s)")
        lines.append(
            "| Target RPS | Base P95 | Candidate P95 | Diff (Abs) | Diff (%) |"
        )
        lines.append("| --- | --- | --- | --- | --- |")

        for stage_id in stages:
            b_data = base_res.get(stage_id)
            c_data = candidate_res.get(stage_id)
            target_rps = (b_data or c_data)["target_rps"]

            b_p95 = (
                b_data["latencies"][metric]["aggregate"]["p95"]
                if b_data and metric in b_data.get("latencies", {})
                else None
            )
            c_p95 = (
                c_data["latencies"][metric]["aggregate"]["p95"]
                if c_data and metric in c_data.get("latencies", {})
                else None
            )

            b_str = fmt_lat(b_p95)
            c_str = fmt_lat(c_p95)

            diff_str = "-"
            diff_pct_str = "-"
            if b_p95 is not None and c_p95 is not None:
                if b_p95 >= 10 or c_p95 >= 10:
                    diff_str = "-"
                    diff_pct_str = "-"
                else:
                    diff = c_p95 - b_p95
                    diff_pct = (diff / b_p95) * 100 if b_p95 != 0 else 0
                    diff_str = f"{diff:.4f}"
                    diff_pct_str = f"{diff_pct:.1f}%"

            lines.append(
                f"| {target_rps} | {b_str} | {c_str} | {diff_str} | {diff_pct_str} |"
            )
        lines.append("")

        # Per-Iteration
        lines.append("#### Per-Iteration Results")
        for stage_id in stages:
            b_data = base_res.get(stage_id)
            c_data = candidate_res.get(stage_id)
            target_rps = (b_data or c_data)["target_rps"]
            lines.append(f"##### Stage: {target_rps} Target RPS")
            lines.append(
                "| Iteration | Base P95 | Candidate P95 | Base P50 | Candidate P50 | Base P99 | Candidate P99 |"
            )
            lines.append("| --- | --- | --- | --- | --- | --- | --- |")

            b_iters = (
                b_data["latencies"][metric]["iterations"]
                if b_data and metric in b_data.get("latencies", {})
                else []
            )
            c_iters = (
                c_data["latencies"][metric]["iterations"]
                if c_data and metric in c_data.get("latencies", {})
                else []
            )
            max_iters = max(len(b_iters), len(c_iters))

            for i in range(max_iters):
                b_it = b_iters[i] if i < len(b_iters) else None
                c_it = c_iters[i] if i < len(c_iters) else None

                b_p95 = fmt_lat(b_it["p95"] if b_it else None)
                c_p95 = fmt_lat(c_it["p95"] if c_it else None)
                b_p50 = fmt_lat(b_it["p50"] if b_it else None)
                c_p50 = fmt_lat(c_it["p50"] if c_it else None)
                b_p99 = fmt_lat(b_it["p99"] if b_it else None)
                c_p99 = fmt_lat(c_it["p99"] if c_it else None)

                lines.append(
                    f"| {i + 1} | {b_p95} | {c_p95} | {b_p50} | {c_p50} | {b_p99} | {c_p99} |"
                )
            lines.append("")

    # 3. Resources
    lines.append("## Resource Usage\n")

    # Group 1: Comparison table
    api_services = ["api-server"]

    # Group 2: Single table per service
    other_services = ["worker", "localstack"]

    metrics = ["cpu", "memory_avg", "memory_peak"]

    # Iterate through api_services
    for service in api_services:
        lines.append(f"### {service}".title())

        # Comparison style
        for metric in metrics:
            unit = "(MB)" if "memory" in metric else "(cpu)"
            lines.append(f"#### {metric.replace('_', ' ').title()} {unit}")
            lines.append("| Target RPS | Base | Candidate | Diff (Abs) | Diff (%) |")
            lines.append("| --- | --- | --- | --- | --- |")

            for stage_id in stages:
                b_data = base_res.get(stage_id)
                c_data = candidate_res.get(stage_id)
                target_rps = (b_data or c_data)["target_rps"]

                def get_val(data):
                    if (
                        data
                        and service in data.get("resources", {})
                        and metric in data["resources"][service]
                    ):
                        val = data["resources"][service][metric]["mean"]
                        if "memory" in metric:
                            val = val / 1024 / 1024
                        return val
                    return None

                b_val = get_val(b_data)
                c_val = get_val(c_data)

                b_str = fmt_val(b_val)
                c_str = fmt_val(c_val)

                diff_str = "-"
                diff_pct_str = "-"
                if b_val is not None and c_val is not None:
                    diff = c_val - b_val
                    diff_pct = (diff / b_val) * 100 if b_val != 0 else 0
                    diff_str = fmt_val(diff)
                    diff_pct_str = f"{diff_pct:.1f}%"

                lines.append(
                    f"| {target_rps} | {b_str} | {c_str} | {diff_str} | {diff_pct_str} |"
                )
            lines.append("")

        # Iterate through other_services
        for service in other_services:
            lines.append(f"### {service}".title())

            # Single table style
            # Construct table header
            header = (
                "| Target RPS | "
                + " | ".join(
                    [
                        f"{m.replace('_', ' ').title()} {'(MB)' if 'memory' in m else '(cpu)'}"
                        for m in metrics
                    ]
                )
                + " |"
            )
            lines.append(header)
            lines.append("| --- | " + " | ".join([" --- "] * len(metrics)) + " |")

            for stage_id in stages:
                c_data = candidate_res.get(stage_id)
                if not c_data or service not in c_data.get("resources", {}):
                    continue

                target_rps = c_data["target_rps"]
                row = [str(target_rps)]

                for metric in metrics:
                    val = c_data["resources"][service][metric]["mean"]
                    if "memory" in metric:
                        val = val / 1024 / 1024
                    row.append(f"{val:.2f}")

                lines.append("| " + " | ".join(row) + " |")
            lines.append("")

    # Resource Configuration Consistency
    lines.append("## Resource Configuration Consistency\n")

    # We can extract from config_data if available
    for service, label in [("spring", "Target Spring App"), ("db", "Db")]:
        lines.append(f"### {label}".title())
        lines.append("| Configuration | Base | Candidate |")
        lines.append("| --- | --- | --- |")

        # Find configuration entry containing service name
        def get_cfg_by_service(cfg_data, service_name):
            for key in cfg_data.keys():
                if service_name in key:
                    return cfg_data[key]
            return {}

        b_cfg = get_cfg_by_service(base_cfg, service)
        t_cfg = get_cfg_by_service(candidate_cfg, service)

        lines.append(
            f"| cpuset | {b_cfg.get('CpusetCpus', '-')} | {t_cfg.get('CpusetCpus', '-')} |"
        )
        lines.append(
            f"| memoryLimit (MB) | {b_cfg.get('MemoryLimitMB', '-')} | {t_cfg.get('MemoryLimitMB', '-')} |\n"
        )

    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"Comparison report generated at {output_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate benchmark comparison report")
    parser.add_argument("--base-dir", required=True, help="Baseline result directory")
    parser.add_argument(
        "--candidate-dir", required=True, help="Candidate result directory"
    )
    parser.add_argument("--output", required=True, help="Output markdown file")

    args = parser.parse_args()

    report_benchmark_result(args.base_dir, args.candidate_dir, args.output)
