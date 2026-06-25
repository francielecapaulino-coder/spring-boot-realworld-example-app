package io.spring.application.article;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Request payload for {@code PUT /articles/{slug}}.
 *
 * <p>Pure carrier of data — converted to {@code record} under US-06.03 / KR1.5
 * (mandate J5). The previous Lombok class assigned literal {@code ""} defaults
 * to every field; records do not support default values per component, so the
 * compact constructor below normalises any {@code null} that Jackson produces
 * for absent JSON properties into the empty string. This preserves the contract
 * relied on by {@code ArticleCommandService.updateArticle}, which uses
 * {@code Util.isEmpty(...)} (i.e. empty-string-aware) to decide whether each
 * field was supplied.
 */
@JsonRootName("article")
public record UpdateArticleParam(String title, String body, String description) {

  public UpdateArticleParam {
    title = title == null ? "" : title;
    body = body == null ? "" : body;
    description = description == null ? "" : description;
  }
}
