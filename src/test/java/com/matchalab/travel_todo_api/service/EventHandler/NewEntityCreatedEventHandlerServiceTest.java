package com.matchalab.travel_todo_api.service.EventHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import com.matchalab.travel_todo_api.event.handler.FlightRouteEventListener;
import com.matchalab.travel_todo_api.factory.AirportFactory;
import com.matchalab.travel_todo_api.factory.DestinationFactory;
import com.matchalab.travel_todo_api.mapper.FlightRouteMapperImpl;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Flight.Airline;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.repository.AirlineRepository;
import com.matchalab.travel_todo_api.repository.DestinationRepository;
import com.matchalab.travel_todo_api.repository.FlightRouteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {FlightRouteMapperImpl.class})
@TestInstance(Lifecycle.PER_CLASS)
@RecordApplicationEvents
public class NewEntityCreatedEventHandlerServiceTest {

  @Mock private DestinationRepository destinationRepository;

  @Mock private FlightRouteRepository flightRouteRepository;

  @Mock private AirlineRepository airlineRepository;

  @InjectMocks private FlightRouteEventListener newEntityCreatedEventHandler;

  /*
   * Event
   */
  @Autowired private ApplicationEvents applicationEvents;

  private List<Airline> airlines;

  @BeforeAll
  void globalSetup() {
    airlines =
        List.of(
            new Airline("RF", "EOK", "에어로K"),
            new Airline("BX", "ABL", "에어부산"),
            new Airline("YP", "APZ", "에어프레미아"),
            new Airline("RS", "ASV", "에어서울"),
            new Airline("OZ", "AAR", "아시아나항공"),
            new Airline("ZE", "ESR", "이스타항공"),
            new Airline("7C", "JJA", "제주항공"),
            new Airline("LJ", "JNA", "진에어"),
            new Airline("KE", "KAL", "대한항공"),
            new Airline("TW", "TWB", "티웨이항공"),
            new Airline("KJ", "AIH", "에어인천"),
            new Airline("HD", "ADO", "에어두"),
            new Airline("NH", "ANA", "전일본공수"),
            new Airline("JH", "FDA", "후지드림항공"),
            new Airline("JL", "JAL", "일본항공"),
            new Airline("NU", "JTA", "저팬트랜스오션에어"),
            new Airline("GK", "JJP", "제트스타일본"),
            new Airline("MM", "APJ", "피치항공"),
            new Airline("BC", "SKY", "스카이마크항공"),
            new Airline("6J", "SNJ", "솔라시드에어"),
            new Airline("7G", "SFJ", "스타플라이어"),
            new Airline("NQ", "AJX", "에어재팬"),
            new Airline("IJ", "SJO", "일본춘추 항공"),
            new Airline("ZG", "TZP", "집에어"),
            new Airline("MZ", "AHX", "아마쿠사항공"),
            new Airline("EH", "AKX", "아나윙즈"),
            new Airline("FW", "IBX", "아이벡스항공"),
            new Airline("JC", "JAC", "일본항공 커뮤터"),
            new Airline("OC", "ORC", "오리엔탈에어브리지"),
            new Airline("NU", "RAC", "류큐에어커뮤터"),
            new Airline("BV", "TOK", "토키항공"));
  }

  @BeforeEach
  void setup() {

    lenient()
        .when(destinationRepository.findById(any()))
        .thenReturn(Optional.of(DestinationFactory.createValidDestination("오사카")));

    lenient()
        .when(destinationRepository.save(any(Destination.class)))
        .thenAnswer(
            invocation -> {
              return invocation.getArgument(0);
            });

    lenient()
        .when(flightRouteRepository.save(any(FlightRoute.class)))
        .thenAnswer(
            invocation -> {
              return invocation.getArgument(0);
            });

    lenient()
        .when(flightRouteRepository.findById(any()))
        .thenReturn(
            Optional.of(
                new FlightRoute(
                    AirportFactory.createValidAirport("ICN"),
                    AirportFactory.createValidAirport("KIX"))));

    lenient()
        .when(flightRouteRepository.findByDepartureIataCodeAndArrivalIataCode(any(), any()))
        .thenAnswer(
            invocation -> {
              return Optional.of(
                  new FlightRoute(
                      AirportFactory.createValidAirport(invocation.getArgument(0)),
                      AirportFactory.createValidAirport(invocation.getArgument(1))));
            });

    lenient()
        .when(airlineRepository.findById(any()))
        .thenAnswer(
            invocation -> {
              return airlines.stream()
                  .filter(al -> al.getIataCode().equals(invocation.getArgument(0)))
                  .findAny();
            });
  }
}
