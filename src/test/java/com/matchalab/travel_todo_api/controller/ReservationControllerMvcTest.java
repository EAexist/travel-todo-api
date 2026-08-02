package com.matchalab.travel_todo_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.service.HtmlParserService;
import com.matchalab.travel_todo_api.service.ReservationService;
import com.matchalab.travel_todo_api.utils.Utils;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReservationController.class)
@Import(HtmlParserService.class)
@WithMockUser
public class ReservationControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @Disabled
    void givenInvalidConfirmationText_whenCreateReservationFromText_thenReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException()).when(reservationService).extractReservationFromText(any(), any());

        mockMvc.perform(post("/trip/{tripId}/reservation/analysis/text", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(CreateReservationDTO.builder()
                        .category(ReservationCategory.UNKNOWN)
                        .confirmationText("invalid-text").build())))
               .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void givenValidConfirmationTextButServiceFails_whenCreateReservationFromText_thenReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error")).when(reservationService).saveReservation(any(), any());

        mockMvc.perform(post("/trip/{tripId}/reservation/analysis/text", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.asJsonString(CreateReservationDTO.builder()
                        .category(ReservationCategory.UNKNOWN)
                        .confirmationText("valid-text").build())))
               .andExpect(status().isInternalServerError());
    }
}
