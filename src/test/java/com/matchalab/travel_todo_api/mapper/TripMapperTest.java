package com.matchalab.travel_todo_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.matchalab.travel_todo_api.DTO.TripDTO;
import com.matchalab.travel_todo_api.DTO.TripSummaryDTO;
import com.matchalab.travel_todo_api.config.MapperTestConfig;
import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.travel_todo_api.enums.TodoCategory;
import com.matchalab.travel_todo_api.model.Icon;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.repository.StockTodoContentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import({TestConfig.class, MapperTestConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
public class TripMapperTest {

  @Autowired private Trip tripHydrated;

  @Autowired private TripDTO tripDTO;

  @Autowired private TripSummaryDTO tripSummaryDTO;

  @MockitoBean private StockTodoContentRepository stockTodoContentRepository;

  @Autowired private TripMapper tripMapper;

  /*
   * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
   * https://www.baeldung.com/mapstruct
   */
  // private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

  @BeforeAll
  public void setUp() throws Exception {
    when(stockTodoContentRepository.findById(any()))
        .thenReturn(
            Optional.of(
                new StockTodoContent(
                    TodoCategory.FOREIGN,
                    "환전",
                    null,
                    new Icon("💱"),
                    UUID.randomUUID(),
                    "currency")));
  }

  @Test
  void mapToTripDTO_Given_trip_When_mapped_Then_correctTripDTO() {
    TripDTO mappedTripDTO = tripMapper.mapToTripDTO(tripHydrated);
    assertThat(mappedTripDTO)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes()
        .ignoringFields()
        .isEqualTo(tripDTO);
  }

  @Test
  void mapToTrip_Given_tripDTO_When_mapped_Then_correctTrip() {
    Trip mappedTrip = tripMapper.mapToTrip(tripDTO);
    assertThat(mappedTrip)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(Trip.class)
        .ignoringFields("id")
        .isEqualTo(tripHydrated);
  }

  @Test
  void mapToTripSummaryDTO_Given_trip_When_mapped_Then_correctTripSummaryDTO() {
    TripSummaryDTO mappedTripSummaryDTO = tripMapper.mapToTripSummaryDTO(tripHydrated);

    assertThat(mappedTripSummaryDTO.createDateIsoString()).isNotNull();

    assertThat(mappedTripSummaryDTO)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes()
        .ignoringFields("createDateIsoString")
        .isEqualTo(tripSummaryDTO);
  }
}
