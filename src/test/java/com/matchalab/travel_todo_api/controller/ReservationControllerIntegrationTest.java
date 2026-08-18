package com.matchalab.travel_todo_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.config.MockReservationConfig;
import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.repository.UserAccountRepository;
import com.matchalab.travel_todo_api.utils.TestUtils;
import com.matchalab.travel_todo_api.utils.Utils;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@WithMockUser
@Import({TestConfig.class, MockReservationConfig.class})
@SpringBootTest
public class ReservationControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private TripRepository tripRepository;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private Trip trip;

  private UUID tripId;

  @BeforeEach
  void setUp() {
    tripRepository.deleteAll();
    userAccountRepository.save(new UserAccount());
    Trip savedTrip = tripRepository.save(new Trip(trip));
    tripId = savedTrip.getId();
  }

  @Test
  void givenInvalidReservationId_whenDeleteReservation_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(delete("/reservation/{reservationId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  private ResultActions postReservationAndTestLocation(String resourcePath) throws Exception {
    CreateReservationDTO createReservationDTO =
        TestUtils.createReservationDTOFromFile(resourcePath, ReservationCategory.UNKNOWN);

    return mockMvc.perform(
        post("/trip/{tripId}/reservation/analysis/text", tripId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(Utils.asJsonString(createReservationDTO)));
  }
}
