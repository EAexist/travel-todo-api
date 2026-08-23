# 0001-task-event-architecture

## Status

<!--What is the status, such as proposed, accepted, rejected, deprecated, superseded, etc.? -->

accpeted

## Context

<!--What is the issue that we're seeing that is motivating this decision or change? -->
# 0001-task-event-architecture

## Status

accepted

## Context

기존 아키텍처는 예약 내역 분석 파이프라인을 API 요청 내에서 동기적으로 처리합니다. 예약 내역 분석 파이프라인은 장시간 실행되며 외부 LLM API를 사용합니다.

```text
API Request
    ↓
Spring API
    ↓
External API
    ↓
DB Persistence
    ↓
HTTP 201 CREATED
```

예약 내역 분석 파이프라인이 완료될 떄 까지 API 서버 스레드가 점유되므로 부하가 증가할수록 API 서버 리소스 사용량이 증가하고, API 처리 지연과 지속 가능한 처리량이 제한됩니다.

## Decision

- 장시간 실행되는 Business Operation은 API 서버 내에서 처리하지 않고 별도의 Worker에서 비동기적으로 처리하는 Event-Driven Architecture로 구성되는 것을 원칙으로 합니다. 예약 내역 분석 파이프라인 뿐만 아니라 기획되는 모든 Operation에 대해 적용됩니다.

- 예약 내역 분석 파이프라인을 비동기 Event-Driven Architecture로 변경합니다.

```mermaid
flowchart LR
    Client[Client / k6]
    API[Spring API]
    Message Broker[AWS SQS]
    Worker[Go Worker]
    External[External API]
    DB[(PostgreSQL)]

    Client -->|POST| API
    API -->|Publish Message| Message Broker
    API -->|202 Accepted| Client

    Message Broker -->|Consume Message| Worker
    Worker -->|API Call| External
    Worker -->|Persist Result| DB
```

### API

* Stack: `Spring Boot` (기존과 동일)

* Business Operation에 필요한 정보를 메세지 브로커에 메시지로 발행합니다.
* 메시지가 성공적으로 발행되면 `202 Accepted`를 반환합니다.
* 지연 시간이 큰 외부 API 호출은 API 서버에서 수행하지 않습니다.
* API와 Worker는 독립적으로 확장할 수 있도록 분리합니다.

### Message Broker

* Stack: `AWS SQS`
* 일시적인 Worker 내 오류에 대응해 메세지를 복구할 수 있어야 합니다.

### 예약 내역 분석 Worker
* Stack: `Go`
* 메세지 브로커의 메시지를 소비합니다.
* 기존의 예약 내역 분석 기능을 수행하고, 결과를 데이터베이스에 직접 영속화합니다.
* `Prometheus Go client`를 사용하여 Spring API와 동일한 방식으로 Prometheus metrics를 제공합니다.

## Consequences

### Pros
* API의 P95 지연 시간이 감소하고 리소스 효율이 증가합니다.
* API와 장시간 실행되는 Business Operation이 독립적으로 확장되고 유지보수 됩니다.
* 장시간 실행되는 Business Operation의 장애 및 오류가 API 요청 lifecycle과 분리됩니다.

### Cons & Solutions

* **Queue 및 Worker 관리 워크로드**
  - 구현 워크로드가 상대적으로 적은 AWS SQS, Go 프레임워크를 선택합니다.

* **Queue 및 Worker 관리 비용**
  - 동일한 Business Operation Throughput에서 Spring API, Worker 및 DB의 CPU/Memory 사용량을 비교하여 전체 Resource Efficiency를 평가합니다.