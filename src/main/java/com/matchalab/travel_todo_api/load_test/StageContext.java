package com.matchalab.travel_todo_api.load_test;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class StageContext {

    private static final String STAGE_HEADER = "Load-Test-Stage-Id";
    private static final String DEFAULT_STAGE = "none";

    public String getCurrentStageId() {
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String header = request.getHeader(STAGE_HEADER);
        return (header != null && !header.isBlank()) ? header : DEFAULT_STAGE;
    }
}