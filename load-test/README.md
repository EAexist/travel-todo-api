## Required Constraints

### ./compose.loadtest.yml

CPUsets and memory limit/reservation are configured in ./compose.loadtest.yml. 

### WSL Setup (Windows)

- [ ] .wslconfig
  ```
  [wsl2]
  memory=4GB
  processors=6
  swap=0
  kernelCommandLine=isolcpus=0-3 irqaffinity=4,5
  ```
- [ ] Process Lasso

    | Process                                            | CPU Affinity |
    |----------------------------------------------------|--------------|
    | VM processes `\Processor(_Total)\% Processor Time` | 0~5 (target) |
    | All other processes                                | 6~ (extra)   |


## Supplementary Resource Condition Metrics Tracking

### Host (Running WSL2)
#### 1. Goal
- WSL 환경에서 테스트를 진행할 경우 host(windows)의 메모리 상태를 함께 모니터링 해야 합니다.
- host에 메모리 압박이 발생하면 guest 메모리(VM)를 동적으로 회수하거나 page cache evictions을 발생시키거나 hard page fault가 발생해 WSL 리소스 조건이 동일하지 않게 됩니다.

### 2. Required Host Metrics

| Metric Name                                                            | Source Collector | Target Threshold / Condition                     | Verification Intent                                                   |
|------------------------------------------------------------------------|------------------|--------------------------------------------------|-----------------------------------------------------------------------|
| `windows_os_physical_memory_free_bytes`                                | `os`             | $> 2\text{ GB}$ ($> ?\text{ bytes}$) | Host 메모리 부족으로 인한 VM 메모리 reclamation이 없었을지 확인.                         |
| `windows_hyperv_dynamic_memory_vm_guest_visible_physical_memory_bytes` | `hyperv`         | Flat line at `.wslconfig` limit                  | Hyper-V가 테스트 중 WSL physical RAM allocation을 축소하지 않았는지 확인.             |
| `windows_hyperv_dynamic_memory_vm_current_pressure`                    | `hyperv`         | $\le 100$                                        | Vm memory currently allocated < Vm memory demand 인 상황이 발생하지 않았는지 확인.  |

---

### 3. Example Host Grafana Alloy Config

테스트 시작 전 Host에서 다음과 같이 `config.host.alloy`를 작성하고 alloy를 실행(`alloy run config.host.alloy`)합니다. 

```alloy
// 1. Enable lightweight Windows host metrics (OS + Hyper-V only)
prometheus.exporter.windows "host" {
  enabled_collectors = ["os", "hyperv"]
}

// 2. Scrape host performance counters
prometheus.scrape "windows_host_metrics" {
  targets         = prometheus.exporter.windows.host.targets
  scrape_interval = "5s"
  forward_to      = [prometheus.remote_write.prometheus_server.receiver]
}

// 3. Forward to central Prometheus / Grafana agent
prometheus.remote_write "prometheus_server" {
  endpoint {
    url = "http://localhost:9090/api/v1/write"
  }
}
```

### Container (Docker)

Rate of steal time over the isolcpus set, one time series per core (4 series):
```promql
rate(node_cpu_seconds_total{mode="steal", cpu=~"0|1|2|3"}[30s])
```
Aggregated series across the whole set:
```promql
avg(rate(node_cpu_seconds_total{mode="steal", cpu=~"0|1|2|3"}[30s]))
```
For your final report, you want the run's peak, not just a live-view rate — use max_over_time wrapping the rate, across the exact run window:

```promql
max_over_time(
  (avg(rate(node_cpu_seconds_total{mode="steal", cpu=~"0|1|2|3"}[30s])))[10m:30s]
)
```
<!--
| Metrics                                                                                        | Argument                                                                                              |
| ---------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| **Memory Availability** — `node_memory_MemAvailable_bytes`                                     | Confirms WSL2 did not experience materially different memory pressure across A/B runs.                |
| **Swap Usage** — `/proc/meminfo`                                                               | Zero swap activity confirms paging did not introduce additional latency variation.                    |
| **CPU Utilization** — WSL processes / CPU activity outside benchmark containers | Detects WSL-side workload that could interfere with the benchmark independently of the intended load. |
| **CPU Utilization** — WSL processes / CPU activity outside benchmark containers | Detects WSL-side workload that could interfere with the benchmark independently of the intended load. |
| **CPU Throttling** — Docker `cpu.stat`                                                         | Confirms containers were not unexpectedly throttled by their configured CPU limits.                   |
| **CPU Affinity** — Docker `cpuset`                                                             | Confirms benchmark containers remained restricted to the same CPUs across A/B runs.                   |
-->