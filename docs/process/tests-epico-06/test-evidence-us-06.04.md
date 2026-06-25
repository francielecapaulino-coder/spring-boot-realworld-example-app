# Evidência de teste — US-06.04

## Contexto

A US-06.04 converte os **dois resources de erro REST** do pacote `io.spring.api.exception` para `record`:

- `FieldErrorResource(String resource, String field, String code, String message)` — entrada individual de erro de campo.
- `ErrorResource(List<FieldErrorResource> fieldErrors)` — agregado serializado pelo `ErrorResourceSerializer` no envelope histórico `{"errors": {"<field>": ["<msg>"...]}}`.

O wire format é decidido pelo serializer customizado — a conversão não muda a representação JSON.

## Procedimento de verificação

1. **Confirmar conversões para record**:
   ```bash
   grep -n "^public record" \
     src/main/java/io/spring/api/exception/FieldErrorResource.java \
     src/main/java/io/spring/api/exception/ErrorResource.java
   ```
2. **Validar preservação das anotações Jackson em `ErrorResource`**:
   ```bash
   grep -n "@JsonSerialize\|@JsonIgnoreProperties\|@JsonRootName" \
     src/main/java/io/spring/api/exception/ErrorResource.java
   ```
   *Resultado esperado:* `@JsonSerialize(using = ErrorResourceSerializer.class)`, `@JsonIgnoreProperties(ignoreUnknown = true)` e `@JsonRootName("errors")` na declaração.
3. **Disparar os testes 422 explicitamente** (o serializer customizado é exercido aqui):
   ```bash
   ./gradlew test --tests "io.spring.api.integration.AuthApiIntegrationTest.register_blank_username_returns_422" \
                  --tests "io.spring.api.integration.AuthApiIntegrationTest.register_duplicate_email_returns_422"
   ```
4. **Validar atualização dos acessadores** em `ErrorResourceSerializer` e `GraphQLCustomizeExceptionHandler`:
   ```bash
   grep -n "fieldErrorResource\." \
     src/main/java/io/spring/api/exception/ErrorResourceSerializer.java \
     src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java
   ```
   *Resultado esperado:* todas as chamadas usam `field()` / `message()` / `fieldErrors()` (record accessors).
5. **Suite completa verde**:
   ```bash
   ./gradlew clean test --no-daemon
   ```

## Critérios de aceite

| ID | Critério | Status | Como foi verificado |
|---|---|---|---|
| CA-01 | `FieldErrorResource` e `ErrorResource` são `record` e compilam | ✅ | `grep "^public record"` em 2 arquivos |
| CA-02 | Anotações Jackson preservadas (`@JsonSerialize`, `@JsonRootName("errors")`, `@JsonIgnoreProperties`) | ✅ | Linhas 17-19 de `ErrorResource.java` |
| CA-03 | Respostas 422 continuam com o envelope `{"errors": {...}}` | ✅ | 2 testes 422 PASSED individualmente |
| CA-04 | Nenhuma das duas classes tem `import lombok.*` | ✅ | `grep "import lombok"` não retorna nada |
| CA-05 | Call-sites atualizados (10 acessos em 2 arquivos) | ✅ | `ErrorResourceSerializer` (4) + `GraphQLCustomizeExceptionHandler` (6) |
| CA-06 | `./gradlew test` 100 % verde | ✅ | 115/115 PASSED |

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
| PR | [#96](https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/pull/96) |
| Branch | `refactor/us-06.04-records-exception-resources` |
| Commit | `a14a331` |
| Diff | 4 arquivos, +26 / −27 |

---
*Esta evidência foi criada de acordo com a US-06.04 e segue o modelo de evidência usado nas histórias anteriores.*
