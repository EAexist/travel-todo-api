package com.matchalab.trip_todo_api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToMany(mappedBy = "destination")
    @JsonIgnore
    // @Builder.Default
    private List<Trip> trip = new ArrayList<Trip>();

    private String title;
    private String iso2DigitNationCode;
    private String region;
    private String description;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "destination_outbound", joinColumns = @JoinColumn(name = "destination_id"), inverseJoinColumns = @JoinColumn(name = "flight-route_id"))
    // @Builder.Default
    private List<FlightRoute> recommendedOutboundFlight = new ArrayList<FlightRoute>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "destination_return", joinColumns = @JoinColumn(name = "destination_id"), inverseJoinColumns = @JoinColumn(name = "flight-route_id"))
    // @Builder.Default
    private List<FlightRoute> recommendedReturnFlight = new ArrayList<FlightRoute>();

    public Destination(Destination destination) {
        this();
        this.id = destination.getId();
        this.description = destination.getDescription();
        this.iso2DigitNationCode = destination.getIso2DigitNationCode();
        this.title = destination.getTitle();
        this.region = destination.getRegion();
    }

    public Destination(String title,
            String iso2DigitNationCode,
            String region,
            String description) {
        this();
        this.title = title;
        this.iso2DigitNationCode = iso2DigitNationCode;
        this.region = region;
        this.description = description;
    }
}
