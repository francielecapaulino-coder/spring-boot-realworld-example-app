package io.spring.infrastructure.comment;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.JpaCommentRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({JpaCommentRepository.class})
public class JpaCommentRepositoryTest extends DbTestBase {
  @Autowired private CommentRepository commentRepository;

  @Test
  public void should_create_and_fetch_comment_success() {
    Comment comment = new Comment("content", "123", "456");
    commentRepository.save(comment);

    Optional<Comment> optional = commentRepository.findById("456", comment.getId());
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get(), comment);
  }

  /**
   * Kills the {@code VoidMethodCall} mutant on {@code remove()} (line 35): if the
   * delete call is removed, the comment would still be findable after invocation.
   */
  @Test
  public void should_remove_existing_comment_so_it_is_no_longer_findable() {
    Comment comment = new Comment("removable", "user-1", "article-1");
    commentRepository.save(comment);
    Assertions.assertTrue(
        commentRepository.findById("article-1", comment.getId()).isPresent());

    commentRepository.remove(comment);

    Assertions.assertFalse(
        commentRepository.findById("article-1", comment.getId()).isPresent());
  }
}
