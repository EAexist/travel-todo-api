package com.matchalab.trip_todo_api.model.Flight;

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
    // @GeneratedValue(strategy = GenerationType.UUID)
    // String id;

    String name;
    String title;
    String iso2DigitNationCode;

    @Nullable
    String region;

    @Nullable
    String iataCode;
}
