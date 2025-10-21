package com.matchalab.trip_todo_api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import jakarta.persistence.OneToOne;
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
    private UUID id;

    @Builder.Default
    private String createDateIsoString = Instant.now().toString();

    private String title;
    private String startDateIsoString;
    private String endDateIsoString;

    @Builder.Default
    private Boolean isInitialized = false;

    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "trip_to_destination", joinColumns = @JoinColumn(name = "trip_id"), inverseJoinColumns = @JoinColumn(name = "destination_id"))
    @Builder.Default
    private List<Destination> destination = new ArrayList<Destination>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Todo> todolist = new ArrayList<Todo>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<Reservation>();

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private TodoPreset todoPreset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccount_id")
    private UserAccount userAccount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "settings_id")
    @Builder.Default
    private TripSettings settings = new TripSettings();

    public Trip(Trip trip) {
        this();
        this.title = trip.getTitle();
        this.startDateIsoString = trip.getStartDateIsoString();
        this.endDateIsoString = trip.getEndDateIsoString();
        this.destination = trip.getDestination();
        this.todolist = trip.getTodolist();
        this.reservations = trip.getReservations();
    }

    /* Todo */
    public void addTodo(Todo todo) {
        this.todolist.add(todo);
        todo.setTrip(this);
        // return todo;
    }

    public void removeTodo(Todo todo) {
        this.todolist.remove(todo);
        todo.setTrip(null);
    }

    /* Reservation */

    public Reservation addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setTrip(this);
        return reservation;
    }

    public Boolean addReservation(List<Reservation> reservation) {
        Boolean isAdded = this.reservations.addAll(reservation);
        reservation.forEach(r -> r.setTrip(this));
        return isAdded;
    }

    public void removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setTrip(null);
    }
}
