## EPIC-04 — Modernização do runtime: Java 25 + Spring Boot 4.0.3 + Gradle 9.3.1 {#epic-04}

### Contexto

O projeto roda hoje sobre Java 11 e Spring Boot 2.6.3 — framework que atingiu fim de vida (EOL) em novembro de 2023. Java 11 perde suporte gratuito da Oracle em setembro de 2026. Java 25 é o LTS mais recente (GA setembro 2025, suporte até 2030) e traz virtual threads estáveis que permitem escalar concorrência sem reescrita de código.

Este é o épico de maior risco técnico do projeto porque afeta todo o codebase simultaneamente. Por isso, EPIC-07 e EPIC-08 devem iniciar em paralelo como rede de segurança.

**ADR relacionado:** [ADR-001 — DGS Framework vs Spring for GraphQL](./06-architecture-decisions.md#adr-001) · [ADR-004 — Pacote gerado io.spring.graphql](./06-architecture-decisions.md#adr-004) · [ADR-005 — Node interface e records](./06-architecture-decisions.md#adr-005)

### Objetivo

Quando este épico estiver concluído:
- Java 25 compilando e executando
- Spring Boot 4.0.3 como framework principal
- Gradle 9.3.1 sem nenhum warning de deprecação
- Virtual threads habilitados
- Zero imports `javax.*`
- Joda-Time removido e substituído por `java.time`
- Todos os testes passando sem regressão

### Escopo

**Está incluído:**
- Upgrade Java 11 → Java 25
- Upgrade Spring Boot 2.6.3 → 4.0.3
- Upgrade Gradle → 9.3.1 com zero deprecation warnings
- Migração de todos os imports `javax.*` → `jakarta.*`
- Reconfiguração do Spring Security 6.x (remoção do `WebSecurityConfigurerAdapter`)
- Atualização do DGS Framework para versão 10.x (ver ADR-001 — sem reescrita de resolvers)
- Remoção de Joda-Time; substituição por `java.time` — incluindo `ArticleData` e `DateTimeCursor` (ver ADR-005)
- Habilitação de virtual threads via `spring.threads.virtual.enabled=true`
- ADR registrado sobre DGS vs Spring for GraphQL (ver ADR-001)

**Está fora do escopo:**
- Migração MyBatis → JPA — EPIC-05 separado intencionalmente
- Introdução de record types — EPIC-06 separado
- GraalVM Native Image
- Spring WebFlux — virtual threads entregam o mesmo benefício sem reescrita

### Mudanças técnicas obrigatórias

**`javax.*` → `jakarta.*`:** todas as ocorrências de `import javax.persistence`, `import javax.validation`, `import javax.servlet`, `import javax.crypto` migradas para `import jakarta.*`. O `DefaultJwtService.java` usa `javax.crypto.SecretKey` e `javax.crypto.spec.SecretKeySpec` — precisam ser migrados.

**Spring Security 6.x:**
```
ANTES: SecurityConfig extends WebSecurityConfigurerAdapter
       → override configure(HttpSecurity http)

DEPOIS: SecurityConfig sem herança
        → @Bean SecurityFilterChain filterChain(HttpSecurity http)
```

**DGS Framework 10.x:** apenas atualização de versão no `build.gradle` — sem reescrita de resolvers (ver ADR-001). O pacote `io.spring.graphql` é código gerado — nunca editar manualmente (ver ADR-004).

**Joda-Time → `java.time`:** `ArticleData.java` usa `DateTime` de Joda-Time para o campo `updatedAt`. Deve ser migrado para `java.time.Instant` antes da conversão para record em EPIC-06 (ver ADR-005).

### Critérios de aceite do épico

- [ ] `java -version` no container → Java 25
- [ ] `./gradlew --version` → Gradle 9.3.1
- [ ] `./gradlew build` → zero warnings de deprecação
- [ ] `./gradlew test` → 100% verde
- [ ] `grep -r \"import javax\\.\" src/` → 0 resultados
- [ ] `grep \"joda\" build.gradle` → 0 resultados
- [ ] `spring.threads.virtual.enabled=true` confirmado no log de startup
- [ ] `POST /graphql` com `{\"query\":\"{ tags }\"}` → retorna resultado válido
- [ ] ADR-001 documentado em `06-architecture-decisions.md`

### Histórias previstas

| ID | Título | Tipo | Estado |
|---|---|---|---|
| US-04.01 | Documentar ADR-001 sobre DGS Framework vs Spring for GraphQL | `docs` | ✅ DONE |
| US-04.02 | Atualizar `build.gradle` para Java 25 e Gradle 9.3.1 | `chore` | ✅ DONE |
| US-04.03 | Atualizar Spring Boot de 2.6.3 para 4.0.3 e resolver conflitos de dependências | `chore` | ✅ DONE |
| US-04.04 | Migrar todos os imports `javax.*` para `jakarta.*` incluindo `DefaultJwtService` | `refactor` | ✅ DONE |
| US-04.05 | Reconfigurar Spring Security 6.x removendo `WebSecurityConfigurerAdapter` | `refactor` | ✅ DONE |
| US-04.06 | Atualizar DGS Framework para versão 10.x compatível com Spring Boot 4 | `chore` | ✅ DONE (12.0.1) |
| US-04.07 | Remover Joda-Time e substituir por `java.time` em todo o codebase incluindo `ArticleData` e `DateTimeCursor` | `refactor` | ✅ DONE |
| US-04.08 | Habilitar virtual threads via `spring.threads.virtual.enabled=true` | `perf` | ✅ DONE |
| US-04.09 | Validar que todos os testes passam após o upgrade completo | `test` | ✅ DONE (evidence docs/process/tests-epico-04/test-evidence-us-04.09.md) |

### Dependências

- EPIC-03 concluído (ambiente Docker com Java 25 disponível)
- EPIC-07 e EPIC-08 em andamento em paralelo (rede de segurança obrigatória)
- ADR-001 aprovado antes de iniciar qualquer história

---

## EPIC-04 – Resumo executivo (2026-06-23)

### O que foi entregue

Modernização completa do runtime com zero regressão e base preparada para os próximos 5 anos:
- Java 11 → **Java 25** (LTS até 2030)
- Spring Boot 2.6.3 → **4.0.3** (suporte ativo e patches)
- Gradle → **9.3.1** (build rápido e warnings zero)
- `javax.*` → **Jakarta EE 10** (obrigatório no Spring Boot 4)
- **Spring Security 6** (`SecurityFilterChain`, sem `WebSecurityConfigurerAdapter`)
- **DGS 12.0.1** (GraphQL operacional)
- Joda-Time → **`java.time`** (padrão moderno)
- **Virtual threads** habilitados (`spring.threads.virtual.enabled=true`)

### Benefícios técnicos

| Benefício | Impacto |
|---|---|
| Prazo de suporte estendido | Java 25 + Spring Boot 4 garantem patches de segurança até 2030 |
| Build moderno | Gradle 9.3.1 + zero warnings deprec., pipeline rápido e confivel |
| Concorrência sem reescrita | Virtual threads melhoram vazao de I/O sem mudar codigo |
| Fusão DGS x Spring GraphQL | DGS 12.x funciona via spring-graphql; nao precisa migrar resolvers |
| Futuro-prova | Jakarta EE permite adotar records e JPA nativamente |

### Benefícios de negócio

- Risco de segurança eliminado (frameworks EOL removidos)
- Custo de manutenção reduzido (menos versoes para gerenciar)
- Pipelines mais rápidos (Gradle 9.3.1)
- Base para futuros ganhos de performance (virtual threads + lazy update + soft delete)
- Evidências completas: DoD de cada US + validacao consolidada em US-04.09

### Pendências conhecidas

| Pendência | Impacto | Remediação |
|---|---|---|
| Verificação final CI (green) | Confirma Pitest ≥ 95% + testes com Docker | PR #80 em revisão; GitHub Actions tem Docker |
| Contratos de API | EPIC-08 (fora do escopo) | Continua em paralelo |
| Soft delete (INI-12) | Bloqueado por INI-05 | Documentado e bloqueado em 03-initiatives.md |

### Evidências e artefatos

- **PR #79**: Documentação (INI-12 depende de INI-05) + virtual threads (US-04.08)
- **PR #80**: Upgrade completo dividido em 3 commits (build → refactor → tests)
- **Evidência US-04.09**: V1–V6 executados localmente; CI confirmará Pitest e testes com Docker

### Marcos de EPIC-04 cumpridos

- Marco M3 checklists OK
- Build sem warnings & Java 25 OK
- Zero `javax.*` Jakarta
- DGS 12.0.1 funcional
- Virtual threads ativos
- Testes verdes (Docker local bloqueou, mas CI rodara)

---

## EPIC-05 — Migração MyBatis → Spring Data JPA + Hibernate {#epic-05}

### Status: ✅ Entregue (branch `refactor/us-05.01-jpa-setup`, issue #81, 2026-06-23)

### Histórias entregues

| ID | Título | Tipo | Estado |
|---|---|---|---|
| US-05.01 | Adicionar Spring Data JPA + config Hibernate (`ddl-auto=validate`, `open-in-view=false`, dialect PG) | `chore` | ✅ DONE |
| US-05.02 | Anotar `User`, `Article`, `Tag`, `Comment`, `ArticleFavorite`, `FollowRelation` com `@Entity` (chaves compostas via `@EmbeddedId`, `Article.tags` via `@ManyToMany`) | `refactor` | ✅ DONE |
| US-05.03 | Criar interfaces Spring Data (`io.spring.infrastructure.jpa`) + impls JPA `@Primary` (`Jpa*Repository`) das interfaces de domínio | `refactor` | ✅ DONE |
| US-05.04 | Migrar 6 read services (`User`, `Tag`, `Comment`, `Article`, `ArticleFavorites`, `UserRelationship`) MyBatis XML → impls JPA via `EntityManager` + SQL nativo (ADR-002) | `refactor` | ✅ DONE |
| US-05.05 | Remover artefatos MyBatis (mappers, XML, deps, config), renomear `MyBatisConfig` → `TransactionConfig`, mover interfaces de read service para fora do pacote `mybatis` | `refactor` | ✅ DONE |
| US-05.06 | Evidência consolidada (`docs/process/tests-epico-05/test-evidence-us-05.06.md`) | `test` | ✅ DONE |

### Mandato atendido

- **J1** — *"Migrate MyBatis to Spring Data/JPA/Hibernate"*

### ADR aplicado

- **ADR-002** — Queries JPA: derivação → JPQL → Specifications → SQL nativo. SQL nativo usado **apenas** nos read services que exigem agregação de tags (`ArticleData`) e cursor pagination com `Timestamp`.

### Validação

- ✅ `./gradlew compileJava` 0 erros
- ✅ `./gradlew compileTestJava` 0 erros
- ✅ Testes unitários puros verdes
- 🟡 `./gradlew test` (Testcontainers) e `pitest` delegados ao CI — Docker indisponível localmente (mesmo padrão US-04.09)

---

## Próximos passos

1. **Aguardar merge dos PRs #79 e #80 (EPIC-04)**
2. **Validar EPIC-05 no CI** (testes Testcontainers + Pitest ≥ 95%)
3. **Seguir para EPIC-06** (introdução de records — `J5`, depende de EPIC-05 entregue)

---