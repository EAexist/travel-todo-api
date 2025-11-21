package com.matchalab.travel_todo_api.model.Reservation;

import java.util.UUID;

import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Accomodation;
import com.matchalab.travel_todo_api.model.Trip;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
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
public class Reservation implements Persistable<UUID> {

    @Id
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Builder.Default
    private Boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
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

    // @Nullable
    // private String serverFileUri;

    // @Nullable
    // private String localAppStorageFileUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostPersist
    @PostLoad
    private void setIsNotNew() {
        this.isNew = false;
    }

    public Reservation(
            Reservation reservation) {
        this.id = UUID.randomUUID();
        this.isCompleted = reservation.getIsCompleted();
        this.category = reservation.getCategory();
        // this.rawText = reservation.getRawText();
        this.primaryHrefLink = reservation.getPrimaryHrefLink();
        this.accomodation = reservation.getAccomodation() != null ? new Accomodation(reservation.getAccomodation())
                : null;
        this.flightBooking = reservation.getFlightBooking() != null ? new FlightBooking(reservation.getFlightBooking())
                : null;
        this.flightTicket = reservation.getFlightTicket() != null ? new FlightTicket(reservation.getFlightTicket())
                : null;
        this.generalReservation = reservation.getGeneralReservation() != null
                ? new GeneralReservation(reservation.getGeneralReservation())
                : null;
        // this.serverFileUri = reservation.getServerFileUri();
        // this.localAppStorageFileUri = reservation.getLocalAppStorageFileUri();
    }
}