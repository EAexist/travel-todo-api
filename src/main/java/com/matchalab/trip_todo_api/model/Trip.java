package com.matchalab.trip_todo_api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trip", indexes = {
        @Index(name = "idx_sample", columnList = "is_sample")
})
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

    // @ManyToMany(cascade = { CascadeType.MERGE })
    // @JoinTable(name = "trip_to_destination", joinColumns = @JoinColumn(name =
    // "trip_id"), inverseJoinColumns = @JoinColumn(name = "destination_id"))
    // @Builder.Default
    // private List<Destination> destinations = new ArrayList<Destination>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TripDestination> destinations = new ArrayList<TripDestination>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Todo> todolist = new ArrayList<Todo>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<Reservation>();

    @ManyToOne(fetch = FetchType.LAZY)
    private TodoPreset todoPreset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccount_id")
    private UserAccount userAccount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "settings_id")
    @Builder.Default
    private TripSettings settings = new TripSettings();

    @Builder.Default
    private Boolean isTodoPresetUpdated = true;

    @Builder.Default
    private Boolean isSample = false;

    // public Trip(Trip sourceTrip) {
    // this();
    // this.title = sourceTrip.getTitle();
    // this.startDateIsoString = sourceTrip.getStartDateIsoString();
    // this.endDateIsoString = sourceTrip.getEndDateIsoString();
    // this.isInitialized = sourceTrip.getIsInitialized();
    // this.destinations = sourceTrip.getDestinations();
    // this.todolist = sourceTrip.getTodolist();
    // this.reservations = sourceTrip.getReservations();
    // this.reservations = sourceTrip.getReservations();
    // }

    /* Deep Copy Constructor. 샘플 Trip을 복사할 때 사용. */
    public Trip(Trip sourceTrip) {
        this();
        this.title = sourceTrip.title;
        this.startDateIsoString = sourceTrip.getStartDateIsoString();
        this.endDateIsoString = sourceTrip.getEndDateIsoString();
        this.isInitialized = sourceTrip.getIsInitialized();

        // One-to-many relationship 설정 (TripDestination, Todo, Reservation)
        // TripDestination
        this.destinations = new ArrayList<>();
        for (TripDestination sourceTripDestination : sourceTrip.getDestinations()) {
            Destination newDestination = new Destination(sourceTripDestination.getDestination());
            TripDestination newTripDestination = new TripDestination(sourceTripDestination, this, newDestination);
            newTripDestination.setTrip(this);
            this.destinations.add(newTripDestination);
        }
        // Todo
        this.todolist = new ArrayList<>();
        for (Todo sourceTodo : sourceTrip.getTodolist()) {
            Todo newTodo = new Todo(sourceTodo);
            newTodo.setTrip(this);
            this.todolist.add(newTodo);
        }
        // Reservation
        this.reservations = new ArrayList<>();
        for (Reservation sourceReservation : sourceTrip.getReservations()) {
            Reservation newReservation = new Reservation(sourceReservation);
            newReservation.setTrip(this);
            this.reservations.add(newReservation);
        }

        // One-to-one relationship 설정 (TripSettings)
        this.settings = new TripSettings(sourceTrip.settings);

        // Many-to-one relationship 설정 (TodoPreset)
        this.todoPreset = sourceTrip.todoPreset;
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

    /* Destination */
    public List<Destination> getDestinationsDirectly() {
        return this.destinations.stream()
                .map(TripDestination::getDestination)
                .collect(Collectors.toList());
    }

    public boolean removeDestination(UUID destinationId) {
        Optional<TripDestination> relationshipOpt = this.destinations.stream()
                .filter(ba -> ba.getDestination().getId().equals(destinationId))
                .findFirst();

        if (relationshipOpt.isPresent()) {
            TripDestination relationshipToRemove = relationshipOpt.get();

            relationshipToRemove.getDestination().getTrips().remove(relationshipToRemove);

            this.destinations.remove(relationshipToRemove);

            return true;
        }

        return false;
    }

    public List<FlightRoute> getOutboundFlights() {
        return this.collectFlightRoutes(
                this.getDestinationsDirectly().stream()
                        .map(dest -> dest.getRecommendedOutboundFlight()));
    }

    public List<FlightRoute> getReturnFlights() {
        return this.collectFlightRoutes(
                this.getDestinationsDirectly().stream()
                        .map(dest -> dest.getRecommendedReturnFlight()));
    }

    private List<FlightRoute> collectFlightRoutes(Stream<List<FlightRoute>> stream) {
        return new ArrayList<>(stream.flatMap(List::stream).collect(Collectors.toSet()));

    }
}
