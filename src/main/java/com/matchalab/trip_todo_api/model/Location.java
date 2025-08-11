package com.matchalab.trip_todo_api.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Location {

    // @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    // Long id;

    String name;
    String title;
    String countryISO;

    @Nullable
    String region;

    @Nullable
    String iataCode;
}
