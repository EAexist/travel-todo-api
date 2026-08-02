package com.matchalab.travel_todo_api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.travel_todo_api.model.Flight.FlightRoute;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "destination", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_destination_title_code_region",
                columnNames = {"title", "iso2DigitNationCode", "region"}
        )
})
// @Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // @ManyToMany(mappedBy = "destinations")
    // @JsonIgnore
    // @Builder.Default
    // private List<Trip> trips = new ArrayList<Trip>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    // @Builder.Default
    private List<TripDestination> trips = new ArrayList<TripDestination>();

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

    public void addRecommendedOutboundFlight(List<FlightRoute> flightRoutes) {
        this.recommendedOutboundFlight.clear();
        this.recommendedOutboundFlight.addAll(flightRoutes);
    }

    public void addRecommendedReturnFlight(List<FlightRoute> flightRoutes) {
        this.recommendedReturnFlight.clear();
        this.recommendedReturnFlight.addAll(flightRoutes);
    }
}
