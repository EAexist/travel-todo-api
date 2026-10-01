from pydantic import BaseModel, Field
from typing import List, Optional
from enum import Enum

# Assuming AccomodationCategory enum exists in Java, 
# defining a placeholder here. Update with actual values if needed.
class AccomodationCategory(str, Enum):
    HOTEL = "HOTEL"
    HOSTEL = "HOSTEL"
    RESORT = "RESORT"
    OTHER = "OTHER"

class ExtractFlightBookingChatResult(BaseModel):
    reservationDetailHrefLink: Optional[str] = None
    reservationNumberOrCode: Optional[str] = None
    flightNumber: str
    departureAirportIataCode: Optional[str] = None
    arrivalAirportIataCode: Optional[str] = None
    numberOfPassenger: Optional[int] = None
    passengerNames: Optional[List[str]] = None
    departureDateTimeIsoString: Optional[str] = None

class ExtractFlightTicketChatResult(BaseModel):
    reservationDetailHrefLink: Optional[str] = None
    reservationNumberOrCode: Optional[str] = None
    flightNumber: str
    departureAirportIataCode: Optional[str] = None
    arrivalAirportIataCode: Optional[str] = None
    passengerName: Optional[str] = None
    departureDateTimeIsoString: Optional[str] = None

class ExtractAccomodationChatResult(BaseModel):
    reservationDetailHrefLink: Optional[str] = None
    reservationNumberOrCode: Optional[str] = None
    accomodationTitle: str
    roomTitle: Optional[str] = None
    numberOfClient: Optional[int] = None
    clientName: Optional[str] = None
    checkinDateIsoString: Optional[str] = None
    checkoutDateIsoString: Optional[str] = None
    checkinAvailableSinceThisTimeIsoString: Optional[str] = None
    checkinAvailableUntilThisTimeIsoString: Optional[str] = None
    checkoutDeadlineTimeIsoString: Optional[str] = None
    location: Optional[str] = None
    accomodationCategory: AccomodationCategory

class ExtractGeneralReservationChatResult(BaseModel):
    reservationDetailHrefLink: Optional[str] = None
    reservationNumberOrCode: Optional[str] = None
    reservationTitle: str
    numberOfClient: Optional[int] = None
    clientNames: Optional[List[str]] = None
    reservationDateTimeIsoString: Optional[str] = None

class ExtractReservationChatResult(BaseModel):
    partOfTextAndLinksThatContainsReservationInformation: Optional[str] = None
    flightBookings: List[ExtractFlightBookingChatResult] = Field(default_factory=list)
    flightTickets: List[ExtractFlightTicketChatResult] = Field(default_factory=list)
    accomodations: List[ExtractAccomodationChatResult] = Field(default_factory=list)
    otherReservations: List[ExtractGeneralReservationChatResult] = Field(default_factory=list)
