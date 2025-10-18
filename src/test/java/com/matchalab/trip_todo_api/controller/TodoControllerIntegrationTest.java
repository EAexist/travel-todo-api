package com.matchalab.trip_todo_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.factory.TodoFactory;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles({ "local", "local-init-data" })
@EnableWebSecurity
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private StockTodoContentRepository stockTodoContentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private TodoMapper todoMapper;

    /* TestConfig */
    @Autowired
    private Destination destination_kyoto;

    @Autowired
    private Destination destination_osaka;

    @Autowired
    private Todo customTodo;

    /* Local Variables */

    private Trip savedTrip;

    private UUID userAccountId;

    private StockTodoContent stockTodoContent_passport;

    @BeforeAll
    void setUp() {
        // customTodoContentRepository.deleteAll();
        // todoRepository.deleteAll();
        // tripRepository.deleteAll();
        // destinationRepository.deleteAll();

        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        List<Destination> savedDestinations = destinationRepository.saveAll(List.of(new Destination(destination_kyoto),
                new Destination(destination_osaka)));

        savedTrip = new Trip();
        savedTrip.setDestination(savedDestinations);

        StockTodoContent stockTodoContent_currency = stockTodoContentRepository
                .findByTitle("환전")
                .orElseThrow(() -> new NotFoundException(null));
        stockTodoContent_passport = stockTodoContentRepository.findByTitle("여권")
                .orElseThrow(() -> new NotFoundException(null));

        savedTrip.addTodo(customTodo);
        savedTrip.addTodo(TodoFactory.createValidStockTodo("currency", stockTodoContent_currency));
        savedTrip = tripRepository.save(savedTrip);
        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    void createTodo_Given_ValidTripIdAndCustomTodoDTO_When_RequestPost_Then_CreateTodo() throws Exception {

        UUID tripId = savedTrip.getId();
        TodoDTO todoDTO = TodoFactory.createValidCustomTodoDTO("new-reservation");

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(todoDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdTodoDTO = TestUtils.asObject(result, TodoDTO.class);
        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, todoDTO.id())));

        assertThat(createdTodoDTO).usingRecursiveComparison()
                .isEqualTo(todoDTO);
    }

    @Test
    void createTodo_Given_ValidTripIdAndStockTodoDTOOnlyWithId_When_RequestPost_Then_CreateTodo() throws Exception {

        UUID tripId = savedTrip.getId();
        UUID todoDtoId = UUID.nameUUIDFromBytes("todo-stock".getBytes());

        TodoDTO expectedTodoDTO = TodoDTO.builder().id(todoDtoId).orderKey(0)
                .content(todoMapper.mapToTodoContentDTO(stockTodoContent_passport))
                .build();

        TodoDTO todoDTO = TodoDTO.builder().id(todoDtoId).orderKey(0)
                .content(TodoContentDTO.builder().id(todoMapper.mapToTodoContentDTO(stockTodoContent_passport).getId())
                        .isStock(true).build())
                .build();

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(todoDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, todoDtoId)));

        assertThat(createdTodoDTO).usingRecursiveComparison()
                .isEqualTo(expectedTodoDTO);
    }

    @Test
    void createTodo_Given_ValidTripIdAndStockTodoDTO_When_RequestPost_Then_CreateTodo() throws Exception {

        UUID tripId = savedTrip.getId();
        UUID todoDtoId = UUID.nameUUIDFromBytes("todo-stock".getBytes());

        TodoDTO todoDTO = TodoDTO.builder().id(todoDtoId).orderKey(0)
                .content(todoMapper.mapToTodoContentDTO(stockTodoContent_passport))
                .build();

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(todoDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, todoDtoId)));

        assertThat(createdTodoDTO).usingRecursiveComparison()
                .isEqualTo(todoDTO);
    }

    // @TODO
    // @Test
    // void
    // createTodo_Given_ValidTripIdAndFlightTodoDTO_When_RequestPost_Then_CreateTodo()
    // throws Exception {

    // String id = savedTrip.getId();

    // ResultActions result =
    // mockMvc.perform(post(String.format("/trip/%s/todo", userAccountId,
    // id))
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(
    // Utils.asJsonString(TodoDTO.builder().type("flight")
    // .flightRoutes(List.of(new FlightRoute(id, null, null, null),
    // new FlightRoute(id, null, null, null)))
    // .build())))
    // .andDo(print())
    // .andExpect(status().isCreated())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    // .andExpect(jsonPath("id")
    // .isNotEmpty());

    // TodoDTO createdTodoDTO = TestUtils.asObject(result, TodoDTO.class);
    // result.andExpect(header().string("Location",
    // String.format("http://localhost/trip/%s/todo/%s", userAccountId, id,
    // createdTodoDTO.id())));
    // }

    @Test
    // @Transactional
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchCustomTodo_Then_UpdateTodo() throws Exception {

        UUID id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() == null).toList()
                .getFirst();

        TodoDTO patchTodoDTO = TodoDTO.builder().orderKey(4).note("새로운 노트")
                .completeDateIsoString("2025-02-23T00:00:00.001Z")
                .content(TodoContentDTO.builder().isStock(false).category("goods").type("goods")
                        .title("새로운 할 일 이름").icon(
                                new Icon("🎁"))
                        .build())
                .build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/todo/%s", todo.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(patchTodoDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .comparingOnlyFields("orderKey", "note", "completeDateIsoString", "content.category", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(patchTodoDTO);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFields("orderKey", "note", "completeDateIsoString", "content.category", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(todoMapper.mapToTodoDTO(todo));
    }

    @Test
    // @Transactional
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchStockTodo_Then_UpdateTodo() throws Exception {

        UUID id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() != null).toList()
                .getFirst();

        TodoDTO patchTodoDTO = TodoDTO.builder().id(todo.getId()).orderKey(4).note("새 노트")
                .completeDateIsoString("2025-02-23T00:00:00.001Z").content(todoMapper.mapToTodoContentDTO(todo))
                .build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/todo/%s", todo.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(patchTodoDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .comparingOnlyFields("orderKey", "note", "completeDateIsoString", "content.category", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(patchTodoDTO);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFields("orderKey", "note", "completeDateIsoString", "content.category", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(todoMapper.mapToTodoDTO(todo));
    }

    /* @TODO */
    void deleteTodo_When_Then() throws Exception {
    }
}