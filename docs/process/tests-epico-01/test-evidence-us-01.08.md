# Evidência de testes — US-01.08

> **História:** US-01.08 — Documentar guia de instalação e uso do GitAhead
> **Branch:** `docs/us-01.08-gitahead-guide`
> **PR:** #22 — 🟡 OPEN (aguardando aprovação PM)
> **Data de execução:** 2026-06-15

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |
| GitAhead | v2.7.1 (`https://github.com/gitahead/gitahead`) |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 68 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ A história US-01.08 adiciona apenas documentação do guia GitAhead — **nenhuma regressão** introduzida no código Java.

---

## Artefatos entregues (branch `docs/us-01.08-gitahead-guide`)

| Artefato | Detalhes | Status |
|---|---|---|
| `docs/process/gitahead-guide.md` | 261 linhas, v1.0 | ✅ na branch PR #22 |
| `README.md` | link ao guia adicionado | ✅ na branch PR #22 |

> ⚠️ **PR #22 ainda não foi mergeada.** Os artefatos estarão no `master` após aprovação explícita da PM.

---

## Verificação estrutural do guia

| Seção | Conteúdo | Status |
|---|---|---|
| `## 1. O que é o GitAhead` | Descrição, licença MIT, versão v2.7.1 | ✅ |
| `## 2. Instalação` | macOS (binário universal Intel+Apple Silicon), Windows 10+, Linux (Gittyup) | ✅ |
| `## 3. Configuração inicial` | Clone do repositório, SSH key | ✅ |
| `## 4. Uso no workflow` | Visualização do branch `bleeding`, auditoria de Conventional Commits | ✅ |
| `## 5. Atalhos úteis` | presente | ✅ |
| `## 6. Troubleshooting` | presente | ✅ |
| `## 7. Checklist DoR` | presente | ✅ |
| `## 8. Atualizações deste guia` | presente | ✅ |

### Informações técnicas verificadas

| Item | Valor | Status |
|---|---|---|
| Versão confirmada | v2.7.1 (dezembro 2023) | ✅ |
| URL do repositório oficial | `https://github.com/gitahead/gitahead` | ✅ |
| Alternativa Gittyup documentada (Linux) | presente | ✅ |
| Aviso sobre descontinuação (último release v2.7.1) | presente | ✅ |

---

## CI da PR #22

| Check | Status |
|---|---|
| `build` (Java CI) | ✅ verde |
| `commitlint` | ✅ verde |

---

## Como reproduzir

```bash
# Verificar artefatos na branch
git checkout docs/us-01.08-gitahead-guide
ls docs/process/gitahead-guide.md
wc -l docs/process/gitahead-guide.md   # esperado: ~261

# Verificar versão do GitAhead no guia
grep "v2.7.1" docs/process/gitahead-guide.md

# Suíte Java
./gradlew test --rerun-tasks --console=plain
```
