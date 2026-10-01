package com.matchalab.travel_todo_api.exception;

public class AiServiceUnavailableException extends RuntimeException {
  public AiServiceUnavailableException(String message) {
    super(message);
  }

  public AiServiceUnavailableException() {
    super();
  }
}
