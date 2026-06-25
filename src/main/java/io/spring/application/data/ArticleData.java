package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.JacksonCustomizations.DateTimeSerializer;
import io.spring.application.DateTimeCursor;
import io.spring.application.Node;
import java.time.Instant;
import java.util.List;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * Read-only article view returned by query services and serialised to REST/GraphQL clients.
 *
 * <p>Converted to {@code record} under US-06.01 / KR1.5 (mandate J5). The
 * Jackson annotations (date-time serialiser, {@code @JsonProperty("author")})
 * are pinned to the matching record components so the JSON envelope is
 * preserved byte-for-byte.
 *
 * <p>Because the previous mutable class was enriched in place by
 * {@code ArticleQueryService} (favorited flag, favorites count, following
 * status on the embedded profile), three withers are provided so callers can
 * obtain enriched copies and reassign their references — typically through
 * {@link java.util.List#replaceAll(java.util.function.UnaryOperator)}.
 *
 * <p>{@link #getCursor()} is declared explicitly per ADR-005: records do not
 * auto-implement interface methods, so the cursor must be redeclared here for
 * pagination to keep working.
 */
public record ArticleData(
    String id,
    String slug,
    String title,
    String description,
    String body,
    boolean favorited,
    int favoritesCount,
    @JsonSerialize(using = DateTimeSerializer.class) Instant createdAt,
    @JsonSerialize(using = DateTimeSerializer.class) Instant updatedAt,
    List<String> tagList,
    @JsonProperty("author") ProfileData profileData)
    implements Node {

  /** Returns a copy of this article with {@code favorited} replaced. */
  public ArticleData withFavorited(boolean favorited) {
    return new ArticleData(
        id, slug, title, description, body, favorited, favoritesCount, createdAt, updatedAt,
        tagList, profileData);
  }

  /** Returns a copy of this article with {@code favoritesCount} replaced. */
  public ArticleData withFavoritesCount(int favoritesCount) {
    return new ArticleData(
        id, slug, title, description, body, favorited, favoritesCount, createdAt, updatedAt,
        tagList, profileData);
  }

  /** Returns a copy of this article with the embedded profile replaced. */
  public ArticleData withProfileData(ProfileData profileData) {
    return new ArticleData(
        id, slug, title, description, body, favorited, favoritesCount, createdAt, updatedAt,
        tagList, profileData);
  }

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(updatedAt);
  }
}
