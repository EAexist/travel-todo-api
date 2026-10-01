package com.matchalab.travel_todo_api.model.Todo;

import com.matchalab.travel_todo_api.enums.TodoCategory;
import com.matchalab.travel_todo_api.model.Icon;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Builder(builderMethodName = "todoContentBuilder")
public class TodoContent {

  @Enumerated(EnumType.STRING)
  private TodoCategory category;

  private String title;
  private String subtitle;

  @JdbcTypeCode(SqlTypes.JSON)
  private Icon icon;

  public TodoContent(TodoContent todoContent) {
    this.category = todoContent.getCategory();
    this.title = todoContent.getTitle();
    this.subtitle = todoContent.getSubtitle();
    this.icon = todoContent.getIcon();
  }
}
