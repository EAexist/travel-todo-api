package com.matchalab.trip_todo_api.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoContent;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        TripMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TripMapperTest {

    @Autowired
    private CustomTodoContent customTodoContent;

    @Autowired
    private TodoDTO customTodoDTO;

    @Autowired
    private Todo customTodo;

    @Autowired
    private TripDTO tripDTO;

    @Autowired
    private Trip trip;

    @Autowired
    private Accomodation[] accomodations;
    @Autowired
    private Destination[] destinations;
    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    @Autowired
    private TripMapper tripMapper;
    // private final TripMapper tripMapper = Mappers.getMapper(TripMapper.class);

    @MockitoBean
    private PresetTodoContentRepository presetTodoContentRepository;

    @BeforeEach
    public void setUp() throws Exception {
        customTodo.setCustomTodoContent(customTodoContent);

        // Arrays.stream(accomodations).forEach(a -> a.setTrip(trip));
        trip.setAccomodation(Arrays.asList(accomodations));

        // Arrays.stream(destinations).forEach(a -> a.setTrip(trip));
        trip.setDestination(Arrays.asList(destinations));
    }

    @Test
    void mapToTodo_Given_customToDoDTO_When_mapped_Then_correctTodo() {

        Todo mappedTodo = tripMapper.mapToTodo(customTodoDTO);
        assertNotNull(customTodoDTO);
        assertNotNull(mappedTodo);
        assertThat(mappedTodo).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields().isEqualTo(customTodo);

        // TodoContent todoContent = customTodo.getPresetTodoContent() != null ?
        // customTodo.getPresetTodoContent()
        // : customTodo.getCustomTodoContent();
        // TodoContent mappedTodoContent = mappedTodo.getPresetTodoContent() != null ?
        // mappedTodo.getPresetTodoContent()
        // : mappedTodo.getCustomTodoContent();
        // assertThat(todoContent).usingRecursiveComparison().isEqualTo(mappedTodoContent).ignoringFields("todo");
    }

    @Test
    void mapToTodoDTO_Given_customToDo_When_mapped_Then_correctTodoDTO() {
        TodoDTO mappedTodoDTO = tripMapper.mapToTodoDTO(customTodo);
        assertNotNull(customTodo);
        assertNotNull(mappedTodoDTO);
        assertThat(mappedTodoDTO).usingRecursiveComparison().isEqualTo(customTodoDTO);
    }

    @Test
    void mapToTripDTO_Given_tripDTO_When_mapped_Then_correctTrip() {
        Trip mappedTrip = tripMapper.mapToTrip(tripDTO);
        assertThat(trip).usingRecursiveComparison()
                .ignoringFieldsOfTypes(Trip.class).ignoringFields("id")
                .isEqualTo(mappedTrip);
    }

    @Test
    void mapToTripDTO_Given_trip_When_mapped_Then_correctTripDTO() {
        TripDTO mappedTripDTO = tripMapper.mapToTripDTO(trip);
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(String.format("[TripMapperTest] tripDTO=%s\n, trip=%s\\n" + //
                    ", mappedTripDTO=%s ",
                    mapper.writeValueAsString(tripDTO),
                    mapper.writeValueAsString(trip),
                    mapper.writeValueAsString(mappedTripDTO)));
        } catch (Exception e) {
        }
        assertThat(mappedTripDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields()
                .isEqualTo(tripDTO);
    }
}
