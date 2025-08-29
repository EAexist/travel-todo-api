package com.matchalab.trip_todo_api.model.Flight;

import com.matchalab.trip_todo_api.model.Todo.Todo;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "trip_id")
    // Trip trip;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Nullable
    Todo todo;

    String flightNumber;
    String departure;
    String arrival;
    int numberOfPassenger;
    String[] passengerNames;
    String departureDateTimeISOString;
    String arrivalDateTimeISOString;
}
