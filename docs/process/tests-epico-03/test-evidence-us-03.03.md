# Evidência de testes — US-03.03

> **História:** US-03.03 — Substituir SQLite por PostgreSQL 16 e validar migrations Flyway
> **Branch:** `chore/us-03.03-postgresql-flyway`
> **PR:** a abrir
> **Issue:** #57
> **Data de execução:** 2026-06-18

---

## Ambiente de compilação

| Item | Valor |
|---|---|
| Java | OpenJDK 11.0.31 |
| Gradle | 7.4 (wrapper) |
| Spring Boot | 2.6.3 |
| Testcontainers | 1.17.6 |
| PostgreSQL (imagem) | 16 (via Testcontainers JDBC URL) |
| Docker para testes | ⏳ indisponível neste ambiente — testes de integração pendentes |

---

## Compilação Java + Testes

| Fase | Resultado |
|---|---|
| `compileJava` | ✅ BUILD SUCCESSFUL |
| `compileTestJava` | ✅ BUILD SUCCESSFUL (Testcontainers no classpath) |
| `./gradlew test` (integração DB) | ⏳ pendente — requer Docker em execução |

---

## Artefatos entregues

| Artefato | Verificação |
|---|---|
| `build.gradle` — `sqlite-jdbc` removido | ✅ `grep "sqlite" build.gradle` → sem resultado |
| `build.gradle` — `runtimeOnly 'org.postgresql:postgresql'` | ✅ presente |
| `build.gradle` — `testImplementation 'org.testcontainers:postgresql:1.17.6'` | ✅ presente |
| `build.gradle` — `testImplementation 'org.testcontainers:junit-jupiter:1.17.6'` | ✅ presente |
| `build.gradle` — task `clean` sem `delete './dev.db'` | ✅ bloco removido |
| `application.properties` — datasource PostgreSQL com env vars | ✅ `jdbc:postgresql://${POSTGRES_HOST:localhost}:...` |
| `application-test.properties` — Testcontainers JDBC URL | ✅ `jdbc:tc:postgresql:16:///realworld` |
| `db/migration/V1__create_tables.sql` | ✅ SQL padrão, compatível PostgreSQL |

---

## Critérios de aceite verificados

| CA | Descrição | Status | Saída |
|---|---|---|---|
| CA-01 | `sqlite-jdbc` removido do `build.gradle` | ✅ | `grep "sqlite" build.gradle` → sem resultado |
| CA-02 | Driver `org.postgresql:postgresql` presente (`runtimeOnly`) | ✅ | linha presente |
| CA-03 | `application.properties` datasource aponta para PostgreSQL com vars | ✅ | `jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:realworld}` |
| CA-04 | `application-test.properties` usa Testcontainers JDBC URL | ✅ | `jdbc:tc:postgresql:16:///realworld` |
| CA-05 | `V1__create_tables.sql` usa SQL padrão (sem dialeto SQLite) | ✅ | `grep "last_insert_rowid\|AUTOINCREMENT\|strftime\|PRAGMA"` → 0 resultados |
| CA-06 | Testcontainers `postgresql` e `junit-jupiter` 1.17.6 no classpath de teste | ✅ | `compileTestJava` BUILD SUCCESSFUL |
| CA-07 | `./gradlew test` → migrations Flyway aplicadas via Testcontainers | ⏳ pendente Docker |
| CA-08 | 73+ testes, 0 falhas após migração | ⏳ pendente Docker |
| CA-09 | Nenhum perfil usa SQLite ou H2 | ✅ | `grep -r "sqlite\|h2" src/main/resources/` → sem resultado |

---

## Verificações estruturais executadas

```bash
# V1 — SQLite removido
$ grep -rn "sqlite" build.gradle
(sem resultado)

# V2 — PostgreSQL presente
$ grep "postgresql" build.gradle
    runtimeOnly 'org.postgresql:postgresql'
    testImplementation 'org.testcontainers:postgresql:1.17.6'

# V3 — application.properties datasource
$ grep "datasource" src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:realworld}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${POSTGRES_USER:realworld}
spring.datasource.password=${POSTGRES_PASSWORD:realworld}

# V4 — application-test.properties
$ cat src/main/resources/application-test.properties
spring.datasource.url=jdbc:tc:postgresql:16:///realworld
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.flyway.enabled=true

# V5 — dialeto SQLite nos mappers
$ grep -rn "last_insert_rowid|AUTOINCREMENT|strftime|PRAGMA" src/main/resources/mapper/
(sem resultado — SQL compatível com PostgreSQL)

# V6 — compilação
$ JWT_SECRET=test-secret-for-ci-at-least-32-characters-long ./gradlew compileJava compileTestJava --console=plain
BUILD SUCCESSFUL in 3s
4 actionable tasks: 2 executed, 2 up-to-date
```

---

## Observações

- Estratégia de teste: Testcontainers JDBC URL (`jdbc:tc:postgresql:16:///realworld`) — zero código Java extra, container PostgreSQL 16 gerenciado automaticamente
- Flyway continua habilitado: `spring.flyway.enabled=true` no perfil de teste garante que `V1__create_tables.sql` é aplicada ao container efêmero
- CA-07 e CA-08 serão validados quando Docker Desktop estiver disponível no ambiente local ou em CI (ubuntu-latest tem Docker nativo)
- `tasks.named('clean')` atualizado: remoção do artefato `dev.db` (SQLite) desnecessária após migração
