# 08 — Risks and Dependencies
> Versão 1.0 · Junho 2026  
> Projeto: RealWorld Platform Modernization  
> Responsável: Product Management

---

## O que é este documento

Este documento consolida **todos os riscos identificados** ao longo dos documentos do projeto (`01-as-is.md`, `03-initiatives.md`, `04-roadmap.md`, `05-backlog.md`, `06-architecture-decisions.md`, `07-metrics.md`) em um único lugar rastreável, com probabilidade, impacto, mitigação e plano de contingência para cada risco.

Também documenta as **dependências externas** — técnicas, de equipe e de processo — que o projeto precisa para avançar.

> **Para vibe coding:** antes de iniciar qualquer iniciativa, leia os riscos da fase correspondente neste documento. Riscos ignorados em projetos de geração assistida por IA tendem a se materializar na forma de código gerado incorretamente ou retrabalho extenso.

---

## Sumário

1. [Matriz de riscos consolidada](#1-matriz-de-riscos-consolidada)
2. [Riscos críticos](#2-riscos-críticos)
3. [Riscos altos](#3-riscos-altos)
4. [Riscos médios](#4-riscos-médios)
5. [Riscos baixos](#5-riscos-baixos)
6. [Riscos de negócio e contrato](#6-riscos-de-negócio-e-contrato)
7. [Riscos de processo e vibe coding](#7-riscos-de-processo-e-vibe-coding)
8. [Dependências externas](#8-dependências-externas)
9. [Dependências de equipe e ambiente](#9-dependências-de-equipe-e-ambiente)
10. [Registro de riscos por fase](#10-registro-de-riscos-por-fase)

---

## Escala de avaliação

| Probabilidade | Critério |
|---|---|
| 🔴 **Alta** | > 60% de chance de ocorrer neste projeto |
| 🟡 **Média** | 30–60% de chance |
| 🟢 **Baixa** | < 30% de chance |

| Impacto | Critério |
|---|---|
| 🔴 **Alto** | Bloqueia um marco ou iniciativa inteira |
| 🟡 **Médio** | Atrasa um marco; requer replanejamento parcial |
| 🟢 **Baixo** | Workaround disponível; não afeta cronograma |

| Severidade | Probabilidade × Impacto |
|---|---|
| 🚨 **Crítico** | Alta × Alto |
| ⚠️ **Alto** | Alta × Médio **ou** Média × Alto |
| 📋 **Médio** | Média × Médio **ou** Alta × Baixo |
| ℹ️ **Baixo** | Baixa × qualquer |

---

## 1. Matriz de riscos consolidada

| ID | Risco | Probabilidade | Impacto | Severidade | Fase | Marco afetado |
|---|---|---|---|---|---|---|
| [R-01](#r-01) | DGS Framework incompatível com Spring Boot 4 | 🔴 Alta | 🔴 Alto | 🚨 Crítico | Fase 3 | M3 |
| [R-02](#r-02) | Spring Security 6.x — breaking changes subestimados | 🔴 Alta | 🔴 Alto | 🚨 Crítico | Fase 3 | M3 |
| [R-03](#r-03) | Pitest abaixo de 95% exige mais testes que estimado | 🔴 Alta | 🟡 Médio | ⚠️ Alto | Fase Testes | MT, Fase 5 |
| [R-04](#r-04) | Java 25 indisponível no CI — fallback necessário | 🟡 Média | 🔴 Alto | ⚠️ Alto | Fase 3 | M3 |
| [R-05](#r-05) | Testes de integração revelam regressões do upgrade | 🔴 Alta | 🟡 Médio | ⚠️ Alto | Fase 3 | M3 |
| [R-06](#r-06) | Joda-Time em `ArticleData` bloqueia conversão para record | 🔴 Alta | 🟡 Médio | ⚠️ Alto | Fase 3 | M3 |
| [R-07](#r-07) | Dependências transitivas incompatíveis com novo stack | 🟡 Média | 🔴 Alto | ⚠️ Alto | Fase 3 | M3 |
| [R-08](#r-08) | Testcontainers indisponível ou mal configurado no CI | 🟡 Média | 🔴 Alto | ⚠️ Alto | Fase Testes | MT |
| [R-09](#r-09) | Queries MyBatis complexas sem equivalente direto em JPQL | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 3 | M3 |
| [R-10](#r-10) | `ApiMetricsAspect` não captura todos os endpoints | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 4 | M4 |
| [R-11](#r-11) | Script Python com comportamento diferente por sistema operacional | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 1 | M1 |
| [R-12](#r-12) | Playwright com comportamento inesperado em requisições GraphQL | 🟡 Média | 🟡 Médio | 📋 Médio | Fase Testes | MT |
| [R-13](#r-13) | `JWT_SESSION_TIME` ausente causa falha de startup inesperada | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 1 | M1 |
| [R-14](#r-14) | Versões do LGTM Stack incompatíveis entre si no Docker Compose | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 1/4 | M1, M4 |
| [R-15](#r-15) | Edição manual do pacote `io.spring.graphql` sobrescrita pelo build | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 3 | M3 |
| [R-16](#r-16) | Artigos nunca lidos permanecem com `reading_time_minutes NULL` para sempre | 🟡 Média | 🟢 Baixo | 📋 Médio | Fase 5 | M5 |
| [R-17](#r-17) | Vibe coding gera código sem leitura dos ADRs | 🔴 Alta | 🟡 Médio | ⚠️ Alto | Todas | Todos |
| [R-18](#r-18) | Issues não criadas antes de codar — rastreabilidade perdida | 🟡 Média | 🟡 Médio | 📋 Médio | Todas | Todos |
| [R-19](#r-19) | Documentação Coda abandonada após primeiras fases | 🔴 Alta | 🟢 Baixo | 📋 Médio | Todas | M6 |
| [R-20](#r-20) | Gameday 1 ou 2 com resultado > 15 minutos na primeira tentativa | 🟡 Média | 🟡 Médio | 📋 Médio | Fase 4/6 | M4, M6 |
| [R-21](#r-21) | Contrato HTTP do DELETE muda para não-204 após soft delete | 🟢 Baixa | 🔴 Alto | ⚠️ Alto | Fase 5 | M5 |
| [R-22](#r-22) | Banco SQLite ainda em uso após Fase 1 em algum perfil | 🟢 Baixa | 🔴 Alto | ⚠️ Alto | Fase 1 | M1 |
| [R-23](#r-23) | Java 25 perde status de LTS antes do encerramento | 🟢 Baixa | 🟡 Médio | ℹ️ Baixo | Fase 3 | M3 |
| [R-24](#r-24) | Pitest muito lento — impacta CI significativamente | 🟡 Média | 🟢 Baixo | ℹ️ Baixo | Fase Testes | MT |

---

## 2. Riscos críticos

### R-01 — DGS Framework incompatível com Spring Boot 4 {#r-01}

**Severidade:** 🚨 Crítico · **Probabilidade:** 🔴 Alta · **Impacto:** 🔴 Alto

**Descrição**

O projeto usa o Netflix DGS Framework 4.9.21. A versão 10.x do DGS foi redesenhada para usar o Spring for GraphQL internamente, mas a compatibilidade exata com Spring Boot 4.0.3 precisa ser verificada no Maven Central antes de iniciar INI-04. Se houver incompatibilidade na versão disponível, INI-04 fica bloqueado até a decisão sobre reescrita de resolvers (ver ADR-001).

**Fase afetada:** Fase 3 (INI-04 / EPIC-04)  
**Marco afetado:** M3  
**Documentos relacionados:** `03-initiatives.md` INI-04; `06-architecture-decisions.md` ADR-001

**Mitigação**

Verificar disponibilidade e compatibilidade do DGS 10.x com Spring Boot 4.0.3 no Maven Central **antes de iniciar qualquer código de INI-04**. Esta verificação é pré-condição do DoR de EPIC-04 (US-04.01).

```bash
# Verificar versão disponível antes de iniciar
# Buscar: https://search.maven.org/artifact/com.netflix.graphql.dgs/graphql-dgs-spring-boot-starter
```

**Plano de contingência**

Se DGS 10.x não for compatível com Spring Boot 4.0.3:

| Opção | Esforço | Recomendação |
|---|---|---|
| Aguardar patch do DGS compatível | Baixo | Verificar roadmap DGS; pode atrasar Fase 3 |
| Migrar para Spring for GraphQL nativo | Alto | Reescreve todos os resolvers; adiciona 2–4 semanas |
| Usar Spring Boot 4.0.x mais antigo compatível com DGS 10.x | Baixo | Compromise aceitável se diferença for apenas patch version |

Decisão deve ser registrada como atualização do ADR-001 antes de qualquer implementação.

---

### R-02 — Spring Security 6.x: breaking changes subestimados {#r-02}

**Severidade:** 🚨 Crítico · **Probabilidade:** 🔴 Alta · **Impacto:** 🔴 Alto

**Descrição**

O Spring Boot 4.x usa Spring Security 6.x, que removeu `WebSecurityConfigurerAdapter` completamente. O projeto atual tem toda a configuração de segurança baseada nessa classe. Além disso, o filtro JWT customizado precisa ser reescrito para a nova API de `SecurityFilterChain`. O escopo real de mudança pode ser maior do que estimado se outros padrões de segurança legados existirem.

**Fase afetada:** Fase 3 (INI-04 / EPIC-04, US-04.05)  
**Marco afetado:** M3  
**Documentos relacionados:** `03-initiatives.md` INI-04; `05-backlog.md` EPIC-04

**Mitigação**

- Executar INI-04 em branch isolado — nunca na main enquanto a segurança está quebrada
- INI-07 e INI-08 devem estar em andamento em paralelo como rede de segurança
- Mapear todos os pontos de configuração de segurança antes de iniciar (auditoria prévia)
- Reservar tempo adicional na estimativa de US-04.05

**Plano de contingência**

Se a reconfiguração de Spring Security se revelar mais complexa que estimado, dividir US-04.05 em múltiplas histórias mais granulares antes de bloquear o merge na main.

---

## 3. Riscos altos

### R-03 — Pitest abaixo de 95%: mais testes que estimado {#r-03}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🔴 Alta · **Impacto:** 🟡 Médio

**Descrição**

O estado atual do projeto tem cobertura de mutação de 0% (Pitest não configurado). Atingir 95% de mutation score a partir do zero exige escrever testes de qualidade, não apenas de quantidade. É provável que o primeiro relatório Pitest mostre score abaixo de 95%, exigindo ciclos adicionais de escrita de testes. Se isso acontecer na Fase 3, atrasa a transição para a Fase 5 (que requer MT verificado).

**Fase afetada:** Fase Testes (INI-07 / EPIC-07)  
**Marco afetado:** MT (atraso), Fase 5 (bloqueio)  
**Métrica relacionada:** M-QUAL-01

**Mitigação**

- Iniciar INI-07 **em paralelo com INI-04** — não aguardar M3 para começar
- Executar `./gradlew pitest` localmente durante o desenvolvimento — não apenas no CI
- Priorizar camadas com mais lógica: `core` e `application` têm maior retorno por teste escrito
- Usar o relatório Pitest para identificar mutantes que sobrevivem e guiar a escrita de testes

**Plano de contingência**

Se o score estiver persistentemente abaixo de 95% após múltiplos ciclos:
1. Revisar se o threshold está excluindo código gerado (`io.spring.graphql.*`)
2. Avaliar se há código morto que pode ser removido
3. Aceitar temporariamente threshold menor (ex: 90%) documentando como débito técnico
4. Elevar para 95% antes de declarar M6

---

### R-04 — Java 25 indisponível no CI — fallback necessário {#r-04}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🟡 Média · **Impacto:** 🔴 Alto

**Descrição**

Java 25 é LTS (GA setembro de 2025). Porém, dependendo da infraestrutura de CI (GitHub Actions, Bitbucket Pipelines, etc.), o runner pode não ter Java 25 disponível como versão pré-instalada ou a action/plugin de setup pode não suportá-la ainda.

**Fase afetada:** Fase 3 (INI-04 / EPIC-04)  
**Marco afetado:** M3  

**Mitigação**

Verificar disponibilidade de Java 25 no CI **antes de iniciar INI-04**:
```yaml
# GitHub Actions — verificar se Java 25 está disponível
- uses: actions/setup-java@v4
  with:
    java-version: '25'
    distribution: 'temurin'
```
Esta verificação é item do DoR de EPIC-04.

**Plano de contingência**

Se Java 25 estiver indisponível no CI:
- **Fallback primário:** Java 21 LTS (suporte Oracle até setembro de 2026)
- Java 21 é compatível com Spring Boot 4 e entrega a maioria dos benefícios
- Documentar a decisão de fallback como atualização do ADR-001
- Retornar para Java 25 quando o CI suportar, sem breaking changes

---

### R-05 — Testes de integração revelam regressões do upgrade {#r-05}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🔴 Alta · **Impacto:** 🟡 Médio

**Descrição**

O upgrade de Spring Boot 2.6.3 → 4.0.3 envolve múltiplas breaking changes: `javax.*` → `jakarta.*`, Spring Security 6.x, DGS 10.x, Hibernate 6.x. É muito provável que os testes de integração (INI-08) identifiquem comportamentos diferentes após o upgrade — isso é esperado e desejado, mas representa ciclos de correção adicionais antes de M3.

**Fase afetada:** Fases 3 e Testes  
**Marco afetado:** M3 (atraso potencial)  

**Mitigação**

- Aceitar que regressões serão encontradas — são sinal de que os testes estão funcionando
- INI-08 deve iniciar **junto com INI-04** para ter feedback contínuo
- Manter uma lista de regressões conhecidas no repositório durante a Fase 3
- Cada regressão identificada deve virar uma issue antes de ser corrigida

**Plano de contingência**

Se o volume de regressões for alto o suficiente para ameaçar M3:
1. Priorizar as regressões por severidade (bloqueiam endpoints críticos vs comportamento marginal)
2. Separar o upgrade em commits menores e verificáveis
3. Considerar feature flags para migração gradual em casos extremos

---

### R-06 — Joda-Time em `ArticleData` bloqueia conversão para record {#r-06}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🔴 Alta · **Impacto:** 🟡 Médio

**Descrição**

`ArticleData.java` (confirmado no repositório) usa `org.joda.time.DateTime` nos campos `createdAt` e `updatedAt`. A interface `Node` exige `DateTimeCursor(updatedAt)`. Antes de converter `ArticleData` para record (INI-06), a migração de Joda-Time para `java.time.Instant` precisa estar concluída em INI-04. Se a ordem for invertida, INI-06 não pode prosseguir.

**Fase afetada:** Fase 3 (INI-04 → INI-06)  
**Marco afetado:** M3  
**Documentos relacionados:** `06-architecture-decisions.md` ADR-005

**Mitigação**

- A dependência INI-04 → INI-06 está explícita no DoR de EPIC-06
- US-04.07 (remover Joda-Time) é pré-condição de US-06.02 (converter `ArticleData` para record)
- Não iniciar EPIC-06 antes de US-04.07 estar concluída e os testes passando

**Plano de contingência**

Se a migração de Joda-Time revelar inconsistências no comportamento de datas (timezone, formatação):
- Testar serialização JSON de `createdAt` e `updatedAt` antes e depois da migração
- Garantir que o cursor de paginação `DateTimeCursor` mantém o mesmo comportamento com `Instant`

---

### R-07 — Dependências transitivas incompatíveis com novo stack {#r-07}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🟡 Média · **Impacto:** 🔴 Alto

**Descrição**

O upgrade de Spring Boot 2.6.3 → 4.0.3 atualiza dezenas de dependências transitivas automaticamente. Algumas delas podem ter breaking changes próprias que não são óbvias na migração principal. Exemplos: versões de Jackson, Hibernate, Flyway, jjwt.

**Fase afetada:** Fase 3 (INI-04)  
**Marco afetado:** M3  

**Mitigação**

- Executar `./gradlew dependencies` antes de iniciar e revisar as principais dependências
- Verificar especificamente: Jackson 3.x (mudanças de serialização), Hibernate 6.x (mudanças de mapeamento), Flyway 10.x (mudanças de migração), jjwt 0.12.x (mudanças de API)
- Manter um arquivo `DEPENDENCY_CHANGES.md` registrando cada breaking change encontrada

**Plano de contingência**

Para cada dependência transitiva com breaking change:
1. Criar issue separada com o comportamento antes/depois
2. Verificar se existe workaround na versão nova
3. Avaliar se é possível fixar na versão anterior temporariamente enquanto resolve

---

### R-08 — Testcontainers indisponível ou mal configurado no CI {#r-08}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🟡 Média · **Impacto:** 🔴 Alto

**Descrição**

Os testes de integração (INI-08) usam Testcontainers para subir PostgreSQL 16 real durante os testes. Isso requer que o ambiente de CI tenha Docker disponível e com permissões para subir containers. Ambientes de CI sem Docker-in-Docker ou sem DinD configurado falharão nos testes.

**Fase afetada:** Fase Testes (INI-08 / EPIC-08)  
**Marco afetado:** MT  

**Mitigação**

- Verificar suporte a Docker no CI escolhido **antes de iniciar INI-08**
- Para GitHub Actions: usar `ubuntu-latest` que tem Docker disponível por padrão
- Testar a configuração de Testcontainers localmente antes de commitar

```yaml
# GitHub Actions — Docker disponível no ubuntu-latest
jobs:
  test:
    runs-on: ubuntu-latest  # Docker disponível
```

**Plano de contingência**

Se Docker não estiver disponível no CI:
- Configurar Docker-in-Docker no runner
- Considerar serviço externo de CI com Docker nativo (GitHub Actions ubuntu, CircleCI)
- Como último recurso: usar Testcontainers Cloud (pago) que não requer Docker local

---

### R-17 — Vibe coding gera código sem leitura dos ADRs {#r-17}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🔴 Alta · **Impacto:** 🟡 Médio

**Descrição**

Em desenvolvimento assistido por IA, a velocidade de geração de código é alta. O risco específico deste projeto é que a IA não tem acesso aos ADRs (`06-architecture-decisions.md`) por padrão — se o desenvolvedor não fornecer o contexto correto no prompt, o código gerado pode violar decisões arquiteturais já tomadas. Exemplos: gerar contadores Micrometer diretamente nos controllers (violando ADR-003), editar `io.spring.graphql` manualmente (violando ADR-004), converter entidades JPA para records (violando ADR-005).

**Fase afetada:** Todas as fases  
**Marco afetado:** Todos os marcos  
**Documentos relacionados:** `06-architecture-decisions.md`

**Mitigação**

- **Regra obrigatória de vibe coding:** antes de gerar código para qualquer épico, ler o ADR correspondente e incluir o contexto relevante no prompt
- DoD de cada história inclui: "código gerado está alinhado com os ADRs referenciados no épico"
- Revisão de PR inclui verificação dos ADRs correspondentes

**Plano de contingência**

Se código gerado violar um ADR:
1. Identificar qual ADR foi violado
2. Criar issue descrevendo a violação
3. Corrigir antes de mergear — nunca mergear violação de ADR conhecida
4. Documentar no Coda o que aconteceu e o prompt corrigido

---

### R-21 — Contrato HTTP do DELETE muda para não-204 após soft delete {#r-21}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🟢 Baixa · **Impacto:** 🔴 Alto

**Descrição**

A RealWorld spec define `DELETE /articles/:slug` como `204 No Content`. Se a implementação do soft delete (INI-12) retornar `200 OK` com um body (como `{ "success": true }`) em vez de `204`, qualquer cliente que espera `204` quebrará. Este risco é baixo em probabilidade porque está explicitamente documentado como regra de negócio, mas o impacto de uma mudança acidental é alto.

**Fase afetada:** Fase 5 (INI-12 / EPIC-12)  
**Marco afetado:** M5  
**Métrica relacionada:** M-PROD-04

**Mitigação**

- US-12.06 e US-12.07 são histórias específicas para garantir que o `204` permanece
- Testes de contrato (INI-08) verificam o status code a cada build
- DoD de EPIC-12 inclui verificação explícita: `curl -X DELETE ... -w "%{http_code}"` → `204`

**Plano de contingência**

Se o soft delete alterar inadvertidamente o status code:
1. Os testes de contrato detectam imediatamente
2. Corrigir o service para não alterar o response do controller
3. Verificar que `@Where` no JPA não interfere no ciclo de vida da resposta HTTP

---

### R-22 — SQLite ainda em uso após Fase 1 em algum perfil {#r-22}

**Severidade:** ⚠️ Alto · **Probabilidade:** 🟢 Baixa · **Impacto:** 🔴 Alto

**Descrição**

Se um perfil de ambiente (ex: `dev` local sem Docker) ainda usar SQLite após INI-03, os testes de integração podem passar localmente mas falhar em CI (que usa PostgreSQL). Isso cria inconsistência e falsos negativos.

**Fase afetada:** Fase 1 (INI-03 / EPIC-03)  
**Marco afetado:** M1  

**Mitigação**

- Critério de aceitação de EPIC-03: `grep -r "sqlite" src/main/resources/` → 0 resultados
- `sqlite-jdbc` removido do `build.gradle` após INI-03
- Único banco configurado em todos os perfis: PostgreSQL via Docker

---

## 4. Riscos médios

### R-09 — Queries MyBatis complexas sem equivalente direto em JPQL {#r-09}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O `ArticleMapper.xml` contém queries SQL que podem ser complexas (joins com follows, favorites, tags). Algumas queries podem não ter equivalente direto em JPQL e exigir SQL nativo ou Specifications — aumentando o esforço de INI-05.

**Fase afetada:** Fase 3 (INI-05 / EPIC-05)  
**Marco afetado:** M3  
**Documentos relacionados:** `06-architecture-decisions.md` ADR-002

**Mitigação**

- Mapear todas as queries MyBatis **antes de iniciar INI-05** (pré-condição do DoR de EPIC-05)
- Estratégia de queries definida em ADR-002: derivação → JPQL → Specifications → SQL nativo
- Reservar capacidade extra para as queries de filtros combinados (`articles` com múltiplos filtros opcionais)

**Plano de contingência**

Para queries que não mapeiam bem para JPQL: usar `@Query(nativeQuery = true)` com SQL PostgreSQL direto, documentando a decisão como comentário no código.

---

### R-10 — `ApiMetricsAspect` não captura todos os endpoints {#r-10}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O aspecto AOP para métricas (ADR-003) usa `execution(* io.spring.api.*.*(..))`. Se algum controller estiver em subpacote diferente ou se novos controllers forem adicionados fora de `io.spring.api`, o aspecto não os captura. A métrica M-OBS-01 (19/19 endpoints com contador) pode passar no CI mesmo com endpoints não cobertos.

**Fase afetada:** Fase 4 (INI-10 / EPIC-10)  
**Marco afetado:** M4  
**Documentos relacionados:** `06-architecture-decisions.md` ADR-003; `07-metrics.md` M-OBS-01

**Mitigação**

- Verificar M-OBS-01 explicitamente: chamar cada um dos 19 endpoints e confirmar incremento no Prometheus
- Adicionar teste de aspecto que valida que o advice é aplicado para cada controller do `api` package
- O aspecto deve usar anotação Spring MVC (`@RequestMapping`) para resolver o nome do endpoint — não pattern fixo

**Plano de contingência**

Se endpoints forem descobertos sem cobertura durante o Gameday 1:
1. Expandir o pointcut do aspecto para cobrir o novo padrão
2. Verificar novamente todos os 19 endpoints após a correção

---

### R-11 — Script Python com comportamento diferente por sistema operacional {#r-11}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O `scripts/validate_startup.py` usa subprocess para controlar o Docker Compose e lê logs de container. Em Windows, comandos de processo e parsing de logs podem se comportar diferente de Mac/Linux. Se desenvolvedores usam Windows, o script pode falhar localmente mesmo que o CI (Linux) passe.

**Fase afetada:** Fase 1 (INI-03 / EPIC-03)  
**Marco afetado:** M1  

**Mitigação**

- Testar o script em Mac e Linux antes de commitar
- Documentar no `CONTRIBUTING.md` os pré-requisitos do Python (versão, libs)
- Usar `subprocess.run` com `shell=False` para maior portabilidade

**Plano de contingência**

Se Windows for um ambiente necessário para algum membro do time:
- Usar WSL2 (Windows Subsystem for Linux) como ambiente de desenvolvimento
- Documentar configuração WSL2 no `CONTRIBUTING.md`

---

### R-12 — Playwright com comportamento inesperado em requisições GraphQL {#r-12}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

Os fluxos E2E do Playwright (INI-09) usam `APIRequestContext` para chamadas HTTP. Requisições GraphQL precisam de `Content-Type: application/json` e body com `{ "query": "..." }`. Se o `APIRequestContext` tiver comportamento diferente do esperado para GraphQL (ex: serialização do body), os testes podem falhar por razões não relacionadas ao produto.

**Fase afetada:** Fase Testes (INI-09 / EPIC-09)  
**Marco afetado:** MT  

**Mitigação**

- Criar um teste simples de GraphQL (`query { tags }`) como smoke test antes de implementar os fluxos completos
- Verificar o `Content-Type` correto para GraphQL no Playwright:

```javascript
// Requisição GraphQL via Playwright APIRequestContext
const response = await request.post('/graphql', {
    headers: { 'Content-Type': 'application/json' },
    data: JSON.stringify({ query: '{ tags }' })
});
```

**Plano de contingência**

Se Playwright apresentar problemas persistentes com GraphQL:
- Usar REST Assured via Node.js para os fluxos que cobrem operações GraphQL
- Os fluxos REST-only (que são a maioria) continuam com Playwright

---

### R-13 — `JWT_SESSION_TIME` ausente causa falha de startup inesperada {#r-13}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

Se `application.properties` for atualizado para `${JWT_SESSION_TIME}` **sem fallback** (em vez de `${JWT_SESSION_TIME:86400}`), e o desenvolvedor não definir a variável no ambiente local, a aplicação falhará com `IllegalArgumentException: Could not resolve placeholder 'jwt.sessionTime'`. O erro é confuso porque a mensagem aponta para `jwt.sessionTime`, não para `JWT_SESSION_TIME`.

**Fase afetada:** Fase 1 (INI-02 / EPIC-02)  
**Marco afetado:** M1  
**Documentos relacionados:** `06-architecture-decisions.md` ADR-006

**Mitigação**

- ADR-006 define explicitamente: `jwt.sessionTime=${JWT_SESSION_TIME:86400}` com fallback
- US-02.02 deve implementar a configuração exata do ADR-006
- Teste de EPIC-02: iniciar sem `JWT_SESSION_TIME` → aplicação sobe com sessão de 86400s

---

### R-14 — Versões do LGTM Stack incompatíveis entre si {#r-14}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O Docker Compose do LGTM Stack (Loki + Grafana + Tempo + Prometheus) envolve 4 projetos com versões independentes. Algumas combinações de versões podem ser incompatíveis (ex: versão do Loki incompatível com o datasource do Grafana, ou formato de trace do Tempo incompatível com OpenTelemetry da versão usada).

**Fase afetada:** Fase 1 (INI-03) e Fase 4 (INI-10)  
**Marco afetado:** M1, M4  

**Mitigação**

- Fixar versões específicas de cada componente no `docker-compose.yml` — nunca usar `latest`
- Testar toda a stack localmente antes de commitar
- Usar configurações de referência da documentação oficial do Grafana Stack

**Versões sugeridas (verificar compatibilidade antes de adotar):**
```yaml
grafana/loki:3.x
grafana/grafana:11.x
grafana/tempo:2.x
prom/prometheus:2.x
```

---

### R-15 — Edição manual do pacote `io.spring.graphql` sobrescrita {#r-15}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O pacote `io.spring.graphql` é gerado automaticamente pelo DGS Codegen a cada build. Se um desenvolvedor — especialmente em vibe coding, onde código é gerado rapidamente — editar arquivos deste pacote manualmente, as alterações serão silenciosamente sobrescritas no próximo `./gradlew build`.

**Fase afetada:** Fase 3 e posteriores  
**Marco afetado:** M3  
**Documentos relacionados:** `06-architecture-decisions.md` ADR-004

**Mitigação**

- ADR-004 documenta isso explicitamente — leitura obrigatória no DoR de EPIC-04
- Considerar adicionar ao `.gitignore`:
```
  src/main/java/io/spring/graphql/
```
- Adicionar comentário no topo de um arquivo do pacote gerado:
```java
  // GENERATED CODE — DO NOT EDIT MANUALLY
  // Run ./gradlew generateJava to regenerate
```

---

### R-16 — Artigos nunca lidos permanecem com `reading_time_minutes NULL` {#r-16}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟢 Baixo

**Descrição**

A estratégia de lazy update (INI-13) calcula `reading_time_minutes` na primeira leitura via `GET /articles/:slug`. Artigos que existiam antes de INI-13 e que nunca forem lidos após a Fase 5 permanecerão com o campo `NULL` para sempre. A métrica M-PROD-07 monitora isso, mas a resolução completa depende de tráfego.

**Fase afetada:** Fase 5 (INI-13 / EPIC-13)  
**Marco afetado:** M5, M6  
**Métrica relacionada:** M-PROD-07

**Mitigação**

- O escopo do projeto aceita `NULL` para artigos nunca relidos — é comportamento esperado
- A listagem `GET /articles` retorna `null` para artigos sem campo — documentado no API Mapping
- Monitorar via M-PROD-07 após M5

**Plano de contingência (pós-projeto)**

Se a quantidade de artigos com `NULL` for inaceitável no futuro:
- Script de backfill: calcular `reading_time_minutes` para todos os artigos com `NULL` em um job agendado
- Este script é um job de manutenção, não parte do escopo desta modernização

---

### R-18 — Issues não criadas antes de codar {#r-18}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

Em vibe coding, a velocidade de geração de código pode levar desenvolvedores a iniciar implementações sem criar issues primeiro. Isso quebra a rastreabilidade do projeto — código sem issue não tem contexto de por que foi escrito, o que foi decidido ou quais ADRs foram consultados.

**Fase afetada:** Todas  
**Marco afetado:** Todos  

**Mitigação**

- Template de PR exige link para issue obrigatoriamente
- DoD de todo épico: "issue existe e está vinculada à PR"
- PM não aprova PRs sem issue — regra não negociável

---

### R-19 — Documentação Coda abandonada após primeiras fases {#r-19}

**Severidade:** 📋 Médio · **Probabilidade:** 🔴 Alta · **Impacto:** 🟢 Baixo

**Descrição**

A documentação de prompts e skills no Coda é alta friction — requer disciplina para manter atualizada em todas as etapas. Em projetos com pressão de prazo, esta é frequentemente a primeira atividade a ser cortada. Se abandonada nas fases finais, o M6 (Coda 100%) não é atingido.

**Fase afetada:** Todas, mais crítico nas Fases 4–6  
**Marco afetado:** M6  
**Métrica relacionada:** M-GOV-05

**Mitigação**

- Coda incluído no DoD de **cada história** — não só do épico
- Retrospectiva ao final de cada fase inclui revisão do Coda
- PM verifica amostra de entradas do Coda a cada marco

---

### R-20 — Gameday com resultado acima de 15 minutos {#r-20}

**Severidade:** 📋 Médio · **Probabilidade:** 🟡 Média · **Impacto:** 🟡 Médio

**Descrição**

O Gameday 1 (M4) e o Gameday 2 (M6) têm meta de ≤ 15 minutos para localizar a causa raiz de um problema simulado. Se a equipe não estiver familiarizada com o Grafana/Loki/Tempo, ou se o problema simulado for muito complexo, o tempo pode exceder 15 minutos na primeira tentativa.

**Fase afetada:** Fase 4 (Gameday 1), Fase 6 (Gameday 2)  
**Marco afetado:** M4, M6  
**Métrica relacionada:** M-OBS-05, M-OBS-06

**Mitigação**

- Realizar sessões de familiarização com o Grafana antes dos Gamedays (não formais — exploração durante a Fase 4)
- Definir o problema simulado com antecedência junto ao tech lead (mas não revelar ao time)
- Começar com problema simples (ex: endpoint retornando 500) antes de complexos

**Plano de contingência**

Se o primeiro Gameday exceder 15 minutos:
1. Documentar o resultado mesmo assim
2. Identificar o gargalo (ex: "não sabíamos navegar no Tempo")
3. Realizar sessão de aprendizado focada no gargalo
4. Realizar Gameday complementar antes de declarar M4 atingido

---

## 5. Riscos baixos

### R-23 — Java 25 perde status de LTS antes do encerramento {#r-23}

**Severidade:** ℹ️ Baixo · **Probabilidade:** 🟢 Baixa · **Impacto:** 🟡 Médio

**Descrição**

Java 25 é LTS com suporte gratuito Oracle até setembro de 2030. É improvável que percamos suporte LTS durante o projeto, mas vale registrar como risco para projetos de longa duração.

**Mitigação:** nenhuma necessária atualmente. Monitorar anualmente.

---

### R-24 — Pitest muito lento — impacta CI {#r-24}

**Severidade:** ℹ️ Baixo · **Probabilidade:** 🟡 Média · **Impacto:** 🟢 Baixo

**Descrição**

Pitest com mutadores `STRONGER` pode ser significativamente mais lento que os testes normais, especialmente em codebases maiores. Isso pode tornar o CI lento se Pitest rodar em cada push.

**Mitigação**

- Configurar Pitest com `threads = 4` para paralelismo
- Considerar rodar Pitest apenas em merges para main, não em feature branches
- Cache de resultados Pitest para mutantes que não mudaram

---

## 6. Riscos de negócio e contrato

### R-CONT-01 — Mudança de comportamento visível após soft delete

**Descrição:** qualquer mudança no comportamento visível ao usuário (ex: artigo deletado ainda aparece por um instante) constitui regressão de produto.

**Mitigação:** testes de contrato (INI-08) verificam o comportamento a cada build. O threshold de Pitest garante que as regras de filtro (`@Where`) são testadas adequadamente.

---

### R-CONT-02 — `readingTimeMinutes` com valor incorreto

**Descrição:** se o cálculo de tempo de leitura retornar `0` (violando a regra de mínimo de 1 minuto) ou um valor muito diferente do esperado, a feature tem impacto negativo na UX.

**Mitigação:** M-PROD-08 garante que nenhum artigo tem `readingTimeMinutes < 1`. Pitest valida a lógica de cálculo com mutações em operadores aritméticos (`CEIL`, `MAX`).

---

### R-CONT-03 — GraphQL e REST retornam dados diferentes para o mesmo recurso

**Descrição:** após as novas features (leitura, soft delete), REST e GraphQL precisam retornar os mesmos dados. Se `readingTimeMinutes` aparecer no REST mas não no GraphQL (ou vice-versa), o contrato fica inconsistente.

**Mitigação:** EPIC-13 inclui US-13.06 (REST) e US-13.07 (GraphQL) como histórias separadas. Testes de contrato validam ambas as APIs com os mesmos dados. US-11.09 atualiza o schema `.graphqls` junto com a implementação.

---

## 7. Riscos de processo e vibe coding

### R-PROC-01 — Scope creep durante vibe coding

**Descrição:** a facilidade de gerar código pode levar a implementar funcionalidades não planejadas ou expandir o escopo de histórias sem atualizar os documentos.

**Mitigação:** toda mudança de escopo passa por `03-initiatives.md` primeiro. DoD inclui verificação de que o escopo implementado corresponde ao épico documentado. PM valida antes de fechar.

---

### R-PROC-02 — Código gerado por IA com padrões inconsistentes

**Descrição:** diferentes sessões de vibe coding podem gerar código com estilos diferentes (nomes de variáveis, estrutura de classes, tratamento de erros), tornando o codebase inconsistente.

**Mitigação:** Spotless + Google Java Format (já configurado) padroniza formatação. Conventional Commits padroniza mensagens. Revisão de PR verifica consistência de estilo. ADRs definem os padrões arquiteturais que o código gerado deve seguir.

---

### R-PROC-03 — Harness development sem commits automáticos

**Descrição:** o branch `bleeding` deve receber commits automáticos durante o harness development. Se o processo de commits automáticos não estiver configurado corretamente, o histórico de progresso não é rastreável via GitAhead.

**Mitigação:** US-01.06 configura o branch `bleeding` como parte da Fase 0. Verificado antes de iniciar qualquer outra fase.

---

## 8. Dependências externas

> Itens que o projeto depende mas não controla diretamente.

### 8.1 Dependências técnicas externas

| Dependência | Status | Necessária em | Como verificar |
|---|---|---|---|
| **Java 25 LTS** disponível no CI | ✅ GA desde set/2025 | Fase 3 | `actions/setup-java@v4` com `java-version: '25'` em branch de teste |
| **Spring Boot 4.0.3** no Maven Central | ✅ Disponível | Fase 3 | `https://search.maven.org/artifact/org.springframework.boot/spring-boot` |
| **DGS Framework 10.x** compatível com SB4 | ⚠️ Verificar | Fase 3 | `https://search.maven.org/artifact/com.netflix.graphql.dgs/graphql-dgs-spring-boot-starter` |
| **Gradle 9.3.1** estável | ✅ Disponível | Fase 3 | `https://gradle.org/releases/` |
| **PostgreSQL 16 Alpine** no Docker Hub | ✅ Disponível | Fase 1 | `docker pull postgres:16-alpine` |
| **Testcontainers** com Docker no CI | ✅ GitHub Actions ubuntu-latest | Fase Testes | Testar em branch de validação |
| **Playwright Node.js** versão compatível | ✅ Disponível | Fase Testes | `npm install @playwright/test` |
| **truffleHog** ou **git-secrets** | ✅ Disponíveis | Fase 1 | `pip install trufflehog` ou `brew install git-secrets` |
| **LGTM Stack** versões compatíveis | ⚠️ Verificar combinação | Fase 1/4 | Testar `docker compose up` localmente antes de commitar |

---

### 8.2 Dependências de processo externo

| Dependência | Necessária em | Responsável | Risco se ausente |
|---|---|---|---|
| Acesso ao **GitHub** com permissão para criar templates | Fase 0 | Tech Lead | Bloqueia EPIC-01 |
| Acesso ao **Coda** para todos os membros | Fase 0 | PM | Bloqueia M6 (KR6.7) |
| **ADR-001 aprovado** antes de iniciar INI-04 | Fase 3 | PM + Tech Lead | Bloqueia INI-04 |
| Disponibilidade da **PM para Gameday 1** | Fase 4 | PM | Bloqueia M4 |
| Disponibilidade da **PM para Gameday 2** | Fase 6 | PM | Bloqueia M6 |
| **Desenvolvedor novo** disponível para teste de onboarding | Fase 1 | PM | Impede validação de M-DEV-01 |

---

## 9. Dependências de equipe e ambiente

> Itens que cada membro do time precisa ter configurado antes de iniciar.

### 9.1 Pré-requisitos por fase

| Fase | Pré-requisito | Para quê | Obrigatório para |
|---|---|---|---|
| **Fase 0** | Acesso ao GitHub com 2FA | Templates, issues, PRs | Todo o time |
| **Fase 0** | GitAhead instalado | Validar histórico de commits | Todo o time |
| **Fase 0** | Coda com acesso | Documentação de prompts e skills | Todo o time |
| **Fase 1+** | Docker Desktop (Mac/Linux) ou Docker Engine (Linux) | `docker compose up` | Todo o time |
| **Fase 1+** | Python 3.x instalado | Script de validação startup/shutdown | Todo o time |
| **Fase 3+** | Java 25 JDK instalado localmente | Desenvolvimento e build | Dupla A |
| **Fase Testes+** | Node.js LTS instalado | Playwright | Dupla B |
| **Fase Testes+** | `@playwright/test` instalado | Fluxos E2E | Dupla B |

---

### 9.2 Configuração mínima do ambiente de desenvolvimento

```bash
# Verificar pré-requisitos antes de iniciar

# Java 25
java -version  # deve mostrar Java 25

# Docker
docker --version  # deve mostrar 24.x+
docker compose version  # deve mostrar 2.x

# Python
python3 --version  # deve mostrar 3.8+

# Node.js (para Playwright)
node --version  # deve mostrar 20.x+

# GitAhead
# Instalado via download em https://gitahead.github.io/

# Coda
# Acesso via navegador em https://coda.io/
```

---

### 9.3 Variáveis de ambiente obrigatórias

Seguindo ADR-006, cada membro do time precisa configurar no ambiente local:

```bash
# Obrigatória — sem esta variável a aplicação não sobe
export JWT_SECRET=$(openssl rand -base64 64)

# Opcional — padrão 86400 se ausente
export JWT_SESSION_TIME=604800  # 7 dias em dev

# Para Docker Compose
export SPRING_PROFILES_ACTIVE=dev
```

O arquivo `.env.example` no repositório documenta todas as variáveis necessárias com instruções.

---

## 10. Registro de riscos por fase

### Fase 0 — Processo

| ID | Risco | Ação preventiva |
|---|---|---|
| R-18 | Issues não criadas antes de codar | Template de PR exige issue |
| R-19 | Coda abandonado | DoD de cada história inclui Coda |
| R-PROC-03 | Branch bleeding sem commits automáticos | US-01.06 configura na Fase 0 |

---

### Fase 1 — Segurança e Ambiente

| ID | Risco | Ação preventiva |
|---|---|---|
| R-11 | Script Python diferente por SO | Testar em Mac e Linux antes de commitar |
| R-13 | JWT_SESSION_TIME sem fallback | `${JWT_SESSION_TIME:86400}` obrigatório — ADR-006 |
| R-14 | LGTM Stack versões incompatíveis | Fixar versões no docker-compose.yml |
| R-22 | SQLite ainda em uso | Grep por sqlite na Fase 1 — critério de aceitação |

---

### Fase 3 — Modernização do Stack

| ID | Risco | Ação preventiva |
|---|---|---|
| **R-01** 🚨 | DGS incompatível com SB4 | Verificar Maven Central **antes de iniciar** — DoR de EPIC-04 |
| **R-02** 🚨 | Spring Security 6.x breaking changes | Branch isolado + INI-08 em paralelo |
| R-04 | Java 25 indisponível no CI | Verificar GitHub Actions setup-java **antes de iniciar** |
| R-05 | Testes revelam regressões | Aceitar como esperado; criar issues por regressão |
| R-06 | Joda-Time bloqueia records | US-04.07 precede US-06.02 — DoR explícito |
| R-07 | Dependências transitivas incompatíveis | `./gradlew dependencies` antes de iniciar |
| R-09 | Queries MyBatis complexas | Mapear todas as queries antes de iniciar INI-05 |
| R-15 | Edição manual de `io.spring.graphql` | ADR-004 leitura obrigatória; considerar .gitignore |
| R-17 ⚠️ | Vibe coding sem leitura de ADRs | ADRs no DoR de cada épico |

---

### Fase Testes — Qualidade

| ID | Risco | Ação preventiva |
|---|---|---|
| **R-03** ⚠️ | Pitest abaixo de 95% | Iniciar INI-07 durante Fase 3; não aguardar M3 |
| R-08 | Testcontainers sem Docker no CI | Verificar CI antes de INI-08 |
| R-12 | Playwright com GraphQL | Smoke test antes dos fluxos completos |

---

### Fase 4 — Observabilidade

| ID | Risco | Ação preventiva |
|---|---|---|
| R-10 | AOP não captura todos os endpoints | Verificar M-OBS-01 endpoint a endpoint |
| R-14 | LGTM Stack incompatível | Versões fixadas desde Fase 1 |
| R-20 | Gameday > 15 minutos | Familiarização com Grafana antes do exercício |

---

### Fase 5 — Produto

| ID | Risco | Ação preventiva |
|---|---|---|
| R-16 | Artigos com `reading_time_minutes NULL` | Comportamento esperado e documentado; monitorar via M-PROD-07 |
| R-21 | DELETE retorna não-204 | US-12.06 e US-12.07 verificam explicitamente |
| R-CONT-01 | Comportamento visível após soft delete | Testes de contrato validam a cada build |
| R-CONT-02 | `readingTimeMinutes` incorreto | M-PROD-08 + Pitest validam a lógica |
| R-CONT-03 | REST e GraphQL inconsistentes | US-13.06 + US-13.07 implementam ambas as APIs |

---

### Fase 6 — Validação

| ID | Risco | Ação preventiva |
|---|---|---|
| R-19 | Coda abandonado | Revisão de Coda em cada retrospectiva de fase |
| R-20 | Gameday 2 > 15 minutos | Gameday 1 como treino; incorporar aprendizados |
| R-PROC-01 | Scope creep acumulado | Revisão de scope em cada retrospectiva |

---

## Resumo executivo

| Severidade | Quantidade | Riscos principais |
|---|---|---|
| 🚨 Crítico | 2 | R-01 (DGS/SB4), R-02 (Spring Security) |
| ⚠️ Alto | 7 | R-03 (Pitest), R-04 (Java 25), R-05 (regressões), R-06 (Joda-Time), R-07 (deps), R-08 (Testcontainers), R-17 (vibe coding) |
| 📋 Médio | 11 | Queries JPA, AOP, script Python, Playwright, JWT, LGTM, código gerado, artigos NULL, issues, Coda, Gameday |
| ℹ️ Baixo | 2 | Java 25 LTS, Pitest lento |
| De negócio/contrato | 3 | DELETE 204, readingTimeMinutes, REST/GraphQL consistência |
| De processo | 3 | Scope creep, padrões de código, branch bleeding |

**Os dois riscos que mais merecem atenção proativa:**
1. **R-01 (DGS/SB4):** verificar compatibilidade antes de qualquer linha de código da Fase 3
2. **R-03 (Pitest):** iniciar durante a Fase 3, não depois — cada semana de atraso impacta a transição para Fase 5

---

*Documento vivo — atualizar status de cada risco conforme fases progridem*  
*Riscos materializados devem ser registrados com: data, impacto real, resolução adotada*  
*24 riscos identificados · 8 dependências externas · 9 pré-requisitos de equipe*  
*Rastreado em: `03-initiatives.md` · `04-roadmap.md` · `05-backlog.md` · `06-architecture-decisions.md` · `07-metrics.md`*