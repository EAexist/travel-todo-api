package com.matchalab.trip_todo_api.model.Flight;

import com.opencsv.bean.CsvBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    @Id
    @CsvBindByName(column = "airlineIcaoCode", required = true)
    private String icaoCode;

    @CsvBindByName(column = "airlineIataCode", required = true)
    private String iataCode;

    @CsvBindByName(column = "airlineName_trimmed", required = true)
    private String title;
}