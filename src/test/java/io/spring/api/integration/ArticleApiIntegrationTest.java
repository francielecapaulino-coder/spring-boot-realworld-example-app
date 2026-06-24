package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for articles contract.
 * Covers: POST /articles, GET /articles, GET /articles/:slug,
 *         PUT /articles/:slug, DELETE /articles/:slug,
 *         POST/DELETE /articles/:slug/favorite, GET /articles/feed
 */
class ArticleApiIntegrationTest extends ApiIntegrationTestBase {

  private String authorToken;

  @BeforeEach
  void setUp() {
    authorToken = registerAndGetToken("author@example.com", "author", "pass1234");
  }

  private String createArticleAndGetSlug(String token) {
    ResponseEntity<String> response = postWithToken("/articles",
        "{\"article\":{\"title\":\"Test Article\",\"description\":\"desc\",\"body\":\"body content\",\"tagList\":[\"java\"]}}",
        token);
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    return extractJsonString(response.getBody(), "slug");
  }

  @Test
  void create_article_returns_200_with_article_fields() {
    ResponseEntity<String> response = postWithToken("/articles",
        "{\"article\":{\"title\":\"New Article\",\"description\":\"desc\",\"body\":\"body\",\"tagList\":[\"java\",\"spring\"]}}",
        authorToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"title\":\"New Article\"");
    assertThat(response.getBody()).contains("\"username\":\"author\"");
    assertThat(response.getBody()).contains("\"slug\":");
  }

  @Test
  void create_article_unauthenticated_returns_401() {
    ResponseEntity<String> response = post("/articles",
        "{\"article\":{\"title\":\"T\",\"description\":\"d\",\"body\":\"b\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(401);
  }

  @Test
  void get_article_by_slug_returns_200() {
    String slug = createArticleAndGetSlug(authorToken);

    ResponseEntity<String> response = get("/articles/" + slug);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"slug\":\"" + slug + "\"");
  }

  @Test
  void get_nonexistent_article_returns_404() {
    ResponseEntity<String> response = get("/articles/no-such-article");

    assertThat(response.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void list_articles_returns_200_with_articles_count() {
    createArticleAndGetSlug(authorToken);

    ResponseEntity<String> response = get("/articles");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("articlesCount");
  }

  @Test
  void update_article_returns_200_with_new_title() {
    String slug = createArticleAndGetSlug(authorToken);

    ResponseEntity<String> response = putWithToken("/articles/" + slug,
        "{\"article\":{\"title\":\"Updated Title\"}}", authorToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"title\":\"Updated Title\"");
  }

  @Test
  void update_article_by_non_author_returns_403() {
    String slug = createArticleAndGetSlug(authorToken);
    String otherToken = registerAndGetToken("other@example.com", "other", "pass1234");

    ResponseEntity<String> response = putWithToken("/articles/" + slug,
        "{\"article\":{\"title\":\"Hijacked\"}}", otherToken);

    assertThat(response.getStatusCode().value()).isEqualTo(403);
  }

  @Test
  void delete_article_returns_204() {
    String slug = createArticleAndGetSlug(authorToken);

    ResponseEntity<String> deleteResponse = deleteWithToken("/articles/" + slug, authorToken);
    assertThat(deleteResponse.getStatusCode().value()).isEqualTo(204);

    ResponseEntity<String> getResponse = get("/articles/" + slug);
    assertThat(getResponse.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void favorite_article_returns_200_with_favorited_true() {
    String slug = createArticleAndGetSlug(authorToken);
    String readerToken = registerAndGetToken("reader@example.com", "reader", "pass1234");

    ResponseEntity<String> response = postWithToken("/articles/" + slug + "/favorite", "", readerToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"favorited\":true");
    assertThat(response.getBody()).contains("\"favoritesCount\":1");
  }

  @Test
  void unfavorite_article_returns_200_with_favorited_false() {
    String slug = createArticleAndGetSlug(authorToken);
    String readerToken = registerAndGetToken("reader2@example.com", "reader2", "pass1234");

    postWithToken("/articles/" + slug + "/favorite", "", readerToken);

    ResponseEntity<String> response = deleteWithToken("/articles/" + slug + "/favorite", readerToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"favorited\":false");
    assertThat(response.getBody()).contains("\"favoritesCount\":0");
  }

  @Test
  void get_feed_returns_200_for_authenticated_user() {
    ResponseEntity<String> response = getWithToken("/articles/feed", authorToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("articlesCount");
  }

  // ---- Soft delete (US-88) ---------------------------------------------------

  @Test
  void soft_deleted_article_is_excluded_from_list() {
    // Create two articles with distinct slugs AND distinct tags, then soft-delete
    // the first one. NOTE: tags are intentionally distinct ("sd-first"/"sd-second")
    // because a pre-existing interaction between @EntityGraph(attributePaths="tags")
    // on findBySlug and shared tags in Hibernate 7.2 can return duplicate rows.
    // This is unrelated to soft delete and is tracked separately.
    String firstSlug =
        extractJsonString(
            postWithToken(
                    "/articles",
                    "{\"article\":{\"title\":\"First To Delete\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"sd-first\"]}}",
                    authorToken)
                .getBody(),
            "slug");
    String secondSlug =
        extractJsonString(
            postWithToken(
                    "/articles",
                    "{\"article\":{\"title\":\"Second Survives\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"sd-second\"]}}",
                    authorToken)
                .getBody(),
            "slug");

    assertThat(deleteWithToken("/articles/" + firstSlug, authorToken).getStatusCode().value())
        .isEqualTo(204);

    ResponseEntity<String> list = get("/articles");
    assertThat(list.getStatusCode().value()).isEqualTo(200);
    assertThat(list.getBody()).doesNotContain("\"slug\":\"" + firstSlug + "\"");
    assertThat(list.getBody()).contains("\"slug\":\"" + secondSlug + "\"");
  }

  @Test
  void soft_delete_by_non_author_is_forbidden_and_article_remains_visible() {
    String slug = createArticleAndGetSlug(authorToken);
    String otherToken = registerAndGetToken("sd-intruder@example.com", "sdintruder", "pass1234");

    ResponseEntity<String> deleteResponse = deleteWithToken("/articles/" + slug, otherToken);
    assertThat(deleteResponse.getStatusCode().value()).isEqualTo(403);

    // The article must still be reachable since the unauthorized delete was rejected.
    ResponseEntity<String> getResponse = get("/articles/" + slug);
    assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
    assertThat(getResponse.getBody()).contains("\"slug\":\"" + slug + "\"");
  }

  @Test
  void second_delete_of_soft_deleted_article_returns_404() {
    String slug = createArticleAndGetSlug(authorToken);

    assertThat(deleteWithToken("/articles/" + slug, authorToken).getStatusCode().value())
        .isEqualTo(204);

    // After soft delete the article is no longer visible to reads OR to a
    // subsequent delete (findBySlug filters it out), so the second call
    // results in ResourceNotFoundException -> 404.
    ResponseEntity<String> secondDelete = deleteWithToken("/articles/" + slug, authorToken);
    assertThat(secondDelete.getStatusCode().value()).isEqualTo(404);
  }
}
