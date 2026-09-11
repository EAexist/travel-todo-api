
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