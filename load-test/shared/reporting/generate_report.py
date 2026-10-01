import json
from pathlib import Path


def generate_report(output_dir, title):
    test_path = Path(output_dir)

    with open(test_path / "result.json", "r") as f:
        report_data = json.load(f)
    with open(test_path / "resource_config.json", "r") as f:
        config_data = json.load(f)
    with open(test_path / "test-summary.json", "r") as f:
        summary_data = json.load(f)

    # Build report content
    lines = [f"# {title}\n"]

    # 1. Performance Results (Dynamic)
    lines.append("## Performance\n")

    for metric_name, data in report_data.get("latencies", {}).items():
        lines.append(f"### {metric_name}")
        lines.append("| Iteration | P50 (ms) | P95 (ms) | P99 (ms) |")
        lines.append("| --- | --- | --- | --- |")
        agg = data["aggregate"]
        lines.append(
            f"| **Total** | {agg['p50'] * 1000:.2f} | {agg['p95'] * 1000:.2f} | {agg['p99'] * 1000:.2f} |"
        )
        for idx, it in enumerate(data.get("iterations", [])):
            lines.append(
                f"| {idx + 1} | {it['p50'] * 1000:.2f} | {it['p95'] * 1000:.2f} | {it['p99'] * 1000:.2f} |"
            )
        lines.append("")

    # 1.1 Throughput (Conditional)
    throughput = report_data.get("throughput", {})
    if throughput:
        lines.append("### Throughput")
        lines.append("| Metric | Mean (rps) | Std Dev (rps) |")
        lines.append("| --- | --- | --- |")
        for name, stats in throughput.items():
            lines.append(
                f"| {name.upper()} | {stats['mean']:.2f} | {stats['std_dev']:.2f} |"
            )
        lines.append("Unit: rps")
        lines.append("")

    # 2. Resource Condition Proof
    lines.append("## Resource Conditions\n")

    lines.append("### Configurations")
    lines.append("| Service | Memory Limit (MB) | CPUset |")
    lines.append("| --- | --- | --- |")
    for service, cfg in config_data.items():
        lines.append(f"| {service} | {cfg['MemoryLimitMB']} | {cfg['CpusetCpus']} |")
    lines.append("")

    # 3. Resource Usage (Dynamic)
    lines.append("### Usage Stats")
    for service_name, metrics in report_data.get("resources", {}).items():
        lines.append(f"#### {service_name.replace('_', ' ').title()}")
        cpu = metrics["cpu"]
        mem_avg = metrics["memory_avg"]
        mem_peak = metrics["memory_peak"]
        lines.append(
            f"- **Mean Peak Memory Usage**: {mem_peak['mean'] / 1024 / 1024:.2f} MB"
        )
        lines.append(
            f"- **Mean Avg Memory Usage**: {mem_avg['mean'] / 1024 / 1024:.2f} MB"
        )
        lines.append(f"- **Mean CPU Usage**: {cpu['mean']:.4f} {cpu['unit']}\n")

    # 4. Resource Usage Time Series
    grafana_dir = test_path / "grafana"
    if grafana_dir.exists() and grafana_dir.is_dir():
        image_files = sorted(list(grafana_dir.glob("*.png")))
        if image_files:
            lines.append("### Usage Time Series")
            for img in image_files:
                import urllib.parse

                safe_name = urllib.parse.quote(img.name)
                lines.append(f"![{img.name}](grafana/{safe_name})")
            lines.append("")

    # 0. Test Configuration Summary
    lines.append("## Load Test Configuration")
    lines.append(f"- **Target URI**: `{summary_data['uri']}`")
    lines.append(
        f"- **Iterations**: {sum(1 for i in summary_data['iterations'] if i['is_target'])}"
    )
    lines.append(
        f"- **Duration per Iteration**: {summary_data['iterations'][1]['duration']}\n"
    )

    # Save to report.md
    with open(test_path / "report.md", "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"Report generated at {test_path / 'report.md'}")


def generate_staged_test_report(output_dir, title):
    test_path = Path(output_dir)

    with open(test_path / "result.json", "r") as f:
        report_data = json.load(f)
    with open(test_path / "1" / "resource_config.json", "r") as f:
        config_data = json.load(f)
    with open(test_path / "1" / "test-summary.json", "r") as f:
        summary_data = json.load(f)

    # Build report content
    lines = [f"# {title}\n"]

    # 1. Performance Results (Dynamic)
    lines.append("## Performance\n")
    all_stages = list(report_data.values())
    if all_stages:
        first_stage = all_stages[0]
        for metric_name in first_stage.get("latencies", {}).keys():
            lines.append(f"### {metric_name}")
            for stage_id, stage_data in report_data.items():
                target_rps = stage_data.get("target_rps", "unknown")
                lines.append(f"#### Target RPS {target_rps}")
                lines.append("| Iteration | P95 (s) | P50 (s) | P99 (s) |")
                lines.append("| --- | --- | --- | --- |")
                data = stage_data["latencies"][metric_name]
                agg = data["aggregate"]
                lines.append(
                    f"| **Total** | {agg['p95']:.3f} | {agg['p50']:.3f} | {agg['p99']:.3f} |"
                )
                for idx, it in enumerate(data.get("iterations", [])):
                    lines.append(
                        f"| {idx + 1} | {it['p95']:.3f} | {it['p50']:.3f} | {it['p99']:.3f} |"
                    )
                lines.append("")

        # 1.1 Throughput (Conditional)
        if "throughputs" in first_stage:
            lines.append("### Throughput")
            for metric_name in first_stage["throughputs"].keys():
                lines.append(f"#### {metric_name.upper()}")
                lines.append("| Target RPS | Throughput (rps) |")
                lines.append("| --- | --- |")
                for stage_id, stage_data in report_data.items():
                    target_rps = stage_data.get("target_rps", "unknown")
                    stats = stage_data["throughputs"][metric_name]
                    lines.append(
                        f"| {target_rps} | {stats['mean']:.2f} ± {stats['std_dev']:.2f} |"
                    )
                lines.append("")

    # 2. Resource Condition Proof
    lines.append("## Resource Conditions\n")

    lines.append("### Configurations")
    lines.append("| Service | Memory Limit (MB) | CPUset |")
    lines.append("| --- | --- | --- |")
    for service, cfg in config_data.items():
        lines.append(f"| {service} | {cfg['MemoryLimitMB']} | {cfg['CpusetCpus']} |")
    lines.append("")

    # 3. Resource Usage (Dynamic)
    lines.append("### Usage Stats")
    lines.append(
        "| Target RPS | Service | Avg Memory (MB) | Peak Memory (MB) | Mean CPU |"
    )
    lines.append("| --- | --- | --- | --- | --- |")
    for stage_id, stage_data in report_data.items():
        target_rps = stage_data.get("target_rps", "unknown")
        for service_name, metrics in stage_data.get("resources", {}).items():
            cpu = metrics["cpu"]
            mem_avg = metrics["memory_avg"]
            mem_peak = metrics["memory_peak"]
            lines.append(
                f"| {target_rps} | {service_name.replace('_', ' ').title()} | {mem_avg['mean'] / 1024 / 1024:.2f} | {mem_peak['mean'] / 1024 / 1024:.2f} | {cpu['mean']:.4f} {cpu['unit']} |"
            )
    lines.append("")

    # 0. Test Configuration Summary
    lines.append("## Load Test Configuration")
    lines.append(f"- **Target URI**: `{summary_data['uri']}`")
    lines.append(f"- **Iterations**: {all_stages[0]['iterations']}")
    lines.append(
        f"- **Stages**: {sum(1 for i in summary_data['stages'] if i['is_target'])}"
    )
    # Using the duration of the first target stage if available
    target_stages = [i for i in summary_data["stages"] if i["is_target"]]
    if target_stages:
        lines.append(f"- **Duration per Stage**: {target_stages[0]['duration']}\n")

    # Save to report.md
    with open(test_path / "report.md", "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"Report generated at {test_path / 'report.md'}")


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Generate performance report")
    parser.add_argument(
        "--test-dir", required=True, help="Directory containing report data"
    )
    parser.add_argument(
        "--title", default="ORM Efficiency Benchmark Report", help="Report Title"
    )
    parser.add_argument(
        "--staged", action="store_true", help="Use staged report generation"
    )
    args = parser.parse_args()
    if args.staged:
        generate_staged_test_report(args.test_dir, args.title)
    else:
        generate_report(args.test_dir, args.title)
