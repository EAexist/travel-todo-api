package com.matchalab.trip_todo_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Reservation.FlightBooking;
import com.matchalab.trip_todo_api.model.Reservation.FlightTicket;
import com.matchalab.trip_todo_api.model.Reservation.GeneralReservation;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.genAI.ExtractAccomodationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightBookingChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractGeneralReservationChatResultDTO;
import com.matchalab.trip_todo_api.repository.AirportRepository;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReservationMapper {

    @Autowired
    protected AirportRepository airportRepository;

    private Airport getAirport(String airportIataCode) {
        return airportRepository.findById(airportIataCode).orElse(new Airport(airportIataCode));
    }

    public Accomodation mapToAccomodation(ExtractAccomodationChatResultDTO dto) {
        return Accomodation.builder().type(dto.accomodationType())
                .title(dto.accomodationTitle())
                .roomTitle(dto.roomTitle())
                .location(dto.location())
                .numberOfGuest(dto.numberOfGuest())
                .clientName(dto.clientName())
                .checkinDateIsoString(dto.checkinDateIsoString())
                .checkoutDateIsoString(dto.checkoutDateIsoString())
                .checkinStartTimeIsoString(Utils.timeStringToIsoDateTimeString(
                        dto.checkinAvailableSinceThisTimeIsoString(), dto.checkinDateIsoString()))
                .checkinEndTimeIsoString(Utils.timeStringToIsoDateTimeString(
                        dto.checkinAvailableUntilThisTimeIsoString(), dto.checkinDateIsoString()))
                .checkoutTimeIsoString(Utils.timeStringToIsoDateTimeString(dto.checkoutDeadlineTimeIsoString(),
                        dto.checkoutDateIsoString()))
                .build();
    }

    public FlightBooking mapToFlightBooking(ExtractFlightBookingChatResultDTO dto) {
        return FlightBooking.builder()
                .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIataCode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIataCode()))
                .numberOfPassenger(dto.numberOfPassenger())
                .passengerNames(dto.passengerNames())
                .departureDateTimeIsoString(dto.departureDateTimeIsoString())
                .build();
    }

    public FlightTicket mapToFlightTicket(ExtractFlightTicketChatResultDTO dto) {
        return FlightTicket.builder()
                .flightNumber(dto.flightNumber())
                .departureAirport(getAirport(dto.departureAirportIataCode()))
                .arrivalAirport(getAirport(dto.arrivalAirportIataCode()))
                .passengerName(dto.passengerName())
                .departureDateTimeIsoString(dto.departureDateTimeIsoString())
                .build();
    }

    public GeneralReservation mapToGeneralReservation(ExtractGeneralReservationChatResultDTO dto) {
        return GeneralReservation.builder()
                .title(dto.title())
                .numberOfClient(dto.numberOfClient())
                .clientNames(dto.clientNames())
                .dateTimeIsoString(dto.reservationDateTimeIsoString())
                .build();
    }

    public Reservation mapToReservation(ExtractAccomodationChatResultDTO dto) {
        return Reservation.builder()
                .category(ReservationCategory.ACCOMODATION)
                .primaryHrefLink(dto.reservationDetailHrefLink())
                .code(dto.reservationNumberOrCode())
                .accomodation(mapToAccomodation(dto))
                .build();
    }

    public Reservation mapToReservation(ExtractFlightBookingChatResultDTO dto) {
        return Reservation.builder()
                .category(ReservationCategory.FLIGHT_BOOKING)
                .primaryHrefLink(dto.reservationDetailHrefLink())
                .code(dto.reservationNumberOrCode())
                .flightBooking(mapToFlightBooking(dto))
                .build();
    }

    public Reservation mapToReservation(ExtractFlightTicketChatResultDTO dto) {
        return Reservation.builder()
                .category(ReservationCategory.FLIGHT_TICKET)
                .primaryHrefLink(dto.reservationDetailHrefLink())
                .code(dto.reservationNumberOrCode())
                .flightTicket(mapToFlightTicket(dto))
                .build();
    }

    public Reservation mapToReservation(ExtractGeneralReservationChatResultDTO dto) {
        return Reservation.builder()
                .category(ReservationCategory.GENERAL)
                .primaryHrefLink(dto.reservationDetailHrefLink())
                .code(dto.reservationNumberOrCode())
                .generalReservation(mapToGeneralReservation(dto))
                .build();
    }

}