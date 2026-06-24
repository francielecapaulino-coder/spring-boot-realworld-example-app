package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for profile contract.
 * Covers: GET /profiles/:username, POST/DELETE /profiles/:username/follow
 */
class ProfileApiIntegrationTest extends ApiIntegrationTestBase {

  private String followerToken;

  @BeforeEach
  void setUp() {
    registerAndGetToken("target@example.com", "target", "pass1234");
    followerToken = registerAndGetToken("follower@example.com", "follower", "pass1234");
  }

  @Test
  void get_profile_returns_200_with_username() {
    ResponseEntity<String> response = get("/profiles/target");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"username\":\"target\"");
    assertThat(response.getBody()).contains("\"following\":false");
  }

  @Test
  void get_nonexistent_profile_returns_404() {
    ResponseEntity<String> response = get("/profiles/nobody");

    assertThat(response.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void follow_user_returns_200_with_following_true() {
    ResponseEntity<String> response = postWithToken("/profiles/target/follow", "", followerToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"username\":\"target\"");
    assertThat(response.getBody()).contains("\"following\":true");
  }

  @Test
  void unfollow_user_returns_200_with_following_false() {
    postWithToken("/profiles/target/follow", "", followerToken);

    ResponseEntity<String> response = deleteWithToken("/profiles/target/follow", followerToken);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"following\":false");
  }
}
