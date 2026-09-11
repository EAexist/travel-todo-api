package com.matchalab.travel_todo_api.reservation_analysis;

import com.matchalab.travel_todo_api.jobs.Job;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
public class CreateReservationJob extends Job {

  @Column(name = "trip_id", nullable = false)
  private UUID tripId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private CreateReservationPayload payload;

  public CreateReservationJob(UUID tripId, CreateReservationPayload payload){
    this.tripId = tripId;
    this.payload = payload;
  }
}
