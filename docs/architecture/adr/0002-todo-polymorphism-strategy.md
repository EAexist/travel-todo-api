# 0001-reservation-polymorphism-strategy

## Status

What is the status, such as proposed, accepted, rejected, deprecated, superseded, etc.?

- accepted

## Context

What is the issue that we're seeing that is motivating this decision or change?

1 .Reservation 데이터 모델은
A. 자주 사용되는 예약 타입(e.g. 숙소, 항공권, ...) 각각에 대해 서로 다른 정형화된 모델을 정의해야 합니다.
B. 새로운 예약 타입과, 그에 해당하는 정형화된 모델을 추가할 수 있는 확장성을 가져야 합니다.
C. 타입을 지정하지 않은 일반적인 예약을 저장할 수 있는 정형화된 모델을 정의해야 합니다.
D. 타입에 상관 없이 모든 예약 타입이 공유하는 공통 속성을 정의할 수 있어야 하고, 이 속성 목록이 확장성을 가져야 합니다.
E. 사용자가 <항공권> 예약을 추가하면 이 예약에 의존하는 발권(체크인) 할 일이 추가되고 관리되어야 합니다.

## Decision

What is the change that we're proposing and/or doing?

- Reservation 데이터 모델에는 polymorphism이 구현되어야 합니다. (1-A, 1-B, 1-C, 1-D)
- Reservation 데이터 모델에는 타입과 상관없이 공유하는 공통 속성만 정의합니다. (1-D)
- 각 예약 타입 및 타입을 지정하지 않은 일반적인 예약에 대응하는 데이터 모델을 새로 정의하고, reservation_id FK 를 지정합니다. (1-A, 1-B, 1-C)
- <항공권 예약하기> 타입 할 일에 flight_reservation_id nullable FK 가 지정되어야합니다. (1-E)

## Consequences

What becomes easier or more difficult to do because of this change?

### +

- Reservation 데이터의 data integrity
- 새 Reservation 타입 확장 안정성, 안편리함
- <항공권> 예약에 따른 발권(체크인) 할일 자동 추가 UX 구현의 편리함

### -

- 예약 타입 개수만큼의 새로운 테이블 유지 보수 workload