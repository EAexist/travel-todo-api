package com.matchalab.travel_todo_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchalab.travel_todo_api.DTO.TodoCreateDTO;
import com.matchalab.travel_todo_api.DTO.TodoPatchDTO;
import java.util.List;
import java.util.UUID;

import com.matchalab.travel_todo_api.exception.NotFoundException;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Icon;
import com.matchalab.travel_todo_api.utils.Utils;
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

import com.matchalab.travel_todo_api.DTO.TodoContentDTO;
import com.matchalab.travel_todo_api.DTO.TodoDTO;
import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.travel_todo_api.factory.TodoFactory;
import com.matchalab.travel_todo_api.mapper.TodoMapper;
import com.matchalab.travel_todo_api.mapper.TripMapper;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.Todo;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.DestinationRepository;
import com.matchalab.travel_todo_api.repository.StockTodoContentRepository;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.repository.UserAccountRepository;
import com.matchalab.travel_todo_api.utils.TestUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles({ "local" })
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
        destinationRepository.deleteAll();

        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        List<Destination> savedDestinations = destinationRepository.saveAll(List.of(new Destination(destination_kyoto),
                new Destination(destination_osaka)));

        savedTrip = tripRepository.save(new Trip());
        savedTrip.addDestinations(savedDestinations);

        StockTodoContent stockTodoContent_currency = stockTodoContentRepository
                .findByType("CASH")
                .orElseThrow(() -> new NotFoundException(null));
        stockTodoContent_passport = stockTodoContentRepository.findByType("PASSPORT")
                .orElseThrow(() -> new NotFoundException(null));

        savedTrip.addTodo(customTodo);
        savedTrip.addTodo(TodoFactory.createValidStockTodo("currency", stockTodoContent_currency));
        savedTrip = tripRepository.save(savedTrip);
//        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    void givenValidCustomTodoDto_whenCreateTodo_thenReturnsCreated() throws Exception {

        UUID tripId = savedTrip.getId();
        TodoCreateDTO createDto = TodoFactory.createValidCustomTodoCreateDTO();

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdDTO = TestUtils.asObject(result, TodoDTO.class);
        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, createDto.id())));

        assertThat(createdDTO).usingRecursiveComparison()
            .comparingOnlyFields("id", "orderKey", "content")
                .isEqualTo(createDto);
    }

    @Test
    void givenValidStockTodoDtoWithOnlyId_whenCreateTodo_thenReturnsCreated() throws Exception {

        UUID tripId = savedTrip.getId();
        UUID todoId = UUID.nameUUIDFromBytes("todo-stock".getBytes());

        TodoDTO expectedTodoDTO = TodoDTO.builder().id(todoId).orderKey(0)
                .content(todoMapper.mapToTodoContentDTO(stockTodoContent_passport))
                .build();

        TodoCreateDTO createDto = TodoCreateDTO.builder().id(todoId).orderKey(0)
                .content(TodoContentDTO.builder().id(todoMapper.mapToTodoContentDTO(stockTodoContent_passport).getId())
                        .isStock(true).build())
                .build();

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdDTO = TestUtils.asObject(result, TodoDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, todoId)));

        assertThat(createdDTO).usingRecursiveComparison()
            .comparingOnlyFields("id", "orderKey", "content")
                .isEqualTo(expectedTodoDTO);
    }

    @Test
    void givenValidStockTodoDto_whenCreateTodo_thenReturnsCreated() throws Exception {

        UUID tripId = savedTrip.getId();
        UUID todoId = UUID.nameUUIDFromBytes("todo-stock".getBytes());

        TodoCreateDTO createDto = TodoCreateDTO.builder().id(todoId).orderKey(0)
                .content(todoMapper.mapToTodoContentDTO(stockTodoContent_passport))
                .build();

        ResultActions result = mockMvc.perform(post(String.format("/trip/%s/todo", tripId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO createdDTO = TestUtils.asObject(result, TodoDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s/todo/%s", tripId, todoId)));

        assertThat(createdDTO).usingRecursiveComparison()
            .comparingOnlyFields("id", "orderKey", "content")
                .isEqualTo(createDto);
    }

    @Test
    void givenValidCustomTodoPatchDto_whenPatchTodo_thenReturnsOk() throws Exception {

        UUID id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() == null).toList()
                .getFirst();

        TodoPatchDTO patchTodoDTO = TodoPatchDTO.builder().orderKey(4).note("새로운 노트")
                .content(TodoContentDTO.builder().isStock(false).category(null).type("goods")
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
                .comparingOnlyFields("orderKey", "note", "completeDateIsoString", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(patchTodoDTO);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFields("orderKey", "note", "completeDateIsoString", "content.type",
                        "content.title",
                        "content.icon")
                .isEqualTo(todoMapper.mapToTodoDTO(todo));
    }

    @Test
    void givenValidStockTodoPatchDto_whenPatchTodo_thenReturnsOk() throws Exception {

        UUID id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() != null).toList()
                .getFirst();

        TodoPatchDTO patchTodoDTO = TodoPatchDTO.builder().id(todo.getId()).orderKey(4).note("새 노트")
                .content(todoMapper.mapToTodoContentDTO(todo))
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