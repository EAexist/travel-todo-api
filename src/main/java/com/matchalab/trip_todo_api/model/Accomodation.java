package com.matchalab.trip_todo_api.model;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Accomodation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "trip_id")
    // private Trip trip;
    private String title;
    private String roomTitle;
    private int numberOfGuest;
    private String clientName;
    private String checkinDateISOString;
    private String checkoutDateISOString;
    private String checkinStartTimeISOString;
    private String checkinEndTimeISOString;
    private String checkoutTimeISOString;
    private String region;
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> links;

    public Accomodation(Accomodation accomodation) {
        this.title = accomodation.getTitle();
        // this.trip = accomodation.getTrip();
        this.roomTitle = accomodation.getRoomTitle();
        this.numberOfGuest = accomodation.getNumberOfGuest();
        this.clientName = accomodation.getClientName();
        this.checkinDateISOString = accomodation.getCheckinDateISOString();
        this.checkoutDateISOString = accomodation.getCheckoutDateISOString();
        this.checkinStartTimeISOString = accomodation.getCheckinStartTimeISOString();
        this.checkinEndTimeISOString = accomodation.getCheckinEndTimeISOString();
        this.checkoutTimeISOString = accomodation.getCheckoutTimeISOString();
        this.region = accomodation.getRegion();
        this.type = accomodation.getType();
        this.links = accomodation.getLinks();
    }
}
