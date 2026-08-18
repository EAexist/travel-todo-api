package com.matchalab.travel_todo_api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewDestinationCreatedEvent extends ApplicationEvent {
  private final UUID destinationId;

  public NewDestinationCreatedEvent(Object source, UUID destinationId) {
    super(source);
    this.destinationId = destinationId;
  }
}
