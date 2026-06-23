package io.spring.core.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment {
  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "body", columnDefinition = "text")
  private String body;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "article_id")
  private String articleId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public Comment(String body, String userId, String articleId) {
    this.id = UUID.randomUUID().toString();
    this.body = body;
    this.userId = userId;
    this.articleId = articleId;
    this.createdAt = Instant.now();
  }
}
