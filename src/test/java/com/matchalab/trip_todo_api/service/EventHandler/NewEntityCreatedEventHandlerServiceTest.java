package com.matchalab.trip_todo_api.service.EventHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import com.matchalab.trip_todo_api.DataLoader;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.event.handler.NewEntityCreatedEventHandler;
import com.matchalab.trip_todo_api.factory.AirportFactory;
import com.matchalab.trip_todo_api.factory.DestinationFactory;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.model.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.service.GenAIService;
import com.matchalab.trip_todo_api.utils.Utils;
import com.matchalab.trip_todo_api.model.mapper.FlightRouteMapperImpl;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        FlightRouteMapperImpl.class
})
@ActiveProfiles("local")
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
@RecordApplicationEvents
public class NewEntityCreatedEventHandlerServiceTest {

    // @Autowired
    // private FlightRouteMapper flightRouteMapper;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private FlightRouteRepository flightRouteRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private NewEntityCreatedEventHandler newEntityCreatedEventHandler;

    /*
     * Event
     */
    @Autowired
    private ApplicationEvents applicationEvents;

    private List<Airline> airlines;

    @BeforeAll
    void globalSetup() {
        airlines = List.of(
                new Airline("RF", "에어로K"),
                new Airline("BX", "에어부산"),
                new Airline("YP", "에어프레미아"),
                new Airline("RS", "에어서울"),
                new Airline("OZ", "아시아나항공"),
                new Airline("ZE", "이스타항공"),
                new Airline("7C", "제주항공"),
                new Airline("LJ", "진에어"),
                new Airline("KE", "대한항공"),
                new Airline("TW", "티웨이항공"),
                new Airline("KJ", "에어인천"),
                new Airline("HD", "에어두"),
                new Airline("NH", "전일본공수"),
                new Airline("JH", "후지드림항공"),
                new Airline("JL", "일본항공"),
                new Airline("NU", "저팬트랜스오션에어"),
                new Airline("GK", "제트스타일본"),
                new Airline("MM", "피치항공"),
                new Airline("BC", "스카이마크항공"),
                new Airline("6J", "솔라시드에어"),
                new Airline("7G", "스타플라이어"),
                new Airline("NQ", "에어재팬"),
                new Airline("IJ", "일본춘추 항공"),
                new Airline("ZG", "집에어"),
                new Airline("MZ", "아마쿠사항공"),
                new Airline("EH", "아나윙즈"),
                new Airline("FW", "아이벡스항공"),
                new Airline("JC", "일본항공 커뮤터"),
                new Airline("OC", "오리엔탈에어브리지"),
                new Airline("NU", "류큐에어커뮤터"),
                new Airline("BV", "토키항공"));
    }

    @BeforeEach
    void setup() {
        // GenAIService genAIService = new GenAIService();
        newEntityCreatedEventHandler.setGenAIService(new GenAIService());

        lenient().when(destinationRepository.findById(anyLong()))
                .thenReturn(Optional.of(DestinationFactory.createValidDestination("오사카")));

        lenient().when(destinationRepository.save(any(Destination.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        lenient().when(flightRouteRepository.save(any(FlightRoute.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        lenient().when(flightRouteRepository.findById(anyLong()))
                .thenReturn(Optional.of(new FlightRoute(new Airport("ICN"),
                        new Airport("KIX"))));

        lenient().when(flightRouteRepository.findByDepartureIATACodeAndArrivalIATACode(anyString(), anyString()))
                .thenAnswer(invocation -> {
                    return Optional.of(new FlightRoute(new Airport(invocation.getArgument(0)),
                            new Airport(invocation.getArgument(1))));
                });

        lenient().when(airlineRepository.findById(anyString())).thenAnswer(invocation -> {
            return airlines.stream().filter(al -> al.getIATACode().equals(invocation.getArgument(0))).findAny();
        });
        // when(flightRouteMapper.mapToFlightRoute(any(FlightRouteWithoutAirline.class))).thenAnswer(invocation
        // -> {
        // FlightRouteWithoutAirline frWithoutAirline = invocation.getArgument(0);
        // return new FlightRoute(null, new
        // Airport(frWithoutAirline.departureAirportIATACode()),
        // new Airport(frWithoutAirline.arrivalAirportIATACode()), null);
        // });

        // when(genAIService.getRecommendedFlight(anyString())).thenReturn(
        // RecommendedFlightChatResult.builder()
        // .recommendedOutboundFlight(
        // List.of(FlightRouteWithoutAirline.builder().departureAirportIATACode("ICN")
        // .arrivalAirportIATACode("KIX")
        // .build()))
        // .recommendedReturnFlight(
        // List.of(FlightRouteWithoutAirline.builder().departureAirportIATACode("KIX")
        // .arrivalAirportIATACode("ICN").build()))
        // .build());

    }

    @Test
    public void processNewDestinationAsync_When_NewDestinationCreatedEvent_Then_AddRecommendedFlights()
            throws Exception {

        CompletableFuture<Destination> future = newEntityCreatedEventHandler
                .processNewDestinationAsync(new NewDestinationCreatedEvent(this, 0L));
        Destination destination = future.get();

        log.info(String.format("destination: %s", Utils.asJsonString(destination)));

        assertThat(destination.getRecommendedOutboundFlight().getFirst())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(FlightRoute.class, Airport.class)
                .isEqualTo(List.of(new FlightRoute(AirportFactory.createValidAirport("ICN"),
                        AirportFactory.createValidAirport("KIX"))));

        assertThat(destination.getRecommendedOutboundFlight().stream()
                .filter(f -> f.getDeparture().getIATACode().equals("CJJ")).toList().getFirst())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(FlightRoute.class, Airport.class)
                .isEqualTo(List.of(new FlightRoute(AirportFactory.createValidAirport("CJJ"),
                        AirportFactory.createValidAirport("KIX"))));

        assertThat(destination.getRecommendedReturnFlight().getFirst())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(FlightRoute.class, Airport.class)
                .isEqualTo(List.of(new FlightRoute(AirportFactory.createValidAirport("KIX"),
                        AirportFactory.createValidAirport("ICN"))));
    }

    @Test
    public void processNewFlightRouteAsync_When_NewFlightRouteCreatedEvent_Then_AddRecommendedAirlines()
            throws Exception {

        CompletableFuture<FlightRoute> future = newEntityCreatedEventHandler
                .processNewFlightRouteAsync(new NewFlightRouteCreatedEvent(this, 0L));
        FlightRoute flightRoute = future.get();

        log.info(String.format("FlightRoute: %s", Utils.asJsonString(flightRoute)));
        assertThat(flightRoute.getAirlines().stream().map(al -> al.getName()).toList()).contains(
                "대한항공", "아시아나항공", "일본항공", "에어부산", "진에어", "에어서울", "이스타항공", "피치항공", "제트스타일본");
    }
}