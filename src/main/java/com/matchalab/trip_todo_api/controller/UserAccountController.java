package com.matchalab.trip_todo_api.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.trip_todo_api.DTO.TripDTO;
import com.matchalab.trip_todo_api.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.UserAccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user/{userId}")
public class UserAccountController {
    @Autowired
    private final UserAccountMapper userAccountMapper;
    @Autowired
    private final UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccountService userAccountService;

    // @Autowired
    // private final TripService tripService;

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping("")
    public ResponseEntity<UserAccountDTO> userAccount(@PathVariable UUID userId) {
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
     * Provide the details of a Trip with the given id.
     */
    @GetMapping("/activeTrip")
    public ResponseEntity<TripDTO> activeTrip(@PathVariable UUID userId) {
        try {

            return ResponseEntity.ok().body(userAccountService.getActiveTrip(userId));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @PostMapping("/activeTrip/{tripId}")
    public ResponseEntity<TripDTO> setActiveTrip(@PathVariable UUID userId, @PathVariable UUID tripId) {
        try {
            return ResponseEntity.ok().body(userAccountService.setActiveTrip(userId, tripId));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping("/tripSummary")
    public ResponseEntity<UserAccountDTO> tripSummary(@PathVariable UUID userId) {
        try {
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(userId));

            return ResponseEntity.ok().body(UserAccountDTO.builder()
                    .tripSummary(userAccountMapper.mapToUserAccountDTO(userAccount).tripSummary()).build());
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
