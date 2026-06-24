package io.spring.infrastructure.jpa;

import io.spring.core.comment.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, String> {
  Optional<Comment> findByArticleIdAndId(String articleId, String id);

  List<Comment> findByArticleId(String articleId);
}
