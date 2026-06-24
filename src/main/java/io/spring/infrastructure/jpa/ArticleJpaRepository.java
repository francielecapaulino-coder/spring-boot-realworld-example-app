package io.spring.infrastructure.jpa;

import io.spring.core.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleJpaRepository extends JpaRepository<Article, String> {

  // Eagerly fetch tags so callers can access Article.tags outside an open session.
  @EntityGraph(attributePaths = "tags")
  Optional<Article> findById(String id);

  @EntityGraph(attributePaths = "tags")
  Optional<Article> findBySlug(String slug);
}
