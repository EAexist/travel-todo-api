package com.matchalab.trip_todo_api.model.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.trip_todo_api.model.Flight.FlightRoute;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightTodoContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "flight-todo-content_flight-route", joinColumns = @JoinColumn(name = "flight-todo-content_id"), inverseJoinColumns = @JoinColumn(name = "flight-route_id"))
    @Builder.Default
    private List<FlightRoute> routes = new ArrayList<FlightRoute>();
}