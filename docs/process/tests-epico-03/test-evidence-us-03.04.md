# Evidência de testes — US-03.04

> **História:** US-03.04 — Criar `docker-compose.yml` com app + PostgreSQL + stack LGTM
> **Branch:** `chore/us-03.04-docker-compose`
> **PR:** a abrir
> **Issue:** #59
> **Data de execução:** 2026-06-18

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Docker / Docker Compose | ⏳ **indisponível no ambiente local** (`docker: command not found`) |
| Validador YAML | Python 3.13 `yaml.safe_load` |
| Arquivos entregues | `docker-compose.yml` (raiz), `.env.example` (Seção 3 PostgreSQL) |

> Os critérios de aceite que exigem `docker compose up/ps/down` e `curl` aos endpoints
> (CA-06 a CA-10) **não puderam ser executados localmente** por ausência de Docker.
> São validáveis com Docker Desktop instalado, seguindo as verificações V3–V8 da US.
> Os critérios estruturais (CA-01 a CA-05) foram validados por inspeção do YAML.

---

## Resultado dos critérios de aceite

| CA | Tipo | Descrição | Status | Evidência |
|---|---|---|---|---|
| CA-01 | Estrutural | `docker-compose.yml` presente na raiz | ✅ | `ls docker-compose.yml` |
| CA-02 | Serviços | 6 serviços: `app`, `postgres`, `prometheus`, `loki`, `tempo`, `grafana` | ✅ | `yaml.safe_load` → 6 serviços |
| CA-03 | Healthcheck | `app` usa `/actuator/health` no healthcheck | ✅ | `CMD curl -f http://localhost:8080/actuator/health` |
| CA-04 | Dependência | `app` tem `depends_on: postgres: condition: service_healthy` | ✅ | `condition == service_healthy` |
| CA-05 | Variáveis | `JWT_SECRET` e `JWT_SESSION_TIME` injetados no serviço `app` | ✅ | ambas presentes em `app.environment` |
| CA-06 | Execução | `docker compose up -d` → 6 serviços `healthy` | ⏳ pendente Docker | requer Docker Desktop |
| CA-07 | Funcional | `curl http://localhost:8080/tags` → HTTP 200 JSON | ⏳ pendente Docker | requer Docker Desktop |
| CA-08 | Funcional | `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` | ⏳ pendente Docker | requer Docker Desktop |
| CA-09 | Funcional | Grafana acessível em `http://localhost:3000` | ⏳ pendente Docker | requer Docker Desktop |
| CA-10 | Cleanup | `docker compose down` → 0 serviços running | ⏳ pendente Docker | requer Docker Desktop |

---

## Verificação estrutural executada

```text
CA-01 file present: OK
CA-02 services: ['app', 'postgres', 'prometheus', 'loki', 'tempo', 'grafana']
CA-02 6 services: OK
CA-03 app healthcheck: CMD curl -f http://localhost:8080/actuator/health
CA-03 actuator/health: OK
CA-04 app depends_on postgres condition: service_healthy
CA-04 service_healthy: OK
CA-05 JWT vars in app env: OK
ALL STRUCTURAL CHECKS PASSED
```

---

## Conformidade com guardrails e ADRs

| Verificação | Resultado |
|---|---|
| Guardrail `docker-compose.yml` (infra) → confirmação do usuário | ✅ solicitada e concedida antes da execução |
| ADR-006 — `JWT_SECRET`/`JWT_SESSION_TIME` injetados via `.env` (sem hardcode) | ✅ `${JWT_SECRET}`, `${JWT_SESSION_TIME:-604800}` |
| Sem segredos hardcoded no compose | ✅ todos os valores sensíveis via `${...}` |
| Sem referência a SQLite/H2 | ✅ 0 ocorrências; `postgres:16-alpine` |
| Escopo respeitado — não configura datasources do Grafana (US-03.05) | ✅ apenas serviço `grafana` declarado, sem provisioning |

---

## Como validar com Docker (passos manuais)

```bash
cp .env.example .env
# preencha JWT_SECRET (openssl rand -base64 64) e POSTGRES_PASSWORD (openssl rand -base64 32)
docker compose config            # V1/V2 — YAML válido, 6 serviços
docker compose up -d             # V3
sleep 60 && docker compose ps    # V4 — 6 serviços healthy
curl -s http://localhost:8080/actuator/health   # V5 — {"status":"UP"}
curl -s http://localhost:8080/tags               # V6 — {"tags":[...]}
curl -s -o /dev/null -w "%{http_code}" http://localhost:3000   # V7 — 200/302
docker compose down              # V8 — 0 serviços running
```
