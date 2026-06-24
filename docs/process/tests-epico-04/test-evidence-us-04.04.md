# Evidência de teste — US-04.04: Migrar imports Jakarta EE `javax.*` para `jakarta.*`

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup` (validação consolidada EPIC-04/EPIC-05)
- **Data:** 2026-06-23
- **Escopo da história:** remover imports Java EE/Jakarta legados (`javax.persistence`, `javax.validation`, `javax.servlet`, `javax.annotation`) e preservar `javax.crypto.*` como exceção JDK para JWT.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| Imports Java EE/Jakarta legados | `grep -RIn 'import javax\.\(persistence\|validation\|servlet\|annotation\)' src` | ✅ 0 ocorrências |
| Exceção JDK documentada | `grep -n 'javax.crypto' src/main/java/io/spring/infrastructure/service/DefaultJwtService.java` | ✅ `javax.crypto.SecretKey` preservado |
| Compilação de produção/testes | `./gradlew compileJava compileTestJava --no-daemon --console=plain` | ✅ `BUILD SUCCESSFUL in 4s` |
| Testes JWT sem Docker | `./gradlew test --tests ArticleTest --tests JwtSecretFailureAnalyzerTest --tests DefaultJwtServiceTest` | ✅ `BUILD SUCCESSFUL in 7s` |
| Suíte completa | `./gradlew test --no-daemon --console=plain` | 🟡 25 falhas por Docker/Testcontainers indisponível localmente |

## Resultado da suíte completa

- Total: `77 tests completed, 25 failed`.
- Causa das falhas: `DockerClientProviderStrategy` + `HikariPool$PoolInitializationException` ao iniciar PostgreSQL via Testcontainers.
- Nenhuma falha de compilação ou regressão JWT antes do bloqueio de ambiente.

## Conclusão

US-04.04 está validada localmente para o escopo não-Docker: namespace Jakarta EE migrado, exceção `javax.crypto` preservada e testes JWT verdes. A validação integral com Testcontainers permanece delegada ao CI com Docker.
