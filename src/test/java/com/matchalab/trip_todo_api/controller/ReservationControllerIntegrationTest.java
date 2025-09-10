package com.matchalab.trip_todo_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "local", "local-init-data" })
// @EnableWebSecurity
public class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // @Test
    // void testExtractTextFromImage() throws Exception {

    // Long id = savedTrip.getId();
    // File _file = new
    // ClassPathResource("/image/accomodation-agoda-app-ios_1.png").getFile();
    // MultipartFile file = new MockMultipartFile(_file.getName(), new
    // FileInputStream(_file));

    // ResultActions result = mockMvc
    // .perform(post(String.format("/user/%s/trip/%s/reservation", userAccountId,
    // id))
    // .contentType(MediaType.APPLICATION_JSON)
    // // .content(Utils.asJsonString(new MultipartFile[] { file
    // // }))
    // )
    // .andDo(print())
    // .andExpect(status().isOk())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    // }

    @Test
    void testExtractTextFromImage() throws Exception {

        Long id = 0L;
        File _file = new ClassPathResource("/image/accomodation-agoda-app-ios_1.png").getFile();
        MultipartFile file = new MockMultipartFile(_file.getName(), new FileInputStream(_file));

        ResultActions result = mockMvc
                .perform(post(String.format("/trip/%s/reservation", id))
                        .contentType(MediaType.APPLICATION_JSON)
                // .content(asJsonString(new MultipartFile[] { file
                // }))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // this.mockMvc
        // .perform(get(TEXT_IMAGE_URL))
        // .andDo(
        // response -> {
        // ModelAndView result = response.getModelAndView();
        // String textFromImage = ((String) result.getModelMap().get("text")).trim();
        // assertThat(textFromImage).isEqualTo("STOP");
        // });
    }

    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T asObject(final ResultActions result, TypeReference<T> classType) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result.andReturn().getResponse().getContentAsString(),
                    classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T asObject(final ResultActions result, Class<T> classType) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result.andReturn().getResponse().getContentAsString(),
                    classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
