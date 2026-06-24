package io.spring;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

@Configuration
public class JacksonCustomizations {

  /**
   * ISO-8601 date-time formatter that always emits milliseconds and a literal {@code +00:00}
   * offset, e.g. {@code 2024-06-22T13:45:30.000+00:00}. Uses {@link
   * DateTimeFormatterBuilder#appendOffset(String, String)} so that a zero offset is rendered as
   * {@code +00:00} instead of the ISO-8601 short form {@code Z}.
   */
  public static final DateTimeFormatter ISO_DATE_TIME_MILLIS_UTC =
      new DateTimeFormatterBuilder()
          .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
          .appendOffset("+HH:MM", "+00:00")
          .toFormatter()
          .withZone(ZoneOffset.UTC);

  /**
   * Forces the {@link DateTimeSerializer} for {@link Instant} on the auto-configured {@code
   * ObjectMapper}, taking precedence over the default {@code JavaTimeModule} serializer registered
   * by {@code jackson-datatype-jsr310}.
   */
  @Bean
  public JsonMapperBuilderCustomizer instantJacksonCustomizer() {
    return builder -> builder.addModule(new RealWorldModules());
  }

  public static class RealWorldModules extends SimpleModule {

    public RealWorldModules() {
      addSerializer(Instant.class, new DateTimeSerializer());
    }
  }

  public static class DateTimeSerializer extends StdSerializer<Instant> {

    public DateTimeSerializer() {
      super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializationContext provider) {
      if (value == null) {
        gen.writeNull();
      } else {
        gen.writeString(ISO_DATE_TIME_MILLIS_UTC.format(value));
      }
    }
  }
}
