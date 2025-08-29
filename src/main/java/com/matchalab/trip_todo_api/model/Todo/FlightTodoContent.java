package com.matchalab.trip_todo_api.model.Todo;

import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "flightTodoContent_flightRoute", joinColumns = @JoinColumn(name = "flight-todo-content_id"), inverseJoinColumns = @JoinColumn(name = "flight-route_id"))
    @Builder.Default
    private List<FlightRoute> routes = new ArrayList<FlightRoute>();
}