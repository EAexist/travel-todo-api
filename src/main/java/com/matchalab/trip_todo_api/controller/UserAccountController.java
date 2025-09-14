package com.matchalab.trip_todo_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.TripService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserAccountController {
    @Autowired
    private final UserAccountMapper userAccountMapper;
    @Autowired
    private final UserAccountRepository userAccountRepository;

    @Autowired
    private final TripMapper tripMapper;

    // @Autowired
    // private final TripService tripService;

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserAccountDTO> userAccount(@PathVariable String userId) {
        try {
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(userId));

            return ResponseEntity.ok().body(userAccountMapper.mapToUserAccountDTO(userAccount));
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Create New Empty Trip that is owned by UserAccount@userId.
     */
    @PostMapping("/{userId}/trip")
    public ResponseEntity<UserAccountDTO> createTrip(@PathVariable String userId) {
        try {
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(userId));
            Trip trip = new Trip();
            userAccount.getTrip().add(trip);
            userAccount = userAccountRepository.save(userAccount);
            TripDTO tripDTO = tripMapper.mapToTripDTO(userAccount.getTrip().getLast());
            return ResponseEntity.created(Utils.getLocation(tripDTO.id()))
                    .body(userAccountMapper.mapToUserAccountDTO(userAccount));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

}
