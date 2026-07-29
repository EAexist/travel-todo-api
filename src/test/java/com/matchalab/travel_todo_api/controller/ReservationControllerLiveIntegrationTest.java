package com.matchalab.travel_todo_api.controller;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"ai"})
@WithMockUser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TestConfig.class })
public class ReservationControllerLiveIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TripRepository tripRepository;
    @Autowired private UserAccountRepository userAccountRepository;
    @Autowired private Trip trip;

    private UUID tripId;

    @BeforeAll
    void setUp() {
        tripRepository.deleteAll();
        userAccountRepository.save(new UserAccount());
        Trip savedTrip = tripRepository.save(new Trip(trip));
        tripId = savedTrip.getId();
    }

    @Test
    void givenValidConfirmationText_whenCreateReservationFromTextLive_thenReturnsCreated() throws Exception {
        // Essential happy path scenario using real AI (requires 'live' profile)
        CreateReservationDTO createReservationDTO = TestUtils.createReservationDTOFromFile("text/flightTicket/eastarjet/kakao_text_ko.txt", ReservationCategory.UNKNOWN);
        
        mockMvc.perform(post("/trip/{tripId}/reservation/analysis/text", tripId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(createReservationDTO)))
               .andExpect(status().isCreated());
    }
}
