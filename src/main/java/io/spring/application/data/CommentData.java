package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.JacksonCustomizations.DateTimeSerializer;
import io.spring.application.DateTimeCursor;
import io.spring.application.Node;
import java.time.Instant;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * Read-only comment view embedding the author profile.
 *
 * <p>Converted to {@code record} under US-06.01 / KR1.5 (mandate J5). The
 * Jackson annotations (date-time serialiser, {@code @JsonProperty("author")},
 * {@code @JsonIgnore} on {@code articleId}) are pinned to the matching record
 * components so the historical JSON envelope is preserved byte-for-byte.
 *
 * <p>{@link #getCursor()} is declared explicitly per ADR-005: records do not
 * auto-implement interface methods, so the cursor must be redeclared here for
 * pagination to keep working.
 */
public record CommentData(
    String id,
    String body,
    @JsonIgnore String articleId,
    @JsonSerialize(using = DateTimeSerializer.class) Instant createdAt,
    @JsonSerialize(using = DateTimeSerializer.class) Instant updatedAt,
    @JsonProperty("author") ProfileData profileData)
    implements Node {

  /** Returns a copy of this comment with the embedded profile replaced. */
  public CommentData withProfileData(ProfileData profileData) {
    return new CommentData(id, body, articleId, createdAt, updatedAt, profileData);
  }

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(createdAt);
  }
}
