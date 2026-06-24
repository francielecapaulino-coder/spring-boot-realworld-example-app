# Evidência de teste — US-05.05: Remover artefatos MyBatis

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup`
- **Issue:** #81
- **Data:** 2026-06-23
- **Escopo da história:** remover mappers, XMLs, dependências e referências MyBatis do código fonte.

## Checks executados

| Check | Comando | Resultado |
|---|---|---|
| Referências MyBatis/ibatis em `src` | `grep -RInE 'org\.apache\.ibatis|MyBatisConfig|infrastructure\.mybatis|@MybatisTest|MyBatis|mybatis' src/main src/test` | ✅ 0 ocorrências |
| Arquivos mapper/MyBatis | `find src \( -name '*Mapper.java' -o -name '*Mapper.xml' -o -name 'MyBatis*.java' \)` | ✅ 0 arquivos |
| Dependência/config MyBatis | `grep -RInE 'mybatis|org\.apache\.ibatis|MyBatis' build.gradle application.properties src/main src/test` | ✅ 0 ocorrências |
| Compilação de testes | `./gradlew compileTestJava --no-daemon --console=plain` | ✅ `BUILD SUCCESSFUL in 5s` |
| Renomeação dos testes | inspeção de `src/test/java/io/spring/infrastructure/**/Jpa*RepositoryTest.java` | ✅ `JpaArticleRepositoryTest`, `JpaCommentRepositoryTest`, `JpaArticleFavoriteRepositoryTest`, `JpaUserRepositoryTest` |

## Correção aplicada nesta revisão

Os testes de repositório já exercitavam as implementações JPA, mas ainda mantinham nomes `MyBatis*RepositoryTest`. Para cumprir o critério "zero artefatos MyBatis sob `src/`", os arquivos/classes foram renomeados para `Jpa*RepositoryTest` e recompilados com sucesso.

## Resultado da suíte completa

`./gradlew test --no-daemon --console=plain` falha localmente apenas por ausência de Docker/Testcontainers (`77 tests completed, 25 failed`). As classes `Jpa*RepositoryTest` aparecem entre os testes bloqueados por PostgreSQL via Testcontainers, não por erro de compilação.

## Conclusão

US-05.05 está validada: não há artefatos MyBatis restantes em `src`, build/test compile passa e a validação runtime com banco fica para CI.
