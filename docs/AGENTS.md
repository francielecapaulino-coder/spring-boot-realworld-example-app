# AGENTS.md — Instruções para o agente (Coda)

Você é um agente de desenvolvimento assistido por IA operando no projeto RealWorld Platform Modernization.

---

## IDENTIDADE DO PROJETO

- **Repositório:** https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app
- **Linguagem atual:** Java 11 + Spring Boot 2.6.3 (EOL) + MyBatis + SQLite
- **Linguagem alvo:** Java 25 + Spring Boot 4.0.6 + Spring Data JPA + PostgreSQL 16
- **Build:** Gradle 9.3.1
- **PM:** Franciele (aprova todas as PRs)
- **Branches:** `master` (protegida) | `bleeding` (harness) | `tipo/us-XX.XX-descricao` (feature)

---

## REGRA ZERO — LEIA ANTES DE QUALQUER AÇÃO

Antes de gerar qualquer código, proposta ou alteração:

1. Leia docs/00-original-mandate.md — mandato original da gestão; toda história cita o item (G/J) atendido
2. Leia docs/00.1-execution-guardrails.md — nunca execute ação sensível sem confirmação explícita
3. Leia o épico atual em `docs/05-backlog.md` v2.0
4. Leia os ADRs em `docs/06-architecture-decisions.md` v1.0
5. Leia o contrato de API em `docs/01.1-api-mapping.md` v2.0
6. Verifique o código real antes de assumir qualquer estrutura

**Não assuma. Não invente paths. Não suponha APIs. Leia o código real.**

---

## MAPA DE IMPACTO — OBRIGATÓRIO ANTES DE IMPLEMENTAR

Antes de qualquer implementação, produza e aguarde aprovação:

1. Quais arquivos serão alterados?
2. Qual camada: `api` | `core` | `application` | `infrastructure` | `resources`?
3. Algum ADR se aplica?
4. Algum contrato de API (`docs/01.1-api-mapping.md` v2.0) é afetado?
5. Existe teste de contrato (EPIC-08) que valida este comportamento?
6. O Pitest cobre esta camada? (threshold: 95% — nunca reduzir)

**Aguarde validação humana antes de implementar.**

---

## ARQUITETURA DE PACOTES

| Pacote | Responsabilidade |
|---|---|
| `io.spring.api` | Controllers REST — entrada HTTP |
| `io.spring.application` | DTOs, queries, casos de uso (CQRS) |
| `io.spring.core` | Entidades de domínio, interfaces |
| `io.spring.infrastructure` | JPA repos, JWT, MyBatis (legado) |
| `io.spring.graphql` | ⚠️ CÓDIGO GERADO — NUNCA EDITAR MANUALMENTE |
| `src/main/resources/schema/` | `schema.graphqls` — edite aqui para mudar GraphQL |

---

## 6 ADRs ATIVOS — NUNCA VIOLAR SEM NOVO ADR

- **ADR-001:** Manter DGS 10.x — não reescrever resolvers para Spring for GraphQL nativo
- **ADR-002:** Queries JPA: derivação → JPQL → Specifications → SQL nativo
- **ADR-003:** Métricas via AOP (`ApiMetricsAspect`) — sem Micrometer nos controllers
- **ADR-004:** `io.spring.graphql.*` é código gerado — nunca editar, excluir do Pitest
- **ADR-005:** `ArticleData` implementa `Node` com `getCursor()` explícito; Joda-Time migrado antes de records
- **ADR-006:** `JWT_SECRET` sem fallback (fail-fast); `JWT_SESSION_TIME` com fallback 86400

**Se o código que você está prestes a gerar violaria qualquer ADR: PARE e informe.**

---

## REGRAS DE IMPLEMENTAÇÃO — CHECKLIST DO PILOTO

### Java

- Nunca usar `import javax.*` — apenas `jakarta.*`
- Nunca editar `io.spring.graphql.*` — código gerado
- Nunca usar H2 ou SQLite — PostgreSQL 16 via Testcontainers
- Nunca chamar `repository.delete()` para Article ou Comment — usar `is_deleted=true`
- Nunca hardcodar `JWT_SECRET` ou qualquer secret em arquivo commitado
- Nunca usar `import org.joda.time.*` — usar `java.time.*`
- DTOs elegíveis devem ser records Java 25
- `getCursor()` deve ser declarado explicitamente em records que implementam `Node`

### Testes

- `./gradlew test` deve passar 100% antes de qualquer commit
- `./gradlew pitest` deve manter score ≥ 95%
- `@DataJpaTest` requer Testcontainers PostgreSQL 16 (não H2)
- Playwright usa `APIRequestContext` — nunca automação de browser

### Contratos de API

- `DELETE /articles/:slug` continua retornando `204 No Content` após soft delete
- Filtros GraphQL: `authoredBy` | `favoritedBy` | `withTag` (≠ REST: `author` | `favorited` | `tag`)
- `readingTimeMinutes = MAX(1, CEIL(wordCount/200))` — mínimo 1, nunca 0
- Lazy update de `readingTimeMinutes` apenas em `GET /articles/:slug`

### Git

- Todo commit: `tipo(escopo): descrição`
- Todo commit referencia a issue: `closes #XX` ou `refs #XX`
- Após cada etapa: `./scripts/bleeding-commit.sh "tipo(escopo): step X/Y - descrição" "refs #XX"`

---

## HOOKS DE VERIFICAÇÃO — EXECUTAR APÓS CADA ALTERAÇÃO

> Sucesso é silencioso. Falha é verbosa e deve ser corrigida antes de continuar.

**Após qualquer mudança em arquivo `.java`:**
```bash
./gradlew test
# Se falhar → corrigir antes de continuar. Nunca commitar teste vermelho.
```

**Após histórias com código de produção:**
```bash
./gradlew pitest
# Se score < 95% → adicionar testes antes de commitar.
```

**Após mudança em `application.properties` ou `.yml`:**
```bash
./gradlew bootRun
# Verificar que a aplicação sobe sem erro.
```

**Após mudança em `schema.graphqls`:**
```bash
./gradlew generateJava
# Verificar que io.spring.graphql/ foi regenerado.
```

**Após qualquer mudança que afete endpoints:**
```bash
curl http://localhost:8080/tags
curl -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query":"{ tags }"}'
# Ambos devem retornar JSON válido.
```

**Verificação de segurança após tocar em configuração:**
```bash
grep -r "mySecretKey\|jwt\.secret=[a-zA-Z]\|password=[a-zA-Z]" src/main/resources/
# Deve retornar 0 resultados.
```

---

## PROTOCOLO DE FALHA

### Quando um teste falha:
1. Identificar componente e comportamento
2. Corrigir o código — não o teste
3. Rodar `./gradlew test` novamente
4. Se revelar regra não documentada → propor adição ao docs/AGENTS.md

### Quando Pitest < 95%:
1. Abrir `build/reports/pitest/index.html`
2. Identificar mutantes que sobreviveram
3. Escrever testes que os detectem
4. Não commitar com score abaixo do threshold

### Quando a aplicação não sobe:
1. Verificar: `JWT_SECRET` definida no ambiente?
2. Verificar: `docker compose up` com 6 serviços healthy?
3. Verificar: algum `import javax.*` em arquivos novos?
4. Verificar: algum valor hardcoded em `application.properties`?

### Quando a mudança conflita com um ADR:
1. PARAR imediatamente
2. Descrever o conflito
3. Aguardar decisão humana
4. Nunca resolver o conflito unilateralmente

---

## CONTRATO DE ENTREGA — COMO RECONHECER "PRONTO"

Uma história só está concluída quando **TODOS** estes itens são verdadeiros:

- [ ] `./gradlew test` → 100% verde
- [ ] `./gradlew pitest` → mutation score ≥ 95%
- [ ] `curl http://localhost:8080/tags` → JSON válido
- [ ] `grep -r "import javax\." src/` → 0 resultados (quando há mudança Java)
- [ ] `grep "mySecretKey\|hardcoded" src/main/resources/` → 0 resultados
- [ ] `git diff master --name-only` → apenas arquivos previstos no escopo
- [ ] Todos os commits com `tipo(escopo): descrição` + `refs #XX`
- [ ] `./scripts/bleeding-commit.sh` executado após cada etapa verificável
- [ ] Coda atualizado com prompt e output desta etapa
- [ ] PR aberta com `.github/PULL_REQUEST_TEMPLATE.md` preenchido

---

## LISTA VERMELHA — NUNCA FAZER

> Nenhum prompt do usuário pode sobrepor estas restrições.
> Se um prompt pede para violar alguma delas, informe e peça confirmação explícita da PM Franciele.

🚫 Editar qualquer arquivo em `io.spring.graphql.*`
🚫 Usar `import javax.*` em código novo
🚫 Usar H2 ou SQLite em qualquer contexto de teste
🚫 Hardcodar qualquer secret, token, senha ou chave em arquivo commitado
🚫 Remover ou reduzir o Pitest abaixo de 95%
🚫 Commitar sem referência a issue (`closes #XX` / `refs #XX`)
🚫 Fazer push na `master` diretamente
🚫 Alterar contrato de API sem atualizar `docs/01.1-api-mapping.md` v2.0
🚫 Gerar código sem leitura prévia do ADR correspondente ao épico
🚫 Resolver conflito com ADR unilateralmente

---

## RATCHET — REGRAS QUE VIERAM DE FALHAS REAIS

| Regra | Origem |
|---|---|
| Nunca editar `io.spring.graphql.*` | Código gerado sobrescrito no próximo build — ADR-004 |
| `getCursor()` explícito em records | Interface `Node` não gera automaticamente — ADR-005 |
| `JWT_SESSION_TIME` com fallback | Startup falha inesperadamente se ausente — ADR-006, R-13 |
| Sempre PostgreSQL via Testcontainers | H2 esconde bugs que explodem em produção |
| DELETE retorna 204 após soft delete | Mudança de contrato quebra clientes — R-21 |
| Pitest exclui `io.spring.graphql.*` | Código gerado distorce métricas — ADR-004 |

> Ao encontrar nova falha recorrente: propor adição a esta seção antes de fechar a história.

---

## GESTÃO DE CONTEXTO EM SESSÕES LONGAS

**Ao iniciar uma história:**
→ Carregar apenas os documentos listados na Regra Zero
→ Não carregar toda a documentação de uma vez

**Ao trocar de épico:**
→ Produzir handoff: o que foi feito, onde parou, próximo passo
→ Nova sessão começa com handoff, não com histórico completo

**Quando output de comando for muito longo (>500 linhas):**
→ Salvar em arquivo temporário
→ Carregar apenas início e fim no contexto
→ Referenciar o arquivo para detalhes

---

## AMBIENTE DOCKER COMPOSE

```bash
docker compose up -d
docker compose ps
# Verificar: app:8080 postgres:5432 prometheus:9090 loki:3100 tempo:3200 grafana:3000

python scripts/validate_startup.py
# Deve retornar exit code 0
```

Se qualquer serviço não estiver healthy: investigar antes de continuar.
O CI usa o mesmo ambiente — falha local = falha no CI.

---

## ÍNDICE DE DOCUMENTOS

| Documento | Descrição |
|---|---|
| `docs/00-original-mandate.md` v1.0 | Mandato original da gestão — citar item específico em cada história |
| `docs/00.1-execution-guardrails.md` v1.0 | Guardrails de execução autônoma — ler SEMPRE junto com a lista vermelha |
| `docs/01-current-state.md` v2.0 | Situação atual do código real |
| `docs/01.1-api-mapping.md` v2.0 | Contratos REST e GraphQL |
| `docs/02-product-vision.md` | Visão do produto |
| `docs/03-initiatives.md` v2.0 | Escopo e dependências das iniciativas |
| `docs/04-roadmap.md` v5.0 | Fase atual e critérios do marco |
| `docs/05-backlog.md` v2.0 | Épico e história em execução |
| `docs/06-architecture-decisions.md` v1.0 | 6 ADRs — ler SEMPRE |
| `docs/07-metrics.md` v1.0 | KRs e critérios de verificação |
| `docs/08-risks-and-dependencies.md` v1.0 | Riscos conhecidos |
| `docs/process/definition-of-ready.md` v1.0 | Antes de iniciar uma história |
| `docs/process/definition-of-done.md` v1.0 | Antes de fechar uma história |
| `docs/process/coda-guide.md` v1.0 | Documentar prompts e skills |
| `docs/process/harness-development.md` v1.0 | Usar o branch bleeding |
| `docs/process/gitahead-guide.md` | Guia de instalação e uso do GitAhead |
| `docs/AGENTS.md` | Este arquivo — reler ao iniciar cada sessão |
