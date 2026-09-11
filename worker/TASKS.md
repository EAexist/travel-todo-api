# Tasks

Refer to the C:\Users\hyeon\.gemini\tmp\worker\b54d32ea-9edb-447f-aaa9-425164ea3d4a\plans/reservation-worker-implementation.md for plan

- [x] Initialize Go module and project structure
- [x] Define Go structs for `Job`, `Reservation`, and `ReservationResponseDto`
- [x] Implement AWS SQS client connection
- [x] Implement PostgreSQL client connection
- [x] Implement message processing loop
- [x] Add unit tests for `internal/sqs`, `internal/db`, `internal/processor`
- [x] Implement Gemini API client and prompt template management
- [x] Implement parsing and mapping from Gemini response to `Reservation` entities
- [x] Implement persistence logic for `Reservation` entities
- [x] Configure .env file based environment variable system for sensitive/configurable values
- [x] Prepare Dockerfile for the worker
- [x] Add configuration management and logging
- [x] Define `ReservationRepositoryInterface` in `internal/store/reservation_store.go`
- [x] Refactor `Processor` to use `ReservationRepositoryInterface`
- [x] Update `internal/processor/processor_test.go` to use `MockReservationRepository`
- [x] Implement AI call logic in `processMessage`
- [x] Implement Gemini response parsing and mapping to `Reservation`
- [x] Implement persistence using `ReservationRepository`
