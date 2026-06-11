<!--
  Template de Pull Request — RealWorld Platform Modernization
  Preencha todas as seções aplicáveis. Itens marcados com "(quando aplicável)"
  podem ser ignorados em PRs de docs ou chore que não tocam código.
  Os comentários HTML (como este) são invisíveis no render do GitHub.
-->

## Issue relacionada

closes #

<!--
  OBRIGATÓRIO. Toda PR deve referenciar uma issue criada ANTES do código.
  PR sem issue vinculada NÃO é aprovada.
  Formatos aceitos:
    - "closes #XX"  -> fecha a issue automaticamente ao mergear
    - "refs #XX"    -> referencia a issue sem fechá-la (commits parciais)
-->

---

## Tipo de mudança

<!-- Marque o(s) tipo(s) que correspondem ao título do(s) commit(s) Conventional Commits. -->

- [ ] `feat` — nova funcionalidade ou comportamento novo no sistema
- [ ] `fix` — correção de bug
- [ ] `test` — adição ou correção de testes (unidade, integração, mutação, E2E)
- [ ] `docs` — documentação (ADRs, OpenAPI, CONTRIBUTING, README)
- [ ] `chore` — setup, dependências, configuração, ferramentas
- [ ] `refactor` — reestruturação de código sem mudança de comportamento
- [ ] `ci` — workflows, pipelines e scripts de build
- [ ] `perf` — melhoria mensurável de performance
- [ ] `style` — formatação de código sem mudança de lógica

---

## O que foi feito

<!-- Descreva em 2-5 frases. Referencie o épico e a história (ex: EPIC-01 / US-01.03). -->

---

## ADRs consultados

<!-- Marque os ADRs lidos antes de implementar. Vibe coding sem leitura dos ADRs é risco R-17. -->

- [ ] ADR-001 — DGS Framework vs Spring for GraphQL
- [ ] ADR-002 — Spring Data JPA: estratégia de queries
- [ ] ADR-003 — Métricas por endpoint: AOP
- [ ] ADR-004 — io.spring.graphql: código gerado pelo DGS Codegen
- [ ] ADR-005 — Node interface e cursor pagination: records
- [ ] ADR-006 — JWT_SECRET e JWT_SESSION_TIME: variáveis de ambiente
- [ ] Nenhum ADR aplicável a esta PR

---

## Checklist de Definition of Done

<!-- Fonte de verdade: docs/process/definition-of-done.md -->

### Código e funcionalidade

- [ ] Código implementado e funcionando localmente
- [ ] Comportamento esperado verificado manualmente (critérios de aceitação da issue)
- [ ] Nenhuma regressão introduzida

### Testes

- [ ] `./gradlew test` passando sem falhas
- [ ] Testes de unidade escritos para lógica nova (quando aplicável)
- [ ] Testes de integração escritos ou verificados (quando há mudança de contrato — EPIC-08)
- [ ] `./gradlew pitest` — mutation score ≥ 95% mantido (quando há mudança de código Java)
- [ ] Fluxo E2E no Playwright atualizado (quando há mudança de comportamento visível — EPIC-09)
- [ ] Testcontainers com PostgreSQL real — nenhum teste usa H2 ou SQLite

### Contrato e documentação

- [ ] OpenAPI/Swagger atualizado (quando há mudança de contrato REST — EPIC-11)
- [ ] Schema GraphQL `.graphqls` atualizado (quando há mudança de contrato GraphQL)
- [ ] API Mapping (`API-mapping.md` v2.0) continua preciso após esta mudança

### Processo e rastreabilidade

- [ ] Todos os commits seguem Conventional Commits (commitlint no CI valida)
- [ ] Commit message referencia a issue: `closes #XX` ou `refs #XX`
- [ ] Issue vinculada existe e está preenchida com DoR atendida
- [ ] Prompts e skills documentados no Coda (link abaixo)

---

## Link do Coda

<!-- OBRIGATÓRIO: link para o registro de prompts e skills desta história no Coda. -->

---

## Evidência de testes

<details>
<summary>Saída do <code>./gradlew test</code></summary>

```text

```

</details>

<details>
<summary>Saída do <code>./gradlew pitest</code> (quando aplicável)</summary>

```text

```

</details>

<details>
<summary>Resultado do Playwright (quando aplicável)</summary>

```text

```

</details>

---

## Observações para o reviewer

<!-- Contexto adicional: decisões tomadas, trade-offs, pontos de atenção, áreas que merecem revisão cuidadosa. -->
