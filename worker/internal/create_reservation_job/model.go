package create_reservation_job

import (
	"reservation-worker/internal/reservation"
	"time"

	"github.com/google/uuid"
	"gorm.io/datatypes"
)

type CreateReservationJob struct {
	ID        uuid.UUID                                    `json:"id"`
	Status    string                                       `json:"status"`
	CreatedAt time.Time                                    `json:"created_at"`
	TripID    uuid.UUID                                    `json:"trip_id"`
	Payload   datatypes.JSONType[CreateReservationPayload] `json:"payload"`
}

type CreateReservationPayload struct {
	Category         reservation.ReservationCategory
	ConfirmationText string
}

func (CreateReservationJob) TableName() string {
	return "create_reservation_job"
}
