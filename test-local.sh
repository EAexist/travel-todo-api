docker compose up -d && ./gradlew clean test \
    --tests TripControllerIntegrationTest.createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_AddFlightRouteAndAirlines
    # --tests NewEntityCreatedEventHandlerServiceTest.processNewDestinationAsync_When_NewFlightRouteAdded_Then_PublishNewFlightRouteCreatedEvent \ \
    # --tests TripControllerIntegrationTest.createDestination_Given_AlreadyExistingDestination_Then_DoNotMakeRedundantDestination \
