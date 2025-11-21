package com.matchalab.travel_todo_api.model.genAI;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;

@Builder
public record ExtractReservationChatResultDTO(
        String partOfTextAndLinksThatContainsReservationInformation,
        List<ExtractFlightBookingChatResultDTO> flightBookings,
        List<ExtractFlightTicketChatResultDTO> flightTickets,
        List<ExtractAccomodationChatResultDTO> accomodations,
        List<ExtractGeneralReservationChatResultDTO> otherReservations) {

    public static class ExtractReservationChatResultDTOBuilder {
        List<ExtractFlightBookingChatResultDTO> flightBookings = new ArrayList<ExtractFlightBookingChatResultDTO>();
        List<ExtractFlightTicketChatResultDTO> flightTickets = new ArrayList<ExtractFlightTicketChatResultDTO>();
        List<ExtractAccomodationChatResultDTO> accomodations = new ArrayList<ExtractAccomodationChatResultDTO>();
        List<ExtractGeneralReservationChatResultDTO> otherReservations = new ArrayList<ExtractGeneralReservationChatResultDTO>();
    }
}