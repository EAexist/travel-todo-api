package com.matchalab.trip_todo_api.model;

import com.google.auto.value.AutoValue.Builder;
import com.opencsv.bean.CsvBindByName;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@RequiredArgsConstructor
public class Airport {

    @Id
    @CsvBindByName(column = "airportName", required = true)
    String airportName;

    @CsvBindByName(column = "cityName", required = true)
    String cityName;

    @CsvBindByName(column = "countryISO", required = true)
    String countryISO;

    @CsvBindByName(column = "iataCode", required = true)
    String iataCode;
}
