package com.matchalab.travel_todo_api.factory;

import java.util.UUID;

import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.Reservation.ReservationDTO;

public class ReservationFactory {
    public static Reservation createValidReservation(String key) {
        switch (key) {
            case "new-reservation":
                return Reservation.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
                        .build();
            default:
                return Reservation.builder().id(UUID.randomUUID())
                        .build();
        }
    }

    public static ReservationDTO createValidReservationDTO(String key) {
        switch (key) {
            case "new-reservation":
                return ReservationDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
                        .build();
            default:
                return ReservationDTO.builder().id(UUID.randomUUID())
                        .build();
        }
    }
}
