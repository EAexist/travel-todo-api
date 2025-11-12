package com.matchalab.travel_todo_api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.travel_todo_api.enums.AccomodationCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Accomodation {

    @Id
    private UUID id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "trip_id")
    // private Trip trip;
    @Enumerated(EnumType.STRING)
    private AccomodationCategory category;

    private String title;
    private String roomTitle;
    private String location;
    private int numberOfClient;
    private String clientName;
    private String checkinDateIsoString;
    private String checkoutDateIsoString;
    private String checkinStartTimeIsoString;
    private String checkinEndTimeIsoString;
    private String checkoutTimeIsoString;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<Link> links = new ArrayList<Link>();

    public Accomodation(Accomodation accomodation) {
        this.title = accomodation.getTitle();
        // this.trip = accomodation.getTrip();
        this.roomTitle = accomodation.getRoomTitle();
        this.numberOfClient = accomodation.getNumberOfClient();
        this.clientName = accomodation.getClientName();
        this.checkinDateIsoString = accomodation.getCheckinDateIsoString();
        this.checkoutDateIsoString = accomodation.getCheckoutDateIsoString();
        this.checkinStartTimeIsoString = accomodation.getCheckinStartTimeIsoString();
        this.checkinEndTimeIsoString = accomodation.getCheckinEndTimeIsoString();
        this.checkoutTimeIsoString = accomodation.getCheckoutTimeIsoString();
        this.location = accomodation.getLocation();
        this.category = accomodation.getCategory();
        this.links = accomodation.getLinks();
    }
}
