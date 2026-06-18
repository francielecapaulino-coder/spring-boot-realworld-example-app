# Evidência de testes — EPIC-02 (Segurança e eliminação de vulnerabilidades)

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Épico:** EPIC-02 — Segurança e eliminação de vulnerabilidades (Marco M1)
> **Versão:** 1.0 · Junho 2026
> **Ambiente:** OpenJDK 11.0.31 · Gradle 7.4 (wrapper) · TruffleHog v3.95.5

---

## 1. Suíte de testes Java (Gradle)

| Métrica | Valor |
|---|---|
| Comando | `JWT_SECRET=<gerado> ./gradlew test --rerun-tasks --console=plain` |
| Data de execução | **2026-06-18** |
| Resultado | **BUILD SUCCESSFUL** |
| **Total de testes** | **73** |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |
| **Classes de teste** | 21 |

> O EPIC-02 adicionou **5 novos testes** via `JwtSecretFailureAnalyzerTest` (US-02.02). As demais histórias entregam configuração e documentação — sem código Java de produção alterado.

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
| **io.spring.infrastructure.config.JwtSecretFailureAnalyzerTest** | **5** | 0 | 0 |
| io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest | 2 | 0 | 0 |
| io.spring.infrastructure.service.DefaultJwtServiceTest | 3 | 0 | 0 |
| io.spring.infrastructure.user.MyBatisUserRepositoryTest | 4 | 0 | 0 |
| **Total** | **73** | **0** | **0** |

---

## 2. US-02.01 — Remover `jwt.secret` e `jwt.sessionTime` hardcoded

**PR:** #30 · **Branch:** `fix/us-02.01-remove-jwt-hardcoded-values` · **Status:** ✅ MERGED

> Evidência detalhada em [`test-evidence-us-02.01.md`](./test-evidence-us-02.01.md)

### Artefatos entregues

| Artefato | Status |
|---|---|
| `src/main/resources/application.properties` — `jwt.secret=${JWT_SECRET}` | ✅ presente |
| `src/main/resources/application.properties` — `jwt.sessionTime=${JWT_SESSION_TIME:86400}` | ✅ presente |
| Comentários explicativos em ambas as linhas | ✅ presentes |

### Verificação de segurança

| Critério | Resultado | Status |
|---|---|---|
| `grep "mySecretKey" src/main/resources/` | 0 ocorrências | ✅ |
| `jwt.secret` usa `${JWT_SECRET}` sem fallback (fail-fast) | confirmado | ✅ |
| `jwt.sessionTime` usa `${JWT_SESSION_TIME:86400}` (com fallback seguro) | confirmado | ✅ |

---

## 3. US-02.02 — Fail-fast com mensagem clara quando `JWT_SECRET` está ausente

**PR:** #32 · **Branch:** `feat/us-02.02-jwt-secret-fail-fast-message` · **Status:** ✅ MERGED

> Evidência detalhada em [`test-evidence-us-02.02.md`](./test-evidence-us-02.02.md)

### Artefatos entregues

| Artefato | Status |
|---|---|
| `io.spring.infrastructure.config.JwtSecretFailureAnalyzer` | ✅ presente |
| `META-INF/spring.factories` — registra o analyzer | ✅ presente |
| `JwtSecretFailureAnalyzerTest` — 5 testes unitários | ✅ 5/5 passando |

### Validação dos 5 testes unitários

| Teste | Status |
|---|---|
| `analyzesJwtSecretMissingException` | ✅ |
| `returnsNullWhenMessageDoesNotContainJwtSecret` | ✅ |
| `returnsNullWhenCauseMessageIsNull` | ✅ |
| `descriptionContainsApplicationCannotStart` | ✅ |
| `actionContainsEnvExampleReference` | ✅ |

---

## 4. US-02.03 — Criar `.env.example` com variáveis JWT documentadas

**PR:** #34 · **Branch:** `docs/us-02.03-env-example` · **Status:** ✅ MERGED

> Evidência detalhada em [`test-evidence-us-02.03.md`](./test-evidence-us-02.03.md)

### Artefatos entregues

| Artefato | Status |
|---|---|
| `.env.example` (84 linhas) — 4 seções: JWT, Spring Profiles, PostgreSQL, Observabilidade | ✅ presente |
| `.env` em `.gitignore` | ✅ protegido |
| `.env.example` não em `.gitignore` | ✅ commitado |

---

## 5. US-02.04 — Perfis Spring para dev, staging, prod e test

**PR:** #38 · **Branch:** `chore/us-02.04-spring-profiles` · **Status:** ✅ MERGED
**PR:** #40 · **Branch:** `chore/us-02.04-spring-profiles` · **Status:** 🟡 OPEN (YAML v1.1)

> Evidência detalhada em [`test-evidence-us-02.04.md`](./test-evidence-us-02.04.md)

### Artefatos entregues

| Artefato | `JWT_SESSION_TIME` default | Status |
|---|---|---|
| `application-dev.properties` | 604800 (7 dias) | ✅ presente |
| `application-staging.properties` | 86400 (24h) | ✅ presente |
| `application-prod.properties` | 3600 (1h) | ✅ presente |
| `application-test.properties` | SQLite in-memory | ✅ presente |

---

## 6. US-02.05 — TruffleHog como primeiro step do CI

**PR:** #44 · **Branch:** `ci/us-02.05-secret-scan-trufflehog` · **Status:** 🟡 OPEN (aguardando aprovação PM)

> Evidência detalhada em [`test-evidence-us-02.05.md`](./test-evidence-us-02.05.md)

### Artefatos entregues (branch PR #44)

| Artefato | Status |
|---|---|
| Job `secret-scan` com TruffleHog v3.95.5 | ✅ na branch |
| `build` com `needs: secret-scan` | ✅ na branch |
| `fetch-depth: 0` para histórico completo | ✅ na branch |
| `--results=verified,unknown` | ✅ na branch |

> ⚠️ **PR #44 ainda não foi mergeada.** Artefatos na branch `ci/us-02.05-secret-scan-trufflehog`, pendentes de aprovação PM.

---

## 7. US-02.06 — Documentar configuração de ambiente no `CONTRIBUTING.md`

**PR:** #48 · **Branch:** `docs/us-02.06-contributing-env-setup` · **Status:** 🟡 OPEN (aguardando aprovação PM)

> Evidência detalhada em [`test-evidence-us-02.06.md`](./test-evidence-us-02.06.md)

### Artefatos entregues (branch PR #48)

| Artefato | Status |
|---|---|
| `CONTRIBUTING.md` — seção `## Configuração de ambiente` | ✅ na branch (linha 27) |
| Tabela `JWT_SECRET` / `JWT_SESSION_TIME` com obrigatoriedade e ADR | ✅ |
| Instrução `openssl rand -base64 64` | ✅ |
| Tabela de perfis com `JWT_SESSION_TIME` por ambiente | ✅ |

> ⚠️ **PR #48 ainda não foi mergeada.** Artefatos na branch `docs/us-02.06-contributing-env-setup`, pendentes de aprovação PM.

---

## 8. Status de CI por Pull Request

| História | PR | `secret-scan` (TruffleHog) | `build` (Java CI) | `commitlint` |
|---|---|---|---|---|
| US-02.01 — remover hardcoded | #30 | ⬜ pré-TruffleHog | ✅ | ✅ |
| US-02.02 — fail-fast analyzer | #32 | ⬜ pré-TruffleHog | ✅ | ✅ |
| US-02.03 — `.env.example` | #34 | ⬜ pré-TruffleHog | ✅ | ✅ |
| US-02.04 — Spring profiles | #38 | ⬜ pré-TruffleHog | ✅ | ✅ |
| US-02.05 — TruffleHog CI | #44 | ✅ (self-test) | ✅ | ✅ |
| US-02.06 — CONTRIBUTING env | #48 | ✅ | ✅ | ✅ |

> ⬜ PRs #30/#32/#34/#38 foram mergeadas antes da implementação do TruffleHog (US-02.05). O scan foi adicionado posteriormente na sequência correta.

---

## 9. Verificação de artefatos — resumo

| História | Artefato(s) | Status |
|---|---|---|
| US-02.01 | `application.properties` — env vars sem hardcoded | ✅ MERGED |
| US-02.02 | `JwtSecretFailureAnalyzer.java` + `spring.factories` + 5 testes | ✅ MERGED |
| US-02.03 | `.env.example` (84 linhas, 4 seções) | ✅ MERGED |
| US-02.04 | 4 arquivos de perfil `application-*.properties` | ✅ MERGED (#38) · 🟡 YAML pendente (#40) |
| US-02.05 | `gradle.yml` — job `secret-scan` + `needs: secret-scan` | 🟡 PR #44 aguarda merge |
| US-02.06 | `CONTRIBUTING.md` — seção de ambiente | 🟡 PR #48 aguarda merge |

---

## 10. Métricas de segurança (OKR 6 — KR6.5)

| Métrica | Antes | Depois | Status |
|---|---|---|---|
| M-SEC-01: Secrets hardcoded no repositório | 2 (`mySecretKey`, `86400`) | 0 | ✅ Marco M1 atingido |
| M-SEC-02: `JWT_SECRET` via variável de ambiente | ❌ | ✅ | ✅ |
| M-SEC-03: `JWT_SESSION_TIME` via variável de ambiente | ❌ | ✅ | ✅ |
| M-SEC-04: Fail-fast com mensagem diagnóstica clara | ❌ | ✅ | ✅ |
| M-SEC-05: Scan automático de secrets no CI (TruffleHog) | ❌ | ✅ (PR #44) | 🟡 aguarda merge |

---

## 11. Pendências conhecidas

- **US-02.04 PR #40:** Conversão de perfis de `.properties` para `.yml` aguardando aprovação PM. Base funcional já mergeada via PR #38.
- **US-02.05 PR #44:** TruffleHog CI configurado e testado — aguardando aprovação PM para entrar no `master`.
- **US-02.06 PR #48:** Seção de ambiente no `CONTRIBUTING.md` — aguardando aprovação PM para entrar no `master`.

---

## 12. Como reproduzir

```bash
# Suíte Java completa (73 testes, 0 falhas)
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long \
  ./gradlew test --rerun-tasks --console=plain
# Relatório HTML: build/reports/tests/test/index.html

# Verificar remoção de hardcoded secrets (US-02.01)
grep "jwt" src/main/resources/application.properties
# Esperado: ${JWT_SECRET} e ${JWT_SESSION_TIME:86400}

# Verificar scan de segurança
grep -r "mySecretKey\|jwt\.secret=[a-zA-Z]" src/main/resources/
# Esperado: 0 resultados

# Verificar JwtSecretFailureAnalyzerTest (US-02.02)
JWT_SECRET=test-secret-for-ci-at-least-32-characters-long \
  ./gradlew test --tests "io.spring.infrastructure.config.JwtSecretFailureAnalyzerTest"
# Esperado: 5 testes passando

# Verificar .env.example (US-02.03)
git ls-files .env.example
grep "JWT_SECRET" .env.example

# Verificar perfis Spring (US-02.04)
ls src/main/resources/application-*.properties
grep "sessionTime" src/main/resources/application-dev.properties        # 604800
grep "sessionTime" src/main/resources/application-prod.properties       # 3600

# Verificar TruffleHog no CI (US-02.05 — branch da PR)
git show ci/us-02.05-secret-scan-trufflehog:.github/workflows/gradle.yml | grep -A 5 "trufflehog"
```

---

| Versão | Data | O que mudou |
|---|---|---|
| 1.0 | 2026-06-18 | Evidência consolidada inicial — 73 testes, 6 histórias, segurança JWT completa |

> Veja também evidências individuais: [`test-evidence-us-02.01.md`](./test-evidence-us-02.01.md) · [`test-evidence-us-02.02.md`](./test-evidence-us-02.02.md) · [`test-evidence-us-02.03.md`](./test-evidence-us-02.03.md) · [`test-evidence-us-02.04.md`](./test-evidence-us-02.04.md) · [`test-evidence-us-02.05.md`](./test-evidence-us-02.05.md) · [`test-evidence-us-02.06.md`](./test-evidence-us-02.06.md)
