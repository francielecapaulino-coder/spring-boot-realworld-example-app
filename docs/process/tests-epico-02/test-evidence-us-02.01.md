# Evidência de testes — US-02.01

> **História:** US-02.01 — Remover `jwt.secret` e `jwt.sessionTime` hardcoded do `application.properties`
> **Branch:** `fix/us-02.01-remove-jwt-hardcoded-values`
> **PR:** #30 — MERGED
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

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ Nenhuma regressão introduzida. US-02.01 altera apenas `application.properties` — nenhum arquivo `.java` foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `src/main/resources/application.properties` — linha `jwt.secret` | ✅ usa `${JWT_SECRET}` (sem fallback — fail-fast) |
| `src/main/resources/application.properties` — linha `jwt.sessionTime` | ✅ usa `${JWT_SESSION_TIME:86400}` (fallback seguro) |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | `jwt.secret=mySecretKey` removido de `application.properties` | ✅ linha substituída por `jwt.secret=${JWT_SECRET}` |
| CA-02 | `jwt.sessionTime=86400` removido de `application.properties` | ✅ linha substituída por `jwt.sessionTime=${JWT_SESSION_TIME:86400}` |
| CA-03 | Comentários explicativos adicionados para cada variável | ✅ presentes em `application.properties` |
| CA-04 | `JWT_SECRET` sem fallback (fail-fast conforme ADR-006) | ✅ `${JWT_SECRET}` — sem valor default |
| CA-05 | `JWT_SESSION_TIME` com fallback `:86400` (conforme ADR-006) | ✅ `${JWT_SESSION_TIME:86400}` |
| CA-06 | `grep "mySecretKey" src/main/resources/` retorna 0 resultados | ✅ 0 resultados |
| CA-07 | CI passa com `JWT_SECRET` gerado dinamicamente | ✅ workflow gera via `openssl rand -base64 64` |

---

## Verificação de segurança

```bash
# Comando executado
grep -r "mySecretKey\|jwt\.secret=[a-zA-Z]\|password=[a-zA-Z]" src/main/resources/
# Resultado: 0 ocorrências ✅
```

---

## Diff aplicado

```diff
- jwt.secret=mySecretKey
+ # JWT - chave secreta para assinatura de tokens
+ # Obrigatoria: a aplicacao NAO inicia sem esta variavel
+ # Configurar com: export JWT_SECRET=$(openssl rand -base64 64)
+ jwt.secret=${JWT_SECRET}

- jwt.sessionTime=86400
+ # Tempo de expiracao dos tokens JWT em segundos
+ # Opcional: usa 86400 (24h) como padrao se nao definida
+ # Exemplos: dev=604800 (7d), staging=86400 (24h), prod=3600 (1h)
+ jwt.sessionTime=${JWT_SESSION_TIME:86400}
```

---

## Como reproduzir

```bash
# Verificar que não há hardcoded secrets
grep -r "mySecretKey" src/main/resources/
# Esperado: 0 resultados

# Verificar valores atuais de jwt no application.properties
grep "jwt" src/main/resources/application.properties
# Esperado:
# jwt.secret=${JWT_SECRET}
# jwt.sessionTime=${JWT_SESSION_TIME:86400}

# Suíte Java (com JWT_SECRET injetado)
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| ADR | ADR-006 (`docs/06-architecture-decisions.md`) |
| Issue | `closes #29` |
| PR | #30 (MERGED) |
| Commit | `0765a8e fix(security): remove jwt.secret and jwt.sessionTime hardcoded values` |
