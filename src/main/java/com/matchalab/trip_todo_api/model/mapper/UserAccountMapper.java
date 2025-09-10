package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.matchalab.trip_todo_api.model.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { TripMapper.class })
public abstract class UserAccountMapper {

    public abstract UserAccountDTO mapToUserAccountDTO(UserAccount userAccount);
    // {
    // return new UserAccountDTO(userAccount.getId(), userAccount.getNickname(),
    // userAccount.getTrip().stream().map(trip -> trip.getId()).toList());
    // };

    // public UserAccountDTO mapToUserAccountDTO(UserAccount userAccount) {
    // return new UserAccountDTO(userAccount.getId(), userAccount.getNickname(),
    // userAccount.getTrip().stream().map(trip -> trip.getId()).toList());
    // };

}