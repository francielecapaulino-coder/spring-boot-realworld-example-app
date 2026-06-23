package io.spring.infrastructure.jpa;

import io.spring.core.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleJpaRepository extends JpaRepository<Article, String> {
  Optional<Article> findBySlug(String slug);
}
