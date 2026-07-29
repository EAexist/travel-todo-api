package com.matchalab.travel_todo_api.exception.handler;

import com.matchalab.travel_todo_api.exception.AiQuotaExceededException;
import com.matchalab.travel_todo_api.exception.AiServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiQuotaExceededException.class)
    public ResponseEntity<?> handleQuotaExceeded(AiQuotaExceededException e) {
        log.warn("AI quota exceeded");
        return ResponseEntity
                .status(429)
                .body(Map.of(
                        "code", "AI_QUOTA_EXCEEDED",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(AiServiceUnavailableException.class)
    public ResponseEntity<?> handleAiDown(AiServiceUnavailableException e) {
        log.warn("AI Unavailable");
        return ResponseEntity
                .status(503)
                .body(Map.of(
                        "code", "AI_UNAVAILABLE",
                        "message", e.getMessage()
                ));
    }
}
