package com.matchalab.trip_todo_api.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.DTO.FlightRouteDTO;
import com.matchalab.trip_todo_api.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.DTO.TodoDTO;
import com.matchalab.trip_todo_api.DTO.TodoPatchDTO;
import com.matchalab.trip_todo_api.DTO.TodoPresetItemDTO;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { FlightRouteMapper.class })
// @AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class TodoMapper {

    @Autowired
    protected StockTodoContentRepository stockTodoContentRepository;

    @Autowired
    protected FlightRouteMapper flightRouteMapper;

    public TodoMapper(StockTodoContentRepository stockTodoContentRepository) {
        this.stockTodoContentRepository = stockTodoContentRepository;
    }

    protected <T> T unwrapJsonNullable(JsonNullable<T> nullable, @TargetType Class<T> targetType) {
        if (nullable == null || !nullable.isPresent()) {
            return null;
        }
        return nullable.get();
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
    @Mapping(target = "flightRoutes", expression = "java(mapToFlightRoutes(customTodoContent))")
    public abstract TodoContentDTO mapToTodoContentDTO(CustomTodoContent customTodoContent);

    @Mapping(target = "flightRoutes", expression = "java(mapToFlightRoutes(customTodoContent))")
    public List<FlightRouteDTO> mapToFlightRoutes(CustomTodoContent customTodoContent) {
        return customTodoContent.getFlightTodoContent() != null ? customTodoContent.getFlightTodoContent().getRoutes()
                .stream().map(flightRouteMapper::mapToFlightRouteDTO)
                .toList() : null;

    }

    /*
     * mapToEntity
     */

    @Mapping(target = "customTodoContent", expression = "java(mapToCustomTodoContent(todoDTO))")
    @Mapping(target = "stockTodoContent", expression = "java(mapToStockTodoContent(todoDTO))")
    public abstract Todo mapToTodo(TodoPatchDTO todoDTO);

    /*
     * udpateToDto
     */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract TodoDTO updateTodoDTOFromDto(TodoDTO todoDTOSource, @MappingTarget TodoDTO todoDTOTarget);

    @Mapping(target = "customTodoContent", expression = "java(updateCustomTodoContentFromDto(todoDTO, todo.getCustomTodoContent()))")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Todo updateTodoFromDto(TodoPatchDTO todoDTO, @MappingTarget Todo todo);

    @Named("mapToCustomTodoContent")
    public CustomTodoContent mapToCustomTodoContent(TodoPatchDTO todoDTO) {
        return todoDTO.content().getIsStock() ? null : mapToCustomTodoContentHelper(todoDTO.content());
    };

    @Named("mapToStockTodoContent")
    public StockTodoContent mapToStockTodoContent(TodoPatchDTO todoDTO) {
        return todoDTO.content().getIsStock() ? mapToStockTodoContentHelper(todoDTO.content()) : null;
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

    public CustomTodoContent updateCustomTodoContentFromDto(TodoPatchDTO todoDTO,
            CustomTodoContent customTodoContent) {
        return (customTodoContent == null) ? null
                : todoDTO.content() == null ? customTodoContent
                        : updateCustomTodoContentFromDtoHelper(todoDTO.content(), customTodoContent);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract CustomTodoContent updateCustomTodoContentFromDtoHelper(TodoContentDTO todoContentDTO,
            @MappingTarget CustomTodoContent customTodoContent);
    /*
     * mapToTodoPresetItemDTO
     */

    @Mapping(target = "content", expression = "java(mapToTodoContentDTO(todoPresetStockTodoContent.getStockTodoContent()))")
    public abstract TodoPresetItemDTO mapToTodoPresetItemDTO(TodoPresetStockTodoContent todoPresetStockTodoContent);
}
