package com.matchalab.trip_todo_api.mapper;

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
import com.matchalab.trip_todo_api.model.DTO.TodoPresetItemDTO;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.AllArgsConstructor;
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
     * mapToDto
     */

    public TodoDTO mapToTodoDTO(Todo todo) {
        return TodoDTO.builder().id(todo.getId()).orderKey(todo.getOrderKey()).note(todo.getNote())
                .completeDateIsoString(todo.getCompleteDateIsoString())
                .content(mapToTodoContentDTO(todo)).build();
    }

    public TodoContentDTO mapToTodoContentDTO(Todo todo) {
        return (todo.getStockTodoContent() != null)
                ? mapToTodoContentDTO(todo.getStockTodoContent())
                : mapToTodoContentDTO(todo.getCustomTodoContent());
    }

    @Mapping(target = "isStock", expression = "java(true)")
    public abstract TodoContentDTO mapToTodoContentDTO(StockTodoContent stockTodoContent);

    @Mapping(target = "isStock", expression = "java(false)")
    public abstract TodoContentDTO mapToTodoContentDTO(CustomTodoContent customTodoContent);

    /*
     * mapToEntity
     */

    @Mapping(target = "customTodoContent", expression = "java(mapToCustomTodoContent(todoDTO.content()))")
    @Mapping(target = "stockTodoContent", expression = "java(mapToStockTodoContent(todoDTO.content()))")
    public abstract Todo mapToTodo(TodoDTO todoDTO);

    /*
     * udpateToDto
     */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract TodoDTO updateTodoDTOFromDto(TodoDTO todoDTOSource, @MappingTarget TodoDTO todoDTOTarget);

    @Mapping(target = "customTodoContent", expression = "java(updateCustomTodoContentFromDto(todoDTO, todo.getCustomTodoContent()))")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Todo updateTodoFromDto(TodoDTO todoDTO, @MappingTarget Todo todo);

    @Named("mapToCustomTodoContent")
    public CustomTodoContent mapToCustomTodoContent(TodoContentDTO content) {
        return content.getIsStock() ? null : mapToCustomTodoContentHelper(content);
    };

    @Named("mapToStockTodoContent")
    public StockTodoContent mapToStockTodoContent(TodoContentDTO content) {
        return content.getIsStock() ? mapToStockTodoContentHelper(content) : null;
    };

    public abstract CustomTodoContent mapToCustomTodoContentHelper(TodoContentDTO content);

    public StockTodoContent mapToStockTodoContentHelper(TodoContentDTO content) {
        return stockTodoContentRepository.findById(content.getId())
                .orElseThrow(() -> new NotFoundException(content.getId()));
    }

    @Named("mapStockTodoContent")
    public StockTodoContent mapStockTodoContent(TodoDTO todoDTO) {
        return todoDTO.content().getIsStock()
                ? stockTodoContentRepository.findById(todoDTO.content().getId())
                        .orElseThrow(() -> new NotFoundException(todoDTO.content().getId()))
                : null;
    }

    public CustomTodoContent updateCustomTodoContentFromDto(TodoDTO todoDTO,
            CustomTodoContent customTodoContent) {
        return todoDTO.content().getIsStock() ? null
                : updateCustomTodoContentFromDtoHelper(todoDTO.content(), customTodoContent);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract CustomTodoContent updateCustomTodoContentFromDtoHelper(TodoContentDTO todoContentDTO,
            @MappingTarget CustomTodoContent customTodoContent);
    /*
     * mapToTodoPresetItemDTO
     */

    @Mapping(target = "todoContent", expression = "java(mapToTodoContentDTO(todoPresetStockTodoContent.getStockTodoContent()))")
    public abstract TodoPresetItemDTO mapToTodoPresetItemDTO(TodoPresetStockTodoContent todoPresetStockTodoContent);
}
