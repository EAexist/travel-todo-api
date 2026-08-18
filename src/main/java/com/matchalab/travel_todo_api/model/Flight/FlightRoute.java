package com.matchalab.travel_todo_api.model.Flight;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FlightRoute {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "departure_airport_id")
  private Airport departure;

  @ManyToOne
  @JoinColumn(name = "arrival_airport_id")
  private Airport arrival;

  @OneToMany private List<Airline> airlines = new ArrayList<Airline>();

  public FlightRoute(Airport departure, Airport arrival) {
    this();
    this.departure = departure;
    this.arrival = arrival;
  }
}
