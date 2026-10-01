package com.matchalab.travel_todo_api.mapper;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Reservation.*;
import com.matchalab.travel_todo_api.model.Flight.Airport;
import com.matchalab.travel_todo_api.model.genAI.ExtractAccomodationChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.ExtractFlightBookingChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.ExtractGeneralReservationChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.ExtractVisitJapanChatResultDTO;
import com.matchalab.travel_todo_api.repository.AirportRepository;
import com.matchalab.travel_todo_api.utils.Utils;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReservationMapper {

  @Autowired protected AirportRepository airportRepository;

  protected <T> T unwrapJsonNullable(JsonNullable<T> nullable, @TargetType Class<T> targetType) {
    if (nullable == null || !nullable.isPresent()) {
      return null;
    }
    return nullable.get();
  }

  protected String unwrapString(Optional<String> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  protected Accomodation unwrapAccomodation(Optional<Accomodation> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  protected FlightBooking unwrapFlightBooking(Optional<FlightBooking> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  protected FlightTicket unwrapFlightTicket(Optional<FlightTicket> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  protected VisitJapan unwrapVisitJapan(Optional<VisitJapan> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  protected GeneralReservation unwrapGeneralReservation(Optional<GeneralReservation> optional) {
    return optional != null && optional.isPresent() ? optional.get() : null;
  }

  public ReservationDTO mapToDTO(Reservation reservation) {
    ReservationDetail detail = reservation.getDetail();

    return ReservationDTO.builder()
            .id(reservation.getId())
            .category(reservation.getCategory())
            .code(reservation.getCode())
            .primaryHrefLink(reservation.getPrimaryHrefLink())
            .isCompleted(reservation.getIsCompleted())
            .accomodation(detail instanceof Accomodation a ? a : null)
            .flightBooking(detail instanceof FlightBooking fb ? fb : null)
            .flightTicket(detail instanceof FlightTicket ft ? ft : null)
            .generalReservation(detail instanceof GeneralReservation gr ? gr : null)
            .visitJapan(detail instanceof VisitJapan vj ? vj : null)
            .build();
  }


  public abstract Reservation mapToReservation(ReservationPatchDTO reservationDTO);

  @AfterMapping
  protected void linkDetailAndCategory(ReservationPatchDTO dto, @MappingTarget Reservation reservation) {
    if (dto.getAccomodation() != null) {
      reservation.setDetail(dto.getAccomodation().get());
      reservation.setCategory(ReservationCategory.ACCOMODATION);
    } else if (dto.getFlightBooking() != null) {
      reservation.setDetail(dto.getFlightBooking().get());
      reservation.setCategory(ReservationCategory.FLIGHT_BOOKING);
    } else if (dto.getFlightTicket() != null) {
      reservation.setDetail(dto.getFlightTicket().get());
      reservation.setCategory(ReservationCategory.FLIGHT_TICKET);
    } else if (dto.getGeneralReservation() != null) {
      reservation.setDetail(dto.getGeneralReservation().get());
      reservation.setCategory(ReservationCategory.GENERAL);
    } else if (dto.getVisitJapan() != null) {
      reservation.setDetail(dto.getVisitJapan().get());
      reservation.setCategory(ReservationCategory.VISIT_JAPAN);
    }
  }

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract Reservation updateFromDto(
      ReservationPatchDTO reservationDTO, @MappingTarget Reservation reservation);

  public Accomodation mapToAccomodation(ExtractAccomodationChatResultDTO dto) {
    return Accomodation.builder()
        .category(dto.accomodationCategory())
        .title(dto.accomodationTitle())
        .roomTitle(dto.roomTitle())
        .location(dto.location())
        .numberOfClient(dto.numberOfClient())
        .clientName(dto.clientName())
        .checkinDateIsoString(dto.checkinDateIsoString())
        .checkoutDateIsoString(dto.checkoutDateIsoString())
        .checkinStartTimeIsoString(
            Utils.timeStringToIsoDateTimeString(
                dto.checkinAvailableSinceThisTimeIsoString(), dto.checkinDateIsoString()))
        .checkinEndTimeIsoString(
            Utils.timeStringToIsoDateTimeString(
                dto.checkinAvailableUntilThisTimeIsoString(), dto.checkinDateIsoString()))
        .checkoutTimeIsoString(
            Utils.timeStringToIsoDateTimeString(
                dto.checkoutDeadlineTimeIsoString(), dto.checkoutDateIsoString()))
        .build();
  }

  public FlightBooking mapToFlightBooking(ExtractFlightBookingChatResultDTO dto) {
    return FlightBooking.builder()
        .flightNumber(dto.flightNumber())
        .departureAirport(getAirport(dto.departureAirportIataCode()))
        .arrivalAirport(getAirport(dto.arrivalAirportIataCode()))
        .numberOfPassenger(dto.numberOfPassenger())
        .passengerName(dto.passengerNames().length > 0 ? dto.passengerNames()[0] : null)
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

  public VisitJapan mapToVisitJapan(ExtractVisitJapanChatResultDTO dto) {
    return VisitJapan.builder().dateTimeIsoString(dto.reservationDateTimeIsoString()).build();
  }

  public GeneralReservation mapToGeneralReservation(ExtractGeneralReservationChatResultDTO dto) {
    return GeneralReservation.builder()
        .title(dto.reservationTitle())
        .numberOfClient(dto.numberOfClient())
        .clientName(
            (dto.clientNames() != null) && dto.clientNames().size() > 0
                ? dto.clientNames().getFirst()
                : null)
        .dateTimeIsoString(dto.reservationDateTimeIsoString())
        .build();
  }

  public Reservation mapToReservation(ExtractAccomodationChatResultDTO dto) {
    return Reservation.builder()
        .category(ReservationCategory.ACCOMODATION)
        .primaryHrefLink(dto.reservationDetailHrefLink())
        .code(dto.reservationNumberOrCode())
        .detail(mapToAccomodation(dto))
        .build();
  }

  public Reservation mapToReservation(ExtractFlightBookingChatResultDTO dto) {
    return Reservation.builder()
        .category(ReservationCategory.FLIGHT_BOOKING)
        .primaryHrefLink(dto.reservationDetailHrefLink())
        .code(dto.reservationNumberOrCode())
        .detail(mapToFlightBooking(dto))
        .build();
  }

  public Reservation mapToReservation(ExtractFlightTicketChatResultDTO dto) {
    return Reservation.builder()
        .category(ReservationCategory.FLIGHT_TICKET)
        .primaryHrefLink(dto.reservationDetailHrefLink())
        .code(dto.reservationNumberOrCode())
        .detail(mapToFlightTicket(dto))
        .build();
  }

  public Reservation mapToReservation(ExtractGeneralReservationChatResultDTO dto) {
    return Reservation.builder()
        .category(ReservationCategory.GENERAL)
        .primaryHrefLink(dto.reservationDetailHrefLink())
        .code(dto.reservationNumberOrCode())
        .detail(mapToGeneralReservation(dto))
        .build();
  }

  private Airport getAirport(String airportIataCode) {
    return airportRepository.findById(airportIataCode).orElse(new Airport(airportIataCode));
  }
}
