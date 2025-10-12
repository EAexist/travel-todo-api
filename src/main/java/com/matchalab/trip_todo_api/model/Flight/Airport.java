package com.matchalab.trip_todo_api.model.Flight;

import com.opencsv.bean.CsvBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @CsvBindByName(column = "IataCode", required = true)
    private String iataCode;

    @CsvBindByName(column = "airportName", required = true)
    private String airportName;

    @CsvBindByName(column = "cityName", required = true)
    private String cityName;

    @CsvBindByName(column = "iso2DigitNationCode", required = true)
    private String iso2DigitNationCode;

    public Airport(String iataCode) {
        this.iataCode = iataCode;
    }
}
