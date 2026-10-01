// TODO: must adjust schema definition based on actual DB schema and requirements
package reservation

import (
	"encoding/json"

	"github.com/google/uuid"
)

type ReservationCategory string

const (
	UNKNOWN        ReservationCategory = "UNKNOWN"
	GENERAL        ReservationCategory = "GENERAL"
	FLIGHT_BOOKING ReservationCategory = "FLIGHT_BOOKING"
	FLIGHT_TICKET  ReservationCategory = "FLIGHT_TICKET"
	ACCOMODATION   ReservationCategory = "ACCOMODATION"
	VISIT_JAPAN    ReservationCategory = "VISIT_JAPAN"
)

type Reservation struct {
	ID              uuid.UUID           `json:"id"`
	TripID          uuid.UUID           `gorm:"column:trip_id;type:uuid"`
	Category        ReservationCategory `json:"category"`
	IsCompleted     bool                `json:"is_completed"`
	RawText         string              `json:"raw_text"`
	PrimaryHrefLink *string             `json:"primary_href_link"`
	Code            *string             `json:"code"`
	Note            string              `json:"note"`
	Detail          json.RawMessage     `db:"detail"`
}

func (Reservation) TableName() string {
	return "reservation"
}

func NewReservation() Reservation {
	return Reservation{
		ID: uuid.New(),
	}
}

type VisitJapan struct {
	ID                uuid.UUID `json:"id"`
	DateTimeIsoString *string   `json:"dateTimeIsoString,omitempty"`
}

type Accomodation struct {
	ID                        uuid.UUID `json:"id"`
	Category                  string    `json:"category"`
	Title                     string    `json:"title"`
	RoomTitle                 string    `json:"roomTitle"`
	Location                  string    `json:"location"`
	NumberOfClient            int       `json:"numberOfClient"`
	ClientName                string    `json:"clientName"`
	CheckinDateIsoString      string    `json:"checkinDateIsoString"`
	CheckoutDateIsoString     string    `json:"checkoutDateIsoString"`
	CheckinStartTimeIsoString string    `json:"checkinStartTimeIsoString"`
	CheckinEndTimeIsoString   string    `json:"checkinEndTimeIsoString"`
	CheckoutTimeIsoString     string    `json:"checkoutTimeIsoString"`
}

type FlightBooking struct {
	ID                         uuid.UUID `json:"id"`
	FlightNumber               string    `json:"flightNumber"`
	DepartureDateTimeIsoString string    `json:"departureDateTimeIsoString"`
	DepartureAirport           *Airport  `json:"departureAirport"`
	ArrivalAirport             *Airport  `json:"arrivalAirport"`
	NumberOfPassenger          *int      `json:"numberOfPassenger,omitempty"`
	PassengerName              string    `json:"passengerName"`
}

type FlightTicket struct {
	ID                         uuid.UUID `json:"id"`
	FlightNumber               string    `json:"flightNumber"`
	DepartureDateTimeIsoString string    `json:"departureDateTimeIsoString"`
	DepartureAirport           *Airport  `json:"departureAirport"`
	ArrivalAirport             *Airport  `json:"arrivalAirport"`
	PassengerName              string    `json:"passengerName"`
}

type GeneralReservation struct {
	ID                uuid.UUID `json:"id"`
	Title             string    `json:"title"`
	NumberOfClient    *int      `json:"numberOfClient,omitempty"`
	ClientName        *string   `json:"clientName,omitempty"`
	DateTimeIsoString *string   `json:"dateTimeIsoString,omitempty"`
}

type Airport struct {
	IataCode            string `json:"iataCode"`
	AirportName         string `json:"airportName"`
	CityName            string `json:"cityName"`
	Iso2DigitNationCode string `json:"iso2DigitNationCode"`
}
