package com.matchalab.trip_todo_api.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @ManyToMany(mappedBy = "destination")
    // private List<Trip> trip = new ArrayList<Trip>();

    private final String title;
    private final String countryISO;
    private final String region;
    private final String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "destination_outbound", joinColumns = @JoinColumn(name = "destination_id"), inverseJoinColumns = @JoinColumn(name = "flightRoute_id"))
    @Builder.Default
    private List<FlightRoute> recommendedOutboundFlight = new ArrayList<FlightRoute>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "destination_return", joinColumns = @JoinColumn(name = "destination_id"), inverseJoinColumns = @JoinColumn(name = "flightRoute_id"))
    @Builder.Default
    private List<FlightRoute> recommendedReturnFlight = new ArrayList<FlightRoute>();

    public Destination(Destination destination) {
        // this.trip = destination.getTrip();
        this.description = destination.getDescription();
        this.countryISO = destination.getCountryISO();
        this.title = destination.getTitle();
        this.region = destination.getRegion();
    }
}
