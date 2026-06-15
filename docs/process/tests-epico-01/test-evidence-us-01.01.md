# Evidência de testes — US-01.01

> **História:** US-01.01 — Configurar commitlint no CI com regras Conventional Commits
> **Branch:** `chore/setup-commitlint`
> **PR:** #2 — MERGED
> **Data de execução:** 2026-06-15

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Node.js | 20+ |
| commitlint | v19 (`@commitlint/config-conventional`) |
| Comando Java | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 68 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ A história US-01.01 adiciona apenas tooling de CI (arquivos de configuração e workflow YAML) — **nenhuma regressão** introduzida no código Java.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `.commitlintrc.yml` | ✅ presente na raiz |
| `package.json` (`devDependencies`: commitlint) | ✅ presente |
| `package-lock.json` | ✅ commitado |
| `.github/workflows/commitlint.yml` | ✅ presente |
| `node_modules/` em `.gitignore` | ✅ configurado |

---

## Validação funcional do commitlint

Comando: `echo "<mensagem>" | npx commitlint`

| CA | Tipo | Input | Resultado esperado | Status |
|---|---|---|---|---|
| CA-01 | Negativo | `ajuste no controller` | exit 1 · `type may not be empty` | ✅ |
| CA-02 | Negativo | `update: fix something` | exit 1 · `type must be one of [feat, fix, chore...]` | ✅ |
| CA-03 | Negativo | `FEAT: add something` | exit 1 · `type must be lower-case` | ✅ |
| CA-04 | Negativo | `feat(articles): Add reading time` | exit 1 · `subject must not be sentence-case` | ✅ |
| CA-05 | Negativo | `feat(articles): add reading time.` | exit 1 · `subject may not end with full stop` | ✅ |
| CA-06 | Positivo | `feat(articles): add reading time estimation` | exit 0 · `0 problems` | ✅ |
| CA-07 | Positivo | `chore: upgrade gradle to 9.3.1` | exit 0 · `0 problems` | ✅ |

---

## Verificação do CI

| Check | Status |
|---|---|
| `commitlint` no CI (`.github/workflows/commitlint.yml`) | ✅ presente e ativo |
| CI configurado para branches-ignore: `master`, `bleeding` | ✅ correto |
| CI disparado em `pull_request` (opened, synchronize, reopened) | ✅ correto |

---

## Como reproduzir

```bash
# Validar mensagem válida (esperado: exit 0)
echo "feat(articles): add reading time estimation" | npx commitlint

# Validar mensagem inválida (esperado: exit 1)
echo "ajuste no controller" | npx commitlint

# Suíte Java
./gradlew test --rerun-tasks --console=plain
```
