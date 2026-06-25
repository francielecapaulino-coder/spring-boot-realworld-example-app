package io.spring.api.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ErrorResourceSerializer extends ValueSerializer<ErrorResource> {
  @Override
  public void serialize(ErrorResource value, JsonGenerator gen, SerializationContext context) {
    Map<String, List<String>> json = new HashMap<>();
    gen.writeStartObject();
    gen.writeObjectPropertyStart("errors");
    for (FieldErrorResource fieldErrorResource : value.fieldErrors()) {
      if (!json.containsKey(fieldErrorResource.field())) {
        json.put(fieldErrorResource.field(), new ArrayList<String>());
      }
      json.get(fieldErrorResource.field()).add(fieldErrorResource.message());
    }
    for (Map.Entry<String, List<String>> pair : json.entrySet()) {
      gen.writeArrayPropertyStart(pair.getKey());
      pair.getValue().forEach(gen::writeString);
      gen.writeEndArray();
    }
    gen.writeEndObject();
    gen.writeEndObject();
  }
}
