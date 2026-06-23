# Evidência de teste — US-04.08: Habilitar virtual threads

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup` (validação consolidada EPIC-04/EPIC-05)
- **Data:** 2026-06-23
- **Escopo da história:** habilitar virtual threads via configuração Spring Boot.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| Propriedade configurada | `grep -RIn 'spring.threads.virtual.enabled=true' src/main/resources` | ✅ `application.properties:33` |
| Java 25 disponível | `java -version` com `JAVA_HOME=/opt/homebrew/opt/openjdk@25/...` | ✅ usado nos comandos Gradle |
| Compilação | `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |
| Testes sem Docker | `./gradlew test --tests ArticleTest --tests JwtSecretFailureAnalyzerTest --tests DefaultJwtServiceTest` | ✅ `BUILD SUCCESSFUL in 7s` |

## Observação sobre startup/endpoints

A validação de startup real e endpoints (`/actuator/health`, `/tags`) requer a aplicação em execução com PostgreSQL. Docker não está disponível localmente (`docker: command not found`), então essa validação permanece delegada ao CI/ambiente Docker.

## Conclusão

US-04.08 está validada para configuração e compilação: `spring.threads.virtual.enabled=true` está presente e não introduz regressão nos checks locais possíveis.
