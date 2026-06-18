# Evidência de testes — US-02.02

> **História:** US-02.02 — Fail-fast com mensagem clara quando `JWT_SECRET` está ausente
> **Branch:** `feat/us-02.02-jwt-secret-fail-fast-message`
> **PR:** #32 — MERGED
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| JWT_SECRET | gerado via `openssl rand -base64 64` |
| Comando Java | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — cobertura da US-02.02

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |
| **Testes específicos da US** | 5 (`JwtSecretFailureAnalyzerTest`) |

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `src/main/java/io/spring/infrastructure/config/JwtSecretFailureAnalyzer.java` | ✅ presente (1.871 bytes) |
| `src/test/java/io/spring/infrastructure/config/JwtSecretFailureAnalyzerTest.java` | ✅ presente (2.171 bytes) |
| `src/main/resources/META-INF/spring.factories` | ✅ registra `JwtSecretFailureAnalyzer` |

---

## Testes unitários — `JwtSecretFailureAnalyzerTest`

| Teste | Descrição | Status |
|---|---|---|
| `analyzesJwtSecretMissingException` | Exceção com `JWT_SECRET` → análise não nula, descrição e ação corretas | ✅ |
| `returnsNullWhenMessageDoesNotContainJwtSecret` | Exceção com outra variável (`OTHER_VAR`) → retorna `null` | ✅ |
| `returnsNullWhenCauseMessageIsNull` | Exceção com mensagem `null` → retorna `null` sem NPE | ✅ |
| `descriptionContainsApplicationCannotStart` | Descrição contém `"aplicacao nao pode iniciar"` | ✅ |
| `actionContainsEnvExampleReference` | Action contém referência a `.env.example` | ✅ |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | `JwtSecretFailureAnalyzer` presente em `io.spring.infrastructure.config` | ✅ |
| CA-02 | Registrado em `META-INF/spring.factories` | ✅ |
| CA-03 | Mensagem contém `JWT_SECRET`, `obrigatória`, referência ao `openssl` | ✅ |
| CA-04 | Action contém `export JWT_SECRET=<valor>` e referência ao `.env.example` | ✅ |
| CA-05 | Action referencia ADR-006 | ✅ |
| CA-06 | `returnsNull` quando exceção não é sobre `JWT_SECRET` | ✅ |
| CA-07 | `returnsNull` quando mensagem da causa é `null` (sem NPE) | ✅ |
| CA-08 | 5 testes unitários passando, sem falhas | ✅ |

---

## Conteúdo verificado — `JwtSecretFailureAnalyzer.java`

```
Classe:   JwtSecretFailureAnalyzer extends AbstractFailureAnalyzer<IllegalArgumentException>
Constante: JWT_SECRET_PLACEHOLDER = "JWT_SECRET"
DESCRIPTION: inclui "JWT_SECRET e obrigatoria", "nenhuma autenticacao e possivel", "aplicacao nao pode iniciar"
ACTION:     inclui "openssl rand -base64 64", "export JWT_SECRET=<valor-gerado>", ".env.example", "ADR-006"
Condição:   analyze() retorna null se cause.getMessage() == null ou não contém "JWT_SECRET"
```

---

## Como reproduzir

```bash
# Executar somente os testes da US-02.02
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long \
  ./gradlew test --tests "io.spring.infrastructure.config.JwtSecretFailureAnalyzerTest" --console=plain
# Esperado: 5 testes passando, 0 falhas

# Suíte completa
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas

# Verificar registro no spring.factories
grep "JwtSecretFailureAnalyzer" src/main/resources/META-INF/spring.factories
# Esperado: org.springframework.boot.diagnostics.FailureAnalyzer=io.spring.infrastructure.config.JwtSecretFailureAnalyzer
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| ADR | ADR-006 (`docs/06-architecture-decisions.md`) |
| Issue | `closes #31` |
| PR | #32 (MERGED) |
| Commit | `2dc86d7 feat(security): add fail-fast with clear error message when JWT_SECRET is absent` |
