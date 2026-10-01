package reservation

import (
	"context"
	"reservation-worker/internal/db"
)

type ReservationRepositoryInterface interface {
	Save(ctx context.Context, reservations []Reservation) error
}

type ReservationRepository struct {
	*db.Repository
}

func NewReservationRepository(base *db.Repository) *ReservationRepository {
	return &ReservationRepository{Repository: base}
}

func (s *ReservationRepository) Save(ctx context.Context, reservations []Reservation) error {
	return s.GetDB(ctx).Create(&reservations).Error
}
