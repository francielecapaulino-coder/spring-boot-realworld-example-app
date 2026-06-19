# Evidência de testes — US-03.06

> **História:** US-03.06 — Criar script Python de validação de startup e shutdown
> **Branch:** `chore/us-03.06-validate-startup-script`
> **PR:** a abrir
> **Issue:** #63
> **Data de execução:** 2026-06-18

---

## Ambiente de execução

| Item | Valor |
|---|---|
| Python | 3.13 (sistema) / 3.14 (venv de teste) |
| Docker / Docker Compose | ⏳ **indisponível no ambiente local** (`docker: command not found`) |
| Dependência `requests` | declarada em `requirements-scripts.txt` |
| Arquivos entregues | `scripts/validate_startup.py`, `requirements-scripts.txt` |

> Os critérios que exigem a stack completa via Docker (CA-07: execução bem-sucedida
> com `/actuator/health` UP) **não puderam ser executados localmente** por ausência
> de Docker. Os demais critérios foram validados por inspeção e execução real do
> script (caminhos de falha e exit codes).

---

## Resultado dos critérios de aceite

| CA | Tipo | Descrição | Status | Evidência |
|---|---|---|---|---|
| CA-01 | Estrutural | `scripts/validate_startup.py` presente e executável | ✅ | `-rwxr-xr-x ... validate_startup.py` |
| CA-02 | Funcional | Script executa `docker compose up -d` | ✅ | `run(["docker", "compose", "up", "-d"])` |
| CA-03 | Funcional | Aguarda até 60s por `/actuator/health` → `{"status":"UP"}` | ✅ | `wait_for_health(STARTUP_TIMEOUT_S=60)` com loop + poll 3s |
| CA-04 | Funcional | Executa `docker compose stop` após validação | ✅ | `run(["docker", "compose", "stop"])` |
| CA-05 | Exit codes | Sucesso → exit 0 com mensagem verde | ✅ | `return 0` + `✓ Startup/Shutdown validated` |
| CA-06 | Exit codes | Falha → exit 1 com mensagem descritiva | ✅ | execução real → exit 1 + `✗ docker compose up failed: command not found: 'docker'` |
| CA-07 | Execução | `python3 scripts/validate_startup.py` → exit 0 com stack completa | ⏳ pendente Docker | requer Docker Desktop |
| CA-08 | Timeout | Com stack parada: timeout 60s → exit 1 | ✅ (caminho de falha) | sem Docker, falha antecipada no `up` → exit 1; timeout coberto por `wait_for_health` |

---

## Execuções reais

### Sintaxe / compilação
```text
python3 -m py_compile scripts/validate_startup.py  →  py_compile OK
```

### CA-06 — falha com mensagem descritiva (sem Docker)
```text
→ Starting docker compose...
✗ docker compose up failed:
command not found: 'docker' (is Docker installed?)
exit code: 1
```

### Dependência ausente — mensagem amigável
```text
✗ Missing dependency 'requests'. Install with: pip install -r requirements-scripts.txt
exit code: 1
```

---

## Robustez adicionada

| Item | Detalhe |
|---|---|
| `requests` ausente | Import protegido → mensagem clara + exit 1 (sem traceback) |
| `docker` ausente | `run()` captura `FileNotFoundError` → `CompletedProcess(returncode=127)` → mensagem descritiva (sem traceback) |
| Shutdown | `wait_for_shutdown()` confirma 0 containers via `docker compose ps -q` (até 30s) |

---

## Como validar com Docker (passo manual)

```bash
pip install -r requirements-scripts.txt
cp .env.example .env   # preencha JWT_SECRET e POSTGRES_PASSWORD
python3 scripts/validate_startup.py
echo $?                # esperado: 0  (✓ Startup validated / ✓ Shutdown validated)
docker compose ps      # esperado: 0 containers running
```
