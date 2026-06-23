package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.jpa.ArticleFavoriteJpaRepository;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
public class JpaArticleFavoriteRepository implements ArticleFavoriteRepository {

  private final ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

  public JpaArticleFavoriteRepository(
      ArticleFavoriteJpaRepository articleFavoriteJpaRepository) {
    this.articleFavoriteJpaRepository = articleFavoriteJpaRepository;
  }

  @Override
  @Transactional
  public void save(ArticleFavorite articleFavorite) {
    if (articleFavoriteJpaRepository
        .findByIdArticleIdAndIdUserId(
            articleFavorite.getArticleId(), articleFavorite.getUserId())
        .isEmpty()) {
      articleFavoriteJpaRepository.save(articleFavorite);
    }
  }

  @Override
  public Optional<ArticleFavorite> find(String articleId, String userId) {
    return articleFavoriteJpaRepository.findByIdArticleIdAndIdUserId(articleId, userId);
  }

  @Override
  @Transactional
  public void remove(ArticleFavorite favorite) {
    articleFavoriteJpaRepository.delete(favorite);
  }
}
