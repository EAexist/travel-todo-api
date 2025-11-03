package com.matchalab.trip_todo_api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.DTO.TripDTO;
import com.matchalab.trip_todo_api.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.TripMapper;
import com.matchalab.trip_todo_api.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserAccountService {

    @Autowired
    private final UserAccountRepository userAccountRepository;

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final TripService tripService;

    @Autowired
    private final TripMapper tripMapper;

    @Autowired
    private final UserAccountMapper userAccountMapper;

    public UserAccountDTO createInitialEmptyTrip(UserAccount userAccount) {

        if (userAccount.getTrips().isEmpty()) {
            tripService.createTrip(userAccount.getId());
        }
        return getUserAccountDTO(userAccount);
    }

    public UserAccountDTO createInitialSampleTrip(UserAccount userAccount) {

        if (userAccount.getTrips().isEmpty()) {
            tripService.createSampleTrip(userAccount.getId());
        }
        return getUserAccountDTO(userAccount);
    }

    public TripDTO getActiveTrip(UUID userAccountId) {

        UUID activeTripId = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new NotFoundException(userAccountId)).getActiveTripId();

        Trip trip = tripRepository.findById(activeTripId).orElseThrow(() -> new NotFoundException(activeTripId));
        return tripMapper.mapToTripDTO(trip);

    }

    public TripDTO setActiveTrip(UUID userAccountId, UUID tripId) {
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new NotFoundException(userAccountId));

        log.info(String.format("[UserAccountService.setActiveTrp]\n" + //
                "tripId=%s\nids=%s", tripId,
                userAccount.getTrips().stream().map(t -> t.getId()).toList().toString()));

        if (userAccount.getTrips().stream().anyMatch(t -> t.getId().equals(tripId))) {
            userAccount.setActiveTripId(tripId);
            return tripMapper
                    .mapToTripDTO(tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(tripId)));

        } else {
            throw new NotFoundException(tripId);
        }
    }

    private UserAccountDTO getUserAccountDTO(UserAccount userAccount) {
        return userAccountMapper.mapToUserAccountDTO(userAccountRepository.findById(userAccount.getId())
                .orElseThrow(() -> new NotFoundException(userAccount.getId())));

    }
}