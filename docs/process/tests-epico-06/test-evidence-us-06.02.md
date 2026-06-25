# Evidência de teste — US-06.02

## Contexto

A US-06.02 converte quatro **wrapper DTOs e contadores** de `io.spring.application.data` para `record`:

- `UserData` — view do usuário.
- `UserWithToken` — view do usuário + JWT (preserva construtor secundário `(UserData, String)` usado por `UsersApi` e `MeDatafetcher`).
- `ArticleFavoriteCount` — contador por artigo.
- `ArticleDataList` — wrapper paginado (`articles` + `articlesCount`); `@JsonProperty` permanece nos componentes para preservar o JSON.

Esta foi a primeira US do EPIC-06: mais simples, estabeleceu o padrão de conversão usado depois pelas US-06.03/04/01.

## Procedimento de verificação

1. **Confirmar conversões para record**:
   ```bash
   grep -n "^public record" \
     src/main/java/io/spring/application/data/UserData.java \
     src/main/java/io/spring/application/data/UserWithToken.java \
     src/main/java/io/spring/application/data/ArticleFavoriteCount.java \
     src/main/java/io/spring/application/data/ArticleDataList.java
   ```
2. **Verificar preservação do contrato JSON do `ArticleDataList`**:
   ```bash
   grep -n "@JsonProperty" src/main/java/io/spring/application/data/ArticleDataList.java
   ```
   *Resultado esperado:* `@JsonProperty("articles")` e `@JsonProperty("articlesCount")` nos componentes.
3. **Validar construtor secundário do `UserWithToken`**:
   ```bash
   grep -n "public UserWithToken(UserData" \
     src/main/java/io/spring/application/data/UserWithToken.java
   ```
4. **Suite completa verde**:
   ```bash
   ./gradlew clean test --no-daemon
   ```
5. **Integration tests REST que exercem login/register e listagem de artigos** (`AuthApiIntegrationTest`, `ArticleApiIntegrationTest`) — confirmam o JSON byte-for-byte.

## Critérios de aceite

| ID | Critério | Status | Como foi verificado |
|---|---|---|---|
| CA-01 | `UserData`, `UserWithToken`, `ArticleFavoriteCount`, `ArticleDataList` são `record` | ✅ | `grep "^public record"` em 4 arquivos |
| CA-02 | Wire format `{"articles": [...], "articlesCount": N}` preservado | ✅ | `@JsonProperty` em componentes; integration tests verdes |
| CA-03 | `UserWithToken(UserData, String)` continua funcionando para `UsersApi` e `MeDatafetcher` | ✅ | Construtor secundário declarado no record |
| CA-04 | Nenhuma das quatro classes tem `import lombok.*` | ✅ | `grep "import lombok"` não retorna nada |
| CA-05 | `./gradlew test` 100 % verde | ✅ | 115/115 PASSED |
| CA-06 | Call-sites atualizados (`ProfileQueryService`, `MeDatafetcher`, `ArticleQueryService`, `ArticleQueryServiceTest`) | ✅ | 4 arquivos modificados, suite verde |

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
| PR | [#95](https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/pull/95) |
| Branch | `refactor/us-06.02-records-wrappers` |
| Commit | `cae2d2d` |
| Diff | 8 arquivos, +64 / −78 |

---
*Esta evidência foi criada de acordo com a US-06.02 e segue o modelo de evidência usado nas histórias anteriores.*
