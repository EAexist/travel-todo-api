docker compose up -d && 
./gradlew clean test -Dspring.profiles.active=local \
# --tests "ReservationControllerIntegrationTest*"
# --tests "com.matchalab.trip_todo_api.controller.*"
# --tests "com.matchalab.trip_todo_api.mapper.*"
# --tests "com.matchalab.trip_todo_api.service.*"
# --tests "TripControllerIntegrationTest.getTodoPreset_Given_PopulatedPresetDB_When_RequestGet_Then_AllPresets"
# --tests "ReservationControllerIntegrationTest"
    # --tests ReservationControllerIntegrationTest.createReservationFromText_Given_FlightBooking_Eastarjet_Gmail_Rtf_Ko_When_RequestPostWithoutCategory_Then_createReservation
    # --tests TodoMapperTest
    # --tests TripControllerIntegrationTest.createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_AddFlightRouteAndAirlines
    # --tests NewEntityCreatedEventHandlerServiceTest.processNewDestinationAsync_When_NewFlightRouteAdded_Then_PublishNewFlightRouteCreatedEvent \ \
    # --tests TripControllerIntegrationTest.createDestination_Given_AlreadyExistingDestination_Then_DoNotMakeRedundantDestination \
