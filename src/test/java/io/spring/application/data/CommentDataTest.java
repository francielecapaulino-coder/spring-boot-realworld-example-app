package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.DateTimeCursor;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CommentData} targeting US-07.03. Covers the wither
 * (replaces embedded profile preserving every other field) and the explicit
 * {@code getCursor()} declared per ADR-005.
 */
class CommentDataTest {

  @Test
  void withProfileData_should_replace_profile_keeping_other_fields() {
    ProfileData originalProfile = new ProfileData("p1", "alice", "bio", "img", false);
    ProfileData replacement = new ProfileData("p2", "bob", "new bio", "new img", true);
    Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-01-02T00:00:00Z");
    CommentData original =
        new CommentData("c1", "body", "art-1", createdAt, updatedAt, originalProfile);

    CommentData copy = original.withProfileData(replacement);

    assertThat(copy.id()).isEqualTo("c1");
    assertThat(copy.body()).isEqualTo("body");
    assertThat(copy.articleId()).isEqualTo("art-1");
    assertThat(copy.createdAt()).isEqualTo(createdAt);
    assertThat(copy.updatedAt()).isEqualTo(updatedAt);
    assertThat(copy.profileData()).isSameAs(replacement);
    // original instance untouched
    assertThat(original.profileData()).isSameAs(originalProfile);
  }

  @Test
  void getCursor_should_be_based_on_createdAt() {
    Instant createdAt = Instant.parse("2026-03-04T12:34:56Z");
    Instant updatedAt = Instant.parse("2026-03-05T00:00:00Z");
    ProfileData profile = new ProfileData("p", "u", "b", "i", false);
    CommentData comment = new CommentData("c", "body", "a", createdAt, updatedAt, profile);

    DateTimeCursor cursor = comment.getCursor();

    assertThat(cursor).isNotNull();
    assertThat(cursor.getData()).isEqualTo(createdAt);
  }
}
