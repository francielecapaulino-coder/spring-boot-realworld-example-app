# Contributing — RealWorld Platform Modernization

> Repositório: `francielecapaulino-coder/spring-boot-realworld-example-app`

Antes de contribuir, leia os documentos de processo:

- **Definition of Ready:** [`docs/process/definition-of-ready.md`](docs/process/definition-of-ready.md)
- **Definition of Done:** [`docs/process/definition-of-done.md`](docs/process/definition-of-done.md)
- **Harness development:** [`docs/process/harness-development.md`](docs/process/harness-development.md)

## Fluxo resumido

1. A PM cria/aprova uma issue (sem issue, sem código).
2. Crie a branch: `tipo/us-XX.XX-descricao-curta`.
3. Documente os prompts no Coda.
4. Implemente em etapas, registrando cada uma no branch `bleeding`.
5. Abra a PR usando o template e aguarde a aprovação da PM.

## Convenções

- **Conventional Commits** em 100% dos commits (validados pelo commitlint no CI).
- Tipos: `feat`, `fix`, `chore`, `docs`, `test`, `refactor`, `ci`, `perf`, `style`.
- Todo commit referencia a issue: `closes #XX` ou `refs #XX`.

---

## Branch `bleeding` e harness development

O branch `bleeding` registra cada etapa verificável do desenvolvimento. Guia completo:
[`docs/process/harness-development.md`](docs/process/harness-development.md).

### Uso rápido

```bash
./scripts/bleeding-commit.sh "tipo(escopo): step X/Y - descrição" "refs #XX"
```

### O que o script faz

- Valida a mensagem (Conventional Commits) e a referência de issue (`refs #N` / `closes #N`).
- Faz stash das mudanças locais, alterna para `bleeding` e sincroniza com `git pull --rebase`.
- Faz merge da branch de trabalho, commita a etapa e dá push em `bleeding`.
- Restaura a branch de trabalho e o stash, sem alterar o seu estado local.

### Regras

- Commitar no `bleeding` **a cada etapa verificável** (não só no final).
- A mensagem deve indicar o progresso: `step X/Y - descrição`.
- Sempre incluir a referência da issue: `refs #XX`.
- Validar cada commit no **GitAhead** (branch `bleeding`).
