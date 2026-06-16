markdown# 02 -  Product Vision — RealWorld Platform Modernization
> Versão 3.0 · Junho 2026  
> Baseado no AS IS: `gothinkster/spring-boot-realworld-example-app`

---

## Sumário

1. [Declaração de visão](#1-declaração-de-visão)
2. [Contexto estratégico](#2-contexto-estratégico)
3. [O problema que estamos resolvendo](#3-o-problema-que-estamos-resolvendo)
4. [Visão de negócio](#4-visão-de-negócio)
5. [OKRs e métricas](#5-okrs-e-métricas)
6. [Visão técnica](#6-visão-técnica)
7. [Roadmap de evolução](#7-roadmap-de-evolução)
8. [Riscos e dependências](#9-riscos-e-dependências)

---

# 1. Declaração de visão

> Transformar a aplicação RealWorld em uma **plataforma moderna, observável, escalável e rastreável**, eliminando dívida técnica crítica, estabelecendo práticas de engenharia de referência e criando a base para crescimento contínuo — com qualidade verificável, processo documentado e times capazes de evoluir o produto com previsibilidade e confiança.

Esta visão tem três dimensões inseparáveis:

- **Para o negócio:** uma plataforma confiável que suporta usuários reais, entrega novas capacidades com velocidade crescente e não acumula risco silencioso.
- **Para a engenharia:** um ambiente moderno, com ferramentas, processos e práticas que tornam cada entrega mais segura do que a anterior.
- **Para o time:** um processo de trabalho claro, rastreável e colaborativo — onde cada pessoa sabe o que está fazendo, por que está fazendo e como o seu trabalho se conecta ao resultado do produto.

---

# 2. Contexto estratégico

## Por que agora?

A aplicação RealWorld demonstra boas intenções arquiteturais — separação de camadas, DDD, CQRS — mas foi construída sobre escolhas tecnológicas que já eram provisórias quando foram feitas e hoje representam bloqueadores reais.

O Spring Boot 2.6 está sem suporte de segurança desde novembro de 2023. O banco SQLite é explicitamente um artefato de desenvolvimento local. A chave de autenticação está exposta publicamente no código. Não existe observabilidade. Não existe processo padronizado de desenvolvimento.

Ao mesmo tempo, a gestão definiu um conjunto claro de evoluções esperadas: modernização para Java 25 + Spring Boot 4 + Gradle 9.3, novas funcionalidades de produto (soft delete, tempo de leitura), governança com GitHub Issues e Conventional Commits, cobertura por mutação a 95%, observabilidade com LGTM Stack, e desenvolvimento assistido por IA com Coda.

**Este não é um projeto de manutenção. É uma modernização estratégica com entrega de valor mensurável.**

## Para quem entregamos valor?

| Audiência | O que espera desta evolução |
|---|---|
| **Gestão / stakeholders** | Plataforma confiável, rastreável, com risco controlado e métricas visíveis |
| **Time de desenvolvimento** | Ambiente moderno, processo claro, ferramentas adequadas, onboarding ágil |
| **Usuários finais** | Experiência mais rica, novas funcionalidades, maior estabilidade |
| **Novos desenvolvedores** | Documentação clara, ambiente que sobe com um comando, processo definido |

---

# 3. O problema que estamos resolvendo

## Situação atual em números

| Indicador | Valor atual | Impacto |
|---|---|---|
| Versões com suporte ativo | 0 de 3 (Java, Spring Boot, Gradle) | Risco de segurança contínuo sem patches |
| Vulnerabilidades críticas conhecidas | 2 (JWT exposto + Spring Boot EOL) | Qualquer pessoa pode falsificar autenticação |
| Usuários simultâneos suportados | < 10 (limitação SQLite) | Inviabiliza qualquer uso real |
| Cobertura de testes por mutação | 0% | Mudanças podem quebrar tudo sem ser detectadas |
| Endpoints documentados formalmente | 0 de 19 | Integrações dependem de leitura direta do código |
| Tempo estimado de onboarding | 1–2 semanas | Alto custo de entrada para novos membros |
| Observabilidade | Nenhuma | Tempo médio de diagnóstico: horas a dias |
| Commits com convenção padronizada | 0% | Histórico ilegível, changelogs impossíveis |
| Issues criadas antes de codar | 0% | Trabalho sem rastreabilidade |

## O custo invisível

Cada problema acima tem um custo que não aparece em nenhum relatório, mas é real:

- Um bug em produção sem observabilidade custa **horas de trabalho** de engenharia só para ser localizado
- Um novo desenvolvedor sem ambiente containerizado e documentado custa **dias de setup** antes de ser produtivo
- Um commit sem convenção custa **minutos de leitura** multiplicados por cada pessoa que precisa entender a mudança
- Uma mudança sem testes de mutação pode **silenciosamente quebrar** o que já funcionava sem que ninguém perceba

---

# 4. Visão de negócio

## O que preservamos

A arquitetura em camadas (DDD + CQRS) é um ativo real. O domínio de negócio — usuários, artigos, comentários, tags, follows, favoritos — está correto e será mantido. Os contratos de API (REST e GraphQL) serão preservados durante toda a evolução. **Não reescrevemos o que funciona bem.**

## O que entregamos de novo

### Soft delete
Artigos e comentários deletados passarão a usar uma flag `is_deleted` em vez de remoção permanente. O comportamento visível ao usuário é idêntico. Para o negócio: auditoria de exclusões, capacidade de recuperação futura, conformidade com requisitos de retenção de dados.

### Tempo estimado de leitura
Cada artigo exibirá o tempo estimado de leitura (200 palavras/minuto). Calculado na criação e armazenado em cache. Artigos existentes recebem o valor de forma lazy na primeira leitura. Para o usuário: decisão de leitura mais informada. Para o produto: fundação para futuras funcionalidades de engajamento.

### Observabilidade com LGTM Stack
A plataforma passará a expor métricas por endpoint, logs estruturados de startup e shutdown, e traces habilitados. Para a operação: visibilidade real do comportamento do sistema em tempo real.

### Qualidade verificável
Cobertura de testes por mutação a 95% significa que a equipe consegue entregar mudanças com confiança de que nada foi silenciosamente quebrado.

---

# 5. OKRs e métricas

> Os OKRs estão organizados por objetivo estratégico. Cada Key Result tem uma métrica palpável com valor atual e meta clara.

---

## OKR 1 — Modernizar a plataforma tecnológica

**Objective:** A plataforma opera sobre uma stack com suporte ativo, recursos modernos de linguagem e sem warnings de deprecação.

> *Por que importa:* Cada dia rodando Spring Boot 2.x EOL é um dia acumulando risco de segurança sem patches. Cada recurso de Java 11 que poderíamos usar em Java 25 é produtividade deixada na mesa.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR1.1 | Upgrade completo do runtime | Versão do Java em produção | Java 11 | Java 25 | `java -version` no container |
| KR1.2 | Upgrade completo do framework | Versão do Spring Boot | 2.6.3 | 4.0.3 | `build.gradle` |
| KR1.3 | Build sem warnings de deprecação | Warnings de deprecação no build | N/A (não medido) | 0 warnings | Saída do `./gradlew build` |
| KR1.4 | Persistência migrada para JPA | Arquivos de mapper MyBatis restantes | 100% MyBatis | 0 mappers MyBatis | Contagem de arquivos `*Mapper.java` |
| KR1.5 | Adoção de records Java | % de DTOs usando record types | 0% | ≥ 80% dos DTOs | Revisão de código + análise estática |
| KR1.6 | Joda-Time eliminado | Ocorrências de `import org.joda` | N/A (presente) | 0 ocorrências | Grep no codebase / SonarQube |

---

## OKR 2 — Aumentar qualidade e confiabilidade

**Objective:** Qualquer mudança no código pode ser entregue com confiança de que nada existente foi quebrado, verificado por testes que detectam falhas reais — não apenas executam código.

> *Por que importa:* Cobertura de linhas é uma métrica enganosa. Testes de mutação verificam se os testes realmente detectam bugs. 95% de cobertura de mutação significa que 95 de cada 100 bugs artificialmente introduzidos são capturados.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR2.1 | Cobertura de testes por mutação | Mutation score (Pitest) | 0% | ≥ 95% | Relatório Pitest no CI |
| KR2.2 | Contrato REST coberto por testes de integração | % de endpoints REST com teste de integração | ~40% (parcial) | 100% (19/19 endpoints) | Contagem de testes por endpoint |
| KR2.3 | Contrato GraphQL coberto por testes de integração | % de queries/mutations com teste | ~0% | 100% (18/18 operações) | Contagem de testes por operação |
| KR2.4 | Testes E2E com Playwright funcionando | Fluxos críticos cobertos com Playwright | 0 | ≥ 5 fluxos críticos | Branch `playwright-working` no CI |
| KR2.5 | Build quebra se cobertura cair | Threshold de cobertura configurado no CI | Não existe | Falha se mutation score < 95% | Configuração do CI pipeline |
| KR2.6 | Zero regressões entre fases | Bugs introduzidos por mudanças de stack | N/A | 0 regressões não detectadas por testes | Comparação de comportamento pré/pós upgrade |

---

## OKR 3 — Melhorar experiência do desenvolvedor

**Objective:** Um desenvolvedor novo consegue entender o projeto, subir o ambiente completo e fazer seu primeiro commit dentro do primeiro dia de trabalho.

> *Por que importa:* Onboarding lento é custo direto. Cada semana que um novo desenvolvedor passa configurando ambiente em vez de entregar é uma semana de salário sem retorno.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR3.1 | Tempo para subir o ambiente local do zero | Minutos do clone ao sistema rodando | ~120–480 min (estimado) | ≤ 15 minutos | Medição cronometrada com dev novo |
| KR3.2 | Ambiente completo com um comando | Serviços no Docker Compose | 0 (apenas app) | app + db + observabilidade | `docker compose up` funcional |
| KR3.3 | Documentação de setup verificável | Passos do `CONTRIBUTING.md` sem erro | Inexistente | 100% executáveis por dev novo | Walkthrough com dev externo ao projeto |
| KR3.4 | Startup e shutdown validados automaticamente | Script Python de validação | Inexistente | Passa em 100% das execuções | Execução do script no CI |
| KR3.5 | Perfis de ambiente configurados | Ambientes com configuração separada | 0 | 3 (dev, staging, prod) | Arquivos `application-{env}.yml` |

---

## OKR 4 — Preparar para crescimento futuro

**Objective:** A plataforma suporta crescimento real de carga e usuários sem necessidade de reescrita, e cada funcionalidade nova preserva os dados de forma auditável.

> *Por que importa:* SQLite como banco de produção é um teto artificial de escala. Soft delete é a diferença entre perder dados e ter controle sobre eles.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR4.1 | Banco substituído por PostgreSQL | Banco de dados em uso | SQLite | PostgreSQL | Configuração no `application.yml` |
| KR4.2 | Soft delete implementado em artigos | Artigos deletados permanentemente | 100% (hard delete) | 0% (todos usam `is_deleted`) | Migration aplicada + testes |
| KR4.3 | Soft delete implementado em comentários | Comentários deletados permanentemente | 100% (hard delete) | 0% (todos usam `is_deleted`) | Migration aplicada + testes |
| KR4.4 | Tempo de leitura calculado em novos artigos | Artigos criados sem `reading_time_minutes` | 100% | 0% | Teste de criação de artigo |
| KR4.5 | Tempo de leitura com lazy update para artigos existentes | Artigos existentes sem `reading_time_minutes` calculado | 100% | 0% após primeira leitura | Query no banco pós-uso |

---

## OKR 5 — Implementar observabilidade ponta a ponta

**Objective:** A equipe consegue responder "o que está acontecendo agora?" e "o que aconteceu quando falhou?" em menos de 15 minutos, com dados — não com suposições.

> *Por que importa:* Sem observabilidade, diagnóstico de incidente depende de sorte e experiência individual. Com ela, depende de dados.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR5.1 | Endpoints com contador de chamadas | % de endpoints com métrica de contador | 0% | 100% (19/19 endpoints REST) | Verificação no dashboard Grafana |
| KR5.2 | Log de startup estruturado e validado | Log de startup existe e é validado | Não existe | Script Python passa 100% das vezes | CI + execução do script |
| KR5.3 | Log de shutdown estruturado e validado | Log de shutdown existe e é validado | Não existe | Script Python passa 100% das vezes | CI + execução do script |
| KR5.4 | Traces habilitados | % de requisições com trace | 0% | 100% | Verificação no Tempo / Grafana |
| KR5.5 | Dashboard Grafana operacional | Dashboards disponíveis localmente | 0 | ≥ 1 dashboard com métricas de negócio | `docker compose up` + acesso ao Grafana |
| KR5.6 | Tempo de diagnóstico de incidente simulado | Minutos para localizar causa raiz num cenário simulado | N/A (sem dados) | ≤ 15 minutos | Exercício de gameday com a equipe |

---

## OKR 6 — Fortalecer governança e rastreabilidade

**Objective:** Toda mudança tem origem documentada, formato padronizado e é rastreável do problema ao código entregue.

> *Por que importa:* Um histórico de commits legível vale meses de documentação. Uma issue bem estruturada evita retrabalho. Rastreabilidade é o que separa um time de amadores de um time de profissionais.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR6.1 | Conventional Commits adotados | % de commits no formato convencional | 0% | 100% | commitlint no CI — falha se formato errado |
| KR6.2 | Issues criadas antes de codar | % de PRs com issue vinculada | 0% | 100% | Template de PR exige link para issue |
| KR6.3 | DoR e DoD documentados | Repositórios com DoR e DoD | 0 | 1 (este repositório) | Arquivo no repositório |
| KR6.4 | API documentada formalmente | % de endpoints com OpenAPI spec | 0% | 100% (19/19 REST + 18 GraphQL) | Swagger UI acessível e completo |
| KR6.5 | JWT secret fora do código | Segredos hardcoded no repositório | 1 (JWT secret) | 0 | Scan de segredos no CI (git-secrets / truffleHog) |
| KR6.6 | PRs abertas sem revisão zeradas | PRs abertas pendentes | 5 | 0 | Aba Pull Requests no GitHub |
| KR6.7 | Prompts e skills documentados no Coda | % de etapas com documentação no Coda | 0% | 100% das etapas do roadmap | Revisão no Coda ao final de cada fase |

---

## OKR 7 — Evoluir funcionalidades de negócio

**Objective:** A plataforma entrega duas novas capacidades funcionais verificáveis por usuários e cobertas por testes, sobre uma base tecnológica saudável.

> *Por que importa:* Modernização sem entrega de produto é projeto interno. As funcionalidades novas provam que a base modernizada funciona para o que foi criada: entregar valor.

| # | Key Result | Métrica | Hoje | Meta | Como medir |
|---|---|---|---|---|---|
| KR7.1 | Soft delete funcional e testado | Testes de mutação cobrindo soft delete | 0% | ≥ 95% (Pitest) | Relatório Pitest |
| KR7.2 | Tempo de leitura funcional e testado | Testes de mutação cobrindo tempo de leitura | 0% | ≥ 95% (Pitest) | Relatório Pitest |
| KR7.3 | Tempo de leitura documentado no OpenAPI | Campo `readingTimeMinutes` no schema OpenAPI | Não existe | Documentado e visível no Swagger UI | Swagger UI |
| KR7.4 | Soft delete auditável no banco | Registros com `is_deleted = true` consultáveis | Impossível (hard delete) | Query retorna histórico de exclusões | Query direta no PostgreSQL |
| KR7.5 | Cache lazy 100% resolvido | % de artigos existentes com `reading_time_minutes` calculado | 0% | 100% após primeira leitura de cada artigo | Query no banco |

---

## Painel consolidado de OKRs

> Referência rápida para acompanhamento em reuniões de gestão.

| OKR | Objective | KRs | Prazo sugerido |
|---|---|---|---|
| **OKR 1** | Stack com suporte ativo e sem deprecations | 6 KRs | Fase 2 |
| **OKR 2** | Qualidade verificável por mutação | 6 KRs | Fase 3 |
| **OKR 3** | Onboarding em menos de 1 dia | 5 KRs | Fase 1 + 3 |
| **OKR 4** | Escala real e dados auditáveis | 5 KRs | Fase 1 + 5 |
| **OKR 5** | Observabilidade ponta a ponta | 6 KRs | Fase 4 |
| **OKR 6** | Governança e rastreabilidade total | 7 KRs | Fase 0 + contínuo |
| **OKR 7** | Duas novas funcionalidades entregues | 5 KRs | Fase 5 |
| **Total** | | **40 KRs mensuráveis** | |

---

# 6. Visão técnica

## Stack alvo

| Camada | Atual | Alvo |
|---|---|---|
| Linguagem | Java 11 | **Java 25** |
| Framework | Spring Boot 2.6.3 | **Spring Boot 4.0.3** |
| Build | Gradle (versão antiga) | **Gradle 9.3.1+** sem deprecation warnings |
| Banco de dados | SQLite (arquivo local) | **PostgreSQL** (containerizado) |
| ORM / Mapper | MyBatis 2.2.2 | **Spring Data JPA / Hibernate** |
| Observabilidade | Nenhuma | **LGTM Stack** (Loki + Grafana + Tempo + Prometheus) |
| Documentação API | Nenhuma | **OpenAPI / Swagger UI** (springdoc 2.x) |
| Testes de mutação | Nenhum | **Pitest** — cobertura ≥ 95% |
| Testes E2E | Nenhum | **Playwright** |
| Commits | Livre | **Conventional Commits** + commitlint no CI |
| Gestão de trabalho | Nenhuma | **GitHub Issues** com templates, DoR e DoD |
| Desenvolvimento assistido | Nenhum | **Coda** — prompts e skills documentados |
| Containerização | Parcial | **Docker Compose** completo (app + db + observ.) |

## Evoluções de código

### Records Java 25
```java
// Antes (classe com boilerplate)
public class ArticleData {
    private String slug;
    private String title;
    // getters, constructor, equals, hashCode...
}

// Depois (record imutável, zero boilerplate)
public record ArticleData(String slug, String title, String description,
                          String body, List tagList,
                          int readingTimeMinutes, boolean favorited,
                          int favoritesCount, ProfileData author) {}
```

### Soft delete
```sql
-- Migration Flyway V{n}__add_soft_delete.sql
ALTER TABLE articles ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE comments ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
```

```java
@Where(clause = "is_deleted = false")
@Entity
public class Article { ... }

// Soft delete: nunca mais repository.delete()
article.setDeleted(true);
articleRepository.save(article);
```

### Tempo de leitura com cache lazy
```java
public static int calculateReadingTime(String body) {
    if (body == null || body.isBlank()) return 1;
    int wordCount = body.trim().split("\\s+").length;
    return Math.max(1, (int) Math.ceil(wordCount / 200.0));
}

// Lazy update: calculado na primeira leitura se ausente
public ArticleData getArticle(String slug) {
    Article article = articleRepository.findBySlug(slug);
    if (article.getReadingTimeMinutes() == null) {
        article.setReadingTimeMinutes(calculateReadingTime(article.getBody()));
        articleRepository.save(article); // atualiza em background
    }
    return toData(article);
}
```

### Observabilidade — contador por endpoint
```java
@GetMapping("/articles")
public ResponseEntity listArticles(...) {
    meterRegistry.counter("api.calls.total",
        "endpoint", "list_articles",
        "method", "GET"
    ).increment();
    // ...
}
```

### Conventional Commits — exemplos reais
```
feat(articles): add reading time estimation on article creation
feat(articles): add soft delete with is_deleted flag
fix(auth): move jwt secret to environment variable
chore(deps): upgrade spring boot to 4.0.3
chore(deps): upgrade java to 25
refactor(infrastructure): migrate article repository from mybatis to jpa
test(articles): add mutation tests for reading time calculation
test(contracts): add integration tests for all 19 REST endpoints
ci(quality): add pitest with 95% mutation threshold
docs(api): add openapi spec for articles endpoints
```

---

# 7. Roadmap de evolução

## Fase 0 — Fundação do processo
*Antes de qualquer código: o processo precisa existir*

**OKRs servidos:** OKR 6

- [ ] Criar templates de GitHub Issues com DoR e DoD documentados
- [ ] Configurar commitlint no CI para Conventional Commits
- [ ] Criar issues para todas as entregas das fases seguintes
- [ ] Setup do Coda para documentação de prompts e skills
- [ ] Configurar branch `bleeding` para harness development
- [ ] Instalar GitAhead nos ambientes do time

**Critério de saída:** processo documentado, issues criadas, CI rejeitando commits fora do padrão.

---

## Fase 1 — Segurança e infraestrutura base
*Eliminar bloqueadores críticos sem alterar comportamento visível*

**OKRs servidos:** OKR 3, OKR 4, OKR 6 (KR6.5)

- [ ] Mover JWT secret para variável de ambiente
- [ ] Configurar perfis Spring (`dev`, `staging`, `prod`)
- [ ] Substituir SQLite por PostgreSQL via Docker Compose
- [ ] Adicionar Spring Actuator (`/health`, `/info`, `/metrics`)
- [ ] Configurar logging estruturado (JSON) com correlation IDs
- [ ] Script Python de validação de startup e shutdown
- [ ] Docker Compose completo: app + PostgreSQL

**Critério de saída:** KR3.2, KR3.4, KR4.1 e KR6.5 atingidos.

---

## Fase 2 — Modernização do stack
*Sair do estado EOL e adotar Java 25 + Spring Boot 4*

**OKRs servidos:** OKR 1

- [ ] Upgrade Java 11 → Java 25
- [ ] Upgrade Spring Boot 2.6.3 → Spring Boot 4.0.3
- [ ] Upgrade Gradle → 9.3.1+ sem deprecation warnings
- [ ] Ajustar `javax.*` → `jakarta.*`
- [ ] Migrar MyBatis → Spring Data JPA / Hibernate
- [ ] Introduzir record types nos DTOs
- [ ] Substituir Joda-Time por `java.time`

**Critério de saída:** todos os KRs do OKR 1 atingidos; todos os testes passando.

---

## Fase 3 — Qualidade verificável
*Dar à equipe confiança para entregar*

**OKRs servidos:** OKR 2

- [ ] Configurar JaCoCo no CI
- [ ] Configurar Pitest — threshold ≥ 95%
- [ ] Criar testes de integração para os 19 endpoints REST
- [ ] Criar testes de integração para as 18 operações GraphQL
- [ ] Criar branch `playwright-broken`
- [ ] Criar branch `playwright-working` com Playwright cobrindo ≥ 5 fluxos críticos
- [ ] CI falha se mutation score cair abaixo de 95%

**Critério de saída:** todos os KRs do OKR 2 atingidos.

---

## Fase 4 — Observabilidade e documentação
*Ver o sistema por dentro e comunicar o contrato para fora*

**OKRs servidos:** OKR 5, OKR 6 (KR6.4)

- [ ] Integrar LGTM Stack no Docker Compose
- [ ] Adicionar contador Micrometer em todos os 19 endpoints
- [ ] Configurar Loki para logs estruturados
- [ ] Habilitar traces via Tempo
- [ ] Dashboard Grafana com métricas básicas de negócio
- [ ] Gerar OpenAPI completo com springdoc 2.x
- [ ] Swagger UI acessível e documentando 100% dos endpoints
- [ ] Gameday de diagnóstico com a equipe (KR5.6)

**Critério de saída:** todos os KRs do OKR 5 e KR6.4 atingidos.

---

## Fase 5 — Evolução funcional
*Entregar as novas capacidades de produto sobre a base saudável*

**OKRs servidos:** OKR 4 (KRs 4.2–4.5), OKR 7

- [ ] Soft delete em artigos (`is_deleted` + migration Flyway)
- [ ] Soft delete em comentários
- [ ] Cálculo de tempo de leitura na criação de artigos
- [ ] Cache lazy para artigos existentes
- [ ] Testes de mutação para as novas funcionalidades (≥ 95%)
- [ ] OpenAPI atualizado com `readingTimeMinutes` e comportamento de soft delete

**Critério de saída:** todos os KRs do OKR 7 e KRs 4.2–4.5 atingidos.

---

# 8. Riscos e dependências

## Riscos técnicos

| Risco | Probabilidade | Impacto | KRs afetados | Mitigação |
|---|---|---|---|---|
| DGS Framework incompatível com Spring Boot 4 | Alta | Alto | KR1.1, KR1.2 | Avaliar Spring for GraphQL antes da Fase 2; documentar decisão como ADR |
| Breaking changes Spring Security 6.x | Alta | Médio | KR2.6 | Executar upgrade em branch isolado com test suite completo |
| Migrations Flyway SQLite → PostgreSQL com diferenças de comportamento | Média | Médio | KR4.1 | Revisar e testar todos os scripts SQL; manter KR2.2 como rede de segurança |
| Java 25 sem LTS no momento da execução | Baixa | Médio | KR1.1 | Usar Java 21 (LTS atual) como fallback; confirmar antes de iniciar Fase 2 |
| Cobertura insuficiente para detectar regressões no upgrade | Alta | Alto | KR2.1, KR2.6 | Executar Fase 3 antes ou em paralelo com Fase 2 |

## Riscos de processo

| Risco | Probabilidade | Impacto | KRs afetados | Mitigação |
|---|---|---|---|---|
| Time não adotar Conventional Commits | Média | Médio | KR6.1 | commitlint no CI — build falha se formato errado |
| PRs sem issues vinculadas | Média | Médio | KR6.2 | Template de PR exige link; incluído no DoD |
| Documentação Coda abandonada | Alta | Baixo | KR6.7 | Incluir na DoD; revisar na retrospectiva de cada fase |

## Dependências externas

| Dependência | Necessidade | Fase | KRs dependentes |
|---|---|---|---|
| Docker e Docker Compose | Obrigatório | Fase 1 | KR3.2, KR5.5 |
| PostgreSQL (via Docker) | Obrigatório | Fase 1 | KR4.1 |
| Java 25 disponível (ou Java 21 LTS) | Obrigatório | Fase 2 | KR1.1 |
| Spring Boot 4.0.3 GA disponível | Obrigatório | Fase 2 | KR1.2 |
| Gradle 9.3.1 estável | Obrigatório | Fase 2 | KR1.3 |
| Acesso ao Coda | Necessário | Fase 0 | KR6.7 |
| GitAhead instalado | Necessário | Todas | KR6.1 |

---

*Documento vivo — revisitar ao fim de cada fase*  
*Próxima revisão recomendada: ao encerrar a Fase 0*  
*40 KRs mensuráveis distribuídos em 7 OKRs e 5 fases de execução*  
*Baseado no AS IS mapeado em Junho 2026 e nas diretrizes da gestão*

A principal mudança desta versão: cada OKR tem Key Results com valor atual, meta clara e forma de medição — nada subjetivo. Os 40 KRs são todos verificáveis: números, percentuais, contagens ou estados binários. O painel consolidado no final do OKR 5 dá à gestão uma visão de uma página para acompanhamento em reuniões. E cada fase do roadmap agora referencia explicitamente quais OKRs e KRs ela serve.