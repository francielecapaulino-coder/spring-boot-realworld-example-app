package io.spring.core.favorite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ArticleFavoriteTest {

  @Test
  void should_expose_article_id_and_user_id_through_accessors() {
    ArticleFavorite favorite = new ArticleFavorite("article-1", "user-2");
    assertThat(favorite.getArticleId()).isEqualTo("article-1");
    assertThat(favorite.getUserId()).isEqualTo("user-2");
  }

  @Test
  void should_be_equal_when_composite_ids_match() {
    ArticleFavorite a = new ArticleFavorite("article-1", "user-1");
    ArticleFavorite b = new ArticleFavorite("article-1", "user-1");
    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
