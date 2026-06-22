# 00 — Mandato Original do Projeto
> Versão 1.0 · Junho 2026
> Fonte: instruções originais da gestão, fornecidas em inglês, sem edição de conteúdo.
> **Documento canônico** — toda história de usuário (`docs/09-user-stories/`) deve referenciar este documento e citar literalmente o item específico que atende, na seção "Premissas da gestão atendidas por esta história".

---

## Por que este documento existe

Antes deste documento, cada história citava trechos do mandato original diretamente, sem um ponto único de referência. Isso gerava risco de paráfrase divergente do texto original entre histórias diferentes. Este documento resolve isso:

1. Preserva o texto original **verbatim** (sem reescrita, sem tradução, sem reinterpretação)
2. Numera cada item para citação inequívoca (`G1`, `G2`, ... para "General"; `J1`, `J2`, ... para "Java")
3. Mapeia cada item para a iniciativa (`03-initiatives.md`) e o épico (`05-backlog.md`) que o atende
4. Serve como fonte única de verdade — se houver dúvida sobre o que a gestão pediu, a resposta está aqui, não em paráfrases espalhadas por outros documentos

---

## Texto original (verbatim)

> Preservado exatamente como recebido da gestão. Não editar esta seção — qualquer correção ou esclarecimento futuro deve ser tratado como um adendo, nunca como alteração do texto abaixo.

### General

```
Tracking of issues using github issues, templates integrated with repositories,
definition of ready and definition of done documented per repo adjusted to team preferences
Create issues before working, reference changes to repo to issues
Create integration tests for the current contract
Containerize using docker and compose
Test with Playwright
Update to the most recent version of the framework
Document current API contract with OpenAPI/Swagger
Add Stryker, Pitest or similar mutation test, cover to 95%
Add integration tests
Not necessarily in that order
User Conventional Commits for all commits
Code and commits with Coda
Add integration tests
Add end-to-end tests
A branch with functional tests broken and a branch with the functional test working using Playwright
Add LGTM stack into compose, validate each artifact has a counter metric on each endpoint that
increases by one for each call, a log for startup and when exiting the app and traces are enabled.
Python script start the app in Docker and validate the startup log and exit log work.
Document prompts for each step, document skills used or needed, comment with others
After tutorials: In groups of two create a stepwise process implementing harness development to
migrate each repository. Have it commit automatically into a bleeding branch using conventional
commits. Use GitAhead to validate progress.
```

### Java

```
Migrate MyBatis to Spring Data/JPA/Hibernate
Migrate to Java 25
Migrate to Spring Boot 4.0.3
Migrate to Gradle 9.3.1 or newer with no deprecation warnings
Introduce record types when possible
Add the feature of soft delete, change the delete behaviour to use a is_deleted flag
Add a feature to estimate article reading time based on word count eg 200 words per minute
Add a feature to cache the reading time of articles and lazy update data on read for articles
with no information
```

> **Nota sobre repetição no texto original:** "Add integration tests" aparece três vezes e "Test with Playwright" / "Add end-to-end tests" se sobrepõem. Isso não foi um erro de transcrição — o texto original da gestão já continha essa repetição. Por rastreabilidade, cada ocorrência recebe um número de item abaixo (`G3`, `G9`, `G13`), mas as três apontam para a mesma entrega real (EPIC-08).

> **Nota sobre versão do Spring Boot:** o item `J3` pede explicitamente **Spring Boot 4.0.3**. ✅ **Resolvido (2026-06-22):** a PM Franciele decidiu **manter o literal do mandato (`4.0.3`)**. Toda a documentação do projeto foi **alinhada para `4.0.3`** (`02-product-vision.md`, ADRs, `docs/AGENTS.md`, `04-roadmap.md`, `05-backlog.md`, `07-metrics.md`, `08-risks-and-dependencies.md`, `03-initiatives.md`, `01-current-state.md`). Histórico: uma revisão anterior (GAP-D) havia adotado `4.0.6`; substituída pela decisão da PM. Ver seção "Divergências abertas" abaixo.

---

## Itens numerados — rastreabilidade para iniciativas e épicos

### General

| # | Item (verbatim) | Iniciativa | Épico |
|---|---|---|---|
| G1 | "Tracking of issues using github issues, templates integrated with repositories, definition of ready and definition of done documented per repo adjusted to team preferences" | INI-01 | EPIC-01 |
| G2 | "Create issues before working, reference changes to repo to issues" | INI-01 | EPIC-01 |
| G3 | "Create integration tests for the current contract" | INI-08 | EPIC-08 |
| G4 | "Containerize using docker and compose" | INI-03 | EPIC-03 |
| G5 | "Test with Playwright" | INI-09 | EPIC-09 |
| G6 | "Update to the most recent version of the framework" | INI-04 | EPIC-04 |
| G7 | "Document current API contract with OpenAPI/Swagger" | INI-11 | EPIC-11 |
| G8 | "Add Stryker, Pitest or similar mutation test, cover to 95%" | INI-07 | EPIC-07 |
| G9 | "Add integration tests" (1ª ocorrência) | INI-08 | EPIC-08 |
| G10 | "Not necessarily in that order" *(nota de processo — gestão autoriza reordenar a sequência das entregas, não é um entregável)* | — | — |
| G11 | "User [sic — Use] Conventional Commits for all commits" | INI-01 | EPIC-01 |
| G12 | "Code and commits with Coda" | INI-01 | EPIC-01 |
| G13 | "Add integration tests" (2ª ocorrência) | INI-08 | EPIC-08 |
| G14 | "Add end-to-end tests" | INI-09 | EPIC-09 |
| G15 | "A branch with functional tests broken and a branch with the functional test working using Playwright" | INI-09 | EPIC-09 (US-09.02 `feat/playwright-broken`, US-09.08 `feat/playwright-working`) |
| G16 | "Add LGTM stack into compose, validate each artifact has a counter metric on each endpoint that increases by one for each call, a log for startup and when exiting the app and traces are enabled" | INI-10 | EPIC-10 |
| G17 | "Python script start the app in Docker and validate the startup log and exit log work" | INI-03 / INI-10 | EPIC-03 (US-03.06/03.07), EPIC-10 (US-10.06) |
| G18 | "Document prompts for each step, document skills used or needed, comment with others" | INI-01 | EPIC-01 (`docs/process/coda-guide.md`) |
| G19 | "After tutorials: in groups of two create a stepwise process implementing harness development [...]. Have it commit automatically into a bleeding branch using conventional commits. Use GitAhead to validate progress" | INI-01 | EPIC-01 (US-01.06 branch `bleeding`, US-01.08 guia GitAhead) |

### Java

| # | Item (verbatim) | Iniciativa | Épico |
|---|---|---|---|
| J1 | "Migrate MyBatis to Spring Data/JPA/Hibernate" | INI-05 | EPIC-05 |
| J2 | "Migrate to Java 25" | INI-04 | EPIC-04 |
| J3 | "Migrate to Spring Boot 4.0.3" ⚠️ ver divergência de versão | INI-04 | EPIC-04 |
| J4 | "Migrate to Gradle 9.3.1 or newer with no deprecation warnings" | INI-04 | EPIC-04 |
| J5 | "Introduce record types when possible" | INI-06 | EPIC-06 |
| J6 | "Add the feature of soft delete, change the delete behaviour to use a is_deleted flag" | INI-12 | EPIC-12 |
| J7 | "Add a feature to estimate article reading time based on word count eg 200 words per minute" | INI-13 | EPIC-13 |
| J8 | "Add a feature to cache the reading time of articles and lazy update data on read for articles with no information" | INI-13 | EPIC-13 |

---

## Divergências abertas entre o mandato e a documentação derivada

| Item | Mandato original | Documentação atual | Status |
|---|---|---|---|
<<<<<<< HEAD
<<<<<<< HEAD
| Versão Spring Boot | J3: "4.0.3" | `02-product-vision.md`, ADRs, docs/AGENTS.md, `04-roadmap.md`, `05-backlog.md`: "4.0.6" | ✅ **Resolvido (2026-06-22) — a PM Franciele decidiu manter o literal do mandato: Spring Boot `4.0.3`.** As histórias do EPIC-04 (US-04.01..09) foram escritas com `4.0.3`. ⚠️ Divergência remanescente: `04-roadmap.md` v5.0, `05-backlog.md` v2.0 e `docs/AGENTS.md` ainda referenciam `4.0.6` (GAP-D) — devem ser alinhados para `4.0.3` em atualização posterior desses documentos canônicos. |
=======
| Versão Spring Boot | J3: "4.0.3" | Toda a documentação derivada alinhada para "4.0.3" | ✅ **Resolvido (2026-06-22) — a PM Franciele decidiu manter o literal do mandato: Spring Boot `4.0.3`.** As histórias do EPIC-04 (US-04.01..09) usam `4.0.3` e os documentos canônicos (`04-roadmap.md`, `05-backlog.md`, `07-metrics.md`, `08-risks-and-dependencies.md`, `03-initiatives.md`, `06-architecture-decisions.md`, `01-current-state.md`, `docs/AGENTS.md`) foram alinhados para `4.0.3`. GAP-D encerrado. |
>>>>>>> docs/align-spring-boot-403
=======
| Versão Spring Boot | J3: "4.0.3" | Toda a documentação derivada alinhada para "4.0.3" | ✅ **Resolvido (2026-06-22) — a PM Franciele decidiu manter o literal do mandato: Spring Boot `4.0.3`.** As histórias do EPIC-04 (US-04.01..09) usam `4.0.3` e os documentos canônicos (`04-roadmap.md`, `05-backlog.md`, `07-metrics.md`, `08-risks-and-dependencies.md`, `03-initiatives.md`, `06-architecture-decisions.md`, `01-current-state.md`, `docs/AGENTS.md`) foram alinhados para `4.0.3`. GAP-D encerrado. |
>>>>>>> docs/us-04.01-adr-dgs-vs-spring-graphql
| **EPIC-02 sem item de origem** | Nenhum item de "General" ou "Java" menciona JWT, secrets ou perfis de ambiente | EPIC-02 existe como épico completo em `05-backlog.md` e INI-02 em `03-initiatives.md` | 🔶 **Gap de rastreabilidade.** Diferente de todos os outros 12 épicos (cada um mapeia 1:1 para algum item `G`/`J`), o EPIC-02 não tem origem no texto verbatim do mandato. Ele aparenta ter sido adicionado pelo time a partir da auditoria técnica AS IS (`01-current-state.md` — JWT secret hardcoded identificado como vulnerabilidade crítica), e não de uma instrução direta da gestão. As histórias deste épico (US-02.01–02.06) citam apenas os itens de processo geral (`G1`, `G11`, `G12`, `G18`) como premissa — não um item que justifique *por que* corrigir o JWT especificamente. Recomendado: confirmar com a PM se isso foi uma decisão consciente do time ou se há uma instrução da gestão sobre segurança que não chegou a este texto. |

---

## Como citar este documento em uma história de usuário

Toda história em `docs/09-user-stories/` deve incluir, na seção **"Premissas da gestão atendidas por esta história"**, uma linha no formato:

```
| Item do mandato | Como é atendida |
|---|---|
| G16 (docs/00-original-mandate.md) — "Add LGTM stack into compose, validate each artifact has a counter metric..." | <explicação específica de como esta história atende este item> |
```

E, no cabeçalho de "Identificação" da história, uma linha fixa:

```
> Pré-requisito: ler docs/AGENTS.md (REGRA ZERO + 6 ADRs ATIVOS) e docs/00-original-mandate.md (item <G/J-XX>) antes de implementar.
```

---

## Rastreabilidade

| Documento | Referência |
|---|---|
| `02-product-vision.md` | Seção 2 — "a gestão definiu um conjunto claro de evoluções esperadas" |
| `03-initiatives.md` | Todas as 13 iniciativas — origem de cada uma rastreada aqui |
| `05-backlog.md` | Todos os 13 épicos |
| `04-roadmap.md` | "As 8 demandas da gestão e onde vivem neste roadmap" — complementar a este documento |
| `docs/AGENTS.md` | REGRA ZERO — deve ser lido junto com este documento antes de qualquer prompt |

---

*Documento vivo — divergências devem ser resolvidas e registradas aqui, nunca silenciosamente corrigidas em outro documento.*
