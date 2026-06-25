package io.spring.core.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ArticleTest {

  // ---------------------------------------------------------------------------
  // Slug generation (existing tests)
  // ---------------------------------------------------------------------------

  @Test
  public void should_get_right_slug() {
    Article article = new Article("a new   title", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_get_right_slug_with_number_in_title() {
    Article article = new Article("a new title 2", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title-2"));
  }

  @Test
  public void should_get_lower_case_slug() {
    Article article = new Article("A NEW TITLE", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_handle_other_language() {
    Article article = new Article("中文：标题", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("中文-标题"));
  }

  @Test
  public void should_handle_commas() {
    Article article = new Article("what?the.hell,w", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("what-the-hell-w"));
  }

  // ---------------------------------------------------------------------------
  // getTagList — covers the NO_COVERAGE mutation on Article.getTagList() (line 113)
  // ---------------------------------------------------------------------------

  @Test
  public void should_return_tag_names_from_constructor() {
    Article article =
        new Article("title", "desc", "body", Arrays.asList("java", "spring"), "user-1");
    List<String> tagList = article.getTagList();
    assertThat(tagList).containsExactlyInAnyOrder("java", "spring");
  }

  @Test
  public void should_return_empty_tag_list_when_constructed_without_tags() {
    Article article = new Article("title", "desc", "body", List.of(), "user-1");
    assertThat(article.getTagList()).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // update — covers SURVIVED mutation on Article.update() body branch (line 102)
  // and the other two conditional branches (title, description).
  // ---------------------------------------------------------------------------

  @Test
  public void should_update_title_when_not_empty() {
    Article article = new Article("old", "desc", "body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();
    sleepOneMs();

    article.update("new title", "", "");

    assertThat(article.getTitle()).isEqualTo("new title");
    assertThat(article.getSlug()).isEqualTo("new-title");
    assertThat(article.getUpdatedAt()).isAfter(originalUpdatedAt);
    // unchanged fields preserved
    assertThat(article.getDescription()).isEqualTo("desc");
    assertThat(article.getBody()).isEqualTo("body");
  }

  @Test
  public void should_update_description_when_not_empty() {
    Article article = new Article("title", "old desc", "body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();
    sleepOneMs();

    article.update("", "new desc", "");

    assertThat(article.getDescription()).isEqualTo("new desc");
    assertThat(article.getUpdatedAt()).isAfter(originalUpdatedAt);
    // unchanged
    assertThat(article.getTitle()).isEqualTo("title");
    assertThat(article.getBody()).isEqualTo("body");
  }

  @Test
  public void should_update_body_when_not_empty() {
    Article article = new Article("title", "desc", "old body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();
    sleepOneMs();

    article.update("", "", "new body");

    assertThat(article.getBody()).isEqualTo("new body");
    assertThat(article.getUpdatedAt()).isAfter(originalUpdatedAt);
    // unchanged
    assertThat(article.getTitle()).isEqualTo("title");
    assertThat(article.getDescription()).isEqualTo("desc");
  }

  @Test
  public void should_not_change_anything_when_all_inputs_are_empty() {
    Article article = new Article("title", "desc", "body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();
    String originalSlug = article.getSlug();

    article.update("", "", "");

    assertThat(article.getTitle()).isEqualTo("title");
    assertThat(article.getDescription()).isEqualTo("desc");
    assertThat(article.getBody()).isEqualTo("body");
    assertThat(article.getSlug()).isEqualTo(originalSlug);
    assertThat(article.getUpdatedAt()).isEqualTo(originalUpdatedAt);
  }

  @Test
  public void should_treat_null_inputs_as_empty_on_update() {
    Article article = new Article("title", "desc", "body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();

    article.update(null, null, null);

    assertThat(article.getTitle()).isEqualTo("title");
    assertThat(article.getDescription()).isEqualTo("desc");
    assertThat(article.getBody()).isEqualTo("body");
    assertThat(article.getUpdatedAt()).isEqualTo(originalUpdatedAt);
  }

  // ---------------------------------------------------------------------------
  // delete — soft delete flips the deleted flag and bumps updatedAt
  // ---------------------------------------------------------------------------

  @Test
  public void should_mark_article_as_deleted_and_bump_updated_at() {
    Article article = new Article("title", "desc", "body", List.of(), "user-1");
    Instant originalUpdatedAt = article.getUpdatedAt();
    sleepOneMs();

    article.delete();

    assertThat(article.isDeleted()).isTrue();
    assertThat(article.getUpdatedAt()).isAfter(originalUpdatedAt);
  }

  // ---------------------------------------------------------------------------
  // toSlug (static) — covers downstream conversions used directly
  // ---------------------------------------------------------------------------

  @Test
  public void static_toSlug_should_convert_title_to_slug() {
    assertThat(Article.toSlug("Hello World")).isEqualTo("hello-world");
  }

  private static void sleepOneMs() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
