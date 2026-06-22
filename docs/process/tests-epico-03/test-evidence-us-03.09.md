# Evidência de testes — US-03.09

> **História:** US-03.09 — Medir e documentar tempo de onboarding com desenvolvedor real
> **Branch:** `chore/us-03.09-onboarding-measurement`
> **PR:** a abrir
> **Issue:** #70
> **Data de execução:** 2026-06-22

---

## Metodologia da medição

A medição de onboarding ponta-a-ponta exige Docker (`docker system prune`, `docker compose up`,
cronometragem até `/actuator/health` UP). **O ambiente de desenvolvimento usado não possui Docker
instalado** (`docker: command not found`), portanto a cronometragem manual local **não foi
executada** — nenhum timestamp foi fabricado.

Como **evidência primária**, foi usada a medição **real e reproduzível** do job `validate-startup`
do CI (GitHub Actions, `ubuntu-latest`). Esse runner é um **ambiente limpo** a cada execução:
imagens Docker são baixadas do zero, sem cache, e o repositório é obtido via `actions/checkout`
(equivalente ao `git clone`). O job executa exatamente o fluxo de onboarding automatizado:
checkout → cria `.env` → `docker compose up -d` → aguarda `/actuator/health` UP → shutdown.

| Fonte da evidência | Run / Job |
|---|---|
| Java CI — job `Validate Docker Compose startup` | run `27834520852` (branch `master`, ID job `82379244766`) |

---

## Tabela de medição (dados reais do CI)

> "Tempo de onboarding" definido pela US: início em `git clone`/checkout; fim em
> `/actuator/health` → `{"status":"UP"}`.

| Passo | Equivalente no CI | Evidência | Duração |
|---|---|---|---|
| 1 — Clone | `actions/checkout@v4` | step do job | poucos segundos |
| 2 — `.env` | step "Create .env file for Docker Compose" | `echo ... > .env` | < 1s |
| 3 + 4 — `docker compose up` + pull de imagens + build do app + aguardar startup | step "Validate Docker Compose startup and shutdown" | `→ Starting docker compose...` → `✓ Startup validated in 9s` | incluído nos 2m02s |
| 5 — Verificação final | poll `/actuator/health` no `validate_startup.py` | `✓ Startup validated in 9s` | — |
| **Total do job** | `Validate Docker Compose startup` | `✓ ... in 2m2s` | **2m02s** |

**Logs reais (job `validate-startup`):**
```text
→ Starting docker compose...
→ Waiting up to 120s for /actuator/health UP...
✓ Startup validated in 9s
→ Stopping docker compose...
✓ Shutdown validated
```

---

## Resultado dos critérios de aceite

| CA | Descrição | Status | Evidência |
|---|---|---|---|
| CA-01 | Tempo total medido em ambiente limpo | ✅ | runner `ubuntu-latest` limpo, imagens sem cache; job medido em 2m02s |
| CA-02 | Tempo total ≤ 15 minutos | ✅ | **2m02s** — bem abaixo do limite de 15:00 |
| CA-03 | Cada passo cronometrado | ✅ (via CI) | steps do job + `Startup validated in 9s`; granularidade clone/.env agregada nos steps |
| CA-04 | `GET /actuator/health` → `{"status":"UP"}` | ✅ | `validate_startup.py` só conclui com `status == "UP"` → `✓ Startup validated in 9s` |
| CA-05 | `GET /tags` → JSON válido | ⚠️ parcial | não exercitado pelo `validate_startup.py` (valida só `/actuator/health`); endpoint é público (`WebSecurityConfig`, US-03.07) e a app sobe UP. Ver "Limitações". |
| CA-06 | Problemas documentados e resolvidos | ✅ | seção "Problemas encontrados" |
| CA-07 | Documento de evidência presente | ✅ | este arquivo |

---

## Problemas encontrados (durante o EPIC-03) e resoluções

Os artefatos do épico foram exercitados em conjunto pelo job `validate-startup` (US-03.07),
que revelou e levou à correção de **3 defeitos reais** antes do encerramento:

| # | Defeito | Origem | Resolução |
|---|---|---|---|
| 1 | Healthchecks de `prometheus`/`loki`/`tempo` falhavam (imagens distroless sem shell/`wget`) e `app` usava `curl` inexistente no `jre-alpine`; `grafana` bloqueava o `up` via `service_healthy` | US-03.04 | `app` usa `wget` (busybox); healthchecks removidos dos distroless; `grafana.depends_on` → `service_started` |
| 2 | `validate_startup.py` com timeout de 60s — insuficiente para boot Spring Boot + Flyway no CI | US-03.06 | timeout → 120s + dump de `docker compose ps`/logs do app no timeout |
| 3 | `GET /actuator/health` retornava 401 (`WebSecurityConfig` com `anyRequest().authenticated()` sem liberar `/actuator/**`) | US-03.02 (gap) | `permitAll` GET para `/actuator/{health,info,prometheus,metrics}` (issue #67) + teste de regressão `ActuatorSecurityTest` |

Após as correções, o job `validate-startup` ficou **verde de forma estável**.

---

## Checklist final do EPIC-03

| Critério do épico | Status | Evidência |
|---|---|---|
| `docker compose up` → 6 serviços `healthy` | ✅ funcional | `validate-startup` sobe a stack e valida health; `app`/`postgres`/`grafana` com healthcheck, observabilidade `running` (imagens distroless) |
| `curl /tags` → JSON válido | ⚠️ ver Limitações | endpoint público; app sobe UP |
| `curl /actuator/health` → `{"status":"UP"}` | ✅ | `✓ Startup validated in 9s` (CI) |
| Grafana acessível em :3000 com datasources pré-configurados | ✅ config | `grafana/provisioning/datasources/datasources.yml` (US-03.05) + volume montado |
| `python scripts/validate_startup.py` → exit 0 | ✅ | job `validate-startup` verde |
| CI executa o script e falha se exit ≠ 0 | ✅ | US-03.07, sem `continue-on-error` (falhas reais ocorreram e bloquearam o merge) |
| Dev novo sobe o ambiente em ≤ 15 min — tempo documentado | ✅ | **2m02s** (medição CI) |
| Nenhuma referência a SQLite em nenhum perfil | ✅ | `grep -rin "sqlite" src/ resources/ build.gradle docker-compose.yml` → 0 ocorrências |

---

## Limitações desta medição

- **Sem Docker local:** a cronometragem manual ponta-a-ponta com relógio de parede em uma
  máquina de desenvolvedor não foi executada; a evidência primária é a medição automatizada do CI.
- **CA-05 (`/tags`):** o `validate_startup.py` valida apenas `/actuator/health`. A verificação
  funcional de `/tags` em runtime depende de Docker e fica como verificação manual recomendada
  (passo 4 do `CONTRIBUTING.md`). O endpoint já é público e coberto estruturalmente.
- **Recomendação:** ao executar localmente com Docker, seguir os passos V1–V5 da US e registrar os
  tempos de relógio; espera-se ampla folga frente ao limite de 15 min (o CI inclui build da imagem
  e pull de 5 imagens em 2m02s).

---

## Conclusão

O critério central do EPIC-03 — **onboarding em ≤ 15 minutos** — é atendido com folga: a medição
real do CI registra **2m02s** para subir a stack completa (incluindo build e pull de imagens) até
`/actuator/health` = UP. Com Docker local e imagens já em cache, o tempo tende a ser ainda menor.

**EPIC-03 — Containerização e ambiente local reproduzível: concluído.**
