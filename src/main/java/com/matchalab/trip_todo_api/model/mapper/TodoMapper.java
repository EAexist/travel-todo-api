package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
// @AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class TodoMapper {

    @Autowired
    protected StockTodoContentRepository stockTodoContentRepository;

    public TodoMapper(StockTodoContentRepository stockTodoContentRepository) {
        this.stockTodoContentRepository = stockTodoContentRepository;

    }

    /*
     * mapToTodoDTO
     */

    public TodoDTO mapToTodoDTO(Todo todo) {
        TodoContentDTO todoContentDTO = (todo.getStockTodoContent() != null)
                ? new TodoContentDTO(todo.getStockTodoContent())
                : new TodoContentDTO(todo.getCustomTodoContent());
        return new TodoDTO(todo.getId(), todo.getOrderKey(), todo.getNote(), todo.getCompleteDateISOString(),
                todoContentDTO);
    }

    /*
     * mapToTodo
     */

    @Mapping(target = "customTodoContent", expression = "java(mapToCustomTodoContent(todoDTO.content()))")
    @Mapping(target = "stockTodoContent", expression = "java(mapToStockTodoContent(todoDTO.content()))")
    public abstract Todo mapToTodo(TodoDTO todoDTO);

    @Named("mapToCustomTodoContent")
    public abstract CustomTodoContent mapToCustomTodoContentHelper(TodoContentDTO content);

    @Named("mapToStockTodoContent")
    public abstract StockTodoContent mapToStockTodoContentHelper(TodoContentDTO content);

    @Named("mapToCustomTodoContent")
    public CustomTodoContent mapToCustomTodoContent(TodoContentDTO content) {
        return content.getIsStock() ? null : mapToCustomTodoContentHelper(content);
    };

    @Named("mapToStockTodoContent")
    public StockTodoContent mapToStockTodoContent(TodoContentDTO content) {
        return content.getIsStock() ? mapToStockTodoContentHelper(content) : null;
    };

    @Named("mapToCustomTodoContent")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract CustomTodoContent updateCustomTodoContentFromDto(TodoDTO todoDTO,
            @MappingTarget CustomTodoContent customTodoContent);

    @Named("mapStockTodoContent")
    public StockTodoContent mapStockTodoContent(TodoDTO todoDTO) {
        return todoDTO.content().getIsStock()
                ? stockTodoContentRepository.findById(todoDTO.content().getId())
                        .orElseThrow(() -> new NotFoundException(todoDTO.content().getId()))
                : null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract TodoDTO updateTodoDTOFromDto(TodoDTO todoDTOSource, @MappingTarget TodoDTO todoDTOTarget);

    @Mapping(target = "customTodoContent", expression = "java(updateCustomTodoContentFromDto(todoDTO, todo.getCustomTodoContent()))")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Todo updateTodoFromDto(TodoDTO todoDTO, @MappingTarget Todo todo);
}