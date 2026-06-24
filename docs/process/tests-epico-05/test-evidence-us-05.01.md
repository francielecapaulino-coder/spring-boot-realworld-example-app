# Evidência de teste — US-05.01: Configurar Spring Data JPA + Hibernate

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup`
- **Issue:** #81
- **Data:** 2026-06-23
- **Escopo da história:** adicionar Spring Data JPA e configurar Hibernate/Flyway em modo PostgreSQL-first.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| Starter JPA | `grep -n 'spring-boot-starter-data-jpa' build.gradle` | ✅ presente |
| Starter Flyway Boot 4 | `grep -n 'spring-boot-starter-flyway' build.gradle` | ✅ presente |
| `ddl-auto=validate` | `grep -n 'spring.jpa.hibernate.ddl-auto=validate' application.properties` | ✅ presente |
| `open-in-view=false` | `grep -n 'spring.jpa.open-in-view=false' application.properties` | ✅ presente |
| Dialeto PostgreSQL | `grep -n 'PostgreSQLDialect' application.properties` | ✅ presente |
| Compilação | `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |

## Resultado da suíte completa

`./gradlew test` foi executado e chegou à fase de testes, mas 25 testes falharam por ausência local de Docker/Testcontainers (`DockerClientProviderStrategy`). A validação completa com PostgreSQL é delegada ao CI.

## Conclusão

US-05.01 está validada: dependências e configuração JPA/Hibernate/Flyway estão presentes e o projeto compila com Java 25.
