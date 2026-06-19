package io.spring.api.security;

import io.spring.api.TagsApi;
import io.spring.application.TagsQueryService;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Guards the Spring Security rules for the Actuator endpoints (issue #67).
 *
 * <p>The Docker healthcheck (US-03.04), {@code validate_startup.py} (US-03.06) and the Prometheus
 * scrape (US-03.05) all rely on {@code /actuator/*} being reachable without authentication. A
 * regression that re-protects {@code /actuator/health} would break the whole containerized stack.
 *
 * <p>This is a lightweight {@code @WebMvcTest} slice (no datasource / Testcontainers): the actuator
 * endpoints are not mapped in the slice, so a permitted request reaches the dispatcher and yields
 * {@code 404} (handler not found) instead of {@code 401} (blocked by security). Asserting "not 401"
 * verifies the {@code permitAll} rule deterministically and fast, in the {@code build} job.
 */
@WebMvcTest(TagsApi.class)
@Import(WebSecurityConfig.class)
class ActuatorSecurityTest {

  @Autowired private MockMvc mvc;

  @MockBean private TagsQueryService tagsQueryService;
  @MockBean private JwtService jwtService;
  @MockBean private UserRepository userRepository;

  @Test
  void actuator_health_is_not_blocked_by_authentication() throws Exception {
    // permitAll -> request passes security and reaches dispatcher (404, no handler), never 401.
    mvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void actuator_prometheus_is_not_blocked_by_authentication() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void protected_endpoint_still_requires_authentication() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/articles/feed"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void public_business_endpoint_is_permitted() throws Exception {
    // /tags is permitAll and has a handler in this slice -> 200.
    mvc.perform(MockMvcRequestBuilders.get("/tags"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
