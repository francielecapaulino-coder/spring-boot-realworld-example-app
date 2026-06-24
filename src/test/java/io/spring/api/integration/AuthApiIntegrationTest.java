package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for authentication contract.
 * Covers: POST /users (register), POST /users/login
 */
class AuthApiIntegrationTest extends ApiIntegrationTestBase {

  @Test
  void register_success_returns_201_with_user_and_token() {
    ResponseEntity<String> response = post("/users",
        "{\"user\":{\"email\":\"alice@example.com\",\"username\":\"alice\",\"password\":\"secret123\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getBody()).contains("\"email\":\"alice@example.com\"");
    assertThat(response.getBody()).contains("\"username\":\"alice\"");
    assertThat(response.getBody()).contains("\"token\":");
  }

  @Test
  void register_duplicate_email_returns_422() {
    post("/users", "{\"user\":{\"email\":\"dup@example.com\",\"username\":\"dup1\",\"password\":\"secret123\"}}");

    ResponseEntity<String> response = post("/users",
        "{\"user\":{\"email\":\"dup@example.com\",\"username\":\"dup2\",\"password\":\"secret123\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).contains("duplicated email");
  }

  @Test
  void register_duplicate_username_returns_422() {
    post("/users", "{\"user\":{\"email\":\"u1@example.com\",\"username\":\"dupuser\",\"password\":\"secret123\"}}");

    ResponseEntity<String> response = post("/users",
        "{\"user\":{\"email\":\"u2@example.com\",\"username\":\"dupuser\",\"password\":\"secret123\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).contains("duplicated username");
  }

  @Test
  void register_blank_username_returns_422() {
    ResponseEntity<String> response = post("/users",
        "{\"user\":{\"email\":\"x@example.com\",\"username\":\"\",\"password\":\"secret123\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).contains("can't be empty");
  }

  @Test
  void login_success_returns_200_with_token() {
    registerAndGetToken("bob@example.com", "bob", "pass1234");

    ResponseEntity<String> response = post("/users/login",
        "{\"user\":{\"email\":\"bob@example.com\",\"password\":\"pass1234\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).contains("\"email\":\"bob@example.com\"");
    assertThat(response.getBody()).contains("\"token\":");
  }

  @Test
  void login_wrong_password_returns_422() {
    registerAndGetToken("carol@example.com", "carol", "correct");

    ResponseEntity<String> response = post("/users/login",
        "{\"user\":{\"email\":\"carol@example.com\",\"password\":\"wrong\"}}");

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).contains("invalid email or password");
  }
}
