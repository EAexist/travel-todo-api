package com.matchalab.travel_todo_api.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matchalab.travel_todo_api.enums.TodoCategory;
import com.matchalab.travel_todo_api.model.Icon;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoContentDTO {

  @Nullable private UUID id;

  @Builder.Default private Boolean isStock = false;

  private TodoCategory category;
  private String type;
  private String title;
  private String subtitle;
  private Icon icon;

  @Builder.Default private List<FlightRouteDTO> flightRoutes = new ArrayList<FlightRouteDTO>();
}
