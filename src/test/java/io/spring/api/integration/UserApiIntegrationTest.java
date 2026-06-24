package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for current-user contract.
 * Covers: GET /user, PUT /user
 */
class UserApiIntegrationTest extends ApiIntegrationTestBase {

  private String token;

  @BeforeEach
  void registerUser() {
    token = registerAndGetToken("dave@example.com", "dave", "pass1234");
  }

  @Test
  void get_current_user_returns_200_with_user_fields() {
    ResponseEntity<String> response = getWithToken("/user", token);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"email\":\"dave@example.com\"");
    assertThat(response.getBody()).contains("\"username\":\"dave\"");
    assertThat(response.getBody()).contains("\"token\":");
  }

  @Test
  void get_current_user_unauthenticated_returns_401() {
    ResponseEntity<String> response = get("/user");

    assertThat(response.getStatusCode().value()).isEqualTo(401);
  }

  @Test
  void update_user_bio_returns_200() {
    ResponseEntity<String> response = putWithToken("/user", "{\"user\":{\"bio\":\"I love coding\"}}", token);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"bio\":\"I love coding\"");
  }
}
