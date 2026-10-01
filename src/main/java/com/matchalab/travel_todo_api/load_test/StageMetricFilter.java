package com.matchalab.travel_todo_api.load_test;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StageMetricFilter implements ObservationFilter {

    private static final Map<String, String> HEADER_TO_TAG_KEY = Map.of(
            "Load-Test-Stage-Id", "stage_id"
    );

    @Override
    public Observation.Context map(Observation.Context context) {
        if (context instanceof ServerRequestObservationContext serverContext) {
            HEADER_TO_TAG_KEY.forEach((headerName, tagKey) -> {
                String headerValue = serverContext.getCarrier().getHeader(headerName);
                String value = (headerValue != null && !headerValue.isBlank()) ? headerValue : "none";

                context.addLowCardinalityKeyValue(KeyValue.of(tagKey, value));
            });
        }
        return context;
    }
}