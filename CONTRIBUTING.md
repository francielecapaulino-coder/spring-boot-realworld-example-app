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

## Configuração de ambiente

Antes de rodar `./gradlew bootRun` ou os testes, configure as variáveis de ambiente obrigatórias.

### Variáveis JWT (obrigatórias)

| Variável | Obrigatória? | Padrão se ausente | Referência |
|---|---|---|---|
| `JWT_SECRET` | **Sim** — a aplicação não inicia sem ela | Nenhum (fail-fast) | ADR-006 |
| `JWT_SESSION_TIME` | Não | `86400` (24h) | ADR-006 |

**Como gerar `JWT_SECRET` de forma segura:**

```bash
export JWT_SECRET=$(openssl rand -base64 64)
```

Nunca use valores fracos, placeholders conhecidos ou o mesmo valor entre ambientes diferentes.

**Configuração rápida:**

```bash
cp .env.example .env
# edite .env e preencha JWT_SECRET com o comando acima
source .env
```

### Perfis Spring (`SPRING_PROFILES_ACTIVE`)

| Perfil | `JWT_SESSION_TIME` padrão | Quando usar |
|---|---|---|
| `dev` | 604800 (7 dias) | Desenvolvimento local |
| `staging` | 86400 (24 horas) | Homologação |
| `prod` | 3600 (1 hora) | Produção |

```bash
export SPRING_PROFILES_ACTIVE=dev
```

Veja `.env.example` para o template completo e `docs/06-architecture-decisions.md` (ADR-006) para o racional completo da decisão.

---

## Onboarding — ambiente local com Docker Compose

> Tempo estimado: < 15 minutos com conexão de internet estável.

### Pré-requisitos

| Ferramenta | Versão mínima | Verificação |
|---|---|---|
| Docker Desktop / Engine | 24.x+ | `docker --version` |
| Docker Compose | v2.x+ | `docker compose version` |
| Python 3 | 3.10+ | `python3 --version` |
| Git | 2.x+ | `git --version` |

### Passo a passo

1. Clone o repositório:

   ```bash
   git clone https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app.git
   cd spring-boot-realworld-example-app
   ```

2. Crie o arquivo `.env` a partir do exemplo e preencha os segredos:

   ```bash
   cp .env.example .env
   # Gere e copie os valores para o .env (ou exporte no shell atual):
   export JWT_SECRET=$(openssl rand -base64 64)
   export POSTGRES_PASSWORD=$(openssl rand -base64 32)
   ```

3. Suba a stack completa (6 serviços):

   ```bash
   docker compose up -d
   ```

4. Aguarde os serviços ficarem saudáveis (~60s) e verifique:

   ```bash
   curl http://localhost:8080/actuator/health   # {"status":"UP"}
   curl http://localhost:8080/tags              # {"tags":[...]}
   ```

5. Acesse os serviços:

   - Aplicação:  http://localhost:8080
   - Grafana:    http://localhost:3000  (admin/admin)
   - Prometheus: http://localhost:9090

6. (Opcional) Valide o startup e o shutdown via script:

   ```bash
   pip install -r requirements-scripts.txt
   python3 scripts/validate_startup.py
   # Esperado: exit code 0 — ✓ Startup validated / ✓ Shutdown validated
   ```

7. Para encerrar o ambiente:

   ```bash
   docker compose down
   ```

### Troubleshooting

| Problema | Causa provável | Solução |
|---|---|---|
| `app` não sobe | `JWT_SECRET` ausente no `.env` | Gerar com `openssl rand -base64 64` (fail-fast, ADR-006) |
| `postgres` unhealthy | `POSTGRES_PASSWORD` não configurada | Verificar `POSTGRES_PASSWORD` no `.env` |
| Porta 8080 ocupada | Outro processo usando a porta | `lsof -i :8080` e encerrar o processo |
| Grafana sem fontes de dados | Volume de provisioning não montado | Verificar a seção `grafana.volumes` no `docker-compose.yml` |
| `validate_startup.py` falha com timeout | App ainda iniciando (boot lento) | Aguardar mais e reexecutar; ver `docker compose logs app` |

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
