# Evidência de teste — US-06.03

## Contexto

A US-06.03 converte quatro DTOs de **escrita** (request payloads / commands) para `record`:

- `NewArticleParam` — `POST /articles`; `@JsonRootName("article")` e `@NotBlank` + `@DuplicatedArticleConstraint` movidos para os componentes; `@Builder` removido.
- `RegisterParam` — `POST /users`; `@JsonRootName("user")`, `@NotBlank`, `@Email`, `@DuplicatedEmailConstraint`, `@DuplicatedUsernameConstraint` nos componentes.
- `UpdateUserCommand` — comando interno do `UserService`; `@UpdateUserConstraint` class-level preservado na declaração do record.
- `UpdateArticleParam` — `PUT /articles/{slug}`; **compact constructor** normaliza `null → ""` para preservar o contrato `Util.isEmpty(...)` usado em `Article.update`.

`UpdateUserParam` permanece como classe Lombok (`@Builder.Default` exige defaults mutáveis) — exclusão justificada e documentada.

## Procedimento de verificação

1. **Confirmar conversões para record**:
   ```bash
   grep -n "^public record" \
     src/main/java/io/spring/application/article/NewArticleParam.java \
     src/main/java/io/spring/application/article/UpdateArticleParam.java \
     src/main/java/io/spring/application/user/RegisterParam.java \
     src/main/java/io/spring/application/user/UpdateUserCommand.java
   ```
2. **Validar que Bean Validation continua funcionando** — disparar os testes 422 explicitamente:
   ```bash
   ./gradlew test --tests "io.spring.api.integration.AuthApiIntegrationTest.register_blank_username_returns_422" \
                  --tests "io.spring.api.integration.AuthApiIntegrationTest.register_duplicate_email_returns_422" \
                  --tests "io.spring.api.integration.AuthApiIntegrationTest.register_duplicate_username_returns_422"
   ```
3. **Validar compact constructor do `UpdateArticleParam`**:
   ```bash
   grep -A4 "public UpdateArticleParam {" \
     src/main/java/io/spring/application/article/UpdateArticleParam.java
   ```
   *Resultado esperado:* `title = title == null ? "" : title;` (e idem para `body`, `description`).
4. **Confirmar exclusão justificada do `UpdateUserParam`**:
   ```bash
   grep -n "@Builder.Default" src/main/java/io/spring/application/user/UpdateUserParam.java
   ```
5. **Suite completa verde**:
   ```bash
   ./gradlew clean test --no-daemon
   ```

## Critérios de aceite

| ID | Critério | Status | Como foi verificado |
|---|---|---|---|
| CA-01 | `NewArticleParam`, `UpdateArticleParam`, `RegisterParam`, `UpdateUserCommand` são `record` | ✅ | `grep "^public record"` em 4 arquivos |
| CA-02 | `POST /users` com body inválido continua retornando 422 | ✅ | 3 testes `register_*_returns_422` PASSED |
| CA-03 | `POST /articles` com título duplicado continua retornando 422 | ✅ | `@DuplicatedArticleConstraint` no componente `title`; ArticleApiIntegrationTest verde |
| CA-04 | `UpdateArticleParam`: compact constructor normaliza `null → ""` | ✅ | Linhas 21-24 de `UpdateArticleParam.java` |
| CA-05 | `UpdateUserParam` permanece como classe (`@Builder.Default`) | ✅ | Exclusão documentada em `record-coverage.md` |
| CA-06 | Nenhuma das classes convertidas tem `import lombok.*` | ✅ | `grep "import lombok"` não retorna nada nos quatro arquivos |
| CA-07 | `./gradlew test` 100 % verde | ✅ | 115/115 PASSED |

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
| PR | [#97](https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/pull/97) |
| Branch | `refactor/us-06.03-records-write-dtos` |
| Commit | `307e1e2` |
| Diff | 7 arquivos, +81 / −81 |

---
*Esta evidência foi criada de acordo com a US-06.03 e segue o modelo de evidência usado nas histórias anteriores.*
