// TODO: must adjust schema definition based on actual DB schema and requirements
package reservation

type AccomodationCategory string

const (
	AccHotel AccomodationCategory = "HOTEL"
	Hostel   AccomodationCategory = "HOSTEL"
	Resort   AccomodationCategory = "RESORT"
	OtherAcc AccomodationCategory = "OTHER"
)

type ExtractFlightBookingChatResult struct {
	ReservationDetailHrefLink  *string  `json:"reservationDetailHrefLink"`
	ReservationNumberOrCode    *string  `json:"reservationNumberOrCode"`
	FlightNumber               string   `json:"flightNumber"`
	DepartureAirportIataCode   *string  `json:"departureAirportIataCode"`
	ArrivalAirportIataCode     *string  `json:"arrivalAirportIataCode"`
	NumberOfPassenger          *int     `json:"numberOfPassenger"`
	PassengerNames             []string `json:"passengerNames"`
	DepartureDateTimeIsoString *string  `json:"departureDateTimeIsoString"`
}

type ExtractFlightTicketChatResult struct {
	ReservationDetailHrefLink  *string `json:"reservationDetailHrefLink"`
	ReservationNumberOrCode    *string `json:"reservationNumberOrCode"`
	FlightNumber               string  `json:"flightNumber"`
	DepartureAirportIataCode   *string `json:"departureAirportIataCode"`
	ArrivalAirportIataCode     *string `json:"arrivalAirportIataCode"`
	PassengerName              *string `json:"passengerName"`
	DepartureDateTimeIsoString *string `json:"departureDateTimeIsoString"`
}

type ExtractAccomodationChatResult struct {
	ReservationDetailHrefLink              *string              `json:"reservationDetailHrefLink"`
	ReservationNumberOrCode                *string              `json:"reservationNumberOrCode"`
	AccomodationTitle                      string               `json:"accomodationTitle"`
	RoomTitle                              *string              `json:"roomTitle"`
	NumberOfClient                         *int                 `json:"numberOfClient"`
	ClientName                             *string              `json:"clientName"`
	CheckinDateIsoString                   *string              `json:"checkinDateIsoString"`
	CheckoutDateIsoString                  *string              `json:"checkoutDateIsoString"`
	CheckinAvailableSinceThisTimeIsoString *string              `json:"checkinAvailableSinceThisTimeIsoString"`
	CheckinAvailableUntilThisTimeIsoString *string              `json:"checkinAvailableUntilThisTimeIsoString"`
	CheckoutDeadlineTimeIsoString          *string              `json:"checkoutDeadlineTimeIsoString"`
	Location                               *string              `json:"location"`
	AccomodationCategory                   AccomodationCategory `json:"accomodationCategory"`
}

type ExtractGeneralReservationChatResult struct {
	ReservationDetailHrefLink    *string  `json:"reservationDetailHrefLink"`
	ReservationNumberOrCode      *string  `json:"reservationNumberOrCode"`
	ReservationTitle             string   `json:"reservationTitle"`
	NumberOfClient               *int     `json:"numberOfClient"`
	ClientNames                  []string `json:"clientNames"`
	ReservationDateTimeIsoString *string  `json:"reservationDateTimeIsoString"`
}

type ExtractReservationChatResult struct {
	PartOfTextAndLinksThatContainsReservationInformation *string                               `json:"partOfTextAndLinksThatContainsReservationInformation"`
	FlightBookings                                       []ExtractFlightBookingChatResult      `json:"flightBookings"`
	FlightTickets                                        []ExtractFlightTicketChatResult       `json:"flightTickets"`
	Accomodations                                        []ExtractAccomodationChatResult       `json:"accomodations"`
	OtherReservations                                    []ExtractGeneralReservationChatResult `json:"otherReservations"`
}
