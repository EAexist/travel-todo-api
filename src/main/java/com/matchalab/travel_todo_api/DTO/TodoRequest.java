package com.matchalab.travel_todo_api.DTO;

import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.Builder;
import org.openapitools.jackson.nullable.JsonNullable;

public interface TodoRequest {
  UUID id();
  int orderKey();
  String note();
  @Nullable JsonNullable<String> completeDateIsoString();
  TodoContentDTO content();
}
