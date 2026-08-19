# 0001-reservation-polymorphism-strategy

## Status

<!--What is the status, such as proposed, accepted, rejected, deprecated, superseded, etc.? -->

accpeted

## Context

<!--What is the issue that we're seeing that is motivating this decision or change? -->

### Reservation 데이터 모델 요구사항 
1. 자주 사용되는 예약 타입(e.g. `숙소`, `항공권`, `항공기 탑승권`, `비짓 재팬`) 각각에 대해 서로 다른 속성 집합을 정의해야 합니다.
2. 기획을 통해 새로운 예약 타입(e.g. 특정 티켓팅 플랫폼을 통해 예약된 티켓, 열차 또는 버스 예약)과, 그에 해당하는 속성 집합을 추가할 수 있어야 합니다.
3. 타입을 지정하지 않은 일반적인 예약을 저장할 수 있는 모델을 정의해야 합니다.
4. 타입에 상관 없이 모든 예약 타입이 공유하는 공통 속성들을 정의할 수 있어야 하고, 기획을 통해 공통 속성 집합을 변경할 수 있어야 합니다.
5. 사용자가 `항공권` 예약을 추가하면 이 예약에 의존하는 `발권(체크인)` 할 일이 추가되고 관리되어야 합니다.

### Reservation 관련 Access Pattern
B-1, B-2, B-3, B-4, B-5, C-5, C-7 ([Access Pattern Matrix](../access-pattern-matrix.md))

### 아키텍처 비교

| Criteria                                          | Class Table                                                                                                   | Single Table                                                               | Single Table + Hybrid JSONB                                                                                                 |
|:--------------------------------------------------|:--------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|
| **Creating Multiple Unkown Same Subtypes (C-5)**  | **Low** Requires multi-table transaction per record. Cannot perform a single plain multi-row SQL `INSERT`.    | **Maximum** Single query `INSERT INTO reservations VALUES (...)`.                | **Maximum** Single-table write.                                                                                              |
| **Reading Multiple Unkown Same Subtypes (C-7)**   | **Low** Requires `LEFT JOIN` across all subtype tables or `UNION ALL`. **Scales poorly as sub-tables grow.**  | **Maximum** Single query scan (`SELECT * FROM reservations WHERE trip_id = ?`).  | **Maximum** Single query scan (`SELECT * FROM reservations WHERE trip_id = ?`).                                              |
| **Reading Multiple Subtypes(B-1)**                | **Low** Requires `LEFT JOIN` across all subtype tables or `UNION ALL`. **Scales poorly as sub-tables grow.**  | **Maximum** Single query scan (`SELECT * FROM reservations WHERE trip_id = ?`).  | **High** Single query scan (`SELECT * FROM reservations WHERE trip_id = ?`). Requires dynamic JSON payload handling per row. |
| **Updating Subtype-Specific Fields(B-3)**         | **High** Direct update on child table using indexed PK. Base table untouched.                                 | **High** Direct update on base table row using indexed PK.                       | **Moderate** Requires JSON update operations (`jsonb_set` or application layer full overwrite).                              |
| **Workload of Adding New Subtype Class**          | **High** Requires `CREATE TABLE` DDL, PK/FK constraints, application ORM mappings update.                     | **Low** Requires `ALTER TABLE` DDL to append nullable columns.                   | **Zero** No schema migrations required. Handled at the application layer.                                                    |

## Decision

<!--What is the change that we're proposing and/or doing?-->

Single Table + Hybrid JSONB 아키텍처
  
## Consequences

<!--What becomes easier or more difficult to do because of this change?-->

### Pros
- 새 예약 타입을 도입할 때 DB 스키마 변경 없이 application 계층 수정만으로 해결할 수 있습니다.
- 클라이언트에서 예약 내역을 다루는 스키마를 변경할 때 마찬가지로 DB 스키마 변경 없이 DB migration 과 어플리케이션 계층 수정만으로 해결할 수 있습니다.
- 배치 Read(API) / Write(Worker) 쿼리 요청이 더 효율적입니다. 쿼리 빈도는 예약 분석 요청 빈도 N에 비례해 서비스가 성장할 수록 많은 DB 리소스를 절감합니다.

### Cons & Solutions
- 기존 데이터 모델을 변경할 때, 특정 예약 타입의 스키마 변경 마이그레이션을 할 때, 새 예약 타입을 도입할 때 DB 계층에서 데이터 무결성이 기본적으로 보장되지 않습니다.
  - DB 계층 CHECK statement 검증 추가
  - application 계층에서 검증 추가