# Evidência de testes — US-03.08

> **História:** US-03.08 — Atualizar `CONTRIBUTING.md` com guia de onboarding completo
> **Branch:** `docs/us-03.08-contributing-onboarding`
> **PR:** a abrir
> **Issue:** #68
> **Data de execução:** 2026-06-19

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Arquivo modificado | `CONTRIBUTING.md` (seção `## Onboarding — ambiente local com Docker Compose`) |
| Tipo | `docs` (sem guardrail de infra/auth) |
| Compilação Java | OpenJDK 11 / Gradle 7.4 (apenas sanity — nenhum código alterado) |

---

## Resultado dos critérios de aceite

| CA | Tipo | Descrição | Status | Evidência |
|---|---|---|---|---|
| CA-01 | Estrutural | Seção `## Onboarding — ambiente local com Docker Compose` presente | ✅ | linha 70 |
| CA-02 | Conteúdo | Pré-requisitos com comandos de verificação | ✅ | `docker --version`, `docker compose version`, `python3 --version`, `git --version` |
| CA-03 | Conteúdo | Passo a passo: clone → `.env` → `docker compose up` → verificação | ✅ | `git clone`, `cp .env.example .env`, `docker compose up -d`, `curl .../actuator/health` |
| CA-04 | Conteúdo | URLs dos serviços (app:8080, Grafana:3000, Prometheus:9090) | ✅ | 5 ocorrências de `localhost:8080/3000/9090` |
| CA-05 | Conteúdo | Instrução para executar `validate_startup.py` | ✅ | `python3 scripts/validate_startup.py` (passo 6) |
| CA-06 | Conteúdo | Seção de troubleshooting com erros comuns | ✅ | `### Troubleshooting` (linha 134) com 5 entradas |
| CA-07 | Conteúdo | Comando `docker compose down` documentado | ✅ | passo 7 |

---

## Verificações executadas (V1–V5)

```text
# V1 — seção de onboarding presente
70:## Onboarding — ambiente local com Docker Compose

# V2 — URLs dos serviços documentadas
grep -cE "localhost:8080|localhost:3000|localhost:9090" CONTRIBUTING.md  ->  5

# V3 — script de validação referenciado
124:   python3 scripts/validate_startup.py

# V4 — troubleshooting presente
134:### Troubleshooting

# V5 — suíte Java não afetada (apenas docs)
./gradlew compileJava compileTestJava  ->  BUILD SUCCESSFUL
```

---

## Observações

- Conteúdo alinhado ao estado real do repositório após US-03.04..US-03.07:
  - passo 6 usa `pip install -r requirements-scripts.txt` (criado na US-03.06) em vez de `pip install requests`;
  - troubleshooting inclui o caso de timeout do `validate_startup.py` (boot lento) com dica de `docker compose logs app`.
- A medição real do tempo de onboarding (< 15 min com dev real) é escopo da US-03.09.
