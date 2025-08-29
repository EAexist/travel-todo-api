package com.matchalab.trip_todo_api.model.Flight;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
// @Builder
@NoArgsConstructor
@EqualsAndHashCode
public class FlightRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id")
    private Airport departure;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrival;

    @OneToMany
    // @Builder.Default
    private List<Airline> airlines = new ArrayList<Airline>();

    public FlightRoute(Airport departure, Airport arrival) {
        this();
        this.departure = departure;
        this.arrival = arrival;
    }
}
