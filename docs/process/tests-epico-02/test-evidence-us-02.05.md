# Evidência de testes — US-02.05

> **História:** US-02.05 — Configurar TruffleHog como primeiro step do CI para bloquear commits com secrets
> **Branch:** `ci/us-02.05-secret-scan-trufflehog`
> **PR:** #44 — OPEN (aguardando aprovação PM)
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| TruffleHog | v3.95.5 (`trufflesecurity/trufflehog@v3.95.5`) |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ US-02.05 altera apenas `.github/workflows/gradle.yml` — nenhum arquivo `.java` foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `.github/workflows/gradle.yml` — job `secret-scan` (TruffleHog) | ✅ presente na branch `ci/us-02.05-secret-scan-trufflehog` |
| Job `build` com `needs: secret-scan` (sequencial) | ✅ build só executa se scan passar |
| TruffleHog configurado com `--results=verified,unknown` | ✅ detecta secrets verificados e desconhecidos |

---

## Critérios de aceite verificados

| CA | Descrição | Verificação |
|---|---|---|
| CA-01 | Job `secret-scan` adicionado como primeiro job do CI | ✅ aparece antes de `build` no YAML |
| CA-02 | `build` tem dependência `needs: secret-scan` | ✅ |
| CA-03 | TruffleHog versão fixada (`@v3.95.5`) para reproducibilidade | ✅ |
| CA-04 | `actions/checkout@v4` com `fetch-depth: 0` para histórico completo | ✅ |
| CA-05 | Scan faz diff entre `HEAD` e `default_branch` (apenas commits novos) | ✅ `base: ${{ github.event.repository.default_branch }}` |
| CA-06 | `extra_args: --results=verified,unknown` (não bloqueia em `unverified`) | ✅ |
| CA-07 | Scan ativo em `push` e `pull_request` em todas as branches | ✅ |

---

## Estrutura do workflow verificado (branch PR #44)

```yaml
jobs:
  secret-scan:
    name: Scan for secrets (TruffleHog)
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - name: Fetch base branch for diff
        run: git fetch origin ${{ github.event.repository.default_branch }}:...
      - uses: trufflesecurity/trufflehog@v3.95.5
        with:
          path: ./
          base: ${{ github.event.repository.default_branch }}
          head: HEAD
          extra_args: --results=verified,unknown

  build:
    needs: secret-scan   # ← build só inicia após scan passar
    ...
```

---

## Status de PR e CI

| Item | Status |
|---|---|
| PR #44 — branch `ci/us-02.05-secret-scan-trufflehog` | 🟡 OPEN — aguardando aprovação PM |
| CI verde na branch (commitlint + build) | ✅ (verificado via GitHub Actions da PR) |
| `master` ainda sem TruffleHog | ⚠️ aguarda merge da PR #44 |

---

## Como reproduzir

```bash
# Verificar configuração do TruffleHog na branch da PR
git show ci/us-02.05-secret-scan-trufflehog:.github/workflows/gradle.yml | grep -A 15 "secret-scan"
# Esperado: job secret-scan com trufflehog@v3.95.5

# Verificar que build depende de secret-scan
git show ci/us-02.05-secret-scan-trufflehog:.github/workflows/gradle.yml | grep "needs"
# Esperado: needs: secret-scan

# Verificar versão do TruffleHog
git show ci/us-02.05-secret-scan-trufflehog:.github/workflows/gradle.yml | grep "trufflehog"
# Esperado: trufflesecurity/trufflehog@v3.95.5

# Suíte Java
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --rerun-tasks --console=plain
# Esperado: BUILD SUCCESSFUL — 73 testes, 0 falhas
```

---

## Rastreabilidade

| Item | Referência |
|---|---|
| Issue | `closes #43` |
| PR | #44 (OPEN — aguarda merge) |
| Marco | M1 — critério: "`truffleHog scan .` retorna 0 findings críticos" |
| Roadmap | `docs/04-roadmap.md` v5.0 — Fase 1 |
