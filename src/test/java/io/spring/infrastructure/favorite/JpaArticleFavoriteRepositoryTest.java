package io.spring.infrastructure.favorite;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.jpa.ArticleFavoriteJpaRepository;
import io.spring.infrastructure.repository.JpaArticleFavoriteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({JpaArticleFavoriteRepository.class})
public class JpaArticleFavoriteRepositoryTest extends DbTestBase {
  @Autowired private ArticleFavoriteRepository articleFavoriteRepository;

  @Autowired private ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

  @Test
  public void should_save_and_fetch_articleFavorite_success() {
    ArticleFavorite articleFavorite = new ArticleFavorite("123", "456");
    articleFavoriteRepository.save(articleFavorite);
    Assertions.assertTrue(
        articleFavoriteJpaRepository
            .findByIdArticleIdAndIdUserId(
                articleFavorite.getArticleId(), articleFavorite.getUserId())
            .isPresent());
  }

  @Test
  public void should_remove_favorite_success() {
    ArticleFavorite articleFavorite = new ArticleFavorite("123", "456");
    articleFavoriteRepository.save(articleFavorite);
    articleFavoriteRepository.remove(articleFavorite);
    Assertions.assertFalse(articleFavoriteRepository.find("123", "456").isPresent());
  }
}
