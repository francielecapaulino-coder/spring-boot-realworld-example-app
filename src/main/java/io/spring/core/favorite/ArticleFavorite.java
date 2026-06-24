package io.spring.core.favorite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_favorites")
@NoArgsConstructor
@Getter
// Equality is delegated to the composite key. The fields "articleId" / "userId"
// referenced previously do not exist on this class - they live inside the
// embedded id - which produced two Lombok "This field does not exist" warnings.
// The inner ArticleFavoriteId already declares @EqualsAndHashCode over its own
// fields, so comparing by id preserves the (articleId, userId) semantics.
@EqualsAndHashCode(of = {"id"})
public class ArticleFavorite {

  @EmbeddedId private ArticleFavoriteId id;

  public ArticleFavorite(String articleId, String userId) {
    this.id = new ArticleFavoriteId(articleId, userId);
  }

  public String getArticleId() {
    return id.getArticleId();
  }

  public String getUserId() {
    return id.getUserId();
  }

  @Embeddable
  @NoArgsConstructor
  @Getter
  @EqualsAndHashCode
  public static class ArticleFavoriteId implements Serializable {
    @Column(name = "article_id")
    private String articleId;

    @Column(name = "user_id")
    private String userId;

    public ArticleFavoriteId(String articleId, String userId) {
      this.articleId = articleId;
      this.userId = userId;
    }
  }
}
