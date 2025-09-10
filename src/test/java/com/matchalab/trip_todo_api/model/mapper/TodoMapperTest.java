package com.matchalab.trip_todo_api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
// @ExtendWith(MockitoExtension.class)
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        TodoMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TodoMapperTest {

    @Autowired
    private TodoDTO customTodoDTO;

    @Autowired
    private Todo customTodo;

    @MockitoBean
    private StockTodoContentRepository stockTodoContentRepository;

    @InjectMocks
    private TodoMapperImpl todoMapper;

    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    // private final TripMapper todoMapper = Mappers.getMapper(TripMapper.class);

    @BeforeAll
    public void setUp() throws Exception {
        when(stockTodoContentRepository.findById(anyLong()))
                .thenReturn(Optional.of(new StockTodoContent(0L, true, "foreign",
                        "currency", "환전", new Icon("💱"))));
    }

    @Test
    void mapToTodo_Given_customToDoDTO_When_mapped_Then_correctTodo() {

        Todo mappedTodo = todoMapper.mapToTodo(customTodoDTO);
        assertNotNull(customTodoDTO);
        assertNotNull(mappedTodo);
        assertThat(mappedTodo).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields().isEqualTo(customTodo);
    }

    @Test
    void mapToTodoDTO_Given_customToDo_When_mapped_Then_correctTodoDTO() {
        TodoDTO mappedTodoDTO = todoMapper.mapToTodoDTO(customTodo);
        assertNotNull(customTodo);
        assertNotNull(mappedTodoDTO);
        assertThat(mappedTodoDTO).usingRecursiveComparison().isEqualTo(customTodoDTO);
    }
}
