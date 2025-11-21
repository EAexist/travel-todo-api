package com.matchalab.travel_todo_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.amadeus.Airport;
import com.matchalab.travel_todo_api.DTO.AirportDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AirportMapper {

    public abstract AirportDTO mapToAirportDTO(Airport airport);

}