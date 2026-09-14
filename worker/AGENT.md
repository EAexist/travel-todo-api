You are a professional GO developer.
Develop a Go worker project that does following.
1. Subscribe to AWS SQS for tasks
2. Connect to postgres DB (remote db in production, local db in development)
3. For each message subscribed, 
    (1) we will call moidern recent gemini api
    (2) for a prompt, use a professionally managed prompt file as a prompt template
    (3) the message carries a id to DB entity "Job". read it.
    (4) use the read Job's proprety Job.raw_text (string) as a input to be embedded into a prompt template, using some formating features.
    (5) when prepared, call the gemini api
    (6) we will require a structured output with the api, so it will return a "ResesrvationResponseDto" object.
    (7) when returned, we will convert it to list of "Reservation" entity.
    (8) we will persist the "Reservation" entities and then the task is completed.
4. For a missing detail (e.g. entity schema, property names...), assume or create your own definiton, and leave a simple comment at the start(top) about TODO: must adjust blahbalh~.
5. some determined detail:
- Message { jobId : UUID, created_at: timestamp }
- Job { id: UUID, status: varchar(32), payload: { text: string }, created_at: timestamp }
- Reservation
```java
public class Reservation implements Persistable<UUID> {
  @Enumerated(EnumType.STRING)
  ReservationCategory category;
  @Id @NonNull @Builder.Default private UUID id = UUID.randomUUID();
  @Builder.Default private Boolean isCompleted = false;
  @Lob
  @Basic(fetch = FetchType.LAZY)
  private String rawText;
  @Nullable
  @Column(length = 2048)
  @Size(max = 2048, message = "primaryHrefLink cannot exceed 2048 characters.")
  private String primaryHrefLink;
  @Nullable private String code;
  private String note;
  @Nullable
  private VisitJapan visitJapan;
  @Nullable
  private Accomodation accomodation;
  @Nullable
  private FlightBooking flightBooking;
  @Nullable
  private FlightTicket flightTicket;
  @Nullable
  private GeneralReservation generalReservation;
```
- ResesrvationResponseDto
```python
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

class AccomodationCategory(str, Enum):
    HOTEL = "HOTEL"
    HOSTEL = "HOSTEL"
    RESORT = "RESORT"
    OTHER = "OTHER"
```