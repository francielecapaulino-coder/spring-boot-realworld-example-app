package io.spring.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minimal OpenAPI metadata for the RealWorld REST API contract.
 *
 * <p>The contract documented here is the current behavior pinned by the integration tests
 * under {@code io.spring.api.integration}. This class does not change any endpoint behavior;
 * it only declares document-level metadata (title, version, license) and the JWT bearer
 * security scheme used by protected endpoints (RealWorld uses {@code Authorization: Token <jwt>}).
 *
 * <p>Reachable at:
 * <ul>
 *   <li>{@code /v3/api-docs} — OpenAPI 3 JSON</li>
 *   <li>{@code /swagger-ui.html} — Swagger UI</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

  private static final String SECURITY_SCHEME_NAME = "Token";

  @Bean
  public OpenAPI realworldOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("RealWorld API")
                .description(
                    "REST API contract for the RealWorld example application. "
                        + "Protected endpoints expect the header "
                        + "'Authorization: Token <jwt>'.")
                .version("1.0.0")
                .license(new License().name("MIT")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(
            new Components()
                .addSecuritySchemes(
                    SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description(
                            "RealWorld uses the scheme 'Token' (not 'Bearer'). "
                                + "Provide the full header value, e.g. 'Token eyJhbGciOi...'.")));
  }
}
