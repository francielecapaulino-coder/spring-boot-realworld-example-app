package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Read-only author/profile view embedded in articles and comments.
 *
 * <p>Converted to {@code record} under US-06.01 / KR1.5 (mandate J5). The
 * previous Lombok class exposed a {@code setFollowing} mutator that
 * {@code ArticleQueryService} / {@code CommentQueryService} used to enrich
 * the DTO after the database read. With an immutable record, callers
 * instead obtain an enriched copy via {@link #withFollowing(boolean)} and
 * reassign their reference (typically through {@code List#replaceAll}).
 *
 * <p>The {@code id} component is marked {@code @JsonIgnore} so the wire
 * representation continues to omit it — matching the historical contract
 * used by REST clients and the GraphQL/DGS layer.
 */
public record ProfileData(
    @JsonIgnore String id, String username, String bio, String image, boolean following) {

  /** Returns a copy of this profile with {@code following} replaced. */
  public ProfileData withFollowing(boolean following) {
    return new ProfileData(id, username, bio, image, following);
  }
}
