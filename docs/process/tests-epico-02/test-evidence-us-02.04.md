# Evidência de testes — US-02.04

> **História:** US-02.04 — Criar perfis Spring para dev, staging, prod e test com `JWT_SESSION_TIME` por ambiente
> **Branch:** `chore/us-02.04-spring-profiles`
> **PR:** #38 — MERGED | PR #40 — OPEN (conversão para YAML v1.1)
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| JWT_SECRET | gerado via `openssl rand -base64 64` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ US-02.04 entrega apenas arquivos de configuração (`resources`) — nenhum arquivo `.java` de lógica foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `src/main/resources/application-dev.properties` | ✅ presente — `jwt.sessionTime=${JWT_SESSION_TIME:604800}` (7d) |
| `src/main/resources/application-staging.properties` | ✅ presente — `jwt.sessionTime=${JWT_SESSION_TIME:86400}` (24h) |
| `src/main/resources/application-prod.properties` | ✅ presente — `jwt.sessionTime=${JWT_SESSION_TIME:3600}` (1h) |
| `src/main/resources/application-test.properties` | ✅ presente — `spring.datasource.url=jdbc:sqlite::memory:` |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | Perfil `dev` criado com `JWT_SESSION_TIME` default 604800 (7 dias) | ✅ |
| CA-02 | Perfil `staging` criado com `JWT_SESSION_TIME` default 86400 (24h) | ✅ |
| CA-03 | Perfil `prod` criado com `JWT_SESSION_TIME` default 3600 (1h) | ✅ |
| CA-04 | Perfil `test` criado com SQLite in-memory | ✅ |
| CA-05 | Comentários explicativos em cada arquivo de perfil | ✅ |
| CA-06 | `jwt.sessionTime` em cada perfil sobrescreve o base `application.properties` | ✅ |
| CA-07 | Variável de ambiente `JWT_SESSION_TIME` pode sobrescrever qualquer perfil | ✅ (uso de `${JWT_SESSION_TIME:VALOR}`) |
| CA-08 | `SPRING_PROFILES_ACTIVE=dev` documentado no `.env.example` | ✅ (US-02.03) |

---

## Conteúdo verificado por perfil

### `application-dev.properties`
```properties
jwt.sessionTime=${JWT_SESSION_TIME:604800}
# 7 dias — sessao longa para desenvolvimento
```

### `application-staging.properties`
```properties
jwt.sessionTime=${JWT_SESSION_TIME:86400}
# 24 horas — igual ao padrao base
```

### `application-prod.properties`
```properties
jwt.sessionTime=${JWT_SESSION_TIME:3600}
# 1 hora — sessao curta para seguranca
```

### `application-test.properties`
```properties
spring.datasource.url=jdbc:sqlite::memory:
# SQLite in-memory para testes rápidos
```

---

## Status da PR #40 (conversão para YAML)

| Item | Status |
|---|---|
| PR #40 — `chore/us-02.04-spring-profiles` (YAML v1.1) | 🟡 OPEN — aguardando aprovação PM |
| Escopo: converter `.properties` → `.yml` | ⚠️ pendente merge |
| Base (`.properties`) funcional no `master` | ✅ já mergeado via PR #38 |

---

## Como reproduzir

```bash
# Verificar todos os arquivos de perfil
ls src/main/resources/application-*.properties
# Esperado: application-dev.properties, application-prod.properties,
#           application-staging.properties, application-test.properties

# Verificar JWT_SESSION_TIME por perfil
grep "sessionTime" src/main/resources/application-dev.properties        # 604800
grep "sessionTime" src/main/resources/application-staging.properties    # 86400
grep "sessionTime" src/main/resources/application-prod.properties       # 3600

# Testar ativação de perfil dev
SPRING_PROFILES_ACTIVE=dev \
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long \
./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| ADR | ADR-006 (`docs/06-architecture-decisions.md`) |
| Issue | `closes #37` |
| PR | #38 (MERGED — `.properties`) · #40 (OPEN — `.yml` v1.1) |
| Commit | `ed0b3b2 chore(security): create spring profiles dev, staging, prod with jwt session time per environment` |
