package com.matchalab.trip_todo_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoContent;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TodoMapper;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.request.CreateTodoRequest;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.transaction.Transactional;
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
    private TodoRepository todoRepository;

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

    @Autowired
    private Accomodation[] accomodations;

    @Autowired
    private Destination destination_kyoto;

    @Autowired
    private Destination destination_osaka;
    @Autowired
    private Todo stockTodo;

    @Autowired
    private Todo customTodo;

    private Trip savedTrip;

    private String userAccountId;

    @BeforeEach
    @Transactional
    void setUp() {
        // StockTodoContentRepository.deleteAll();
        // customTodoContentRepository.deleteAll();
        // todoRepository.deleteAll();
        // tripRepository.deleteAll();
        // destinationRepository.deleteAll();
        // accomodationRepository.deleteAll();
        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        List<Destination> savedDestinations = destinationRepository.saveAll(List.of(new Destination(destination_kyoto),
                new Destination(destination_osaka)));

        savedTrip = new Trip();
        savedTrip.setDestination(savedDestinations);
        savedTrip.setAccomodation(Arrays.stream(accomodations)
                .map(acc -> {
                    Accomodation newAcc = new Accomodation(acc);
                    // newAcc.setTrip(savedTrip);
                    return newAcc;
                })
                .toList());

        // List<Todo> savedTodos = Arrays
        // .stream(new Todo[] { stockTodo, customTodo
        // }).map(todo -> {
        // todo.setTrip(savedTrip);
        // return todo;
        // }).toList();

        Todo stockTodo_ = new Todo(stockTodo);
        stockTodoContentRepository.save(stockTodo_.getStockTodoContent());
        savedTrip.addTodo(stockTodo_);
        savedTrip.addTodo(new Todo(customTodo));
        tripRepository.save(savedTrip);
        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    void createTodo_Given_ValidTripIdAndCustomTodoDTO_When_RequestPost_Then_CreateTodo() throws Exception {

        String id = savedTrip.getId();

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip/%s/todo", userAccountId, id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(new CreateTodoRequest("reservation", null, null))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id")
                        .isNotEmpty());

        TodoDTO createdTodoDTO = TestUtils.asObject(result, TodoDTO.class);
        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/todo/%s", userAccountId, id, createdTodoDTO.id())));
    }

    // @TODO
    // @Test
    // void
    // createTodo_Given_ValidTripIdAndFlightTodoDTO_When_RequestPost_Then_CreateTodo()
    // throws Exception {

    // String id = savedTrip.getId();

    // ResultActions result =
    // mockMvc.perform(post(String.format("/user/%s/trip/%s/todo", userAccountId,
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
    // String.format("http://localhost/user/%s/trip/%s/todo/%s", userAccountId, id,
    // createdTodoDTO.id())));
    // }

    @Test
    void createTodo_Given_ValidTripIdAndPresetTodoDTO_When_RequestPost_Then_CreateTodo() throws Exception {

        String id = savedTrip.getId();
        String stockId = "ID";
        TodoContent StockTodoContent = stockTodoContentRepository.findById(stockId)
                .orElseThrow(() -> new NotFoundException(stockId));

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip/%s/todo", userAccountId, id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(new CreateTodoRequest(null, null, stockId))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id")
                        .isNotEmpty())
                .andExpect(jsonPath("title").value(StockTodoContent.getTitle()))
                .andExpect(jsonPath("orderKey").value(0))
                .andExpect(jsonPath("note").isEmpty());

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        assertThat(StockTodoContent.getIcon()).usingRecursiveComparison()
                .isEqualTo(actualTodoDTO.content().getIcon());

        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/todo/%s", userAccountId, id, actualTodoDTO.id())));
    }

    @Test
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchCustomTodo_Then_UpdateTodo() throws Exception {

        String id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() == null).toList()
                .getFirst();

        TodoDTO todoDTOToPatch = TodoDTO.builder().note("새로운 노트")
                .content(TodoContentDTO.builder().isStock(false).category("goods").type("goods")
                        .title("새로운 할 일 이름").icon(
                                new Icon("🎁"))
                        .build())
                .build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/user/%s/trip/%s/todo/%s", id, userAccountId, todo.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(todoDTOToPatch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes()
                .ignoringFields("completeDateIsoString", "id", "content.id")
                .isEqualTo(todoDTOToPatch);

        assertThat(todoRepository.findById(actualTodoDTO.id()).get().getCustomTodoContent().getId()).isEqualTo(
                todo.getCustomTodoContent().getId());
    }

    @Test
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchPresetTodo_Then_UpdateTodo() throws Exception {

        String id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getStockTodoContent() != null).toList()
                .getFirst();

        TodoDTO todoDTOToPatch = TodoDTO.builder().id("ID").note("새로운 노트")
                .completeDateIsoString("2025-02-20T00:00:00.001Z")
                .content(TodoContentDTO.builder().isStock(false).category("goods").title("새로운 할 일 이름").icon(
                        new Icon("🎁")).build())
                .build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/user/%s/trip/%s/todo/%s", userAccountId, id, todo.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(todoDTOToPatch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("note").value(todoDTOToPatch.note()))
                .andExpect(jsonPath("orderKey").value(todoDTOToPatch.orderKey()))
                .andExpect(jsonPath("completeDateIsoString").value(todoDTOToPatch.completeDateIsoString()));

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);
        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes()
                .ignoringFields("note", "orderKey", "completeDateIsoString")
                .isEqualTo(todoMapper.mapToTodoDTO(todo));

        assertThat(todoRepository.findById(actualTodoDTO.id()).get().getStockTodoContent().getId()).isEqualTo(
                todo.getStockTodoContent().getId());
    }

    /* @TODO */
    void deleteTodo_When_Then() throws Exception {
    }
}