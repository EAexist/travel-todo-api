package com.matchalab.trip_todo_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.matchalab.trip_todo_api.DTO.TodoDTO;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.mapper.TodoMapperImpl;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        TodoMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TodoMapperTest {

    @Autowired
    private TodoDTO stockTodoDTO;

    @Autowired
    private Todo stockTodo;

    @Autowired
    private TodoDTO customTodoDTO;

    @Autowired
    private Todo customTodo;

    @MockitoBean
    private StockTodoContentRepository stockTodoContentRepository;

    @Autowired
    private TodoMapper todoMapper;

    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    // private final TripMapper todoMapper = Mappers.getMapper(TripMapper.class);

    @BeforeAll
    public void setUp() throws Exception {
        when(stockTodoContentRepository.findById(any()))
                .thenReturn(Optional.of(stockTodo.getStockTodoContent()));
    }

    @Test
    void mapToTodo_Given_stockTodoDTO_When_mapped_Then_correctTodo() {

        Todo mappedTodo = todoMapper.mapToTodo(stockTodoDTO);
        assertNotNull(stockTodoDTO);
        assertNotNull(mappedTodo);
        assertThat(mappedTodo).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields().isEqualTo(stockTodo);
    }

    @Test
    void mapToTodoDTO_Given_stockTodo_When_mapped_Then_correctTodoDTO() {
        TodoDTO mappedTodoDTO = todoMapper.mapToTodoDTO(stockTodo);
        assertNotNull(stockTodo);
        assertNotNull(mappedTodoDTO);
        assertThat(mappedTodoDTO).usingRecursiveComparison().isEqualTo(stockTodoDTO);
    }

    @Test
    void mapToTodo_Given_customTodoDTO_When_mapped_Then_correctTodo() {

        Todo mappedTodo = todoMapper.mapToTodo(customTodoDTO);
        assertNotNull(customTodoDTO);
        assertNotNull(mappedTodo);
        assertThat(mappedTodo).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields().isEqualTo(customTodo);
    }

    @Test
    void mapToTodoDTO_Given_customTodo_When_mapped_Then_correctTodoDTO() {
        TodoDTO mappedTodoDTO = todoMapper.mapToTodoDTO(customTodo);
        assertNotNull(customTodo);
        assertNotNull(mappedTodoDTO);
        assertThat(mappedTodoDTO).usingRecursiveComparison().isEqualTo(customTodoDTO);
    }
}
