# Evidência de testes — US-02.03

> **História:** US-02.03 — Criar `.env.example` com variáveis JWT documentadas
> **Branch:** `docs/us-02.03-env-example`
> **PR:** #34 — MERGED
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ US-02.03 entrega documentação (`docs`) — nenhum arquivo `.java` foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `.env.example` (84 linhas) | ✅ presente na raiz do repositório |
| `.env` em `.gitignore` | ✅ protegido — `.env`, `.env.local`, `.env.*.local` |
| `.env.example` **não** em `.gitignore` | ✅ commitado — é o arquivo de referência |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | `.env.example` presente na raiz | ✅ |
| CA-02 | Contém `JWT_SECRET` com instruções de geração (`openssl rand -base64 64`) | ✅ |
| CA-03 | Contém `JWT_SESSION_TIME` com exemplos por ambiente (dev/staging/prod) | ✅ |
| CA-04 | Contém `SPRING_PROFILES_ACTIVE` com valor default `dev` | ✅ |
| CA-05 | Contém referência ao ADR-006 | ✅ |
| CA-06 | Contém seção comentada para PostgreSQL (EPIC-03 futuro) | ✅ |
| CA-07 | Contém seção comentada para observabilidade (EPIC-10 futuro) | ✅ |
| CA-08 | `.env` está no `.gitignore` (arquivo real não pode ser commitado) | ✅ |
| CA-09 | `.env.example` **não** está no `.gitignore` (deve ser commitado) | ✅ |

---

## Verificação estrutural do `.env.example`

| Seção | Conteúdo | Status |
|---|---|---|
| SEÇÃO 1 — JWT | `JWT_SECRET`, `JWT_SESSION_TIME`, instruções `openssl` | ✅ |
| SEÇÃO 2 — Spring Profiles | `SPRING_PROFILES_ACTIVE=dev` com exemplos por ambiente | ✅ |
| SEÇÃO 3 — PostgreSQL | placeholder comentado (EPIC-03) | ✅ |
| SEÇÃO 4 — Observabilidade | placeholder comentado (EPIC-10) | ✅ |
| Referências | Links para ADR-006, DoR, CONTRIBUTING.md | ✅ |

---

## Verificação de segurança

```bash
# .env real protegido
grep "^\.env" .gitignore
# Resultado: .env ✅

# .env.example acessível
git ls-files .env.example
# Resultado: .env.example ✅
```

---

## Como reproduzir

```bash
# Verificar que .env.example existe e está no repositório
git ls-files .env.example
# Esperado: .env.example

# Verificar que .env está no .gitignore
grep "^\.env$" .gitignore
# Esperado: .env

# Verificar conteúdo de JWT_SECRET no .env.example
grep "JWT_SECRET" .env.example
# Esperado: JWT_SECRET=<gere com openssl rand -base64 64>

# Suíte Java (sem mudanças Java — apenas regressão)
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| ADR | ADR-006 (`docs/06-architecture-decisions.md`) |
| Issue | `closes #33` |
| PR | #34 (MERGED) |
| Commit | `ee22891 docs(security): create .env.example with jwt variables and future placeholders` |
