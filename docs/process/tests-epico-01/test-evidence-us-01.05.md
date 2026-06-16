# Evidência de testes — US-01.05

> **História:** US-01.05 — Documentar Definition of Done (DoD)
> **Branch:** `docs/us-01.05-definition-of-done`
> **PR:** #14 — MERGED
> **Data de execução:** 2026-06-15

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Comando | `./gradlew test --rerun-tasks --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 68 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ A história US-01.05 adiciona apenas documentação de processo — **nenhuma regressão** introduzida no código Java.

---

## Artefatos entregues

| Artefato | Status |
|---|---|
| `docs/process/definition-of-done.md` (253 linhas) | ✅ presente |

---

## Verificação estrutural da DoD

| Seção | Obtido | Status |
|---|---|---|
| "O que é a Definition of Done" | presente | ✅ |
| "Gates obrigatórios" | presente | ✅ |
| Gate "CI verde (build + commitlint)" | presente | ✅ |
| Gate "Pitest ≥ 95%" (histórias com mudança de código Java) | presente | ✅ |
| Gate "Testes de integração com Testcontainers PostgreSQL" | presente | ✅ |
| Gate "Aprovação explícita da PM antes do merge" | presente | ✅ |
| Gate "Playwright E2E para APIs" | presente | ✅ |
| "Critérios adicionais por tipo" (feat, fix, docs…) | presente | ✅ |
| "Checklist rápido" | presente | ✅ |
| "O que NÃO é requisito" | presente | ✅ |

---

## Verificação de CI

| Check | Status |
|---|---|
| `build` (Java CI) | ✅ verde |
| `commitlint` | ✅ verde |

---

## Como reproduzir

```bash
# Verificar artefato
ls docs/process/definition-of-done.md
wc -l docs/process/definition-of-done.md   # esperado: ~253

# Verificar gate Pitest
cat docs/process/definition-of-done.md | grep -i "pitest"

# Suíte Java
./gradlew test --rerun-tasks --console=plain
```
