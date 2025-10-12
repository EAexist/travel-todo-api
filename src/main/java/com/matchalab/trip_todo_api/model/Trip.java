package com.matchalab.trip_todo_api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @Builder.Default
    private String createDateIsoString = Instant.now().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount userAccount;

    private String title;
    private String startDateIsoString;
    private String endDateIsoString;

    @Builder.Default
    private Boolean isInitialized = false;

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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservation = new ArrayList<Reservation>();

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    private TodoPreset todoPreset;

    public Trip(Trip trip) {
        this();
        this.title = trip.getTitle();
        this.startDateIsoString = trip.getStartDateIsoString();
        this.endDateIsoString = trip.getEndDateIsoString();
        this.destination = trip.getDestination();
        this.todolist = trip.getTodolist();
        this.reservation = trip.getReservation();
    }

    public void addTodo(Todo todo) {
        todolist.add(todo);
        // todo.setTrip(this); // This is the key step
    }
}
