# AGENTS.md — Instruções para o agente (Coda)

Diretrizes que o agente deve seguir ao trabalhar neste repositório.

---

## Repositório de trabalho

- Todo o trabalho (issues, branches, commits, pull requests) vive no **fork**:
  `francielecapaulino-coder/spring-boot-realworld-example-app`.
- O upstream `gothinkster/spring-boot-realworld-example-app` **não** deve receber issues nem PRs — o autor não tem permissão de merge lá.

## Fluxo obrigatório por história (a partir da US-01.04)

1. **Criar a issue ANTES de qualquer código** (premissa do projeto: sem issue, sem código).
2. Criar a branch a partir do `master` atualizado.
3. Implementar com commits no padrão **Conventional Commits** (validados pelo commitlint no CI).
4. Cada commit referencia a issue: `closes #XX` ou `refs #XX`.
5. Abrir a Pull Request usando o template `.github/PULL_REQUEST_TEMPLATE.md`.
6. **PARAR no PR aberto. NÃO MERGEAR.**
   - A PR permanece **OPEN** aguardando **aprovação explícita da PM**.
   - Só mergear após a PM aprovar e pedir explicitamente o merge.
7. A issue permanece **aberta** até o fechamento formal pós-aprovação.

> ⚠️ Regra crítica: o agente **nunca** deve mergear uma PR por conta própria.
> Merge só acontece mediante solicitação explícita do usuário/PM.

## Convenções

- **Conventional Commits** em 100% dos commits. Tipos válidos:
  `feat`, `fix`, `chore`, `docs`, `test`, `refactor`, `ci`, `perf`, `style`.
- **DoR e DoD**: fonte de verdade em `docs/process/`.
- **Pitest ≥ 95%** como gate de qualidade quando há mudança de código Java.
- **Testes de integração** com Testcontainers PostgreSQL (sem H2/SQLite).
- **Playwright** para E2E via APIRequestContext.
- **OpenAPI/Swagger** para documentação de contratos.
- **Coda**: prompts e skills documentados a cada história.

## Escopo de commits

- Commitar apenas os arquivos pertinentes à história em andamento.
- Não incluir modificações pré-existentes fora de escopo (ex.: `docs/01-08` editados em outra frente).

## Verificação de testes

- Comando da suíte: `./gradlew test` (Java 11, Gradle 7.4 via wrapper).
- Forçar reexecução quando necessário: `./gradlew test --rerun-tasks`.
- Documentar evidência de testes relevante em `docs/process/`.
