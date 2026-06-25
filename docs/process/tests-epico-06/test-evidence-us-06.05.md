# Evidência de teste — US-06.05

## Contexto

A US-06.05 endereça os **value objects do domínio (`io.spring.core`)** — `Tag`, `FollowRelation`, `ArticleFavorite`, `User`, `Article`, `Comment`. A própria especificação da US prevê dois caminhos:

- **Caminho 2a** — se as classes do `core` **não** forem `@Entity` após EPIC-05, convertê-las para record.
- **Caminho 2b** — se forem `@Entity` após EPIC-05, **documentar a análise e encerrar sem conversão de código**, com justificativa técnica explícita.

Após a conclusão do EPIC-05 (migração MyBatis → JPA), **todas** as 6 classes do `core` foram anotadas com `@Entity`. Aplicamos portanto o **caminho 2b**.

## Procedimento de verificação

1. **Confirmar que todas as 6 classes têm `@Entity`**:
   ```bash
   grep -l "@Entity" \
     src/main/java/io/spring/core/user/User.java \
     src/main/java/io/spring/core/article/Article.java \
     src/main/java/io/spring/core/comment/Comment.java \
     src/main/java/io/spring/core/article/Tag.java \
     src/main/java/io/spring/core/favorite/ArticleFavorite.java \
     src/main/java/io/spring/core/user/FollowRelation.java
   ```
2. **Confirmar que continuam tendo métodos mutáveis** (onde aplicável):
   ```bash
   grep -n "public void update\|public void delete" \
     src/main/java/io/spring/core/user/User.java \
     src/main/java/io/spring/core/article/Article.java
   ```
3. **Confirmar que nenhum record do projeto está anotado com `@Entity`** (proibido pelo mandato J5 e pela spec JPA):
   ```bash
   grep -rn "@Entity" src/main/java/io/spring/ --include="*.java" | xargs -I {} echo {} | xargs -I {} sh -c 'file=$(echo {} | cut -d: -f1); grep -l "^public record" "$file" 2>/dev/null'
   ```
   *Resultado esperado:* lista vazia.
4. **Suite completa verde** (a US é docs-only — apenas confirma que nada quebrou):
   ```bash
   ./gradlew clean test --no-daemon
   ```

## Inspeção das 6 classes do `core`

| Classe | `@Entity`? | Mutação interna | Veredito |
|---|---|---|---|
| `core.user.User` | ✅ | `update(email, username, password, bio, image)` muta 5 campos | **Inelegível** — entidade JPA mutável |
| `core.article.Article` | ✅ | `update(title, description, body)` + `delete()` mutam estado | **Inelegível** |
| `core.comment.Comment` | ✅ | Imutável de facto, mas `@Entity` exige ctor no-arg mutável | **Inelegível** |
| `core.article.Tag` | ✅ | Lombok `@Data` em entidade gerenciada | **Inelegível** |
| `core.favorite.ArticleFavorite` | ✅ | Imutável após construção, mas `@Entity` com `@EmbeddedId` | **Inelegível** |
| `core.user.FollowRelation` | ✅ | Mesma estrutura de `ArticleFavorite` | **Inelegível** |

## Justificativa técnica

- A JPA spec (Jakarta Persistence 3.2) exige construtor no-arg e campos não-final em `@Entity`. Records têm componentes implicitamente `final` e não admitem construtor no-arg.
- Hibernate 6.6 (Spring Boot 4) tem suporte experimental a records como `@Embeddable`, **mas não como `@Entity`**.
- O documento US-06.05 reforça (linha 105): **"Nunca adicione `@Entity` a um record"**.

## Critérios de aceite

| ID | Critério | Status | Como foi verificado |
|---|---|---|---|
| CA-01 | Análise pós-EPIC-05 documentada | ✅ | Tabela acima |
| CA-02 | Justificativa técnica registrada | ✅ | Seção "Justificativa técnica" |
| CA-03 | Nenhuma classe do `core` anotada com `@Entity` foi convertida | ✅ | Caminho 2b aplicado |
| CA-04 | Nenhum record do projeto tem `@Entity` | ✅ | Inspeção retornou lista vazia |
| CA-05 | `./gradlew test` 100 % verde após a análise | ✅ | 115/115 PASSED |

## Resultado da execução

A US-06.05 não introduziu mudanças de código de produção (caminho 2b). A suite passou em todas as US subsequentes e na verificação final (US-06.06):

```text
> Task :test

BUILD SUCCESSFUL in 34s
7 actionable tasks: 7 executed

Total tests:    115
Total failures: 0
Total errors:   0
```

## Conclusão

US-06.05 encerrada **sem mudança de código** (caminho 2b), em conformidade com o próprio escopo da história. O resultado **não reduz o denominador** do threshold da KR1.5 — o universo de DTOs elegíveis é composto pelos pacotes `application.*` e `api.exception`, não pelo `core` (cuja totalidade é entidades JPA).

---
*Esta evidência foi criada de acordo com a US-06.05 e segue o modelo de evidência usado nas histórias anteriores.*
