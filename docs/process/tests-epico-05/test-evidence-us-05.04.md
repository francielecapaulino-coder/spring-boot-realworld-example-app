# Evidência de teste — US-05.04: Migrar read services para JPA

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup`
- **Issue:** #81
- **Data:** 2026-06-23
- **Escopo da história:** substituir read services XML/mapper por implementações JPA/EntityManager mantendo contratos de consulta.

## Artefatos verificados

- `JpaArticleFavoritesReadService`
- `JpaArticleReadService`
- `JpaCommentReadService`
- `JpaTagReadService`
- `JpaUserReadService`
- `JpaUserRelationshipQueryService`

## Checks executados

| Check | Resultado |
|---|---|
| 6 read services JPA presentes em `infrastructure/repository/readservice` | ✅ |
| Referências MyBatis/ibatis sob `src/main` e `src/test` | ✅ 0 ocorrências após limpeza de comentários/test names |
| XML mappers sob `src` | ✅ 0 arquivos |
| `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |
| Suíte de query services (`ArticleQueryServiceTest`, etc.) | 🟡 incluída no full test; bloqueada localmente por PostgreSQL/Testcontainers sem Docker |

## Nota ADR-002

`JpaArticleReadService` usa SQL nativo via `EntityManager` para projeções complexas com agregação de tags e cursor pagination. Essa exceção está documentada na evidência consolidada US-05.06 como aplicação do ADR-002.

## Conclusão

US-05.04 está validada para estrutura, compilação e remoção de dependência MyBatis no código fonte. A paridade funcional completa das queries fica para CI com Testcontainers.
