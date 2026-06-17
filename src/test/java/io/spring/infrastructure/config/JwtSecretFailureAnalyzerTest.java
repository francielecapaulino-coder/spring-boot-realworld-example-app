package io.spring.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

class JwtSecretFailureAnalyzerTest {

  private final JwtSecretFailureAnalyzer analyzer = new JwtSecretFailureAnalyzer();

  @Test
  void returnsFailureAnalysisWhenMessageContainsJwtSecret() {
    IllegalArgumentException cause =
        new IllegalArgumentException(
            "Could not resolve placeholder 'JWT_SECRET' in value \"${JWT_SECRET}\"");

    FailureAnalysis result = analyzer.analyze(new RuntimeException(cause), cause);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).contains("JWT_SECRET", "obrigatoria", "tokens JWT");
    assertThat(result.getAction()).contains("openssl rand -base64 64", "export JWT_SECRET", "ADR-006");
  }

  @Test
  void returnsNullWhenMessageDoesNotContainJwtSecret() {
    IllegalArgumentException cause =
        new IllegalArgumentException("Could not resolve placeholder 'OTHER_VAR'");

    FailureAnalysis result = analyzer.analyze(new RuntimeException(cause), cause);

    assertThat(result).isNull();
  }

  @Test
  void returnsNullWhenCauseMessageIsNull() {
    IllegalArgumentException cause = new IllegalArgumentException((String) null);

    FailureAnalysis result = analyzer.analyze(new RuntimeException(cause), cause);

    assertThat(result).isNull();
  }

  @Test
  void descriptionContainsApplicationCannotStart() {
    IllegalArgumentException cause =
        new IllegalArgumentException("placeholder 'JWT_SECRET' missing");

    FailureAnalysis result = analyzer.analyze(new RuntimeException(cause), cause);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).contains("aplicacao nao pode iniciar");
  }

  @Test
  void actionContainsEnvExampleReference() {
    IllegalArgumentException cause =
        new IllegalArgumentException("placeholder 'JWT_SECRET' missing");

    FailureAnalysis result = analyzer.analyze(new RuntimeException(cause), cause);

    assertThat(result).isNotNull();
    assertThat(result.getAction()).contains(".env.example");
  }
}
