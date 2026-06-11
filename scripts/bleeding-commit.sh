#!/usr/bin/env bash
#
# bleeding-commit.sh — Harness development automation
#
# Commits the current branch state into the `bleeding` tracking branch.
# Usage: ./scripts/bleeding-commit.sh "<conventional commit message>" "<refs #N | closes #N>"
#
set -e

# --- Cores ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

REPO_URL="https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app"

usage() {
  echo -e "${YELLOW}Uso:${NC} ./scripts/bleeding-commit.sh \"<mensagem conventional commit>\" \"<refs #N | closes #N>\""
  echo -e "${YELLOW}Exemplo:${NC} ./scripts/bleeding-commit.sh \"feat(articles): step 1/3 - add reading time field\" \"refs #42\""
}

# --- Validação de argumentos ---
if [ "$#" -ne 2 ]; then
  echo -e "${RED}Erro: são necessários exatamente 2 argumentos.${NC}"
  usage
  exit 1
fi

COMMIT_MSG="$1"
ISSUE_REF="$2"

# --- Validação da referência de issue ---
if ! echo "$ISSUE_REF" | grep -qE '^(refs|closes) #[0-9]+$'; then
  echo -e "${RED}Erro: referência de issue inválida: '${ISSUE_REF}'.${NC}"
  echo -e "${YELLOW}Formato esperado:${NC} 'refs #N' ou 'closes #N' (ex: 'refs #42')."
  exit 1
fi

# --- Validação Conventional Commits ---
if ! echo "$COMMIT_MSG" | grep -qE '^(feat|fix|chore|docs|test|refactor|ci|perf|style)(\([a-z-]+\))?: .+'; then
  echo -e "${RED}Erro: mensagem fora do padrão Conventional Commits: '${COMMIT_MSG}'.${NC}"
  echo -e "${YELLOW}Formato esperado:${NC} 'tipo(escopo): descrição' (tipos: feat, fix, chore, docs, test, refactor, ci, perf, style)."
  exit 1
fi

# --- Fluxo principal ---
CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
STASHED="false"

# 2. Stash de mudanças não commitadas
if ! git diff --quiet || ! git diff --cached --quiet; then
  git stash push -u -m "bleeding-commit-temp-${CURRENT_BRANCH}" >/dev/null 2>&1 || true
  STASHED="true"
fi

restore_branch() {
  git checkout "$CURRENT_BRANCH" >/dev/null 2>&1 || true
  if [ "$STASHED" = "true" ]; then
    git stash pop >/dev/null 2>&1 || true
  fi
}

# 3-4. Checkout bleeding + pull rebase
git checkout bleeding >/dev/null 2>&1
git pull origin bleeding --rebase >/dev/null 2>&1 || true

# 5. Merge da branch atual (sem commitar ainda; silenciar merge vazio)
git merge "$CURRENT_BRANCH" --no-ff --no-commit >/dev/null 2>&1 || true

# 6. Stage
git add -A

# 7. Há algo staged?
if git diff --cached --quiet; then
  echo -e "${YELLOW}Nada novo para commitar no bleeding. Restaurando estado anterior.${NC}"
  git merge --abort >/dev/null 2>&1 || true
  restore_branch
  exit 0
fi

# 8. Commit composto
TIMESTAMP="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
FULL_MSG="$(printf '%s\n\n%s\nbleeding-branch-auto-commit: %s\nsource-branch: %s' \
  "$COMMIT_MSG" "$ISSUE_REF" "$TIMESTAMP" "$CURRENT_BRANCH")"
git commit -m "$FULL_MSG" >/dev/null

# 9. Push
git push origin bleeding >/dev/null 2>&1

# 10-11. Restaurar branch + stash
restore_branch

# 12-13. Sucesso
echo -e "${GREEN}✓ Commit no bleeding concluído com sucesso!${NC}"
echo -e "${GREEN}Histórico:${NC} ${REPO_URL}/commits/bleeding"
