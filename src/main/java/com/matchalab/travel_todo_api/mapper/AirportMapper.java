package com.matchalab.travel_todo_api.mapper;

import com.matchalab.travel_todo_api.DTO.AirportDTO;
import com.matchalab.travel_todo_api.model.Flight.Airport;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AirportMapper {

  public abstract AirportDTO mapToAirportDTO(Airport airport);
}
