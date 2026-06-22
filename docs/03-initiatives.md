# 03 — Initiatives
> Versão 2.0 · Junho 2026 — Revisão com validação de stack  
> Projeto: RealWorld Platform Modernization  
> Responsável: Product Management

---

## Nota de revisão — O que mudou nesta versão

Esta versão corrige imprecisões técnicas da v1.0 com base em pesquisa de versões atuais (junho 2026):

| Ponto revisado | v1.0 (incorreto) | v2.0 (correto) | Fonte |
|---|---|---|---|
| Versão alvo Spring Boot | 2.6.3 | **4.0.3** (literal do mandato — item J3) | `docs/00-original-mandate.md` |
| Java mínimo para Spring Boot 4 | "Java 21" | **Java 17 mínimo**, recomendado Java 25 | docs.spring.io/system-requirements |
| Java 25 status | "pode não ser LTS" | **Java 25 é LTS** (GA set/2025, suporte até 2030) | oracle.com |
| DGS Framework compatibilidade | "risco alto, pode ser incompatível" | **DGS 10.x integra nativamente com Spring for GraphQL** — sem breaking change de código | netflix.github.io/dgs |
| Gradle compatibilidade Spring Boot 4 | "requer Gradle 9.3.1" | Spring Boot 4 suporta **Gradle 8.14+ ou 9.x** — Gradle 9.3.1 é válido | docs.spring.io/gradle-plugin |

---

## O que é este documento

Este documento descreve cada **iniciativa** do projeto de modernização da plataforma RealWorld. Uma iniciativa é um conjunto coeso de trabalho com objetivo claro, escopo definido, justificativa explícita de por que está incluído e por que determinados escopos estão fora, além de critérios verificáveis de conclusão.

> **Princípio central para vibe coding:** toda iniciativa aqui é a fonte da verdade para geração de código assistido por IA. Antes de gerar qualquer código, o desenvolvedor lê a iniciativa completa e documenta os prompts no Coda. Mudanças de escopo são refletidas aqui primeiro — nunca diretamente no código.

---

## Por que cada escolha tecnológica foi feita

Antes das iniciativas, é necessário entender as decisões que moldam o escopo de todo o projeto.

### Por que Java 25 e não Java 21?

Java 21 é LTS e amplamente adotado. Java 25 também é LTS (GA setembro 2025, suporte Oracle até setembro 2030) e é o **LTS mais recente disponível**. A gestão explicitamente solicitou Java 25.

Do ponto de vista de custo-benefício para escalabilidade:

| Recurso | Java 21 | Java 25 | Impacto em escala |
|---|---|---|---|
| Virtual threads (Project Loom) | ✅ Estável | ✅ Estável + melhorado | Concorrência massiva sem overhead de threads OS |
| Structured Concurrency | Preview | ✅ **Estável** | Código assíncrono mais seguro e legível |
| Record patterns | ✅ | ✅ + mais expressivo | Redução de boilerplate nos DTOs |
| String Templates | Não tem | ✅ **Estável** | Queries e mensagens mais limpas |
| Compact source files | Não tem | ✅ | Scripts e utilitários mais simples |
| Suporte Oracle gratuito | Até set/2026 | Até set/2030 | **4 anos a mais de suporte gratuito** |

**Decisão:** Java 25. O custo de migração de Java 21 para Java 25 é mínimo — Spring Boot 4 suporta ambos. O ganho em suporte de longo prazo e recursos de linguagem justifica a escolha.

---

### Por que Spring Boot 4.0.3 e não permanecer no 2.x ou usar o 3.x?

Spring Boot 2.6.3 está em EOL desde novembro de 2023 — sem patches de segurança. Spring Boot 3.x está com a última versão 3.5 encerrando suporte em junho de 2026. Spring Boot 4.0.3 é a versão com suporte ativo até dezembro de 2026, com trilha clara para 4.1.x em 2026.

Spring Boot 4 traz, especificamente para este projeto:
- **Jakarta EE 11** (substitui `javax.*` → `jakarta.*` de forma definitiva)
- **Spring Framework 7** com null-safety via JSpecify — menos NullPointerExceptions em produção
- **OpenTelemetry starter nativo** — observabilidade sem configuração extra (relevante para INI-10)
- **API Versioning** nativo — relevante para evolução futura do contrato da API
- **Gradle 9 suportado** — alinhado com a demanda da gestão

**Por que `4.0.3`?** A versão `4.0.3` é a especificada literalmente pela gestão (item J3 do mandato). Em 2026-06-22 a PM Franciele decidiu mantê-la (GAP-D). Caso, na implementação, surja a necessidade de um patch posterior da mesma linha (`4.0.x`) por correção de segurança, a mudança deve ser proposta como adendo e aprovada pela PM, atualizando `docs/00-original-mandate.md`.

---

### Por que Spring Data JPA + Hibernate e não permanecer com MyBatis?

MyBatis oferece controle total sobre SQL — o que é valioso em sistemas com queries extremamente complexas ou otimizações de banco específicas. Para este projeto, o custo-benefício favorece Spring Data JPA:

| Critério | MyBatis (atual) | Spring Data JPA + Hibernate |
|---|---|---|
| Quantidade de código para CRUD simples | Alto (mapper interface + XML) | Mínimo (interface `JpaRepository`) |
| Integração com Spring Boot 4 | Requer configuração manual | **Autoconfigure nativo** |
| Suporte a `@Where` para soft delete | Manual | **Nativo com `@Where(clause="is_deleted=false")`** |
| Suporte a cache `@Cacheable` | Manual | **Nativo via Spring Cache** |
| Queries complexas | ✅ Excelente | ✅ `@Query` JPQL + Specifications |
| Virtual threads (Loom) | Requer tuning | **Integração otimizada no Spring Boot 4** |
| Testcontainers + DataJpaTest | Funcional | **Suporte de primeira classe** |

**O que fica fora do escopo:** não migraremos queries SQL genuinamente complexas para JPQL se a legibilidade for prejudicada. Nesses casos, `@Query` com SQL nativo (`nativeQuery = true`) é a alternativa — mantém Spring Data JPA como framework principal sem sacrificar controle.

---

### Por que DGS Framework e não migrar para Spring for GraphQL puro?

O DGS 10.x — lançado em 2024 — **integra nativamente com Spring for GraphQL internamente**. A Netflix migrou todos os seus serviços para esta integração. Do ponto de vista do código da aplicação, o DGS continua funcionando com as mesmas anotações (`@DgsComponent`, `@DgsQuery`, `@DgsMutation`). A plumbing interna usa Spring for GraphQL.

Isso significa: **não há decisão a tomar.** Atualizar o DGS para a versão 10.x compatível com Spring Boot 4 é suficiente. Não é necessário reescrever resolvers GraphQL.

**O que fica fora do escopo:** migração para Spring for GraphQL puro (sem DGS) está fora do escopo desta modernização. O custo de reescrever todos os resolvers não traz benefício funcional — o DGS 10.x já usa Spring for GraphQL internamente.

---

### Por que Gradle 9.3.1?

A gestão especificou explicitamente Gradle 9.3.1 ou superior sem deprecation warnings. Spring Boot 4 suporta Gradle 8.14+ e Gradle 9.x. Gradle 9 traz:
- Build cache mais eficiente (builds repetidos até 90% mais rápidos)
- Configuration cache estável (paralelismo de configuração)
- Melhor isolamento de projetos
- Compatibilidade nativa com Spring Boot 4 sem workarounds

---

## Índice de iniciativas

| ID | Nome | Fase | OKRs | Status |
|---|---|---|---|---|
| [INI-01](#ini-01) | Fundação do processo de desenvolvimento | Fase 0 | OKR 6 | 🔵 Planejado |
| [INI-02](#ini-02) | Segurança — JWT secret e perfis de ambiente | Fase 1 | OKR 3, OKR 6 | 🔵 Planejado |
| [INI-03](#ini-03) | Containerização e ambiente local reproduzível | Fase 1 | OKR 3, OKR 4 | 🔵 Planejado |
| [INI-04](#ini-04) | Modernização do stack — Java 25 + Spring Boot 4.0.3 + Gradle 9.3.1 | Fase 2 | OKR 1 | 🔵 Planejado |
| [INI-05](#ini-05) | Migração de MyBatis para Spring Data JPA + Hibernate | Fase 2 | OKR 1 | 🔵 Planejado |
| [INI-06](#ini-06) | Introdução de record types Java 25 | Fase 2 | OKR 1 | 🔵 Planejado |
| [INI-07](#ini-07) | Cobertura de testes por mutação — Pitest 95% | Fase 3 | OKR 2 | 🔵 Planejado |
| [INI-08](#ini-08) | Testes de integração — contratos REST e GraphQL | Fase 3 | OKR 2 | 🔵 Planejado |
| [INI-09](#ini-09) | Testes end-to-end com Playwright | Fase 3 | OKR 2 | 🔵 Planejado |
| [INI-10](#ini-10) | Observabilidade com LGTM Stack | Fase 4 | OKR 5 | 🔵 Planejado |
| [INI-11](#ini-11) | Documentação de API com OpenAPI / Swagger | Fase 4 | OKR 6 | 🔵 Planejado |
| [INI-12](#ini-12) | Soft delete de artigos e comentários | Fase 5 | OKR 4, OKR 7 | 🔵 Planejado |
| [INI-13](#ini-13) | Tempo estimado de leitura com cache lazy | Fase 5 | OKR 4, OKR 7 | 🔵 Planejado |

**Legenda:** 🔵 Planejado · 🟡 Em andamento · 🟢 Concluído · 🔴 Bloqueado · ⚫ Cancelado

---

## INI-01 — Fundação do processo de desenvolvimento {#ini-01}

### Por que esta iniciativa existe

Em vibe coding, código é gerado rapidamente com assistência de IA. Sem processo, a velocidade de geração se torna um risco: é possível acumular código não rastreável, sem contexto de por que foi criado, sem critério de quando está "pronto". Esta iniciativa não entrega código de produto — ela entrega a infraestrutura de processo que torna todas as outras iniciativas auditáveis.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Templates de GitHub Issues (bug, feature, chore, spike) | Padroniza como o trabalho é descrito antes de começar |
| Definition of Ready (DoR) por repositório | Define o que precisa estar claro antes de codar — reduz retrabalho |
| Definition of Done (DoD) por repositório | Define o que significa "concluído" — evita entregas parciais |
| commitlint no CI para Conventional Commits | Garante rastreabilidade automática; sem isso a convenção é sugestão |
| Branch `bleeding` para harness development | Permite commits automáticos e validação contínua do processo |
| Guia GitAhead | Ferramenta de visualização de histórico Git — essencial para duplas validarem progresso |
| Setup do Coda | Documentação de prompts e skills por etapa — rastreabilidade do processo de vibe coding |
| Template de Pull Request com checklist | PR sem issue vinculada e sem checklist não é aprovada |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| CI/CD completo além do commitlint | Escopo de INI-03 e fases posteriores — misturar aumenta o risco desta fase |
| Qualquer funcionalidade de produto | Esta fase existe para habilitar as demais com segurança |
| Integração com ferramentas de projeto externas (Jira, Linear) | GitHub Issues é suficiente e está alinhado com a demanda da gestão |

### Entregáveis verificáveis

| Entregável | Localização | Como verificar |
|---|---|---|
| Templates de issue | `.github/ISSUE_TEMPLATE/*.yml` | Criar nova issue no GitHub — templates aparecem |
| Template de PR | `.github/PULL_REQUEST_TEMPLATE.md` | Abrir PR — checklist aparece automaticamente |
| DoR | `docs/process/definition-of-ready.md` | Arquivo acessível no repositório |
| DoD | `docs/process/definition-of-done.md` | Arquivo acessível no repositório |
| commitlint | `.commitlintrc.yml` + workflow CI | Push de commit `fix bad format` → CI falha |
| Branch `bleeding` | GitHub branches | Branch existe |
| Coda workspace | Link no README.md | URL funciona, workspace acessível ao time |

### Definition of Ready
- [ ] Time completo tem acesso ao GitHub e ao Coda
- [ ] GitAhead instalado em pelo menos um ambiente
- [ ] Duplas de desenvolvimento definidas

### Definition of Done
- [ ] CI rejeita commit fora do padrão Conventional Commits (testado manualmente)
- [ ] Pelo menos uma issue criada usando cada template
- [ ] DoR e DoD revisadas e aceitas pelo time
- [ ] Prompts e skills de cada passo documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 6 | KR6.1 | 100% commits no formato Conventional |
| OKR 6 | KR6.2 | 100% PRs com issue vinculada |
| OKR 6 | KR6.3 | DoR e DoD documentadas no repositório |
| OKR 6 | KR6.7 | 100% das etapas documentadas no Coda |

---

## INI-02 — Segurança: JWT secret e perfis de ambiente {#ini-02}

### Por que esta iniciativa existe

Duas vulnerabilidades precisam ser eliminadas antes de qualquer outro trabalho:

**Vulnerabilidade 1 — JWT secret exposto:** a chave secreta de autenticação está hardcoded no `application.properties` público. Qualquer pessoa com acesso ao repositório pode gerar tokens JWT válidos e se passar por qualquer usuário. Custo de correção: horas. Custo de não corrigir: sistema de autenticação completamente comprometido.

**Vulnerabilidade 2 — Stack sem suporte:** Spring Boot 2.6.3 está em EOL. Será resolvida na INI-04, mas os perfis de ambiente configurados aqui são pré-requisito para o upgrade seguro.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| JWT secret via variável de ambiente `JWT_SECRET` | Elimina vulnerabilidade crítica de autenticação |
| Perfis Spring: `dev`, `staging`, `prod` | Pré-requisito para INI-03 (Docker) e INI-04 (upgrade de stack) |
| `.env.example` com variáveis sem valores reais | Documenta quais variáveis são necessárias sem expor valores |
| Scan de segredos no CI (truffleHog ou git-secrets) | Garante que nenhum segredo futuro entre no repositório por acidente |
| Mensagem de erro clara ao iniciar sem `JWT_SECRET` | Evita erros silenciosos no ambiente do dev |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| HashiCorp Vault ou AWS Secrets Manager | Infraestrutura de produção — custo desproporcional para o momento atual do projeto |
| Rotação automática de tokens JWT | Funcionalidade avançada — fora do escopo desta modernização |
| Mudanças na lógica de autenticação | Esta iniciativa é cirúrgica: mover o secret, não redesenhar auth |
| HTTPS / TLS | Responsabilidade da camada de infraestrutura (proxy/load balancer) — não da aplicação |

### Comportamento antes e depois

```
ANTES
application.properties: jwt.secret=mySecretKey  ← exposto no repositório público

DEPOIS
application.properties: jwt.secret=${JWT_SECRET}  ← sem valor, lê do ambiente
.env.example:           JWT_SECRET=your-secret-here  ← template commitado, sem valor real
docker-compose.yml:     JWT_SECRET: ${JWT_SECRET}  ← injetado no container
CI/GitHub Actions:      JWT_SECRET configurado como secret no repositório
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Nenhum secret no código | `truffleHog scan .` retorna 0 findings |
| App inicia com variável definida | `JWT_SECRET=test ./gradlew bootRun` — inicia |
| App falha sem variável | `./gradlew bootRun` sem `JWT_SECRET` — erro claro na inicialização |
| 3 perfis de ambiente | Arquivos `application-dev.yml`, `-staging.yml`, `-prod.yml` existem |
| Scan no CI | Push com secret simulado → CI falha com mensagem clara |

### Definition of Ready
- [ ] INI-01 concluída
- [ ] Escolha feita entre truffleHog e git-secrets

### Definition of Done
- [ ] Scan de CI passa após remoção do secret
- [ ] App inicia corretamente com `JWT_SECRET` via ambiente
- [ ] App falha com mensagem clara sem `JWT_SECRET`
- [ ] `.env.example` commitado no repositório
- [ ] Todos os testes existentes passando
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 3 | KR3.5 | 3 perfis de ambiente configurados |
| OKR 6 | KR6.5 | 0 segredos detectados pelo scan |

---

## INI-03 — Containerização e ambiente local reproduzível {#ini-03}

### Por que esta iniciativa existe

Hoje cada desenvolvedor monta o ambiente de forma diferente. O banco varia, a versão do Java varia, a observabilidade não existe localmente. O objetivo é um único comando — `docker compose up` — que sobe tudo: aplicação, PostgreSQL, e a stack de observabilidade (LGTM) necessária para as iniciativas seguintes.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| `Dockerfile` multi-stage para Spring Boot | Build leve e reproduzível — imagem de produção separada do build |
| `docker-compose.yml`: app + PostgreSQL 16 | Elimina SQLite; PostgreSQL é o banco de produção alvo |
| `docker-compose.yml`: Prometheus + Loki + Tempo + Grafana | LGTM Stack necessário para INI-10; melhor configurar agora do que depois |
| Script Python `scripts/validate_startup.py` | Validação automatizada de log de startup/shutdown — requisito explícito da gestão |
| `CONTRIBUTING.md` com guia de setup | Onboarding em < 15 minutos — KR3.1 mensurável |
| Health check Spring Actuator (`/actuator/health`) | Necessário para Docker Compose saber quando app está pronta; necessário para INI-10 |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Ambiente de staging ou produção | CD pipeline é fase posterior — misturar aumenta complexidade desnecessariamente |
| Dashboards Grafana customizados | Escopo de INI-10 — aqui apenas o stack sobe, métricas de negócio vêm depois |
| Kubernetes / Helm charts | Escopo de infraestrutura de produção — fora do projeto atual |
| Docker Swarm ou multi-node | Desnecessário para desenvolvimento local |
| Banco diferente de PostgreSQL 16 | Decisão tomada: PostgreSQL 16 Alpine é o padrão |

### Arquitetura do Docker Compose

```
┌─────────────────────────────────────────────────────────────┐
│                    docker-compose.yml                        │
│                                                              │
│  ┌─────────────┐     ┌──────────────┐                       │
│  │  app:8080   │────▶│ postgres:5432│                       │
│  │ Spring Boot │     │  PostgreSQL  │                       │
│  │    4.0.3    │     │   16-alpine  │                       │
│  └──────┬──────┘     └──────────────┘                       │
│         │ métricas                                           │
│         ▼                                                    │
│  ┌─────────────┐     ┌──────────────┐  ┌────────────────┐  │
│  │prometheus   │────▶│   grafana    │◀─│     loki       │  │
│  │   :9090     │     │    :3000     │  │    :3100       │  │
│  └─────────────┘     └──────┬───────┘  └────────────────┘  │
│                             │◀────────── tempo:3200          │
└─────────────────────────────────────────────────────────────┘
```

### Script de validação Python — comportamento esperado

```
scripts/validate_startup.py
  1. Executa: docker compose up -d
  2. Aguarda até 60s pelo log: "Started Application in X seconds"
  3. Valida: log contém campos esperados (timestamp, event:startup, version)
  4. Executa: docker compose stop
  5. Aguarda até 30s pelo log: "JVM shutdown hook" ou similar
  6. Valida: log contém campos de shutdown (timestamp, event:shutdown)
  7. Exit 0 (sucesso) ou Exit 1 (falha com mensagem descritiva)
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| `docker compose up` funcional | Todos os 6 serviços em estado `healthy` após o comando |
| App respondendo | `curl http://localhost:8080/tags` retorna `{"tags":[...]}` |
| PostgreSQL acessível | `psql -h localhost -U postgres -c '\l'` lista bancos |
| Grafana acessível | `http://localhost:3000` (admin/admin) abre sem erro |
| Script Python | `python scripts/validate_startup.py` → exit code 0 |
| Onboarding cronometrado | Dev novo sobe ambiente em ≤ 15 min seguindo `CONTRIBUTING.md` |

### Definition of Ready
- [ ] INI-02 concluída (JWT e perfis de ambiente)
- [ ] Versão PostgreSQL decidida (recomendado: 16-alpine)
- [ ] Python 3.x disponível nos ambientes do time

### Definition of Done
- [ ] `docker compose up` sobe todos os serviços sem erro
- [ ] `curl http://localhost:8080/tags` retorna JSON válido
- [ ] Script Python passa no CI (exit code 0)
- [ ] Onboarding cronometrado com dev externo — resultado ≤ 15 min documentado
- [ ] KR3.1 medido e registrado
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 3 | KR3.1 | ≤ 15 min do clone ao sistema rodando |
| OKR 3 | KR3.2 | `docker compose up` sobe tudo |
| OKR 3 | KR3.3 | 100% dos passos executáveis por dev novo |
| OKR 3 | KR3.4 | Script Python passa 100% das vezes no CI |
| OKR 4 | KR4.1 | PostgreSQL em uso (SQLite eliminado) |

---

## INI-04 — Modernização do stack: Java 25 + Spring Boot 4.0.3 + Gradle 9.3.1 {#ini-04}

### Por que esta iniciativa existe

Spring Boot 2.6.3 está em EOL. Java 11 perde suporte gratuito da Oracle em setembro de 2026 (sobreposição com Java 25 LTS). Permanecer nessa stack não é uma opção segura para um sistema em evolução. Esta é a iniciativa de maior risco técnico do projeto porque afeta todo o codebase. Por isso depende de INI-07 e INI-08 como rede de segurança.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Java 11 → Java 25 LTS | LTS mais recente, suporte até 2030, virtual threads estáveis, structured concurrency estável |
| Spring Boot 2.6.3 → 4.0.3 | Versão com suporte ativo; Jakarta EE 11; OpenTelemetry nativo; Gradle 9 |
| Gradle → 9.3.1 sem deprecation warnings | Requisito explícito da gestão; performance de build; configuration cache estável |
| `javax.*` → `jakarta.*` | Breaking change obrigatório no Spring Boot 3+/4.x — sem isso o build não compila |
| Reconfiguração Spring Security 6.x | `WebSecurityConfigurerAdapter` foi removido — código atual não compila sem ajuste |
| DGS Framework → versão 10.x compatível com Spring Boot 4 | DGS 10.x integra com Spring for GraphQL internamente — atualização de versão, não reescrita |
| Remoção de Joda-Time → `java.time` | Joda-Time é legado desde Java 8; `java.time` é a API padrão da plataforma |
| Atualização de dependências transitivas | Dependências antigas podem ter incompatibilidades ou CVEs com o novo stack |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Migração de MyBatis para JPA | INI-05 — separado intencionalmente para controle de risco: misturar ORM com upgrade de framework é receita para bugs difíceis de rastrear |
| Introdução de record types | INI-06 — separado; pode ser feito em paralelo com INI-05 sem interferência |
| Reescrita dos resolvers GraphQL | DGS 10.x mantém as mesmas anotações — reescrita não é necessária |
| Migração de Spring Security para OAuth2/OIDC | Fora do escopo desta modernização — auth permanece JWT |
| GraalVM Native Image | Compilação nativa aumenta complexidade sem benefício direto para este projeto |
| Spring WebFlux / reativo | A aplicação usa modelo imperativo com virtual threads — mais simples e igualmente performático |

### Por que virtual threads (Loom) e não WebFlux?

Spring Boot 4 com Java 25 virtual threads oferece o mesmo benefício de escala de I/O do WebFlux com código imperativo — sem a complexidade do modelo reativo. Para uma API REST/GraphQL como esta, virtual threads são a escolha de melhor custo-benefício:

```
WebFlux:         Requer reescrita completa, curva de aprendizado alta, debugging complexo
Virtual Threads: Habilitar uma propriedade, código existente funciona, debugging simples
```

```properties
# application.properties — habilita virtual threads no Spring Boot 4
spring.threads.virtual.enabled=true
```

### Mudanças técnicas obrigatórias

**`javax.*` → `jakarta.*` — escala do impacto:**
```java
// Todas as ocorrências de:
import javax.persistence.*;      → import jakarta.persistence.*;
import javax.validation.*;       → import jakarta.validation.*;
import javax.transaction.*;      → import jakarta.transaction.*;
import javax.servlet.*;          → import jakarta.servlet.*;
```
Ferramentas como IntelliJ IDEA e o plugin de migração do Spring fazem essa substituição automaticamente.

**Spring Security 6.x — reconfiguração obrigatória:**
```java
// ANTES — Spring Security 5.x (não compila no 6.x)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests().antMatchers("/users/**").permitAll();
    }
}

// DEPOIS — Spring Security 6.x
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**").permitAll()
                .anyRequest().authenticated())
            .build();
    }
}
```

**DGS Framework — atualização de versão, sem reescrita:**
```groovy
// ANTES
implementation 'com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter:4.9.21'

// DEPOIS — DGS 10.x integra com Spring for GraphQL internamente
implementation platform('com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:10.x.x')
implementation 'com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter'
```
Anotações `@DgsComponent`, `@DgsQuery`, `@DgsMutation` continuam funcionando sem alteração.

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Java 25 em uso | `./gradlew -version` e `System.getProperty("java.version")` no actuator |
| Spring Boot 4.0.3 | `build.gradle` + `./gradlew dependencies \| grep spring-boot` |
| Build sem warnings | Saída de `./gradlew build` — zero linhas com `[WARNING]` ou `deprecated` |
| Zero imports `javax.*` | `grep -r "import javax\." src/` retorna 0 resultados |
| Zero Joda-Time | `grep "joda" build.gradle` retorna 0 resultados |
| GraphQL operacional | `POST /graphql` com `{"query":"{ tags }"}` retorna resultado |
| Todos os testes passando | `./gradlew test` — verde |
| Virtual threads habilitados | Log de startup confirma; `spring.threads.virtual.enabled=true` no properties |

### Definition of Ready
- [ ] INI-03 concluída (ambiente Docker com Java 25 disponível no container)
- [ ] INI-07 concluída ou em andamento (rede de segurança de testes)
- [ ] INI-08 concluída ou em andamento (testes de contrato)
- [ ] Versão atual do DGS compatível com Spring Boot 4 confirmada no Maven Central
- [ ] Guia de migração Spring Boot 4.0 lido e anotações feitas sobre breaking changes relevantes

### Definition of Done
- [ ] `./gradlew build` — zero warnings de deprecação
- [ ] `./gradlew test` — 100% verde
- [ ] Grep por `import javax.` retorna 0 resultados
- [ ] GraphQL funcionando com DGS 10.x
- [ ] Virtual threads habilitados e confirmados no log
- [ ] ADR registrado sobre a decisão de manter DGS vs Spring for GraphQL puro
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 1 | KR1.1 | Java 25 em execução |
| OKR 1 | KR1.2 | Spring Boot 4.0.3 |
| OKR 1 | KR1.3 | 0 warnings de deprecação no build |
| OKR 1 | KR1.6 | 0 ocorrências de Joda-Time |

### Dependências
- INI-03 concluída
- INI-07 e INI-08 concluídas ou em execução paralela

---

## INI-05 — Migração de MyBatis para Spring Data JPA + Hibernate {#ini-05}

### Por que esta iniciativa existe

MyBatis exige que cada operação de banco seja escrita manualmente: interface de mapper, SQL em XML ou anotação, implementação de repositório. Spring Data JPA gera a maioria dessas operações automaticamente a partir do nome do método ou de uma interface tipada. Para um projeto CRUD como este, o custo de manutenção do MyBatis é desproporcionalmente alto. Além disso, recursos centrais das próximas iniciativas — soft delete via `@Where`, cache via `@Cacheable`, Testcontainers via `@DataJpaTest` — são nativos no JPA e exigiriam implementação manual no MyBatis.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| `mybatis-spring-boot-starter` removido | Elimina dependência legada |
| `spring-boot-starter-data-jpa` adicionado | Framework de persistência moderno com Spring Boot 4 |
| Entidades anotadas com `@Entity`, `@Table`, `@Column` | JPA requer mapeamento explícito das entidades |
| Repositórios como interfaces `JpaRepository<T, ID>` | CRUD automático sem uma linha de implementação |
| Queries complexas via `@Query` JPQL ou SQL nativo | Preserva controle onde necessário sem retornar ao MyBatis |
| Remoção de todos os `*Mapper.java` e XMLs | Eliminação de código morto após a migração |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| QueryDSL ou Specifications para queries dinâmicas | Escopo futuro — `@Query` cobre os casos atuais sem complexidade adicional |
| Cache de segundo nível do Hibernate (EhCache, Redis) | Cache de tempo de leitura é INI-13 — este escopo é só a migração de ORM |
| Soft delete (`@Where`) | Depende desta iniciativa — implementado em INI-12, não aqui |
| Mudanças no schema do banco | Schema permanece igual — apenas a camada de acesso muda |
| Envers (auditoria de entidades) | Funcionalidade avançada fora do escopo desta modernização |

### Guia de migração por componente

| Componente MyBatis atual | Equivalente JPA | Comportamento preservado |
|---|---|---|
| `UserMapper.java` | `UserRepository extends JpaRepository<User, Long>` | findByEmail, findByUsername gerados automaticamente |
| `ArticleMapper.java` | `ArticleRepository extends JpaRepository<Article, Long>` | Queries por slug via `findBySlug(String slug)` |
| `MyBatisUserRepository.java` | Removido — JPA substitui completamente | Mesmas operações, zero código manual |
| XMLs `*.xml` de mapper | Removidos — queries em `@Query` quando necessário | SQL nativo via `@Query(nativeQuery=true)` onde aplicável |
| Configuração mybatis no `application.yml` | Substituída por `spring.jpa.*` | Configuração mais simples |

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| MyBatis removido | `grep "mybatis" build.gradle` → 0 resultados |
| JPA adicionado | `build.gradle` contém `spring-boot-starter-data-jpa` |
| Entidades anotadas | Todas as classes de domínio têm `@Entity` |
| Repositórios JPA | Uma interface `JpaRepository` por entidade |
| Mappers removidos | `find . -name "*Mapper.java"` → 0 resultados |
| Testes passando | `./gradlew test` verde; testes de contrato (INI-08) passam |

### Definition of Ready
- [ ] INI-04 concluída (Spring Boot 4 com JPA disponível)
- [ ] INI-08 concluída (testes de contrato como rede de segurança)
- [ ] Todas as queries MyBatis existentes mapeadas e equivalentes JPA identificados

### Definition of Done
- [ ] 0 referências a MyBatis no codebase e no `build.gradle`
- [ ] 0 arquivos `*Mapper.java` ou XMLs de mapper
- [ ] Todas as entidades com `@Entity` e repositórios JPA
- [ ] `./gradlew test` 100% verde
- [ ] Testes de contrato REST e GraphQL passando
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 1 | KR1.4 | 0 mappers MyBatis remanescentes |

---

## INI-06 — Introdução de record types Java 25 {#ini-06}

### Por que esta iniciativa existe

Java 25 consolida `record types` como recurso estável. Os DTOs da camada `application` são classes com getters, construtores, equals e hashCode gerados manualmente ou via Lombok. Records eliminam esse boilerplate, tornam os objetos imutáveis por padrão (DTOs não devem ser mutáveis) e melhoram a legibilidade. Esta é uma iniciativa de baixo risco e alto ganho de manutenibilidade.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| DTOs da camada `application` convertidos para records | Elimina boilerplate; imutabilidade por padrão |
| Value objects da camada `core` onde aplicável | Mesmos benefícios onde não há `@Entity` |
| Remoção de Lombok nas classes convertidas | Records tornam Lombok desnecessário nesses casos |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Entidades JPA (`@Entity`) convertidas para records | JPA requer construtor sem argumentos e setters — incompatível com records padrão. Soluções workaround existem mas adicionam complexidade sem benefício |
| Classes com estado mutável necessário | Records são imutáveis por design — forçar a conversão quebraria o design |
| Remoção total de Lombok | Lombok continua útil em entidades JPA e classes mutáveis; remoção completa não justifica o custo |
| Sealed classes ou pattern matching avançado | Recursos interessantes do Java 25, mas fora do escopo desta iniciativa específica |

### Critério de elegibilidade para conversão

Uma classe é candidata a record se atende **todos** os critérios:
- É DTO, objeto de transferência ou value object
- Não é anotada com `@Entity`
- Não precisa herdar de outra classe
- Não tem estado mutável necessário externamente (sem setters usados fora da própria classe)
- Não é serializada de forma especial que quebre com records (verificar frameworks de serialização)

### Exemplo de impacto

```java
// ANTES — 30+ linhas de boilerplate por DTO
public class ArticleData {
    private final String slug;
    private final String title;
    private final String description;
    private final boolean favorited;
    private final int favoritesCount;
    // construtor com 8+ parâmetros
    // 8 getters
    // equals() e hashCode() manuais ou Lombok
    // toString()
}

// DEPOIS — 6 linhas, comportamento idêntico
public record ArticleData(
    String slug,
    String title,
    String description,
    boolean favorited,
    int favoritesCount
) {}
// equals, hashCode, toString, getters gerados automaticamente pelo compilador
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| ≥ 80% dos DTOs como records | Contagem de classes `record` vs classes DTO no pacote `application` |
| Build limpo | `./gradlew build` sem warnings relacionados a records |
| Testes passando | `./gradlew test` verde |

### Definition of Ready
- [ ] INI-04 concluída (Java 25 em execução)
- [ ] Lista de DTOs candidatos à conversão mapeada

### Definition of Done
- [ ] ≥ 80% dos DTOs são record types (KR1.5)
- [ ] Testes passando sem alteração de comportamento
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 1 | KR1.5 | ≥ 80% dos DTOs usando record types |

---

## INI-07 — Cobertura de testes por mutação: Pitest 95% {#ini-07}

### Por que esta iniciativa existe

Cobertura de linhas mede quais linhas foram executadas durante os testes — não se os testes verificam o comportamento correto. Um teste que executa código mas não faz assert conta como 100% de cobertura e detecta zero bugs. Testes de mutação são diferentes: eles modificam o código (invertem condições, trocam operadores, removem retornos) e verificam se os testes detectam essas mudanças. Se o teste não detecta a mutação, o teste não protege contra aquele tipo de bug.

95% de mutation score significa: 95 de cada 100 bugs artificialmente introduzidos são capturados. É a rede de segurança que torna as iniciativas de modernização (INI-04, INI-05) viáveis sem risco de regressão silenciosa.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Pitest configurado no `build.gradle` | Ferramenta de mutation testing mais madura para Java |
| Threshold de 95% no CI — build falha se abaixo | Sem threshold no CI, a cobertura é sugestão, não garantia |
| Testes adicionais para atingir 95% | O estado atual não atinge 95% — testes precisam ser escritos |
| Relatório HTML/XML publicado no CI | Visibilidade do progresso; identifica quais mutações sobrevivem |
| Exclusão de código gerado (DGS codegen) | Código gerado automaticamente não deve ser incluído na métrica |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Testes de integração de contrato | INI-08 — tipo de teste diferente, escopo diferente |
| Testes E2E com Playwright | INI-09 |
| Cobertura de código gerado pelo DGS codegen | Código gerado não é responsabilidade da equipe testar — excluído da configuração Pitest |
| 100% de mutation score | O custo para ir de 95% para 100% é exponencialmente maior e traz retorno marginal; 95% é o ponto de equilíbrio ideal |

### Configuração Pitest esperada

```groovy
// build.gradle
plugins {
    id 'info.solidsoft.pitest' version '1.15.0'
}

pitest {
    targetClasses   = ['io.spring.api.*',
                       'io.spring.core.*',
                       'io.spring.application.*',
                       'io.spring.infrastructure.*']
    excludedClasses = ['io.spring.graphql.*']  // código gerado DGS
    mutators        = ['STRONGER']             // conjunto mais rigoroso que DEFAULT
    threads         = 4
    outputFormats   = ['HTML', 'XML']
    timestampedReports    = false
    mutationThreshold     = 95
    coverageThreshold     = 95
    failWhenNoMutations   = true
    junit5PluginVersion   = '1.2.1'
}
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Pitest configurado | `./gradlew pitest` executa sem erro de configuração |
| Mutation score ≥ 95% | Relatório HTML em `build/reports/pitest/index.html` |
| CI falha se score cair | Remover um assert de teste → CI vermelho |
| Relatório publicado | Artefato disponível no histórico do CI |

### Definition of Ready
- [ ] INI-04 concluída (Java 25 — Pitest requer JVM compatível)
- [ ] Baseline de testes existentes executado e contado

### Definition of Done
- [ ] `./gradlew pitest` — mutation score ≥ 95%
- [ ] CI configurado e testado: threshold ativo
- [ ] Relatório acessível como artefato do CI
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 2 | KR2.1 | ≥ 95% mutation score (Pitest) |
| OKR 2 | KR2.5 | CI falha automaticamente se score cair |

---

## INI-08 — Testes de integração: contratos REST e GraphQL {#ini-08}

### Por que esta iniciativa existe

Testes de integração verificam que o contrato da API — o que foi prometido ao consumidor — continua válido após qualquer mudança. Sem eles, um upgrade de framework, uma refatoração de repositório ou uma mudança de configuração pode silenciosamente quebrar um endpoint sem que nenhum teste detecte. Esta iniciativa é a rede de segurança para INI-04 e INI-05.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Testes para todos os 19 endpoints REST | Cobertura completa do contrato REST atual |
| Testes para todas as 18 operações GraphQL | Cobertura completa do contrato GraphQL atual |
| Cenários de erro (401, 403, 404, 422) | Erros são parte do contrato — clientes dependem deles |
| Testcontainers com PostgreSQL real | Testes contra banco real eliminam falsos positivos que H2 introduziria |
| Testes de autenticação (com e sem token) | A maioria dos endpoints tem comportamento diferente dependendo de auth |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Testes de performance / carga | Escopo diferente — requer ferramentas específicas (JMeter, Gatling) |
| Testes de segurança (penetration testing) | Fora do escopo desta modernização |
| Testes de contrato com Pact (consumer-driven) | Não há consumidor externo definido — overhead desnecessário agora |
| Mocking do banco de dados (H2) | H2 tem comportamento diferente do PostgreSQL — usar banco real via Testcontainers |

### Cobertura mínima por domínio

**Usuários (7 endpoints) — cenários obrigatórios:**

| Endpoint | ✅ Sucesso | ❌ Erro |
|---|---|---|
| `POST /users/login` | Credenciais válidas → token JWT | Senha errada → 401; Email inexistente → 401 |
| `POST /users` | Dados válidos → usuário criado | Email duplicado → 422; Campo faltando → 422 |
| `GET /user` | Token válido → usuário atual | Sem token → 401; Token expirado → 401 |
| `PUT /user` | Atualização válida | Sem token → 401; Email já usado → 422 |
| `GET /profiles/:username` | Perfil existente | Username inexistente → 404 |
| `POST /profiles/:username/follow` | Seguir com sucesso | Sem token → 401; Username inexistente → 404 |
| `DELETE /profiles/:username/follow` | Deixar de seguir | Sem token → 401 |

**Artigos (7 endpoints), Comentários (3), Favoritos (2), Tags (1):** cobertura análoga — ao menos um cenário de sucesso e um de erro por endpoint.

**GraphQL (18 operações):** queries (`article`, `articles`, `me`, `feed`, `profile`, `tags`) e mutations (`createUser`, `login`, `updateUser`, `followUser`, `unfollowUser`, `createArticle`, `updateArticle`, `favoriteArticle`, `unfavoriteArticle`, `deleteArticle`, `addComment`, `deleteComment`) — ao menos um cenário de sucesso por operação.

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| 19 endpoints REST cobertos | Contagem de classes de teste: uma por controller |
| 18 operações GraphQL cobertas | Contagem de métodos de teste por operação GraphQL |
| Testcontainers funcionando | Testes sobem container PostgreSQL; log mostra porta dinâmica |
| CI verde | `./gradlew test` verde com banco real |

### Definition of Ready
- [ ] INI-03 concluída (Docker disponível para Testcontainers)
- [ ] Mapeamento de API completo (`API-mapping.md`) disponível

### Definition of Done
- [ ] 100% dos 19 endpoints REST com ≥ 1 cenário de sucesso + ≥ 1 cenário de erro
- [ ] 100% das 18 operações GraphQL com ≥ 1 cenário de sucesso
- [ ] PostgreSQL real via Testcontainers (sem H2)
- [ ] `./gradlew test` 100% verde
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 2 | KR2.2 | 100% dos endpoints REST cobertos |
| OKR 2 | KR2.3 | 100% das operações GraphQL cobertas |
| OKR 2 | KR2.6 | 0 regressões não detectadas por testes |

---

## INI-09 — Testes end-to-end com Playwright {#ini-09}

### Por que esta iniciativa existe

Testes de unidade e integração verificam componentes isolados e contratos de API. Testes E2E verificam fluxos completos do ponto de vista de um cliente real — sequência de chamadas, estado compartilhado entre requisições, comportamento de ponta a ponta. O Playwright, usado aqui como cliente HTTP (não como browser), permite simular esses fluxos de forma legível e mantível.

O requisito explícito da gestão inclui dois branches: um com testes falhando e um com testes funcionando — documentando o antes e depois do processo.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Playwright configurado como cliente HTTP de API | Simula fluxos reais de consumidor — não mockado |
| Branch `feat/playwright-broken` | Documenta o estado problemático antes da correção — requisito explícito da gestão |
| Branch `feat/playwright-working` | Solução verificável e didática |
| ≥ 5 fluxos críticos E2E | Cobertura dos happy paths mais importantes para o produto |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Testes de UI / browser (Playwright como browser) | Este projeto é backend — não há UI para testar |
| Testes de performance end-to-end | Ferramentas específicas para isso (Gatling, k6) |
| Cobertura de todos os fluxos possíveis | 5 fluxos críticos cobrem os cenários de maior risco; cobertura total tem retorno decrescente |
| Playwright substituindo Pitest | Tipos diferentes de teste — complementares, não substitutos |

### Fluxos E2E obrigatórios

| Fluxo | Sequência de chamadas | Por que é crítico |
|---|---|---|
| **Registro e login** | `POST /users` → `POST /users/login` → `GET /user` | Porta de entrada — se quebrar, nada funciona |
| **Criar e ler artigo** | Login → `POST /articles` → `GET /articles/:slug` → validar campos | Fluxo principal do produto |
| **Interação social** | Login como A → `POST /profiles/B/follow` → `GET /articles/feed` → validar artigos de B | Funcionalidade social completa |
| **Comentar e deletar** | Login → criar artigo → `POST /comments` → `DELETE /comments/:id` → `GET /comments` validar ausência | Ciclo completo de comentários |
| **Soft delete** (após INI-12) | Login → criar artigo → `DELETE /articles/:slug` → `GET /articles` → validar ausência na listagem | Novo comportamento — verificação E2E necessária |

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Branch `feat/playwright-broken` | Existe no GitHub; CI vermelho neste branch |
| Branch `feat/playwright-working` | Existe no GitHub; CI verde neste branch |
| ≥ 5 fluxos cobertos | Relatório Playwright lista os testes |

### Definition of Ready
- [ ] INI-03 concluída (ambiente Docker para testes)
- [ ] INI-08 concluída (contratos documentados e testados)

### Definition of Done
- [ ] Branch `playwright-broken` com CI vermelho documentado
- [ ] Branch `playwright-working` com ≥ 5 fluxos passando
- [ ] CI verde no branch `working`
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 2 | KR2.4 | ≥ 5 fluxos E2E cobertos com Playwright |

---

## INI-10 — Observabilidade com LGTM Stack {#ini-10}

### Por que esta iniciativa existe

Sem observabilidade, diagnóstico de problema depende de suposição. Esta iniciativa atende três requisitos explícitos da gestão: contador de chamadas por endpoint, log de startup validável, log de shutdown validável. Adicionalmente entrega o dashboard de observabilidade completo com LGTM Stack.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Contador Micrometer em todos os 19 endpoints REST | Requisito explícito da gestão — cada endpoint incrementa uma métrica |
| Log de startup estruturado (JSON) | Requisito explícito — validado pelo script Python da INI-03 |
| Log de shutdown estruturado (JSON) | Requisito explícito — validado pelo script Python da INI-03 |
| Traces via OpenTelemetry + Tempo | Spring Boot 4 tem OpenTelemetry starter nativo — sem configuração adicional pesada |
| Loki para logs agregados | Complementa métricas com logs pesquisáveis no Grafana |
| Dashboard Grafana: chamadas/endpoint, latência p95, taxa de erro | Mínimo útil para diagnóstico de incidente |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Alertas automáticos (PagerDuty, OpsGenie) | Infraestrutura de produção — fora do escopo desta modernização |
| Métricas de banco de dados (queries lentas, pool) | Fase futura — métricas de negócio têm prioridade agora |
| APM completo (Datadog, New Relic) | Custo de licença + complexidade desnecessários para este projeto |
| Métricas de segurança (failed logins, rate limiting) | Rate limiting não está implementado — métricas de algo inexistente não fazem sentido |
| Logs de auditoria de negócio | Funcionalidade de produto futura — não de observabilidade técnica |

### Implementação do contador por endpoint

```java
// Padrão para todos os endpoints — exemplo para list articles
@GetMapping("/articles")
public ResponseEntity listArticles(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(defaultValue = "0") int offset,
        MeterRegistry meterRegistry) {

    Counter.builder("api.requests.total")
        .tag("endpoint", "list_articles")
        .tag("method", "GET")
        .tag("path", "/articles")
        .register(meterRegistry)
        .increment();

    // lógica do endpoint
}
```

Ou via AOP para não poluir os controllers — decisão a ser tomada pelo time e registrada como ADR.

### Estrutura dos logs de startup e shutdown

```json
// Log de startup (estruturado JSON via Logback)
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

// Log de shutdown
{
  "timestamp": "2026-06-09T10:05:00.000Z",
  "level": "INFO",
  "event": "application_shutdown",
  "application": "realworld-api",
  "reason": "JVM shutdown hook",
  "message": "Application shutdown complete"
}
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| 19/19 endpoints com contador | Fazer uma chamada → `http://localhost:9090/metrics` mostra `api.requests.total` incrementado |
| Log de startup validado | Script Python da INI-03 passa após adicionar campos estruturados |
| Log de shutdown validado | Script Python da INI-03 passa |
| Traces visíveis | Fazer chamada → Grafana/Tempo mostra trace |
| Dashboard Grafana | `http://localhost:3000` mostra dados reais após chamadas |

### Definition of Ready
- [ ] INI-03 concluída (LGTM Stack no Docker Compose)
- [ ] INI-04 concluída (Spring Boot 4 com OpenTelemetry starter nativo)

### Definition of Done
- [ ] 19/19 endpoints com contador verificado no Prometheus
- [ ] Logs de startup e shutdown passando no script Python
- [ ] Traces visíveis no Tempo/Grafana
- [ ] Dashboard com ≥ 3 painéis: chamadas por endpoint, latência p95, taxa de erro
- [ ] KR5.6 verificado — gameday de diagnóstico ≤ 15 minutos
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 5 | KR5.1 | 100% endpoints com contador |
| OKR 5 | KR5.2 | Log startup validado 100% |
| OKR 5 | KR5.3 | Log shutdown validado 100% |
| OKR 5 | KR5.4 | 100% requisições com trace |
| OKR 5 | KR5.5 | ≥ 1 dashboard funcional |
| OKR 5 | KR5.6 | ≤ 15 min diagnóstico no gameday |

---

## INI-11 — Documentação de API com OpenAPI / Swagger {#ini-11}

### Por que esta iniciativa existe

19 endpoints REST e 18 operações GraphQL existem e funcionam. Nenhum está documentado formalmente. Qualquer integração depende de leitura de código. Esta iniciativa gera documentação viva — sempre sincronizada com o código — via springdoc-openapi 2.x.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| `springdoc-openapi-starter-webmvc-ui` no `build.gradle` | Geração automática de OpenAPI a partir de anotações Spring MVC |
| Anotações `@Operation`, `@ApiResponse` nos controllers | Enriquece a documentação gerada com descrições legíveis |
| Swagger UI em `/swagger-ui.html` | Interface navegável e testável sem Postman ou curl |
| Schema JSON em `/v3/api-docs` | Contrato formal consumível por ferramentas (Postman, Insomnia, geradores de SDK) |
| Documentação dos campos novos de INI-12 e INI-13 | `readingTimeMinutes`, `isDeleted` documentados junto com os endpoints |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Documentação do GraphQL via ferramenta separada | O arquivo `schema.graphqls` já serve como documentação formal do contrato GraphQL |
| Geração de SDK cliente (Java, TypeScript) | Overhead de manutenção sem consumidor definido |
| Versionamento de API (/v1, /v2) | Spring Boot 4 tem API Versioning nativo — decisão futura |
| Publicação em portal de API externo | Infraestrutura fora do escopo |

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Swagger UI acessível | `http://localhost:8080/swagger-ui.html` abre sem erro |
| 19 endpoints documentados | Contagem no Swagger UI |
| Request e response schemas | Cada endpoint com body mostra schema completo |
| `/v3/api-docs` válido | URL retorna JSON OpenAPI válido (validável em editor.swagger.io) |

### Definition of Ready
- [ ] INI-04 concluída (Spring Boot 4 com springdoc 2.x compatível)
- [ ] INI-12 e INI-13 em andamento (documentar campos novos junto)

### Definition of Done
- [ ] 19/19 endpoints no Swagger UI com descrição, request e response
- [ ] Campos `readingTimeMinutes` e comportamento de soft delete documentados
- [ ] KR6.4 verificado
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 6 | KR6.4 | 100% endpoints REST documentados |

---

## INI-12 — Soft delete de artigos e comentários {#ini-12}

### Por que esta iniciativa existe

Deleção permanente é irreversível e impossibilita auditoria. Soft delete — registros marcados como `is_deleted = true` em vez de removidos — preserva o histórico, habilita auditoria futura e é pré-requisito para qualquer conformidade com requisitos de retenção de dados. Para o usuário, o comportamento é idêntico. Para o negócio, a diferença é total.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Migration Flyway: `is_deleted` em `articles` | Mudança de schema versionada e rastreável |
| Migration Flyway: `is_deleted` em `comments` | Mesma proteção para comentários |
| `@Where(clause="is_deleted=false")` nas entidades JPA | Filtragem automática — queries existentes não precisam ser alteradas |
| Substituição de `repository.delete()` por `entity.setDeleted(true)` | Mudança de comportamento de deleção — central desta iniciativa |
| Testes de mutação ≥ 95% para o código novo | Garantia de qualidade para funcionalidade nova |
| OpenAPI atualizado com nota sobre soft delete | Documentação do comportamento para consumidores da API |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Interface de admin para consultar registros deletados | Funcionalidade futura — não faz parte desta iniciativa |
| Recuperação de registros deletados via API | Funcionalidade futura — soft delete cria a base, recuperação é próximo passo |
| Soft delete em `users` | Deleção de conta de usuário tem implicações legais (LGPD) que exigem análise própria |
| Soft delete em `tags` | Tags não são deletadas pelos usuários no fluxo atual |
| Purge automático de registros antigos (TTL) | Política de retenção deve ser definida pelo negócio antes de implementar |
| `@SQLDelete` do Hibernate como alternativa | `@Where` + service layer é mais explícito e legível para o time |

### Comportamento antes e depois

| Ação do usuário | Antes (hard delete) | Depois (soft delete) |
|---|---|---|
| `DELETE /articles/:slug` | `DELETE FROM articles WHERE slug = ?` | `UPDATE articles SET is_deleted = true WHERE slug = ?` |
| `GET /articles` | Artigo não existe | Artigo com `is_deleted=true` não aparece (filtrado por `@Where`) |
| `GET /articles/:slug` (deletado) | 404 Not Found | 404 Not Found — comportamento visível idêntico |
| Query direta no banco | Registro removido para sempre | `SELECT * FROM articles WHERE is_deleted = true` retorna histórico |

### Migration Flyway esperada

```sql
-- V{n}__add_soft_delete_to_articles_and_comments.sql
ALTER TABLE articles
  ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE comments
  ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Índice para performance — queries de listagem filtram por is_deleted=false
CREATE INDEX idx_articles_is_deleted ON articles(is_deleted);
CREATE INDEX idx_comments_is_deleted ON comments(is_deleted);
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Colunas criadas | `\d articles` e `\d comments` no psql mostram `is_deleted` |
| Deleção lógica | `DELETE /articles/:slug` → `SELECT is_deleted FROM articles WHERE slug=?` retorna `true` |
| Listagem filtrada | `GET /articles` não retorna artigos com `is_deleted=true` |
| Testes de mutação | Pitest ≥ 95% incluindo novo código |
| Testes de integração passando | Comportamento visível idêntico ao anterior |

### Definition of Ready
- [ ] INI-05 concluída (Spring Data JPA — necessário para `@Where`)
- [ ] INI-08 concluída (rede de segurança de testes)
- [ ] INI-07 concluída (Pitest configurado)

### Definition of Done
- [ ] Colunas `is_deleted` em `articles` e `comments`
- [ ] `DELETE /articles/:slug` → registro existe no banco com `is_deleted = true`
- [ ] `GET /articles` → artigos deletados não aparecem
- [ ] Pitest ≥ 95% para código novo
- [ ] Testes de integração existentes passando sem alteração
- [ ] OpenAPI atualizado
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 4 | KR4.2 | 0% hard delete em artigos |
| OKR 4 | KR4.3 | 0% hard delete em comentários |
| OKR 7 | KR7.1 | ≥ 95% mutation score para soft delete |
| OKR 7 | KR7.4 | Registros deletados consultáveis via query SQL |

---

## INI-13 — Tempo estimado de leitura com cache lazy {#ini-13}

### Por que esta iniciativa existe

Tempo de leitura é uma das features de UX mais simples e impactantes para plataformas de conteúdo. Saber que um artigo leva 2 ou 20 minutos para ler muda a decisão do usuário. O cálculo é determinístico (200 palavras/minuto), barato computacionalmente e pode ser persistido — eliminando recálculo desnecessário. Para artigos existentes, a atualização lazy evita uma migration pesada que processe todos os registros de uma vez.

### O que está incluído e por quê

| Entregável | Por que está incluído |
|---|---|
| Migration Flyway: `reading_time_minutes INTEGER` em `articles` (nullable) | Nullable permite artigos existentes sem o valor — lazy update os preenche |
| Cálculo automático em `POST /articles` | Novos artigos já nascem com o campo preenchido |
| Recálculo em `PUT /articles/:slug` (quando body muda) | Edição do conteúdo muda a contagem de palavras — campo deve ser atualizado |
| Lazy update em `GET /articles/:slug` | Artigos existentes sem o campo: calcular, persistir e retornar na primeira leitura |
| Campo `readingTimeMinutes` em todos os responses de artigo | REST e GraphQL retornam o campo |
| Testes de mutação ≥ 95% | Garantia de qualidade |
| OpenAPI e schema GraphQL atualizados | Documentação do novo campo |

### O que está fora do escopo e por quê

| Fora do escopo | Por que foi excluído |
|---|---|
| Velocidade de leitura configurável por usuário | Funcionalidade futura — 200 palavras/minuto é suficiente para v1 |
| Cálculo diferenciado para código vs prosa | Algoritmo mais sofisticado — custo de implementação desproporcional ao ganho agora |
| Cache distribuído (Redis) para leitura | Para o volume atual, persistência no PostgreSQL + lazy update é suficiente; Redis é over-engineering para este momento |
| Atualização assíncrona em background (queue) | Complexidade desnecessária — lazy update síncrono tem latência aceitável (operação trivial) |
| Lazy update em `GET /articles` (listagem) | Listagem retorna muitos artigos — lazy update em bulk pode causar latência. Apenas `GET /articles/:slug` faz lazy update |

### Lógica de cálculo

```java
public static int calculateReadingTime(String body) {
    if (body == null || body.isBlank()) {
        return 1; // mínimo de 1 minuto
    }
    int wordCount = body.trim().split("\\s+").length;
    return Math.max(1, (int) Math.ceil((double) wordCount / 200.0));
}

// Exemplos:
// 50 palavras  → ceil(50/200)  = ceil(0.25) = 1  → 1 minuto (mínimo)
// 200 palavras → ceil(200/200) = ceil(1.0)  = 1  → 1 minuto
// 201 palavras → ceil(201/200) = ceil(1.005)= 2  → 2 minutos
// 600 palavras → ceil(600/200) = ceil(3.0)  = 3  → 3 minutos
// 2500 palavras → ceil(2500/200) = ceil(12.5) = 13 → 13 minutos
```

### Comportamento esperado por cenário

| Cenário | O que acontece |
|---|---|
| `POST /articles` com 400 palavras | `reading_time_minutes = 2` calculado e salvo no momento da criação |
| `PUT /articles/:slug` mudando `body` | Recalcula e salva novo valor |
| `PUT /articles/:slug` mudando só `title` | Não recalcula — body não mudou |
| `GET /articles/:slug` — artigo com campo preenchido | Retorna diretamente, sem calcular |
| `GET /articles/:slug` — artigo existente sem campo (null) | Calcula, persiste, retorna — lazy update |
| `GET /articles` (listagem) | Retorna `reading_time_minutes` se preenchido; `null` se não (sem lazy update aqui) |

### Migration Flyway esperada

```sql
-- V{n+1}__add_reading_time_to_articles.sql
ALTER TABLE articles
  ADD COLUMN reading_time_minutes INTEGER;
-- Nullable intencionalmente: artigos existentes recebem o valor via lazy update
-- NOT NULL seria adicionado após todos os registros estarem preenchidos (fase futura)
```

### Entregáveis verificáveis

| Entregável | Como verificar |
|---|---|
| Coluna criada | `\d articles` mostra `reading_time_minutes` |
| `POST /articles` retorna campo | Response inclui `"readingTimeMinutes": N` |
| `PUT` recalcula quando body muda | Criar artigo → editar body → verificar novo valor |
| Lazy update funcional | Artigo sem campo → `GET /articles/:slug` → campo calculado e persistido no banco |
| GraphQL retorna campo | Query `article { readingTimeMinutes }` funciona |
| Pitest ≥ 95% | Relatório Pitest inclui novo código |

### Definition of Ready
- [ ] INI-05 concluída (Spring Data JPA)
- [ ] INI-07 concluída (Pitest configurado)
- [ ] INI-08 concluída (rede de segurança)
- [ ] INI-12 pode ser feita em paralelo (independente)

### Definition of Done
- [ ] Coluna `reading_time_minutes` em `articles`
- [ ] `POST /articles` retorna `readingTimeMinutes` calculado
- [ ] `GET /articles/:slug` faz lazy update e retorna campo
- [ ] `PUT /articles/:slug` recalcula quando `body` é alterado
- [ ] Pitest ≥ 95% para código novo
- [ ] Testes de integração passando para todos os cenários
- [ ] OpenAPI e schema GraphQL atualizados com `readingTimeMinutes`
- [ ] Prompts e skills documentados no Coda

### OKRs rastreados
| OKR | KR | Meta |
|---|---|---|
| OKR 4 | KR4.4 | 0% artigos criados sem `reading_time_minutes` |
| OKR 4 | KR4.5 | 100% artigos existentes com campo após 1ª leitura |
| OKR 7 | KR7.2 | ≥ 95% mutation score |
| OKR 7 | KR7.3 | Campo documentado no OpenAPI e GraphQL |
| OKR 7 | KR7.5 | 100% artigos resolvidos após primeira leitura |

---

## Mapa de dependências entre iniciativas

```
INI-01 ── Processo
  └─▶ INI-02 ── Segurança
        └─▶ INI-03 ── Containerização
              ├─▶ INI-07 ──────────────────────────┐ paralelo
              ├─▶ INI-08 ──────────────────────────┤ paralelo
              │     └─▶ INI-09 (Playwright)         │
              └─▶ INI-04 ── Stack Java 25 / SB 4 ◀─┘
                    ├─▶ INI-05 ── JPA / Hibernate
                    │     ├─▶ INI-12 ── Soft delete  ┐ paralelo
                    │     └─▶ INI-13 ── Leitura       ┘
                    ├─▶ INI-06 ── Records  (paralelo com INI-05)
                    └─▶ INI-10 ── Observabilidade
                          └─▶ INI-11 ── OpenAPI (junto com INI-12/13)
```

---

## Resumo executivo das escolhas tecnológicas

| Decisão | Escolha | Alternativa descartada | Motivo do descarte |
|---|---|---|---|
| Runtime | **Java 25 LTS** | Java 21 LTS | Java 25 tem 4 anos a mais de suporte gratuito; gestão especificou Java 25 |
| Framework | **Spring Boot 4.0.3** | Spring Boot 3.5 | 3.5 encerra suporte em junho/2026; 4.0.3 é o LTS atual |
| Build | **Gradle 9.3.1** | Maven | Gestão especificou Gradle 9.3.1; Spring Boot 4 suporta nativamente |
| ORM | **Spring Data JPA + Hibernate** | Manter MyBatis | JPA é nativo no Spring Boot 4; soft delete e cache dependem de JPA |
| GraphQL | **DGS 10.x** (integra com Spring for GraphQL) | Reescrever para Spring for GraphQL puro | DGS 10.x usa Spring for GraphQL internamente — sem custo de reescrita |
| Concorrência | **Virtual threads** (Loom) | WebFlux reativo | Mesmo ganho de escala, código imperativo, zero reescrita |
| Banco | **PostgreSQL 16** | MySQL, H2 | PostgreSQL: ecossistema maduro, JSON nativo, melhor suporte a migração futura |
| Testes de mutação | **Pitest** | Stryker (JS) | Pitest é nativo JVM; Stryker é para JavaScript/TypeScript |
| Observabilidade | **LGTM Stack** (Loki+Grafana+Tempo+Prometheus) | Datadog, New Relic | LGTM é open source; Spring Boot 4 tem OpenTelemetry nativo alinhado |

---

*Documento vivo — atualizar status ao fechar cada iniciativa*  
*Toda mudança de escopo refletida aqui antes de ser implementada*  
*13 iniciativas · 7 OKRs · 40 KRs · Stack validada em junho 2026*

As principais correções desta revisão: a versão do Spring Boot foi mantida em 4.0.3 conforme o literal do mandato (decisão da PM em 2026-06-22, GAP-D); o risco do DGS Framework foi completamente revisado — DGS 10.x integra nativamente com Spring for GraphQL, então não é breaking change algum; Java 25 foi confirmado como LTS com suporte até 2030; Gradle 9.3.1 foi confirmado como compatível com Spring Boot 4; e virtual threads foram incluídos como a escolha de escalabilidade de melhor custo-benefício em vez de WebFlux.