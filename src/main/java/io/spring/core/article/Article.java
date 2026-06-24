package io.spring.core.article;

import static java.util.stream.Collectors.toList;

import io.spring.Util;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Article {
  @Column(name = "user_id")
  private String userId;

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "slug", unique = true)
  private String slug;

  @Column(name = "title")
  private String title;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column(name = "body", columnDefinition = "text")
  private String body;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(
      name = "article_tags",
      joinColumns = @JoinColumn(name = "article_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  public Article(
      String title, String description, String body, List<String> tagList, String userId) {
    this(title, description, body, tagList, userId, Instant.now());
  }

  public Article(
      String title,
      String description,
      String body,
      List<String> tagList,
      String userId,
      Instant createdAt) {
    this.id = UUID.randomUUID().toString();
    this.slug = toSlug(title);
    this.title = title;
    this.description = description;
    this.body = body;
    this.userId = userId;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
    this.tags = new HashSet<>();
    if (tagList != null) {
      tagList.forEach(name -> this.tags.add(new Tag(name)));
    }
  }

  public void update(String title, String description, String body) {
    if (!Util.isEmpty(title)) {
      this.slug = toSlug(title);
      this.title = title;
      this.updatedAt = Instant.now();
    }
    if (!Util.isEmpty(description)) {
      this.description = description;
      this.updatedAt = Instant.now();
    }
    if (!Util.isEmpty(body)) {
      this.body = body;
      this.updatedAt = Instant.now();
    }
  }

  public List<String> getTagList() {
    return tags.stream().map(Tag::getName).collect(toList());
  }

  public static String toSlug(String title) {
    return title.toLowerCase().replaceAll("[\\s\\?\\,\\.&'\"\\uFE30-\\uFFA0]+", "-");
  }
}
