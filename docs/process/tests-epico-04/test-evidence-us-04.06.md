# Evidência de teste — US-04.06: Atualizar DGS Framework compatível com Spring Boot 4

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup` (validação consolidada EPIC-04/EPIC-05)
- **Data:** 2026-06-23
- **Escopo da história:** manter DGS conforme ADR-001 e validar GraphQL/codegen sem reescrever resolvers.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| DGS starter atualizado | `grep -n 'dgs-starter:12.0.1' build.gradle` | ✅ `com.netflix.graphql.dgs:dgs-starter:12.0.1` |
| DGS codegen configurado | `grep -n 'com.netflix.dgs.codegen' build.gradle` | ✅ plugin presente |
| Codegen | `./gradlew generateJava --no-daemon --console=plain` | ✅ `BUILD SUCCESSFUL in 3s` |
| Compilação | `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |
| Resolvers não reescritos para Spring GraphQL nativo | inspeção de imports/anotações DGS | ✅ `@DgsComponent`, `@DgsQuery`, `@DgsData` preservados |

## Observação sobre endpoint GraphQL

A validação `POST /graphql { tags }` requer aplicação em execução com PostgreSQL. Como Docker não está instalado localmente, a validação runtime completa fica delegada ao CI/ambiente com Docker Compose.

## Conclusão

US-04.06 está validada localmente no que não depende de runtime Docker: DGS 12.0.1 está no build, codegen executa com sucesso e a aplicação compila sem reescrita de resolvers, preservando ADR-001/ADR-004.
