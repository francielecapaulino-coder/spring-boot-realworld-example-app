package io.spring.core.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class CommentTest {

  @Test
  void should_assign_uuid_id_and_keep_constructor_fields() {
    Comment comment = new Comment("body", "user-1", "article-1");
    assertThat(comment.getId()).isNotBlank();
    assertThat(comment.getBody()).isEqualTo("body");
    assertThat(comment.getUserId()).isEqualTo("user-1");
    assertThat(comment.getArticleId()).isEqualTo("article-1");
  }

  @Test
  void should_set_created_at_to_now_on_construction() {
    Instant before = Instant.now();
    Comment comment = new Comment("body", "user-1", "article-1");
    Instant after = Instant.now();
    assertThat(comment.getCreatedAt()).isBetween(before, after);
  }

  @Test
  void should_be_equal_to_self_and_have_consistent_hashcode() {
    Comment comment = new Comment("body", "user-1", "article-1");
    assertThat(comment).isEqualTo(comment);
    assertThat(comment.hashCode()).isEqualTo(comment.hashCode());
  }

  @Test
  void should_not_be_equal_to_another_comment_with_different_id() {
    Comment a = new Comment("body", "user-1", "article-1");
    Comment b = new Comment("body", "user-1", "article-1");
    assertThat(a).isNotEqualTo(b);
  }
}
