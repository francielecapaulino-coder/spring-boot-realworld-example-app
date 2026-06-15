# Evidência de testes — US-01.06

> **História:** US-01.06 — Setup do branch `bleeding` e automação de harness development
> **Branch:** `chore/us-01.06-bleeding-branch-setup`
> **PR:** #16 — MERGED
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

✅ A história US-01.06 adiciona script de automação e documentação de processo — **nenhuma regressão** introduzida no código Java.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `scripts/bleeding-commit.sh` | ✅ presente, `-rwxr-xr-x` (executável) |
| `docs/process/harness-development.md` | ✅ presente |
| `CONTRIBUTING.md` | ✅ presente |

---

## Testes funcionais do script `bleeding-commit.sh`

Comando: `./scripts/bleeding-commit.sh "<msg>" "<ref>"`

| Teste | Tipo | Input | Resultado esperado | Status |
|---|---|---|---|---|
| 1 — mensagem inválida | Negativo | `"mensagem inválida"` + `"refs #1"` | exit 1 + erro vermelho (Conventional Commits) | ✅ |
| 2 — referência inválida | Negativo | `"chore(ci): test"` + `"issue 1"` | exit 1 + erro vermelho (formato `closes/refs #N`) | ✅ |
| 3 — inputs válidos | Positivo | `"chore(ci): step 1/1 - test..."` + `"refs #15"` | exit 0 + sucesso verde + commit no `bleeding` | ✅ |
| 4 — branch restaurada | Positivo | `git branch --show-current` pós-execução | branch de trabalho original restaurada | ✅ |

> O Teste 3 gerou o commit real `34b6ff6` no branch `bleeding`, validando o fluxo completo:
> **stash → checkout bleeding → merge → commit → push → checkout original → pop stash**

---

## Verificação de permissão

```bash
ls -la scripts/bleeding-commit.sh
# Saída esperada: -rwxr-xr-x ... scripts/bleeding-commit.sh
```

| Verificação | Status |
|---|---|
| Arquivo executável (`+x`) | ✅ |

---

## Verificação de CI

| Check | Status |
|---|---|
| `build` (Java CI) | ✅ verde |
| `commitlint` | ✅ verde |

---

## Como reproduzir

```bash
# Verificar permissão
ls -la scripts/bleeding-commit.sh

# Testar validação de entrada (negativo — esperado: exit 1)
./scripts/bleeding-commit.sh "mensagem inválida" "refs #1"

# Suíte Java
./gradlew test --rerun-tasks --console=plain
```
