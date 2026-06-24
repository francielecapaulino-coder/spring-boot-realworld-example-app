# Evidência de teste — US-05.02: Anotar entidades com JPA

## Visão geral

- **Branch:** `refactor/us-05.01-jpa-setup`
- **Issue:** #81
- **Data:** 2026-06-23
- **Escopo da história:** tornar entidades de domínio gerenciáveis pelo Hibernate sem alterar o contrato de domínio.

## Checks executados

| Entidade | Evidência estrutural |
|---|---|
| `Article` | `@Entity`, `@Table(name = "articles")`, `@ManyToMany`, `@JoinTable` |
| `Tag` | `@Entity`, `@Table(name = "tags")` |
| `Comment` | `@Entity`, `@Table(name = "comments")` |
| `ArticleFavorite` | `@Entity`, `@Table(name = "article_favorites")`, `@EmbeddedId`, `@Embeddable` |
| `FollowRelation` | `@Entity`, `@Table(name = "follows")`, `@EmbeddedId`, `@Embeddable` |
| `User` | `@Entity`, `@Table(name = "users")` |

## Verificação técnica

| Check | Resultado |
|---|---|
| `./gradlew compileJava compileTestJava` | ✅ `BUILD SUCCESSFUL in 4s` |
| Imports Java EE/Jakarta legados | ✅ 0 ocorrências de `javax.persistence`, `javax.validation`, `javax.servlet`, `javax.annotation` |
| Testes de repositório JPA | 🟡 executados na suíte completa, bloqueados localmente por Docker/Testcontainers |

## Conclusão

US-05.02 está validada em estrutura e compilação: entidades estão anotadas com Jakarta Persistence e o build Java 25 compila. A validação de schema com PostgreSQL/Testcontainers fica para CI.
