# Evidência de teste — US-04.05: Spring Security 6 `SecurityFilterChain`

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup` (validação consolidada EPIC-04/EPIC-05)
- **Data:** 2026-06-23
- **Escopo da história:** remover `WebSecurityConfigurerAdapter`, configurar `SecurityFilterChain` e preservar regras de autorização, incluindo Actuator público.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| Adapter legado removido | `grep -RIn 'WebSecurityConfigurerAdapter' src/main/java/io/spring/api/security` | ✅ 0 ocorrências |
| `SecurityFilterChain` presente | `grep -n 'SecurityFilterChain' WebSecurityConfig.java` | ✅ import + método `filterChain(HttpSecurity http)` |
| JWT filter preservado | `grep -n 'JwtTokenFilter' WebSecurityConfig.java` | ✅ bean, registro desabilitado e inclusão na chain |
| Actuator público preservado | `grep -n 'actuator' WebSecurityConfig.java` | ✅ `/actuator/health`, `/info`, `/prometheus`, `/metrics` |
| Regressão Actuator | `./gradlew test --tests io.spring.api.security.ActuatorSecurityTest` | ✅ `BUILD SUCCESSFUL in 7s` |
| Compilação | `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |

## Resultado da suíte completa

`./gradlew test --no-daemon --console=plain` foi executado e falhou apenas nos testes que sobem contexto com PostgreSQL/Testcontainers porque Docker não está instalado localmente (`docker: command not found`). O teste específico de segurança (`ActuatorSecurityTest`) passou.

## Conclusão

US-04.05 está validada localmente: `WebSecurityConfigurerAdapter` foi removido, `SecurityFilterChain` está ativo, regras de Actuator foram preservadas e regressão de segurança passou.
