package com.matchalab.trip_todo_api.model.Reservation;

import com.matchalab.trip_todo_api.enums.ReservationType;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Trip;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    Trip trip;

    String dateTimeISOString;
    ReservationType type;
    String title;
    String subtitle;

    @Nullable
    String link;

    @Nullable
    String serverFileUri;

    @Nullable
    String localAppStorageFileUri;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Nullable
    Accomodation accomodation;

    public Reservation(
            String dateTimeISOString,
            ReservationType type,
            String title,
            String subtitle) {
        this.dateTimeISOString = dateTimeISOString;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
    }
}