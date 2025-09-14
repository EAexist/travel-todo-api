package com.matchalab.trip_todo_api.model.Reservation;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.enums.ReservationType;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Flight.Flight;
import com.matchalab.trip_todo_api.model.Flight.FlightBooking;
import com.matchalab.trip_todo_api.model.Flight.FlightTicket;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // String dateTimeISOString;
    ReservationType type;
    // String title;
    // String subtitle;

    String rawText;

    @Nullable
    String link;

    @Nullable
    String serverFileUri;

    @Nullable
    String localAppStorageFileUri;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    Accomodation accomodation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private FlightBooking flightBooking;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private FlightTicket flightTicket;

    public Reservation(
            Reservation reservation) {
        type = reservation.getType();
        rawText = reservation.getRawText();
        link = reservation.getLink();
        serverFileUri = reservation.getServerFileUri();
        localAppStorageFileUri = reservation.getLocalAppStorageFileUri();
        accomodation = reservation.getAccomodation();
        flightBooking = reservation.getFlightBooking();
        flightTicket = reservation.getFlightTicket();
    }
}