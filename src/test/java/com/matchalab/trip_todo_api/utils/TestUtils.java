package com.matchalab.trip_todo_api.utils;

import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static <T> T asObject(final ResultActions result, TypeReference<T> classType) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result.andReturn().getResponse().getContentAsString(),
                    classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T asObject(final ResultActions result, Class<T> classType) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result.andReturn().getResponse().getContentAsString(),
                    classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
