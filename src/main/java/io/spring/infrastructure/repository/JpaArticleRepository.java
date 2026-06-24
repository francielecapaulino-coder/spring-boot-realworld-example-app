package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.infrastructure.jpa.ArticleJpaRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
public class JpaArticleRepository implements ArticleRepository {

  private final ArticleJpaRepository articleJpaRepository;
  private final TagReconciler tagReconciler;

  public JpaArticleRepository(
      ArticleJpaRepository articleJpaRepository, TagReconciler tagReconciler) {
    this.articleJpaRepository = articleJpaRepository;
    this.tagReconciler = tagReconciler;
  }

  @Override
  @Transactional
  public void save(Article article) {
    // Reuse persisted Tag instances so the JPA cascade does not insert a duplicate
    // row for an existing name (#90). Tags arrive as transient objects with a
    // freshly generated UUID; reconcileTag swaps each transient instance for the
    // managed one (or inserts it on demand in its own REQUIRES_NEW transaction
    // so a UNIQUE-violation race does not poison this transaction).
    Set<Tag> reconciled = new HashSet<>();
    for (Tag tag : article.getTags()) {
      reconciled.add(tagReconciler.reconcile(tag));
    }
    article.getTags().clear();
    article.getTags().addAll(reconciled);

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
