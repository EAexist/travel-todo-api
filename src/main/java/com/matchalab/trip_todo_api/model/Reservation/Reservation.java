package com.matchalab.trip_todo_api.model.Reservation;

import java.util.UUID;

import javax.validation.constraints.Size;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.generator.GeneratedOrCustomUUID;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Trip;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    ReservationCategory category;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String rawText;

    @Nullable
    @Column(length = 2048)
    @Size(max = 2048, message = "primaryHrefLink cannot exceed 2048 characters.")
    private String primaryHrefLink;

    @Nullable
    private String code;

    private String note;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private VisitJapan visitJapan;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private Accomodation accomodation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private FlightBooking flightBooking;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private FlightTicket flightTicket;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private GeneralReservation generalReservation;

    @Nullable
    private String serverFileUri;

    @Nullable
    private String localAppStorageFileUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

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