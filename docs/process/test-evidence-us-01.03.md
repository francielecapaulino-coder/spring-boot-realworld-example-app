# Evidência de testes — US-01.03

> História: **US-01.03 — Criar template de Pull Request com checklist de DoD e link para Coda**
> Branch: `chore/us-01.03-pr-template`
> Data de execução: 2026-06-11

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** (12s) |

---

## Resumo

| Métrica | Valor |
|---|---|
| **Total de testes** | 68 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |
| **Classes de teste** | 20 |

✅ Todos os testes passaram. A mudança desta história adiciona apenas `.github/PULL_REQUEST_TEMPLATE.md` (Markdown) — **nenhuma regressão** introduzida no código Java.

---

## Detalhamento por classe

| Classe de teste | Testes |
|---|---|
| io.spring.RealworldApplicationTests | 1 |
| io.spring.api.ArticleApiTest | 6 |
| io.spring.api.ArticleFavoriteApiTest | 2 |
| io.spring.api.ArticlesApiTest | 3 |
| io.spring.api.CommentsApiTest | 5 |
| io.spring.api.CurrentUserApiTest | 6 |
| io.spring.api.ListArticleApiTest | 3 |
| io.spring.api.ProfileApiTest | 3 |
| io.spring.api.UsersApiTest | 7 |
| io.spring.application.article.ArticleQueryServiceTest | 9 |
| io.spring.application.comment.CommentQueryServiceTest | 2 |
| io.spring.application.profile.ProfileQueryServiceTest | 1 |
| io.spring.application.tag.TagsQueryServiceTest | 1 |
| io.spring.core.article.ArticleTest | 5 |
| io.spring.infrastructure.article.ArticleRepositoryTransactionTest | 1 |
| io.spring.infrastructure.article.MyBatisArticleRepositoryTest | 3 |
| io.spring.infrastructure.comment.MyBatisCommentRepositoryTest | 1 |
| io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest | 2 |
| io.spring.infrastructure.service.DefaultJwtServiceTest | 3 |
| io.spring.infrastructure.user.MyBatisUserRepositoryTest | 4 |
| **Total** | **68** |

---

## Verificação estrutural do template (US-01.03)

| Critério | Esperado | Obtido | Status |
|---|---|---|---|
| Seções `##` | 8 | 8 | ✅ |
| Checkboxes totais | 32 | 32 | ✅ |
| Tipos Conventional Commits | 9 | 9 | ✅ |
| ADRs reais | 6 + "Nenhum" | 6 | ✅ |
| Seções `<details>` colapsáveis | 3 | 3 | ✅ |
| `closes #` pré-preenchido | 1 | 1 | ✅ |

---

## Como reproduzir

```bash
cd spring-boot-realworld-example-app
./gradlew test --rerun-tasks --console=plain
# Relatório HTML: build/reports/tests/test/index.html
# Resultados XML:  build/test-results/test/*.xml
```
