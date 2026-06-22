## 04 — Roadmap
> Versão 5.0 · Junho 2026 · Correções pós double-check  
> Projeto: RealWorld Platform Modernization  
> Responsável: Product Management

---

## Registro de revisões

| Versão | O que mudou |
|---|---|
| v1.0 | Versão inicial |
| v2.0 | Fase de testes dedicada; pirâmide de testes documentada |
| v3.0 | Linguagem de negócio; 8 demandas da gestão rastreadas; Fase 6 adicionada |
| v4.0 | 11 gaps corrigidos: fases consolidadas, dependências explicitadas, regra de duplas, Fase 6 vinculada a iniciativas |
| **v5.0** | **7 gaps corrigidos após double-check:** GAP-D versão SB na tabela de demandas; GAP-A explicação da ausência de Fase 2; GAP-B dependência cross-phase INI-09→INI-12 explicitada; GAP-C contagem de fluxos Playwright alinhada (MT=3, M6=7); GAP-E EPIC-11 separado em Fase 4 (setup) + Fase 6 (campos novos); GAP-G Gameday 1 e Gameday 2 nomeados explicitamente |

---

## Para quem é este documento

**Stakeholders e gestão:** leia as seções "O que é" e "O que muda para o negócio" de cada fase, e a tabela de marcos ao final. Você encontrará linguagem clara sobre o que está sendo entregue, por que importa e como saberemos que está concluído.

**Time de desenvolvimento:** leia o documento completo. Cada fase tem escopo técnico, iniciativas vinculadas, dependências explícitas, organização de duplas e critérios verificáveis de entrada e saída.

---

## As 8 demandas da gestão e onde vivem neste roadmap

| # | Demanda da gestão | Fase | Iniciativa |
|---|---|---|---|
| 1 | Migrar MyBatis para Spring Data JPA/Hibernate | Fase 3 | INI-05 |
| 2 | Migrar para Java 25 | Fase 3 | INI-04 |
| 3 | Migrar para Spring Boot 4.0.3 ¹ | Fase 3 | INI-04 |
| 4 | Migrar para Gradle 9.3.1+ sem deprecation warnings | Fase 3 | INI-04 |
| 5 | Introduzir record types onde possível | Fase 3 | INI-06 |
| 6 | Soft delete com flag `is_deleted` | Fase 5 | INI-12 |
| 7 | Tempo de leitura estimado por artigo (200 wpm) | Fase 5 | INI-13 |
| 8 | Cache do tempo de leitura com lazy update | Fase 5 | INI-13 |

> ¹ **Nota sobre versão — GAP-D resolvido:** a gestão especificou Spring Boot **4.0.3** (item J3 do mandato). Em 2026-06-22 a PM Franciele decidiu **manter o literal do mandato (`4.0.3`)**. Este documento foi alinhado para usar `4.0.3` de forma consistente. Histórico: uma revisão anterior havia proposto adotar `4.0.6` (patches posteriores da mesma linha); essa proposta foi substituída pela decisão da PM. Ver `docs/00-original-mandate.md` (Divergências — GAP-D).

---

## Nota sobre numeração de fases — GAP-A corrigido

> **Por que não existe Fase 2?**
>
> Na versão original das iniciativas (`03-initiatives.md`), INI-02 (segurança) e INI-03 (containerização) eram candidatas a fases separadas. Durante a consolidação do Roadmap v4.0, as duas foram **colapsadas em uma única Fase 1** porque são sequenciais por dependência técnica — o Docker Compose de INI-03 precisa injetar o `JWT_SECRET` configurado por INI-02. Fases separadas para trabalho sequencial criavam overhead desnecessário de processo.
>
> A numeração salta de **Fase 1 → Fase 3** para manter alinhamento com os números dos documentos do projeto (`03-initiatives.md`, `04-roadmap.md`). Não existe Fase 2 intencionalmente.

---

## Visão geral das fases

```
FASE 0            FASE 1                  FASE 3
Processo      →   Segurança e Ambiente →  Modernização do Stack
INI-01            INI-02 → INI-03         INI-04
                                          INI-05 (após INI-04)
                                          INI-06 (após INI-04, paralelo com INI-05)

                  ⚠️ INI-07 + INI-08 iniciam em paralelo com INI-04

FASE TESTES       FASE 4                      FASE 5            FASE 6
Qualidade     →   Visibilidade           →    Produto       →   Validação Final
INI-07            INI-10                       INI-12            Consolidação +
INI-08            INI-11 setup inicial          INI-13 (paralelo) OpenAPI atualizado +
INI-09 (3 fluxos) OpenAPI campos novos ──────────────────────▶  Playwright 7 fluxos
```

---

## Mapa de dependências entre iniciativas

```
INI-01 (Processo)
  └─▶ INI-02 (JWT + perfis)
        └─▶ INI-03 (Docker + PostgreSQL + Actuator + LGTM)
              ├─▶ INI-04 (Java 25 + SB 4.0.3 + Gradle 9.3.1) ◀─┐
              │     ├─▶ INI-05 (JPA)                             │ paralelo
              │     │     ├─▶ INI-12 (Soft delete)               │ durante
              │     │     └─▶ INI-13 (Tempo de leitura)          │ INI-04
              │     └─▶ INI-06 (Records) ← paralelo com INI-05   │
              ├─▶ INI-07 (Pitest 95%) ─────────────────────────▶─┤
              ├─▶ INI-08 (Integração) ─────────────────────────▶─┘
              │     └─▶ INI-09 (Playwright — fluxos 1, 2, 3)
              │               └─▶ fluxos 4 e 5 dependem de INI-12 ⚠️ (Fase 6)
              ├─▶ INI-10 (Observabilidade)
              └─▶ INI-11 (OpenAPI setup) ← paralelo com INI-10
                    └─▶ INI-11 update campos ← após INI-12 + INI-13 (Fase 6)
```

---

## Fase 0 — Processo de trabalho

### O que é, em linguagem de negócio

Antes de escrever uma linha de código, o time precisa de um processo de trabalho claro e rastreável. Esta fase estabelece as regras do jogo: como o trabalho é descrito antes de começar, como os commits são nomeados, o que significa "pronto" e onde ficam documentados os prompts e decisões do desenvolvimento assistido por IA.

Em um projeto de vibe coding — onde código é gerado rapidamente com assistência de IA — a ausência de processo transforma velocidade em risco. Código gerado sem rastreabilidade é código que ninguém consegue revisar, auditar ou evoluir com segurança.

### O que muda para o negócio

- Toda mudança no código terá uma origem documentada — issue vinculada antes de codar
- O histórico de decisões ficará registrado para consulta futura no Coda
- A gestão conseguirá acompanhar o progresso de forma transparente pelo GitHub

### Iniciativa desta fase

| Iniciativa | Descrição |
|---|---|
| **INI-01** | Fundação do processo de desenvolvimento |

### O que o time entrega

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| Templates de issues no GitHub | Antes de codar, uma issue descreve o quê e o porquê | Criar issue — template aparece |
| Definition of Ready (DoR) | Lista do que precisa estar claro antes de iniciar | `docs/process/definition-of-ready.md` existe |
| Definition of Done (DoD) | Lista do que precisa estar feito para ser concluído | `docs/process/definition-of-done.md` existe |
| Conventional Commits no CI | Todo commit segue padrão — CI rejeita o que estiver fora | Push fora do padrão → CI falha |
| Template de Pull Request | PR sem issue vinculada não passa | Abrir PR — checklist aparece |
| Branch `bleeding` configurado | Recebe commits automáticos para validação contínua | Branch existe no GitHub |
| Coda configurado | Prompts e skills documentados por etapa | Link no README funciona |
| GitAhead instalado | Ferramenta visual para validar histórico de commits | Instalado nos ambientes do time |

### ✅ Marco M0

> CI rejeita automaticamente commits fora do padrão Conventional Commits. Pelo menos uma issue criada com template. DoR e DoD revisadas e aceitas pelo time. Primeiro registro de prompt documentado no Coda.

---

## Fase 1 — Segurança e ambiente de desenvolvimento

### O que é, em linguagem de negócio

Esta fase resolve dois grupos de problemas que precisam ser eliminados antes de qualquer outro trabalho — um de segurança, um de infraestrutura. Estão na mesma fase porque são sequenciais: o ambiente Docker depende das variáveis de ambiente configuradas pela parte de segurança.

**Grupo 1 — Segurança (INI-02):** a chave secreta que protege o sistema de autenticação está visível publicamente no código. Qualquer pessoa com acesso ao repositório pode gerar tokens válidos e se passar por qualquer usuário. Isso invalida completamente o mecanismo de autenticação da plataforma. A correção é cirúrgica e de baixo risco — mover o secret para variável de ambiente.

**Grupo 2 — Ambiente reproduzível (INI-03):** hoje cada desenvolvedor monta o ambiente de uma forma diferente. O banco de dados varia, a versão das dependências varia, não há observabilidade local. Esta parte da fase garante que qualquer pessoa sobe o sistema completo com um único comando em menos de 15 minutos — e substitui o SQLite por PostgreSQL, que é o banco adequado para um sistema real com múltiplos usuários simultâneos.

### Sequência interna obrigatória

```
INI-02 (JWT secret + perfis de ambiente)
    ↓ concluída primeiro
INI-03 (Docker Compose + PostgreSQL + Spring Actuator + LGTM Stack)
```

INI-02 precede INI-03 porque o `docker-compose.yml` precisa injetar `JWT_SECRET` via variável de ambiente. Iniciar INI-03 antes significaria documentar a vulnerabilidade dentro da infraestrutura, não resolvê-la.

### O que muda para o negócio

- A vulnerabilidade crítica de autenticação é eliminada — nenhum secret exposto no código
- A plataforma passa a suportar múltiplos usuários simultâneos (PostgreSQL no lugar de SQLite)
- Novos desenvolvedores ficam produtivos no primeiro dia, não na segunda semana
- Um script automatizado valida que o sistema sobe e encerra corretamente a cada build

### Iniciativas desta fase

| Iniciativa | Descrição | Depende de |
|---|---|---|
| **INI-02** | Segurança — JWT secret e perfis de ambiente | INI-01 |
| **INI-03** | Containerização e ambiente local reproduzível | INI-02 |

### O que o time entrega

**INI-02 — Segurança:**

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| JWT secret removido do código | Chave de autenticação lida de variável de ambiente | `truffleHog scan .` → 0 findings |
| Scan automático de secrets no CI | Pipeline falha se alguém commitar um secret | Push com secret simulado → CI falha |
| Perfis de ambiente: `dev`, `staging`, `prod` | Configurações separadas por ambiente | Arquivos `application-{env}.yml` existem |
| `.env.example` no repositório | Documenta variáveis necessárias sem expor valores reais | Arquivo commitado sem valores reais |
| Mensagem de erro clara sem `JWT_SECRET` | App falha de forma descritiva se a variável estiver ausente | `./gradlew bootRun` sem variável → erro claro |

**INI-03 — Ambiente:**

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| `docker compose up` funcional | Um comando sobe: app + PostgreSQL + Prometheus + Loki + Tempo + Grafana | 6 serviços em estado `healthy` |
| PostgreSQL 16 substituindo SQLite | Banco robusto com suporte a concorrência real | `curl http://localhost:8080/tags` retorna JSON |
| Spring Actuator habilitado | `/actuator/health` e `/actuator/metrics` — necessários para Docker Compose e Fase 4 | `curl http://localhost:8080/actuator/health` retorna `UP` |
| Grafana acessível | Stack de observabilidade local disponível | `http://localhost:3000` abre (admin/admin) |
| Script Python de validação | Valida startup e shutdown com logs estruturados a cada build no CI | `python scripts/validate_startup.py` → exit 0 |
| `CONTRIBUTING.md` atualizado | Guia de onboarding — do clone ao sistema rodando | Dev novo sobe em ≤ 15 minutos |

### ✅ Marco M1

> `truffleHog scan .` retorna zero findings. `docker compose up` sobe todos os serviços sem erro. `curl http://localhost:8080/tags` retorna JSON válido. `curl http://localhost:8080/actuator/health` retorna `{"status":"UP"}`. Script Python passa no CI. Dev novo seguiu o `CONTRIBUTING.md` e subiu o ambiente em ≤ 15 minutos — tempo medido e documentado.

---

## Fase 3 — Modernização do stack tecnológico

### O que é, em linguagem de negócio

Esta é a fase de maior transformação técnica do projeto e endereça diretamente **cinco das oito demandas da gestão**. Para stakeholders, o que importa entender é o seguinte: o sistema hoje roda sobre um framework sem suporte de segurança há mais de dois anos. Cada dia que passa sem esse upgrade é um dia a mais de risco acumulado. Esta fase elimina esse risco de forma definitiva e posiciona a plataforma para os próximos anos.

As cinco demandas atendidas nesta fase:

| Demanda | Iniciativa | Por que foi escolhida |
|---|---|---|
| Java 25 | INI-04 | LTS mais recente, suporte até 2030; traz virtual threads estáveis que melhoram performance sem reescrita |
| Spring Boot 4.0.3 ¹ | INI-04 | Versão com suporte ativo; Jakarta EE 11; OpenTelemetry nativo — base para Fase 4 |
| Gradle 9.3.1 | INI-04 | Build mais rápido, sem warnings; compatibilidade nativa com Spring Boot 4 |
| MyBatis → Spring Data JPA | INI-05 | Reduz código manual de banco; habilita `@Where` para soft delete (INI-12) e cache lazy (INI-13) |
| Record types | INI-06 | Elimina boilerplate nos DTOs; imutabilidade por padrão; Java 25 torna records estáveis e expressivos |

### Sequência interna obrigatória

```
INI-04 (Java 25 + Spring Boot 4.0.3 + Gradle 9.3.1)
    ↓ concluída
    ├── INI-05 (MyBatis → Spring Data JPA) ──┐ paralelas entre si
    └── INI-06 (Record types Java 25)      ──┘ após INI-04 concluída
```

**INI-05 e INI-06 não podem iniciar antes de INI-04 estar concluída** porque:
- INI-05 depende do Spring Data JPA disponível no Spring Boot 4
- INI-06 depende dos record types estáveis do Java 25

### ⚠️ Regra crítica: testes em paralelo com INI-04

INI-04 é a mudança de maior risco técnico do projeto. Por isso, **INI-07 e INI-08 iniciam no mesmo momento que INI-04** — não depois.

**Organização de duplas:**

| Dupla | Responsabilidade | Iniciativas |
|---|---|---|
| **Dupla A** | Upgrade de stack | INI-04 → depois INI-05 ou INI-06 |
| **Dupla B** | Rede de segurança de testes | INI-07 + INI-08 em paralelo |

Dupla B precisa de INI-03 concluída (Docker disponível para Testcontainers em INI-08).

### O que muda para o negócio

- A plataforma deixa de rodar sobre software sem suporte de segurança
- Suporte garantido até 2030 sem necessidade de novo upgrade major
- Base de código menor, mais legível e mais fácil de manter
- As funcionalidades de produto da Fase 5 se tornam tecnicamente viáveis

### Iniciativas desta fase

| Iniciativa | Descrição | Depende de | Paralelo com |
|---|---|---|---|
| **INI-04** | Java 25 + Spring Boot 4.0.3 + Gradle 9.3.1 | INI-03 | INI-07, INI-08 ⚠️ |
| **INI-05** | MyBatis → Spring Data JPA + Hibernate | INI-04 | INI-06 |
| **INI-06** | Record types Java 25 | INI-04 | INI-05 |

### O que o time entrega

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| Java 25 em execução | Runtime com suporte Oracle até 2030 | `java -version` no container → Java 25 |
| Spring Boot 4.0.3 | Framework com suporte ativo e patches de segurança | `build.gradle` atualizado |
| Gradle 9.3.1 sem warnings | Build limpo, sem avisos de deprecação | `./gradlew build` → zero `[WARNING]` |
| Virtual threads habilitados | Performance de concorrência melhorada sem reescrita | `spring.threads.virtual.enabled=true` no log de startup |
| Zero imports `javax.*` | Migração completa para Jakarta EE 11 (obrigatório no Spring Boot 4) | `grep -r "import javax\." src/` → 0 resultados |
| Joda-Time removido | Substituído pela API de datas nativa do Java (`java.time`) | `grep "joda" build.gradle` → 0 resultados |
| Zero referências a MyBatis | Camada de banco migrada para Spring Data JPA | `grep "mybatis" build.gradle` → 0 resultados |
| ≥ 80% dos DTOs como record types | Código mais conciso e imutável por padrão | Contagem no pacote `application` |
| GraphQL operacional (DGS 10.x) | DGS atualizado — mesmas anotações, integração interna com Spring for GraphQL | `POST /graphql` com `{"query":"{ tags }"}` retorna resultado |
| Todos os testes passando | Nenhuma regressão introduzida pelo upgrade | `./gradlew test` → 100% verde |

### ✅ Marco M3

> `./gradlew build` passa sem nenhum warning de deprecação. `./gradlew test` 100% verde. Zero `import javax.*`. Zero MyBatis. ≥ 80% dos DTOs são record types. GraphQL funcionando com DGS 10.x. Virtual threads habilitados e confirmados no log de startup.

---

## Fase Testes — Qualidade verificável

### O que é, em linguagem de negócio

Testes não são etapa final — são a rede de segurança que permite ao time entregar com confiança. Sem eles, cada mudança pode silenciosamente quebrar algo que já funcionava. Com eles, o time sabe imediatamente se algo foi afetado.

Esta fase estrutura a estratégia em cinco camadas:

```
Camada 5 — E2E (Playwright)            → Fluxos completos do ponto de vista do consumidor da API
Camada 4 — Integração (Testcontainers) → Cada endpoint REST e operação GraphQL com banco real
Camada 3 — Mutação (Pitest 95%)        → Valida que os testes das camadas abaixo realmente detectam bugs
Camada 2 — Slice (@WebMvcTest etc.)    → Controllers e repositórios em isolamento
Camada 1 — Unit (JUnit 5 + Mockito)   → Lógica de negócio isolada — entregue em cada iniciativa
```

**Camada 1 é entregue por cada iniciativa individualmente.** Esta fase consolida, eleva a cobertura ao threshold de mutação e adiciona as camadas 3, 4 e 5 que dependem do sistema completo rodando.

### Timing desta fase

```
Fase 3 inicia
    ├── Dupla A: INI-04 (upgrade)
    └── Dupla B: INI-07 + INI-08 (testes) ← inicia junto com INI-04

Após INI-08 concluída:
    └── INI-09 (Playwright E2E — fluxos 1, 2, 3)

Após INI-12 concluída (Fase 5):
    └── INI-09 (adicionar fluxos 4 e 5 — soft delete) ← GAP-B corrigido
```

### O que muda para o negócio

- Qualquer mudança futura pode ser entregue com confiança quantificada
- Custo de diagnóstico de regressão: de horas para segundos
- Time consegue trabalhar mais rápido nas fases seguintes

### Iniciativas desta fase

| Iniciativa | Descrição | Depende de | Paralelo com |
|---|---|---|---|
| **INI-07** | Pitest — cobertura por mutação 95% | INI-03 | INI-08 |
| **INI-08** | Testes de integração REST + GraphQL | INI-03 (Docker para Testcontainers) | INI-07 |
| **INI-09** | Playwright E2E — broken + working | INI-08 concluída | — |

### Fluxos Playwright por fase — GAP-B e GAP-C corrigidos

INI-09 tem **5 fluxos no total**, distribuídos em dois momentos:

| Fluxo | Conteúdo | Quando | Depende de |
|---|---|---|---|
| Fluxo 1 | Registro e autenticação completa | Fase Testes | INI-08 |
| Fluxo 2 | Criação e leitura de artigo com `readingTimeMinutes` | Fase Testes | INI-08 |
| Fluxo 3 | Interação social — follow, feed, unfollow | Fase Testes | INI-08 |
| Fluxo 4 | Soft delete de artigo | **Fase 6** | **INI-12** ⚠️ |
| Fluxo 5 | Soft delete de comentário | **Fase 6** | **INI-12** ⚠️ |

> **GAP-B corrigido:** os fluxos 4 e 5 do Playwright têm dependência cross-phase de INI-12 (soft delete, Fase 5). O branch `feat/playwright-working` é criado na Fase Testes com 3 fluxos e expandido para 5 na Fase 6 após INI-12 estar concluída.
>
> **GAP-C corrigido:** o Marco MT exige apenas os **3 fluxos independentes**. O Marco M6 exige os **7 fluxos completos** (3 base + 2 soft delete + 2 adicionais de Fase 6).

### O que o time entrega

**INI-07 — Pitest:**

| Entregável | Verificação |
|---|---|
| Pitest configurado no `build.gradle` | `./gradlew pitest` gera relatório HTML |
| Mutation score ≥ 95% | `build/reports/pitest/index.html` mostra score |
| Threshold no CI — falha se cair | Remover um assert → CI vermelho |
| Relatório publicado no CI | Artefato disponível no histórico do pipeline |

**INI-08 — Testes de integração (19 REST + 18 GraphQL):**

| Domínio | Endpoints REST | Cenários obrigatórios |
|---|---|---|
| Autenticação | `POST /users/login`, `POST /users` | Sucesso; credenciais inválidas; campos faltando |
| Usuário | `GET /user`, `PUT /user` | Sucesso; sem token; e-mail duplicado |
| Perfis | `GET /profiles/:username`, follow, unfollow | Sucesso; usuário inexistente; sem token |
| Artigos | Listar, feed, ver, criar, editar, deletar | Sucesso; sem permissão; slug inexistente; campo faltando |
| Favoritos | Favoritar, desfavoritar | Sucesso; sem token |
| Comentários | Listar, criar, deletar | Sucesso; sem permissão; artigo inexistente |
| Tags | `GET /tags` | Sucesso |

GraphQL — 6 queries + 12 mutations: ao menos 1 cenário de sucesso e 1 de erro por operação. Todos os testes usam **PostgreSQL real via Testcontainers**.

**INI-09 — Playwright E2E:**

| Branch | Fluxos | Status CI | Fase |
|---|---|---|---|
| `feat/playwright-broken` | 5 fluxos escritos, falhando | 🔴 Vermelho | Fase Testes |
| `feat/playwright-working` | Fluxos 1, 2, 3 passando | 🟢 Verde | Fase Testes |
| `feat/playwright-working` | Fluxos 1–5 passando (expandido) | 🟢 Verde | Fase 6 |

### ✅ Marco MT

> `./gradlew pitest` reporta mutation score ≥ 95%. CI falha automaticamente se score cair. `./gradlew test` 100% verde com PostgreSQL real via Testcontainers. 19/19 endpoints REST e 18/18 operações GraphQL com cenários de sucesso e erro cobertos. Branch `feat/playwright-broken` existe com CI vermelho. Branch `feat/playwright-working` com CI verde e **3 fluxos base** passando (fluxos 1, 2, 3 — independentes de INI-12).

---

## Fase 4 — Visibilidade do sistema

### O que é, em linguagem de negócio

Hoje, quando algo falha, o time não tem dados — tem suposições. Esta fase resolve isso em duas frentes:

**Observabilidade (INI-10):** o sistema passa a registrar e expor o que está acontecendo em tempo real. A gestão requisitou três comportamentos específicos:
1. Cada endpoint incrementa um **contador** a cada chamada
2. O sistema emite um **log de startup** estruturado e validável automaticamente
3. O sistema emite um **log de shutdown** estruturado e validável automaticamente

**Documentação da API — setup inicial (INI-11):** os 19 endpoints REST passam a ter documentação formal no Swagger UI. Os **campos novos** (`readingTimeMinutes` e nota de soft delete) serão adicionados à documentação na Fase 6, após INI-12 e INI-13 estarem concluídas.

> **GAP-E corrigido:** INI-11 tem dois momentos distintos:
> - **Fase 4:** setup do OpenAPI com os 19 endpoints atuais documentados (campos e comportamentos existentes)
> - **Fase 6:** update com `readingTimeMinutes` e nota sobre soft delete (após INI-12 e INI-13)
>
> Esta separação resolve a inconsistência de Fase 4 depender de trabalho da Fase 5.

### Sequência interna

```
INI-10 (métricas + logs + traces + Gameday 1 + dashboard Grafana) ──┐ paralelas
INI-11 (OpenAPI setup — 19 endpoints existentes)                  ──┘
```

Ambas dependem de INI-04 concluída. INI-03 já providenciou o LGTM Stack no Docker Compose — INI-10 configura as métricas e dashboards sobre essa infraestrutura.

### O que muda para o negócio

- Tempo de diagnóstico de incidente: de horas para ≤ 15 minutos (verificado no Gameday 1)
- Visibilidade de uso por funcionalidade — dados reais para decisões de produto
- Integrações com outros sistemas passam a ter documentação formal de referência
- Logs de inicialização e encerramento validados automaticamente a cada build

### Iniciativas desta fase

| Iniciativa | Descrição | Depende de | Paralelo com |
|---|---|---|---|
| **INI-10** | Observabilidade LGTM — contadores, logs, traces, Gameday 1 | INI-03, INI-04 | INI-11 |
| **INI-11** | OpenAPI setup — 19 endpoints existentes documentados | INI-04 | INI-10 |

### O que o time entrega

**INI-10 — Observabilidade:**

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| Contador Micrometer em 19/19 endpoints | Cada chamada incrementa `api.requests.total` com tag do endpoint | Chamada → Prometheus mostra métrica incrementada |
| Log de startup estruturado (JSON) | Campos: `event: "application_startup"`, `timestamp`, `version`, `environment` | Script Python valida campos no CI |
| Log de shutdown estruturado (JSON) | Campos: `event: "application_shutdown"`, `timestamp`, `reason` | Script Python valida campos no CI |
| Traces em 100% das requisições | Cada requisição rastreável via OpenTelemetry + Tempo | Chamada → Grafana/Tempo mostra trace |
| Dashboard Grafana | Painéis: chamadas por endpoint, latência p95, taxa de erro | `http://localhost:3000` mostra dados reais |
| **Gameday 1** — diagnóstico baseline | Exercício com a equipe: localizar causa raiz no sistema atual, sem as novas features | Resultado ≤ 15 minutos documentado pelo PM |

**INI-11 — OpenAPI setup inicial:**

| Entregável | O que significa na prática | Verificação |
|---|---|---|
| Swagger UI em `/swagger-ui.html` | Documentação navegável dos 19 endpoints existentes | URL abre; 19 endpoints visíveis |
| Schema OpenAPI em `/v3/api-docs` | Contrato formal em JSON — consumível por ferramentas externas | JSON válido verificável em editor.swagger.io |
| 19/19 endpoints documentados | Request body, response 200, erros para cada endpoint | Contagem no Swagger UI |

> **Nota:** campos `readingTimeMinutes` e comportamento de soft delete serão adicionados ao OpenAPI na Fase 6.

### ✅ Marco M4

> Cada chamada a qualquer endpoint aparece no dashboard Grafana com contador incrementado. Script Python valida logs de startup e shutdown no CI. Swagger UI lista 19 endpoints com request, response e erros documentados (campos dos 19 endpoints existentes). **Gameday 1 realizado** — resultado ≤ 15 minutos documentado pelo PM.

---

## Fase 5 — Novas funcionalidades de produto

### O que é, em linguagem de negócio

Esta fase entrega as **três demandas de produto da gestão** que chegam diretamente ao usuário da plataforma. Elas só são viáveis aqui porque dependem de duas conquistas anteriores:
- **Spring Data JPA (INI-05, Fase 3):** o `@Where` habilita o soft delete; os repositórios JPA habilitam o lazy update
- **Rede de segurança de testes (Fase Testes):** funcionalidades novas precisam da mesma régua de qualidade de 95% de mutação

### Condição de entrada obrigatória

```
Fase 5 só inicia quando M3 + MT estão ambos verificados.
M3 sem MT = base pronta, sem rede de segurança.
MT sem M3 = rede de segurança pronta, base ainda incompatível.
Ambos são necessários.
```

### Funcionalidade 1 — Soft delete: exclusão segura de conteúdo (INI-12)

**O que é para o usuário:** quando um artigo ou comentário é deletado, ele desaparece da plataforma. O comportamento visível é idêntico ao de antes.

**O que muda por dentro:** em vez de remover o registro permanentemente, o sistema marca com `is_deleted = true`. O conteúdo existe no banco, invisível para os usuários, auditável pela operação.

| Antes — hard delete | Depois — soft delete |
|---|---|
| `DELETE FROM articles WHERE slug = ?` | `UPDATE articles SET is_deleted = true WHERE slug = ?` |
| Exclusão irreversível | Exclusão lógica — registro preservado |
| Sem histórico | Auditoria completa disponível |
| Erro de deleção = perda definitiva | Erro de deleção pode ser corrigido |
| Incompatível com LGPD futura | Base para requisitos de retenção de dados |

### Funcionalidade 2 — Tempo estimado de leitura (INI-13)

**O que é para o usuário:** cada artigo passa a exibir o tempo estimado de leitura. O cálculo usa 200 palavras por minuto — referência padrão de mercado.

```
400 palavras  → ceil(400 ÷ 200) = 2 minutos
50 palavras   → mínimo de 1 minuto
2500 palavras → ceil(2500 ÷ 200) = 13 minutos
```

### Funcionalidade 3 — Cache do tempo de leitura com lazy update (INI-13)

**O que é:** o cálculo é feito uma vez e armazenado. Artigos existentes sem o campo recebem o valor na primeira leitura (lazy update) — sem processar toda a base de uma vez.

| Cenário | O que acontece |
|---|---|
| Novo artigo criado | Tempo calculado e salvo no momento da criação |
| Artigo editado (body mudou) | Tempo recalculado e salvo |
| Artigo editado (só título mudou) | Tempo não recalculado |
| Artigo existente sem campo — 1ª leitura | Sistema calcula, persiste e retorna |
| Artigo existente sem campo — 2ª leitura | Valor já no banco — retornado diretamente |

### Iniciativas desta fase

| Iniciativa | Descrição | Depende de | Paralelo com |
|---|---|---|---|
| **INI-12** | Soft delete — artigos e comentários | INI-05, INI-07, INI-08 | INI-13 |
| **INI-13** | Tempo de leitura + cache lazy | INI-05, INI-07, INI-08 | INI-12 |

### O que o time entrega

**INI-12 — Soft delete:**

| Entregável | Verificação |
|---|---|
| Migration Flyway: `is_deleted` em `articles` e `comments` | `\d articles` e `\d comments` mostram coluna |
| `DELETE /articles/:slug` → soft delete, retorna `204 No Content` | Registro existe com `is_deleted=true`; response continua sendo 204 |
| `GET /articles` filtra registros com `is_deleted=true` | Artigo deletado ausente da listagem |
| Comportamento visível ao usuário idêntico ao anterior | Testes de contrato existentes passam sem alteração |
| Pitest ≥ 95% mantido para código novo | `./gradlew pitest` ≥ 95% incluindo código novo |

**INI-13 — Tempo de leitura:**

| Entregável | Verificação |
|---|---|
| Migration Flyway: `reading_time_minutes` em `articles` (nullable) | `\d articles` mostra coluna |
| `POST /articles` retorna `readingTimeMinutes` calculado | Response inclui campo |
| `PUT /articles/:slug` recalcula se body mudou | Editar body → novo valor no response |
| `GET /articles/:slug` faz lazy update | Artigo sem campo → após GET → campo no banco |
| Campo disponível em REST e GraphQL | `article { readingTimeMinutes }` funciona |
| Pitest ≥ 95% mantido para código novo | `./gradlew pitest` ≥ 95% incluindo código novo |

### ✅ Marco M5

> `DELETE /articles/:slug` → artigo não aparece na listagem; existe no banco com `is_deleted = true`; retorna `204 No Content`. `POST /articles` → response inclui `readingTimeMinutes` calculado. Artigo existente sem campo → após `GET /articles/:slug` → campo calculado e salvo no banco. `./gradlew pitest` ≥ 95% para todo o codebase incluindo código novo das duas funcionalidades.

---

## Fase 6 — Validação final e encerramento

### O que é, em linguagem de negócio

Esta fase não adiciona funcionalidades novas. É a vistoria final antes de declarar o projeto concluído: tudo funciona, tudo está testado, tudo está documentado, tudo está rastreável. Inclui dois entregáveis residuais que dependiam de trabalho da Fase 5 para ser completados.

Cada atividade tem uma iniciativa de origem — não é trabalho solto.

### O que o time entrega

| Atividade | Iniciativa de origem | Critério de conclusão |
|---|---|---|
| Regressão completa | Todas | `./gradlew test` verde; `./gradlew pitest` ≥ 95% para todo o codebase |
| **Playwright expandido: fluxos 4 e 5** ⚠️ | INI-09 + INI-12 | Fluxos de soft delete adicionados ao branch `feat/playwright-working` — total: **5 fluxos** (3 base + 2 soft delete) |
| **OpenAPI atualizado com campos novos** ⚠️ | INI-11 + INI-12 + INI-13 | `readingTimeMinutes` e nota de soft delete visíveis no Swagger UI — GAP-E corrigido |
| Schema GraphQL atualizado | INI-13 | `readingTimeMinutes: Int` presente no arquivo `.graphqls` |
| Dashboard Grafana com novas features | INI-10 | Chamadas aos endpoints de delete aparecem nos contadores |
| **Gameday 2** — com novas funcionalidades | INI-10 | Exercício com soft delete e tempo de leitura em operação — ≤ 15 minutos. **Diferente do Gameday 1 (Fase 4):** este valida o sistema com as features completas de produto | 
| Coda 100% completo | INI-01 | 100% das etapas de todas as 13 iniciativas com prompts e skills documentados |
| Backlog retrospectivo | INI-01 | Issues abertas triadas; PRs pendentes resolvidas ou fechadas com justificativa |
| Retrospectiva do projeto | INI-01 | O que funcionou, o que seria feito diferente, o que vai para o backlog — documentado |

> **GAP-G corrigido — dois gamedays explicitados:**
> - **Gameday 1 (Fase 4):** sistema baseline sem novas features — valida que observabilidade funciona e o time consegue diagnosticar problemas em ≤ 15 minutos
> - **Gameday 2 (Fase 6):** sistema completo com soft delete e tempo de leitura — valida que as novas features são observáveis e diagnosticáveis na mesma régua de ≤ 15 minutos

### ✅ Marco M6 — Encerramento do projeto

> `./gradlew pitest` ≥ 95% para todo o codebase incluindo INI-12 e INI-13. Branch `feat/playwright-working` com **5 fluxos** passando e CI verde. Swagger UI com `readingTimeMinutes` e soft delete documentados. Dashboard Grafana mostrando métricas das novas features. **Gameday 2 realizado** com resultado ≤ 15 minutos documentado. Coda com 100% das etapas documentadas. Retrospectiva registrada no repositório.

---

## Timeline visual completa

```
┌──────────────┬────────────────────────────────────────────────────────────────────┐
│    FASE      │  INICIATIVAS                                                        │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 0      │  ██████ INI-01 (Processo)                                           │
│  Processo    │  Marco M0                                                           │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 1      │  ████ INI-02 (JWT + Perfis + scan de secrets)                       │
│  Segurança   │       ██████████████ INI-03 (Docker + PostgreSQL + Actuator + LGTM) │
│  e Ambiente  │  Marco M1                                                           │
│              │  [Não existe Fase 2 — INI-02 e INI-03 são sequenciais na Fase 1]   │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 3      │  ████████████████████ INI-04 (Java 25 + SB 4.0.3 + Gradle 9.3.1)  │
│  Stack       │  ◀── Dupla A                                                        │
│              │                       ████████ INI-05 (JPA) ──┐ paralelas após 04  │
│              │                       ████████ INI-06 (Rec.) ──┘                   │
│              │  Marco M3                                                           │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE        │  ████████████████████ INI-07 (Pitest 95%)    ──┐ paralelas         │
│  TESTES ⚠️   │  ████████████████████ INI-08 (Integração)    ──┘ ◀── Dupla B       │
│              │  [INI-07+08 iniciam junto com INI-04]                               │
│              │                       ████████ INI-09 (Playwright — 3 fluxos base) │
│              │  Marco MT (3 fluxos Playwright)                                     │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 4      │  ██████████ INI-10 (LGTM + contadores + logs + Gameday 1) ──┐      │
│  Visibil.    │  ██████████ INI-11 (OpenAPI setup — 19 endpoints)          ──┘paral│
│              │  Marco M4 (Gameday 1)                                               │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 5      │  ████████████ INI-12 (Soft delete) ──┐ paralelas                  │
│  Produto     │  ████████████ INI-13 (Leitura)      ──┘                            │
│              │  Marco M5                                                           │
├──────────────┼────────────────────────────────────────────────────────────────────┤
│  FASE 6      │  ████████ Playwright +fluxos 4+5 (INI-09+12) ┐                    │
│  Validação   │           OpenAPI campos novos (INI-11+12+13)  ├ residuais Fase 5  │
│              │           Gameday 2 + Regressão + Coda 100%   ┘                    │
│              │  Marco M6 — Encerramento                                           │
└──────────────┴────────────────────────────────────────────────────────────────────┘

⚠️  INI-07 e INI-08: iniciam DURANTE Fase 3, em paralelo com INI-04
    Dupla A = INI-04 | Dupla B = INI-07 + INI-08
    INI-09 fluxos 4+5: dependem de INI-12 (Fase 5) — completados na Fase 6
```

---

## Tabela de marcos consolidada

| Marco | Fase | Critério resumido | Verificado por |
|---|---|---|---|
| **M0** | Fase 0 | CI rejeita commits fora do padrão; templates ativos; Coda iniciado | PM + Time |
| **M1** | Fase 1 | Zero secrets; PostgreSQL ativo; Actuator UP; script Python passando; onboarding ≤ 15 min | PM + Tech Lead |
| **M3** | Fase 3 | Build sem warnings; zero javax; zero MyBatis; ≥ 80% records; GraphQL ok; virtual threads | Tech Lead |
| **MT** | Fase Testes | Pitest ≥ 95%; 19+18 contratos cobertos; Playwright working com **3 fluxos base** | PM + Tech Lead |
| **M4** | Fase 4 | Contadores em 19 endpoints; logs validados; Swagger setup completo; **Gameday 1** ≤ 15 min | PM + Stakeholders |
| **M5** | Fase 5 | Soft delete funcional (204 No Content); tempo de leitura com lazy update; Pitest ≥ 95% mantido | PM + Stakeholders |
| **M6** | Fase 6 | Playwright 5 fluxos; OpenAPI final com campos novos; **Gameday 2** ≤ 15 min; Coda 100%; retrospectiva | PM + Gestão |

---

## Critérios de transição entre fases

| De | Para | Condição obrigatória |
|---|---|---|
| Fase 0 | Fase 1 | M0 verificado e documentado |
| Fase 1 | Fase 3 | M1 verificado — ambiente funcional e secrets eliminados |
| Fase 3 | Fase 4 | **M3 + MT ambos verificados** — stack modernizada E testes com threshold atingido |
| Fase 4 | Fase 5 | M4 verificado — observabilidade ativa antes de adicionar produto |
| Fase 5 | Fase 6 | M5 verificado — funcionalidades entregues e cobertas por testes |
| Fase 6 | Encerramento | M6 verificado — validação completa documentada |

> **Por que M3 + MT são condição conjunta para Fase 5?**
> Fase 5 entrega funcionalidades novas sobre a base modernizada. Sem M3, a base não está pronta. Sem MT, não há rede de segurança para o código novo. Iniciar Fase 5 com apenas um dos dois é construir sobre fundação incompleta.

---

## Regras de trabalho em duplas

| Regra | Como é verificada |
|---|---|
| Issue criada antes de qualquer código | Template de PR exige link para issue — sem link, PR não é aprovada |
| Conventional Commits em 100% dos commits | commitlint no CI rejeita automaticamente |
| Prompts e skills documentados no Coda por etapa | DoD de cada iniciativa inclui item de Coda — verificado antes de fechar |
| GitAhead para validar progresso | Dupla valida histórico visual antes de fechar etapa |
| Commits automáticos para branch `bleeding` | Verificado via GitAhead |
| DoD 100% antes de fechar issue | Checklist completo na issue — PM valida antes de fechar |
| Mudança de escopo passa por `03-initiatives.md` primeiro | PM valida toda mudança antes da implementação |

---

## O que está fora do escopo deste projeto

| O que | Por que não agora | Quando revisitar |
|---|---|---|
| Recuperação de senha via e-mail | Requer serviço de e-mail externo | Após M6 |
| Rate limiting em autenticação | Segurança avançada — importante mas não bloqueadora | Após M6 |
| Interface para recuperar conteúdo deletado | Soft delete cria a base — interface de recuperação é o próximo passo | Após M6 |
| Busca por texto nos artigos | Requer ElasticSearch ou extensão PostgreSQL | Backlog futuro |
| Notificações | Requer infraestrutura adicional | Backlog futuro |
| Roles de moderação e administração | Novo modelo de permissões — escopo significativo | Backlog futuro |
| Deploy em produção | Infraestrutura fora do escopo desta modernização | Após M6 |
| GraalVM Native Image | Complexidade sem benefício direto para este projeto | Backlog futuro |

---

*Documento vivo — revisitar ao encerrar cada fase*  
*Próxima revisão obrigatória: ao atingir M1*  
*7 gaps corrigidos na v5.0 · 8 demandas rastreadas · 13 iniciativas · 7 marcos · 6 fases*  
*Rastreado em: `03-initiatives.md` · `05-backlog.md` · `07-metrics.md` · `08-risks.md`*