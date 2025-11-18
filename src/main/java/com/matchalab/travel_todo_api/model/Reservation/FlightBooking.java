package com.matchalab.travel_todo_api.model.Reservation;

import com.matchalab.travel_todo_api.model.Flight.Airport;
import com.matchalab.travel_todo_api.model.Flight.Flight;
import com.matchalab.travel_todo_api.model.Todo.Todo;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FlightBooking extends Flight {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Nullable
    Todo todo;

    @ManyToOne
    Airport departureAirport;

    @ManyToOne
    Airport arrivalAirport;

    @Nullable
    int numberOfPassenger;

    String passengerName;

    public FlightBooking(FlightBooking flightBooking) {
        super(flightBooking.getFlightNumber(), flightBooking.getDepartureDateTimeIsoString());
        // this.todo = flightBooking.getTodo();
        this.departureAirport = flightBooking.getDepartureAirport();
        this.arrivalAirport = flightBooking.getArrivalAirport();
        this.numberOfPassenger = flightBooking.getNumberOfPassenger();
        this.passengerName = flightBooking.getPassengerName();
    }
}
