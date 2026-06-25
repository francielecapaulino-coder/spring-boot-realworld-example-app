# Evidência de teste — US-06.01

## Contexto

A US-06.01 converte os três principais DTOs de leitura do pacote `io.spring.application.data` para `record`:

- `ProfileData` — autor/perfil embarcado em artigos e comentários.
- `CommentData` — comentário (implementa `Node` com `getCursor()` explícito por ADR-005).
- `ArticleData` — artigo (implementa `Node` com `getCursor()` explícito por ADR-005).

Foi a US de maior superfície do EPIC-06 (16 arquivos) porque o código de query enriquecia esses DTOs **em lugar** via setters. O padrão foi substituído por records imutáveis + métodos *wither* + `List.replaceAll(...)` nos call-sites.

## Procedimento de verificação

1. **Confirmar conversões para record** com `getCursor()` explícito:
   ```bash
   grep -n "^public record" \
     src/main/java/io/spring/application/data/ProfileData.java \
     src/main/java/io/spring/application/data/CommentData.java \
     src/main/java/io/spring/application/data/ArticleData.java
   grep -n "getCursor()" \
     src/main/java/io/spring/application/data/CommentData.java \
     src/main/java/io/spring/application/data/ArticleData.java
   ```
2. **Validar ausência de Lombok** nos três DTOs:
   ```bash
   grep -n "import lombok" \
     src/main/java/io/spring/application/data/ProfileData.java \
     src/main/java/io/spring/application/data/CommentData.java \
     src/main/java/io/spring/application/data/ArticleData.java
   ```
   *Resultado esperado:* nenhuma linha.
3. **Suite completa verde**:
   ```bash
   ./gradlew clean test --no-daemon
   ```
4. **Verificação específica do contrato JSON** via 32 integration tests do PR #85:
   ```bash
   ./gradlew test --tests "io.spring.api.integration.*"
   ```

## Critérios de aceite

| ID | Critério | Status | Como foi verificado |
|---|---|---|---|
| CA-01 | `ProfileData`, `ArticleData`, `CommentData` são `record` e compilam | ✅ | `grep "^public record"` retorna 3 linhas |
| CA-02 | `ArticleData` e `CommentData` declaram `getCursor()` explicitamente (ADR-005) | ✅ | Linhas 53-55 de `ArticleData.java` e 38-40 de `CommentData.java` |
| CA-03 | Nenhuma das três classes tem `import lombok.*` | ✅ | `grep "import lombok"` não retorna nada |
| CA-04 | `./gradlew build` sem warnings | ✅ | Última execução: `BUILD SUCCESSFUL in 34s` |
| CA-05 | `./gradlew test` 100 % verde | ✅ | 115/115 PASSED |
| CA-06 | Resolvers GraphQL/DGS continuam funcionando | ✅ | `ArticleDatafetcher`, `CommentDatafetcher`, `ProfileDatafetcher` e `RelationMutation` compilam e os testes de integração passam |

## Resultado da execução

```text
> Task :test

BUILD SUCCESSFUL in 30s
7 actionable tasks: 7 executed

Total tests:    115
Total failures: 0
Total errors:   0
```

## PR e commit

| Item | Valor |
|---|---|
| PR | [#98](https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/pull/98) |
| Branch | `refactor/us-06.01-records-read-dtos` |
| Commit | `b169ec9` |
| Diff | 16 arquivos, +253 / −203 |

---
*Esta evidência foi criada de acordo com a US-06.01 e segue o modelo de evidência usado nas histórias anteriores.*
