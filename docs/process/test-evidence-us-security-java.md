# Evidência de testes — fix(security) — Erros de segurança Java

> **Issue:** #25 — `fix(security): corrigir erros de seguranca Java (NPE SecurityUtil, charset JwtService, raw ResponseEntity)`
> **Branch:** `fix/us-security-java-errors`
> **Data de execução:** 2026-06-15

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Spring Boot | 2.6.3 |
| jjwt | 0.11.2 |
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java

| Métrica | Antes | Depois | Status |
|---|---|---|---|
| **Total de testes** | 68 | 68 | ✅ |
| **Falhas** | 0 | 0 | ✅ |
| **Erros** | 0 | 0 | ✅ |
| **Ignorados** | 0 | 0 | ✅ |

> Nenhuma regressão — todas as 68 verificações continuam passando após as correções de segurança.

---

## Erros corrigidos

### 1. `SecurityUtil.getCurrentUser()` — NPE em authentication null

**Severidade:** ALTA (resposta 500 inesperada em vez de tratamento adequado)

**Arquivo:** `src/main/java/io/spring/graphql/SecurityUtil.java`

**Antes**

```java
if (authentication instanceof AnonymousAuthenticationToken
    || authentication.getPrincipal() == null) {
  return Optional.empty();
}
```

**Risco:** quando o `SecurityContextHolder` retorna `Authentication == null` (situação válida em fluxos não-autenticados), o primeiro operando é `false` e o segundo (`authentication.getPrincipal()`) lança `NullPointerException`, que propaga até a camada HTTP como **500 Internal Server Error**.

**Depois**

```java
if (authentication == null
    || authentication instanceof AnonymousAuthenticationToken
    || authentication.getPrincipal() == null) {
  return Optional.empty();
}
```

**Verificação:** o build continua passando e o helper agora retorna `Optional.empty()` sem lançar NPE quando o contexto está vazio.

---

### 2. `DefaultJwtService` — charset não especificado em `getBytes()`

**Severidade:** MÉDIA (derivação de chave HMAC não determinística entre plataformas)

**Arquivo:** `src/main/java/io/spring/infrastructure/service/DefaultJwtService.java`

**Antes**

```java
this.signingKey = new SecretKeySpec(secret.getBytes(), signatureAlgorithm.getJcaName());
```

**Risco:** `String.getBytes()` usa o charset padrão da plataforma (que varia conforme JVM/SO/configuração). Tokens HS512 assinados numa JVM com charset diferente da JVM que valida produzem **chaves diferentes** → tokens válidos podem ser rejeitados, ou tokens forjados em outra plataforma podem passar.

**Depois**

```java
this.signingKey =
    new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8), signatureAlgorithm.getJcaName());
```

**Verificação:** os 3 testes de `DefaultJwtServiceTest` continuam verdes (`toToken`, `getSubFromToken`, expiração).

---

### 3. Raw `ResponseEntity` em todos os controllers REST

**Severidade:** BAIXA (qualidade de tipo / contrato de API)

**Arquivos:**

| Controller | Métodos | Tipo aplicado |
|---|---|---|
| `ArticleApi` | `deleteArticle` | `ResponseEntity<?>` |
| `ArticleFavoriteApi` | `favoriteArticle`, `unfavoriteArticle` | `ResponseEntity<HashMap<String, Object>>` |
| `ArticlesApi` | `createArticle`, `getFeed`, `getArticles` | `ResponseEntity<Map<String, Object>>`, `ResponseEntity<?>` |
| `CommentsApi` | `getComments`, `deleteComment` | `ResponseEntity<Map<String, Object>>`, `ResponseEntity<?>` |
| `CurrentUserApi` | `currentUser`, `updateProfile` | `ResponseEntity<Map<String, Object>>` |
| `ProfileApi` | `getProfile`, `follow`, `unfollow`, `profileResponse` | `ResponseEntity<Map<String, Object>>` |
| `TagsApi` | `getTags` | `ResponseEntity<Map<String, Object>>` |
| `UsersApi` | `createUser`, `userLogin` | `ResponseEntity<Map<String, Object>>` |

**Risco:** raw types deixam o compilador cego ao formato do corpo de resposta, abrindo espaço para retornar acidentalmente objetos com campos sensíveis (information disclosure) e desligando os warnings de unchecked do `javac`.

**Verificação:** todos os 68 testes (inclusive os contratos `ArticleApiTest`, `UsersApiTest`, `ProfileApiTest`, `CommentsApiTest`) continuam passando, validando que o contrato JSON da API permaneceu o mesmo.

---

## Verificação estrutural — busca por padrões

| Padrão | Antes (matches) | Depois (matches) | Status |
|---|---|---|---|
| `public ResponseEntity ` (raw) em `src/main/java/io/spring/api/` | 13 | 0 | ✅ |
| `secret.getBytes()` sem charset | 1 | 0 | ✅ |
| `authentication == null` em `SecurityUtil` | 0 | 1 | ✅ |

---

## 4. Warnings do compilador (`-Xlint:all`)

Após as correções acima a auditoria foi expandida com `-Xlint:all` no `javac` e atacou os 15 warnings remanescentes (a aba *Problems* do VS Code mostrava 11 — os demais eram avisos de jar/anotações filtrados pelo IDE).

| # | Categoria | Arquivo | Correção |
|---|---|---|---|
| 1 | `[rawtypes] PageCursor` | `application/Node.java:4` | `PageCursor` → `PageCursor<?>` |
| 2 | `[rawtypes] PageCursor` | `application/CursorPager.java:32` | retorno → `PageCursor<?>` |
| 3 | `[rawtypes] PageCursor` | `application/CursorPager.java:36` | retorno → `PageCursor<?>` |
| 4 | `[rawtypes] Class` | `application/user/UserService.java:65` | `Class[]` → `Class<?>[]` |
| 5 | `[rawtypes] Class` | `application/user/UserService.java:67` | `Class[]` → `Class<? extends Payload>[]` |
| 6 | `[rawtypes] CursorPageParameter` | `infrastructure/mybatis/readservice/ArticleReadService.java:33` | `CursorPageParameter` → `CursorPageParameter<?>` |
| 7 | `[rawtypes] CursorPageParameter` | `infrastructure/mybatis/readservice/ArticleReadService.java:41` | `CursorPageParameter` → `CursorPageParameter<?>` |
| 8 | `[serial]` | `JacksonCustomizations$RealWorldModules` | adicionado `serialVersionUID = 1L` |
| 9 | `[serial]` | `JacksonCustomizations$DateTimeSerializer` | adicionado `serialVersionUID = 1L` |
| 10 | `[serial]` | `graphql/exception/AuthenticationException` | adicionado `serialVersionUID = 1L` |
| 11 | `[serial]` | `api/exception/NoAuthorizationException` | adicionado `serialVersionUID = 1L` |
| 12 | `[serial]` | `api/exception/ResourceNotFoundException` | adicionado `serialVersionUID = 1L` |
| 13 | `[serial]` | `api/exception/InvalidAuthenticationException` | adicionado `serialVersionUID = 1L` |
| 14 | `[unchecked]` | `graphql/exception/GraphQLCustomizeExceptionHandler.errorsToMap` | refatorado para `Map<String, List<String>>` + `computeIfAbsent` (eliminado cast `(List)` raw) |
| 15 | `[deprecation]` | `graphql/exception/GraphQLCustomizeExceptionHandler.onException` | implementado `handleException` (novo método não-deprecated) — `onException` legado delega via `.join()` com `@SuppressWarnings("deprecation")` para satisfazer a interface abstrata da graphql-java 17.3 |

### Comando

```bash
./gradlew --init-script lint-init.gradle compileJava --rerun-tasks --console=plain \
  2>&1 | grep -E "warning:|error:"
```

| Métrica | Antes | Depois |
|---|---|---|
| Warnings `[rawtypes]` | 7 | 0 |
| Warnings `[serial]` | 6 | 0 |
| Warnings `[unchecked]` | 1 | 0 |
| Warnings `[deprecation]` | 1 | 0 |
| **Total** | **15** | **0** |

> O init-script `lint-init.gradle` apenas habilita `-Xlint:all -Xlint:-processing -Xlint:-classfile` no `JavaCompile` — não é commitado.

---

## Como reproduzir

```bash
# Build + testes
./gradlew clean test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL, 68 testes, 0 falhas

# Verificar que não há mais raw ResponseEntity nos controllers
grep -n "public ResponseEntity " src/main/java/io/spring/api/*.java
# Esperado: nenhuma linha

# Verificar charset explícito no JWT service
grep -n "getBytes" src/main/java/io/spring/infrastructure/service/DefaultJwtService.java
# Esperado: secret.getBytes(StandardCharsets.UTF_8)

# Verificar null-check em SecurityUtil
grep -n "authentication == null" src/main/java/io/spring/graphql/SecurityUtil.java
# Esperado: 1 ocorrência
```
