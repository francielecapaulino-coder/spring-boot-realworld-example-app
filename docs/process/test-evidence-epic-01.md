# Evidência de testes — EPIC-01 (Fundação do processo)

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Épico:** EPIC-01 — Fundação do processo de desenvolvimento (Marco M0)
> **Versão:** 2.0 · Junho 2026
> **Ambiente:** OpenJDK 11.0.31 · Gradle 7.4 (wrapper) · Node.js 20+ · commitlint v19

---

## 1. Suíte de testes Java (Gradle)

| Métrica | Valor |
|---|---|
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Data de execução | **2026-06-15** |
| Resultado | **BUILD SUCCESSFUL** |
| **Total de testes** | **68** |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |
| **Classes de teste** | 20 |

> O EPIC-01 entrega documentação e tooling de processo (não código Java de produção), portanto a suíte serve como **rede de segurança contra regressão** — nenhuma das histórias alterou o comportamento da aplicação.

### Detalhamento por classe

| Classe | Testes | Falhas | Erros |
|---|---|---|---|
| io.spring.RealworldApplicationTests | 1 | 0 | 0 |
| io.spring.api.ArticleApiTest | 6 | 0 | 0 |
| io.spring.api.ArticleFavoriteApiTest | 2 | 0 | 0 |
| io.spring.api.ArticlesApiTest | 3 | 0 | 0 |
| io.spring.api.CommentsApiTest | 5 | 0 | 0 |
| io.spring.api.CurrentUserApiTest | 6 | 0 | 0 |
| io.spring.api.ListArticleApiTest | 3 | 0 | 0 |
| io.spring.api.ProfileApiTest | 3 | 0 | 0 |
| io.spring.api.UsersApiTest | 7 | 0 | 0 |
| io.spring.application.article.ArticleQueryServiceTest | 9 | 0 | 0 |
| io.spring.application.comment.CommentQueryServiceTest | 2 | 0 | 0 |
| io.spring.application.profile.ProfileQueryServiceTest | 1 | 0 | 0 |
| io.spring.application.tag.TagsQueryServiceTest | 1 | 0 | 0 |
| io.spring.core.article.ArticleTest | 5 | 0 | 0 |
| io.spring.infrastructure.article.ArticleRepositoryTransactionTest | 1 | 0 | 0 |
| io.spring.infrastructure.article.MyBatisArticleRepositoryTest | 3 | 0 | 0 |
| io.spring.infrastructure.comment.MyBatisCommentRepositoryTest | 1 | 0 | 0 |
| io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest | 2 | 0 | 0 |
| io.spring.infrastructure.service.DefaultJwtServiceTest | 3 | 0 | 0 |
| io.spring.infrastructure.user.MyBatisUserRepositoryTest | 4 | 0 | 0 |
| **Total** | **68** | **0** | **0** |

---

## 2. US-01.01 — Configurar commitlint no CI

**PR:** #2 · **Branch:** `chore/setup-commitlint` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `.commitlintrc.yml` (regras Conventional Commits) | ✅ presente |
| `package.json` (devDependencies: commitlint) | ✅ presente |
| `package-lock.json` (gerado e commitado) | ✅ presente |
| `.github/workflows/commitlint.yml` (CI workflow) | ✅ presente |
| `node_modules/` no `.gitignore` | ✅ presente |

### Validação do commitlint

Comando: `echo "<mensagem>" | npx commitlint`

| CA | Input | Resultado esperado | Status |
|---|---|---|---|
| CA-01 | `ajuste no controller` | falha · `type may not be empty` | ✅ |
| CA-02 | `update: fix something` | falha · `type must be one of [...]` | ✅ |
| CA-03 | `FEAT: add something` | falha · `type must be lower-case` | ✅ |
| CA-04 | `feat(articles): Add reading time` | falha · `subject must not be sentence-case` | ✅ |
| CA-05 | `feat(articles): add reading time.` | falha · `subject may not end with full stop` | ✅ |
| CA-06 | `feat(articles): add reading time estimation` | passa · `0 problems` | ✅ |
| CA-07 | `chore: upgrade gradle to 9.3.1` | passa · `0 problems` | ✅ |

> Todos os commits deste épico foram validados pelo commitlint antes do push (exit 0).

---

## 3. US-01.02 — Templates de GitHub Issues

**PR:** #3 · **Branch:** `feat/us-01.02-issue-templates` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `.github/ISSUE_TEMPLATE/config.yml` (`blank_issues_enabled: false`) | ✅ presente |
| `.github/ISSUE_TEMPLATE/feat.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/fix.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/test.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/docs.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/chore.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/refactor.yml` | ✅ presente |
| `.github/ISSUE_TEMPLATE/spike.yml` | ✅ presente |

### Verificação estrutural

| Critério | Esperado | Obtido | Status |
|---|---|---|---|
| Total de arquivos em `ISSUE_TEMPLATE/` | 8 (config + 7 tipos) | 8 | ✅ |
| `blank_issues_enabled` | `false` | `false` | ✅ |
| Issues criadas via template (US-01.01, US-01.02, ...) | referência `closes #XX` | presentes | ✅ |

---

## 4. US-01.03 — Template de Pull Request

**PR:** #4 · **Branch:** `chore/us-01.03-pr-template` · **Status:** ✅ MERGED

> Evidência detalhada disponível em [`test-evidence-us-01.03.md`](./test-evidence-us-01.03.md)

### Verificação estrutural do template

| Critério | Esperado | Obtido | Status |
|---|---|---|---|
| Seções `##` | 8 | 12 | ✅ |
| Checkboxes totais | 32 | 32 | ✅ |
| Tipos Conventional Commits | 9 | 9 | ✅ |
| Seções `<details>` colapsáveis | 3 | 3 | ✅ |
| `closes #` pré-preenchido | 1 | 1 | ✅ |

---

## 5. US-01.04 — Definition of Ready

**PR:** #12 · **Branch:** `docs/us-01.04-definition-of-ready` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `docs/process/definition-of-ready.md` (227 linhas) | ✅ presente |

### Verificação estrutural

| Critério | Obtido | Status |
|---|---|---|
| Seção "Critérios universais" | presente | ✅ |
| Seção "Critérios adicionais por tipo" | presente | ✅ |
| Seção "Checklist rápido" | presente | ✅ |
| Referência ao Coda como pré-requisito | presente | ✅ |
| Referência ao GitAhead | presente | ✅ |

---

## 6. US-01.05 — Definition of Done

**PR:** #14 · **Branch:** `docs/us-01.05-definition-of-done` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `docs/process/definition-of-done.md` (253 linhas) | ✅ presente |

### Verificação estrutural

| Critério | Obtido | Status |
|---|---|---|
| Seção "Gates obrigatórios" | presente | ✅ |
| Gate "Pitest ≥ 95%" (histórias com código Java) | presente | ✅ |
| Gate "Testes de integração com Testcontainers" | presente | ✅ |
| Gate "CI verde (build + commitlint)" | presente | ✅ |
| Seção "O que NÃO é requisito" | presente | ✅ |

---

## 7. US-01.06 — Branch `bleeding` e script de harness development

**PR:** #16 · **Branch:** `chore/us-01.06-bleeding-branch-setup` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `scripts/bleeding-commit.sh` (`-rwxr-xr-x`) | ✅ presente, executável |
| `docs/process/harness-development.md` | ✅ presente |
| `CONTRIBUTING.md` | ✅ presente |

### Testes do script `bleeding-commit.sh`

Comando: `./scripts/bleeding-commit.sh "<msg>" "<ref>"`

| Teste | Input | Resultado esperado | Status |
|---|---|---|---|
| 1 — mensagem inválida | `"mensagem inválida"` + `"refs #1"` | exit 1 + erro vermelho (Conventional Commits) | ✅ |
| 2 — referência inválida | `"chore(ci): test"` + `"issue 1"` | exit 1 + erro vermelho (formato de issue) | ✅ |
| 3 — inputs válidos | `"chore(ci): step 1/1 - test..."` + `"refs #15"` | exit 0 + sucesso verde + commit no `bleeding` | ✅ |
| 4 — branch restaurada | `git branch --show-current` | branch de trabalho restaurada | ✅ |

> O Teste 3 gerou o commit real `34b6ff6` no branch `bleeding`, validando o fluxo stash → checkout → merge → commit → push → restore.

---

## 8. US-01.07 — Guia do Coda

**PR:** #18 · **Branch:** `chore/us-01.07-coda-workspace-setup` · **Status:** ✅ MERGED

### Artefatos entregues

| Artefato | Status |
|---|---|
| `docs/process/coda-guide.md` | ✅ presente |
| Links de documentação no `README.md` | ✅ presente |

### Verificação estrutural

| Critério | Obtido | Status |
|---|---|---|
| Seção "Workspace do projeto" | presente | ✅ |
| Seção de instruções de uso | presente | ✅ |
| Marcador `TODO-CODA-URL` (URL pendente parte manual) | 4 marcadores | ⚠️ pendente |

> **Pendência:** A URL real do workspace Coda não foi inserida. Os marcadores `TODO-CODA-URL` devem ser substituídos quando a PARTE A (manual) for concluída.

---

## 9. US-01.08 — Guia de instalação e uso do GitAhead

**PR:** #22 · **Branch:** `docs/us-01.08-gitahead-guide` · **Status:** 🟡 OPEN — aguardando aprovação PM

### Artefatos entregues (na branch `docs/us-01.08-gitahead-guide`)

| Artefato | Status |
|---|---|
| `docs/process/gitahead-guide.md` (261 linhas) | ✅ na branch PR #22 |
| `README.md` atualizado com link ao guia | ✅ na branch PR #22 |

### Verificação estrutural do guia

| Critério | Obtido | Status |
|---|---|---|
| Seção "O que é o GitAhead" | presente | ✅ |
| Seção "Instalação" (macOS / Windows / Linux) | presente | ✅ |
| Seção "Configuração inicial" | presente | ✅ |
| Seção "Uso no workflow" | presente | ✅ |
| Seção "Troubleshooting" | presente | ✅ |
| Seção "Checklist DoR" | presente | ✅ |
| Versão confirmada: v2.7.1 | presente | ✅ |
| URLs reais do repositório GitAhead | presentes | ✅ |
| Alternativa Gittyup documentada | presente | ✅ |

> ⚠️ **PR #22 ainda não foi mergeada.** Os artefatos acima estão na branch `docs/us-01.08-gitahead-guide` e entrarão no `master` após aprovação explícita da PM.

---

## 10. Status de CI por Pull Request

| História | PR | `build` (Java CI) | `commitlint` |
|---|---|---|---|
| US-01.01 — commitlint | #2 | histórico¹ | ✅ |
| US-01.02 — issue templates | #3 | histórico¹ | ✅ |
| US-01.03 — PR template | #4 | histórico¹ | ✅ |
| US-01.04 — DoR | #12 | ✅ | ✅ |
| US-01.05 — DoD | #14 | ✅ | ✅ |
| US-01.06 — bleeding | #16 | ✅ | ✅ |
| US-01.07 — Coda | #18 | ✅ | ✅ |
| US-01.08 — GitAhead | #22 | ✅ | ✅ |

> ¹ As PRs #2/#3/#4 foram criadas antes da correção das GitHub Actions deprecadas (PR #10, `actions/cache@v2 → v4`). O `build` passou a rodar verde no `master` a partir de então. Conteúdo já integrado e `master` verde.

---

## 11. Verificação de artefatos — resumo

| História | Artefato(s) | Verificação |
|---|---|---|
| US-01.01 | `.commitlintrc.yml`, `package.json`, `package-lock.json`, `.github/workflows/commitlint.yml` | ✅ 4 arquivos + gitignore |
| US-01.02 | `.github/ISSUE_TEMPLATE/` (config + 7 templates) | ✅ 8 arquivos |
| US-01.03 | `.github/PULL_REQUEST_TEMPLATE.md` | ✅ 12 seções, 32 checkboxes |
| US-01.04 | `docs/process/definition-of-ready.md` | ✅ 227 linhas |
| US-01.05 | `docs/process/definition-of-done.md` | ✅ 253 linhas, 5+ gates |
| US-01.06 | `scripts/bleeding-commit.sh` (`-rwxr-xr-x`), `harness-development.md`, `CONTRIBUTING.md` | ✅ 3 arquivos |
| US-01.07 | `docs/process/coda-guide.md` | ✅ presente (`TODO-CODA-URL` pendente) |
| US-01.08 | `docs/process/gitahead-guide.md` | 🟡 branch PR #22 (aguarda merge) |

---

## 12. Pendências conhecidas

- **US-01.07:** URL real do workspace Coda ainda não inserida (4 marcadores `TODO-CODA-URL`). Substituir após PARTE A (manual) concluída.
- **US-01.08:** PR #22 aguardando aprovação explícita da PM para merge no `master`.
- **Issues #1, #5, #6:** Issues das histórias US-01.01/02/03 permanecem abertas formalmente até fechamento pós-aprovação.

---

## 13. Como reproduzir

```bash
# Suíte Java (68 testes, 0 falhas)
./gradlew test --rerun-tasks --console=plain
# Relatório HTML: build/reports/tests/test/index.html
# Resultados XML:  build/test-results/test/*.xml

# Validação de mensagem de commit (commitlint)
echo "feat(articles): add reading time estimation" | npx commitlint  # esperado: exit 0
echo "ajuste no controller" | npx commitlint                          # esperado: exit 1

# Verificar permissão do script de bleeding
ls -la scripts/bleeding-commit.sh  # esperado: -rwxr-xr-x

# Verificar artefatos no repositório
ls .github/ISSUE_TEMPLATE/          # 8 arquivos
ls docs/process/                    # DoR, DoD, coda-guide, harness-development
cat docs/process/definition-of-done.md | grep "Pitest"  # gate Pitest ≥ 95%
```

---

| Versão | Data | O que mudou |
|---|---|---|
| 1.0 | Junho 2026 | Evidência consolidada inicial — Gradle, commitlint, bleeding script, CI e artefatos |
| 2.0 | 2026-06-15 | Revisão completa — evidência por história (US-01.01..08), reexecução Gradle, verificação estrutural detalhada por artefato |

> Veja também: [`test-evidence-us-01.03.md`](./test-evidence-us-01.03.md) · [`definition-of-done.md`](./definition-of-done.md)
