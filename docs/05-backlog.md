# 05 — Backlog
> Versão 2.0 · Junho 2026  
> Projeto: RealWorld Platform Modernization  
> Responsável: Product Management

---

## Registro de revisões

| Versão | O que mudou |
|---|---|
| v1.0 | Versão inicial — 13 épicos, 110 histórias previstas |
| **v2.0** | **4 correções pós double-check e cross-check do GitHub:** EPIC-02 reescrito com `JWT_SESSION_TIME` (GAP-4/GAP-8 — `DefaultJwtService.java` confirmado); EPIC-10 com Gameday 1 explícito e Gameday 2 na Fase 6 (GAP-G); EPIC-11 separado em setup Fase 4 + update campos Fase 6 (GAP-E); EPIC-12 com HTTP `204 No Content` especificado para soft delete (GAP-NEW) |

---

## O que é este documento

Este documento descreve os **épicos** do projeto — unidades de trabalho grandes o suficiente para agrupar múltiplas histórias, mas bem definidas o suficiente para ter valor claro, escopo delimitado e critérios verificáveis de conclusão.

Cada épico contém:
- **Contexto:** por que existe e o que o motivou — incluindo evidências do código real quando relevante
- **Objetivo:** o que precisa ser verdadeiro quando concluído
- **Escopo:** o que está dentro e o que está deliberadamente fora
- **Comportamento esperado:** o que muda para quem usa ou mantém o sistema
- **Regras de negócio:** as regras que as histórias precisarão implementar
- **Critérios de aceitação:** conjunto mínimo verificável para considerar o épico concluído
- **Histórias previstas:** lista de histórias a serem escritas
- **Dependências:** o que precisa estar pronto antes de iniciar

> **Para vibe coding:** cada épico é o briefing que precede a geração de código. Leia o épico completo, consulte os ADRs referenciados em `06-architecture-decisions.md`, documente os prompts no Coda antes de gerar qualquer código, e referencie o épico na issue correspondente.

---

## Índice de épicos

| ID | Nome | Fase | Iniciativa | Status |
|---|---|---|---|---|
| [EPIC-01](#epic-01) | Fundação do processo de desenvolvimento | Fase 0 | INI-01 | 🔵 Planejado |
| [EPIC-02](#epic-02) | Segurança e eliminação de vulnerabilidades | Fase 1 | INI-02 | 🔵 Planejado |
| [EPIC-03](#epic-03) | Containerização e ambiente local reproduzível | Fase 1 | INI-03 | 🔵 Planejado |
| [EPIC-04](#epic-04) | Modernização do runtime — Java 25 + Spring Boot 4.0.6 + Gradle 9.3.1 | Fase 3 | INI-04 | 🔵 Planejado |
| [EPIC-05](#epic-05) | Migração de persistência — MyBatis para Spring Data JPA | Fase 3 | INI-05 | 🔵 Planejado |
| [EPIC-06](#epic-06) | Modernização de código — record types Java 25 | Fase 3 | INI-06 | 🔵 Planejado |
| [EPIC-07](#epic-07) | Qualidade — cobertura por mutação com Pitest | Fase Testes | INI-07 | 🔵 Planejado |
| [EPIC-08](#epic-08) | Testes de contrato — REST e GraphQL | Fase Testes | INI-08 | 🔵 Planejado |
| [EPIC-09](#epic-09) | Testes end-to-end com Playwright | Fase Testes + Fase 6 | INI-09 | 🔵 Planejado |
| [EPIC-10](#epic-10) | Observabilidade com LGTM Stack | Fase 4 | INI-10 | 🔵 Planejado |
| [EPIC-11](#epic-11) | Documentação de API — OpenAPI / Swagger | Fase 4 + Fase 6 | INI-11 | 🔵 Planejado |
| [EPIC-12](#epic-12) | Soft delete de artigos e comentários | Fase 5 | INI-12 | 🔵 Planejado |
| [EPIC-13](#epic-13) | Tempo de leitura com cache lazy | Fase 5 | INI-13 | 🔵 Planejado |

**Legenda:** 🔵 Planejado · 🟡 Em andamento · 🟢 Concluído · 🔴 Bloqueado · ⚫ Cancelado

---

## EPIC-01 — Fundação do processo de desenvolvimento {#epic-01}

### Contexto

O projeto usa vibe coding — desenvolvimento assistido por IA com geração rápida de código. Sem processo estabelecido antes de começar, a velocidade de geração se torna um risco: código acumulado sem rastreabilidade, sem contexto de decisão, sem critério de quando está pronto.

Este épico não entrega código de produto. Entrega a infraestrutura de processo que torna todos os outros épicos auditáveis, revisáveis e rastreáveis.

### Objetivo

Quando este épico estiver concluído, qualquer membro do time consegue:
- Criar uma issue com contexto suficiente para outra pessoa continuar o trabalho
- Commitar seguindo o padrão estabelecido sem consultar documentação
- Saber exatamente o que precisa estar pronto para começar e para terminar uma tarefa
- Documentar os prompts e skills usados em cada etapa do desenvolvimento

### Escopo

**Está incluído:**
- Templates de GitHub Issues para os tipos: `feat`, `bug`, `chore`, `refactor`, `spike`
- Template de Pull Request com checklist obrigatório incluindo link para issue
- Definition of Ready (DoR) — critérios que uma issue precisa atender antes de entrar em desenvolvimento
- Definition of Done (DoD) — critérios que o código precisa atender para a issue ser fechada
- Configuração do commitlint no CI com o padrão Conventional Commits
- Configuração do branch `bleeding` para commits automáticos do harness development
- Guia de instalação e uso do GitAhead
- Setup do workspace Coda com estrutura de documentação por etapa

**Está fora do escopo:**
- Integração com ferramentas externas de gestão como Jira ou Linear — GitHub Issues é suficiente
- Automação de changelogs ou release notes — escopo de Fase 6
- Qualquer código de produto

### Comportamento esperado após o épico

| Antes | Depois |
|---|---|
| Commits com mensagens arbitrárias | CI rejeita automaticamente qualquer commit fora do padrão Conventional Commits |
| Trabalho iniciado sem descrição formal | Nenhuma PR é aprovada sem issue vinculada com DoR atendida |
| "Pronto" é subjetivo | DoD é checklist verificável — PM valida antes de fechar qualquer issue |
| Prompts de IA sem registro | Cada etapa tem prompts e skills documentados no Coda antes de avançar |
| Histórico de Git ilegível | GitAhead mostra histórico visual estruturado e legível por todo o time |

### Regras de negócio deste épico

1. **Todo commit deve seguir Conventional Commits:** `tipo(escopo): descrição`. Tipos permitidos: `feat`, `fix`, `chore`, `docs`, `test`, `refactor`, `ci`, `perf`, `style`. O CI rejeita automaticamente o que estiver fora do padrão.
2. **Toda PR deve referenciar uma issue:** sem link para issue no template de PR, a PR não pode ser aprovada.
3. **DoR antes de codar:** uma issue só entra em desenvolvimento quando todos os itens do DoR estão verificados.
4. **DoD antes de fechar:** uma issue só é fechada quando todos os itens do DoD estão verificados pela PM.
5. **Coda obrigatório:** cada etapa de desenvolvimento deve ter prompts e skills documentados antes de avançar para a próxima.
6. **Branch bleeding:** todo commit de desenvolvimento vai automaticamente para o branch `bleeding` além do branch de trabalho.

### Critérios de aceitação do épico

- [ ] CI rejeita commit com formato inválido (testado manualmente com push fora do padrão)
- [ ] Criar nova issue no GitHub exibe pelo menos 3 templates diferentes
- [ ] Abrir PR exibe template com checklist incluindo campo de link para issue
- [ ] DoR está acessível em `docs/process/definition-of-ready.md`
- [ ] DoD está acessível em `docs/process/definition-of-done.md`
- [ ] Branch `bleeding` existe e recebe commits automáticos
- [ ] Coda workspace está acessível com link no README do repositório
- [ ] GitAhead documentado e instalado em pelo menos um ambiente do time
- [ ] Pelo menos uma issue foi criada usando cada template disponível

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-01.01 | Configurar commitlint no CI com regras Conventional Commits | `chore` |
| US-01.02 | Criar templates de GitHub Issues por tipo de trabalho | `chore` |
| US-01.03 | Criar template de Pull Request com checklist obrigatório | `chore` |
| US-01.04 | Documentar Definition of Ready do projeto | `docs` |
| US-01.05 | Documentar Definition of Done do projeto | `docs` |
| US-01.06 | Configurar branch `bleeding` e harness de commits automáticos | `chore` |
| US-01.07 | Configurar workspace Coda com estrutura de documentação por etapa | `chore` |
| US-01.08 | Documentar guia de instalação e uso do GitAhead | `docs` |

### Dependências

- Nenhuma — este épico é o ponto de partida de todo o projeto

---

## EPIC-02 — Segurança e eliminação de vulnerabilidades {#epic-02}

> **Versão:** 2.0 · Corrigido com base no cross-check do código real (GAP-4/GAP-8)  
> **ADR relacionado:** [ADR-006 — Variáveis de ambiente JWT](./06-architecture-decisions.md#adr-006)

### Contexto

Existem duas vulnerabilidades críticas no projeto que precisam ser eliminadas antes de qualquer outro trabalho. A primeira é imediata e cirúrgica. A segunda prepara a infraestrutura de configuração que toda a modernização vai precisar.

**Vulnerabilidade 1 — Configuração JWT exposta no código**

O `DefaultJwtService.java` (confirmado no repositório) lê **duas propriedades** diretamente do `application.properties` via `@Value`:

```java
@Autowired
public DefaultJwtService(
    @Value("${jwt.secret}") String secret,
    @Value("${jwt.sessionTime}") int sessionTime) {
```

Ambas estão hardcoded no `application.properties` que faz parte do repositório público:

- **`jwt.secret`** — chave criptográfica que assina os tokens JWT. Qualquer pessoa com acesso ao código pode gerar tokens válidos para qualquer usuário, assumindo identidade de qualquer conta. O mecanismo de autenticação está completamente comprometido.
- **`jwt.sessionTime`** — tempo de expiração dos tokens em segundos. Não é segredo criptográfico, mas é configuração operacional que precisa variar por ambiente (dev: sessão longa; prod: sessão curta) e não deve estar fixada no código.

**Vulnerabilidade 2 — Configuração única para todos os ambientes**

Não existem perfis de ambiente separados. Configuração de desenvolvimento, homologação e produção coexistem no mesmo arquivo sem separação, aumentando o risco de configurações incorretas se propagarem entre ambientes.

### Objetivo

Quando este épico estiver concluído:
- `jwt.secret` e `jwt.sessionTime` são lidas de variáveis de ambiente — nenhum valor hardcoded no repositório
- O scan automático de CI impede que novos valores sensíveis entrem no código por acidente
- `JWT_SECRET` ausente causa falha imediata e clara no startup
- `JWT_SESSION_TIME` ausente usa fallback de 86400 segundos (24h) sem falhar
- Três ambientes têm configurações separadas e documentadas
- O `.env.example` documenta **ambas** as variáveis com comentários

### Escopo

**Está incluído:**
- Remoção de `jwt.secret` **e** `jwt.sessionTime` hardcoded do `application.properties`
- Configuração para leitura de `${JWT_SECRET}` (sem fallback) e `${JWT_SESSION_TIME:86400}` (com fallback)
- Scan automático de secrets no CI (truffleHog ou git-secrets)
- Arquivo `.env.example` com **ambas** as variáveis documentadas com comentários e exemplos por ambiente
- Perfis Spring: `application-dev.yml`, `application-staging.yml`, `application-prod.yml`
- Mensagem de erro clara quando `JWT_SECRET` não está definida
- Atualização do `CONTRIBUTING.md` com instruções de configuração para ambas as variáveis

**Está fora do escopo:**
- HashiCorp Vault ou AWS Secrets Manager — infraestrutura de produção avançada
- Rotação automática de tokens JWT — funcionalidade futura
- HTTPS/TLS — responsabilidade da camada de proxy/load balancer
- Qualquer mudança na lógica de autenticação — apenas onde as propriedades vivem
- Limpeza do histórico Git com os valores antigos — riscos maiores que o benefício em projetos educacionais

### Comportamento esperado após o épico

| Cenário | Comportamento |
|---|---|
| `JWT_SECRET` definida, `JWT_SESSION_TIME` definida | Aplicação inicia com as configurações fornecidas |
| `JWT_SECRET` definida, `JWT_SESSION_TIME` **ausente** | Aplicação inicia com fallback de **86400 segundos (24h)** |
| `JWT_SECRET` **ausente** | Aplicação **falha no startup** com mensagem clara — não sobe |
| Ambas ausentes | Aplicação **falha no startup** pela ausência de `JWT_SECRET` |
| Developer commita com secret hardcoded | CI falha indicando arquivo e linha |
| Novo developer clona o repositório | Encontra `.env.example` com ambas as variáveis documentadas |
| Deploy em `dev` | Usa `application-dev.yml` — `JWT_SESSION_TIME` padrão: 604800 (7 dias) |
| Deploy em `staging` | Usa `application-staging.yml` — `JWT_SESSION_TIME` padrão: 86400 (24h) |
| Deploy em `prod` | Usa `application-prod.yml` — `JWT_SESSION_TIME` padrão: 3600 (1h) |

### Configuração esperada

**`application.properties` após a correção:**
```properties
# JWT_SECRET: sem fallback — falha imediatamente se ausente
# JWT_SESSION_TIME: fallback 86400 (24h) — não falha se ausente
jwt.secret=${JWT_SECRET}
jwt.sessionTime=${JWT_SESSION_TIME:86400}
```

**`application-dev.yml`:**
```yaml
jwt:
  secret: ${JWT_SECRET}
  sessionTime: ${JWT_SESSION_TIME:604800}   # 7 dias em dev
```

**`application-staging.yml`:**
```yaml
jwt:
  secret: ${JWT_SECRET}
  sessionTime: ${JWT_SESSION_TIME:86400}    # 24 horas em staging
```

**`application-prod.yml`:**
```yaml
jwt:
  secret: ${JWT_SECRET}
  sessionTime: ${JWT_SESSION_TIME:3600}     # 1 hora em produção
```

**`.env.example`:**
```bash
# =============================================================
# CONFIGURAÇÃO JWT
# =============================================================

# Chave secreta para assinatura de tokens JWT
# OBRIGATÓRIA — a aplicação NÃO inicia sem este valor
# Gerar com: openssl rand -base64 64
JWT_SECRET=your-secret-here-min-32-chars

# Tempo de expiração dos tokens em segundos
# OPCIONAL — padrão: 86400 (24h) se não definida
# Exemplos: dev=604800 (7d) | staging=86400 (24h) | prod=3600 (1h)
JWT_SESSION_TIME=86400
```

### Regras de negócio deste épico

1. **`JWT_SECRET` sem fallback — fail fast:** chave criptográfica não pode ter valor padrão seguro. Se ausente, a aplicação falha imediatamente com mensagem clara antes de qualquer inicialização de componentes.
2. **`JWT_SESSION_TIME` com fallback razoável:** não é segredo — é configuração operacional. O valor 86400 (24h) é padrão seguro e razoável para qualquer ambiente que não defina a variável explicitamente.
3. **Nenhum valor de configuração JWT no código:** nem `jwt.secret=valor` nem `jwt.sessionTime=3600` em nenhum arquivo commitado. Apenas referências `${...}` são permitidas.
4. **`.env.example` sempre atualizado:** qualquer nova variável de ambiente adicionada ao projeto deve ser documentada no `.env.example` antes da PR ser aprovada — regra do DoD para todo o projeto.
5. **Scan no CI é o primeiro step:** executa antes do build e dos testes. Um commit que escapa do scan pode chegar ao histórico.
6. **Perfis obrigatórios para novos deploys:** nenhum deploy deve usar `application.properties` diretamente em staging ou prod — ativar perfil via `SPRING_PROFILES_ACTIVE`.

### Critérios de aceitação do épico

- [ ] `grep "jwt.secret=" application.properties` → retorna apenas `jwt.secret=${JWT_SECRET}`
- [ ] `grep "jwt.sessionTime=" application.properties` → retorna apenas `jwt.sessionTime=${JWT_SESSION_TIME:86400}`
- [ ] `truffleHog scan .` → retorna 0 findings críticos
- [ ] Iniciar sem `JWT_SECRET` → falha no startup com mensagem clara
- [ ] Iniciar com `JWT_SECRET=valor` sem `JWT_SESSION_TIME` → aplicação sobe com sessão de 86400s
- [ ] Iniciar com `JWT_SECRET=valor` e `JWT_SESSION_TIME=3600` → sessão de 3600s
- [ ] `.env.example` existe com `JWT_SECRET` e `JWT_SESSION_TIME` documentados com comentários
- [ ] `application-dev.yml`, `application-staging.yml`, `application-prod.yml` existem com valores diferentes de `JWT_SESSION_TIME`
- [ ] CI falha quando secret simulado é adicionado ao código em branch de teste
- [ ] `CONTRIBUTING.md` descreve configuração de ambas as variáveis

### Histórias previstas

| ID | Título | Tipo | Observação |
|---|---|---|---|
| US-02.01 | Remover `jwt.secret` e `jwt.sessionTime` hardcoded do `application.properties` | `fix` | Ambas as propriedades — não apenas o secret |
| US-02.02 | Configurar `JWT_SECRET` com fail-fast e `JWT_SESSION_TIME` com fallback 86400 | `feat` | Ver ADR-006 para estratégia de fail-fast vs fallback |
| US-02.03 | Criar `.env.example` com ambas as variáveis JWT documentadas com comentários | `docs` | Incluir exemplos de valores por ambiente |
| US-02.04 | Criar perfis Spring `dev`, `staging`, `prod` com `JWT_SESSION_TIME` por perfil | `chore` | dev=604800, staging=86400, prod=3600 |
| US-02.05 | Configurar scan automático de secrets no CI como primeiro step do pipeline | `ci` | truffleHog ou git-secrets |
| US-02.06 | Documentar configuração de ambiente no `CONTRIBUTING.md` com ambas as variáveis | `docs` | Incluir como gerar `JWT_SECRET` seguro |

### Dependências

- EPIC-01 concluído (processo estabelecido — issue e commits rastreáveis)

---

## EPIC-03 — Containerização e ambiente local reproduzível {#epic-03}

### Contexto

Hoje o projeto não tem ambiente de desenvolvimento padronizado. Cada desenvolvedor monta o ambiente de uma forma diferente: versão diferente de banco de dados, sem observabilidade local, configurações divergentes. O resultado é o problema clássico do "funciona na minha máquina" e um onboarding que leva dias ou semanas.

Além disso, o banco de dados atual é SQLite — um arquivo local que não suporta múltiplas conexões de escrita simultâneas. Isso torna o sistema incapaz de operar com mais de um usuário ativo ao mesmo tempo.

Este épico resolve os dois problemas com uma solução única: um `docker-compose.yml` que sobe toda a stack necessária com um único comando.

### Objetivo

Quando este épico estiver concluído:
- Qualquer pessoa clona o repositório, executa `docker compose up` e tem o sistema completo rodando em menos de 15 minutos
- SQLite foi substituído por PostgreSQL 16
- A stack LGTM está disponível localmente para uso nas fases seguintes
- Um script Python automatizado valida que a aplicação sobe e encerra corretamente a cada execução do CI

### Escopo

**Está incluído:**
- `Dockerfile` multi-stage para a aplicação Spring Boot
- `docker-compose.yml` orquestrando: app + PostgreSQL 16 + Prometheus + Loki + Tempo + Grafana
- Substituição de SQLite por PostgreSQL 16 com migrations Flyway validadas
- Spring Actuator: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- Script Python `scripts/validate_startup.py` que valida logs de startup e shutdown
- `CONTRIBUTING.md` com guia de setup completo e verificável
- Medição cronometrada do onboarding com desenvolvedor real

**Está fora do escopo:**
- Ambiente de staging ou produção
- Dashboards Grafana customizados — infraestrutura só; métricas e dashboards são EPIC-10
- Kubernetes ou Docker Swarm

### Arquitetura do ambiente Docker Compose

```
docker-compose.yml
│
├── app (porta 8080)
│   ├── Depende de: postgres (healthcheck via /actuator/health)
│   └── Variáveis: JWT_SECRET, JWT_SESSION_TIME, SPRING_PROFILES_ACTIVE=dev
│
├── postgres (porta 5432) — postgres:16-alpine
│
├── prometheus (porta 9090) — coleta de /actuator/prometheus
├── loki (porta 3100) — logs estruturados
├── tempo (porta 3200) — traces
└── grafana (porta 3000) — dashboards (admin/admin)
```

### Comportamento esperado do script de validação

O script `scripts/validate_startup.py`:
1. Executa `docker compose up -d`
2. Aguarda até 60s pelo log de startup com campos: `event: "application_startup"`, `timestamp`, `version`, `environment`
3. Executa `docker compose stop`
4. Aguarda até 30s pelo log de shutdown com campos: `event: "application_shutdown"`, `timestamp`, `reason`
5. Retorna exit code 0 (sucesso) ou exit code 1 com mensagem descritiva (falha)

### Regras de negócio deste épico

1. **Onboarding em ≤ 15 minutos:** medido com desenvolvedor real, resultado documentado.
2. **SQLite proibido após este épico:** nenhum perfil pode usar SQLite como banco.
3. **PostgreSQL como única fonte de verdade:** migrations Flyway testadas contra PostgreSQL — nunca H2.
4. **Script de validação é gate do CI:** qualquer falha bloqueia o merge.
5. **Actuator obrigatório antes do Docker Compose:** `/actuator/health` é usado pelo compose para saber quando a aplicação está pronta.
6. **Grafana pré-configurado via provisioning:** fontes de dados não dependem de configuração manual.

### Critérios de aceitação do épico

- [ ] `docker compose up` → 6 serviços em estado `healthy`
- [ ] `curl http://localhost:8080/tags` → JSON válido
- [ ] `curl http://localhost:8080/actuator/health` → `{"status":"UP"}`
- [ ] Grafana acessível em `http://localhost:3000` com fontes de dados pré-configuradas
- [ ] `python scripts/validate_startup.py` → exit code 0
- [ ] CI executa o script e falha se exit code ≠ 0
- [ ] Dev novo subiu o ambiente em ≤ 15 minutos — tempo documentado
- [ ] Nenhuma referência a SQLite em nenhum perfil de ambiente

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-03.01 | Criar `Dockerfile` multi-stage para a aplicação Spring Boot | `chore` |
| US-03.02 | Configurar Spring Actuator com `/health`, `/info` e `/metrics` | `feat` |
| US-03.03 | Substituir SQLite por PostgreSQL 16 e validar migrations Flyway | `chore` |
| US-03.04 | Criar `docker-compose.yml` com app + PostgreSQL + stack LGTM | `chore` |
| US-03.05 | Configurar fontes de dados do Grafana via provisioning automático | `chore` |
| US-03.06 | Criar script Python de validação de startup e shutdown | `chore` |
| US-03.07 | Integrar script Python de validação ao pipeline CI | `ci` |
| US-03.08 | Atualizar `CONTRIBUTING.md` com guia de onboarding completo | `docs` |
| US-03.09 | Medir e documentar tempo de onboarding com desenvolvedor real | `chore` |

### Dependências

- EPIC-02 concluído (JWT via variável de ambiente — necessário para o Docker Compose injetar ambas as variáveis `JWT_SECRET` e `JWT_SESSION_TIME`)

---

## EPIC-04 — Modernização do runtime: Java 25 + Spring Boot 4.0.6 + Gradle 9.3.1 {#epic-04}

### Contexto

O projeto roda hoje sobre Java 11 e Spring Boot 2.6.3 — framework que atingiu fim de vida (EOL) em novembro de 2023. Java 11 perde suporte gratuito da Oracle em setembro de 2026. Java 25 é o LTS mais recente (GA setembro 2025, suporte até 2030) e traz virtual threads estáveis que permitem escalar concorrência sem reescrita de código.

Este é o épico de maior risco técnico do projeto porque afeta todo o codebase simultaneamente. Por isso, EPIC-07 e EPIC-08 devem iniciar em paralelo como rede de segurança.

**ADR relacionado:** [ADR-001 — DGS Framework vs Spring for GraphQL](./06-architecture-decisions.md#adr-001) · [ADR-004 — Pacote gerado io.spring.graphql](./06-architecture-decisions.md#adr-004) · [ADR-005 — Node interface e records](./06-architecture-decisions.md#adr-005)

### Objetivo

Quando este épico estiver concluído:
- Java 25 compilando e executando
- Spring Boot 4.0.6 como framework principal
- Gradle 9.3.1 sem nenhum warning de deprecação
- Virtual threads habilitados
- Zero imports `javax.*`
- Joda-Time removido e substituído por `java.time`
- Todos os testes passando sem regressão

### Escopo

**Está incluído:**
- Upgrade Java 11 → Java 25
- Upgrade Spring Boot 2.6.3 → 4.0.6
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

### Critérios de aceitação do épico

- [ ] `java -version` no container → Java 25
- [ ] `./gradlew --version` → Gradle 9.3.1
- [ ] `./gradlew build` → zero warnings de deprecação
- [ ] `./gradlew test` → 100% verde
- [ ] `grep -r "import javax\." src/` → 0 resultados
- [ ] `grep "joda" build.gradle` → 0 resultados
- [ ] `spring.threads.virtual.enabled=true` confirmado no log de startup
- [ ] `POST /graphql` com `{"query":"{ tags }"}` → retorna resultado válido
- [ ] ADR-001 documentado em `06-architecture-decisions.md`

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-04.01 | Documentar ADR-001 sobre DGS Framework vs Spring for GraphQL | `docs` |
| US-04.02 | Atualizar `build.gradle` para Java 25 e Gradle 9.3.1 | `chore` |
| US-04.03 | Atualizar Spring Boot de 2.6.3 para 4.0.6 e resolver conflitos de dependências | `chore` |
| US-04.04 | Migrar todos os imports `javax.*` para `jakarta.*` incluindo `DefaultJwtService` | `refactor` |
| US-04.05 | Reconfigurar Spring Security 6.x removendo `WebSecurityConfigurerAdapter` | `refactor` |
| US-04.06 | Atualizar DGS Framework para versão 10.x compatível com Spring Boot 4 | `chore` |
| US-04.07 | Remover Joda-Time e substituir por `java.time` em todo o codebase incluindo `ArticleData` e `DateTimeCursor` | `refactor` |
| US-04.08 | Habilitar virtual threads via `spring.threads.virtual.enabled=true` | `perf` |
| US-04.09 | Validar que todos os testes passam após o upgrade completo | `test` |

### Dependências

- EPIC-03 concluído (ambiente Docker com Java 25 disponível)
- EPIC-07 e EPIC-08 em andamento em paralelo (rede de segurança obrigatória)
- ADR-001 aprovado antes de iniciar qualquer história

---

## EPIC-05 — Migração de persistência: MyBatis para Spring Data JPA {#epic-05}

### Contexto

O projeto usa MyBatis como framework de acesso a dados. MyBatis exige que cada operação de banco seja escrita manualmente: interface de mapper, SQL em XML ou anotação, implementação de repositório. Para um projeto CRUD como este, esse nível de controle gera custo de manutenção desproporcional ao benefício.

Spring Data JPA gera automaticamente a maioria das operações CRUD. Os recursos de produto da Fase 5 dependem diretamente do JPA: soft delete usa `@Where(clause = "is_deleted = false")` e cache lazy usa repositórios JPA para persistir o valor calculado.

**ADR relacionado:** [ADR-002 — Spring Data JPA estratégia de queries](./06-architecture-decisions.md#adr-002)

### Objetivo

Quando este épico estiver concluído:
- Nenhuma referência a MyBatis existe no codebase ou no `build.gradle`
- Todas as operações de banco são executadas via Spring Data JPA
- Comportamento de todas as queries é funcionalmente idêntico ao anterior
- A base está preparada para `@Where` (soft delete) e lazy update (tempo de leitura)

### Escopo

**Está incluído:**
- Remoção de `mybatis-spring-boot-starter` do `build.gradle`
- Adição de `spring-boot-starter-data-jpa` e driver PostgreSQL
- Anotação de todas as entidades com `@Entity`, `@Table`, `@Id`, `@Column`
- Criação de interfaces `JpaRepository<T, ID>` para cada entidade
- Migração de queries customizadas para `@Query` JPQL ou Specifications (ver ADR-002)
- Remoção de todos os `*Mapper.java` e XMLs de mapper
- Configuração Hibernate/JPA no `application.yml`

**Está fora do escopo:**
- Soft delete com `@Where` — EPIC-12
- Cache de tempo de leitura — EPIC-13
- QueryDSL — `@Query` e Specifications cobrem os casos atuais
- Cache de segundo nível Hibernate
- Mudanças no schema do banco

### Mapeamento de migração

| Componente MyBatis | Equivalente JPA | Observação |
|---|---|---|
| `UserMapper.java` | `UserRepository extends JpaRepository<User, Long>` | `findByEmail()`, `findByUsername()` gerados por nome de método |
| `ArticleMapper.java` | `ArticleRepository extends JpaRepository<Article, Long>` | Filtros opcionais via Specifications (ver ADR-002) |
| `CommentMapper.java` | `CommentRepository extends JpaRepository<Comment, Long>` | Queries por `articleId` geradas automaticamente |
| `TagMapper.java` | `TagRepository extends JpaRepository<Tag, Long>` | `findByName()` gerado automaticamente |
| `MyBatisUserRepository.java` | Removido | JPA Repository substitui diretamente |
| XMLs `*.xml` de mapper | Removidos | `@Query` onde necessário |

### Regras de negócio deste épico

1. **Comportamento funcional idêntico:** testes de contrato do EPIC-08 são a rede de segurança.
2. **PostgreSQL real nos testes de repositório:** `@DataJpaTest` com Testcontainers — nunca H2.
3. **Estratégia de queries conforme ADR-002:** derivação por nome → JPQL → Specifications → SQL nativo.
4. **Entidades JPA não viram records:** JPA requer construtor sem argumentos — incompatível com records padrão (ver ADR-005).

### Critérios de aceitação do épico

- [ ] `grep "mybatis" build.gradle` → 0 resultados
- [ ] `find . -name "*Mapper.java"` → 0 resultados
- [ ] `find . -name "*.xml" -path "*/mapper/*"` → 0 resultados
- [ ] Todas as entidades têm `@Entity` e `@Table`
- [ ] Uma interface `JpaRepository` por entidade
- [ ] `./gradlew test` → 100% verde
- [ ] Testes de contrato REST e GraphQL (EPIC-08) passam sem alteração
- [ ] `@DataJpaTest` com Testcontainers PostgreSQL passa para cada repositório

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-05.01 | Substituir `mybatis-spring-boot-starter` por `spring-boot-starter-data-jpa` | `chore` |
| US-05.02 | Anotar entidade `User` com JPA e criar `UserRepository` | `refactor` |
| US-05.03 | Anotar entidade `Article` com JPA e criar `ArticleRepository` com Specifications | `refactor` |
| US-05.04 | Anotar entidade `Comment` com JPA e criar `CommentRepository` | `refactor` |
| US-05.05 | Anotar entidade `Tag` com JPA e criar `TagRepository` | `refactor` |
| US-05.06 | Migrar relacionamentos M:N (follows, favorites, article_tag) para `@ManyToMany` JPA | `refactor` |
| US-05.07 | Remover todos os `*Mapper.java` e XMLs de mapper | `refactor` |
| US-05.08 | Configurar Hibernate/JPA no `application.yml` para todos os perfis | `chore` |
| US-05.09 | Criar testes `@DataJpaTest` com Testcontainers PostgreSQL para cada repositório | `test` |

### Dependências

- EPIC-04 concluído (Spring Boot 4 com JPA disponível)
- EPIC-08 em andamento (testes de contrato como rede de segurança)

---

## EPIC-06 — Modernização de código: record types Java 25 {#epic-06}

### Contexto

Os DTOs da camada `application` são classes Java com getters, construtores, `equals()`, `hashCode()` e `toString()` — gerados manualmente ou via Lombok. São objetos que nunca deveriam ser mutáveis, mas nada no código impede isso.

Java 25 consolida `record types` como recurso estável. Records são classes imutáveis que eliminam todo esse boilerplate. O `ArticleData.java` implementa a interface `io.spring.application.Node` com o método `getCursor()` — records podem implementar interfaces normalmente, mas `getCursor()` deve ser declarado explicitamente no corpo do record. Além disso, `ArticleData` usa `Joda DateTime` que deve ser migrado em EPIC-04 antes desta conversão (ver ADR-005).

**ADR relacionado:** [ADR-005 — Node interface e cursor pagination](./06-architecture-decisions.md#adr-005)

### Objetivo

Quando este épico estiver concluído:
- ≥ 80% dos DTOs da camada `application` são record types
- Value objects elegíveis da camada `core` também são records
- Nenhum getter manual em classes convertidas
- Comportamento idêntico ao anterior

### Escopo

**Está incluído:**
- Conversão de DTOs de `application` para records: `ArticleData`, `UserData`, `ProfileData`, `CommentData`, `TagsData` e wrappers
- `ArticleData` convertido com `getCursor()` explícito implementando interface `Node` (ver ADR-005)
- `DateTimeCursor` convertido para record (após migração de Joda-Time em EPIC-04)
- Conversão de value objects elegíveis da camada `core`
- Remoção de Lombok nas classes convertidas

**Está fora do escopo:**
- Entidades JPA (`@Entity`) — JPA requer construtor sem argumentos; incompatível com records
- Sealed classes ou pattern matching — além do escopo desta tarefa
- Remoção total de Lombok — continua útil em entidades JPA

### Critério de elegibilidade para conversão

Uma classe é candidata a record se atende **todos** os critérios:
1. É DTO, objeto de transferência ou value object
2. Não é anotada com `@Entity`
3. Não precisa herdar de outra classe
4. Não tem estado mutável necessário externamente
5. Não tem serialização especial que quebre com records (verificar Jackson)

### Exemplo de conversão — `ArticleData` com interface `Node`

```java
// ANTES — classe com Lombok + interface Node
@Data @NoArgsConstructor @AllArgsConstructor
public class ArticleData implements Node {
    private String id;
    private String slug;
    // ... campos com Joda DateTime
    @Override
    public DateTimeCursor getCursor() {
        return new DateTimeCursor(updatedAt);
    }
}

// DEPOIS — record implementando interface Node (após EPIC-04 migrar Joda-Time)
public record ArticleData(
    String id,
    String slug,
    String title,
    String description,
    String body,
    boolean favorited,
    int favoritesCount,
    Instant createdAt,
    Instant updatedAt,
    List<String> tagList,
    @JsonProperty("author") ProfileData profileData
) implements Node {
    @Override
    public DateTimeCursor getCursor() {
        return new DateTimeCursor(updatedAt);  // getCursor() explícito no record
    }
}
```

### Regras de negócio deste épico

1. **EPIC-04 é pré-requisito obrigatório:** a migração de `Joda DateTime` → `java.time.Instant` em `ArticleData` e `DateTimeCursor` deve ocorrer em EPIC-04 antes desta conversão.
2. **`getCursor()` sempre explícito em records que implementam `Node`:** não é gerado automaticamente.
3. **Jackson serializa records corretamente:** Spring Boot 4 com Jackson 3.x suporta records nativamente. Verificar que todos os endpoints retornam JSON correto após a conversão.
4. **80% é threshold mínimo:** converter todos os DTOs elegíveis.

### Critérios de aceitação do épico

- [ ] ≥ 80% dos DTOs no pacote `application` são `record`
- [ ] `ArticleData` é record implementando `Node` com `getCursor()` explícito
- [ ] `DateTimeCursor` é record usando `java.time.Instant`
- [ ] Nenhum getter manual em classes convertidas
- [ ] Todos os endpoints REST retornam JSON correto após a conversão
- [ ] `./gradlew test` → 100% verde

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-06.01 | Mapear todos os DTOs da camada `application` e classificar elegibilidade para record | `chore` |
| US-06.02 | Converter `ArticleData` para record implementando `Node` com `getCursor()` explícito | `refactor` |
| US-06.03 | Converter `UserData` e `ProfileData` para record types | `refactor` |
| US-06.04 | Converter `CommentData`, `TagsData` e demais DTOs para record types | `refactor` |
| US-06.05 | Converter `DateTimeCursor` para record usando `java.time.Instant` | `refactor` |
| US-06.06 | Converter value objects elegíveis da camada `core` para record types | `refactor` |
| US-06.07 | Validar serialização JSON de todos os endpoints após conversão | `test` |

### Dependências

- EPIC-04 concluído (Java 25 **e** Joda-Time migrado para `java.time` — pré-requisito para `ArticleData`)

---

## EPIC-07 — Qualidade: cobertura por mutação com Pitest {#epic-07}

### Contexto

Cobertura de linhas mede quais linhas foram executadas — não se os testes verificam o comportamento correto. Testes de mutação resolvem isso: o Pitest modifica o código sistematicamente e verifica se os testes detectam essas mudanças. 95% de mutation score significa que 95 de cada 100 bugs artificialmente introduzidos são capturados.

**ADR relacionado:** [ADR-004 — Pacote gerado io.spring.graphql](./06-architecture-decisions.md#adr-004)

### Objetivo

Quando este épico estiver concluído:
- `./gradlew pitest` reporta mutation score ≥ 95%
- CI falha automaticamente se o score cair abaixo de 95%
- Relatório publicado como artefato do CI após cada build
- `io.spring.graphql` (código gerado pelo DGS Codegen) excluído da métrica

### Escopo

**Está incluído:**
- Configuração do plugin Pitest 1.15.x no `build.gradle`
- Threshold de 95% no CI como gate obrigatório
- Exclusão de `io.spring.graphql.*` da métrica (código gerado — ver ADR-004)
- Testes adicionais de unidade e slice para atingir o threshold
- Relatório HTML e XML publicado no CI

**Está fora do escopo:**
- Testes de integração de contrato — EPIC-08
- Testes E2E — EPIC-09
- 100% de mutation score — custo exponencial sem retorno proporcional

### Configuração esperada

```groovy
pitest {
    targetClasses   = ['io.spring.api.*',
                       'io.spring.core.*',
                       'io.spring.application.*',
                       'io.spring.infrastructure.*']
    excludedClasses = ['io.spring.graphql.*']  // código gerado DGS — nunca testar
    mutators        = ['STRONGER']
    threads         = 4
    outputFormats   = ['HTML', 'XML']
    timestampedReports    = false
    mutationThreshold     = 95
    coverageThreshold     = 95
    failWhenNoMutations   = true
    junit5PluginVersion   = '1.2.1'
}
```

### Regras de negócio deste épico

1. **95% é threshold mínimo e gate do CI:** qualquer push que faça o score cair abaixo de 95% bloqueia o merge.
2. **`io.spring.graphql.*` sempre excluído:** código gerado pelo DGS Codegen não é responsabilidade da equipe testar (ver ADR-004).
3. **Pitest roda contra testes de unidade e slice:** testes de integração com Testcontainers são muito lentos para rodar a cada mutação.

### Critérios de aceitação do épico

- [ ] `./gradlew pitest` executa sem erro de configuração
- [ ] Relatório HTML em `build/reports/pitest/index.html`
- [ ] Mutation score global ≥ 95%
- [ ] CI configurado: push com assert removido de teste crítico → CI vermelho
- [ ] Relatório publicado como artefato no CI
- [ ] `io.spring.graphql` excluído da métrica (verificado no relatório)

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-07.01 | Configurar plugin Pitest 1.15.x no `build.gradle` com threshold de 95% e exclusão de `io.spring.graphql.*` | `chore` |
| US-07.02 | Integrar Pitest ao pipeline CI como gate obrigatório | `ci` |
| US-07.03 | Configurar publicação do relatório Pitest como artefato do CI | `ci` |
| US-07.04 | Atingir mutation score ≥ 95% na camada `core` | `test` |
| US-07.05 | Atingir mutation score ≥ 95% na camada `application` | `test` |
| US-07.06 | Atingir mutation score ≥ 95% na camada `infrastructure` | `test` |
| US-07.07 | Atingir mutation score ≥ 95% na camada `api` | `test` |

### Dependências

- EPIC-03 concluído (Java 25 disponível via Docker)
- Deve iniciar em paralelo com EPIC-04 — não após

---

## EPIC-08 — Testes de contrato: REST e GraphQL {#epic-08}

### Contexto

Os testes de integração de contrato verificam que o que a API promete continua sendo entregue após qualquer mudança. São a rede de segurança que torna os upgrades de stack e as refatorações de ORM seguros. O projeto tem testes parciais. Este épico cobre 100% dos 19 endpoints REST e 18 operações GraphQL.

### Objetivo

Quando este épico estiver concluído:
- 100% dos 19 endpoints REST com ≥ 1 teste de sucesso e ≥ 1 de erro
- 100% das 18 operações GraphQL com ≥ 1 teste de sucesso e ≥ 1 de erro
- Todos os testes usam PostgreSQL 16 real via Testcontainers
- `./gradlew test` verde em qualquer ambiente com Docker

### Escopo

**Está incluído:**
- Testcontainers com PostgreSQL 16 para testes de integração
- Anotação customizada `@IntegrationTest` para reduzir boilerplate
- Testes de sucesso (2xx) para todos os endpoints e operações
- Testes de erro para cenários críticos: 401, 403, 404, 422
- Testes de autenticação e autorização

**Está fora do escopo:**
- Testes de performance ou carga
- Testes de segurança (penetration testing)
- Cobertura de todos os cenários possíveis

### Cobertura obrigatória

**REST — 19 endpoints:**

| Domínio | Endpoints | Cenários obrigatórios |
|---|---|---|
| Autenticação | `POST /users/login`, `POST /users` | Sucesso; credenciais inválidas; campos faltando |
| Usuário | `GET /user`, `PUT /user` | Sucesso; sem token; e-mail duplicado |
| Perfis | `GET /profiles/:username`, follow, unfollow | Sucesso; usuário inexistente; sem token |
| Artigos | Listar, feed, ver, criar, editar, deletar | Sucesso; sem permissão; slug inexistente; campo faltando |
| Favoritos | Favoritar, desfavoritar | Sucesso; sem token |
| Comentários | Listar, criar, deletar | Sucesso; sem permissão; artigo inexistente |
| Tags | `GET /tags` | Sucesso |

**GraphQL — 18 operações:** 6 queries + 12 mutations — ao menos 1 sucesso e 1 erro por operação.

### Regras de negócio deste épico

1. **PostgreSQL real obrigatório:** sem H2 ou banco em memória.
2. **Estado limpo entre testes:** `@Transactional` ou `@Sql` para limpeza.
3. **Tokens JWT válidos:** helper de teste que gera tokens — não mockar autenticação.
4. **Testar o contrato, não a implementação:** status code e campos do response.

### Critérios de aceitação do épico

- [ ] `@IntegrationTest` configurada e funcionando
- [ ] Testcontainers PostgreSQL 16 subindo no CI
- [ ] 19/19 endpoints REST com ≥ 1 teste de sucesso e ≥ 1 de erro
- [ ] 18/18 operações GraphQL com ≥ 1 teste de sucesso e ≥ 1 de erro
- [ ] `./gradlew test` → 100% verde no CI com Docker
- [ ] Nenhum teste usa H2 ou SQLite

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-08.01 | Configurar Testcontainers PostgreSQL e anotação `@IntegrationTest` | `test` |
| US-08.02 | Criar helper de geração de tokens JWT para testes | `test` |
| US-08.03 | Criar testes de integração para endpoints de autenticação e usuário | `test` |
| US-08.04 | Criar testes de integração para endpoints de perfil | `test` |
| US-08.05 | Criar testes de integração para endpoints de artigo | `test` |
| US-08.06 | Criar testes de integração para endpoints de favoritos, comentários e tags | `test` |
| US-08.07 | Criar testes de integração para todas as queries GraphQL | `test` |
| US-08.08 | Criar testes de integração para todas as mutations GraphQL | `test` |
| US-08.09 | Integrar suite de testes de integração ao pipeline CI | `ci` |

### Dependências

- EPIC-03 concluído (Docker disponível para Testcontainers)
- Deve iniciar em paralelo com EPIC-04 — não após

---

## EPIC-09 — Testes end-to-end com Playwright {#epic-09}

### Contexto

Playwright é usado aqui como **cliente HTTP via `APIRequestContext`** — não como automação de browser. Simula fluxos completos de múltiplas chamadas encadeadas do ponto de vista de um consumidor real da API.

A gestão requisitou dois branches específicos que documentam o processo: um com testes falhando (documenta o problema antes da solução) e um com testes passando.

Os fluxos 4 e 5 (soft delete) têm dependência cross-phase de EPIC-12 (Fase 5) — são adicionados ao branch `working` na Fase 6 após EPIC-12 estar concluído.

### Objetivo

Quando este épico estiver concluído (Fase Testes + Fase 6):
- Branch `feat/playwright-broken` existe com CI vermelho
- Branch `feat/playwright-working` com 5 fluxos passando e CI verde

### Escopo

**Está incluído:**
- Setup Playwright com `APIRequestContext`
- Branch `feat/playwright-broken` — testes escritos, CI vermelho
- Branch `feat/playwright-working` — fluxos 1, 2, 3 na Fase Testes; fluxos 4 e 5 na Fase 6

**Está fora do escopo:**
- Automação de browser ou testes de UI
- Playwright substituindo testes de integração do EPIC-08

### Fluxos E2E por fase

| Fluxo | Conteúdo | Fase | Depende de |
|---|---|---|---|
| 1 | Registro e autenticação completa | Fase Testes | EPIC-08 |
| 2 | Criação e leitura de artigo com `readingTimeMinutes` | Fase Testes | EPIC-08 |
| 3 | Interação social — follow, feed, unfollow | Fase Testes | EPIC-08 |
| 4 | Soft delete de artigo | **Fase 6** | **EPIC-12** |
| 5 | Soft delete de comentário | **Fase 6** | **EPIC-12** |

### Regras de negócio deste épico

1. **`APIRequestContext` apenas:** sem `page.goto()` ou automação de browser.
2. **Branch `broken` documentado intencionalmente:** não é erro — é documentação do processo. Mantido e não deletado.
3. **Estado limpo entre fluxos:** cada fluxo cria seus próprios dados de teste.
4. **`BASE_URL` via variável de ambiente:** configurável para diferentes ambientes.

### Critérios de aceitação do épico

- [ ] Branch `feat/playwright-broken` com CI vermelho (Fase Testes)
- [ ] Branch `feat/playwright-working` com CI verde e 3 fluxos base (Fase Testes)
- [ ] Branch `feat/playwright-working` expandido para 5 fluxos (Fase 6 — após EPIC-12)
- [ ] Cada fluxo passa em ambiente isolado
- [ ] `BASE_URL` configurável via variável de ambiente

### Histórias previstas

| ID | Título | Tipo | Fase |
|---|---|---|---|
| US-09.01 | Configurar Playwright com `APIRequestContext` e `playwright.config.ts` | `chore` | Fase Testes |
| US-09.02 | Criar branch `feat/playwright-broken` com testes escritos para 5 fluxos | `test` | Fase Testes |
| US-09.03 | Implementar Fluxo 1 — Registro e autenticação completa | `test` | Fase Testes |
| US-09.04 | Implementar Fluxo 2 — Criação e leitura com `readingTimeMinutes` | `test` | Fase Testes |
| US-09.05 | Implementar Fluxo 3 — Interação social completa | `test` | Fase Testes |
| US-09.06 | Implementar Fluxo 4 — Soft delete de artigo | `test` | **Fase 6** |
| US-09.07 | Implementar Fluxo 5 — Soft delete de comentário | `test` | **Fase 6** |
| US-09.08 | Criar branch `feat/playwright-working` com fluxos 1, 2, 3 passando | `test` | Fase Testes |
| US-09.09 | Integrar Playwright ao pipeline CI no branch `working` | `ci` | Fase Testes |

### Dependências

- EPIC-08 concluído (contratos documentados e validados)
- EPIC-12 concluído para fluxos 4 e 5 (Fase 6)

---

## EPIC-10 — Observabilidade com LGTM Stack {#epic-10}

> **Versão:** 2.0 · Gameday 1 e Gameday 2 nomeados explicitamente (GAP-G)  
> **ADR relacionado:** [ADR-003 — Métricas por endpoint via AOP](./06-architecture-decisions.md#adr-003)

### Contexto

Hoje, quando algo falha, o time não tem dados — tem suposições. A gestão requisitou três comportamentos específicos como obrigatórios: contador por endpoint, log de startup estruturado e log de shutdown estruturado.

Este épico entrega também o **Gameday 1** — exercício com o sistema baseline que valida que o time consegue localizar a causa de qualquer problema em ≤ 15 minutos.

> **Dois gamedays neste projeto:**
> - **Gameday 1 (este épico — Fase 4):** sistema sem as novas features de produto. Valida que a observabilidade funciona e que o time diagnostica incidentes no sistema atual em ≤ 15 minutos.
> - **Gameday 2 (Fase 6):** sistema completo com soft delete e tempo de leitura. Valida que as novas features são observáveis e diagnosticáveis na mesma régua. Não pertence a este épico.

### Objetivo

Quando este épico estiver concluído:
- 19/19 endpoints REST com contador Micrometer
- Logs estruturados de startup e shutdown com campos obrigatórios
- 100% das requisições com traces visíveis no Grafana/Tempo
- Dashboard Grafana com chamadas por endpoint, latência p95, taxa de erro
- **Gameday 1 realizado** — resultado ≤ 15 minutos documentado pelo PM

### Escopo

**Está incluído:**
- Contadores via AOP — `ApiMetricsAspect` único (ver ADR-003)
- Log de startup com campos: `event: "application_startup"`, `timestamp`, `version`, `environment`, `port`
- Log de shutdown com campos: `event: "application_shutdown"`, `timestamp`, `reason`
- OpenTelemetry + Tempo para traces
- Dashboard Grafana com ≥ 3 painéis
- **Gameday 1** com o sistema baseline

**Está fora do escopo:**
- Alertas automáticos (PagerDuty, OpsGenie)
- Métricas de banco de dados
- APM completo (Datadog, New Relic)
- **Gameday 2** — acontece na Fase 6, após EPIC-12 e EPIC-13 concluídos

### Estrutura dos logs obrigatórios

```json
// Startup
{
  "timestamp": "2026-06-09T10:00:00.000Z",
  "level": "INFO",
  "event": "application_startup",
  "application": "realworld-api",
  "version": "1.0.0",
  "environment": "dev",
  "port": 8080,
  "message": "Application started successfully"
}

// Shutdown
{
  "timestamp": "2026-06-09T10:05:00.000Z",
  "level": "INFO",
  "event": "application_shutdown",
  "application": "realworld-api",
  "reason": "JVM shutdown hook",
  "message": "Application shutdown complete"
}
```

### Implementação dos contadores — via AOP (ADR-003)

```java
@Aspect
@Component
public class ApiMetricsAspect {
    private final MeterRegistry meterRegistry;

    @Around("execution(* io.spring.api.*.*(..))")
    public Object trackApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String endpoint = resolveEndpointName(joinPoint);
        String method   = resolveHttpMethod(joinPoint);

        meterRegistry.counter("api.requests.total",
            "endpoint", endpoint,
            "method",   method
        ).increment();

        return joinPoint.proceed();
    }
}
```

### Regras de negócio deste épico

1. **100% dos endpoints com contador:** verificado comparando endpoints no Swagger com métricas no Prometheus.
2. **Logs de startup e shutdown são gates do CI:** script Python deve passar após adição dos campos estruturados.
3. **Traces habilitados por padrão:** sem requisição sem trace.
4. **Dashboard provisionado automaticamente:** não depende de configuração manual.
5. **Gameday 1 é entregável obrigatório:** resultado documentado e assinado pelo PM.
6. **Gameday 2 não pertence a este épico:** acontece na Fase 6.

### Critérios de aceitação do épico

- [ ] Chamada a qualquer endpoint → `api_requests_total{endpoint="..."}` incrementado no Prometheus
- [ ] 19/19 endpoints com contador verificado
- [ ] Script Python `validate_startup.py` passa com campos JSON obrigatórios de startup
- [ ] Script Python passa com campos JSON obrigatórios de shutdown
- [ ] Grafana em `http://localhost:3000` mostra dados reais após chamadas
- [ ] Dashboard com ≥ 3 painéis: chamadas por endpoint, latência p95, taxa de erro
- [ ] Traces visíveis no Grafana/Tempo
- [ ] **Gameday 1 realizado** — resultado ≤ 15 minutos documentado e assinado pelo PM

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-10.01 | Documentar ADR-003 sobre estratégia de implementação de contadores via AOP | `docs` |
| US-10.02 | Implementar `ApiMetricsAspect` com contador Micrometer para todos os 19 endpoints | `feat` |
| US-10.03 | Configurar Logback JSON com campos estruturados para todos os logs | `chore` |
| US-10.04 | Implementar log de startup estruturado com campos obrigatórios | `feat` |
| US-10.05 | Implementar log de shutdown estruturado com campos obrigatórios | `feat` |
| US-10.06 | Atualizar script Python para validar campos JSON dos logs | `chore` |
| US-10.07 | Configurar OpenTelemetry + Tempo para traces distribuídos | `feat` |
| US-10.08 | Criar dashboard Grafana com painéis de chamadas, latência e taxa de erro | `chore` |
| US-10.09 | Configurar provisioning automático dos dashboards Grafana | `chore` |
| US-10.10 | Realizar **Gameday 1** com sistema baseline e documentar resultado | `chore` |

### Dependências

- EPIC-03 concluído (LGTM Stack no Docker Compose)
- EPIC-04 concluído (Spring Boot 4 com OpenTelemetry starter nativo)

---

## EPIC-11 — Documentação de API: OpenAPI / Swagger {#epic-11}

> **Versão:** 2.0 · Separado em Fase 4 (setup) + Fase 6 (campos novos) — GAP-E corrigido

### Contexto

Os 19 endpoints REST existem, funcionam e estão mapeados no documento de API. Mas não há documentação formal acessível. O springdoc-openapi 2.x gera documentação OpenAPI automaticamente a partir das anotações do Spring MVC — nunca desatualizada.

> **Este épico tem dois momentos distintos:**
> - **Fase 4 — setup inicial:** documentar os 19 endpoints com campos e comportamentos existentes. Não depende das features de produto da Fase 5.
> - **Fase 6 — update de campos novos:** adicionar `readingTimeMinutes` e nota sobre soft delete após EPIC-12 e EPIC-13 estarem concluídos.

### Objetivo

**Ao final da Fase 4 (setup inicial):**
- 19/19 endpoints REST documentados no Swagger UI
- Schema OpenAPI válido em `/v3/api-docs`

**Ao final da Fase 6 (update completo):**
- Campo `readingTimeMinutes` documentado em todos os endpoints de artigo
- Comportamento de soft delete documentado em `DELETE /articles/:slug` e `DELETE /articles/:slug/comments/:id`
- Schema GraphQL atualizado com `readingTimeMinutes: Int`

### Escopo

**Está incluído — Fase 4:**
- `springdoc-openapi-starter-webmvc-ui` no `build.gradle`
- Anotações `@Operation`, `@ApiResponse`, `@Parameter`, `@RequestBody` nos controllers
- Swagger UI em `/swagger-ui.html`
- Schema OpenAPI JSON em `/v3/api-docs`
- 19 endpoints documentados com campos e comportamentos existentes
- Swagger UI desabilitado no perfil `prod`

**Está incluído — Fase 6:**
- Campo `readingTimeMinutes` nos schemas de artigo
- Nota de soft delete em `DELETE /articles/:slug` e `DELETE /articles/:slug/comments/:id`
- Schema `.graphqls` atualizado com `readingTimeMinutes: Int`

**Está fora do escopo:**
- Documentação GraphQL via ferramenta separada — `.graphqls` já é documentação formal
- Geração de SDK cliente
- Versionamento de API (/v1, /v2)
- Publicação em portal de API externo

### Regras de negócio deste épico

1. **100% dos 19 endpoints documentados na Fase 4:** critério do Marco M4.
2. **Schema OpenAPI válido:** verificado em `editor.swagger.io`.
3. **Swagger UI desabilitado em `prod`:** expor apenas em `dev` e `staging`.
4. **Update de campos novos obrigatoriamente na Fase 6:** `readingTimeMinutes` e soft delete documentados após EPIC-12 e EPIC-13 — nunca antes de existirem.
5. **Descrições em português:** campos `summary` e `description` em português.

### Critérios de aceitação

**Fase 4 (Marco M4):**
- [ ] `http://localhost:8080/swagger-ui.html` abre sem erro
- [ ] 19/19 endpoints visíveis com descrição, request e response
- [ ] `/v3/api-docs` retorna JSON OpenAPI válido verificado em `editor.swagger.io`
- [ ] Swagger UI não acessível com perfil `prod`

**Fase 6 (Marco M6):**
- [ ] Campo `readingTimeMinutes` visível no Swagger UI nos endpoints de artigo
- [ ] Nota de soft delete visível em `DELETE /articles/:slug` e `DELETE /articles/:slug/comments/:id`
- [ ] `readingTimeMinutes: Int` presente no arquivo `.graphqls`

### Histórias previstas

**Fase 4 — setup inicial:**

| ID | Título | Tipo |
|---|---|---|
| US-11.01 | Adicionar `springdoc-openapi-starter-webmvc-ui` e configurar Swagger UI | `chore` |
| US-11.02 | Documentar endpoints de autenticação e usuário com anotações OpenAPI | `docs` |
| US-11.03 | Documentar endpoints de perfil com anotações OpenAPI | `docs` |
| US-11.04 | Documentar endpoints de artigo com anotações OpenAPI | `docs` |
| US-11.05 | Documentar endpoints de favoritos, comentários e tags | `docs` |
| US-11.06 | Configurar Swagger UI para expor apenas em `dev` e `staging` | `chore` |
| US-11.07 | Validar schema OpenAPI gerado em `editor.swagger.io` | `chore` |

**Fase 6 — update de campos novos:**

| ID | Título | Tipo | Depende de |
|---|---|---|---|
| US-11.08 | Atualizar OpenAPI com `readingTimeMinutes` e nota de soft delete | `docs` | EPIC-12 + EPIC-13 concluídos |
| US-11.09 | Atualizar schema `.graphqls` com `readingTimeMinutes: Int` | `docs` | EPIC-13 concluído |

### Dependências

**Fase 4:** EPIC-04 concluído (Spring Boot 4 com springdoc 2.x compatível)  
**Fase 6 (US-11.08, US-11.09):** EPIC-12 e EPIC-13 concluídos

---

## EPIC-12 — Soft delete de artigos e comentários {#epic-12}

> **Versão:** 2.0 · HTTP `204 No Content` especificado explicitamente (GAP-NEW)

### Contexto

Hoje, quando um artigo ou comentário é deletado, o registro é removido permanentemente do banco. Isso impede auditoria, torna erros irreversíveis e é incompatível com requisitos futuros de retenção de dados (LGPD).

O soft delete resolve isso: o registro permanece no banco com `is_deleted = true`. Para o usuário, o comportamento é **idêntico ao atual** — o conteúdo desaparece. O contrato REST também permanece idêntico: `DELETE /articles/:slug` continua retornando `204 No Content`.

A implementação usa `@Where(clause = "is_deleted = false")` do Spring Data JPA — filtragem automática em todas as queries sem alterar nenhuma delas.

### Objetivo

Quando este épico estiver concluído:
- Nenhum artigo ou comentário é removido fisicamente por ação do usuário
- `DELETE /articles/:slug` e `DELETE /articles/:slug/comments/:id` marcam `is_deleted = true` e **continuam retornando `204 No Content`**
- Listagens filtram `is_deleted = true` automaticamente
- Comportamento visível ao usuário é idêntico ao atual
- Auditoria de exclusões disponível via query direta no banco

### Escopo

**Está incluído:**
- Migration Flyway: `is_deleted BOOLEAN NOT NULL DEFAULT FALSE` em `articles` e `comments`
- Índices: `idx_articles_is_deleted` e `idx_comments_is_deleted`
- `@Where(clause = "is_deleted = false")` nas entidades `Article` e `Comment`
- Substituição de `repository.delete()` por `entity.setDeleted(true); repository.save(entity)`
- Garantia explícita de que `DELETE` continua retornando `204 No Content`
- Testes de mutação ≥ 95% para código novo
- Testes de contrato existentes (EPIC-08) passando sem alteração

**Está fora do escopo:**
- Interface de administração para visualizar registros deletados
- Endpoint de recuperação de conteúdo deletado
- Soft delete em `User` — implicações LGPD exigem análise própria
- Soft delete em `Tag` — não deletadas por usuários
- Purge automático com TTL
- **Mudança no HTTP response code do DELETE** — permanece `204 No Content`

### Comportamento esperado em detalhe

| Ação | SQL antes | SQL depois | Response HTTP | Visível ao usuário |
|---|---|---|---|---|
| `DELETE /articles/:slug` | `DELETE FROM articles WHERE slug = ?` | `UPDATE articles SET is_deleted = true WHERE slug = ?` | **`204 No Content`** (inalterado) | Artigo some da plataforma |
| `DELETE /articles/:slug/comments/:id` | `DELETE FROM comments WHERE id = ?` | `UPDATE comments SET is_deleted = true WHERE id = ?` | **`204 No Content`** (inalterado) | Comentário some |
| `GET /articles` | `SELECT * FROM articles` | `SELECT * FROM articles WHERE is_deleted = false` | `200 OK` (inalterado) | Deletados não aparecem |
| `GET /articles/:slug` (deletado) | Retornaria dados | `WHERE is_deleted = false` → vazio → 404 | `404 Not Found` (inalterado) | Não encontrado |
| Query direta no banco | Registro inexistente | `SELECT * FROM articles WHERE is_deleted = true` | N/A | Apenas para operação/auditoria |

> **Por que `204 No Content` permanece inalterado?**
>
> A RealWorld spec define `DELETE` como `204 No Content`. O soft delete é uma mudança de implementação interna — do ponto de vista do consumidor da API, o artigo foi deletado. Mudar o status code quebraria todos os clientes que esperam `204`. A mudança é completamente transparente para a API pública.

### Regras de negócio deste épico

1. **Comportamento visível ao usuário é idêntico:** testes de contrato do EPIC-08 passam sem alteração de comportamento.
2. **HTTP `204 No Content` preservado:** o endpoint `DELETE` deve continuar retornando `204` — nunca `200` com body.
3. **`@Where` é a única forma de filtrar:** sem `WHERE is_deleted = false` escrito manualmente em nenhuma query.
4. **Nenhum `repository.delete()` para artigos e comentários:** verificar com grep após implementação.
5. **Índices obrigatórios:** a coluna `is_deleted` é usada em todas as queries de listagem.
6. **Migrations são irreversíveis:** `DEFAULT FALSE` garante que registros existentes não são afetados.

### Critérios de aceitação do épico

- [ ] `\d articles` e `\d comments` no psql → mostram coluna `is_deleted`
- [ ] `DELETE /articles/:slug` → **retorna `204 No Content`** (comportamento HTTP inalterado)
- [ ] `DELETE /articles/:slug` → `SELECT is_deleted FROM articles WHERE slug=?` retorna `true`
- [ ] `DELETE /articles/:slug/comments/:id` → **retorna `204 No Content`** (inalterado)
- [ ] `GET /articles` → artigo com `is_deleted = true` ausente da listagem
- [ ] `GET /articles/:slug` para artigo deletado → `404 Not Found` (inalterado)
- [ ] `SELECT * FROM articles WHERE is_deleted = true` → retorna histórico de exclusões
- [ ] Todos os testes de contrato do EPIC-08 passam sem alteração
- [ ] `grep "repository.delete()" src/` em classes de artigo e comentário → 0 resultados
- [ ] `./gradlew pitest` → ≥ 95% incluindo código novo

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-12.01 | Criar migration Flyway adicionando `is_deleted` em `articles` e `comments` com índices | `feat` |
| US-12.02 | Adicionar `@Where(clause = "is_deleted = false")` na entidade `Article` | `feat` |
| US-12.03 | Adicionar `@Where(clause = "is_deleted = false")` na entidade `Comment` | `feat` |
| US-12.04 | Substituir hard delete de artigo por soft delete no `ArticleService` | `feat` |
| US-12.05 | Substituir hard delete de comentário por soft delete no `CommentService` | `feat` |
| US-12.06 | Garantir que `DELETE /articles/:slug` continua retornando `204 No Content` | `test` |
| US-12.07 | Garantir que `DELETE /articles/:slug/comments/:id` continua retornando `204 No Content` | `test` |
| US-12.08 | Escrever testes de unidade para a lógica de soft delete | `test` |
| US-12.09 | Verificar que todos os testes de contrato existentes passam sem alteração | `test` |
| US-12.10 | Atingir mutation score ≥ 95% para o código novo de soft delete | `test` |
| US-12.11 | Atualizar documentação OpenAPI com nota sobre comportamento de exclusão (via US-11.08) | `docs` |

### Dependências

- EPIC-05 concluído (Spring Data JPA — `@Where` é anotação JPA)
- EPIC-07 em andamento (Pitest configurado)
- EPIC-08 concluído (testes de contrato como rede de segurança)

---

## EPIC-13 — Tempo de leitura com cache lazy {#epic-13}

### Contexto

Plataformas de conteúdo modernas exibem o tempo estimado de leitura de cada artigo. Saber que um artigo leva 2 ou 18 minutos influencia diretamente a decisão do usuário. A gestão requisitou três comportamentos: estimativa por contagem de palavras (200 wpm), cache do resultado e lazy update para artigos existentes sem o campo.

### Objetivo

Quando este épico estiver concluído:
- Todo artigo criado tem `reading_time_minutes` calculado e armazenado
- Artigos existentes recebem o valor na primeira leitura via `GET /articles/:slug`
- `readingTimeMinutes` retornado em todos os responses de artigo — REST e GraphQL
- Testes cobrem todos os cenários de cálculo, criação, edição e lazy update

### Escopo

**Está incluído:**
- Migration Flyway: `reading_time_minutes INTEGER` em `articles` (nullable)
- Método de cálculo: `MAX(1, CEIL(wordCount / 200))`
- Cálculo automático em `POST /articles` e `PUT /articles/:slug` (quando `body` muda)
- Lazy update em `GET /articles/:slug` para artigos com campo `null`
- Campo `readingTimeMinutes` em todos os responses REST de artigo
- Campo `readingTimeMinutes: Int` no schema GraphQL
- Testes de mutação ≥ 95% para código novo

**Está fora do escopo:**
- Lazy update em `GET /articles` (listagem) — bulk lazy update causaria latência
- Velocidade de leitura configurável por usuário — v1 usa 200 wpm para todos
- Algoritmo diferenciado para código vs prosa
- Cache distribuído (Redis) — PostgreSQL + lazy update é suficiente para o volume atual

### Lógica de cálculo

```
reading_time_minutes = MAX(1, CEIL(wordCount / 200))

Exemplos:
  50 palavras   → MAX(1, CEIL(0.25))  = 1  → 1 minuto (mínimo)
  200 palavras  → MAX(1, CEIL(1.0))   = 1  → 1 minuto
  201 palavras  → MAX(1, CEIL(1.005)) = 2  → 2 minutos
  2500 palavras → MAX(1, CEIL(12.5))  = 13 → 13 minutos

Contagem: body.trim().split("\\s+").length
```

### Comportamento esperado

| Cenário | O que acontece | Resultado |
|---|---|---|
| `POST /articles` com 400 palavras | Calcula 2 min, persiste, inclui no response | `readingTimeMinutes: 2` |
| `PUT /articles/:slug` alterando body para 600 palavras | Recalcula 3 min, atualiza no banco | `readingTimeMinutes: 3` |
| `PUT /articles/:slug` alterando apenas `title` | Não recalcula — body não mudou | `readingTimeMinutes` inalterado |
| `GET /articles/:slug` — artigo com campo preenchido | Retorna valor armazenado sem recalcular | `readingTimeMinutes: N` |
| `GET /articles/:slug` — artigo com campo `null` | Calcula, persiste, retorna | `readingTimeMinutes: N` (calculado) |
| `GET /articles/:slug` — segunda leitura do mesmo | Campo já no banco — retorna diretamente | `readingTimeMinutes: N` (do banco) |
| `GET /articles` (listagem) | Retorna campo se preenchido; `null` se não | Sem lazy update na listagem |

### Regras de negócio deste épico

1. **Mínimo de 1 minuto:** nenhum artigo pode ter `readingTimeMinutes` < 1.
2. **Arredondamento para cima sempre:** `CEIL` — melhor superestimar.
3. **Lazy update apenas em `GET /articles/:slug`:** não em listagens, não em background.
4. **Recálculo obrigatório em edição de body:** se `body` muda no `PUT`, recalcula. Se só `title` ou `description` mudam, não recalcula.
5. **Campo nullable na migration:** artigos existentes têm `null` até a primeira leitura.
6. **Consistência REST e GraphQL:** mesmo nome `readingTimeMinutes` nas duas APIs.

### Critérios de aceitação do épico

- [ ] `\d articles` → mostra coluna `reading_time_minutes INTEGER`
- [ ] `POST /articles` com 400 palavras → `"readingTimeMinutes": 2`
- [ ] `POST /articles` com 50 palavras → `"readingTimeMinutes": 1`
- [ ] `PUT /articles/:slug` alterando body para 600 palavras → `readingTimeMinutes: 3`
- [ ] `PUT /articles/:slug` alterando apenas `title` → `readingTimeMinutes` inalterado
- [ ] `GET /articles/:slug` para artigo com campo `null` → campo calculado e persistido
- [ ] Segunda leitura do mesmo artigo → sem UPDATE no banco (campo já preenchido)
- [ ] `query { article(slug:"...") { readingTimeMinutes } }` → valor correto via GraphQL
- [ ] `./gradlew pitest` → ≥ 95% incluindo código novo

### Histórias previstas

| ID | Título | Tipo |
|---|---|---|
| US-13.01 | Criar migration Flyway adicionando `reading_time_minutes` em `articles` | `feat` |
| US-13.02 | Implementar método `calculateReadingTime(String body)` com testes unitários | `feat` |
| US-13.03 | Calcular e persistir `reading_time_minutes` ao criar artigo | `feat` |
| US-13.04 | Recalcular `reading_time_minutes` ao editar artigo com novo body | `feat` |
| US-13.05 | Implementar lazy update em `GET /articles/:slug` para artigos com campo `null` | `feat` |
| US-13.06 | Adicionar `readingTimeMinutes` ao response REST de todos os endpoints de artigo | `feat` |
| US-13.07 | Adicionar `readingTimeMinutes: Int` ao schema GraphQL e implementar resolver | `feat` |
| US-13.08 | Escrever testes de integração para todos os cenários de cálculo e lazy update | `test` |
| US-13.09 | Atingir mutation score ≥ 95% para o código novo | `test` |
| US-13.10 | Atualizar documentação OpenAPI e schema GraphQL com o campo | `docs` |

### Dependências

- EPIC-05 concluído (Spring Data JPA — repositórios para persistir o valor)
- EPIC-07 em andamento (Pitest configurado)
- EPIC-08 concluído (testes de contrato como rede de segurança)

---

## Resumo executivo do backlog

| ID | Épico | Fase | Histórias | Principais correções v2.0 |
|---|---|---|---|---|
| EPIC-01 | Fundação do processo | Fase 0 | 8 | — |
| EPIC-02 | Segurança | Fase 1 | 6 | ✏️ `JWT_SESSION_TIME` adicionado em todo o épico; ADR-006 referenciado |
| EPIC-03 | Containerização | Fase 1 | 9 | Docker Compose injeta ambas as variáveis JWT |
| EPIC-04 | Modernização do runtime | Fase 3 | 9 | `DefaultJwtService` javax.crypto mencionado; ADR-001, ADR-004, ADR-005 |
| EPIC-05 | Migração JPA | Fase 3 | 9 | ADR-002 referenciado |
| EPIC-06 | Record types | Fase 3 | 7 | `getCursor()` explícito em `ArticleData`; pré-req EPIC-04 Joda-Time |
| EPIC-07 | Pitest 95% | Fase Testes | 7 | `io.spring.graphql.*` exclusão com referência a ADR-004 |
| EPIC-08 | Testes de contrato | Fase Testes | 9 | — |
| EPIC-09 | Playwright E2E | Fase Testes + Fase 6 | 9 | Fluxos 4 e 5 explicitamente na Fase 6 |
| EPIC-10 | Observabilidade LGTM | Fase 4 | 10 | ✏️ **Gameday 1** explícito; **Gameday 2** na Fase 6; ADR-003 |
| EPIC-11 | OpenAPI / Swagger | Fase 4 + Fase 6 | 9 | ✏️ Setup Fase 4 + update campos Fase 6 separados |
| EPIC-12 | Soft delete | Fase 5 | 11 | ✏️ **HTTP `204 No Content`** especificado; tabela comportamento com coluna Response HTTP |
| EPIC-13 | Tempo de leitura | Fase 5 | 10 | — |
| **Total** | | | **113 histórias** | **4 épicos corrigidos** |

**Épicos com entregas em duas fases:**
- EPIC-11: US-11.01–US-11.07 na Fase 4; US-11.08–US-11.09 na Fase 6
- EPIC-09: US-09.01–US-09.05, US-09.08–US-09.09 na Fase Testes; US-09.06–US-09.07 na Fase 6

**Gamedays:**
- **Gameday 1** — EPIC-10, US-10.10, Fase 4: sistema baseline, valida observabilidade
- **Gameday 2** — Fase 6: sistema completo com novas features, valida diagnóstico end-to-end

**ADRs referenciados neste backlog:**
- ADR-001 → EPIC-04 (DGS Framework vs Spring for GraphQL)
- ADR-002 → EPIC-05 (estratégia de queries JPA)
- ADR-003 → EPIC-10 (métricas via AOP)
- ADR-004 → EPIC-07 (io.spring.graphql excluído do Pitest)
- ADR-005 → EPIC-06 (Node interface + records)
- ADR-006 → EPIC-02 (JWT_SECRET + JWT_SESSION_TIME)

---

*Documento vivo — atualizar status de cada épico conforme progresso*  
*Toda mudança de escopo refletida aqui antes de ser implementada*  
*13 épicos · 113 histórias previstas · 4 épicos corrigidos na v2.0*  
*Rastreado em: `03-initiatives.md` · `04-roadmap.md` · `06-architecture-decisions.md` · `07-metrics.md` · `08-risks.md`*
