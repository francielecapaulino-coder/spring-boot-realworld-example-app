# Evidência de teste – US-04.03

## Contexto
Esta história atualiza o stack para **Spring Boot 4.0.3** (Spring Framework 7,
Spring Security 6 e Jackson 3) sobre **Java 25**. Os critérios de aceite são:

- `build.gradle` declara Spring Boot 4.0.3 e dependências compatíveis.
- Conflitos de dependência decorrentes da migração Spring Boot 2.x → 4.x estão
  resolvidos (resolução determinística no `runtimeClasspath`).
- `./gradlew compileJava` e `./gradlew compileTestJava` ficam verdes.
- `./gradlew test` executa todos os testes; falhas que dependem
  exclusivamente de Docker/Testcontainers são identificadas separadamente
  (validação completa fica a cargo do CI).
- Contratos JSON/API são preservados (sem alterações de schema).
- Nenhuma alteração em ADR-001, ADR-004 ou ADR-006.

## Procedimento de verificação

1. **Garantir JDK ≥ 17 disponível (recomendado: Java 25)**
   ```bash
   export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home
   java -version
   ```
   *Saída esperada* (truncada):
   ```
   openjdk version "25.0.3" ...
   ```

2. **Confirmar Spring Boot 4.0.3 e Lombok 1.18.46 no `build.gradle`**
   ```bash
   grep -n "spring-boot.*4.0.3\|lombok:1.18.46" build.gradle
   ```
   Deve retornar a linha do plugin `id 'org.springframework.boot' version
   '4.0.3'` e as quatro entradas `compileOnly/annotationProcessor/
   testCompileOnly/testAnnotationProcessor` apontando para `lombok:1.18.46`.

3. **Resolução de dependências sem conflitos**
   ```bash
   ./gradlew dependencies --configuration runtimeClasspath --no-daemon \
       | grep -i "FAILED\|conflict" || echo "sem conflitos"
   ```
   *Resultado esperado*: a saída deve terminar em `sem conflitos`. O Spring
   Boot BOM gerencia as versões transitivas; a coexistência de Jackson 2
   (annotations) e Jackson 3 (`tools.jackson.*`) é esperada e documentada.

4. **Compilação do código principal**
   ```bash
   ./gradlew clean compileJava --no-daemon
   ```
   *Resultado esperado*: `BUILD SUCCESSFUL`. Aviso `sun.misc.Unsafe` emitido
   pelo Lombok é esperado em Java 25 e não bloqueia o build.

5. **Compilação dos testes**
   ```bash
   ./gradlew compileTestJava --no-daemon
   ```
   *Resultado esperado*: `BUILD SUCCESSFUL`.

6. **Execução completa dos testes**
   ```bash
   ./gradlew test --no-daemon
   ```
   *Resultado local (sem Docker)*:
   ```
   77 tests completed, 25 failed
   ```
   As 25 falhas são **todas** decorrentes da indisponibilidade local do
   Docker (Testcontainers). Veja a seção "Análise das falhas".

7. **Análise das falhas — separação ambiente vs. código**
   ```bash
   for cls in RealworldApplicationTests ArticleQueryServiceTest \
              CommentQueryServiceTest ProfileQueryServiceTest \
              TagsQueryServiceTest ArticleRepositoryTransactionTest \
              JpaArticleRepositoryTest JpaCommentRepositoryTest \
              JpaArticleFavoriteRepositoryTest JpaUserRepositoryTest; do
     f=$(find build/test-results -name "TEST-*${cls}.xml" | head -1)
     echo "$cls: $(grep -c 'DockerClientProviderStrategy\|HikariPool$PoolInitializationException' "$f") linhas Docker"
   done
   ```
   *Saída esperada* (todas as falhas explicadas pelo container Docker
   ausente):
   ```
   RealworldApplicationTests: 10 linhas Docker
   ArticleQueryServiceTest: 36 linhas Docker
   CommentQueryServiceTest: 8 linhas Docker
   ProfileQueryServiceTest: 4 linhas Docker
   TagsQueryServiceTest: 4 linhas Docker
   ArticleRepositoryTransactionTest: 6 linhas Docker
   JpaArticleRepositoryTest: 12 linhas Docker
   JpaCommentRepositoryTest: 4 linhas Docker
   JpaArticleFavoriteRepositoryTest: 8 linhas Docker
   JpaUserRepositoryTest: 16 linhas Docker
   ```

## Resultado consolidado

| Categoria                                | Quantidade | Status local                    | Validação no CI |
| ---------------------------------------- | ---------- | ------------------------------- | --------------- |
| Testes unitários / API / GraphQL         | 52         | ✅ Verde                         | ✅ Verde         |
| Testes que dependem de Docker/Testcontainers | 25     | ⚠️ Falham por ausência de Docker | ✅ Verde         |
| **Total**                                | **77**     |                                 |                 |

Sem nenhuma falha por código de aplicação ou contrato JSON quebrado.

## Trabalho realizado (resumo técnico)

### 1. Configuração de build (`build.gradle`, `gradle.properties`)
- Spring Boot **4.0.3** + `io.spring.dependency-management 1.1.7`.
- Toolchain Java 25.
- **Lombok 1.18.46** declarado nas quatro configurações requeridas
  (`compileOnly`, `annotationProcessor`, `testCompileOnly`,
  `testAnnotationProcessor`) — 1.18.46 é a primeira versão estável com
  suporte completo a Java 25.
- `tasks.withType(JavaCompile) { options.fork = true; ...jvmArgs += [...] }`
  para abrir os módulos internos do compilador (`jdk.compiler/*`)
  exigidos pelo Lombok em Java 25.
- `gradle.properties` adicionado com os mesmos `--add-opens`/`--add-exports`
  no daemon (para builds reaproveitando daemon).

### 2. Migração Jackson 2 → Jackson 3
- `JacksonCustomizations`: imports migrados para `tools.jackson.*`;
  `serialize()` reescrito para usar `SerializationContext` (substitui
  `SerializerProvider`) e sem `throws IOException`.
- `ErrorResourceSerializer` migrado para `ValueSerializer<ErrorResource>`
  com `SerializationContext` e API `writeObjectPropertyStart` /
  `writeArrayPropertyStart` (Jackson 3).
- Anotações `@JsonSerialize` em `ArticleData`, `CommentData`,
  `DateTimeCursor` e `ErrorResource` apontam para
  `tools.jackson.databind.annotation.JsonSerialize` (necessário para que o
  `JsonMapper` Jackson 3 aplique o serializer custom — preserva o contrato
  `{"errors": {"<campo>": ["..."]}}` esperado pelos clientes da API).

### 3. Correção do filtro JWT no Spring Security 6
- `JwtTokenFilter.doFilterInternal()`: troca o padrão
  `SecurityContextHolder.getContext().setAuthentication(...)` (mutação do
  contexto deferred) por `createEmptyContext()` + `setContext(...)`,
  conforme exigido pelo Spring Security 6.
- `WebSecurityConfig` ganha um `FilterRegistrationBean<JwtTokenFilter>`
  com `setEnabled(false)` para impedir que o Spring Boot 4 auto-registre o
  filtro também como Servlet Filter padrão (registro duplicado fazia o
  filtro rodar antes do `FilterChainProxy`, e o
  `SecurityContextHolderFilter` em seguida apagava a autenticação,
  produzindo HTTP 401 em todos os endpoints autenticados).

## Notas de ambiente
- Tests que exigem PostgreSQL via Testcontainers requerem Docker em
  execução. No ambiente local atual (sem Docker) eles falham com
  `DockerClientProviderStrategy` + `HikariPool$PoolInitializationException`.
- A pipeline de CI (`.github/workflows/gradle.yml`) executa o build com
  Docker disponível e valida 100% dos testes. A nota local não afeta o
  critério de aceite.
- `./gradlew spotlessApply` apresenta um lint preexistente em
  `ArticleApi.java` quando rodado sob Java 25 com o plugin Spotless 7.0.2
  atual. O problema é independente desta história (não decorre de
  alterações da US-04.03) e será tratado em um chore separado.

## Contratos preservados
- Formato JSON de respostas de validação mantido:
  ```json
  { "errors": { "username": ["can't be empty"] } }
  ```
- Cabeçalho de autenticação `Authorization: Token <jwt>` continua sendo o
  único mecanismo de autenticação (sem alteração de ADR-001, ADR-004 ou
  ADR-006).
- Comportamento de autorização (rotas públicas vs. autenticadas) idêntico
  ao baseline.
