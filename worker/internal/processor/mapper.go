package processor

import (
	"encoding/json"
	"fmt"
	"reservation-worker/internal/reservation"

	"github.com/google/uuid"
)

// MapAIResponseToReservations parses the AI response JSON and maps it to Reservation entities.
func MapAIResponseToReservations(aiResponse string) ([]reservation.Reservation, error) {
	var result reservation.ExtractReservationChatResult
	if err := json.Unmarshal([]byte(aiResponse), &result); err != nil {
		return nil, fmt.Errorf("failed to unmarshal AI response: %w", err)
	}

	var reservations []reservation.Reservation

	// Map Accommodations
	for _, acc := range result.Accomodations {
		detail, _ := json.Marshal(reservation.Accomodation{
			ID:                        uuid.New(),
			Category:                  string(acc.AccomodationCategory),
			Title:                     acc.AccomodationTitle,
			RoomTitle:                 deref(acc.RoomTitle),
			Location:                  deref(acc.Location),
			NumberOfClient:            derefInt(acc.NumberOfClient),
			ClientName:                deref(acc.ClientName),
			CheckinDateIsoString:      deref(acc.CheckinDateIsoString),
			CheckoutDateIsoString:     deref(acc.CheckoutDateIsoString),
			CheckinStartTimeIsoString: deref(acc.CheckinAvailableSinceThisTimeIsoString),
			CheckinEndTimeIsoString:   deref(acc.CheckinAvailableUntilThisTimeIsoString),
			CheckoutTimeIsoString:     deref(acc.CheckoutDeadlineTimeIsoString),
		})

		reservations = append(reservations, reservation.Reservation{
			ID:              uuid.New(),
			Category:        reservation.ACCOMODATION,
			PrimaryHrefLink: acc.ReservationDetailHrefLink,
			Code:            acc.ReservationNumberOrCode,
			Detail:          detail,
		})
	}

	// Map Flight Bookings
	for _, fb := range result.FlightBookings {
		detail, _ := json.Marshal(reservation.FlightBooking{
			ID:                         uuid.New(),
			FlightNumber:               fb.FlightNumber,
			DepartureDateTimeIsoString: deref(fb.DepartureDateTimeIsoString),
			DepartureAirport:           mapAirport(fb.DepartureAirportIataCode),
			ArrivalAirport:             mapAirport(fb.ArrivalAirportIataCode),
			NumberOfPassenger:          fb.NumberOfPassenger,
			PassengerName:              deref(safeIndex(fb.PassengerNames, 0)),
		})

		reservations = append(reservations, reservation.Reservation{
			ID:              uuid.New(),
			Category:        reservation.FLIGHT_BOOKING,
			PrimaryHrefLink: fb.ReservationDetailHrefLink,
			Code:            fb.ReservationNumberOrCode,
			Detail:          detail,
		})
	}

	// Map Flight Tickets
	for _, fb := range result.FlightTickets {
		detail, _ := json.Marshal(reservation.FlightTicket{
			ID:                         uuid.New(),
			FlightNumber:               fb.FlightNumber,
			DepartureDateTimeIsoString: deref(fb.DepartureDateTimeIsoString),
			DepartureAirport:           mapAirport(fb.DepartureAirportIataCode),
			ArrivalAirport:             mapAirport(fb.ArrivalAirportIataCode),
			PassengerName:              deref(fb.PassengerName),
		})

		reservations = append(reservations, reservation.Reservation{
			ID:              uuid.New(),
			Category:        reservation.FLIGHT_TICKET,
			PrimaryHrefLink: fb.ReservationDetailHrefLink,
			Code:            fb.ReservationNumberOrCode,
			Detail:          detail,
		})
	}

	// Map Other Reservations
	for _, res := range result.OtherReservations {
		detail, _ := json.Marshal(reservation.GeneralReservation{
			ID:                uuid.New(),
			Title:             res.ReservationTitle,
			NumberOfClient:    res.NumberOfClient,
			ClientName:        safeIndexPtr(res.ClientNames, 0),
			DateTimeIsoString: res.ReservationDateTimeIsoString,
		})

		reservations = append(reservations, reservation.Reservation{
			ID:              uuid.New(),
			Category:        reservation.GENERAL,
			PrimaryHrefLink: res.ReservationDetailHrefLink,
			Code:            res.ReservationNumberOrCode,
			Detail:          detail,
		})
	}

	return reservations, nil
}

func deref(s *string) string {
	if s != nil {
		return *s
	}
	return ""
}

func derefInt(i *int) int {
	if i != nil {
		return *i
	}
	return 0
}

func mapAirport(iataCode *string) *reservation.Airport {
	if iataCode == nil {
		return nil
	}
	return &reservation.Airport{
		IataCode: *iataCode,
	}
}

func safeIndex(s []string, i int) *string {
	if len(s) > i {
		return &s[i]
	}
	return nil
}

func safeIndexPtr(s []string, i int) *string {
	if len(s) > i {
		return &s[i]
	}
	return nil
}
