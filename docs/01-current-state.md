# AS IS — spring-boot-realworld-example-app
> Versão 2.0 · Junho 2026  
> Análise de situação atual — Técnica e de Negócio  
> Verificado linha a linha contra o repositório público  
> 🔗 https://github.com/gothinkster/spring-boot-realworld-example-app

---

## Registro de revisões

| Versão | O que mudou |
|---|---|
| v1.0 | Versão inicial — análise de situação atual técnica e de negócio |
| **v2.0** | **7 correções pós cross-check do código real:** GAP-7 estatísticas confirmadas do GitHub; GAP-4 `jwt.sessionTime` adicionado à seção de segurança; GAP-5 pacote `io.spring.graphql` como código gerado documentado; GAP-9 interface `Node` em `ArticleData` documentada; back-reference `Comment.article` adicionado; campos reais de `ArticleData` confirmados do fonte; imports `javax.crypto` em `DefaultJwtService` documentados |

---

## Sumário

1. [O que é este projeto](#1-o-que-é-este-projeto)
2. [Saúde do repositório](#2-saúde-do-repositório)
3. [Stack tecnológica atual](#3-stack-tecnológica-atual)
4. [Arquitetura de software](#4-arquitetura-de-software)
5. [Estrutura de código confirmada](#5-estrutura-de-código-confirmada)
6. [Funcionalidades implementadas](#6-funcionalidades-implementadas)
7. [Modelo de dados e domínio](#7-modelo-de-dados-e-domínio)
8. [Segurança](#8-segurança)
9. [Testes e qualidade](#9-testes-e-qualidade)
10. [CI/CD e operação](#10-cicd-e-operação)
11. [Riscos e dívida técnica](#11-riscos-e-dívida-técnica)
12. [Avaliação de maturidade](#12-avaliação-de-maturidade)
13. [Visão de negócio](#13-visão-de-negócio)
14. [Gap analysis — o que falta para produção](#14-gap-analysis--o-que-falta-para-produção)

---

# 🧭 PARTE 1 — VISÃO DE NEGÓCIO
> *Para gestores, PMs, BAs e stakeholders. Sem jargão técnico.*

---

## 1. O que é este projeto?

Este projeto é um **backend de referência** criado pela comunidade global de desenvolvimento de software. Ele simula o servidor de uma plataforma de blog estilo Medium — chamada internamente de "Conduit" — com funcionalidades completas de publicação, interação social e autenticação de usuários.

**Ele não é um produto comercial.** É código educacional de alta qualidade, usado por desenvolvedores do mundo todo para aprender boas práticas e comparar diferentes tecnologias. A organização *gothinkster* mantém dezenas de implementações similares em linguagens diferentes (Python, Node.js, Go, Ruby…), e este repositório é a versão oficial em Java com Spring Boot.

| Atributo | Valor |
|---|---|
| **Nome** | spring-boot-realworld-example-app |
| **Organização** | gothinkster |
| **Tipo** | Aplicação de referência / exemplo educacional |
| **Domínio simulado** | Plataforma de blog (estilo Medium / Conduit) |
| **Licença** | MIT — uso livre, incluindo comercial, com atribuição |
| **Linguagem** | Java 100% |
| **Branch principal** | `master` |
| **URL** | https://github.com/gothinkster/spring-boot-realworld-example-app |

---

## 2. Saúde do repositório

### Popularidade e adoção

| Métrica | Valor confirmado (junho 2026) | Interpretação |
|---|---|---|
| ⭐ Stars | 1.5k | Alto interesse da comunidade — posição relevante entre backends Java |
| 🍴 Forks | 935 | Amplamente usado como base de estudos e experimentos |
| 👁️ Watchers | 55 | Comunidade ativa o suficiente para monitoramento |
| 👥 Contributors | 14 | Equipe pequena com contribuições pontuais externas |
| 📝 Commits | 114 | Histórico enxuto — projeto estável, não em evolução ativa |

### Estado de manutenção

| Indicador | Status | Observação |
|---|---|---|
| Issues abertas | 0 | Pode indicar projeto inativo ou triagem desabilitada |
| Pull Requests abertas | 5 | PRs pendentes sem merge — baixa atividade de revisão |
| Releases formais | **Nenhuma** | Sem versionamento semântico publicado |
| CI rodando | ✅ GitHub Actions | Badge "Java CI" presente e funcional |

> **Interpretação para negócio:** o projeto está em modo de manutenção passiva. Não recebe novas funcionalidades, mas continua funcionando como material de referência. Não há SLA de suporte ou roadmap público.

---

## 3. O que o sistema faz hoje?

### Para o usuário da plataforma

**Gestão de conta**
- Cadastro com e-mail e senha
- Login com geração de token JWT
- Visualização e edição do próprio perfil (bio, foto via URL)

**Publicação de conteúdo**
- Criar, editar e deletar artigos
- Categorizar artigos com tags livres
- Favoritar artigos de outros usuários

**Interação social**
- Seguir e deixar de seguir outros usuários
- Feed personalizado com artigos de quem você segue
- Comentar em artigos e deletar os próprios comentários

**Descoberta de conteúdo**
- Listar artigos com filtro por tag, autor ou artigos favoritados
- Paginação de resultados
- Visualizar o perfil público de qualquer usuário

### O que o sistema não faz

| Capacidade | Status | Nota |
|---|---|---|
| Recuperação de senha | ❌ Não existe | Funcionalidade futura |
| Login via Google / GitHub (OAuth) | ❌ Não existe | Fora do escopo atual |
| Upload direto de imagem | ❌ Só aceita URL | Limitação de produto |
| Notificações | ❌ Não existe | Fora do escopo atual |
| Busca por texto nos artigos | ❌ Não existe | Requer infraestrutura adicional |
| Moderação / painel admin | ❌ Não existe | Fora do escopo atual |
| Múltiplos idiomas | ❌ Inglês apenas | Campos de API em inglês |
| Tempo estimado de leitura | ❌ Não existe | **Planejado — Fase 5** |
| Exclusão segura com histórico | ❌ Não existe | **Planejado — Fase 5** |

---

## 4. O projeto está pronto para uso real?

**Não, sem intervenções.** Três problemas bloqueiam qualquer uso em produção:

**🔴 Banco de dados inadequado**
O sistema usa SQLite — um banco de dados em arquivo local. Ele não suporta múltiplos usuários simultâneos com segurança. Para um ambiente real, precisa ser substituído por PostgreSQL ou MySQL.

**🔴 Chave de segurança exposta**
A chave secreta usada para autenticação JWT — e também o tempo de expiração das sessões — estão visíveis no repositório público. Qualquer pessoa pode usar essas informações para se passar por qualquer usuário do sistema.

**🔴 Framework sem suporte de segurança**
O Spring Boot 2.6.3 está sem suporte oficial desde novembro de 2023. Vulnerabilidades de segurança identificadas após essa data não recebem correção.

---

# ⚙️ PARTE 2 — VISÃO TÉCNICA
> *Para desenvolvedores, tech leads e arquitetos.*
> *Todos os dados confirmados diretamente do código-fonte no repositório.*

---

## 5. Stack tecnológica atual

### Linguagem e runtime

| Componente | Tecnologia | Versão | Status |
|---|---|---|---|
| Linguagem | Java | 11 (sourceCompatibility) | ⚠️ Suportada, mas mercado migrou para 17/21/25 |
| Framework principal | Spring Boot | 2.6.3 | 🔴 **EOL — sem suporte desde nov/2023** |
| Gerenciamento de dependências | Spring Dependency Management | 1.0.11.RELEASE | 🔴 Alinhado ao Spring Boot 2.x obsoleto |
| Build tool | Gradle (com Wrapper) | Não especificado no `build.gradle` | ✅ Padrão de mercado |
| Formatação de código | Spotless + Google Java Format | 6.2.1 | ✅ |
| Versão do artefato | — | 0.0.1-SNAPSHOT | ⚠️ Snapshot — sem release formal |

### Banco de dados e persistência

| Componente | Tecnologia | Versão | Status |
|---|---|---|---|
| Banco de dados | SQLite (arquivo `dev.db`) | sqlite-jdbc 3.36.0.3 | 🔴 **Não adequado para produção multi-usuário** |
| ORM / Mapper | MyBatis Spring Boot Starter | 2.2.2 | ⚠️ Será substituído por Spring Data JPA |
| Migrations | Flyway Core | Gerenciado pelo Spring BOM | ✅ Boas práticas — schema versionado |
| XML Mappers | `src/main/resources/mapper/*.xml` | — | ⚠️ SQL escrito manualmente por entidade |

> **Nota sobre SQLite:** o arquivo `dev.db` é deletado automaticamente ao rodar `./gradlew clean`. Aceitável para desenvolvimento individual, inaceitável para qualquer ambiente compartilhado.

### APIs e protocolos

| Componente | Tecnologia | Versão | Endpoint |
|---|---|---|---|
| REST API | Spring MVC (Web) | Spring Boot 2.6.3 BOM | `http://localhost:8080/*` |
| GraphQL API | Netflix DGS Framework | 4.9.21 | `http://localhost:8080/graphql` |
| GraphQL Codegen | DGS Codegen Plugin | 5.0.6 | Gera `io.spring.graphql.*` a partir do schema |
| HATEOAS | Spring HATEOAS | Spring Boot 2.6.3 BOM | Suporte a links hipermídia |
| Validação | Spring Boot Validation | Spring Boot 2.6.3 BOM | `@Valid`, `@NotBlank`, etc. |

> **Nota sobre DGS Codegen:** o pacote `io.spring.graphql` é **gerado automaticamente** pelo plugin DGS Codegen a partir do arquivo `src/main/resources/schema/schema.graphqls`. Este pacote não deve ser editado manualmente — qualquer alteração nos tipos GraphQL deve ser feita no schema e o código regenerado pelo build.

### Segurança

| Componente | Tecnologia | Versão | Status |
|---|---|---|---|
| Autenticação/Autorização | Spring Security | Spring Boot 2.6.3 BOM | ✅ Integrado com filtro JWT customizado |
| Tokens JWT | jjwt-api / jjwt-impl / jjwt-jackson | 0.11.2 | ✅ Geração e validação de tokens |
| Chave secreta JWT | `application.properties` hardcoded | — | 🔴 **Vulnerabilidade crítica** |
| Tempo de sessão JWT | `application.properties` hardcoded | — | 🔴 **Exposto no repositório público** |
| Criptografia | `javax.crypto.SecretKey` | Java 11 | ⚠️ Import `javax.*` — precisa migrar para `jakarta.*` |

### Bibliotecas utilitárias

| Biblioteca | Versão | Uso | Status |
|---|---|---|---|
| Lombok | compileOnly | Redução de boilerplate (`@Data`, `@AllArgsConstructor`, etc.) | ✅ |
| Joda-Time | 2.10.13 | Manipulação de datas em DTOs e cursores de paginação | ⚠️ Legado — substituir por `java.time` |

### Testes

| Biblioteca | Versão | Uso |
|---|---|---|
| Spring Boot Test | Spring Boot 2.6.3 BOM | Contexto de testes de integração |
| REST Assured | 4.5.1 | Testes de API REST (BDD-style) |
| REST Assured Spring Mock MVC | 4.5.1 | Testes de controllers sem servidor real |
| Spring Security Test | Spring Boot 2.6.3 BOM | Testes com contexto de segurança |
| MyBatis Test Starter | 2.2.2 | Testes de repositório com MyBatis |

---

## 6. Arquitetura de software

### Estilo arquitetural

O projeto adota uma arquitetura em camadas inspirada em **Domain-Driven Design (DDD)** com separação clara de responsabilidades. Utiliza o padrão **CQRS** (Command Query Responsibility Segregation) para separar operações de leitura e escrita.

### Estrutura de pacotes

```
src/main/java/io/spring/
├── api/              → Camada de entrada (Web Layer)
├── core/             → Domínio de negócio (entidades e serviços)
├── application/      → Casos de uso e queries (CQRS)
└── infrastructure/   → Implementações técnicas (MyBatis, JWT, etc.)

src/main/java/io/spring/graphql/   ← GERADO AUTOMATICAMENTE pelo DGS Codegen
                                      Não editar manualmente
```

### Descrição detalhada de cada camada

**`api` — Camada de entrada**

Responsabilidade: receber e responder requisições HTTP. Não contém lógica de negócio.

Controllers confirmados no repositório:
- `UsersApi.java` — Registro e login (`POST /users`, `POST /users/login`)
- `CurrentUserApi.java` — Perfil do usuário autenticado (`GET /user`, `PUT /user`)
- `ArticlesApi.java` — Listagem e criação de artigos
- `ArticleApi.java` — Operações em artigo específico (get, update, delete, favorite)
- `ProfileApi.java` — Perfil público e follow/unfollow (inferido do contrato)
- `CommentsApi.java` — Comentários por artigo (inferido do contrato)
- `TagsApi.java` — Listagem de tags (inferido do contrato)

**`core` — Domínio de negócio**

Responsabilidade: entidades de domínio, interfaces de repositório e serviços de domínio. Agnóstico de tecnologia — não conhece Spring, MyBatis ou banco de dados.

**`application` — Casos de uso (CQRS)**

Responsabilidade: orquestrar operações de alto nível. Produz DTOs formatados para retorno nas APIs.

DTOs confirmados no repositório:
- `ArticleData.java` — DTO de artigo completo, implementa interface `Node` para cursor pagination

**`infrastructure` — Implementações técnicas**

Responsabilidade: implementações concretas de todas as interfaces do `core`.

Classes confirmadas no repositório:
- `DefaultJwtService.java` — geração e validação de JWT, lê `jwt.secret` e `jwt.sessionTime`
- `MyBatisUserRepository.java` — implementação de repositório via MyBatis
- `UserMapper.java` — interface MyBatis Mapper para usuários
- `ArticleMapper.xml` — SQL de artigo mapeado em XML MyBatis

**`io.spring.graphql` — Código gerado (não editar)**

Responsabilidade: tipos Java gerados automaticamente pelo DGS Codegen a partir do `schema.graphqls`. Todo o conteúdo deste pacote é regenerado a cada build.

### Diagrama conceitual de camadas

```
┌─────────────────────────────────────────────────────────────┐
│                    ENTRADA (api)                             │
│  REST Controllers (Spring MVC) │ GraphQL Resolvers (DGS)    │
└───────────────────────┬─────────────────────────────────────┘
                        │ chama
┌───────────────────────▼─────────────────────────────────────┐
│               CASOS DE USO (application)                     │
│       Query Services │ DTOs │ Orquestração (CQRS)           │
└───────────┬───────────────────────┬─────────────────────────┘
            │ usa entidades         │ usa repositórios
┌───────────▼───────────┐ ┌────────▼───────────────────────────┐
│   DOMÍNIO (core)       │ │    INFRAESTRUTURA (infrastructure)  │
│  Entities │ Services  │ │  MyBatis Repos │ JWT │ Mappers XML  │
│  Repository Interfaces│ │  SQLite (dev.db)                    │
└───────────────────────┘ └────────────────────────────────────┘
                                    ↑
                         io.spring.graphql (código gerado DGS)
                         ─────────────────────────────────────
                         Gerado automaticamente — nunca editar
```

### Padrões de design utilizados

| Padrão | Onde é aplicado | Benefício |
|---|---|---|
| **DDD** | Separação em 4 camadas; entidades no `core` | Domínio isolado de tecnologia |
| **CQRS** | Camada `application` separa reads de writes | Queries otimizadas; menor acoplamento |
| **Data Mapper** | MyBatis via `infrastructure` | Separação entre objetos de domínio e SQL |
| **Repository Pattern** | Interfaces em `core`, impl. em `infrastructure` | Inversão de dependência; testabilidade |
| **DTO Pattern** | Objetos de resposta na camada `application` | API não expõe entidades internas |
| **Cursor Pagination** | DTOs implementam interface `Node` com `getCursor()` | Paginação consistente em REST e GraphQL |
| **Filter Chain** | Filtro JWT customizado no Spring Security | Autenticação stateless por token |

### Interface `Node` e cursor pagination

Os DTOs da camada `application` implementam a interface `io.spring.application.Node`, confirmada no `ArticleData.java`:

```java
// ArticleData.java (confirmado — 33 linhas)
@Data @NoArgsConstructor @AllArgsConstructor
public class ArticleData implements io.spring.application.Node {
    private String id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private boolean favorited;
    private int favoritesCount;
    private DateTime createdAt;   // Joda-Time — a ser migrado para java.time
    private DateTime updatedAt;   // Joda-Time — a ser migrado para java.time
    private List<String> tagList;
    @JsonProperty("author")
    private ProfileData profileData;

    @Override
    public DateTimeCursor getCursor() {
        return new DateTimeCursor(updatedAt);  // cursor baseado em updatedAt
    }
}
```

Esta interface habilita a paginação cursor-based usada no schema GraphQL (`first/after/last/before`).

### REST e GraphQL compartilham o mesmo domínio

O README documenta: *"REST or GraphQL is just a kind of adapter. And the domain layer will be consistent all the time."* Isso está correto no código — os controllers REST e os resolvers GraphQL delegam para a mesma camada `application`, que usa as mesmas entidades `core`.

---

## 7. Estrutura de código confirmada

### `DefaultJwtService.java` — confirmado linha a linha

```java
// Lê DUAS propriedades do application.properties
@Value("${jwt.secret}")      String secret      // EXPOSTO no repositório
@Value("${jwt.sessionTime}") int sessionTime    // EXPOSTO no repositório

// Usa javax.crypto — precisa migrar para jakarta.* no Spring Boot 4
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// Algoritmo: HMAC-SHA512
signatureAlgorithm = SignatureAlgorithm.HS512;
```

### `ArticleData.java` — campos confirmados

```
id             String
slug           String
title          String
description    String
body           String
favorited      boolean
favoritesCount int
createdAt      DateTime (Joda-Time)
updatedAt      DateTime (Joda-Time) — usado como cursor de paginação
tagList        List<String>
profileData    ProfileData (@JsonProperty("author") — serializado como "author")
```

Implementa `io.spring.application.Node` com `getCursor()` retornando `DateTimeCursor(updatedAt)`.

### `schema.graphqls` — estrutura confirmada (177 linhas)

**Queries (6):** `article`, `articles`, `me`, `feed`, `profile`, `tags`

**Mutations (12):** `createUser`, `login`, `updateUser`, `followUser`, `unfollowUser`, `createArticle`, `updateArticle`, `favoriteArticle`, `unfavoriteArticle`, `deleteArticle`, `addComment`, `deleteComment`

**Tipos de domínio:**
- `Article` — inclui sub-campo `comments(first, after, last, before): CommentsConnection`
- `Profile` — inclui sub-campos `articles`, `favorites`, `feed` (todos com cursor pagination)
- `User` — inclui campo `profile: Profile!`
- `Comment` — inclui back-reference `article: Article!`
- `DeletionStatus { success: Boolean! }` — compartilhado por `deleteArticle` e `deleteComment`
- `union UserResult = UserPayload | Error` — apenas `createUser` retorna este union

**Campos planejados (não existem ainda):**
- `Article.readingTimeMinutes: Int` — será adicionado na Fase 5 (INI-13)

### Recursos confirmados

| Arquivo | Localização | Conteúdo |
|---|---|---|
| Schema GraphQL | `src/main/resources/schema/schema.graphqls` | 177 linhas — definição completa |
| Mapper XML de artigos | `src/main/resources/mapper/ArticleMapper.xml` | SQL MyBatis para artigos |
| CI workflow | `.github/workflows/` | GitHub Actions "Java CI" |

---

## 8. Segurança

### Fluxo de autenticação

```
1. Cliente envia credenciais → POST /users/login
2. Servidor valida password (BCrypt via Spring Security)
3. Servidor gera JWT assinado com HS512 usando jwt.secret
4. Token tem expiração configurada em jwt.sessionTime segundos
5. Cliente armazena JWT e envia em cada requisição:
   Authorization: Token <jwt_value>
6. Filtro JWT customizado intercepta a requisição
7. Spring Security popula SecurityContext
8. Controller recebe usuário via @AuthenticationPrincipal
```

### Configuração de segurança

| Aspecto | Configuração atual | Avaliação |
|---|---|---|
| Hash de senha | BCrypt via Spring Security | ✅ Padrão seguro |
| Algoritmo JWT | HMAC-SHA512 | ✅ Algoritmo robusto |
| `jwt.secret` | Hardcoded em `application.properties` | 🔴 **Crítico — exposto publicamente** |
| `jwt.sessionTime` | Hardcoded em `application.properties` | 🔴 **Exposto — não configurável por ambiente** |
| Imports de criptografia | `javax.crypto.SecretKey`, `javax.crypto.spec.SecretKeySpec` | ⚠️ `javax.*` — precisa migrar para `jakarta.*` no Spring Boot 4 |
| HTTPS | Não configurado na aplicação | ⚠️ Responsabilidade da camada de proxy |
| CORS | Não documentado explicitamente | ⚠️ Pode causar bloqueios em integrações |
| Rate limiting | Não implementado | ⚠️ Sem proteção contra brute force |

### Vulnerabilidade crítica — duas propriedades JWT expostas

O arquivo `application.properties` contém no repositório público:

```properties
jwt.secret=mySecretKey       # ← chave criptográfica — permite falsificar tokens
jwt.sessionTime=86400        # ← tempo de sessão — exposto desnecessariamente
```

Ambas precisam ser movidas para variáveis de ambiente. A estratégia de migração:
- `JWT_SECRET`: sem fallback — fail fast (aplicação não sobe sem a variável)
- `JWT_SESSION_TIME`: com fallback de 86400 — não falha se ausente

Ver ADR-006 em `06-architecture-decisions.md` para a decisão completa.

---

## 9. Testes e qualidade

### Cobertura de testes

| Tipo | Ferramenta | O que cobre |
|---|---|---|
| Testes de API (integração) | REST Assured + Spring MockMvc | Endpoints REST com contexto Spring |
| Testes de repositório | MyBatis Test + Spring Boot Test | Operações de banco isoladas |
| Testes de segurança | Spring Security Test | Contextos autenticados/não autenticados |

### Qualidade de código

| Prática | Ferramenta | Status |
|---|---|---|
| Formatação automática | Spotless + Google Java Format | ✅ Configurado no `build.gradle` |
| Análise estática | Não configurada | ⚠️ Ausente (SonarQube, SpotBugs, PMD) |
| Cobertura de testes (métricas) | Não configurada | ⚠️ Ausente (JaCoCo) |
| Testes de mutação | Não configurados | ⚠️ Ausente (Pitest) |
| Testes E2E | Não configurados | ⚠️ Ausente (Playwright) |

### Comandos de qualidade

```bash
./gradlew test               # executar todos os testes
./gradlew spotlessJavaApply  # aplicar formatação automática
./gradlew spotlessCheck      # verificar formatação sem aplicar
```

---

## 10. CI/CD e operação

### Pipeline de CI

- **Plataforma:** GitHub Actions
- **Workflow:** `.github/workflows/` — executa build e testes a cada push
- **Badge:** `Java CI` — indica status do último build
- **Cobertura de deploy:** nenhuma — o pipeline apenas valida, não entrega

### Execução local

```bash
# Pré-requisito: Java 11 instalado

# Iniciar aplicação
./gradlew bootRun

# Verificar funcionamento
curl http://localhost:8080/tags

# Porta padrão
http://localhost:8080
```

### Docker

```bash
# Build da imagem
./gradlew bootBuildImage --imageName spring-boot-realworld-example-app

# Executar container
docker run -p 8081:8080 spring-boot-realworld-example-app

# Acesso via Docker
http://localhost:8081
```

### Configuração

| Arquivo | Localização | Conteúdo |
|---|---|---|
| `application.properties` | `src/main/resources/` | String de conexão SQLite, `jwt.secret`, `jwt.sessionTime`, porta |
| Schema GraphQL | `src/main/resources/schema/schema.graphqls` | Definição completa do schema (177 linhas) |
| Migrations Flyway | `src/main/resources/db/migration/` | Scripts SQL versionados |
| Mappers MyBatis | `src/main/resources/mapper/*.xml` | SQL por entidade em XML |

### Ausências operacionais

| Capacidade | Status | Impacto |
|---|---|---|
| Logging estruturado | Não configurado | Dificulta observabilidade em produção |
| Health checks | Não configurado | Necessário para orquestradores |
| Métricas (Prometheus/Micrometer) | Não configurado | Sem observabilidade de negócio |
| Tracing distribuído | Não configurado | Sem rastreabilidade de requisições |
| Perfis por ambiente | Não configurado | Sem separação dev/staging/prod |
| CD pipeline | Não existe | Deploy manual |

---

## 11. Riscos e dívida técnica

### Riscos críticos 🔴

#### Spring Boot 2.6.x — Fim de vida

| Item | Detalhe |
|---|---|
| **Problema** | Spring Boot 2.x teve suporte encerrado em novembro de 2023 |
| **Impacto** | Sem patches de segurança; CVEs conhecidas não serão corrigidas pelo fornecedor |
| **Mitigação** | Migrar para Spring Boot 4.0.6 (requer Java 17+ e ajustes de breaking changes) |
| **Iniciativa** | INI-04 / EPIC-04 |

#### JWT secret e sessionTime hardcoded

| Item | Detalhe |
|---|---|
| **Problema** | `jwt.secret` e `jwt.sessionTime` expostos no repositório público em `application.properties` |
| **Impacto** | Qualquer pessoa pode gerar tokens JWT válidos e definir sessões arbitrárias |
| **Mitigação** | Mover para variáveis de ambiente `JWT_SECRET` e `JWT_SESSION_TIME` |
| **Iniciativa** | INI-02 / EPIC-02 / ADR-006 |

#### SQLite não adequado para produção

| Item | Detalhe |
|---|---|
| **Problema** | Banco de dados em arquivo local sem suporte a concorrência real de escrita |
| **Impacto** | Falhas ou corrupção com múltiplos usuários simultâneos |
| **Mitigação** | Substituir por PostgreSQL via Docker Compose |
| **Iniciativa** | INI-03 / EPIC-03 |

### Riscos médios ⚠️

#### Java 11 — Versão desatualizada

| Item | Detalhe |
|---|---|
| **Problema** | Java 11 LTS perde suporte gratuito Oracle em setembro de 2026 |
| **Impacto** | Incompatibilidade crescente com bibliotecas modernas; sem acesso a recursos de linguagem novos |
| **Mitigação** | Upgrade para Java 25 (LTS mais recente, suporte até 2030) |
| **Iniciativa** | INI-04 / EPIC-04 |

#### Imports `javax.crypto` no `DefaultJwtService`

| Item | Detalhe |
|---|---|
| **Problema** | `DefaultJwtService.java` usa `javax.crypto.SecretKey` e `javax.crypto.spec.SecretKeySpec` |
| **Impacto** | Esses imports são `javax.*` — não compilam sem ajuste após migração para Spring Boot 4/Jakarta EE |
| **Mitigação** | Migrar para `jakarta.crypto.*` durante INI-04 (junto com todos os `javax.*`) |
| **Iniciativa** | INI-04 / EPIC-04, US-04.04 |

#### MyBatis com XML mappers manuais

| Item | Detalhe |
|---|---|
| **Problema** | Cada operação de banco exige interface mapper + XML + implementação de repositório manual |
| **Impacto** | Alto custo de manutenção; incompatível com `@Where` (soft delete) e Testcontainers nativo |
| **Mitigação** | Migrar para Spring Data JPA |
| **Iniciativa** | INI-05 / EPIC-05 |

#### Joda-Time em DTOs

| Item | Detalhe |
|---|---|
| **Problema** | `ArticleData.java` e `DateTimeCursor.java` usam `Joda DateTime` em vez de `java.time` |
| **Impacto** | Dependência de biblioteca legada; bloqueador para conversão de `ArticleData` para record Java 25 |
| **Mitigação** | Substituir por `java.time.Instant` durante INI-04; necessário antes de INI-06 (records) |
| **Iniciativa** | INI-04 / EPIC-04, US-04.07 / ADR-005 |

### Débitos menores ℹ️

| Item | Impacto | Iniciativa |
|---|---|---|
| Sem testes de mutação (Pitest) | Qualidade de testes não verificável | INI-07 / EPIC-07 |
| Sem JaCoCo | Cobertura de testes não medida | INI-07 |
| Sem análise estática | Bugs e code smells não detectados automaticamente | INI-07 |
| Sem OpenAPI/Swagger | Contrato da API não documentado formalmente | INI-11 / EPIC-11 |
| Sem Spring Actuator | Sem health checks para orquestradores | INI-03 / EPIC-03 |
| Sem perfis de ambiente | Sem separação dev/staging/prod | INI-02 / EPIC-02 |
| 5 PRs abertas sem revisão | Contribuições externas ignoradas; projeto parece abandonado | INI-01 / EPIC-01 |
| Sem releases / versionamento | Dependências externas sem versão estável | Processo |
| `io.spring.graphql` não documentado como código gerado | Risk de edição manual que será sobrescrita pelo build | ADR-004 |

---

## 12. Avaliação de maturidade

| Dimensão | Pontuação | Justificativa |
|---|---|---|
| 🏗️ Qualidade arquitetural | 4/5 | DDD + CQRS + separação clara de camadas; REST + GraphQL no mesmo domínio |
| 🧪 Cobertura de testes | 3/5 | Testes de API e repositório existem; sem mutação, sem E2E, sem métricas de cobertura |
| 🚀 Prontidão para produção | 1/5 | SQLite + JWT secrets expostos + Spring Boot EOL inviabilizam uso imediato |
| 🔒 Segurança | 1/5 | `jwt.secret` E `jwt.sessionTime` expostos; `javax.crypto` a migrar; sem rate limiting |
| 🔄 Atualização tecnológica | 1/5 | Spring Boot 2.x EOL; Java 11 com prazo; Joda-Time legado; MyBatis a migrar |
| 📚 Documentação | 3/5 | README funcional; sem ADRs, sem API docs, sem OpenAPI |
| 👥 Atividade da comunidade | 3/5 | Alta popularidade (1.5k stars, 935 forks); baixa atividade de manutenção recente |

```
Uso educacional / referência:  ████████████████████  EXCELENTE
Base para novos projetos:      ██████████░░░░░░░░░░  REQUER UPGRADES ANTES
Produção sem modificações:     ████░░░░░░░░░░░░░░░░  NÃO RECOMENDADO
```

---

## 13. Visão de negócio — o que o sistema entrega

### Mapa de capacidades

| Capacidade | Status | Observação |
|---|---|---|
| Cadastro e login (e-mail/senha) | ✅ Completo | Sem OAuth/SSO |
| Perfil de usuário | ✅ Completo | Foto por URL; sem upload direto |
| Rede social (follow/unfollow) | ✅ Completo | Sem notificações |
| Publicação de artigos | ✅ Completo | Sem rich text nativo |
| Categorização por tags | ✅ Completo | Tags livres, sem hierarquia |
| Comentários e favoritos | ✅ Completo | Sem reações ou threaded comments |
| Feed personalizado | ✅ Completo | Baseado em follows |
| Busca e filtragem | ⚠️ Parcial | Por tag, autor, favoritos; sem full-text search |
| Tempo de leitura | ❌ Ausente | **Planejado — Fase 5** |
| Exclusão segura (soft delete) | ❌ Ausente | **Planejado — Fase 5** |
| Moderação de conteúdo | ❌ Ausente | Fora do escopo |
| Analytics de uso | ❌ Ausente | Fora do escopo |

### Limitações de escala

| Limitação | Causa técnica | Threshold estimado |
|---|---|---|
| Usuários simultâneos | SQLite sem suporte a concorrência de escrita | < 10 usuários simultâneos com segurança |
| Volume de dados | SQLite em arquivo local | Dezenas de MB antes de degradação |
| Disponibilidade | Sem clustering, sem replicação | Single point of failure |
| Performance | Sem cache (Redis, etc.) | Degrada com crescimento do dataset |

---

## 14. Gap analysis — o que falta para produção

### Gaps críticos (bloqueadores)

| Gap | Esforço | Iniciativa | Fase |
|---|---|---|---|
| Substituir SQLite por PostgreSQL | Médio | INI-03 / EPIC-03 | Fase 1 |
| Mover `jwt.secret` para variável de ambiente | Baixo | INI-02 / EPIC-02 | Fase 1 |
| Mover `jwt.sessionTime` para variável de ambiente | Baixo | INI-02 / EPIC-02 | Fase 1 |
| Upgrade Spring Boot 2.x → 4.0.6 + Java 25 | Alto | INI-04 / EPIC-04 | Fase 3 |
| Configurar HTTPS / TLS | Baixo (infra) | Fora do escopo | Após M6 |

### Gaps de modernização técnica

| Gap | Esforço | Iniciativa | Fase |
|---|---|---|---|
| Migrar MyBatis → Spring Data JPA | Médio | INI-05 / EPIC-05 | Fase 3 |
| Migrar `javax.*` → `jakarta.*` (incluindo `javax.crypto`) | Médio | INI-04 / EPIC-04 | Fase 3 |
| Remover Joda-Time; substituir por `java.time` | Baixo | INI-04 / EPIC-04 | Fase 3 |
| Introduzir record types (incluindo `ArticleData` com `getCursor()`) | Baixo | INI-06 / EPIC-06 | Fase 3 |
| Upgrade Gradle 9.3.1 sem deprecation warnings | Baixo | INI-04 / EPIC-04 | Fase 3 |

### Gaps de qualidade

| Gap | Esforço | Iniciativa | Fase |
|---|---|---|---|
| Testes de mutação — Pitest 95% | Médio | INI-07 / EPIC-07 | Fase Testes |
| Testes de integração de contrato REST/GraphQL | Médio | INI-08 / EPIC-08 | Fase Testes |
| Testes E2E com Playwright | Médio | INI-09 / EPIC-09 | Fase Testes |
| Análise estática (SonarQube/SpotBugs) | Baixo | INI-07 | Fase Testes |

### Gaps operacionais

| Gap | Esforço | Iniciativa | Fase |
|---|---|---|---|
| Logging estruturado (JSON) | Baixo | INI-10 / EPIC-10 | Fase 4 |
| Health checks (Spring Actuator) | Baixo | INI-03 / EPIC-03 | Fase 1 |
| Métricas por endpoint (Micrometer + Prometheus) | Médio | INI-10 / EPIC-10 | Fase 4 |
| Traces distribuídos (OpenTelemetry + Tempo) | Médio | INI-10 / EPIC-10 | Fase 4 |
| Perfis de ambiente (dev/staging/prod) | Baixo | INI-02 / EPIC-02 | Fase 1 |

### Gaps de produto

| Gap | Esforço | Iniciativa | Fase |
|---|---|---|---|
| Documentação OpenAPI / Swagger | Baixo | INI-11 / EPIC-11 | Fase 4 |
| Soft delete com `is_deleted` | Médio | INI-12 / EPIC-12 | Fase 5 |
| Tempo de leitura (200 wpm) + cache lazy | Médio | INI-13 / EPIC-13 | Fase 5 |
| Recuperação de senha | Médio | Fora do escopo | Após M6 |
| Rate limiting em autenticação | Médio | Fora do escopo | Após M6 |

---

## Rastreabilidade com demais documentos

| Este documento | Documento relacionado | Conexão |
|---|---|---|
| Stack tecnológica atual | `04-roadmap.md` Fase 3 | Stack alvo da modernização |
| Vulnerabilidade JWT (secret + sessionTime) | `06-architecture-decisions.md` ADR-006 | Decisão de migração para env vars |
| Pacote `io.spring.graphql` gerado | `06-architecture-decisions.md` ADR-004 | Exclusão do Pitest, nunca editar |
| Interface `Node` em `ArticleData` | `06-architecture-decisions.md` ADR-005 | Migração para record com `getCursor()` explícito |
| Schema GraphQL confirmado | `API-mapping.md` | Mapeamento completo de operações |
| Gaps identificados | `05-backlog.md` | Épicos endereçando cada gap |

---

*Versão 2.0 — Análise confirmada contra o repositório GitHub em Junho de 2026*  
*Dados verificados linha a linha: `build.gradle`, `DefaultJwtService.java`, `ArticleData.java`, `schema.graphqls`*  
*Próxima revisão recomendada: ao encerrar a Fase 1 para registrar o estado pós-migração*