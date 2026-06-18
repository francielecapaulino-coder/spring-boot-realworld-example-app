# Definition of Done (DoD)

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Projeto:** RealWorld Platform Modernization
> **Versão:** 1.0 · Junho 2026
> **Ajustado ao time:** vibe coding com duplas · Coda · Pitest 95% · Docker Compose · Playwright

---

## 1. O que é a Definition of Done

A **Definition of Done (DoD)** é o conjunto de condições que uma história precisa satisfazer para ser considerada **concluída** e apta a ser mergeada. Enquanto a **DoR é a porta de entrada** (o que precisa estar pronto para começar), a **DoD é a porta de saída** (o que precisa estar pronto para terminar). Ela garante qualidade verificável, rastreabilidade da issue ao código e nenhum item obrigatório esquecido.

> ⚠️ **Regra fundamental**
> **PM não aprova PR com itens obrigatórios não marcados.**
> **PR com DoD incompleta = PR não mergeada.**

---

## 2. Gates obrigatórios

Bloqueiam o merge **absolutamente**. Sem exceção.

```text
🚫 GATE 1: ./gradlew test passando sem falhas (quando há código Java)
🚫 GATE 2: ./gradlew pitest ≥ 95% mantido (quando há mudança de código Java)
🚫 GATE 3: commit referencia a issue — closes #XX ou refs #XX
🚫 GATE 4: prompts documentados inline na PR e/ou no arquivo de história (Coda.io ou inline)
🚫 GATE 5: PR template preenchido — todos os campos obrigatórios marcados
```

---

## 3. Critérios universais (todas as histórias)

### 3.1 Código e funcionalidade

- [ ] Escopo implementado corresponde à issue (nem mais, nem menos)
- [ ] Comportamento verificado manualmente seguindo os critérios de aceitação
- [ ] Nenhuma regressão introduzida
- [ ] `docker compose up` continua funcional — 6 serviços healthy:

```bash
# app 8080 · postgres 5432 · prometheus 9090 · loki 3100 · tempo 3200 · grafana 3000
docker compose up -d && docker compose ps
```

- [ ] `curl http://localhost:8080/tags` retorna JSON válido
- [ ] `curl http://localhost:8080/actuator/health` retorna `{"status":"UP"}`

### 3.2 Processo e rastreabilidade

- [ ] Conventional Commits em todos os commits
- [ ] Pelo menos um commit com `closes #XX` ou `refs #XX`
- [ ] Issue vinculada existe com a DoR atendida
- [ ] Branch nomeada: `tipo/us-XX.XX-descricao-curta`
- [ ] PR aberta usando `.github/PULL_REQUEST_TEMPLATE.md`

### 3.3 Documentação de prompts e execução

- [ ] Prompts documentados inline no corpo da PR **e/ou** na seção "Log de execução" do arquivo `.md` da história
- [ ] Skills e ajustes relevantes registrados (inline ou no Coda quando workspace URL disponível)
- [ ] Se Coda.io configurado (`TODO-CODA-URL` substituído): link do Coda preenchido na PR e na issue

> **Preferência de sessão ativa:** documentação inline é aceita como substituta completa do Coda.io enquanto a URL do workspace não estiver configurada.

---

## 4. Critérios adicionais por tipo

### 4.1 `feat`

- [ ] `./gradlew test` 100% verde
- [ ] Testes de unidade para lógica nova em `core` e `application`
- [ ] Testes de integração: REST Assured + Testcontainers PostgreSQL 16 (**NUNCA H2**)
  - [ ] ≥ 1 sucesso + ≥ 1 erro por endpoint afetado
- [ ] `./gradlew pitest` ≥ 95% — verificar `build/reports/pitest/index.html`
- [ ] OpenAPI/Swagger atualizado quando há novo endpoint ou campo
  - [ ] Verificar: `http://localhost:8080/swagger-ui.html` mostra o novo contrato
- [ ] Schema `.graphqls` atualizado quando há nova operação GraphQL
- [ ] `API-mapping.md` v2.0 continua preciso
- [ ] Playwright atualizado em `feat/playwright-working` quando há mudança de comportamento visível

### 4.2 `fix`

- [ ] Bug corrigido e verificado seguindo os passos de reprodução da issue
- [ ] Teste de regressão: falha na `master`, passa após o fix
- [ ] `./gradlew test` 100% verde
- [ ] `./gradlew pitest` ≥ 95%
- [ ] Comportamento correto não alterado
- [ ] Contrato HTTP preservado (ex: `DELETE` retorna **204** — não 200)

### 4.3 `test`

- [ ] Tipo implementado conforme issue: unitário | slice | integração | mutação | E2E
- [ ] Para **integração**:
  - [ ] PostgreSQL 16 via Testcontainers (verificar: log mostra container iniciando)
  - [ ] **NUNCA H2**:

```bash
grep -r "H2\|sqlite\|DataSource.*embedded" src/test/ && echo "FALHA - remover" || echo "OK - 0 resultados"
```

  - [ ] ≥ 1 sucesso + ≥ 1 erro por endpoint/operação
- [ ] Para **mutação (Pitest)**:
  - [ ] `./gradlew pitest` score ≥ 95%
  - [ ] CI com threshold bloqueando merge se < 95%
  - [ ] Relatório publicado como artefato no CI
- [ ] Para **E2E (Playwright)**:
  - [ ] `npx playwright test` passando em `feat/playwright-working`
  - [ ] `feat/playwright-broken` existe com CI vermelho
  - [ ] APIRequestContext — **NUNCA** automação de browser
  - [ ] `BASE_URL` configurável via variável de ambiente

### 4.4 `refactor`

- [ ] Comportamento externo idêntico — nenhum endpoint muda resposta
- [ ] `./gradlew test` sem alteração nos testes existentes
- [ ] Testes de contrato (EPIC-08) passando sem modificação
- [ ] `./gradlew pitest` ≥ 95%
- [ ] ADRs consultados listados na seção de ADRs da PR
- [ ] **NENHUM** arquivo de `io.spring.graphql` editado manualmente (ADR-004):

```bash
git diff --name-only | grep "io/spring/graphql" && echo "FALHA - codigo gerado" || echo "OK - 0 resultados"
```

### 4.5 `docs`

- [ ] Arquivo criado/atualizado e acessível no path correto
- [x] Conteúdo revisado — auto-aprovado para tipo `docs` (preferência de sessão)
- [ ] Links internos verificados
- [ ] Se ADR: seção "Rastreabilidade" com referências a iniciativas, épicos e histórias
- [ ] Se OpenAPI: Swagger UI renderiza sem erros, `/v3/api-docs` retorna JSON válido

### 4.6 `spike`

- [ ] Pergunta respondida com evidências concretas
- [ ] Resultado documentado (ADR atualizado | comentário | relatório em `docs/`)
- [ ] Timebox respeitado — se não concluiu, documentar o que faltou
- [ ] Próximos passos definidos
- [ ] Se impacta ADR: `ADR-00X` atualizado em `06-architecture-decisions.md` v1.0

### 4.7 `chore` (inclui `ci`, `perf`, `style`)

- [ ] Configuração funcionando local e no CI
- [ ] `docker compose up` continua funcional (quando a mudança envolve Docker)
- [ ] `./gradlew build` sem erros
- [ ] Se nova variável de ambiente: `.env.example` atualizado com comentário
- [ ] `CONTRIBUTING.md` atualizado se o setup de ambiente mudou

---

## 5. Checklist rápido

Copie e use antes de abrir qualquer PR:

```text
[ ] GATE 1: ./gradlew test passando (quando há código Java)
[ ] GATE 2: ./gradlew pitest ≥ 95% mantido (quando há código Java)
[ ] GATE 3: commit com closes #XX ou refs #XX
[ ] GATE 4: prompts documentados inline na PR e/ou no arquivo de história
[ ] GATE 5: PR template preenchido (todos os campos obrigatórios)
[ ] docker compose up continua funcional (6 serviços healthy)
[ ] Critérios adicionais do tipo desta história verificados
[x] PM Franciele notificada para review (auto-aprovado para riscos LOW/MEDIUM)
```

---

## 6. Fluxo de trabalho

```text
┌──────────────────────────────────────────────────────────────────┐
│  CICLO DE SAÍDA — DA CONCLUSÃO AO MERGE                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Desenvolvimento concluído                                      │
│            │                                                     │
│            ▼                                                     │
│   Verificar DoD (este documento)                                 │
│            │                                                     │
│            ▼                                                     │
│   ./gradlew test verde? ──NÃO──► corrigir ──┐                   │
│            │ SIM                             │                   │
│            ▼                                 │                   │
│   pitest ≥ 95%? ────────NÃO──► adicionar testes ─┘              │
│            │ SIM                                                 │
│            ▼                                                     │
│   Coda documentado? ────NÃO──► documentar ──┐                   │
│            │ SIM                             │                   │
│            ▼                                 │                   │
│   PR aberta (PR template) ◄──────────────────┘                  │
│            │                                                     │
│            ▼                                                     │
│   PM revisa gates? ─────NÃO──► dupla corrige ──┐                │
│            │ SIM                                │                │
│            ▼                                    │                │
│   PR aprovada ◄─────────────────────────────────┘               │
│            │                                                     │
│            ▼                                                     │
│   Merge → branch bleeding atualizada → issue fechada            │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 7. O que NÃO é requisito

Evitar over-engineering. Os itens abaixo **não** fazem parte da DoD:

| Item | Por que NÃO é requisito |
|---|---|
| 100% de mutation score | Custo exponencial; 95% é o ponto definido nos OKRs |
| Testes de performance (JMeter, Gatling) | Fora do escopo desta modernização |
| Todos os cenários E2E possíveis | 5 fluxos críticos cobrem os casos de maior risco |
| GraalVM Native Image | Complexidade sem benefício para este projeto |
| Testes de segurança (penetration testing) | Fora do escopo desta modernização |
| H2 como banco alternativo nos testes | Proibido — apenas PostgreSQL via Testcontainers |

---

## 8. Responsabilidades

| Papel | Responsabilidade na DoD |
|---|---|
| **Dupla (ambos)** | Rodar o checklist rápido antes de abrir a PR; verificar os 5 gates |
| **Dupla A (tech lead)** | Pitest, testes de integração, Playwright, contrato de API |
| **Dupla B** | Coda documentado, branch `bleeding` atualizada, mensagem de commit |
| **PM (Franciele)** | PR template preenchido; critérios de aceitação atendidos; aprovar ou solicitar ajustes |

---

## 9. Atualizações desta DoD

Este documento é atualizado **via Pull Request** com commit do tipo `docs`:

```bash
git commit -m "docs(process): update definition of done — <motivo>"
```

### Histórico de versões

| Versão | Data | O que mudou |
|---|---|---|
| 1.0 | Junho 2026 | Versão inicial — 5 gates, critérios universais, critérios por tipo, checklist, fluxo, não-requisitos, responsabilidades |

---

> **Fonte de verdade para DoD neste repositório.**
> Templates de issue: [`.github/ISSUE_TEMPLATE/`](../../.github/ISSUE_TEMPLATE/)
> Template de PR: [`.github/PULL_REQUEST_TEMPLATE.md`](../../.github/PULL_REQUEST_TEMPLATE.md)
> Definition of Ready: [`docs/process/definition-of-ready.md`](./definition-of-ready.md)
