# Evidência de testes — US-01.02

> **História:** US-01.02 — Criar templates de GitHub Issues por tipo de trabalho
> **Branch:** `feat/us-01.02-issue-templates`
> **PR:** #3 — MERGED
> **Data de execução:** 2026-06-15

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 68 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ A história US-01.02 adiciona apenas templates YAML para GitHub Issues — **nenhuma regressão** introduzida no código Java.

---

## Artefatos entregues

| Arquivo | Descrição | Status |
|---|---|---|
| `.github/ISSUE_TEMPLATE/config.yml` | `blank_issues_enabled: false` + links | ✅ |
| `.github/ISSUE_TEMPLATE/feat.yml` | Template para novas features | ✅ |
| `.github/ISSUE_TEMPLATE/fix.yml` | Template para correções de bug | ✅ |
| `.github/ISSUE_TEMPLATE/test.yml` | Template para histórias de teste | ✅ |
| `.github/ISSUE_TEMPLATE/docs.yml` | Template para documentação | ✅ |
| `.github/ISSUE_TEMPLATE/chore.yml` | Template para chores (ci, perf, style) | ✅ |
| `.github/ISSUE_TEMPLATE/refactor.yml` | Template para refatoração | ✅ |
| `.github/ISSUE_TEMPLATE/spike.yml` | Template para spikes de investigação | ✅ |

**Total:** 8 arquivos (1 config + 7 templates)

---

## Verificação estrutural

| Critério | Esperado | Obtido | Status |
|---|---|---|---|
| Total de arquivos em `ISSUE_TEMPLATE/` | 8 | 8 | ✅ |
| `blank_issues_enabled` em `config.yml` | `false` | `false` | ✅ |
| Templates cobrem: feat, fix, test, docs, chore, refactor, spike | 7 tipos | 7 tipos | ✅ |
| Cada template tem `title` com prefixo Conventional Commits | presente | presente | ✅ |
| Checklist de DoR nos templates | presente | presente | ✅ |

---

## Premissas de gestão verificadas

| Premissa | Status |
|---|---|
| `blank_issues_enabled: false` — issues sem template bloqueadas | ✅ |
| Issues criadas para US-01.01 (#1), US-01.02 (#5), US-01.03 (#6) usam os templates | ✅ |
| Campo `closes #XX` presente nos templates para rastreabilidade | ✅ |

---

## Como reproduzir

```bash
# Verificar templates
ls .github/ISSUE_TEMPLATE/

# Verificar configuração
cat .github/ISSUE_TEMPLATE/config.yml

# Suíte Java
./gradlew test --rerun-tasks --console=plain
```
