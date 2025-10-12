package com.matchalab.trip_todo_api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
// @AllArgsConstructor
public class UserAccountService {

    @Autowired
    private final UserAccountRepository userAccountRepository;

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final TripMapper tripMapper;

    @Autowired
    private final UserAccountMapper userAccountMapper;

    private final int maxNumberOfTrip;

    public UserAccountService(UserAccountRepository userAccountRepository,
            TripRepository tripRepository,
            TripMapper tripMapper,
            UserAccountMapper userAccountMapper,
            @Value("${app.max-number-of-trip}") int maxNumberOfTrip) {
        this.userAccountRepository = userAccountRepository;
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.userAccountMapper = userAccountMapper;
        this.maxNumberOfTrip = maxNumberOfTrip;
    }

    public UserAccountDTO createTrip(UserAccount userAccount) {
        if (userAccount.getTrip().size() >= maxNumberOfTrip) {
            while (userAccount.getTrip().size() >= maxNumberOfTrip) {
                userAccount.getTrip().removeFirst();

            }
        }
        Trip trip = tripRepository.save(new Trip());
        userAccount.addTrip(trip);
        userAccount.setActiveTripId(trip.getId());
        return userAccountMapper.mapToUserAccountDTO(userAccountRepository.save(userAccount));
    }

    public UserAccountDTO createInitialTripIfEmpty(UserAccount userAccount) {

        if (userAccount.getTrip().isEmpty()) {
            return createTrip(userAccount);
        } else
            return userAccountMapper.mapToUserAccountDTO(userAccount);
    }

    public TripDTO getActiveTrip(String userAccountId) {

        String activeTripId = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new NotFoundException(userAccountId)).getActiveTripId();

        Trip trip = tripRepository.findById(activeTripId).orElseThrow(() -> new NotFoundException(activeTripId));
        return tripMapper.mapToTripDTO(trip);

    }

    public TripDTO setActiveTrip(String userAccountId, String tripId) {
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new NotFoundException(userAccountId));

        log.info(String.format("[UserAccountService.setActiveTrp]\n" + //
                "tripId=%s\nids=%s", tripId,
                userAccount.getTrip().stream().map(t -> t.getId()).toList().toString()));

        if (userAccount.getTrip().stream().anyMatch(t -> t.getId().equals(tripId))) {
            userAccount.setActiveTripId(tripId);
            return tripMapper
                    .mapToTripDTO(tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(tripId)));

        } else {
            throw new NotFoundException(tripId);
        }

    }
}