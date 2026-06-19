# Evidência de testes — US-03.05

> **História:** US-03.05 — Configurar fontes de dados do Grafana via provisioning automático
> **Branch:** `chore/us-03.05-grafana-provisioning`
> **PR:** a abrir
> **Issue:** #61
> **Data de execução:** 2026-06-18

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Docker / Docker Compose | ⏳ **indisponível no ambiente local** (`docker: command not found`) |
| Validador YAML | Python 3.13 `yaml.safe_load` |
| Arquivos entregues | `grafana/provisioning/datasources/datasources.yml`, `docker-compose.yml` (volume de provisioning) |

> Os critérios funcionais que exigem `docker compose up` e chamadas à API do Grafana
> (CA-06, CA-07) **não puderam ser executados localmente** por ausência de Docker.
> São validáveis com Docker Desktop seguindo as verificações V4–V6 da US.
> Os critérios estruturais (CA-01 a CA-05) foram validados por inspeção do YAML.

---

## Resultado dos critérios de aceite

| CA | Tipo | Descrição | Status | Evidência |
|---|---|---|---|---|
| CA-01 | Estrutural | `grafana/provisioning/datasources/datasources.yml` presente | ✅ | arquivo criado |
| CA-02 | Conteúdo | Datasource Prometheus com URL `http://prometheus:9090` | ✅ | `names['Prometheus']` |
| CA-03 | Conteúdo | Datasource Loki com URL `http://loki:3100` | ✅ | `names['Loki']` |
| CA-04 | Conteúdo | Datasource Tempo com URL `http://tempo:3200` | ✅ | `names['Tempo']` |
| CA-05 | Docker | Volume de provisioning montado no `docker-compose.yml` | ✅ | `./grafana/provisioning:/etc/grafana/provisioning` |
| CA-06 | Funcional | Grafana acessível em `http://localhost:3000` (admin/admin) | ⏳ pendente Docker | requer Docker Desktop |
| CA-07 | Funcional | 3 fontes de dados listadas em `/api/datasources` | ⏳ pendente Docker | requer Docker Desktop |

---

## Verificação estrutural executada

```text
CA-01 file present: OK
CA-02 Prometheus url: http://prometheus:9090
CA-03 Loki url: http://loki:3100
CA-04 Tempo url: http://tempo:3200
CA-05 grafana volumes: ['grafana_data:/var/lib/grafana', './grafana/provisioning:/etc/grafana/provisioning']
CA-05 provisioning volume mounted: OK
ALL STRUCTURAL CHECKS PASSED
```

---

## Conformidade com guardrails e escopo

| Verificação | Resultado |
|---|---|
| Guardrail `docker-compose.yml` (infra) → confirmação do usuário | ✅ obtida antes da execução |
| `editable: false` nas 3 fontes (provisioning imutável) | ✅ |
| Escopo respeitado — sem dashboards/alertas (EPIC-10) | ✅ apenas datasources |
| Escopo respeitado — sem alterar scrape targets do Prometheus | ✅ |
| Sem segredos hardcoded | ✅ |

---

## Como validar com Docker (passos manuais)

```bash
cp .env.example .env   # preencha JWT_SECRET e POSTGRES_PASSWORD
docker compose up -d
sleep 30
curl -s -o /dev/null -w "%{http_code}" http://localhost:3000     # V4 — 200/302
curl -s -u admin:admin http://localhost:3000/api/datasources \
  | python3 -c "import json,sys; print(len(json.load(sys.stdin)), 'datasources')"   # V5 — 3 datasources
docker compose down
```
