package create_reservation_job

import (
	"context"
	"reservation-worker/internal/db"

	"github.com/google/uuid"
)

type CreateReservationJobRepositoryInterface interface {
	Find(ctx context.Context, id uuid.UUID) (*CreateReservationJob, error)
}

type CreateReservationJobRepository struct {
	*db.Repository
}

func NewCreateReservationJobRepository(base *db.Repository) *CreateReservationJobRepository {
	return &CreateReservationJobRepository{Repository: base}
}

func (r *CreateReservationJobRepository) Find(ctx context.Context, id uuid.UUID) (*CreateReservationJob, error) {
	job := &CreateReservationJob{}

	err := r.GetDB(ctx).Take(job, "id = ?", id).Error
	if err != nil {
		return nil, err
	}

	return job, nil
}
