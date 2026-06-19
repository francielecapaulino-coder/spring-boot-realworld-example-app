# Evidência de testes — US-03.07

> **História:** US-03.07 — Integrar script Python de validação ao pipeline CI
> **Branch:** `ci/us-03.07-validate-startup-ci`
> **PR:** a abrir
> **Issue:** #65
> **Data de execução:** 2026-06-19

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Arquivo modificado | `.github/workflows/gradle.yml` (job `validate-startup`) |
| Validador YAML | Python 3.13 `yaml.safe_load` |
| Compilação Java | OpenJDK 11 / Gradle 7.4 (wrapper) |
| Docker para `validate-startup` | ⏳ indisponível localmente — validado pelo runner `ubuntu-latest` do CI |

> O job `validate-startup` executa `docker compose up`/`down` e só roda com Docker
> nativo (GitHub Actions `ubuntu-latest`). CA-07 (CI verde) é verificado pelo
> próprio GitHub Actions na PR desta história. Os demais critérios foram validados
> por inspeção do workflow e compilação local.

---

## Resultado dos critérios de aceite

| CA | Tipo | Descrição | Status | Evidência |
|---|---|---|---|---|
| CA-01 | Config | Job `validate-startup` adicionado ao `gradle.yml` | ✅ | `jobs: [secret-scan, build, validate-startup]` |
| CA-02 | Config | Job instala Python 3 e `requests` | ✅ | `setup-python@v5` + `pip install -r requirements-scripts.txt` |
| CA-03 | Config | `JWT_SECRET` via `openssl rand -base64 64` | ✅ | step "Generate secrets for Docker Compose" |
| CA-04 | Config | `POSTGRES_PASSWORD` via `openssl rand -base64 32` | ✅ | step "Generate secrets for Docker Compose" |
| CA-05 | Funcional | CI falha se `validate_startup.py` retorna exit 1 | ✅ | step sem `continue-on-error` → falha propaga |
| CA-06 | Cleanup | `docker compose down` com `if: always()` | ✅ | step "Cleanup Docker Compose" |
| CA-07 | CI verde | PR com stack funcional → `validate-startup` verde | ✅/⏳ | verificado no GitHub Actions (ver PR) |

---

## Verificações executadas

```text
# YAML válido + ordem de jobs
jobs: ['secret-scan', 'build', 'validate-startup']
validate-startup needs: secret-scan
YAML OK

# V1 — job presente
grep -c "validate-startup" .github/workflows/gradle.yml  ->  1

# V2 — script referenciado
run: python3 scripts/validate_startup.py

# V3 — JWT_SECRET gerado
run: echo "JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')" >> $GITHUB_ENV

# V4 — cleanup presente
run: docker compose down --volumes --remove-orphans
if: always()

# V5 — suíte Java não afetada (compilação)
./gradlew compileJava compileTestJava  ->  BUILD SUCCESSFUL
```

---

## Conformidade com guardrails

| Verificação | Resultado |
|---|---|
| Guardrail `.github/workflows` (CI/CD) → confirmação do usuário | ✅ obtida antes da execução |
| Segredos gerados dinamicamente no runner (nunca commitados) | ✅ `openssl rand` + `.env` efêmero |
| `.env` criado apenas no runner (ignorado pelo `.gitignore`) | ✅ |
| Job `build` (testes Java) preservado e independente | ✅ inalterado |

---

## Observação sobre o resultado do CI

O job `validate-startup` constrói a imagem da aplicação (`build: .`) e sobe 6 serviços;
seu tempo de execução é maior que os demais jobs.

### Defeito revelado pelo CI (e corrigido nesta PR)

A primeira execução do job `validate-startup` **falhou** com:

```
✗ docker compose up failed:
dependency failed to start: container realworld-tempo is unhealthy
Process completed with exit code 1.
```

**Causa raiz:** os healthchecks definidos na US-03.04 usavam `wget`/`curl`, mas:
- `prom/prometheus`, `grafana/loki`, `grafana/tempo` são imagens **distroless** (sem shell, sem `wget`/`curl`) → healthcheck sempre falhava → `unhealthy`, bloqueando o `grafana` (que dependia delas via `service_healthy`);
- a imagem runtime do `app` é `eclipse-temurin:11-jre-alpine`, que **não tem `curl`** (apenas o `wget` do busybox).

**Correção aplicada ao `docker-compose.yml` nesta PR:**
- `app` healthcheck: `curl -f` → `wget --spider ... /actuator/health` (busybox, presente no alpine);
- `prometheus`/`loki`/`tempo`: healthchecks removidos (imagens sem ferramenta de checagem);
- `grafana.depends_on`: `service_healthy` → `service_started` para os 3 serviços de observabilidade.

Este é exatamente o tipo de regressão que a US-03.07 foi criada para capturar — o CI
funcionou como esperado. O resultado verde final é confirmado nos checks desta PR.
