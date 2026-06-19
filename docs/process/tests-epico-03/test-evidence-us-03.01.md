# Evidência de testes — US-03.01

> **História:** US-03.01 — Criar `Dockerfile` multi-stage para a aplicação Spring Boot
> **Branch:** `chore/us-03.01-dockerfile`
> **PR:** #54 — OPEN
> **Issue:** #53
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| JWT_SECRET | `test-secret-for-ci-at-least-32-characters-long` |
| Comando de teste | `./gradlew test --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |
| Docker | Não disponível no ambiente de shell (verificações CA-05 a CA-09 pendentes) |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ Nenhuma regressão introduzida. US-03.01 adiciona apenas `Dockerfile` e `.dockerignore` — nenhum arquivo `.java` ou `.properties` foi modificado.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `Dockerfile` — raiz do repositório | ✅ presente (`ls Dockerfile`) |
| `.dockerignore` — raiz do repositório | ✅ presente (`ls .dockerignore`) |
| Stage build: `eclipse-temurin:11-jdk-alpine AS build` | ✅ `grep "FROM.*jdk" Dockerfile` |
| Stage runtime: `eclipse-temurin:11-jre-alpine` | ✅ `grep "FROM.*jre" Dockerfile` |
| `EXPOSE 8080` presente | ✅ `grep "EXPOSE" Dockerfile` |
| `ENTRYPOINT ["java", "-jar", "app.jar"]` | ✅ `grep "ENTRYPOINT" Dockerfile` |
| `.dockerignore` exclui `build/`, `.git/`, `docs/` | ✅ conteúdo verificado |

---

## Critérios de aceite verificados

| CA | Descrição | Status | Saída |
|---|---|---|---|
| CA-01 | `Dockerfile` presente na raiz | ✅ | `-rw-r--r-- Dockerfile` |
| CA-02 | `.dockerignore` presente na raiz | ✅ | `-rw-r--r-- .dockerignore` |
| CA-03 | Stage build usa `eclipse-temurin:11-jdk-alpine` | ✅ | `FROM eclipse-temurin:11-jdk-alpine AS build` |
| CA-04 | Stage runtime usa `eclipse-temurin:11-jre-alpine` | ✅ | `FROM eclipse-temurin:11-jre-alpine` |
| CA-05 | `docker build -t realworld-app .` → exit 0 | ⏳ pendente | Docker não disponível no ambiente de shell |
| CA-06 | `docker run -e JWT_SECRET=<valor>` → app sobe | ⏳ pendente | Docker não disponível |
| CA-07 | `curl http://localhost:8080/tags` → HTTP 200 | ⏳ pendente | Docker não disponível |
| CA-08 | `docker run` sem `JWT_SECRET` → fail-fast | ⏳ pendente | Docker não disponível |
| CA-09 | Imagem runtime < 300 MB | ⏳ pendente | Docker não disponível |

---

## Verificações estruturais executadas

```bash
# V1 — Dockerfile e .dockerignore presentes
$ ls -la Dockerfile .dockerignore
-rw-r--r--@ 1 franciele.paulino  staff   640 Jun 18 17:18 .dockerignore
-rw-r--r--@ 1 franciele.paulino  staff  1497 Jun 18 17:18 Dockerfile

# V2 — Stage build usa jdk-alpine
$ grep "FROM.*jdk" Dockerfile
FROM eclipse-temurin:11-jdk-alpine AS build

# V3 — Stage runtime usa jre-alpine
$ grep "FROM.*jre" Dockerfile
FROM eclipse-temurin:11-jre-alpine

# V4 — Suíte Java
$ JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --console=plain
BUILD SUCCESSFUL in 14s
5 actionable tasks: 2 executed, 3 up-to-date
```

---

## Observações

- **Bleeding commit:** executado com sucesso — `git stash pop` falhou silenciosamente após o merge no `bleeding` (bug conhecido no script quando há mudanças não commitadas). Arquivos restaurados via `git show bleeding:Dockerfile` e `git show bleeding:.dockerignore`.
- **CA-05 a CA-09:** requerem Docker Desktop instalado e em execução. Serão validados quando `docker compose up` for executado em US-03.04 ou manualmente pelo time.
- **Sem alterações Java:** nenhum arquivo `.java`, `.properties` ou `.yml` foi modificado — apenas infraestrutura de build.
