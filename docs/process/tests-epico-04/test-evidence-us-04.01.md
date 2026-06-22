# Evidência de testes — US-04.01

> **História:** US-04.01 — Documentar ADR-001 sobre DGS Framework vs Spring for GraphQL
> **Branch:** `docs/us-04.01-adr-dgs-vs-spring-graphql`
> **PR:** a abrir
> **Issue:** #76
> **Data de execução:** 2026-06-22

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Arquivo modificado | `docs/06-architecture-decisions.md` (ADR-001 — apenas rastreabilidade) |
| Tipo | `docs` (decisão arquitetural — ADR já estava ✅ Aceito) |
| Código de produção | nenhum (decisão do ADR inalterada; `io.spring.graphql.*` intocado) |

---

## Resultado dos critérios de aceite

| CA | Descrição | Status | Evidência |
|---|---|---|---|
| CA-01 | ADR-001 presente com status `✅ Aceito` | ✅ | linha 27 (índice) e linha 41 (`Status: ✅ Aceito`) |
| CA-02 | Contexto, Opção A, Opção B e Decisão documentados | ✅ | seções "Contexto", "Opções consideradas (A/B)", "Decisão" |
| CA-03 | Decisão: **Opção A — DGS 10.x (sem reescrita de resolvers)** | ✅ | linha 92 ("Opção A: Atualizar DGS Framework para versão 10.x") |
| CA-04 | Consequências e plano de revisão futura | ✅ | seção "Consequências" (linhas 98-112) + linha 96 ("pode ser revisada") |
| CA-05 | ADR referenciado por INI-04 / US-04.01 **e histórias dependentes** | ✅ | rastreabilidade agora cita US-04.03 e US-04.06 (linhas 121-122) |
| CA-06 | Decisão não viola ADR-004 (código gerado não editado) | ✅ | `git diff --name-only` → apenas `06-architecture-decisions.md` |

---

## Verificações executadas

```text
# V1 — ADR-001 presente e aceito
27:| [ADR-001] DGS Framework vs Spring for GraphQL | ✅ Aceito | Fase 3 | INI-04, US-04.01 |
41:**Status:** ✅ Aceito

# V2 — decisão pela Opção A
92:**Opção A: Atualizar DGS Framework para versão 10.x.**

# V3 — nenhum código gerado tocado
git diff --name-only → docs/06-architecture-decisions.md  (0 arquivos io.spring.graphql)

# CA-05 — histórias dependentes citadas
121:| 05-backlog.md | EPIC-04, US-04.03 (upgrade Spring Boot 4.0.3) — depende desta decisão |
122:| 05-backlog.md | EPIC-04, US-04.06 (atualização DGS para 10.x) — implementa a Opção A |
```

---

## Conformidade

| Verificação | Resultado |
|---|---|
| Decisão do ADR inalterada (Opção A — DGS 10.x) | ✅ apenas rastreabilidade ajustada |
| ADR-004 — `io.spring.graphql.*` não editado | ✅ |
| Risco HIGH (ADR) — confirmação da PM obtida antes da edição | ✅ |
| Spring Boot 4.0.3 (literal do mandato, GAP-D encerrado) | ✅ consistente em toda a doc |

---

## Conclusão

O ADR-001 está completo, com status `✅ Aceito`, decisão registrada (Opção A — DGS 10.x) e
rastreabilidade ampliada para as histórias dependentes (US-04.03 e US-04.06). A US-04.01 está
concluída — desbloqueia o início do upgrade do framework (US-04.02 em diante).
