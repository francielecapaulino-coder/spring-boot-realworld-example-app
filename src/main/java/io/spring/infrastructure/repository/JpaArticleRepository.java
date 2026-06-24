package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.infrastructure.jpa.ArticleJpaRepository;
import io.spring.infrastructure.jpa.TagJpaRepository;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
public class JpaArticleRepository implements ArticleRepository {

  private final ArticleJpaRepository articleJpaRepository;
  private final TagJpaRepository tagJpaRepository;

  public JpaArticleRepository(
      ArticleJpaRepository articleJpaRepository, TagJpaRepository tagJpaRepository) {
    this.articleJpaRepository = articleJpaRepository;
    this.tagJpaRepository = tagJpaRepository;
  }

  @Override
  @Transactional
  public void save(Article article) {
    // Persist or merge existing tags to avoid duplicates
    article
        .getTags()
        .forEach(
            tag -> {
              Optional<Tag> existing = tagJpaRepository.findByName(tag.getName());
              if (existing.isEmpty()) {
                tagJpaRepository.save(tag);
              }
            });
    articleJpaRepository.save(article);
  }

  @Override
  public Optional<Article> findById(String id) {
    return articleJpaRepository.findById(id);
  }

  @Override
  public Optional<Article> findBySlug(String slug) {
    return articleJpaRepository.findBySlug(slug);
  }

  @Override
  @Transactional
  public void remove(Article article) {
    articleJpaRepository.delete(article);
  }
}
