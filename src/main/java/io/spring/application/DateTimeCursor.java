package io.spring.application;

import io.spring.JacksonCustomizations.DateTimeSerializer;
import java.time.Instant;
import tools.jackson.databind.annotation.JsonSerialize;

public class DateTimeCursor extends PageCursor<Instant> {

  public DateTimeCursor(Instant data) {
    super(data);
  }

  @Override
  @JsonSerialize(using = DateTimeSerializer.class)
  public Instant getData() {
    return super.getData();
  }

  @Override
  public String toString() {
    return String.valueOf(getData().toEpochMilli());
  }

  public static Instant parse(String cursor) {
    if (cursor == null) {
      return null;
    }
    return Instant.ofEpochMilli(Long.parseLong(cursor));
  }
}
