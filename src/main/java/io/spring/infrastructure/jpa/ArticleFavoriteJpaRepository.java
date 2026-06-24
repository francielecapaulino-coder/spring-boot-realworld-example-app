package io.spring.infrastructure.jpa;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavorite.ArticleFavoriteId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleFavoriteJpaRepository
    extends JpaRepository<ArticleFavorite, ArticleFavoriteId> {

  Optional<ArticleFavorite> findByIdArticleIdAndIdUserId(String articleId, String userId);
}
