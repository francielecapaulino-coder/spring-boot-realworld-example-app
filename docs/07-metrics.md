# 07 — Metrics
> Versão 1.0 · Junho 2026  
> Projeto: RealWorld Platform Modernization  
> Responsável: Product Management

---

## O que é este documento

Este documento é a **fonte única de verdade para métricas** do projeto. Ele consolida todos os indicadores definidos no Product Vision (OKRs), no Roadmap (marcos) e no Backlog (critérios de aceitação) em um único lugar verificável.

Cada métrica tem:
- **Valor atual (AS IS):** confirmado contra o repositório real
- **Meta (TO BE):** o que precisa ser verdadeiro para o marco ser atingido
- **Como medir:** ferramenta, comando ou dashboard exato
- **Frequência:** com que regularidade medir
- **Marco e OKR:** rastreabilidade para o contexto de onde a métrica veio

> **Para vibe coding:** antes de declarar qualquer história concluída, as métricas correspondentes devem ser verificadas. O DoD de cada épico no `05-backlog.md` aponta para as métricas deste documento.

---

## Sumário

1. [Painel executivo — visão de negócio](#1-painel-executivo--visão-de-negócio)
2. [Métricas de segurança](#2-métricas-de-segurança)
3. [Métricas de modernização do stack](#3-métricas-de-modernização-do-stack)
4. [Métricas de qualidade de testes](#4-métricas-de-qualidade-de-testes)
5. [Métricas de experiência do desenvolvedor](#5-métricas-de-experiência-do-desenvolvedor)
6. [Métricas de observabilidade](#6-métricas-de-observabilidade)
7. [Métricas de governança e processo](#7-métricas-de-governança-e-processo)
8. [Métricas de produto](#8-métricas-de-produto)
9. [Métricas de runtime — operação](#9-métricas-de-runtime--operação)
10. [Consolidado por OKR](#10-consolidado-por-okr)
11. [Consolidado por marco](#11-consolidado-por-marco)

---

## 1. Painel executivo — visão de negócio

> *Para stakeholders. Métricas que respondem: "estamos em risco?" e "o projeto está evoluindo?"*

### 1.1 Scorecard de risco — segurança

| Indicador | AS IS | Meta | Status |
|---|---|---|---|
| Vulnerabilidades críticas conhecidas | **2** (jwt.secret + jwt.sessionTime expostos) | **0** | 🔴 |
| Versões de runtime sem suporte ativo | **3/3** (Java 11, Spring Boot 2.6.3, sem Gradle versão fixada) | **0/3** | 🔴 |
| Secrets hardcoded no repositório | **2** | **0** | 🔴 |
| Framework principal em EOL | **Sim** (Spring Boot 2.6.3 — EOL nov/2023) | **Não** | 🔴 |

**Interpretação para gestão:** o projeto acumula 4 indicadores críticos de risco que precisam ser resolvidos antes de qualquer uso em produção.

---

### 1.2 Scorecard de capacidade

| Indicador | AS IS | Meta | Status |
|---|---|---|---|
| Usuários simultâneos suportados (banco) | **< 10** (SQLite) | **Sem limite prático** (PostgreSQL) | 🔴 |
| Tempo de onboarding de novo dev | **1–2 semanas** (estimado) | **≤ 15 minutos** | 🔴 |
| Tempo de diagnóstico de incidente | **Horas a dias** (sem observabilidade) | **≤ 15 minutos** (Gameday validado) | 🔴 |
| Endpoints com documentação formal | **0/19** | **19/19** | 🔴 |

---

### 1.3 Scorecard de entrega de produto

| Funcionalidade | AS IS | Meta | Marco |
|---|---|---|---|
| Soft delete de artigos | ❌ Não existe — hard delete | ✅ Implementado e testado | M5 |
| Soft delete de comentários | ❌ Não existe — hard delete | ✅ Implementado e testado | M5 |
| Tempo estimado de leitura | ❌ Não existe | ✅ Calculado em 200 wpm | M5 |
| Cache lazy do tempo de leitura | ❌ Não existe | ✅ Lazy update em GET /articles/:slug | M5 |

---

### 1.4 Evolução por marco

| Marco | Fase | Indicador principal | Meta verificável |
|---|---|---|---|
| **M0** | Fase 0 | Processo estabelecido | CI rejeita commit fora do padrão |
| **M1** | Fase 1 | Ambiente e segurança | `docker compose up` + 0 secrets no código |
| **M3** | Fase 3 | Stack modernizada | Build sem warnings + 0 javax + 0 MyBatis |
| **MT** | Fase Testes | Qualidade verificável | Pitest ≥ 95% + 19+18 contratos cobertos |
| **M4** | Fase 4 | Sistema observável | Gameday 1 ≤ 15 min + Swagger 19/19 |
| **M5** | Fase 5 | Produto evoluído | Soft delete + leitura funcionando |
| **M6** | Fase 6 | Projeto concluído | Pitest ≥ 95% global + Coda 100% |

---

## 2. Métricas de segurança

> **OKR:** OKR 6 (KR6.5) · **Marco:** M1 · **Épico:** EPIC-02

### M-SEC-01 — Secrets hardcoded no repositório

| Campo | Valor |
|---|---|
| **Descrição** | Número de valores de secrets hardcoded detectados no repositório por scan automático |
| **AS IS** | 2 (`jwt.secret=mySecretKey` e `jwt.sessionTime=86400` em `application.properties`) |
| **Meta** | **0** |
| **Como medir** | `truffleHog scan .` — executa no CI como primeiro step |
| **Frequência** | A cada push (gate do CI) |
| **Acionável quando** | > 0 findings → CI bloqueia o merge automaticamente |

---

### M-SEC-02 — `JWT_SECRET` configurada via ambiente

| Campo | Valor |
|---|---|
| **Descrição** | A aplicação inicia corretamente quando `JWT_SECRET` é fornecida via variável de ambiente e falha com mensagem clara quando ausente |
| **AS IS** | Não — valor hardcoded no código |
| **Meta** | **Sim** — fail-fast sem a variável; inicia normalmente com ela |
| **Como medir** | Teste manual: `./gradlew bootRun` sem `JWT_SECRET` → verificar mensagem de erro; com `JWT_SECRET=valor` → verificar startup |
| **Frequência** | Uma vez ao concluir EPIC-02; validado no script Python de INI-03 |

---

### M-SEC-03 — `JWT_SESSION_TIME` configurada via ambiente

| Campo | Valor |
|---|---|
| **Descrição** | A aplicação usa `JWT_SESSION_TIME` do ambiente (com fallback de 86400s quando ausente) |
| **AS IS** | Não — valor 86400 hardcoded em `application.properties` |
| **Meta** | **Sim** — usa fallback 86400s se ausente; usa valor configurado se presente |
| **Como medir** | Verificar `application.properties`: deve conter `jwt.sessionTime=${JWT_SESSION_TIME:86400}` |
| **Frequência** | Uma vez ao concluir EPIC-02 |

---

### M-SEC-04 — Imports `javax.crypto` migrados

| Campo | Valor |
|---|---|
| **Descrição** | Ocorrências de `import javax.` no codebase — relevante para `DefaultJwtService.java` |
| **AS IS** | > 0 — `javax.crypto.SecretKey` e `javax.crypto.spec.SecretKeySpec` confirmados |
| **Meta** | **0** |
| **Como medir** | `grep -r "import javax\." src/` |
| **Frequência** | A cada build no CI (reportado no log de build) |
| **Marco** | M3 |

---

## 3. Métricas de modernização do stack

> **OKR:** OKR 1 (KR1.1–KR1.6) · **Marco:** M3 · **Épico:** EPIC-04, EPIC-05, EPIC-06

### M-STACK-01 — Versão do Java em execução

| Campo | Valor |
|---|---|
| **Descrição** | Versão do JDK em execução no container da aplicação |
| **AS IS** | Java 11 (`sourceCompatibility = '11'` confirmado no `build.gradle`) |
| **Meta** | **Java 25** |
| **Como medir** | `java -version` no container · `./gradlew --version` · log de startup da aplicação |
| **Frequência** | Verificado no CI a cada build |
| **Marco** | M3 |

---

### M-STACK-02 — Versão do Spring Boot

| Campo | Valor |
|---|---|
| **Descrição** | Versão do Spring Boot declarada no `build.gradle` |
| **AS IS** | 2.6.3 (EOL novembro de 2023 — confirmado no `build.gradle`) |
| **Meta** | **4.0.6** |
| **Como medir** | `grep "spring-boot" build.gradle` · `./gradlew dependencies \| grep spring-boot-starter` |
| **Frequência** | Verificado no CI a cada build |
| **Marco** | M3 |

---

### M-STACK-03 — Warnings de deprecação no build

| Campo | Valor |
|---|---|
| **Descrição** | Número de warnings de deprecação na saída do `./gradlew build` |
| **AS IS** | Não medido (estimado: vários, dado que Spring Boot 2.6.3 está em EOL) |
| **Meta** | **0** |
| **Como medir** | `./gradlew build 2>&1 \| grep -c "\[WARNING\]\|deprecated"` |
| **Frequência** | A cada build no CI — gate: falha se > 0 |
| **Marco** | M3 |

---

### M-STACK-04 — Referências a MyBatis no codebase

| Campo | Valor |
|---|---|
| **Descrição** | Número de arquivos com referências a MyBatis (mappers Java e XMLs) |
| **AS IS** | > 0 — `MyBatisUserRepository.java`, `UserMapper.java`, `ArticleMapper.xml` e outros confirmados |
| **Meta** | **0** |
| **Como medir** | `grep -r "mybatis" build.gradle` → 0; `find . -name "*Mapper.java"` → 0; `find . -name "*.xml" -path "*/mapper/*"` → 0 |
| **Frequência** | Uma vez ao concluir EPIC-05 |
| **Marco** | M3 |

---

### M-STACK-05 — Imports `javax.*` no codebase (exceto crypto)

| Campo | Valor |
|---|---|
| **Descrição** | Total de ocorrências de `import javax.` no código-fonte (exceto gerado) |
| **AS IS** | > 0 — inclui `javax.crypto` em `DefaultJwtService`, persistence, validation, servlet |
| **Meta** | **0** |
| **Como medir** | `grep -r "import javax\." src/main/java/io/spring/` (excluindo `io/spring/graphql`) |
| **Frequência** | A cada build no CI |
| **Marco** | M3 |

---

### M-STACK-06 — Ocorrências de Joda-Time

| Campo | Valor |
|---|---|
| **Descrição** | Número de referências a Joda-Time no codebase |
| **AS IS** | > 0 — `joda-time:2.10.13` no `build.gradle`; `import org.joda.time.DateTime` em `ArticleData.java` e `DateTimeCursor` confirmados |
| **Meta** | **0** |
| **Como medir** | `grep "joda" build.gradle` → 0; `grep -r "import org.joda" src/` → 0 |
| **Frequência** | Uma vez ao concluir EPIC-04 |
| **Marco** | M3 |

---

### M-STACK-07 — DTOs como record types

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de classes DTO na camada `application` convertidas para record types Java 25 |
| **AS IS** | 0% — todos os DTOs são classes com Lombok (`@Data`, `@AllArgsConstructor`, etc.) |
| **Meta** | **≥ 80%** |
| **Como medir** | Contar arquivos `public record` no pacote `io.spring.application.data` ÷ total de DTOs × 100 |
| **Frequência** | Uma vez ao concluir EPIC-06 |
| **Marco** | M3 |

---

### M-STACK-08 — `getCursor()` explícito em records que implementam `Node`

| Campo | Valor |
|---|---|
| **Descrição** | `ArticleData` (e similares) convertidos para record implementam `Node` com `getCursor()` declarado explicitamente no corpo do record |
| **AS IS** | Não aplicável — `ArticleData` ainda é classe com Lombok |
| **Meta** | **Sim** — `public record ArticleData(...) implements Node { @Override public DateTimeCursor getCursor() {...} }` |
| **Como medir** | Revisão de código + `grep "implements Node" src/` verificando que são records |
| **Frequência** | Uma vez ao concluir EPIC-06, US-06.02 |
| **Marco** | M3 |

---

## 4. Métricas de qualidade de testes

> **OKR:** OKR 2 (KR2.1–KR2.6) · **Marco:** MT · **Épico:** EPIC-07, EPIC-08, EPIC-09

### M-QUAL-01 — Mutation score global (Pitest)

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de mutações introduzidas pelo Pitest que são detectadas pelos testes. Mede a qualidade real dos testes — não apenas a cobertura de linhas. |
| **AS IS** | **0%** — Pitest não configurado |
| **Meta** | **≥ 95%** — gate obrigatório do CI |
| **Como medir** | `./gradlew pitest` → relatório em `build/reports/pitest/index.html` |
| **Frequência** | A cada push (CI) · relatório publicado como artefato |
| **Acionável quando** | < 95% → CI bloqueia o merge automaticamente |
| **Marco** | MT |

**Breakdown esperado por camada:**

| Camada | Pacote | Meta |
|---|---|---|
| Domínio | `io.spring.core.*` | ≥ 95% |
| Casos de uso | `io.spring.application.*` | ≥ 95% |
| Infraestrutura | `io.spring.infrastructure.*` | ≥ 95% |
| Controllers | `io.spring.api.*` | ≥ 95% |
| Código gerado | `io.spring.graphql.*` | **Excluído** (não medir) |

---

### M-QUAL-02 — Cobertura de contratos REST

| Campo | Valor |
|---|---|
| **Descrição** | Número de endpoints REST com pelo menos um teste de integração de sucesso e um de erro, usando PostgreSQL real via Testcontainers |
| **AS IS** | **~7/19** (estimado — testes parciais existem) |
| **Meta** | **19/19 endpoints** — 100% |
| **Como medir** | Contar classes de teste por controller em `src/test/java/io/spring/api/` |
| **Frequência** | A cada build no CI |
| **Marco** | MT |

**Endpoints obrigatórios (19):**

| Domínio | Endpoints | Count |
|---|---|---|
| Auth | `POST /users/login`, `POST /users` | 2 |
| Usuário | `GET /user`, `PUT /user` | 2 |
| Perfis | `GET /profiles/:username`, follow, unfollow | 3 |
| Artigos | `GET /articles`, feed, `GET /articles/:slug`, criar, editar, deletar | 6 |
| Favoritos | favoritar, desfavoritar | 2 |
| Comentários | listar, criar, deletar | 3 |
| Tags | `GET /tags` | 1 |
| **Total** | | **19** |

---

### M-QUAL-03 — Cobertura de operações GraphQL

| Campo | Valor |
|---|---|
| **Descrição** | Número de operações GraphQL com pelo menos um teste de integração de sucesso e um de erro |
| **AS IS** | **~0/18** (estimado — testes GraphQL incompletos) |
| **Meta** | **18/18 operações** — 100% |
| **Como medir** | Contar métodos de teste por query e mutation em `src/test/` |
| **Frequência** | A cada build no CI |
| **Marco** | MT |

**Operações obrigatórias (18):**

| Tipo | Operações | Count |
|---|---|---|
| Queries | `article`, `articles`, `me`, `feed`, `profile`, `tags` | 6 |
| Mutations | `createUser`, `login`, `updateUser`, `followUser`, `unfollowUser`, `createArticle`, `updateArticle`, `favoriteArticle`, `unfavoriteArticle`, `deleteArticle`, `addComment`, `deleteComment` | 12 |
| **Total** | | **18** |

---

### M-QUAL-04 — Fluxos E2E com Playwright

| Campo | Valor |
|---|---|
| **Descrição** | Número de fluxos end-to-end passando no branch `feat/playwright-working` usando Playwright como cliente HTTP (`APIRequestContext`) |
| **AS IS** | **0 fluxos** |
| **Meta MT** | **3 fluxos** (fluxos 1, 2, 3 — independentes de soft delete) |
| **Meta M6** | **5 fluxos** (+ fluxos 4 e 5 após EPIC-12) |
| **Como medir** | `npx playwright test` no branch `feat/playwright-working` → relatório de resultados |
| **Frequência** | A cada push no branch `working` (CI) |
| **Marco** | MT (3 fluxos) → M6 (5 fluxos) |

**Fluxos e dependências:**

| # | Fluxo | Disponível em | Depende de |
|---|---|---|---|
| 1 | Registro e autenticação completa | Fase Testes | EPIC-08 |
| 2 | Criação e leitura de artigo com `readingTimeMinutes` | Fase Testes | EPIC-08 |
| 3 | Interação social (follow, feed, unfollow) | Fase Testes | EPIC-08 |
| 4 | Soft delete de artigo | Fase 6 | EPIC-12 |
| 5 | Soft delete de comentário | Fase 6 | EPIC-12 |

---

### M-QUAL-05 — Branch `playwright-broken` documentado

| Campo | Valor |
|---|---|
| **Descrição** | Branch com testes E2E escritos e falhando existe no repositório (documentação intencional do estado anterior) |
| **AS IS** | Não existe |
| **Meta** | **Existe** — CI vermelho no branch `feat/playwright-broken` |
| **Como medir** | `git branch -r \| grep playwright-broken` |
| **Frequência** | Uma vez ao criar o branch em INI-09 |
| **Marco** | MT |

---

### M-QUAL-06 — Banco real em testes de integração

| Campo | Valor |
|---|---|
| **Descrição** | 100% dos testes de integração usam PostgreSQL real via Testcontainers — nenhum usa H2 ou banco em memória |
| **AS IS** | Não verificado — testes existentes usam SQLite |
| **Meta** | **0 referências a H2 ou SQLite nos testes** |
| **Como medir** | `grep -r "H2\|sqlite\|in-memory" src/test/` → 0 resultados; logs de teste mostram container PostgreSQL sendo iniciado |
| **Frequência** | A cada build no CI |
| **Marco** | MT |

---

## 5. Métricas de experiência do desenvolvedor

> **OKR:** OKR 3 (KR3.1–KR3.5) · **Marco:** M1 · **Épico:** EPIC-02, EPIC-03

### M-DEV-01 — Tempo de onboarding

| Campo | Valor |
|---|---|
| **Descrição** | Tempo em minutos desde `git clone` até `curl http://localhost:8080/tags` retornando JSON, medido com um desenvolvedor que nunca viu o projeto |
| **AS IS** | **~1–2 semanas** (estimado — sem ambiente padronizado) |
| **Meta** | **≤ 15 minutos** |
| **Como medir** | Teste presencial: cronometrar desenvolvedor novo seguindo `CONTRIBUTING.md` do zero. Documentar o resultado com data e nome |
| **Frequência** | Uma vez ao concluir EPIC-03; repetir ao onboarding de cada novo membro real |
| **Marco** | M1 |

**Resultado esperado no M1:**
```
Desenvolvedor: [nome]
Data: [data]
Ambiente: [OS / hardware]
Tempo total: X minutos
Obstáculos encontrados: [lista]
Passos que falharam: [lista ou "nenhum"]
```

---

### M-DEV-02 — Serviços funcionais no `docker compose up`

| Campo | Valor |
|---|---|
| **Descrição** | Número de serviços que atingem estado `healthy` após `docker compose up` sem intervenção manual |
| **AS IS** | **0/6** — Docker Compose não existe |
| **Meta** | **6/6** — app + PostgreSQL + Prometheus + Loki + Tempo + Grafana |
| **Como medir** | `docker compose ps` após o compose — todos em `healthy` |
| **Frequência** | A cada push no CI (script Python valida app) |
| **Marco** | M1 |

---

### M-DEV-03 — Script Python de validação de startup/shutdown

| Campo | Valor |
|---|---|
| **Descrição** | O script `scripts/validate_startup.py` retorna exit code 0 em 100% das execuções — valida os logs estruturados de startup e shutdown |
| **AS IS** | **Não existe** |
| **Meta** | **Exit code 0 em 100% das execuções no CI** |
| **Como medir** | `python scripts/validate_startup.py` → `echo $?` → deve ser `0` |
| **Frequência** | A cada push (gate do CI) |
| **Marco** | M1 |

---

### M-DEV-04 — Perfis de ambiente configurados

| Campo | Valor |
|---|---|
| **Descrição** | Número de perfis Spring configurados com propriedades distintas por ambiente |
| **AS IS** | **0** — configuração única em `application.properties` |
| **Meta** | **3** — `application-dev.yml`, `application-staging.yml`, `application-prod.yml` |
| **Como medir** | `ls src/main/resources/application-*.yml \| wc -l` → deve ser `3` |
| **Frequência** | Uma vez ao concluir EPIC-02 |
| **Marco** | M1 |

---

### M-DEV-05 — Passos do CONTRIBUTING.md executáveis sem erro

| Campo | Valor |
|---|---|
| **Descrição** | Percentual dos passos documentados no `CONTRIBUTING.md` que um desenvolvedor novo consegue executar sem erro e sem precisar de ajuda |
| **AS IS** | **0%** — `CONTRIBUTING.md` não existe |
| **Meta** | **100%** |
| **Como medir** | Walkthrough presencial com desenvolvedor novo: marcar cada passo como "passou" ou "falhou" |
| **Frequência** | Uma vez ao concluir EPIC-03; sempre que o guia for atualizado |
| **Marco** | M1 |

---

## 6. Métricas de observabilidade

> **OKR:** OKR 5 (KR5.1–KR5.6) · **Marco:** M4 · **Épico:** EPIC-10

### M-OBS-01 — Endpoints com contador Micrometer

| Campo | Valor |
|---|---|
| **Descrição** | Número de endpoints REST com contador `api.requests.total` que incrementa a cada chamada |
| **AS IS** | **0/19** — sem Micrometer configurado |
| **Meta** | **19/19** |
| **Como medir** | Fazer uma chamada a cada endpoint → `curl http://localhost:9090/metrics \| grep api_requests_total` → deve mostrar 19 entradas distintas |
| **Frequência** | Verificado no dashboard Grafana em tempo real |
| **Marco** | M4 |
| **Query Prometheus** | `api_requests_total{job="realworld-api"}` |

---

### M-OBS-02 — Log de startup estruturado

| Campo | Valor |
|---|---|
| **Descrição** | Log de startup emitido com campos obrigatórios: `event`, `timestamp`, `version`, `environment`, `port` |
| **AS IS** | **Não existe** — log de startup não estruturado |
| **Meta** | **100% das inicializações** — validado automaticamente pelo script Python no CI |
| **Como medir** | `python scripts/validate_startup.py` verifica presença dos campos → exit 0 |
| **Frequência** | A cada push (gate do CI) |
| **Marco** | M4 |
| **Campos obrigatórios** | `event: "application_startup"`, `timestamp`, `version`, `environment`, `port` |

---

### M-OBS-03 — Log de shutdown estruturado

| Campo | Valor |
|---|---|
| **Descrição** | Log de shutdown emitido com campos obrigatórios: `event`, `timestamp`, `reason` |
| **AS IS** | **Não existe** |
| **Meta** | **100% dos encerramentos** — validado automaticamente pelo script Python no CI |
| **Como medir** | `python scripts/validate_startup.py` verifica presença dos campos → exit 0 |
| **Frequência** | A cada push (gate do CI) |
| **Marco** | M4 |

---

### M-OBS-04 — Requisições com traces

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de requisições HTTP visíveis no Grafana/Tempo com trace completo |
| **AS IS** | **0%** — sem OpenTelemetry configurado |
| **Meta** | **100%** |
| **Como medir** | Fazer chamada ao sistema → abrir Grafana → Explore → Tempo → verificar trace da requisição |
| **Frequência** | Verificado durante Gameday 1 e Gameday 2 |
| **Marco** | M4 |

---

### M-OBS-05 — Gameday 1: tempo de diagnóstico baseline

| Campo | Valor |
|---|---|
| **Descrição** | Tempo em minutos para a equipe localizar a causa raiz de um problema simulado no sistema baseline (sem as novas features de produto) usando apenas os dashboards Grafana, logs e traces disponíveis |
| **AS IS** | **Não medido** — sem observabilidade |
| **Meta** | **≤ 15 minutos** |
| **Como medir** | Exercício presencial: PM injeta um problema simulado (ex: endpoint retornando erro) sem avisar o time; time usa Grafana/Loki/Tempo para diagnosticar; PM cronometa e documenta o resultado |
| **Frequência** | **Uma vez** ao concluir EPIC-10 (Gameday 1) |
| **Marco** | M4 |

**Resultado esperado no M4:**
```
Gameday 1 — Sistema baseline
Data: [data]
Problema simulado: [descrição]
Tempo de detecção: X minutos
Tempo de diagnóstico: Y minutos
Ferramentas usadas: [Grafana / Loki / Tempo]
Resultado: PASSOU / FALHOU
Observações: [lista]
```

---

### M-OBS-06 — Gameday 2: tempo de diagnóstico com novas features

| Campo | Valor |
|---|---|
| **Descrição** | Tempo em minutos para a equipe localizar a causa raiz de um problema simulado no sistema completo — com soft delete e tempo de leitura em operação |
| **AS IS** | Não aplicável (features não existem) |
| **Meta** | **≤ 15 minutos** |
| **Como medir** | Mesmo procedimento do Gameday 1, mas com soft delete e tempo de leitura em operação |
| **Frequência** | **Uma vez** na Fase 6 (Gameday 2) |
| **Marco** | M6 |

---

## 7. Métricas de governança e processo

> **OKR:** OKR 6 (KR6.1–KR6.7) · **Marco:** M0, M4 · **Épico:** EPIC-01, EPIC-11

### M-GOV-01 — Conventional Commits: taxa de adoção

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de commits no repositório que seguem o padrão Conventional Commits após a configuração do commitlint |
| **AS IS** | **0%** — sem padrão estabelecido |
| **Meta** | **100%** — CI rejeita automaticamente o que estiver fora do padrão |
| **Como medir** | `git log --oneline \| head -20` — verificar formato dos últimos commits; commitlint no CI garante 100% dos commits novos |
| **Frequência** | Contínuo — gate do CI a cada push |
| **Marco** | M0 |

---

### M-GOV-02 — PRs com issue vinculada

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de Pull Requests abertas após M0 que têm uma issue do GitHub vinculada |
| **AS IS** | **0%** — sem processo estabelecido |
| **Meta** | **100%** |
| **Como medir** | Revisar PRs abertas no GitHub — template de PR exige link para issue como item obrigatório do checklist |
| **Frequência** | Revisado a cada PR — verificação manual pela PM antes de aprovar |
| **Marco** | M0 |

---

### M-GOV-03 — PRs abertas sem revisão

| Campo | Valor |
|---|---|
| **Descrição** | Número de PRs abertas no repositório sem revisão ou resposta |
| **AS IS** | **5** (confirmado no GitHub, junho 2026) |
| **Meta** | **0** |
| **Como medir** | GitHub → Pull Requests → filtrar por "Open" → contar |
| **Frequência** | Semanal |
| **Marco** | M6 |

---

### M-GOV-04 — Endpoints documentados no OpenAPI

| Campo | Valor |
|---|---|
| **Descrição** | Número de endpoints REST com documentação completa no Swagger UI (descrição, request body quando aplicável, response 200 e ao menos um erro) |
| **AS IS** | **0/19** — sem OpenAPI configurado |
| **Meta Fase 4** | **19/19** — endpoints existentes |
| **Meta Fase 6** | **19/19 + campos novos** (`readingTimeMinutes`, nota de soft delete) |
| **Como medir** | `http://localhost:8080/swagger-ui.html` → contar endpoints visíveis; `/v3/api-docs` → validar em `editor.swagger.io` |
| **Frequência** | Uma vez ao concluir EPIC-11 (Fase 4); novamente ao concluir US-11.08 (Fase 6) |
| **Marco** | M4 (setup), M6 (atualizado) |

---

### M-GOV-05 — Etapas documentadas no Coda

| Campo | Valor |
|---|---|
| **Descrição** | Percentual das etapas de desenvolvimento com prompts e skills documentados no Coda |
| **AS IS** | **0%** — Coda não configurado |
| **Meta** | **100%** das etapas de todas as 13 iniciativas |
| **Como medir** | Revisão do workspace Coda — contar etapas com documentação vs total de etapas planejadas |
| **Frequência** | Revisado ao final de cada fase |
| **Marco** | M6 |

---

### M-GOV-06 — DoR e DoD documentadas

| Campo | Valor |
|---|---|
| **Descrição** | Definition of Ready e Definition of Done existem e estão acessíveis no repositório |
| **AS IS** | **Não existem** |
| **Meta** | **Existem** — `docs/process/definition-of-ready.md` e `docs/process/definition-of-done.md` |
| **Como medir** | `ls docs/process/` → dois arquivos |
| **Frequência** | Uma vez ao concluir EPIC-01 |
| **Marco** | M0 |

---

## 8. Métricas de produto

> **OKR:** OKR 4 (KR4.1–KR4.5), OKR 7 (KR7.1–KR7.5) · **Marco:** M5 · **Épico:** EPIC-12, EPIC-13

### M-PROD-01 — Banco de dados em uso

| Campo | Valor |
|---|---|
| **Descrição** | Banco de dados ativo na aplicação |
| **AS IS** | **SQLite** (`sqlite-jdbc:3.36.0.3` — arquivo `dev.db`) |
| **Meta** | **PostgreSQL 16** |
| **Como medir** | `docker compose ps` → verificar serviço `postgres`; `curl http://localhost:8080/actuator/health` → verificar datasource |
| **Frequência** | A cada build no CI |
| **Marco** | M1 |

---

### M-PROD-02 — Hard deletes remanescentes em artigos

| Campo | Valor |
|---|---|
| **Descrição** | Número de chamadas a `repository.delete()` para a entidade `Article` — indica que soft delete não foi implementado corretamente |
| **AS IS** | > 0 — hard delete é o comportamento padrão atual |
| **Meta** | **0** |
| **Como medir** | `grep -r "repository.delete\|articleRepository.delete" src/main/java/io/spring/` → 0 resultados |
| **Frequência** | Uma vez ao concluir EPIC-12 |
| **Marco** | M5 |

---

### M-PROD-03 — Hard deletes remanescentes em comentários

| Campo | Valor |
|---|---|
| **Descrição** | Número de chamadas a `repository.delete()` para a entidade `Comment` |
| **AS IS** | > 0 — hard delete é o comportamento padrão atual |
| **Meta** | **0** |
| **Como medir** | `grep -r "commentRepository.delete" src/main/java/io/spring/` → 0 resultados |
| **Frequência** | Uma vez ao concluir EPIC-12 |
| **Marco** | M5 |

---

### M-PROD-04 — Comportamento do DELETE após soft delete

| Campo | Valor |
|---|---|
| **Descrição** | O endpoint `DELETE /articles/:slug` retorna `204 No Content` após a implementação do soft delete — contrato HTTP não muda |
| **AS IS** | Retorna `204 No Content` (hard delete) |
| **Meta** | **Continua retornando `204 No Content`** (soft delete transparente ao consumidor) |
| **Como medir** | `curl -X DELETE http://localhost:8080/articles/slug -H "Authorization: Token ..." -w "%{http_code}"` → deve retornar `204` |
| **Frequência** | Teste de contrato (EPIC-08) valida a cada build |
| **Marco** | M5 |

---

### M-PROD-05 — Artigos com soft delete auditável

| Campo | Valor |
|---|---|
| **Descrição** | Artigos deletados existem no banco com `is_deleted = true` — auditoria disponível |
| **AS IS** | **Impossível** — hard delete remove o registro permanentemente |
| **Meta** | **Sim** — `SELECT COUNT(*) FROM articles WHERE is_deleted = true` retorna registros após deleções |
| **Como medir** | `psql -h localhost -U postgres -d realworld -c "SELECT COUNT(*) FROM articles WHERE is_deleted = true;"` após deletar artigos |
| **Frequência** | Uma vez ao concluir EPIC-12; periodicamente em operação |
| **Marco** | M5 |

---

### M-PROD-06 — Artigos novos com `readingTimeMinutes` calculado

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de artigos criados via `POST /articles` que incluem `readingTimeMinutes` no response |
| **AS IS** | **0%** — campo não existe |
| **Meta** | **100%** — todo artigo criado deve ter o campo calculado automaticamente |
| **Como medir** | `curl -X POST http://localhost:8080/articles ...` → verificar presença de `readingTimeMinutes` no response JSON |
| **Frequência** | Teste de contrato (EPIC-08) valida a cada build |
| **Marco** | M5 |

---

### M-PROD-07 — Artigos existentes com lazy update resolvido

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de artigos que existiam antes de INI-13 e que já têm `reading_time_minutes` calculado no banco (preenchido via lazy update na primeira leitura) |
| **AS IS** | **0%** — campo não existe no schema |
| **Meta** | **100% após primeira leitura de cada artigo** |
| **Como medir** | `psql -c "SELECT COUNT(*) FROM articles WHERE reading_time_minutes IS NULL;"` → deve aproximar-se de 0 com o tempo à medida que artigos são lidos |
| **Frequência** | Monitorar via query SQL periódica após Fase 5 |
| **Marco** | M5, M6 |

---

### M-PROD-08 — Valor mínimo de `readingTimeMinutes`

| Campo | Valor |
|---|---|
| **Descrição** | Nenhum artigo deve ter `readingTimeMinutes` < 1 (regra de negócio: mínimo de 1 minuto) |
| **AS IS** | Não aplicável |
| **Meta** | **0 artigos com readingTimeMinutes < 1** |
| **Como medir** | `psql -c "SELECT COUNT(*) FROM articles WHERE reading_time_minutes < 1 AND reading_time_minutes IS NOT NULL;"` → deve ser `0` |
| **Frequência** | Teste unitário de `calculateReadingTime()` valida via Pitest a cada build |
| **Marco** | M5 |

---

### M-PROD-09 — Mutation score para código de soft delete

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de mutações introduzidas especificamente no código de soft delete (EPIC-12) que são detectadas pelos testes |
| **AS IS** | **0%** — código não existe |
| **Meta** | **≥ 95%** — medido no relatório Pitest por pacote |
| **Como medir** | `./gradlew pitest` → relatório HTML → filtrar por classes de soft delete |
| **Frequência** | A cada push no CI |
| **Marco** | M5 |

---

### M-PROD-10 — Mutation score para código de tempo de leitura

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de mutações introduzidas no código de tempo de leitura (EPIC-13) que são detectadas pelos testes |
| **AS IS** | **0%** — código não existe |
| **Meta** | **≥ 95%** |
| **Como medir** | `./gradlew pitest` → relatório HTML → filtrar por classes de leitura |
| **Frequência** | A cada push no CI |
| **Marco** | M5 |

---

## 9. Métricas de runtime — operação

> Disponíveis apenas após Fase 4 (observabilidade configurada). Visíveis no Grafana.

### M-RT-01 — Taxa de chamadas por endpoint

| Campo | Valor |
|---|---|
| **Descrição** | Número de chamadas por segundo a cada endpoint REST |
| **Disponível a partir de** | Fase 4 (EPIC-10) |
| **Query Prometheus** | `rate(api_requests_total[5m])` agregado por `endpoint` |
| **Painel Grafana** | "API Calls by Endpoint" — barras por endpoint |
| **Uso** | Identificar endpoints mais usados; detectar picos anômalos de tráfego |

---

### M-RT-02 — Latência p95 por endpoint

| Campo | Valor |
|---|---|
| **Descrição** | 95º percentil do tempo de resposta por endpoint em milissegundos |
| **Disponível a partir de** | Fase 4 (EPIC-10) |
| **Query Prometheus** | `histogram_quantile(0.95, rate(http_server_request_seconds_bucket[5m]))` |
| **Painel Grafana** | "p95 Latency" — linha por endpoint |
| **Threshold de atenção** | > 500ms para endpoints de leitura; > 1000ms para endpoints de escrita |

---

### M-RT-03 — Taxa de erro por endpoint

| Campo | Valor |
|---|---|
| **Descrição** | Percentual de respostas com status 4xx ou 5xx por endpoint |
| **Disponível a partir de** | Fase 4 (EPIC-10) |
| **Query Prometheus** | `rate(api_requests_total{status=~"4..|5.."}[5m]) / rate(api_requests_total[5m])` |
| **Painel Grafana** | "Error Rate" — linha por endpoint |
| **Threshold de atenção** | > 1% de erros 5xx; > 5% de erros 4xx |

---

### M-RT-04 — Eventos de startup e shutdown

| Campo | Valor |
|---|---|
| **Descrição** | Contagem de eventos de startup e shutdown registrados com campos estruturados |
| **Disponível a partir de** | Fase 4 (EPIC-10) |
| **Query Loki** | `{app="realworld-api"} \| json \| event="application_startup"` |
| **Painel Grafana** | "Application Lifecycle Events" |
| **Uso** | Detectar restarts inesperados; confirmar que deploys estão sendo registrados |

---

### M-RT-05 — Artigos com `readingTimeMinutes` nulo (lazy update pendente)

| Campo | Valor |
|---|---|
| **Descrição** | Número de artigos no banco que ainda têm `reading_time_minutes = NULL` — indica lazy update pendente |
| **Disponível a partir de** | Fase 5 (EPIC-13) |
| **Query SQL** | `SELECT COUNT(*) FROM articles WHERE reading_time_minutes IS NULL;` |
| **Tendência esperada** | Decresce com o tempo à medida que artigos são lidos |
| **Uso** | Monitorar cobertura do lazy update; identificar artigos nunca lidos desde Fase 5 |

---

## 10. Consolidado por OKR

| OKR | KR | Métrica | AS IS | Meta | Marco |
|---|---|---|---|---|---|
| **OKR 1** | KR1.1 | Versão Java | Java 11 | Java 25 | M3 |
| **OKR 1** | KR1.2 | Versão Spring Boot | 2.6.3 | 4.0.6 | M3 |
| **OKR 1** | KR1.3 | Warnings no build | N/M | 0 | M3 |
| **OKR 1** | KR1.4 | MyBatis mappers | > 0 | 0 | M3 |
| **OKR 1** | KR1.5 | DTOs como records | 0% | ≥ 80% | M3 |
| **OKR 1** | KR1.6 | Joda-Time | > 0 | 0 | M3 |
| **OKR 2** | KR2.1 | Mutation score | 0% | ≥ 95% | MT |
| **OKR 2** | KR2.2 | Endpoints REST cobertos | ~7/19 | 19/19 | MT |
| **OKR 2** | KR2.3 | Operações GraphQL cobertas | ~0/18 | 18/18 | MT |
| **OKR 2** | KR2.4 | Fluxos Playwright | 0 | 5 | MT→M6 |
| **OKR 2** | KR2.5 | CI gate para mutação | Não | Sim | MT |
| **OKR 2** | KR2.6 | Banco real nos testes | Não | Sim | MT |
| **OKR 3** | KR3.1 | Tempo de onboarding | 1-2 sem | ≤ 15 min | M1 |
| **OKR 3** | KR3.2 | Ambiente em 1 comando | Não | `docker compose up` | M1 |
| **OKR 3** | KR3.3 | CONTRIBUTING executável | 0% | 100% | M1 |
| **OKR 3** | KR3.4 | Script Python passando | Não existe | 100% | M1 |
| **OKR 3** | KR3.5 | Perfis de ambiente | 0 | 3 | M1 |
| **OKR 4** | KR4.1 | Banco em uso | SQLite | PostgreSQL | M1 |
| **OKR 4** | KR4.2 | Hard delete artigos | 100% | 0% | M5 |
| **OKR 4** | KR4.3 | Hard delete comentários | 100% | 0% | M5 |
| **OKR 4** | KR4.4 | Artigos criados sem leitura | 100% | 0% | M5 |
| **OKR 4** | KR4.5 | Lazy update pendente pós 1ª leitura | 100% | 0% | M5 |
| **OKR 5** | KR5.1 | Endpoints com contador | 0/19 | 19/19 | M4 |
| **OKR 5** | KR5.2 | Log startup validado | Não | 100% | M4 |
| **OKR 5** | KR5.3 | Log shutdown validado | Não | 100% | M4 |
| **OKR 5** | KR5.4 | Traces habilitados | 0% | 100% | M4 |
| **OKR 5** | KR5.5 | Dashboard Grafana | 0 | ≥ 1 | M4 |
| **OKR 5** | KR5.6 | Gameday diagnóstico | N/M | ≤ 15 min | M4+M6 |
| **OKR 6** | KR6.1 | Conventional Commits | 0% | 100% | M0 |
| **OKR 6** | KR6.2 | PRs com issue | 0% | 100% | M0 |
| **OKR 6** | KR6.3 | DoR e DoD | Não | Sim | M0 |
| **OKR 6** | KR6.4 | Endpoints no OpenAPI | 0/19 | 19/19 | M4 |
| **OKR 6** | KR6.5 | Secrets no código | 2 | 0 | M1 |
| **OKR 6** | KR6.6 | PRs sem revisão | 5 | 0 | M6 |
| **OKR 6** | KR6.7 | Etapas no Coda | 0% | 100% | M6 |
| **OKR 7** | KR7.1 | Mutation score soft delete | 0% | ≥ 95% | M5 |
| **OKR 7** | KR7.2 | Mutation score leitura | 0% | ≥ 95% | M5 |
| **OKR 7** | KR7.3 | Leitura no OpenAPI | Não | Sim | M6 |
| **OKR 7** | KR7.4 | Registros deletados consultáveis | Não | Sim | M5 |
| **OKR 7** | KR7.5 | Lazy update 100% pós 1ª leitura | Não | Sim | M5 |

**N/M** = não medido · **N/A** = não aplicável

---

## 11. Consolidado por marco

### M0 — Processo estabelecido

| Métrica | Meta | Como verificar |
|---|---|---|
| M-GOV-01 Conventional Commits | 100% | Push fora do padrão → CI vermelho |
| M-GOV-02 PRs com issue | 100% | Template de PR com campo obrigatório |
| M-GOV-06 DoR e DoD | Existem | `ls docs/process/*.md` |

---

### M1 — Ambiente e segurança

| Métrica | Meta | Como verificar |
|---|---|---|
| M-SEC-01 Secrets no repositório | 0 | `truffleHog scan .` → 0 findings |
| M-SEC-02 JWT_SECRET via ambiente | Sim | App inicia / falha conforme variável |
| M-SEC-03 JWT_SESSION_TIME via ambiente | Sim | `application.properties` com `${JWT_SESSION_TIME:86400}` |
| M-DEV-01 Tempo de onboarding | ≤ 15 min | Medido com dev novo e documentado |
| M-DEV-02 Serviços no docker compose | 6/6 healthy | `docker compose ps` |
| M-DEV-03 Script Python | Exit 0 | `python scripts/validate_startup.py` |
| M-DEV-04 Perfis de ambiente | 3 | `ls application-*.yml` |
| M-PROD-01 Banco de dados | PostgreSQL | `actuator/health` datasource |

---

### M3 — Stack modernizada

| Métrica | Meta | Como verificar |
|---|---|---|
| M-STACK-01 Versão Java | Java 25 | `java -version` no container |
| M-STACK-02 Versão Spring Boot | 4.0.6 | `grep spring-boot build.gradle` |
| M-STACK-03 Warnings no build | 0 | `./gradlew build 2>&1 \| grep deprecated \| wc -l` → 0 |
| M-STACK-04 Referências MyBatis | 0 | `grep -r mybatis build.gradle` → 0 |
| M-STACK-05 Imports `javax.*` | 0 | `grep -r "import javax\." src/` → 0 |
| M-STACK-06 Joda-Time | 0 | `grep "joda" build.gradle` → 0 |
| M-STACK-07 DTOs como records | ≥ 80% | Contagem no pacote `application.data` |
| M-STACK-08 `getCursor()` em records | Sim | Grep + revisão de código |
| M-SEC-04 Imports `javax.crypto` | 0 | `grep -r "import javax.crypto" src/` → 0 |

---

### MT — Qualidade verificável

| Métrica | Meta | Como verificar |
|---|---|---|
| M-QUAL-01 Mutation score | ≥ 95% | `./gradlew pitest` → relatório HTML |
| M-QUAL-02 REST cobertos | 19/19 | Contagem de testes por controller |
| M-QUAL-03 GraphQL cobertos | 18/18 | Contagem de testes por operação |
| M-QUAL-04 Fluxos Playwright | 3 (base) | `npx playwright test` no branch `working` |
| M-QUAL-05 Branch `broken` | Existe | `git branch -r \| grep playwright-broken` |
| M-QUAL-06 Banco real nos testes | 0 refs H2 | `grep -r "H2\|sqlite" src/test/` → 0 |

---

### M4 — Sistema observável

| Métrica | Meta | Como verificar |
|---|---|---|
| M-OBS-01 Contadores por endpoint | 19/19 | Prometheus: `api_requests_total` por endpoint |
| M-OBS-02 Log startup estruturado | 100% | Script Python passa no CI |
| M-OBS-03 Log shutdown estruturado | 100% | Script Python passa no CI |
| M-OBS-04 Traces habilitados | 100% | Grafana/Tempo mostra trace |
| M-OBS-05 Gameday 1 | ≤ 15 min | Resultado documentado e assinado pela PM |
| M-GOV-04 OpenAPI endpoints | 19/19 | Swagger UI conta endpoints |

---

### M5 — Produto evoluído

| Métrica | Meta | Como verificar |
|---|---|---|
| M-PROD-02 Hard delete artigos | 0 | `grep repository.delete src/` → 0 |
| M-PROD-03 Hard delete comentários | 0 | `grep repository.delete src/` → 0 |
| M-PROD-04 DELETE retorna 204 | Sim | `curl -X DELETE ... -w "%{http_code}"` → 204 |
| M-PROD-05 Auditoria de deletados | Sim | `SELECT COUNT(*) FROM articles WHERE is_deleted = true` > 0 |
| M-PROD-06 Artigos novos com leitura | 100% | `POST /articles` → response tem `readingTimeMinutes` |
| M-PROD-08 `readingTimeMinutes` ≥ 1 | 0 artigos com < 1 | Query SQL |
| M-PROD-09 Mutation score soft delete | ≥ 95% | Relatório Pitest por classe |
| M-PROD-10 Mutation score leitura | ≥ 95% | Relatório Pitest por classe |
| M-QUAL-01 Pitest global mantido | ≥ 95% | `./gradlew pitest` |

---

### M6 — Encerramento

| Métrica | Meta | Como verificar |
|---|---|---|
| M-QUAL-04 Fluxos Playwright | 5 | `npx playwright test` → 5 passando |
| M-GOV-04 OpenAPI com campos novos | Sim | `readingTimeMinutes` visível no Swagger |
| M-OBS-06 Gameday 2 | ≤ 15 min | Resultado documentado e assinado pela PM |
| M-GOV-03 PRs sem revisão | 0 | GitHub PRs → Open → count |
| M-GOV-05 Etapas no Coda | 100% | Revisão do workspace Coda |
| M-PROD-07 Lazy update pendente | → 0 com tempo | `SELECT COUNT(*) FROM articles WHERE reading_time_minutes IS NULL` |
| M-QUAL-01 Pitest global final | ≥ 95% | `./gradlew pitest` |

---

## Guia de uso deste documento

### Para a PM — verificação de marcos

Antes de declarar um marco atingido:
1. Abrir a seção do marco correspondente na Seção 11
2. Verificar cada métrica da lista usando o comando/ferramenta indicado
3. Registrar o resultado (valor obtido vs meta) com data
4. Assinar o marco no Roadmap (`04-roadmap.md`)

### Para desenvolvedores — verificação de DoD

Antes de fechar uma issue:
1. Identificar as métricas do épico no índice do Backlog (`05-backlog.md`)
2. Verificar cada critério de aceitação do épico
3. Confirmar que os comandos de verificação retornam os valores esperados
4. Documentar no Coda os prompts usados na implementação

### Para stakeholders — leitura rápida

- **Seção 1** dá o estado atual de risco e progresso em linguagem de negócio
- **Tabela da Seção 10** mostra todos os 40 KRs com AS IS, meta e marco de um relance
- **Gameday 1 e Gameday 2** são os exercícios que validam observabilidade — resultados assinados pela PM

---

*Documento vivo — atualizar coluna "AS IS" ao concluir cada fase*  
*40 KRs · 28 métricas técnicas · 5 métricas de runtime · 7 marcos rastreados*  
*Rastreado em: `02-product-vision.md` · `04-roadmap.md` · `05-backlog.md` · `06-architecture-decisions.md`*