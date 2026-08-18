package com.matchalab.travel_todo_api.model.Flight;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Airport {

  @Id private String iataCode;

  private String airportName;

  private String cityName;

  private String iso2DigitNationCode;

  @OneToMany(mappedBy = "departure")
  @Builder.Default
  private List<FlightRoute> departingFlightRoutes = new ArrayList<FlightRoute>();

  @OneToMany(mappedBy = "arrival")
  @Builder.Default
  private List<FlightRoute> arrivingFlightRoutes = new ArrayList<FlightRoute>();

  public Airport(String iataCode) {
    this.iataCode = iataCode;
  }
}
