# Harness Development — branch `bleeding`

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Versão:** 1.0 · Junho 2026

---

## 1. O que é harness development neste projeto

**Harness development** é a prática de registrar **cada etapa verificável** de uma história em um branch de rastreamento contínuo — o branch `bleeding` — por meio de commits automáticos. Em vez de só ver o resultado final no PR, a PM e a dupla conseguem acompanhar a **evolução passo a passo** do vibe coding: cada prompt aplicado, cada teste que passou, cada incremento.

O branch `bleeding` é alimentado pelo script `scripts/bleeding-commit.sh`, que valida a mensagem (Conventional Commits) e a referência de issue antes de commitar.

---

## 2. O branch `bleeding`

| Atributo | Valor |
|---|---|
| **Nome** | `bleeding` |
| **URL** | https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/commits/bleeding |
| **Finalidade** | Rastreamento contínuo de cada etapa do harness development |
| **Proteção** | `allow_deletions: false`, `allow_force_pushes: false` |
| **Exige PR?** | **Não** — push direto é necessário para o script funcionar |
| **Origem** | Criado a partir da `master` |

---

## 3. Como usar o script

### Sintaxe

```bash
./scripts/bleeding-commit.sh "<mensagem conventional commit>" "<refs #N | closes #N>"
```

- **Argumento 1:** mensagem no padrão Conventional Commits (`tipo(escopo): descrição`).
- **Argumento 2:** referência de issue — `refs #N` (referencia) ou `closes #N` (fecha ao mergear).

### Exemplos por tipo de história

```bash
# feat — incremento de funcionalidade
./scripts/bleeding-commit.sh "feat(articles): step 1/3 - add reading_time column" "refs #42"

# refactor — etapa de reestruturação
./scripts/bleeding-commit.sh "refactor(infrastructure): step 2/4 - migrate ArticleMapper to JPA" "refs #57"

# chore — setup/configuração
./scripts/bleeding-commit.sh "chore(ci): step 1/1 - add pitest threshold gate" "refs #61"

# test — adição de testes
./scripts/bleeding-commit.sh "test(articles): step 3/5 - add integration tests for DELETE 204" "refs #48"
```

---

## 4. Quando commitar no `bleeding` por tipo de história

| Tipo | Quando disparar um bleeding-commit |
|---|---|
| `feat` | A cada incremento funcional verificável (campo, endpoint, regra) |
| `fix` | Após reproduzir o bug, após o teste de regressão e após a correção |
| `refactor` | A cada etapa que mantém os testes verdes |
| `test` | A cada grupo de testes que passa (unidade, integração, E2E) |
| `chore` | A cada bloco de configuração concluído |
| `docs` | A cada seção significativa concluída |
| `spike` | A cada evidência relevante coletada |

---

## 5. Fluxo completo (12 passos)

```text
 1. PM cria/aprova a issue no GitHub
 2. Dupla cria a branch (tipo/us-XX.XX-descricao)
 3. Coda aberto — prompt preparado (ADRs + épico)
 4. Etapa de vibe coding executada
 5. ./scripts/bleeding-commit.sh "tipo(escopo): step X/Y - ..." "refs #N"
 6. Validar a etapa no GitAhead (branch bleeding)
 7. Repetir 4-6 para cada etapa verificável
 8. DoD verificada (docs/process/definition-of-done.md)
 9. PR aberta usando .github/PULL_REQUEST_TEMPLATE.md
10. PM revisa gates e aprova
11. Merge na master
12. Issue fechada (closes #N) — bleeding mantém o histórico granular
```

---

## 6. Validando no GitAhead

1. Abra o GitAhead no repositório local.
2. No painel de branches, selecione `bleeding`.
3. Confirme que o commit mais recente corresponde à etapa que você acabou de rodar.
4. Verifique o corpo do commit: deve conter `refs #N`, `bleeding-branch-auto-commit: <timestamp>` e `source-branch: <branch>`.
5. Confirme que a `source-branch` é a branch de trabalho da sua história.

---

## 7. Resolução de problemas

### Merge conflict ao rodar o script

O script usa `git merge --no-ff --no-commit`. Se houver conflito:

```bash
# 1. Abortar o merge em andamento
git merge --abort
# 2. Voltar para a branch de trabalho
git checkout <sua-branch>
# 3. Recuperar mudanças stashed (se houver)
git stash pop
# 4. Resolver a divergência na branch de trabalho e rodar o script novamente
```

### `non-fast-forward` ao dar push no bleeding

Outra dupla atualizou o `bleeding`. O script já roda `git pull origin bleeding --rebase`, mas se persistir:

```bash
git checkout bleeding
git pull origin bleeding --rebase
git checkout <sua-branch>
./scripts/bleeding-commit.sh "<msg>" "refs #N"
```

---

> **Fonte de verdade para harness development neste repositório.**
> Veja também: [`definition-of-ready.md`](./definition-of-ready.md) · [`definition-of-done.md`](./definition-of-done.md)
