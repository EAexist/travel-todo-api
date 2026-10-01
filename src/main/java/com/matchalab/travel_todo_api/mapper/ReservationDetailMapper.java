package com.matchalab.travel_todo_api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Reservation.*;

public class ReservationDetailMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ReservationDetail fromJson(String json, ReservationCategory category) {
        Class<? extends ReservationDetail> targetClass = switch (category) {
            case VISIT_JAPAN -> VisitJapan.class;
            case ACCOMODATION -> Accomodation.class;
            case FLIGHT_BOOKING -> FlightBooking.class;
            case FLIGHT_TICKET -> FlightTicket.class;
            case GENERAL -> GeneralReservation.class;
            default -> GeneralReservation.class;
        };
        try {
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize ReservationDetail JSON", e);
        }
    }

    public static String toJson(ReservationDetail detail) {
        try {
            return OBJECT_MAPPER.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize ReservationDetail to JSON", e);
        }
    }
}