package io.spring.infrastructure.repository.readservice;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.data.ArticleFavoriteCount;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.JpaArticleFavoriteRepository;
import io.spring.infrastructure.repository.JpaArticleRepository;
import io.spring.infrastructure.repository.JpaUserRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Repository-slice tests for {@link JpaArticleFavoritesReadService} targeting US-07.04.
 *
 * <p>Targets the single NO_COVERAGE mutant on {@code userFavorites} (line 68 —
 * EmptyObjectReturnVals) by exercising the populated branch of the result Set.
 */
@Import({
  JpaArticleFavoritesReadService.class,
  JpaArticleRepository.class,
  JpaArticleFavoriteRepository.class,
  JpaUserRepository.class,
})
class JpaArticleFavoritesReadServiceTest extends DbTestBase {

  @Autowired private JpaArticleFavoritesReadService readService;
  @Autowired private ArticleRepository articleRepository;
  @Autowired private ArticleFavoriteRepository articleFavoriteRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void userFavorites_should_return_favorited_article_ids_for_current_user() {
    User user = new User("u@x.com", "u", "p", "", "");
    User other = new User("o@x.com", "o", "p", "", "");
    userRepository.save(user);
    userRepository.save(other);
    Article favoritedByUser = new Article("a", "d", "b", List.of(), user.getId());
    Article favoritedByOther = new Article("b", "d", "b", List.of(), user.getId());
    Article notFavorited = new Article("c", "d", "b", List.of(), user.getId());
    articleRepository.save(favoritedByUser);
    articleRepository.save(favoritedByOther);
    articleRepository.save(notFavorited);
    articleFavoriteRepository.save(new ArticleFavorite(favoritedByUser.getId(), user.getId()));
    articleFavoriteRepository.save(new ArticleFavorite(favoritedByOther.getId(), other.getId()));

    Set<String> result =
        readService.userFavorites(
            List.of(favoritedByUser.getId(), favoritedByOther.getId(), notFavorited.getId()),
            user);

    assertThat(result).containsExactly(favoritedByUser.getId());
  }

  @Test
  void userFavorites_should_return_empty_set_when_ids_is_null() {
    User user = new User("u@x.com", "u", "p", "", "");
    userRepository.save(user);
    assertThat(readService.userFavorites(null, user)).isEmpty();
  }

  @Test
  void userFavorites_should_return_mutable_empty_set_when_ids_is_null() {
    // Kills EmptyObjectReturnVals on line 68: an immutable Set.of() would throw
    // UnsupportedOperationException on add().
    User user = new User("u@x.com", "u", "p", "", "");
    userRepository.save(user);
    Set<String> result = readService.userFavorites(null, user);
    result.add("any");
    assertThat(result).hasSize(1);
  }

  @Test
  void articlesFavoriteCount_should_return_counts_per_article() {
    User user = new User("u@x.com", "u", "p", "", "");
    User other = new User("o@x.com", "o", "p", "", "");
    userRepository.save(user);
    userRepository.save(other);
    Article a = new Article("a", "d", "b", List.of(), user.getId());
    articleRepository.save(a);
    articleFavoriteRepository.save(new ArticleFavorite(a.getId(), user.getId()));
    articleFavoriteRepository.save(new ArticleFavorite(a.getId(), other.getId()));

    List<ArticleFavoriteCount> counts = readService.articlesFavoriteCount(List.of(a.getId()));

    assertThat(counts).hasSize(1);
    assertThat(counts.get(0).id()).isEqualTo(a.getId());
    assertThat(counts.get(0).count()).isEqualTo(2);
  }
}
