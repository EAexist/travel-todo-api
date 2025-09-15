package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightBooking;
import com.matchalab.trip_todo_api.model.Flight.FlightTicket;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.genAI.ExtractAccomodationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightBookingChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.trip_todo_api.repository.AirportRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReservationMapper {

    @Autowired
    protected AirportRepository airportRepository;

    private Airport getAirport(String airportIATACode) {
        return airportRepository.findById(airportIATACode).orElse(new Airport(airportIATACode));
    }

    public Accomodation mapToAccomodation(ExtractAccomodationChatResultDTO dto) {
        return Accomodation.builder()
                .type(dto.accomodationType())
                .title(dto.accomodationTitle())
                .roomTitle(dto.roomTitle())
                .location(dto.location())
                .numberOfGuest(dto.numberOfGuest())
                .clientName(dto.clientName())
                .checkinDateISOString(dto.checkinDateISOString())
                .checkoutDateISOString(dto.checkoutDateISOString())
                .checkinStartTimeISOString(dto.checkinAvailableSinceThisTimeISOString())
                .checkinEndTimeISOString(dto.checkinAvailableUntilThisTimeISOString())
                .checkoutTimeISOString(dto.checkoutDeadlineTimeISOString())
                .build();
    }

    public FlightBooking mapToFlightBooking(ExtractFlightBookingChatResultDTO dto) {
        return FlightBooking.builder()
                // .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIATACode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIATACode()))
                .numberOfPassenger(dto.numberOfPassenger())
                .passengerNames(dto.passengerNames())
                // .departureDateTimeISOString(dto.departureDateTimeISOString())
                .build();
    }

    public FlightTicket mapToFlightTicket(ExtractFlightTicketChatResultDTO dto) {
        return FlightTicket.builder()
                // .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIATACode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIATACode()))
                .passengerName(dto.passengerName())
                // .departureDateTimeISOString(dto.departureDateTimeISOString())
                .build();
    }

    public Reservation mapToReservation(ExtractAccomodationChatResultDTO dto) {
        return Reservation.builder()
                .type(ReservationCategory.Accomodation)
                .accomodation(mapToAccomodation(dto))
                .build();
    }

    public Reservation mapToReservation(ExtractFlightBookingChatResultDTO dto) {
        return Reservation.builder()
                .type(ReservationCategory.Flight)
                .flightBooking(mapToFlightBooking(dto))
                .build();
    }

    public Reservation mapToReservation(ExtractFlightTicketChatResultDTO dto) {
        return Reservation.builder()
                .type(ReservationCategory.FlightTicket)
                .flightTicket(mapToFlightTicket(dto))
                .build();
    }

}