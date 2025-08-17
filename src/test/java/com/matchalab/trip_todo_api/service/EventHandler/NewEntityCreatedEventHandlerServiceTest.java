package com.matchalab.trip_todo_api.service.EventHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.matchalab.trip_todo_api.config.RecommendedFlightTestConfig;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.model.Airport;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.Flight;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.model.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.service.GenAIService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Import({ RecommendedFlightTestConfig.class })
@ActiveProfiles("local")
@Slf4j
public class NewEntityCreatedEventHandlerServiceTest {

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private FlightRouteMapper flightRouteMapper;

    @Mock
    private GenAIService genAIService;

    @InjectMocks
    private NewEntityCreatedEventHandlerService newEntityCreatedEventHandlerService;

    @Autowired
    private String KansaiIntlIATA;

    @Autowired
    private String IncheonIntlIATA;

    @Autowired
    private Airport KansaiIntl;

    @Autowired
    private Airport IncheonIntl;

    @Autowired
    private Destination defaultDestination;

    @Autowired
    private Destination destinationWithRecommendedFlight;

    @BeforeEach
    public void setup() throws IOException {
        when(destinationRepository.findById(anyLong()))
                .thenReturn(Optional.of(defaultDestination));

        when(destinationRepository.save(any(Destination.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        when(flightRouteMapper.mapToFlightRoute(any(Flight.class))).thenAnswer(invocation -> {
            Flight flight = invocation.getArgument(0);
            return new FlightRoute(null, new Airport(flight.departureAirportIATACode()),
                    new Airport(flight.arrivalAirportIATACode()), flight.airlineCompanyIATACode());
        });

        when(genAIService.getRecommendedFlight(anyString())).thenReturn(
                RecommendedFlightChatResult.builder()
                        .recommendedOutboundFlight(
                                List.of(Flight.builder().departureAirportIATACode(IncheonIntlIATA)
                                        .arrivalAirportIATACode(KansaiIntlIATA)
                                        .airlineCompanyIATACode("RS").build()))
                        .recommendedReturnFlight(List.of(Flight.builder().departureAirportIATACode(KansaiIntlIATA)
                                .arrivalAirportIATACode(IncheonIntlIATA).airlineCompanyIATACode("RS").build()))
                        .build());
    }

    @Test
    public void processNewDestinationAsync_When_DestinationCreated_Then_AddRecommendedFlights() throws Exception {

        List<FlightRoute> expectedRecommendedOutboundFlight = List
                .of(new FlightRoute(null, IncheonIntl, KansaiIntl, "RS"));
        List<FlightRoute> expectedRecommendedReturnFlight = List
                .of(new FlightRoute(null, KansaiIntl, IncheonIntl, "RS"));

        CompletableFuture<Destination> future = newEntityCreatedEventHandlerService
                .processNewDestinationAsync(new NewDestinationCreatedEvent(this, 0L));
        Destination destination = future.get();

        log.info(String.format("destination: %s", Utils.asJsonString(destination)));
        assertThat(destination)
                .usingRecursiveComparison()
                .comparingOnlyFields("recommendedOutboundFlight", "recommendedReturnFlight")
                .ignoringFieldsOfTypes()
                .ignoringFields("id")
                .isEqualTo(destinationWithRecommendedFlight);
    }
}