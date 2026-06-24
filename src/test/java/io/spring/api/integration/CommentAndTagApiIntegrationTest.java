package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for comments and tags contracts.
 * Covers: POST/GET/DELETE /articles/:slug/comments, GET /tags
 */
class CommentAndTagApiIntegrationTest extends ApiIntegrationTestBase {

  private String authorToken;
  private String articleSlug;

  @BeforeEach
  void setUp() {
    authorToken = registerAndGetToken("cmtauthor@example.com", "cmtauthor", "pass1234");
    ResponseEntity<String> articleResponse = postWithToken("/articles",
        "{\"article\":{\"title\":\"Comment Test\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"comments\"]}}",
        authorToken);
    articleSlug = extractJsonString(articleResponse.getBody(), "slug");
  }

  @Test
  void add_comment_returns_201_with_comment_body() {
    ResponseEntity<String> response = postWithToken(
        "/articles/" + articleSlug + "/comments",
        "{\"comment\":{\"body\":\"Great article!\"}}",
        authorToken);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getBody()).contains("\"body\":\"Great article!\"");
    assertThat(response.getBody()).contains("\"id\":");
  }

  @Test
  void add_comment_unauthenticated_returns_401() {
    ResponseEntity<String> response = post(
        "/articles/" + articleSlug + "/comments",
        "{\"comment\":{\"body\":\"Spam\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(401);
  }

  @Test
  void list_comments_returns_200() {
    postWithToken("/articles/" + articleSlug + "/comments",
        "{\"comment\":{\"body\":\"First!\"}}", authorToken);

    ResponseEntity<String> response = get("/articles/" + articleSlug + "/comments");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("comments");
  }

  @Test
  void delete_comment_returns_204() {
    ResponseEntity<String> createResponse = postWithToken(
        "/articles/" + articleSlug + "/comments",
        "{\"comment\":{\"body\":\"To be deleted\"}}",
        authorToken);
    String commentId = extractJsonString(createResponse.getBody(), "id");

    ResponseEntity<String> deleteResponse = deleteWithToken(
        "/articles/" + articleSlug + "/comments/" + commentId, authorToken);

    assertThat(deleteResponse.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  void delete_comment_by_non_author_returns_403() {
    ResponseEntity<String> createResponse = postWithToken(
        "/articles/" + articleSlug + "/comments",
        "{\"comment\":{\"body\":\"Mine\"}}",
        authorToken);
    String commentId = extractJsonString(createResponse.getBody(), "id");

    String otherToken = registerAndGetToken("intruder@example.com", "intruder", "pass1234");

    ResponseEntity<String> deleteResponse = deleteWithToken(
        "/articles/" + articleSlug + "/comments/" + commentId, otherToken);

    assertThat(deleteResponse.getStatusCode().value()).isEqualTo(403);
  }

  @Test
  void get_tags_returns_200_with_tag_list() {
    ResponseEntity<String> response = get("/tags");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("comments");
  }
}
