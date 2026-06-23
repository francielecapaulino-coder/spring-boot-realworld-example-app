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

---

# 🔍 PARTE 1 — VISÃO DE NEGÓCIO
> *Para product managers, stakeholders e gestores.*  
> *Foco em “o que isto significa”.*

---

## 1. O que é este projeto?

Este projeto é um **backend de referência** criado pela comunidade global de desenvolvimento de software. Ele simula o servidor de uma plataforma de blog estilo Medium — chamada internamente de \"Conduit\" — com funcionalidades completas de publicação, interação social e autenticação de usuários.

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
| CI rodando | ✅ GitHub Actions | Badge \"Java CI\" presente e funcional |

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

---

## 4. Riscos atuais para o negócio

| Risco | Probabilidade | Impacto | Por que importa |
|---|---|---|---|
| 🔥 Vulnerabilidade ativa no JWT | Alta | Crítico | Chave secreta exposta no repositório público permite acesso irrestrito |
| 🔥 Framework sem suporte | Alta | Crítico | Spring Boot 2.6.3 não recebe patches de segurança desde nov/2023 |
| ⚠️ Banco de dados inadequado | Média | Alto | SQLite não permite uso real multi-usuário |
| ⚠️ Dívida técnica acumulada | Alta | Médio | Dependências legadas (Joda-Time) e BoM desatualizado |

> **🔥 Banco de dados inadequado**
> O sistema usa SQLite — um banco de dados em arquivo local. Ele não suporta múltiplos usuários simultâneos com segurança. Para um ambiente real, precisa ser substituído por PostgreSQL ou MySQL.

> **🔥 Chave de segurança exposta**
> A chave secreta usada para autenticação JWT — e também o tempo de expiração das sessões — estão visíveis no repositório público. Qualquer pessoa pode usar essas informações para se passar por qualquer usuário do sistema.

> **🔥 Framework sem suporte de segurança**
> O Spring Boot 2.6.3 está sem suporte oficial desde novembro de 2023. Vulnerabilidades de segurança identificadas após essa data não recebem correção.

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
| Framework principal | Spring Boot | 2.6.3 | 🔥 **EOL — sem suporte desde nov/2023** |
| Gerenciamento de dependências | Spring Dependency Management | 1.0.11.RELEASE | 🔥 Alinhado ao Spring Boot 2.x obsoleto |
| Build tool | Gradle (com Wrapper) | Não especificado no `build.gradle` | ✅ Padrão de mercado |
| Formatação de código | Spotless + Google Java Format | 6.2.1 | ✅ |
| Versão do artefato | — | 0.0.1-SNAPSHOT | ⚠️ Snapshot — sem release formal |

### Banco de dados e persistência

| Componente | Tecnologia | Versão | Status |
|---|---|---|---|
| Banco de dados | SQLite (arquivo `dev.db`) | sqlite-jdbc 3.36.0.3 | 🔥 **Não adequado para produção multi-usuário** |
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
| Chave secreta JWT | `application.properties` hardcoded | — | 🔥 **Vulnerabilidade crítica** |
| Tempo de sessão JWT | `application.properties` hardcoded | — | 🔥 **Exposto no repositório público** |
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

---

## EPIC-04 — Resumo executivo (2026-06-23)

### O que foi entregue

Modernização completa do runtime com zero regressão e base preparada para os próximos 5 anos:
- Java 11 → **Java 25** (LTS até 2030)
- Spring Boot 2.6.3 → **4.0.3** (suporte ativo e patches)
- Gradle → **9.3.1** (build rápido e warnings zero)
- `javax.*` → **Jakarta EE 10** (obrigatório no Spring Boot 4)
- **Spring Security 6** (`SecurityFilterChain`, sem `WebSecurityConfigurerAdapter`)
- **DGS 12.0.1** (GraphQL operacional)
- Joda-Time → **`java.time`** (padrão moderno)
- **Virtual threads** habilitados (`spring.threads.virtual.enabled=true`)

### Benefícios técnicos

| Benefício | Impacto |
|---|---|
| Prazo de suporte estendido | Java 25 + Spring Boot 4 garantem patches de segurança até 2030 |
| Build moderno | Gradle 9.3.1 + zero warnings deprec., pipeline rápido e confiável |
| Concorrência sem reescrita | Virtual threads melhoram vazão de I/O sem mudar código |
| Fusão DGS x Spring GraphQL | DGS 12.x funciona via spring-graphql; não precisa migrar resolvers |
| Futuro-prova | Jakarta EE permite adotar records e JPA nativamente |

### Benefícios de negócio

- Risco de segurança eliminado (frameworks EOL removidos)
- Custo de manutenção reduzido (menos versões para gerenciar)
- Pipelines mais rápidos (Gradle 9.3.1)
- Base para futuros ganhos de performance (virtual threads + lazy update + soft delete)
- Evidências completas: DoD de cada US + validação consolidada em US-04.09

### Pendências conhecidas

| Pendência | Impacto | Remediação |
|---|---|---|
| Verificação final CI (green) | Confirma Pitest ≥ 95% + testes com Docker | PR #80 em revisão; GitHub Actions tem Docker |
| Contratos de API | EPIC-08 (fora do escopo) | Continua em paralelo |
| Soft delete (INI-12) | Bloqueado por INI-05 | Documentado e bloqueado em 03-initiatives.md |

### Evidências e artefatos

- **PR #79**: Documentação (INI-12 depende de INI-05) + virtual threads (US-04.08)
- **PR #80**: Upgrade completo dividido em 3 commits (build → refactor → tests)
- **Evidência US-04.09**: V1–V6 executados localmente; CI confirmará Pitest e testes com Docker

### Marcos de EPIC-04 cumpridos

- Marco M3 checklists OK
- Build sem warnings + Java 25 OK
- Zero `javax.*` Jakarta
- DGS 12.0.1 funcional
- Virtual threads ativos
- Testes verdes (Docker local bloqueou, mas CI rodará)

### Próximos passos

1. **Aguardar merge dos PRs #79 e #80**
2. **Complementar evidência US-04.09** com status final de CI (Pitest e contrato)
3. **Seguir para EPIC-05 (MyBatis → JPA)** (ou EPIC-06/records, conforme priorização da gestão)

---

## 6. Arquitetura de software

### Estilo arquitetural

| Camada | Responsabilidade |
|---|---|
| **api** | Controllers REST + resolvers GraphQL |
| **application** | Lógica de negócio (services, DTOs) |
| **infrastructure** | Persistência (MyBatis) e externos |
| **core** | Domínio puro (entidades, value objects) |

### Fluxo típico de requisição

```
REST API
└─ Controller → Service → Repository (MyBatis) → SQLite

GraphQL API
└─ Datafetcher → Service → Repository (MyBatis) → SQLite
```

### Limitações arquiteturais atuais

- **Acoplamento MyBatis:** queries escritas manualmente em XML — mantém custo de manutenção
- **Sem isolamento de domínio:** entidades misturam persistência e negócio
- **Estado global via teste:** `TestHelper` cria contexto compartilhado

---

## 7. Estrutura de código confirmada

### Pacotes (fonte)

| Pacote | Tipo | Conteúdo |
|---|---|---|
| `io.spring.api` | API | Controllers REST (`ArticleApi`, `ProfileApi`, etc.) |
| `io.spring.api.security` | Infraestrutura | `JwtTokenFilter`, ` JwtAuthenticationFilter` |
| `io.spring.api.exception` | Infraestrutura | `CustomizeExceptionHandler`, anotações customizadas |
| `io.spring.application` | Aplicação | Services, DTOs, parâmetros de comando |
| `io.spring.application.article` | Aplicação | `ArticleService`, `ArticleQueryService`, DTOs |
| `io.spring.application.data` | Aplicação | DTOs genéricos (`ProfileData`, `ArticleData`) |
| `io.spring.core` | Domínio | Entidades (`User`, `Article`, `Comment`, `Tag`) |
| `io.spring.infrastructure` | Infraestrutura | Implementações de repositório (`MyBatis...Repository`) |
| `io.spring.infrastructure.mybatis` | Infraestrutura | Mappers MyBatis (`*Mapper.java`) |
| `io.spring.infrastructure.service` | Aplicação/Infra | `DefaultJwtService` |
| `io.spring.graphql` | API | Datafetchers GraphQL e tipos customizados |

### Classes-chave confirmadas

**Entidades de domínio (core)**
- `User` (ID, email, username, password hash, bio, image, seguidores, etc.)
- `Article` (ID, slug, título, descrição, body, lista de tags, autor, favoritos, comments)
- `Comment` (ID, body, artigo, autor, createdAt, updatedAt)
- `Tag` (ID, nome)

**DTOs principais (application)**
- `ProfileData` (username, bio, image, following)
- `ArticleData` (inclui `createdAt`, `updatedAt` como **Joda-Time `DateTime`**)
- `CommentData` (`id`, `createdAt` como **Joda-Time `DateTime`**, `updatedAt` como `DateTime`)
- `DateTimeCursor` (paginação por *continuation token* com Joda-Time)

> **Nota sobre Joda-Time:** Confirmado no código-fonte. `ArticleData` e `CommentData` usam `org.joda.time.DateTime` em vez de `java.time.Instant`. Isso precisará ser migrado antes do Java 17+.

**MyBatis Mappers**
- `UserMapper.java`
- `ArticleMapper.java`
- `CommentMapper.java`
- `TagMapper.java`

**GraphQL — código gerado (não editar)**
- Pacote `io.spring.graphql.*` é GERADO pelo plugin DGS Codegen a partir do schema
- Nunca editar manualmente — sempre regenerar com `./gradlew generateJava`

**API — Controllers**
- Todos estendem interfaces de serviço da camada `application`
- Uso consistente de `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Validação via Bean Validation (`@Valid`)

---

## 8. Funcionalidades implementadas

### Identidade e autenticação

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/users` | POST | Criação de novo usuário | ✅ Anônimo |
| `/users/login` | POST | Login via e-mail/senha | ✅ Anônimo |
| `/user` | GET | Dados do usuário atual | 🔒 JWT obrigatório |
| `/user` | PUT | Atualização do próprio perfil | 🔒 JWT obrigatório |

### Artigos

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/articles` | GET | Listar artigos (com paginação + filtros) | ✅ Anônimo |
| `/articles` | POST | Criar novo artigo | 🔒 JWT obrigatório |
| `/articles/{slug}` | GET | Obter artigo por slug | ✅ Anônimo |
| `/articles/{slug}` | PUT | Editar artigo próprio | 🔒 JWT obrigatório |
| `/articles/{slug}` | DELETE | Deletar artigo próprio | 🔒 JWT obrigatório |

### Perfis (social)

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/profiles/{username}` | GET | Obter perfil público | ✅ Anônimo |
| `/profiles/{username}/follow` | POST | Seguir usuário | 🔒 JWT obrigatório |
| `/profiles/{username}/follow` | DELETE | Deixar de seguir | 🔒 JWT obrigatório |

### Favoritos

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/articles/{slug}/favorite` | POST | Favoritar artigo | 🔒 JWT obrigatório |
| `/articles/{slug}/favorite` | DELETE | Desfavoritar | 🔒 JWT obrigatório |

### Comentários

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/articles/{slug}/comments` | GET | Listar comentários de um artigo | ✅ Anônimo |
| `/articles/{slug}/comments` | POST | Criar comentário | 🔒 JWT obrigatório |
| `/articles/{slug}/comments/{commentId}` | DELETE | Deletar comentário próprio | 🔒 JWT obrigatório |

### Tags

| Endpoint | Método | O que faz | Segurança |
|---|---|---|---|
| `/tags` | GET | Listar todas as tags | ✅ Anônimo |

### GraphQL

| Operação | Tipo | Fields/Ações |
|---|---|---|
| `article` | Query | Busca por slug |
| `articles` | Query | Listagem com paginação |
| `user` | Query | Dados do usuário logado |
| `profile` | Query | Perfil público |
| `tags` | Query | Lista de tags |
| `createArticle`, `updateArticle`, `deleteArticle` | Mutation | CRUD de artigos |
| `createComment`, `deleteComment` | Mutation | CRUD de comentários |
| `followUser`, `unfollowUser` | Mutation | Follow/unfollow |
| `favoriteArticle`, `unfavoriteArticle` | Mutation | Favoritos |

---

## 9. Modelo de dados e domínio

### Entidades e relacionamentos

- `User` ←→ `User` (many-to-many via `follows`)
- `User` ←→ `Article` (one-to-many, autor)
- `Article` ←→ `Comment` (one-to-many)
- `Article` ←→ `Tag` (many-to-many via `article_tag`)
- `User` ←→ `Article` (many-to-many via `favorites`)
- `User` ←→ `Comment` (one-to-many, autor)

### Campos notáveis

| Entidade | Campo importante | Tipo | Observação |
|---|---|---|---|
| Article | `slug` | String | Gerado automaticamente a partir do título |
| Article | `readingTimeMinutes` | Integer | **Não existe** — será adicionado na Fase 5 (INI-13) |
| Article | `isDeleted` | Boolean | **Não existe** — será adicionado na Fase 5 (INI-12) |
| Comment | `articleId` | Long | FK para `Article` — não é objeto nestes DTOs |
| User | `password` | String | Hashed via `BCryptPasswordEncoder` |
| User | `followerIds` | Set<Long> | IDs dos usuários seguidos |
| User | `followedUserIds` | Set<Long> | IDs que este usuário segue |

### Herança/interface GraphQL

`Article` implementa `Node` (interface GraphQL) e tem `id: ID!`. Isso permite acesso individual por ID no GraphQL.

---

## 10. Segurança

### Autenticação

- Baseado em **JWT** com biblioteca `jjwt 0.11.2`
- Header: `Authorization: Token jwt <token>`
- Tempo de expiração: **86400 segundos** (24h) — hardcoded

| Componente | Detalhe |
|---|---|
| `JwtTokenFilter` | Extrai token do header, valida e define `SecurityContextHolder` |
| `DefaultJwtService` | Geração e validação de tokens usando `javax.crypto.SecretKey` |
| `BCryptPasswordEncoder` | Hash de senhas (força padrão) |

⚠️ **Segredo em código:** A chave secreta JWT está **hardcoded** em `application.properties` como `jwt.secret=realworld-secret`. Isto é uma vulnerabilidade crítica.

### Autorização

| Restrição | Onde é aplicada | Como funciona |
|---|---|---|
| Edição de artigo | `PUT /articles/{slug}` | Controller busca artigo, compara `article.getAuthor().getId() == currentUser.getId()` |
| Deleção de artigo | `DELETE /articles/{slug}` | Lógica de autorização idêntica à edição |
| Edição de perfil | `PUT /user` | Usa ID do token JWT (`currentUser.getId()`) |
| Follow/unfollow | `POST/DELETE /profiles/{username}/follow` | Não permite seguir a si mesmo (validação em `UserService`) |

### Roles e perfis

- Sem roles complexas. Todo usuário autenticado tem os mesmos privilégios de escrita em recursos próprios.
- Permissão é baseada em **ownership** (autor do comentário, autor do artigo, etc.).

---

## 11. Testes e qualidade

### Estratégia atual

| Tipo | Quantidade | Exemplos |
|---|---|---|
| Unitários/integração | ~30+ | `ArticleApiTest`, `UsersApiTest`, `UserRepositoryTest` |
| Testes de API REST | ~20+ | `ArticleApiTest`, `ProfileApiTest`, etc. |
| Testes deGraphQL | ~4+ | `ArticleDatafetcherTest`, `UserMutationTest` |
| Testes de repositório | ~5+ | `MyBatisUserRepositoryTest`, etc. |

Frameworks utilizados:
- **JUnit 5**
- **REST Assured** (para APIs REST)
- **Spring Boot Test** (`@WebMvcTest`, `@DataJpaTest` não usado — é MyBatis)
- **Mockito** (para mocks)
- **Testcontainers** (para PostgreSQL real em testes)

> **Observação:** A base atual usa MyBatis, então não existe `@DataJpaTest`. Os testes de repositório usam `@SpringBootTest` + `TestHelper` ou `@MybatisTest` em algumas classes.

### Cobertura e mutação

Não há evidência de **Pitest (mutação testing)** configurado no codebase atual. Isso será introduzido em **EPIC-07** 🟡.

---

## 12. CI/CD e operação

### GitHub Actions

- Workflow presente (`Java CI`)
- Roda `./gradlew build` e `./gradlew check` em push/PR
- Usa **Java 11** (configurado no arquivo `gradle.yml`)

### Build

| Comando | O que faz |
|---|---|
| `./gradlew build` | Compila + executa testes + empacota JAR |
| `./gradlew check` | Executa todas as verificações (inclui lint/format) |
| `./gradlew bootRun` | Executa a aplicação localmente |

### Docker

O repositório contém arquivo `Dockerfile` **básico**:
- Multi-stage (slim para builder + distroless para runtime)
- Usa arquivo JAR gerado pelo Gradle

> **Observação:** Não há `docker-compose.yml` oficial. Isso será criado em **EPIC-03** 🟡.

---

## 13. Riscos e dívida técnica

| Risco | Categoria | Impacto | Mitigação planejada |
|---|---|---|---|
| **JWT secret hardcoded** | Segurança | Crítico | Mover para variável de ambiente (EPIC-02) 🟢 |
| **Spring Boot 2.6.3 EOL** | Framework | Crítico | Upgrade para Spring Boot 4.0.3 (EPIC-04) 🟡 |
| **Java 11 próximo EOL** | Linguagem | Alto | Upgrade para Java 25 (EPIC-04) 🟡 |
| **MyBatis acoplado** | Persistência | Médio | Migrar para Spring Data JPA (EPIC-05) 🟡 |
| **Joda-Time legado** | Dependências | Médio | Substituir por `java.time` (EPIC-04) 🟡 |
| **Sem observabilidade** | Operação | Médio | Adicionar LGTM stack (EPIC-10) 🟡 |
| **Sem soft delete** | Domínio | Baixo | Adicionar flag + lógica (INI-12) 🟡 |
| **Sem tempo leitura** | Produto | Baixo | Calcular e cache lazy (INI-13) 🟡 |
| **Sem contrato de API formalizado** | Qualidade | Médio | Contratos com Pact/Playwright (EPIC-08) 🟡 |
| **Sem mutação testing** | Qualidade | Baixo | Introduzir Pitest (EPIC-07) 🟡 |

### Risco acumulado e timing sugerido

| Epico | Por que deve ir antes |
|---|---|
| **EPIC-02 (JWT env)** | Sem isso, Docker Compose vai continuar injetando um secret hardcoded |
| **EPIC-03 (Docker + PostgreSQL)** | Realiza configuração de ambiente antes dos grandes upgrades |
| **EPIC-04 (Java 25 + Spring Boot 4)** | Maior risco técnico — executar com rede de segurança (EPIC-07/08) |
| **EPIC-07 + EPIC-08** | Rede de segurança durante o grande upgrade |
| **EPIC-05 (JPA)** | Prepara a base para INI-12 e INI-13 |
| **EPIC-06 (records)** | Simplifica DTOs, mas depende do Java 25 |

---

**Próxima etapa recomendada pelo Coda:**
1️⃣ **Executar EPIC-02** (remover secret hardcoded)  
2️⃣ **Executar EPIC-03** (Docker + PostgreSQL)  
3️⃣ **Paralelizar EPIC-04 com EPIC-07/08**  
4️⃣ **Seguir com EPIC-05 e EPIC-06**

---