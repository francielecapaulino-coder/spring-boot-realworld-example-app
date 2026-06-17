package io.spring.infrastructure.config;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Intercepta a falha de startup quando JWT_SECRET nao esta definida
 * e substitui a mensagem generica do Spring por instrucoes claras.
 *
 * <p>Registrado via META-INF/spring.factories (Spring Boot 2.x).
 *
 * @see <a href="docs/06-architecture-decisions.md">ADR-006</a>
 */
public class JwtSecretFailureAnalyzer
    extends AbstractFailureAnalyzer<IllegalArgumentException> {

  static final String JWT_SECRET_PLACEHOLDER = "JWT_SECRET";

  private static final String DESCRIPTION =
      "A variavel de ambiente JWT_SECRET nao esta definida.\n"
          + "JWT_SECRET e obrigatoria para assinar tokens JWT (algoritmo HS512).\n"
          + "Sem ela, nenhuma autenticacao e possivel e a aplicacao nao pode iniciar.";

  private static final String ACTION =
      "1. Gere um valor seguro:\n"
          + "     openssl rand -base64 64\n"
          + "2. Exporte a variavel no terminal atual:\n"
          + "     export JWT_SECRET=<valor-gerado>\n"
          + "3. Para desenvolvimento persistente, adicione ao seu .env local:\n"
          + "     JWT_SECRET=<valor-gerado>\n"
          + "     (o arquivo .env NAO deve ser commitado"
          + " -- use .env.example como referencia)\n"
          + "Referencia: docs/06-architecture-decisions.md -- ADR-006";

  @Override
  protected FailureAnalysis analyze(Throwable rootFailure,
      IllegalArgumentException cause) {
    if (!isJwtSecretMissing(cause)) {
      return null;
    }
    return new FailureAnalysis(DESCRIPTION, ACTION, cause);
  }

  private boolean isJwtSecretMissing(IllegalArgumentException cause) {
    String message = cause.getMessage();
    return message != null && message.contains(JWT_SECRET_PLACEHOLDER);
  }
}
