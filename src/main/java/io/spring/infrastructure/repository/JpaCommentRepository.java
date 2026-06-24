package io.spring.infrastructure.repository;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.jpa.CommentJpaRepository;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
public class JpaCommentRepository implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;

  public JpaCommentRepository(CommentJpaRepository commentJpaRepository) {
    this.commentJpaRepository = commentJpaRepository;
  }

  @Override
  @Transactional
  public void save(Comment comment) {
    commentJpaRepository.save(comment);
  }

  @Override
  public Optional<Comment> findById(String articleId, String id) {
    return commentJpaRepository.findByArticleIdAndId(articleId, id);
  }

  @Override
  @Transactional
  public void remove(Comment comment) {
    commentJpaRepository.delete(comment);
  }
}
