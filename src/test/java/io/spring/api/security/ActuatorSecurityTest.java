package io.spring.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Guards the security rules for Spring Actuator endpoints (issue #67).
 *
 * <p>The Docker healthcheck (US-03.04), {@code validate_startup.py} (US-03.06) and the Prometheus
 * scrape (US-03.05) all rely on these endpoints being reachable without authentication. A
 * regression that re-protects {@code /actuator/health} would break the whole containerized stack,
 * so it must be caught by the fast {@code build} job — not only by the {@code validate-startup} job.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorSecurityTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void health_endpoint_is_public_and_reports_up() {
    ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("\"status\":\"UP\"");
  }

  @Test
  void info_endpoint_is_public() {
    ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void prometheus_endpoint_is_public() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/actuator/prometheus", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void protected_endpoint_still_requires_authentication() {
    ResponseEntity<String> response = restTemplate.getForEntity("/articles/feed", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
