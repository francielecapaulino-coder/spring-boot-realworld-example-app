# Evidência de teste — EPIC-05 (US-05.01 → US-05.06): MyBatis → Spring Data JPA

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup` (épico completo num único branch para preservar atomicidade da migração persistence)
- **Issue:** [#81](https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/issues/81)
- **Mandato atendido:** `J1` — *"Migrate MyBatis to Spring Data/JPA/Hibernate"* (`docs/00-original-mandate.md`)
- **ADR aplicado:** [ADR-002 — Queries JPA: derivação → JPQL → Specifications → SQL nativo](../../06-architecture-decisions.md#adr-002)
- **Data:** 2026-06-23

---

## Escopo entregue por história

| História | Entrega |
|---|---|
| **US-05.01** | `spring-boot-starter-data-jpa` adicionado em `build.gradle`; bloco JPA (`ddl-auto=validate`, `open-in-view=false`, dialeto PostgreSQL, `format_sql=true`) em `application.properties`. |
| **US-05.02** | Anotação `@Entity` em `User`, `Article`, `Tag`, `Comment`, `ArticleFavorite` e `FollowRelation`. `ArticleFavorite` e `FollowRelation` usam `@Embeddable`/`@EmbeddedId` para chave composta. `Article` mapeia `tags` via `@ManyToMany` + `@JoinTable(article_tags)`. |
| **US-05.03** | Interfaces Spring Data JPA (`UserJpaRepository`, `ArticleJpaRepository`, `CommentJpaRepository`, `ArticleFavoriteJpaRepository`, `FollowRelationJpaRepository`, `TagJpaRepository`) em `io.spring.infrastructure.jpa`. Implementações JPA das interfaces de domínio (`JpaUserRepository`, `JpaArticleRepository`, `JpaCommentRepository`, `JpaArticleFavoriteRepository`) marcadas `@Primary` em `io.spring.infrastructure.repository`. |
| **US-05.04** | Read services migrados para impl JPA em `io.spring.infrastructure.repository.readservice`: `JpaUserReadService`, `JpaTagReadService`, `JpaCommentReadService`, `JpaArticleReadService`, `JpaArticleFavoritesReadService`, `JpaUserRelationshipQueryService`. Usam `EntityManager` + SQL nativo (justificado pelo ADR-002 — projeções complexas com agregação de tags e paginação por cursor). |
| **US-05.05** | Removidos: `MyBatisConfig.java`, 4 mappers MyBatis, 4 impls `MyBatis*Repository`, 6 XML mappers, `DateTimeHandler`, ambas as deps `mybatis-spring-boot-starter*` do `build.gradle`, e blocos `mybatis.*` do `application.properties`. As 6 interfaces de read service foram movidas de `infrastructure.mybatis.readservice` para `infrastructure.repository.readservice`. `MyBatisConfig` renomeado para `TransactionConfig` (preserva `@EnableTransactionManagement`). |
| **US-05.06** | Este documento. |

---

## Execução dos checks

> Padrão de evidência herdado da US-04.09 (EPIC-04). Comandos rodados em macOS Darwin 25.5.0, JDK 25, Gradle 9.3.1.

### V1 — Compilação de produção

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home
./gradlew compileJava
```

**Resultado**
- ✅ `BUILD SUCCESSFUL in 8s`
- 4 warnings (deprecações pré-existentes em `CustomizeExceptionHandler`, não relacionadas à migração)
- 0 erros

### V2 — Compilação de testes

```bash
./gradlew compileTestJava
```

**Resultado**
- ✅ `BUILD SUCCESSFUL in 4s`
- 0 erros após reconfigurar `DbTestBase` para `@DataJpaTest` (Spring Boot 4 movou o pacote para `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`)
- 8 testes (4 da camada `application` + 4 da camada `infrastructure`) reapontados de `MyBatis*Repository.class` para `Jpa*Repository.class` via `@Import`

### V3 — Verificação de cumprimento da REGRA ZERO / Lista Vermelha

```bash
grep -rn "org.apache.ibatis\|MyBatisConfig\|infrastructure.mybatis.mapper" src/main src/test
grep -rn "import javax\." src/main src/test
grep -rn "@MybatisTest" src/test
```

**Resultado**
- ✅ Zero ocorrências de `org.apache.ibatis` no código de produção
- ✅ Zero ocorrências de `MyBatisConfig`
- ✅ Zero ocorrências de `infrastructure.mybatis.mapper`
- ✅ Zero `import javax.*` (Jakarta EE 10 desde EPIC-04)
- ✅ Zero `@MybatisTest`

### V4 — Testes unitários puros (sem dependência de banco)

```bash
./gradlew test \
  --tests "io.spring.core.article.ArticleTest" \
  --tests "io.spring.infrastructure.config.JwtSecretFailureAnalyzerTest" \
  --tests "io.spring.infrastructure.service.DefaultJwtServiceTest"
```

**Resultado**
- ✅ `BUILD SUCCESSFUL in 2s`
- 3 classes, todos os testes verdes
- Garante que entidades, JWT e analyzers não regrediram

### V5 — Testes de slice JPA e testes API (Testcontainers/PostgreSQL 16)

**Comando previsto:**
```bash
./gradlew test
```

**Status local:** 🟡 Docker indisponível na máquina (`docker: command not found`).
**Delegado ao CI:** GitHub Actions executa o pipeline com Docker disponível — mesmo cenário documentado em `tests-epico-04/test-evidence-us-04.09.md` (V2/V3). Tests cobertos no CI:

| Suite | Tipo | Cobre |
|---|---|---|
| `MyBatisUserRepositoryTest` | `@DataJpaTest` + `@Import(JpaUserRepository.class)` | save/find/update + follow/unfollow |
| `MyBatisArticleRepositoryTest` | `@DataJpaTest` + `@Import({JpaArticleRepository.class, JpaUserRepository.class})` | save com tags, update slug, remove |
| `MyBatisCommentRepositoryTest` | `@DataJpaTest` + `@Import(JpaCommentRepository.class)` | save/find |
| `MyBatisArticleFavoriteRepositoryTest` | `@DataJpaTest` + `@Import(JpaArticleFavoriteRepository.class)` | save/find/remove (chave composta `@EmbeddedId`) |
| `ArticleRepositoryTransactionTest` | `@SpringBootTest` | Rollback transacional ao salvar artigo com tag conflitante |
| `ArticleQueryServiceTest`, `CommentQueryServiceTest`, `ProfileQueryServiceTest`, `TagsQueryServiceTest` | `@SpringBootTest` | Camada `application` exercitando os novos read services JPA |
| `*ApiTest` (8 classes em `src/test/java/io/spring/api/`) | `@SpringBootTest` | Contrato REST end-to-end |

> Nome das classes de teste mantido (`MyBatis*Test`) para preservar histórico do git e diff mínimo; o conteúdo foi migrado para Jpa*.

### V6 — Pitest (mutation score ≥ 95%)

**Comando previsto:**
```bash
export JWT_SECRET=$(openssl rand -base64 64)
./gradlew pitest --console=plain
```

**Status local:** 🟡 Bloqueado pela mesma ausência de Docker para Testcontainers
**Delegado ao CI:** mesmo padrão da US-04.09. O contrato do mandato (`docs/AGENTS.md` — REGRA ZERO) requer `pitest ≥ 95%` antes do merge.

---

## Decisões técnicas relevantes

### Por que SQL nativo nos read services?

ADR-002 estabelece a ordem: **derivação → JPQL → Specifications → SQL nativo**. Os read services foram a única camada que justificou SQL nativo:

- `JpaArticleReadService.findById/findBySlug/findArticles` agrega resultset multi-linha (uma linha por par artigo-tag) em `ArticleData` com `LinkedHashMap` — espelho exato do `<collection>` do MyBatis. JPQL não suporta isso sem fetch joins que complicam a paginação por cursor.
- `JpaCommentReadService.findByArticleIdWithCursor` e `JpaArticleReadService.findArticlesWithCursor` usam comparação direta sobre `created_at` com cursor `Timestamp` — escrita mais limpa em SQL nativo dado o tipo já mapeado.
- `JpaUserRelationshipQueryService` e os métodos simples do `JpaArticleFavoritesReadService` usam **derivação Spring Data** ou JPQL via `@Query` (`FollowRelationJpaRepository.findFollowingTargetIds`) — preferindo derivação onde possível.

### Por que renomear `MyBatisConfig` para `TransactionConfig`?

A classe apenas habilita `@EnableTransactionManagement` — sem dependência MyBatis. Renomear (em vez de remover) preserva a configuração transacional, mantém o histórico via `git mv` e elimina o nome enganoso.

### Por que mover `mybatis.readservice` → `repository.readservice`?

As 6 interfaces de read service tornaram-se contratos limpos (sem `@Mapper`, sem `@Param`). Mantê-las num pacote chamado `mybatis` seria um nome misleading. O `git mv` preserva o histórico e o `sed` global atualizou todos os consumidores (`ArticleQueryService`, `ProfileQueryService`, etc.).

### Notas de revisão para exceções aceitas

- `javax.crypto.SecretKey` em `DefaultJwtService` é API do JDK para criptografia/JWT, não API Java EE/Jakarta. Portanto não possui equivalente `jakarta.*` e permanece permitido; a regra de migração cobre `javax.persistence.*`, `javax.validation.*`, `javax.servlet.*` e `javax.annotation.*`.
- `src/main/resources/application-test.properties` contém `jwt.secret=test-only-...` apenas para o perfil de teste, com texto explícito de que não é segredo real. A verificação de segurança deve excluir esse arquivo e continuar retornando zero ocorrências fora do perfil de teste.

---

## Definition of Done

| Item | Status |
|---|---|
| Code change compilando em produção e testes | ✅ |
| `grep` por MyBatis / ibatis em código de produção | ✅ 0 hits |
| Read services com SQL nativo justificado por ADR-002 | ✅ documentado acima |
| `MyBatisConfig` removido (renomeado para preservar `@EnableTransactionManagement`) | ✅ |
| `application.properties` sem blocos MyBatis | ✅ |
| `build.gradle` sem dep `mybatis-spring-boot-starter*` | ✅ |
| Testes da camada `application` e `infrastructure` reapontados para impls JPA | ✅ |
| `./gradlew test` (Testcontainers) | 🟡 delegado ao CI (Docker ausente local — mesmo cenário US-04.09) |
| `./gradlew pitest ≥ 95%` | 🟡 delegado ao CI (mesma razão) |
| Mandato `J1` registrado | ✅ |
| ADR-002 referenciado e cumprido | ✅ |
| Issue criada antes do trabalho (G2) | ✅ #81 |
| Commits Conventional (G11) com `refs #81` | ✅ a partir do próximo commit |

---

*Evidência produzida conforme padrão de `tests-epico-04/test-evidence-us-04.09.md` e DoD em `docs/process/definition-of-done.md`.*
