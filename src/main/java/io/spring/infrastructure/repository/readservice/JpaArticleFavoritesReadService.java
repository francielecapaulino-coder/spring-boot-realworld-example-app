package io.spring.infrastructure.repository.readservice;

import io.spring.application.data.ArticleFavoriteCount;
import io.spring.core.user.User;
import io.spring.infrastructure.repository.readservice.ArticleFavoritesReadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JpaArticleFavoritesReadService implements ArticleFavoritesReadService {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public boolean isUserFavorite(String userId, String articleId) {
    Number count =
        (Number)
            entityManager
                .createNativeQuery(
                    "select count(1) from article_favorites"
                        + " where user_id = :userId and article_id = :articleId")
                .setParameter("userId", userId)
                .setParameter("articleId", articleId)
                .getSingleResult();
    return count.intValue() > 0;
  }

  @Override
  public int articleFavoriteCount(String articleId) {
    Number count =
        (Number)
            entityManager
                .createNativeQuery(
                    "select count(1) from article_favorites where article_id = :articleId")
                .setParameter("articleId", articleId)
                .getSingleResult();
    return count.intValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ArticleFavoriteCount> articlesFavoriteCount(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    List<Object[]> rows =
        entityManager
            .createNativeQuery(
                "select A.id, count(AF.user_id) as favoriteCount from articles A"
                    + " left join article_favorites AF on A.id = AF.article_id"
                    + " where A.id in :ids group by A.id")
            .setParameter("ids", ids)
            .getResultList();
    return rows.stream()
        .map(row -> new ArticleFavoriteCount((String) row[0], ((Number) row[1]).intValue()))
        .collect(Collectors.toList());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<String> userFavorites(List<String> ids, User currentUser) {
    if (ids == null || ids.isEmpty()) {
      return new HashSet<>();
    }
    List<String> rows =
        entityManager
            .createNativeQuery(
                "select A.id from articles A"
                    + " left join article_favorites AF on A.id = AF.article_id"
                    + " where A.id in :ids and AF.user_id = :userId")
            .setParameter("ids", ids)
            .setParameter("userId", currentUser.getId())
            .getResultList();
    return new HashSet<>(rows);
  }
}
