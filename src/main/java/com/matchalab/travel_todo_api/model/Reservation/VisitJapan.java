package com.matchalab.travel_todo_api.model.Reservation;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class VisitJapan {

  @Nullable String dateTimeIsoString;
  @Id @NonNull @Builder.Default private UUID id = UUID.randomUUID();

  public VisitJapan(VisitJapan visitJapan) {
    this.id = UUID.randomUUID();
    this.dateTimeIsoString = visitJapan.getDateTimeIsoString();
  }
}
