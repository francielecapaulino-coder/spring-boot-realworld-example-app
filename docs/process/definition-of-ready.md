# Definition of Ready (DoR)

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Projeto:** RealWorld Platform Modernization
> **Versão:** 1.0 · Junho 2026
> **Ajustado ao time:** vibe coding em duplas · Coda · Pitest 95% · Docker Compose (6 serviços) · Playwright

---

## 1. O que é a Definition of Ready

A **Definition of Ready (DoR)** é o conjunto mínimo de condições que uma issue precisa satisfazer **antes** de qualquer linha de código ser gerada. Ela garante que toda história entra em desenvolvimento com contexto técnico completo, critérios de aceitação claros, ambiente funcionando e prompts de vibe coding planejados — evitando retrabalho, código sem rastreabilidade e geração de IA sem leitura dos ADRs.

> ⚠️ **Regra fundamental**
> **Código só é gerado após a issue estar pronta. Issue não pronta = desenvolvimento não começa.**

---

## 2. Critérios universais (todas as histórias)

Aplicam-se a **todas** as histórias, independentemente do tipo.

### 2.1 Issue existe e está preenchida

- [ ] Issue criada usando o template correto (`.github/ISSUE_TEMPLATE/`)
- [ ] Título segue Conventional Commits (`tipo(escopo): descrição`)
- [ ] Épico relacionado preenchido (referência a `05-backlog.md` v2.0)
- [ ] Iniciativa relacionada preenchida (`INI-XX` de `03-initiatives.md` v2.0)
- [ ] Fase preenchida (`Fase X` de `04-roadmap.md` v5.0)
- [ ] História de usuário no formato **como / quero / para que**
- [ ] Ao menos 2 critérios de aceitação no formato **dado / quando / então**
- [ ] Campo DoR (resumo) preenchido na issue
- [ ] Campo DoD (resumo) preenchido na issue

### 2.2 Contexto técnico completo

- [ ] Épico lido em `05-backlog.md` v2.0
- [ ] ADRs relevantes consultados em `06-architecture-decisions.md` v1.0:
  - **ADR-001** — DGS Framework vs Spring for GraphQL → histórias que tocam GraphQL
  - **ADR-002** — Spring Data JPA, estratégia de queries → histórias de persistência/consulta
  - **ADR-003** — Métricas por endpoint (AOP) → histórias de observabilidade
  - **ADR-004** — io.spring.graphql, código gerado pelo DGS Codegen → mudanças no schema GraphQL
  - **ADR-005** — Node interface e cursor pagination (records) → DTOs, paginação, records
  - **ADR-006** — JWT_SECRET e JWT_SESSION_TIME (variáveis de ambiente) → autenticação/segurança
- [ ] Riscos consultados em `08-risks-and-dependencies.md` v1.0 (especialmente R-17 e R-18)
- [ ] Dependências desta história concluídas

### 2.3 Ambiente funcionando

- [ ] `git pull origin master` executado
- [ ] Branch criada com nome correto: `tipo/us-XX.XX-descricao-curta`
- [ ] `docker compose up` — 6 serviços healthy:

```bash
docker compose up -d
# Serviços esperados (healthy):
#   app        -> http://localhost:8080
#   postgres   -> localhost:5432
#   prometheus -> http://localhost:9090
#   loki       -> http://localhost:3100
#   tempo      -> http://localhost:3200
#   grafana    -> http://localhost:3000
```

- [ ] `curl http://localhost:8080/tags` retorna JSON:

```bash
curl http://localhost:8080/tags
# Esperado: {"tags":[...]}
```

- [ ] `curl http://localhost:8080/actuator/health` retorna status UP:

```bash
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}
```

### 2.4 Prompts planejados no Coda

- [ ] Sessão no Coda aberta para esta história
- [ ] Contexto dos ADRs relevantes incluído no prompt
- [ ] Contexto do épico e critérios de aceitação incluído no prompt
- [ ] Restrições técnicas documentadas (Pitest 95%, Testcontainers, sem H2/SQLite)

### 2.5 Processo de vibe coding configurado

- [ ] Branch `bleeding` existe no repositório remoto
- [ ] GitAhead instalado e branch `bleeding` visível
- [ ] Commits automáticos para `bleeding` configurados

---

## 3. Critérios adicionais por tipo de história

### 3.1 `feat` / `fix` — código de produção

- [ ] `./gradlew test` passando na `master` antes de iniciar
- [ ] `./gradlew pitest` configurado e threshold de 95% ativo no CI
- [ ] Testcontainers PostgreSQL disponível para testes de integração
- [ ] Zero referências a H2/SQLite nos testes:

```bash
grep -riE "h2|sqlite" src/test && echo "ENCONTRADO - corrigir" || echo "OK - nenhuma referencia"
```

- [ ] Camadas a tocar identificadas: `api` | `core` | `application` | `infrastructure`

### 3.2 `test` — integração, mutação, E2E

- [ ] Tipo definido: unitário | slice | integração | mutação | E2E
- [ ] Para **integração**: PostgreSQL 16 via Testcontainers (nunca H2/SQLite)
- [ ] Para **mutação**: `./gradlew pitest` executável
- [ ] Para **E2E**: Node.js instalado e `npx playwright test` executável
- [ ] Para **Playwright**: branch `feat/playwright-broken` existe
- [ ] Endpoints ou operações a testar listados na issue
- [ ] Cenários de sucesso e de erro definidos para cada alvo

### 3.3 `refactor`

- [ ] ADRs relacionados identificados (obrigatório para refactor)
- [ ] Testes de contrato existentes passando (rede de segurança)
- [ ] Comportamento externo a preservar documentado
- [ ] `./gradlew pitest` ≥ 95% como baseline antes de iniciar

### 3.4 `docs`

- [ ] Artefato de documentação identificado (ADR, OpenAPI, CONTRIBUTING, etc.)
- [ ] Referências cruzadas a outros documentos verificadas

### 3.5 `spike`

- [ ] Pergunta mensurável definida
- [ ] Timebox definido (ex: 4h, 1 dia)
- [ ] Entregável definido (ADR atualizado, relatório, decisão)

### 3.6 `chore` (inclui `ci`, `perf`, `style`)

- [ ] Escopo claro e delimitado
- [ ] Impacto no build Gradle verificado
- [ ] `.env.example` atualizado (quando há nova variável de ambiente)

---

## 4. Checklist rápido para uso diário

Copie e use ao iniciar qualquer história:

```text
[ ] Issue criada com template correto e campos preenchidos
[ ] Épico lido em 05-backlog.md
[ ] ADRs relevantes consultados em 06-architecture-decisions.md
[ ] Branch criada: tipo/us-XX.XX-descricao
[ ] docker compose up — 6 serviços healthy
[ ] curl http://localhost:8080/tags → JSON válido
[ ] Coda aberto com contexto do épico e ADRs no prompt
[ ] Critérios adicionais do tipo desta história verificados
```

---

## 5. Fluxo de trabalho

```text
┌─────────────────────────────────────────────────────────────┐
│  CICLO COMPLETO — DA ISSUE AO MERGE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. PM cria/aprova issue no GitHub                          │
│         │                                                   │
│         ▼                                                   │
│  2. Dupla verifica a DoR (este documento)                  │
│         │                                                   │
│         ▼                                                   │
│  3. Coda aberto — prompt preparado (ADRs + épico)          │
│         │                                                   │
│         ▼                                                   │
│  4. Branch criada (tipo/us-XX.XX-descricao)                │
│         │                                                   │
│         ▼                                                   │
│  5. Vibe coding inicia                                      │
│         │                                                   │
│         ▼                                                   │
│  6. DoD verificada (docs/process/definition-of-done.md)    │
│         │                                                   │
│         ▼                                                   │
│  7. PR aberta usando .github/PULL_REQUEST_TEMPLATE.md      │
│         │                                                   │
│         ▼                                                   │
│  8. PM revisa e aprova                                      │
│         │                                                   │
│         ▼                                                   │
│  9. Merge → branch bleeding atualizada                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Responsabilidades

| Papel | Pessoa | Responsabilidade na DoR |
|---|---|---|
| **PM** | Franciele | Cria/aprova issues; garante épico, critérios e contexto preenchidos; aprova a PR |
| **Dupla A** | Tech Lead | Verifica DoR técnica (ADRs, ambiente, Pitest/Testcontainers); conduz o vibe coding |
| **Dupla B** | Par de desenvolvimento | Revisa prompts no Coda; valida ambiente local; apoia na verificação dos critérios |

---

## 7. Atualizações desta DoR

Este documento é atualizado **via Pull Request** com commit do tipo `docs`:

```bash
git commit -m "docs(process): update definition of ready — <motivo>"
```

### Histórico de versões

| Versão | Data | O que mudou |
|---|---|---|
| 1.0 | Junho 2026 | Versão inicial — critérios universais, critérios por tipo, checklist diário, fluxo de 9 passos, responsabilidades |

---

> **Fonte de verdade para DoR neste repositório.**
> Veja também: [`definition-of-done.md`](./definition-of-done.md)
