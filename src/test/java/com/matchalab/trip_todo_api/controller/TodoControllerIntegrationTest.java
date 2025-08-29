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

import com.matchalab.trip_todo_api.config.RecommendedFlightTestConfig;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.PresetTodoContentNotFoundException;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.PresetTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoContent;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.request.CreateTodoRequest;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestConfig.class, RecommendedFlightTestConfig.class })
// @TestPropertySource(properties = { "spring.config.location =
// classpath:application-test.yml" })
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("local")
@EnableWebSecurity
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private PresetTodoContentRepository presetTodoContentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private Accomodation[] accomodations;

    @Autowired
    private Destination[] destinations;

    @Autowired
    private CustomTodoContent customTodoContent;

    @Autowired
    private PresetTodoContent presetTodoContent;

    @Autowired
    private Todo presetTodo;

    @Autowired
    private Todo customTodo;

    private Trip savedTrip;

    private Long userAccountId;

    @BeforeEach
    void setUp() {
        // presetTodoContentRepository.deleteAll();
        // customTodoContentRepository.deleteAll();
        // todoRepository.deleteAll();
        // tripRepository.deleteAll();
        // destinationRepository.deleteAll();
        // accomodationRepository.deleteAll();
        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        savedTrip = new Trip();
        savedTrip.setDestination(Arrays.stream(destinations).toList());
        savedTrip.setAccomodation(Arrays.stream(accomodations)
                .map(acc -> {
                    Accomodation newAcc = new Accomodation(acc);
                    // newAcc.setTrip(savedTrip);
                    return newAcc;
                })
                .toList());

        final record TodoSet<T extends TodoContent>(
                Todo todo,
                T content) {
        }
        ;

        List<Todo> savedTodos = Arrays
                .stream(new TodoSet[] { new TodoSet<PresetTodoContent>(presetTodo, presetTodoContent),
                        new TodoSet<CustomTodoContent>(customTodo, customTodoContent)
                }).map(todoset -> {
                    Todo newTodo = new Todo(todoset.todo);
                    newTodo.setTrip(savedTrip);
                    if (todoset.content instanceof PresetTodoContent) {
                        newTodo.setPresetTodoContent(presetTodoContentRepository
                                .findById(((PresetTodoContent) todoset.content).getId())
                                .orElseThrow(
                                        () -> new NotFoundException(((PresetTodoContent) todoset.content).getId())));
                    } else {
                        newTodo.setCustomTodoContent(new CustomTodoContent((CustomTodoContent) todoset.content));
                    }
                    return newTodo;
                }).toList();
        savedTrip.setTodolist(savedTodos);
        tripRepository.save(savedTrip);
        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    void createTodo_Given_ValidTripIdAndCustomTodoDTO_When_RequestPost_Then_CreateTodo() throws Exception {

        Long id = savedTrip.getId();

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

    // Long id = savedTrip.getId();

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

        Long id = savedTrip.getId();
        Long presetId = 1L;
        PresetTodoContent presetTodoContent = presetTodoContentRepository.findById(presetId)
                .orElseThrow(() -> new PresetTodoContentNotFoundException(presetId));

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip/%s/todo", userAccountId, id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(new CreateTodoRequest(null, null, presetId))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id")
                        .isNotEmpty())
                .andExpect(jsonPath("title").value(presetTodoContent.getTitle()))
                .andExpect(jsonPath("orderKey").value(0))
                .andExpect(jsonPath("note").isEmpty());

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);

        assertThat(presetTodoContent.getIcon()).usingRecursiveComparison()
                .isEqualTo(actualTodoDTO.icon());

        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/todo/%s", userAccountId, id, actualTodoDTO.id())));
    }

    @Test
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchCustomTodo_Then_UpdateTodo() throws Exception {

        Long id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getPresetTodoContent() == null).toList()
                .getFirst();

        TodoDTO todoDTOToPatch = TodoDTO.builder().id(9L).note("새로운 노트").category("goods").title("새로운 할 일 이름")
                .icon(new Icon("🎁")).build();

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
                .ignoringFields("completeDateISOString", "id", "type")
                .isEqualTo(todoDTOToPatch);

        assertThat(todoRepository.findById(actualTodoDTO.id()).get().getCustomTodoContent().getId()).isEqualTo(
                todo.getCustomTodoContent().getId());
    }

    @Test
    void patchTodo_Given_NewContentAndOrderKey_When_RequestPatchPresetTodo_Then_UpdateTodo() throws Exception {

        Long id = savedTrip.getId();

        Todo todo = savedTrip.getTodolist().stream().filter(todo_ -> todo_.getPresetTodoContent() != null).toList()
                .getFirst();

        TodoDTO todoDTOToPatch = TodoDTO.builder().id(9L).note("새로운 노트")
                .completeDateISOString("2025-02-20T00:00:00.001Z").category("goods").title("새로운 할 일 이름")
                .icon(new Icon("🎁")).build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/user/%s/trip/%s/todo/%s", userAccountId, id, todo.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(todoDTOToPatch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("note").value(todoDTOToPatch.note()))
                .andExpect(jsonPath("orderKey").value(todoDTOToPatch.orderKey()))
                .andExpect(jsonPath("completeDateISOString").value(todoDTOToPatch.completeDateISOString()));

        TodoDTO actualTodoDTO = TestUtils.asObject(result, TodoDTO.class);
        assertThat(actualTodoDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes()
                .ignoringFields("note", "orderKey", "completeDateISOString")
                .isEqualTo(tripMapper.mapToTodoDTO(todo));

        assertThat(todoRepository.findById(actualTodoDTO.id()).get().getPresetTodoContent().getId()).isEqualTo(
                todo.getPresetTodoContent().getId());
    }

    /* @TODO */
    void deleteTodo_When_Then() throws Exception {
    }
}