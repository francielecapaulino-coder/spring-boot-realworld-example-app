# Evidência de testes — US-03.02

> **História:** US-03.02 — Configurar Spring Actuator com `/health`, `/info` e `/metrics`
> **Branch:** `feat/us-03.02-spring-actuator`
> **PR:** a abrir
> **Issue:** #55
> **Data de execução:** 2026-06-18

---

## Ambiente

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Spring Boot | 2.6.3 |
| JWT_SECRET | `test-secret-for-ci-at-least-32-characters-long` |
| Comando de teste | `./gradlew test --console=plain` |
| Resultado do build | **BUILD SUCCESSFUL** |

---

## Suíte Java — rede de segurança contra regressão

| Métrica | Valor |
|---|---|
| **Total de testes** | 73 |
| **Falhas** | 0 |
| **Erros** | 0 |
| **Ignorados** | 0 |

✅ Nenhuma regressão introduzida pela adição das dependências Actuator e Micrometer.

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `build.gradle` — `spring-boot-starter-actuator` | ✅ `grep "actuator" build.gradle` |
| `build.gradle` — `micrometer-registry-prometheus` | ✅ `grep "micrometer-registry-prometheus" build.gradle` |
| `application.properties` — `management.endpoints.web.exposure.include` | ✅ `health,info,metrics,prometheus` |
| `application.properties` — `management.endpoint.health.show-details=always` | ✅ presente |
| `application.properties` — `info.app.name=realworld` | ✅ presente |

---

## Critérios de aceite verificados

| CA | Descrição | Status | Saída |
|---|---|---|---|
| CA-01 | `micrometer-registry-prometheus` presente no `build.gradle` | ✅ | `implementation 'io.micrometer:micrometer-registry-prometheus'` |
| CA-02 | `management.endpoints.web.exposure.include=health,info,metrics,prometheus` | ✅ | linha presente no `application.properties` |
| CA-03 | `management.endpoint.health.show-details=always` | ✅ | linha presente no `application.properties` |
| CA-04 | `curl /actuator/health` → `{"status":"UP"}` | ⏳ pendente | requer app rodando com Docker/bootRun |
| CA-05 | `curl /actuator/info` → JSON com `app.name` | ⏳ pendente | requer app rodando |
| CA-06 | `curl /actuator/metrics` → JSON com lista de métricas | ⏳ pendente | requer app rodando |
| CA-07 | `curl /actuator/prometheus` → formato Prometheus | ⏳ pendente | requer app rodando |
| CA-08 | Suíte Java → 73+ testes, 0 falhas | ✅ | BUILD SUCCESSFUL — 73 testes, 0 falhas |

---

## Verificações estruturais executadas

```bash
# V1 — dependências no build.gradle
$ grep "actuator\|micrometer-registry-prometheus" build.gradle
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'

# V2 — configuração no application.properties
$ grep "management\|info.app" src/main/resources/application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
info.app.name=realworld
info.app.description=RealWorld Platform Modernization
info.app.version=1.0.0

# V3 — suíte Java
$ JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew test --console=plain
BUILD SUCCESSFUL in 10s
5 actionable tasks: 2 executed, 3 up-to-date

# V4 — sem secrets hardcoded
$ grep -r "mySecretKey" src/main/resources/
(0 resultados)
```

---

## Observações

- `spring-boot-starter-actuator` adicionado explicitamente — não é transitive dependency em Spring Boot 2.6.3
- CAs 04–07 (`curl /actuator/*`) serão validados em conjunto com US-03.04 quando a stack Docker Compose subir
- ADR-003 respeitado: Micrometer apenas no Actuator/Prometheus endpoint, sem uso nos controllers
