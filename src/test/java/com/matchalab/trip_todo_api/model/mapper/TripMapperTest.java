package com.matchalab.trip_todo_api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.TripSummaryDTO;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
// @ExtendWith(MockitoExtension.class)
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        TripMapperImpl.class,
        TodoMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TripMapperTest {

    @Autowired
    private Trip tripHydrated;

    @Autowired
    private TripDTO tripDTO;

    @Autowired
    private TripSummaryDTO tripSummaryDTO;

    @MockitoBean
    private StockTodoContentRepository stockTodoContentRepository;

    @InjectMocks
    private TripMapperImpl tripMapper;

    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    // private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

    @BeforeAll
    public void setUp() throws Exception {
        when(stockTodoContentRepository.findById(anyString()))
                .thenReturn(Optional.of(new StockTodoContent(0L, true, "foreign",
                        "currency", "환전", new Icon("💱"))));
    }

    @Test
    void mapToTripDTO_Given_trip_When_mapped_Then_correctTripDTO() {
        TripDTO mappedTripDTO = tripMapper.mapToTripDTO(tripHydrated);
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(String.format("[TripMapperTest] tripDTO=%s\n, trip=%s\\n" + //
                    ", mappedTripDTO=%s ",
                    mapper.writeValueAsString(tripDTO),
                    mapper.writeValueAsString(tripHydrated),
                    mapper.writeValueAsString(mappedTripDTO)));
        } catch (Exception e) {
        }
        assertThat(mappedTripDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields()
                .isEqualTo(tripDTO);
    }

    @Test
    void mapToTripSummaryDTO_Given_trip_When_mapped_Then_correctTripSummaryDTO() {
        TripSummaryDTO mappedTripSummaryDTO = tripMapper.mapToTripSummaryDTO(tripHydrated);
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(String.format("[TripMapperTest] tripDTO=%s\n, trip=%s\\n" + //
                    ", mappedTripDTO=%s ",
                    mapper.writeValueAsString(tripSummaryDTO),
                    mapper.writeValueAsString(tripHydrated),
                    mapper.writeValueAsString(mappedTripSummaryDTO)));
        } catch (Exception e) {
        }
        assertThat(mappedTripSummaryDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields()
                .isEqualTo(tripSummaryDTO);
    }

    @Test
    void mapToTrip_Given_tripDTO_When_mapped_Then_correctTrip() {
        Trip mappedTrip = tripMapper.mapToTrip(tripDTO);
        assertThat(tripHydrated).usingRecursiveComparison()
                .ignoringFieldsOfTypes(Trip.class).ignoringFields("id")
                .isEqualTo(mappedTrip);
    }

}
