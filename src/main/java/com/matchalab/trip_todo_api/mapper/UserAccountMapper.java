package com.matchalab.trip_todo_api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.DTO.TripSummaryDTO;
import com.matchalab.trip_todo_api.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { TripMapper.class })
public abstract class UserAccountMapper {

    @Autowired // Or @Inject for CDI
    protected TripMapper tripMapper; // MapStruct injects this

    @Named("mapToTripSummary")
    public List<TripSummaryDTO> mapToTripSummary(List<Trip> trip) {
        return trip.stream().map(tripMapper::mapToTripSummaryDTO).toList();
    }

    @Mapping(target = "tripSummary", expression = "java(mapToTripSummary(userAccount.getTrips()))")
    public abstract UserAccountDTO mapToUserAccountDTO(UserAccount userAccount);

}