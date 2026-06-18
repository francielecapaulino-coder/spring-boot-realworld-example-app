# Evidência de testes — US-02.06

> **História:** US-02.06 — Documentar configuração de variáveis de ambiente no `CONTRIBUTING.md`
> **Branch:** `docs/us-02.06-contributing-env-setup`
> **PR:** #48 — OPEN (aguardando aprovação PM)
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

✅ US-02.06 entrega documentação (`docs`) — nenhum arquivo `.java` foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `CONTRIBUTING.md` — seção `## Configuração de ambiente` (linha 27) | ✅ presente na branch `docs/us-02.06-contributing-env-setup` |
| Subseção `### Variáveis JWT (obrigatórias)` | ✅ |
| Tabela com `JWT_SECRET` e `JWT_SESSION_TIME` | ✅ |
| Instrução `openssl rand -base64 64` | ✅ |
| Tabela de perfis (`dev` / `staging` / `prod`) com `JWT_SESSION_TIME` | ✅ |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | Seção `## Configuração de ambiente` adicionada ao `CONTRIBUTING.md` | ✅ linha 27 |
| CA-02 | `JWT_SECRET` documentada como obrigatória (fail-fast) | ✅ |
| CA-03 | `JWT_SESSION_TIME` documentada como opcional com default `86400` | ✅ |
| CA-04 | Comando `openssl rand -base64 64` para geração segura | ✅ |
| CA-05 | Tabela de perfis com valores de `JWT_SESSION_TIME` por ambiente | ✅ dev=604800, staging=86400, prod=3600 |
| CA-06 | Referência ao ADR-006 | ✅ |
| CA-07 | Instrução de uso do `.env.example` como referência | ✅ |

---

## Conteúdo verificado — seção adicionada ao `CONTRIBUTING.md`

```
Linha 27: ## Configuração de ambiente
Linha 29: Antes de rodar ./gradlew bootRun ou os testes...
Linha 31: ### Variáveis JWT (obrigatórias)
Linha 35: | JWT_SECRET     | Sim | Nenhum (fail-fast) | ADR-006 |
Linha 36: | JWT_SESSION_TIME | Não | 86400 (24h)      | ADR-006 |
Linha 38: Como gerar JWT_SECRET de forma segura:
Linha 41: export JWT_SECRET=$(openssl rand -base64 64)
Linha 56: Tabela de perfis: dev=604800 | staging=86400 | prod=3600
```

---

## Status de PR e CI

| Item | Status |
|---|---|
| PR #48 — branch `docs/us-02.06-contributing-env-setup` | 🟡 OPEN — aguardando aprovação PM |
| CI verde na branch (commitlint + build) | ✅ |
| `master` ainda sem a seção de ambiente | ⚠️ aguarda merge da PR #48 |

---

## Como reproduzir

```bash
# Verificar seção de ambiente na branch da PR
git show docs/us-02.06-contributing-env-setup:CONTRIBUTING.md | grep -A 30 "Configuração de ambiente"
# Esperado: seção completa com JWT_SECRET, JWT_SESSION_TIME e perfis

# Verificar referência ao ADR-006
git show docs/us-02.06-contributing-env-setup:CONTRIBUTING.md | grep "ADR-006"
# Esperado: pelo menos 1 referência

# Suíte Java
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| ADR | ADR-006 (`docs/06-architecture-decisions.md`) |
| Issue | `closes #47` |
| PR | #48 (OPEN — aguarda merge) |
| Commit | `ca3592a docs(user-stories): fix placeholders and add etapa 3 review log to US-02.06` |
