package com.matchalab.travel_todo_api.exception;

public class AiQuotaExceededException extends RuntimeException {
  public AiQuotaExceededException(String message) {
    super(message);
  }

  public AiQuotaExceededException() {
    super();
  }
}
