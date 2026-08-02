package com.matchalab.travel_todo_api.model.Flight;

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
    private String icaoCode;

    private String iataCode;

    private String title;
}