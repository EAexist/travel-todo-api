package com.matchalab.trip_todo_api.model.Reservation;

import javax.validation.constraints.Size;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.Accomodation;

import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    ReservationCategory category;
    // String dateTimeIsoString;
    // String title;
    // String subtitle;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    String rawText;

    @Nullable
    String code;

    String note;

    @Nullable
    @Column(length = 2048)
    @Size(max = 2048, message = "primaryHrefLink cannot exceed 2048 characters.")
    String primaryHrefLink;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private GeneralReservation generalReservation;

    public Reservation(
            Reservation reservation) {
        category = reservation.getCategory();
        rawText = reservation.getRawText();
        primaryHrefLink = reservation.getPrimaryHrefLink();
        serverFileUri = reservation.getServerFileUri();
        localAppStorageFileUri = reservation.getLocalAppStorageFileUri();
        accomodation = reservation.getAccomodation();
        flightBooking = reservation.getFlightBooking();
        flightTicket = reservation.getFlightTicket();
    }
}