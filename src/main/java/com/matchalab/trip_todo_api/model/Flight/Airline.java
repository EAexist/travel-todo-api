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
    @CsvBindByName(column = "airlineIATACode", required = true)
    private String IATACode;

    @CsvBindByName(column = "airlineName_trimmed", required = true)
    private String name;
}