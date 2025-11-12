package com.matchalab.travel_todo_api.model.Flight;

import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Id
    @CsvBindByName(column = "iataCode", required = true)
    private String iataCode;

    @CsvBindByName(column = "airportName", required = true)
    private String airportName;

    @CsvBindByName(column = "cityName", required = true)
    private String cityName;

    @CsvBindByName(column = "iso2DigitNationCode", required = true)
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
