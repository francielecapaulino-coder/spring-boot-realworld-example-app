# 06 — Architecture Decision Records (ADRs)
> Versão 1.0 · Junho 2026  
> Projeto: RealWorld Platform Modernization  
> Repositório: `gothinkster/spring-boot-realworld-example-app`
---

## O que é este documento

Este documento registra as **decisões arquiteturais** tomadas durante o projeto de modernização da plataforma RealWorld. Cada ADR (Architecture Decision Record) documenta:

- **O contexto** que tornou a decisão necessária
- **As opções consideradas** com seus trade-offs reais
- **A decisão tomada** e sua justificativa
- **As consequências** — o que fica mais fácil, o que fica mais difícil
- **O impacto** nas iniciativas e épicos do projeto

ADRs são documentos imutáveis no sentido de que registram o que foi decidido e por quê em um determinado momento. Se uma decisão for revertida, um novo ADR é criado — o anterior não é apagado.

> **Para vibe coding:** antes de gerar código relacionado a qualquer um destes tópicos, o desenvolvedor deve ler o ADR correspondente. Prompts gerados sem leitura prévia do ADR têm alta probabilidade de produzir código inconsistente com a decisão tomada.

---

## Índice de ADRs

| ID | Título | Status | Fase | Iniciativa |
|---|---|---|---|---|
| [ADR-001](#adr-001) | DGS Framework vs Spring for GraphQL | ✅ Aceito | Fase 3 | INI-04, US-04.01 |
| [ADR-002](#adr-002) | Spring Data JPA — estratégia de queries | ✅ Aceito | Fase 3 | INI-05 |
| [ADR-003](#adr-003) | Métricas por endpoint — implementação direta vs AOP | ✅ Aceito | Fase 4 | INI-10, US-10.01 |
| [ADR-004](#adr-004) | Pacote `io.spring.graphql` — código gerado pelo DGS Codegen | ✅ Aceito | Fase 3 | INI-04, INI-07 |
| [ADR-005](#adr-005) | Interface `Node` e cursor pagination — migração para records | ✅ Aceito | Fase 3 | INI-05, INI-06 |
| [ADR-006](#adr-006) | Variáveis de ambiente JWT — `JWT_SECRET` e `JWT_SESSION_TIME` | ✅ Aceito | Fase 1 | INI-02 |

**Legenda de status:** ✅ Aceito · 🔄 Em revisão · ❌ Rejeitado · 🗄️ Supersedido

---

## ADR-001 — DGS Framework vs Spring for GraphQL {#adr-001}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead + PM  
**Iniciativas afetadas:** INI-04 (US-04.01), INI-05, INI-06, todos os épicos da Fase 3

---

### Contexto

O projeto usa atualmente o **Netflix DGS Framework versão 4.9.21** para expor a API GraphQL. Este framework foi escolhido originalmente porque era o estado da arte em 2021-2022 para GraphQL em Spring Boot.

Com o upgrade para Spring Boot 4.0.3 (INI-04), surgiu uma decisão de arquitetura: manter o DGS Framework ou migrar para o **Spring for GraphQL** — solução nativa do ecossistema Spring, introduzida com Spring Boot 3.x e consolidada no 4.x.

Esta decisão precisa ser tomada **antes** de iniciar qualquer código do INI-04, pois impacta diretamente a estratégia de upgrade e o volume de trabalho.

### Opções consideradas

#### Opção A: Atualizar DGS Framework para versão 10.x

O DGS Framework 10.x, lançado em 2024, passou por uma transformação significativa: internamente, ele foi **reimplementado sobre o Spring for GraphQL**. Do ponto de vista do código da aplicação:

- As anotações `@DgsComponent`, `@DgsQuery`, `@DgsMutation` continuam funcionando sem alteração
- O DGS 10.x usa o Spring for GraphQL como transporte e execução internamente
- A Netflix migrou todos os seus serviços para o DGS 10.x sem alteração de código de resolvers

**Prós:**
- Zero reescrita de resolvers GraphQL — apenas atualização de versão no `build.gradle`
- Mantém todas as funcionalidades DGS (DataLoaders, testing utilities, schema-first)
- Compatível com Spring Boot 4.x (confirmado no Maven Central)
- A Netflix usa em produção — nível de confiança alto

**Contras:**
- Dependência de framework externo (não do core Spring)
- Versões futuras do DGS dependem da Netflix continuar mantendo

#### Opção B: Migrar para Spring for GraphQL nativo

O Spring for GraphQL é a solução oficial do ecossistema Spring para GraphQL desde Spring Boot 3.x. No Spring Boot 4.x, tem integração nativa com OpenTelemetry, Spring Security e Spring Data.

**Prós:**
- Integração nativa — sem dependência de framework externo
- Mantido pela equipe Spring (Broadcom/VMware) — ciclo de vida alinhado com Spring Boot
- Primeira classe no ecossistema: anotações `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`

**Contras:**
- **Reescrita completa de todos os resolvers GraphQL** — `@DgsQuery` → `@QueryMapping`, etc.
- Curva de aprendizado para o time
- Risco adicional na Fase 3 que já é a mais complexa do projeto
- Benefício técnico marginal dado que DGS 10.x já usa Spring for GraphQL internamente

### Decisão

**Opção A: Atualizar DGS Framework para versão 10.x.**

A razão principal é de custo-benefício: o DGS 10.x já usa Spring for GraphQL internamente, portanto os benefícios técnicos da migração direta são marginais. A reescrita de todos os resolvers na Fase 3 — que já carrega o risco de um upgrade completo de framework — adicionaria semanas de trabalho sem ganho funcional para o produto.

A decisão pode ser revisada em uma fase futura quando o projeto estiver estável e a equipe tiver bandwidth para uma migração de menor urgência.

### Consequências

**O que fica mais fácil:**
- INI-04 se torna um upgrade de versão de dependência, não uma reescrita
- Todos os testes de integração GraphQL (INI-08) continuam válidos sem alteração
- O schema `.graphqls` permanece inalterado — a forma de trabalho com GraphQL não muda

**O que fica mais difícil:**
- A dependência do DGS Framework permanece — qualquer breaking change futuro do DGS afeta o projeto
- Eventualmente, a migração para Spring for GraphQL nativo ainda será necessária — apenas adiada

**Impacto direto nos documentos:**
- EPIC-04, US-04.01: "Documentar ADR sobre DGS" — este documento cumpre essa história
- EPIC-04: a história de upgrade do DGS é somente `build.gradle` — não há reescrita de resolvers
- EPIC-07: o pacote `io.spring.graphql` (código gerado) continua excluído do Pitest — ver ADR-004

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-04: "ADR sobre DGS Framework vs Spring for GraphQL deve ser documentado antes de iniciar" |
| `04-roadmap.md` | Fase 3: "ADR sobre DGS aprovado antes de iniciar qualquer história" |
| `05-backlog.md` | EPIC-04, US-04.01 |
| `05-backlog.md` | EPIC-04, US-04.03 (upgrade Spring Boot 4.0.3) — depende desta decisão |
| `05-backlog.md` | EPIC-04, US-04.06 (atualização DGS para 10.x) — implementa a Opção A |

---

## ADR-002 — Spring Data JPA: estratégia de queries {#adr-002}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead  
**Iniciativas afetadas:** INI-05, INI-12, INI-13

---

### Contexto

A migração de MyBatis para Spring Data JPA (INI-05) exige uma decisão sobre como implementar queries que vão além do CRUD básico gerado automaticamente. O MyBatis atual tem queries SQL explícitas em arquivos XML (`ArticleMapper.xml`, `UserMapper.xml`, etc.). No JPA, existem três abordagens principais para queries customizadas.

O projeto tem queries de complexidade variada:
- **Simples:** `findByEmail()`, `findBySlug()`, `findByUsername()` → geradas automaticamente por nome de método
- **Médias:** listagem de artigos com filtros opcionais (`authoredBy`, `favoritedBy`, `withTag`) + paginação cursor-based
- **Relacionamentos:** follows, favorites (M:N), feed de artigos por usuários seguidos

### Opções consideradas

#### Opção A: Derivação por nome de método (`findBy...`)

Spring Data gera automaticamente queries a partir do nome do método. Ex: `findBySlugAndIsDeletedFalse()`.

**Prós:** zero SQL, zero JPQL, completamente tipado  
**Contras:** nomes de método ficam verbosos para queries complexas; não funciona para filtros opcionais

#### Opção B: `@Query` com JPQL

Queries JPQL escritas manualmente em anotações. JPQL é orientado a objetos — usa nomes de entidades e campos Java, não tabelas e colunas SQL.

```java
@Query("SELECT a FROM Article a WHERE (:author IS NULL OR a.author.username = :author) AND a.isDeleted = false")
Page<Article> findByOptionalAuthor(@Param("author") String author, Pageable pageable);
```

**Prós:** legível, portável entre bancos, tipado  
**Contras:** verboso para queries muito complexas; sem suporte a filtros totalmente dinâmicos de forma elegante

#### Opção C: JPA Specifications (Criteria API)

Permite construir queries dinamicamente em código Java. Ideal para filtros opcionais encadeados.

```java
Specification<Article> spec = Specification.where(notDeleted())
    .and(authoredBy(authorParam))
    .and(withTag(tagParam));
```

**Prós:** filtros opcionais elegantes; totalmente tipado; testável em isolamento  
**Contras:** mais código para configurar; curva de aprendizado moderada

#### Opção D: SQL Nativo (`@Query(nativeQuery = true)`)

SQL puro, executado diretamente no banco.

**Prós:** controle total; funcionalidades específicas do PostgreSQL disponíveis  
**Contras:** perde portabilidade; mais difícil de manter; usar apenas como último recurso

### Decisão

**Estratégia combinada por complexidade de query:**

| Cenário | Abordagem | Exemplo |
|---|---|---|
| Queries simples por um campo | Derivação por nome | `findBySlug()`, `findByEmail()` |
| Queries com filtros opcionais múltiplos | JPA Specifications | Listagem de artigos com `authoredBy`, `favoritedBy`, `withTag` |
| Queries médias com joins simples | `@Query` JPQL | Feed de artigos por usuários seguidos |
| Funcionalidades específicas do PostgreSQL | `@Query` SQL nativo | Apenas se JPQL não suportar |

**Regra de ouro:** usar a abordagem mais simples que resolve o problema. Derivação por nome primeiro; `@Query` JPQL se derivação não for expressiva; Specifications se houver filtros opcionais combinados; SQL nativo apenas quando as anteriores forem insuficientes.

### Consequências

**O que fica mais fácil:**
- CRUD básico: zero código de implementação
- Filtros opcionais de artigos: Specifications são mais seguras que múltiplos `if (param != null)` com SQL manual
- Soft delete: `@Where(clause = "is_deleted = false")` + Specifications trabalham juntos naturalmente

**O que fica mais difícil:**
- Developers familiarizados com SQL precisam aprender JPQL e a API Criteria
- Queries com múltiplos joins e subqueries podem ficar verbosas em JPQL

**Impacto direto nos documentos:**
- EPIC-05: histórias de migração de cada entidade devem adotar esta estratégia
- EPIC-12 (soft delete): `@Where` + Specifications — sem `WHERE is_deleted = false` manual em nenhuma query

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-05: "Decisão sobre queries complexas (JPQL vs Specifications vs QueryDSL)" |
| `05-backlog.md` | EPIC-05: "Queries complexas via `@Query` JPQL ou Specifications" |

---

## ADR-003 — Métricas por endpoint: implementação direta vs AOP {#adr-003}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead + PM  
**Iniciativas afetadas:** INI-10, EPIC-10, US-10.01, US-10.02

---

### Contexto

A gestão requisitou que cada endpoint REST incremente um contador Micrometer a cada chamada (INI-10). Existem 19 endpoints distribuídos em 7 controllers. A decisão é: onde e como implementar esses contadores?

O risco central é acoplamento: se cada controller precisar conhecer o `MeterRegistry`, a observabilidade fica misturada com a lógica de negócio — exatamente o tipo de acoplamento que a arquitetura em camadas do projeto tenta evitar.

### Opções consideradas

#### Opção A: Implementação direta nos controllers

Injetar `MeterRegistry` em cada controller e chamar `counter.increment()` no início de cada método.

```java
@GetMapping("/articles")
public ResponseEntity<ArticlesResponse> listArticles(MeterRegistry meterRegistry, ...) {
    meterRegistry.counter("api.requests.total",
        "endpoint", "list_articles", "method", "GET").increment();
    // lógica do endpoint
}
```

**Prós:** explícito e fácil de entender; debug simples  
**Contras:** 19 controllers com código de observabilidade repetido; controllers passam a conhecer `MeterRegistry`; difícil de modificar o padrão de métricas globalmente; viola o princípio de responsabilidade única

#### Opção B: Spring AOP com `@Around` advice

Um único `@Aspect` intercepta todos os métodos de controller e incrementa o contador automaticamente.

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

**Prós:** zero acoplamento nos controllers; mudança de padrão de métricas em um único lugar; controllers continuam responsáveis apenas por HTTP; testável em isolamento  
**Contras:** comportamento implícito — o desenvolvedor precisa saber que AOP está interceptando; debugging de AOP requer conhecimento específico

#### Opção C: `HandlerInterceptor` do Spring MVC

Interceptor registrado no `WebMvcConfigurer` que executa antes/depois de cada request.

```java
public class MetricsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        // incrementar contador
        return true;
    }
}
```

**Prós:** bem integrado ao ciclo de vida do Spring MVC; acesso direto ao `HttpServletRequest`  
**Contras:** menos granular que AOP; difícil de associar ao nome lógico do endpoint; pode ser contornado por requests que não passam pelo DispatcherServlet

### Decisão

**Opção B: Spring AOP com `@Around` advice.**

O AOP é a abordagem que melhor preserva a separação de responsabilidades da arquitetura existente. Os controllers não precisam saber que estão sendo medidos. O aspecto fica em um único lugar (`io.spring.infrastructure.metrics`), alinhado com a camada `infrastructure` que já existe no projeto.

O comportamento implícito do AOP é mitigado por: documentação clara no Coda, nome explícito da classe (`ApiMetricsAspect`) e testes do próprio aspecto.

**Regra de implementação:** o aspecto deve capturar o nome do endpoint a partir da anotação `@RequestMapping` (ou `@GetMapping`, `@PostMapping`, etc.) presente no método — não de strings hardcoded. Isso garante que o nome da métrica fica sempre sincronizado com a rota real.

### Consequências

**O que fica mais fácil:**
- Adicionar novos endpoints futuros: métricas são automáticas
- Mudar o padrão de nomeação das métricas: uma mudança em um arquivo
- Controllers permanecem limpos e focados

**O que fica mais difícil:**
- Debugging de métricas ausentes: o desenvolvedor precisa conhecer o aspecto
- Testes do aspecto são necessários para garantir que todos os endpoints são cobertos

**Impacto direto nos documentos:**
- EPIC-10, US-10.01: este ADR resolve a decisão documentada nessa história
- EPIC-10, US-10.02: implementação deve usar AOP, não injeção direta

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-10: "O time deve decidir entre implementação direta nos controllers ou via AOP — decisão a ser documentada em ADR antes de iniciar" |
| `05-backlog.md` | EPIC-10, US-10.01 |

---

## ADR-004 — Pacote `io.spring.graphql`: código gerado pelo DGS Codegen {#adr-004}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead  
**Iniciativas afetadas:** INI-04, INI-07, todos que tocam GraphQL

> **Origem:** GAP-5 identificado no cross-check do repositório GitHub real (junho 2026).

---

### Contexto

O `build.gradle` do projeto define:

```groovy
tasks.named('generateJava') {
    schemaPaths = ["${projectDir}/src/main/resources/schema"]
    packageName = 'io.spring.graphql'
}
```

Isso significa que **todo o conteúdo do pacote `io.spring.graphql` é gerado automaticamente** pelo plugin DGS Codegen a partir do arquivo `schema.graphqls` a cada build. O código gerado inclui tipos Java correspondentes a cada tipo, input, union e enum definido no schema.

Este comportamento não estava documentado em nenhum lugar do projeto de modernização, criando riscos específicos:

1. **Risco de edição manual:** um desenvolvedor pode editar arquivos em `io.spring.graphql` sem saber que serão sobrescritos no próximo build
2. **Risco de cobertura de testes:** o Pitest pode tentar testar código gerado, inflando ou distorcendo as métricas
3. **Risco de migração:** durante o upgrade de Java/Spring Boot (INI-04), imports `javax.*` no código gerado não devem ser migrados manualmente — o codegen os regenera automaticamente

### Decisão

**`io.spring.graphql` é um pacote de código gerado e deve ser tratado como tal em todo o projeto:**

1. **Nunca editar manualmente:** qualquer alteração nos tipos GraphQL deve ser feita no `schema.graphqls` — o código Java é regenerado automaticamente
2. **Excluir do Pitest:** o threshold de 95% de mutação se aplica apenas ao código escrito pela equipe
3. **Excluir de análise estática:** ferramentas de linting e qualidade não devem reportar issues em código gerado
4. **Não migrar `javax.*` manualmente:** se o codegen gerar imports `javax.*`, a solução é atualizar a versão do plugin DGS Codegen — não editar os arquivos gerados
5. **Adicionar ao `.gitignore` local:** embora seja prática debatida, os arquivos gerados não devem ser commitados se o build os regenera — a decisão final sobre `.gitignore` deve ser tomada considerando o CI

### Consequências

**O que fica mais fácil:**
- Adicionar campos ao schema GraphQL: edita-se o `.graphqls`, o Java é gerado automaticamente
- Upgrade do DGS Codegen: basta atualizar a versão do plugin no `build.gradle`
- Pitest: métricas precisas, sem ruído de código gerado

**O que fica mais difícil:**
- Debugar problemas no código gerado requer entender o que o codegen produz a partir do schema
- Se o codegen tiver um bug, o workaround é mais complexo do que editar o arquivo diretamente

**Configuração obrigatória no Pitest (INI-07):**

```groovy
pitest {
    excludedClasses = ['io.spring.graphql.*']  // código gerado — NÃO testar
    // ... resto da configuração
}
```

**Configuração obrigatória no Spotless/análise estática:**

```groovy
spotless {
    java {
        target project.fileTree(project.rootDir) {
            include '**/*.java'
            exclude 'build/generated/**/*.*'
            exclude 'src/main/java/io/spring/graphql/**'  // código gerado
        }
    }
}
```

**Adição ao `.gitignore` (a avaliar):**

```
# Código gerado pelo DGS Codegen - regenerado automaticamente no build
src/main/java/io/spring/graphql/
```

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-07: "Exclusão do pacote de código gerado pelo DGS (`io.spring.graphql.*`) da métrica" |
| `05-backlog.md` | EPIC-07: "excludedClasses = ['io.spring.graphql.*'] — código gerado DGS" |
| Cross-check GitHub | GAP-5: `build.gradle` linha `packageName = 'io.spring.graphql'` confirmado |

---

## ADR-005 — Interface `Node` e cursor pagination: migração para record types {#adr-005}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead  
**Iniciativas afetadas:** INI-05, INI-06, EPIC-05, EPIC-06

> **Origem:** GAP-9 identificado no cross-check do repositório GitHub real — `ArticleData.java` confirmado linha a linha.

---

### Contexto

O `ArticleData.java` (confirmado no repositório) implementa a interface `io.spring.application.Node`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleData implements io.spring.application.Node {

    private String id;
    private String slug;
    // ... outros campos

    @Override
    public DateTimeCursor getCursor() {
        return new DateTimeCursor(updatedAt);
    }
}
```

A interface `Node` é usada para suportar **cursor-based pagination** — a mesma paginação `first/after/last/before` que aparece no schema GraphQL. `DateTimeCursor` encapsula o `updatedAt` como cursor de paginação.

Quando INI-06 introduzir record types, surge um problema de compatibilidade: records Java são imutáveis por design. A interface `Node` exige `getCursor()` — mas records podem implementar interfaces normalmente. O problema real é mais sutil: o `DateTimeCursor` usa `Joda DateTime`, que será removido em INI-04 (substituído por `java.time`).

Portanto, a migração de `ArticleData` para record envolve **duas mudanças simultâneas**:
1. Converter de classe com Lombok para record
2. Substituir `Joda DateTime` por `java.time.Instant` ou `java.time.OffsetDateTime`

### Decisão

**Records podem e devem implementar a interface `Node`. A migração é viável em dois passos:**

**Passo 1 — Durante INI-04 (migração de Joda-Time):**
Substituir `DateTime` por `java.time.Instant` em `ArticleData` e em `DateTimeCursor`:

```java
// DateTimeCursor.java — antes
public record DateTimeCursor(DateTime dateTime) { ... }

// DateTimeCursor.java — depois
public record DateTimeCursor(Instant instant) { ... }
```

**Passo 2 — Durante INI-06 (record types):**
Converter `ArticleData` para record mantendo a implementação de `Node`:

```java
// ArticleData.java — depois (record implementando interface)
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
        return new DateTimeCursor(updatedAt);
    }
}
```

**Regras para a migração:**

1. Records podem implementar interfaces — não há impedimento técnico em Java 25
2. `@JsonProperty` em records é suportado pelo Jackson — o alias `"author"` para `profileData` é preservado
3. `getCursor()` deve ser declarado explicitamente no corpo do record — não é gerado automaticamente
4. `CommentData` e outros DTOs com cursor também seguem o mesmo padrão
5. A migração de `Joda DateTime` → `java.time.Instant` em **INI-04 é pré-requisito** de INI-06 para as classes com cursor

### Verificação de compatibilidade

| Aspecto | Problema? | Solução |
|---|---|---|
| Record implementa `Node` | ❌ Nenhum | Records implementam interfaces normalmente |
| `@JsonProperty` em record | ❌ Nenhum | Jackson 3.x (Spring Boot 4) suporta records nativamente |
| `getCursor()` em record | ✅ Precisa ser explícito | Declarar no corpo do record — não gerado automaticamente |
| `Joda DateTime` em record | ✅ Bloqueante | Migrar para `java.time.Instant` em INI-04 antes de INI-06 |
| `@NoArgsConstructor` em record | ⚠️ Removido | Records não têm construtor sem args — se JPA precisar, não converter para record |

### Consequências

**O que fica mais fácil:**
- `ArticleData` como record: imutável por design, sem risco de mutação acidental de DTOs
- Serialização Jackson: records são suportados nativamente, sem necessidade de Lombok `@Data`

**O que fica mais difícil:**
- A migração de `ArticleData` para record tem dependência explícita de INI-04 (Joda-Time removido primeiro)
- O desenvolvedor precisa declarar explicitamente `getCursor()` no corpo do record — não é automático

**Ordem de execução obrigatória:**
```
INI-04: substituir Joda DateTime → java.time.Instant em ArticleData e DateTimeCursor
    ↓
INI-06: converter ArticleData (e similares) para record com getCursor() explícito
```

**Impacto direto nos documentos:**
- EPIC-04: a migração de Joda-Time em `ArticleData` deve ser explícita (não só "remover Joda-Time")
- EPIC-06, US-06.02: história de conversão de DTOs de artigo deve mencionar `getCursor()`
- EPIC-05: `DateTimeCursor` não é entidade JPA — pode ser record também

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-06: "Critério de elegibilidade — não tem estado mutável necessário externamente" |
| `05-backlog.md` | EPIC-06: US-06.02 "Converter DTOs de artigo para record types" |
| Cross-check GitHub | GAP-9: `ArticleData.java` confirmado — implementa `Node`, usa `Joda DateTime` |

---

## ADR-006 — Variáveis de ambiente JWT: `JWT_SECRET` e `JWT_SESSION_TIME` {#adr-006}

**Data:** Junho 2026  
**Status:** ✅ Aceito  
**Deciders:** Tech Lead + PM  
**Iniciativas afetadas:** INI-02, EPIC-02, US-02.02, US-02.03

> **Origem:** GAP-4 identificado no cross-check do repositório GitHub real — `DefaultJwtService.java` confirmado linha a linha.

---

### Contexto

O `DefaultJwtService.java` (confirmado no repositório) lê **duas propriedades** via `@Value`:

```java
@Autowired
public DefaultJwtService(
    @Value("${jwt.secret}") String secret,
    @Value("${jwt.sessionTime}") int sessionTime) {
    this.sessionTime = sessionTime;
    // ...
}
```

Toda a documentação do projeto de modernização mencionava apenas `JWT_SECRET` como variável a ser movida para ambiente. `jwt.sessionTime` — que define o tempo de expiração do token em segundos — estava omitido.

**Impacto prático:** se apenas `JWT_SECRET` for movida para variável de ambiente e `jwt.sessionTime` permanecer hardcoded no `application.properties`, a aplicação ainda funciona — mas o tempo de sessão está exposto no repositório e não pode ser configurado por ambiente (dev pode ter sessão longa, prod pode precisar de sessão curta).

**Impacto crítico:** se `jwt.sessionTime` for removido do `application.properties` sem adicionar ao `.env.example`, a aplicação falha no startup com `IllegalArgumentException: Could not resolve placeholder 'jwt.sessionTime'` — mesmo com `JWT_SECRET` corretamente configurado.

### Decisão

**Ambas as propriedades JWT devem ser externalizadas como variáveis de ambiente:**

| Variável de ambiente | Propriedade Spring | Tipo | Valor padrão sugerido |
|---|---|---|---|
| `JWT_SECRET` | `jwt.secret` | String | Sem padrão — obrigatória |
| `JWT_SESSION_TIME` | `jwt.sessionTime` | Integer (segundos) | `86400` (24 horas) |

**Estratégia de fail-fast:**

A aplicação não deve iniciar se `JWT_SECRET` não estiver definida — não há valor padrão seguro para um secret criptográfico. `JWT_SESSION_TIME` pode ter um valor padrão razoável (`86400`) mas deve ser explicitamente configurável.

**Configuração no `application.properties`:**

```properties
# JWT — ambas as variáveis obrigatórias no ambiente
jwt.secret=${JWT_SECRET}
jwt.sessionTime=${JWT_SESSION_TIME:86400}
```

A sintaxe `${JWT_SESSION_TIME:86400}` define 86400 como fallback se a variável não estiver presente — adequado para desenvolvimento local sem exigir configuração adicional. Para `JWT_SECRET`, sem fallback — falha imediatamente.

**Conteúdo obrigatório do `.env.example`:**

```bash
# Chave secreta para assinatura de tokens JWT
# OBRIGATÓRIA — a aplicação não inicia sem este valor
# Gerar com: openssl rand -base64 64
JWT_SECRET=your-secret-here-min-32-chars

# Tempo de sessão em segundos
# Padrão: 86400 (24 horas)
# Desenvolvimento: 604800 (7 dias)
# Produção: 3600 (1 hora) recomendado
JWT_SESSION_TIME=86400
```

**Configuração por perfil:**

```yaml
# application-dev.yml
jwt:
  secret: ${JWT_SECRET}
  sessionTime: ${JWT_SESSION_TIME:604800}  # 7 dias em dev

# application-prod.yml
jwt:
  secret: ${JWT_SECRET}
  sessionTime: ${JWT_SESSION_TIME:3600}  # 1 hora em prod
```

### Consequências

**O que fica mais fácil:**
- Configuração de sessão diferente por ambiente sem alterar código
- Rotação de secrets sem redeploy de configuração de código
- Clareza sobre o que configura o JWT na aplicação

**O que fica mais difícil:**
- Desenvolvedores precisam definir ambas as variáveis (não apenas `JWT_SECRET`) para rodar localmente
- O `.env.example` fica mais longo — necessário documentar bem

**Impacto direto nos documentos:**
- EPIC-02, US-02.02: "Configurar leitura de `JWT_SECRET` via variável de ambiente" → deve ser expandida para incluir `JWT_SESSION_TIME`
- EPIC-02, US-02.03: "Criar `.env.example`" → deve incluir ambas as variáveis com comentários
- EPIC-03, `CONTRIBUTING.md`: instruções de setup devem mencionar ambas as variáveis
- EPIC-03, `docker-compose.yml`: deve injetar ambas: `JWT_SECRET: ${JWT_SECRET}` e `JWT_SESSION_TIME: ${JWT_SESSION_TIME:-86400}`

### Rastreabilidade

| Documento | Referência |
|---|---|
| `03-initiatives.md` | INI-02: "Remoção do JWT secret do `application.properties`" |
| `05-backlog.md` | EPIC-02, US-02.02, US-02.03 |
| Cross-check GitHub | GAP-4: `DefaultJwtService.java` linha 25 — `@Value("${jwt.sessionTime}")` |

---

## Resumo executivo

| ADR | Decisão tomada | Fase | Impacto principal |
|---|---|---|---|
| ADR-001 | Manter DGS 10.x — sem reescrita de resolvers | Fase 3 | INI-04 é upgrade de versão, não reescrita |
| ADR-002 | Queries JPA por complexidade: derivação → JPQL → Specifications → SQL nativo | Fase 3 | INI-05: padrão claro para cada tipo de query |
| ADR-003 | Métricas via AOP — `ApiMetricsAspect` único | Fase 4 | INI-10: controllers sem acoplamento a Micrometer |
| ADR-004 | `io.spring.graphql` é código gerado — nunca editar, excluir do Pitest | Fase 3 | INI-07: configuração Pitest obrigatória |
| ADR-005 | Records implementam `Node` com `getCursor()` explícito; Joda-Time migrado antes | Fase 3 | INI-04 precede INI-06 para `ArticleData` |
| ADR-006 | `JWT_SECRET` + `JWT_SESSION_TIME` — ambas obrigatórias no ambiente | Fase 1 | INI-02: `.env.example` com 2 variáveis JWT |

---

## Processo para novos ADRs

Quando uma nova decisão arquitetural precisar ser documentada:

1. Criar issue no GitHub com template `spike`
2. Adicionar nova seção a este documento seguindo o formato dos ADRs existentes
3. Referenciar o ADR nos documentos afetados (`03-initiatives.md`, `05-backlog.md`)
4. Documentar o prompt e contexto usado para a decisão no Coda
5. Atualizar o índice no topo deste documento

**Critérios para quando criar um ADR:**
- A decisão afeta mais de uma iniciativa ou épico
- A decisão tem consequências difíceis de reverter
- A decisão foi debatida e havia mais de uma opção razoável
- A decisão tem impacto direto em como o código vibe-coding será gerado

---

*Documento vivo — novos ADRs adicionados conforme necessidade*  
*Toda decisão referenciada nos documentos 03, 04 e 05 deve ter ADR correspondente aqui*  
*6 ADRs registrados · Rastreado em: `03-initiatives.md` · `04-roadmap.md` · `05-backlog.md`*