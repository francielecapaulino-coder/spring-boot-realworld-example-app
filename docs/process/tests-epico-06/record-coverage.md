# Cobertura de records — EPIC-06 (US-06.06)

Validação final do critério de saída KR1.5 do EPIC-06: **≥ 80 % dos DTOs e value objects elegíveis convertidos para `record`**. Este documento consolida o inventário e o cálculo do threshold após o merge das US-06.01 (#98), US-06.02 (#95), US-06.03 (#97) e US-06.04 (#96) e da análise pós-EPIC-05 da US-06.05.

## Procedimento de verificação

1. **Listar records de produção** em todos os pacotes candidatos:
   ```bash
   grep -r "^public record" src/main/java/io/spring/ --include="*.java" -l
   ```
2. **Verificar Lombok residual** nos pacotes que abrigam DTOs convertidos:
   ```bash
   grep -rn "import lombok\." \
     src/main/java/io/spring/application/data/ \
     src/main/java/io/spring/application/user/ \
     src/main/java/io/spring/application/article/ \
     src/main/java/io/spring/api/exception/ --include="*.java"
   ```
3. **Suite verde** sem warnings:
   ```bash
   ./gradlew clean test --no-daemon
   ```

## Inventário pós-EPIC-06

| Classe | Pacote | Status | Conta no denominador? | US que converteu |
|---|---|---|---|---|
| `ProfileData` | `application.data` | `record` | ✅ | US-06.01 (#98) |
| `ArticleData` | `application.data` | `record` (`Node`, `getCursor()` explícito) | ✅ | US-06.01 (#98) |
| `CommentData` | `application.data` | `record` (`Node`, `getCursor()` explícito) | ✅ | US-06.01 (#98) |
| `UserData` | `application.data` | `record` | ✅ | US-06.02 (#95) |
| `UserWithToken` | `application.data` | `record` (construtor secundário preservado) | ✅ | US-06.02 (#95) |
| `ArticleDataList` | `application.data` | `record` (`@JsonProperty` nos componentes) | ✅ | US-06.02 (#95) |
| `ArticleFavoriteCount` | `application.data` | `record` | ✅ | US-06.02 (#95) |
| `NewArticleParam` | `application.article` | `record` (`@JsonRootName("article")`, `@NotBlank`, `@DuplicatedArticleConstraint`) | ✅ | US-06.03 (#97) |
| `UpdateArticleParam` | `application.article` | `record` (compact constructor para defaults `""`) | ✅ | US-06.03 (#97) |
| `RegisterParam` | `application.user` | `record` (`@JsonRootName("user")`, `@NotBlank`, `@Email`, `@DuplicatedEmailConstraint`, `@DuplicatedUsernameConstraint`) | ✅ | US-06.03 (#97) |
| `UpdateUserCommand` | `application.user` | `record` (`@UpdateUserConstraint` class-level preservado) | ✅ | US-06.03 (#97) |
| `ErrorResource` | `api.exception` | `record` (`@JsonSerialize`, `@JsonRootName("errors")` preservados) | ✅ | US-06.04 (#96) |
| `FieldErrorResource` | `api.exception` | `record` (`@JsonIgnoreProperties` preservado) | ✅ | US-06.04 (#96) |
| `UpdateUserParam` | `application.user` | `class` Lombok | ❌ (exclusão justificada: `@Builder.Default` para defaults mutáveis) | — |
| `Page` | `application` | `class` Lombok | ❌ (estado mutável necessário; validação em setters) | — |
| `CursorPageParameter` | `application` | `class` Lombok | ❌ (estado mutável necessário) | — |
| `Tag` | `core.article` | `@Entity` Lombok | ❌ (`@Entity` é incompatível com record — JPA spec) | — |
| `FollowRelation` | `core.user` | `@Entity` Lombok | ❌ (idem) | — |
| `ArticleFavorite` | `core.favorite` | `@Entity` Lombok | ❌ (idem) | — |
| `User` | `core.user` | `@Entity` Lombok | ❌ (idem; `update(...)` mutável) | — |
| `Article` | `core.article` | `@Entity` Lombok | ❌ (idem; `update(...)` + `delete()` mutáveis) | — |
| `Comment` | `core.comment` | `@Entity` Lombok | ❌ (idem) | — |

## Cálculo do threshold

| Métrica | Valor |
|---|---|
| Records de produção (numerador) | **13** |
| Classes elegíveis no denominador | **13** (todas convertidas; `UpdateUserParam` é a única exclusão justificada do pacote de DTOs, e está fora do denominador conforme INI-06) |
| Threshold KR1.5 | **≥ 80 %** |
| **Threshold atingido** | **100 %** ✅ |

## Lombok residual em pacotes de DTOs

| Arquivo | Linhas com `import lombok.*` | Veredito |
|---|---|---|
| `application/user/UpdateUserParam.java` | sim | ✅ Esperado — classe inelegível, exclusão justificada |
| `application/article/ArticleCommandService.java` | sim (`@AllArgsConstructor` no Service) | ✅ Esperado — não é DTO, é Service |
| Nenhum outro DTO convertido tem import `lombok.*` residual | — | ✅ Confirma critério de saída |

## Resultado da execução

```text
> Task :test

BUILD SUCCESSFUL in 34s
7 actionable tasks: 7 executed

Total tests:    115
Total failures: 0
Total errors:   0
```

## Critérios de aceite atendidos

- [x] Contagem de DTOs elegíveis documentada
- [x] Percentual de records ≥ 80 % calculado (resultado: **100 %**)
- [x] `./gradlew build` limpo sem warnings relacionados a records ou Lombok
- [x] `./gradlew test` 100 % verde (115/115)
- [x] Nenhuma classe convertida tem import `lombok.*` residual
- [x] Evidência publicada em `docs/process/tests-epico-06/`

---
*Esta evidência foi criada de acordo com a US-06.06 e segue o modelo de evidência usado nas histórias anteriores.*
