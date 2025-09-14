package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.enums.ReservationType;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightBooking;
import com.matchalab.trip_todo_api.model.Flight.FlightTicket;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.genAI.AnalyzeAccomodationReservationDTO;
import com.matchalab.trip_todo_api.model.genAI.AnalyzeFlightBookingReservationDTO;
import com.matchalab.trip_todo_api.model.genAI.AnalyzeFlightTicketReservationDTO;
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

    public Accomodation mapToAccomodation(AnalyzeAccomodationReservationDTO dto) {
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

    public FlightBooking mapToFlightBooking(AnalyzeFlightBookingReservationDTO dto) {
        return FlightBooking.builder()
                // .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIATACode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIATACode()))
                .numberOfPassenger(dto.numberOfPassenger())
                .passengerNames(dto.passengerNames())
                // .departureDateTimeISOString(dto.departureDateTimeISOString())
                .build();
    }

    public FlightTicket mapToFlightTicket(AnalyzeFlightTicketReservationDTO dto) {
        return FlightTicket.builder()
                // .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIATACode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIATACode()))
                .passengerName(dto.passengerName())
                // .departureDateTimeISOString(dto.departureDateTimeISOString())
                .build();
    }

    public Reservation mapToReservation(AnalyzeAccomodationReservationDTO dto) {
        return Reservation.builder()
                .type(ReservationType.Accomodation)
                .accomodation(mapToAccomodation(dto))
                .build();
    }

    public Reservation mapToReservation(AnalyzeFlightBookingReservationDTO dto) {
        return Reservation.builder()
                .type(ReservationType.Flight)
                .flightBooking(mapToFlightBooking(dto))
                .build();
    }

    public Reservation mapToReservation(AnalyzeFlightTicketReservationDTO dto) {
        return Reservation.builder()
                .type(ReservationType.FlightTicket)
                .flightTicket(mapToFlightTicket(dto))
                .build();
    }

}