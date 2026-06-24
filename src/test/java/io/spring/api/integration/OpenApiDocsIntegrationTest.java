package io.spring.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Validates that the springdoc-openapi endpoints are reachable without authentication
 * and that the OpenAPI JSON document includes the documented REST paths.
 *
 * <p>Does not assert the full schema — only that the doc endpoint responds 200, advertises
 * OpenAPI 3, and contains a sample of well-known REST paths covered by the contract.
 */
class OpenApiDocsIntegrationTest extends ApiIntegrationTestBase {

  @Test
  void api_docs_endpoint_returns_200_and_openapi_3_document() {
    ResponseEntity<String> response = get("/v3/api-docs");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    String body = response.getBody();
    assertThat(body).isNotNull();
    // OpenAPI 3 documents declare an "openapi" version field.
    assertThat(body).contains("\"openapi\":\"3.");
    assertThat(body).contains("\"title\":\"RealWorld API\"");
  }

  @Test
  void api_docs_document_includes_known_rest_paths() {
    ResponseEntity<String> response = get("/v3/api-docs");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    String body = response.getBody();
    assertThat(body).contains("/users");
    assertThat(body).contains("/users/login");
    assertThat(body).contains("/user");
    assertThat(body).contains("/articles");
    assertThat(body).contains("/tags");
  }

  @Test
  void swagger_ui_redirect_returns_2xx_or_3xx() {
    ResponseEntity<String> response = get("/swagger-ui.html");

    int status = response.getStatusCode().value();
    assertThat(status).isBetween(200, 399);
  }
}
