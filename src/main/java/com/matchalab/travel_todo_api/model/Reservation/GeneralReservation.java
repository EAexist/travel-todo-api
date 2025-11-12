package com.matchalab.travel_todo_api.model.Reservation;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class GeneralReservation {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String title;
    @Nullable
    private int numberOfClient;
    @Nullable
    private String clientName;
    // @Nullable
    // private List<String> clientNames;
    @Nullable
    private String dateTimeIsoString;
}
