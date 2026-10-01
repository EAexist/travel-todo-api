package com.matchalab.travel_todo_api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StreamUtils;

public class TestUtils {

  public static CreateReservationDTO createReservationDTOFromFile(
      String resourcePath, ReservationCategory category) throws Exception {
    ClassPathResource resource = new ClassPathResource(resourcePath);
    String confirmationText =
        StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    return CreateReservationDTO.builder()
        .category(category)
        .confirmationText(confirmationText)
        .build();
  }

  public static <T> T asObject(final ResultActions result, TypeReference<T> classType) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(result.andReturn().getResponse().getContentAsString(), classType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T asObject(final ResultActions result, Class<T> classType) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(result.andReturn().getResponse().getContentAsString(), classType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
