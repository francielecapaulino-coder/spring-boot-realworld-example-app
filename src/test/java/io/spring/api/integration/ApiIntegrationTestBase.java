package io.spring.api.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.client.RestClient;

/**
 * Base for full-stack HTTP integration tests using RestClient (Spring 6.1+).
 * Starts the real server on a random port, uses Testcontainers PostgreSQL (profile "test"),
 * and truncates all tables after each test method for isolation.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public abstract class ApiIntegrationTestBase {

  @LocalServerPort
  private int serverPort;

  protected RestClient client;

  @BeforeEach
  void setUpRestClient() {
    client = RestClient.builder()
        .baseUrl("http://localhost:" + serverPort)
        .defaultStatusHandler(status -> true, (request, response) -> {
          // suppress exception throwing - tests check status manually via assertions
        })
        .build();
  }

  protected ResponseEntity<String> post(String path, String body) {
    return client.post().uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .toEntity(String.class);
  }

  protected ResponseEntity<String> postWithToken(String path, String body, String token) {
    return client.post().uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Token " + token)
        .body(body)
        .retrieve()
        .toEntity(String.class);
  }

  protected ResponseEntity<String> get(String path) {
    return client.get().uri(path)
        .retrieve()
        .toEntity(String.class);
  }

  protected ResponseEntity<String> getWithToken(String path, String token) {
    return client.get().uri(path)
        .header("Authorization", "Token " + token)
        .retrieve()
        .toEntity(String.class);
  }

  protected ResponseEntity<String> putWithToken(String path, String body, String token) {
    return client.put().uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Token " + token)
        .body(body)
        .retrieve()
        .toEntity(String.class);
  }

  protected ResponseEntity<String> deleteWithToken(String path, String token) {
    return client.delete().uri(path)
        .header("Authorization", "Token " + token)
        .retrieve()
        .toEntity(String.class);
  }

  protected String registerAndGetToken(String email, String username, String password) {
    String body = String.format(
        "{\"user\":{\"email\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}}",
        email, username, password);
    ResponseEntity<String> response = post("/users", body);
    if (response.getStatusCode() != HttpStatus.CREATED) {
      throw new IllegalStateException("Registration failed [" + response.getStatusCode() + "]: " + response.getBody());
    }
    return extractJsonString(response.getBody(), "token");
  }

  protected String loginAndGetToken(String email, String password) {
    String body = String.format(
        "{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}",
        email, password);
    ResponseEntity<String> response = post("/users/login", body);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException("Login failed [" + response.getStatusCode() + "]: " + response.getBody());
    }
    return extractJsonString(response.getBody(), "token");
  }

  /**
   * Extracts the first occurrence of a JSON string field value by key name.
   */
  protected String extractJsonString(String json, String key) {
    String search = "\"" + key + "\":\"";
    int index = json.indexOf(search);
    if (index == -1) {
      throw new IllegalStateException("Key '" + key + "' not found in: " + json);
    }
    int start = index + search.length();
    int end = json.indexOf("\"", start);
    return json.substring(start, end);
  }
}
