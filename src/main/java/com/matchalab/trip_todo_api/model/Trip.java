package com.matchalab.trip_todo_api.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.model.Flight.Flight;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String startDateISOString;
    private String endDateISOString;

    @Builder.Default
    private boolean isInitialized = false;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "userAccount_id")
    // private UserAccount userAccount;

    @Builder.Default
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "trip_to_destination", joinColumns = @JoinColumn(name = "trip_id"), inverseJoinColumns = @JoinColumn(name = "destination_id"))
    private List<Destination> destination = new ArrayList<Destination>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todolist = new ArrayList<Todo>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accomodation> accomodation = new ArrayList<Accomodation>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flight = new ArrayList<Flight>();

    // @Builder.Default
    // @JdbcTypeCode(SqlTypes.JSON)
    // private List<FlightRoute> recommendedFlight = new ArrayList<FlightRoute>();

    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservation = new ArrayList<Reservation>();

    @Nullable
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TodoPreset todoPreset;

    public Trip(Trip trip) {
        this.title = trip.getTitle();
        this.startDateISOString = trip.getStartDateISOString();
        this.endDateISOString = trip.getEndDateISOString();
        this.destination = trip.getDestination();
        this.todolist = trip.getTodolist();
        this.accomodation = trip.getAccomodation();
    }

    public void addTodo(Todo todo) {
        todolist.add(todo);
        // todo.setTrip(this); // This is the key step
    }
}
