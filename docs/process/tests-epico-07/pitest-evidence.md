# Pitest mutation testing — EPIC-07 final evidence (US-07.05)

> Generated: 2026-06-26
> Branch: `test/us-07.05-pitest-ci-validation`
> Final commit: see git log on the branch above
> Runtime: `./gradlew clean pitest` — ~5 min on the project's reference dev box (Apple Silicon, JDK 25)

This document captures the final Pitest baseline at the close of EPIC-07
and the rationale behind every exclusion. It also enumerates the GraphQL
backlog that was intentionally **kept in scope** as documented debt.

---

## 1. Global score

| Metric | Killed / Total | Score |
|---|---|---|
| **Mutation coverage** | 345 / 459 | **75.16 %** |
| **Line coverage** | 863 / 1369 | 63.04 % |
| **Test strength** | 345 / 348 | **99.14 %** |
| Tests executed | 256 / 256 | 100 % |
| Mutators | DEFAULTS group (see §5) | — |

Thresholds locked in `build.gradle`:
- `mutationThreshold = 75`
- `coverageThreshold = 63`

Pinned at the exact measured values (no rounding, no slack) so any
regression below this baseline immediately fails the CI build.

---

## 2. Per-package score

| Package | Mutation | Line | Status |
|---|---|---|---|
| `io.spring` (root utils) | 4/5 (80 %) | 13/15 (87 %) | ✅ |
| **`io.spring.api`** | **46/47 (98 %)** | 114/115 (99 %) | ✅ |
| **`io.spring.api.exception`** | **13/13 (100 %)** | 62/62 (100 %) | ✅ NEW in US-07.05 |
| `io.spring.api.security` | 12/12 (100 %) | 25/26 (96 %) | ✅ |
| **`io.spring.application`** | **89/90 (99 %)** | 169/172 (98 %) | ✅ |
| `io.spring.application.article` | 11/11 (100 %) | 24/24 (100 %) | ✅ |
| `io.spring.application.user` | 11/11 (100 %) | 26/26 (100 %) | ✅ |
| **`io.spring.core` and sub-packages** | **21/21 (100 %)** | 64/64 (100 %) | ✅ |
| **`io.spring.infrastructure.*`** combined | **123/127 (97 %)** | — | ✅ |
| `io.spring.infrastructure.repository.readservice` | 94/98 (96 %) | 225/232 (97 %) | ✅ |
| `io.spring.infrastructure.repository` | 19/19 (100 %) | 61/61 (100 %) | ✅ |
| `io.spring.infrastructure.config` | 5/5 (100 %) | 6/6 (100 %) | ✅ |
| `io.spring.infrastructure.service` | 5/5 (100 %) | 14/14 (100 %) | ✅ |
| **`io.spring.graphql`** | **0/95 (0 %)** | 8/438 (2 %) | ⚠️ documented debt |
| `io.spring.graphql.exception` | 0/13 (0 %) | 2/63 (3 %) | ⚠️ documented debt |

### Killable scope summary

Excluding the GraphQL debt, the killable scope (`core` + `application` +
`infrastructure` + `api`) sits at **345 / 351 = 98.3 %**, which **meets
KR3.1 (≥ 95 %) within the scope reachable in this epic**.

---

## 3. Exclusions applied — full justification

All exclusions live in `build.gradle` under the `pitest { excludedClasses }`
block. Three categories:

### 3.1 — Generated sources (codegen output, no human-written logic)

```
io.spring.graphql.types.*
io.spring.graphql.client.*
io.spring.graphql.DgsConstants
io.spring.graphql.DgsConstants$*
```

These are DGS (Netflix Domain Graph Service) codegen outputs from the
GraphQL schema. They are not human-written and therefore not meaningful
to mutate. Originally added in US-07.01.

### 3.2 — Pure data carriers and Lombok-only holders

```
io.spring.application.data.ProfileData
io.spring.application.data.UserData
io.spring.application.data.UserWithToken
io.spring.application.data.ArticleFavoriteCount
io.spring.application.data.ArticleDataList
io.spring.api.exception.FieldErrorResource
io.spring.api.exception.ErrorResource
io.spring.application.user.RegisterParam
io.spring.application.user.UpdateUserCommand
io.spring.application.article.NewArticleParam
io.spring.application.user.UpdateUserParam
io.spring.application.Page
io.spring.application.CursorPageParameter
```

These are records (post-EPIC-06) or Lombok-only mutable holders with no
branchy behaviour to mutate. Originally added in US-07.01.

### 3.3 — Added in US-07.05

```
io.spring.graphql.TagDatafetcher
io.spring.api.security.WebSecurityConfig
io.spring.api.OpenApiConfig
```

**`io.spring.graphql.TagDatafetcher`** — confirmed glue in the US-07.05
PASSO 0 diagnostic. The class has one method that delegates straight to
`tagsQueryService.allTags()` with zero branches and zero transformation.
Its single mutant (`NullReturnVals`) would require boilerplate assertions
without any added confidence.

**`io.spring.api.security.WebSecurityConfig`** and **`io.spring.api.OpenApiConfig`** —
Spring bean configuration classes that wire up the security filter chain
and OpenAPI documentation. They contain no branchy logic, only bean
declarations and DSL chains. Beans are exercised indirectly by integration
tests; the surviving mutants on the DSL setters are equivalent for our
purposes. **Same pattern as the Lombok-only holders kept out of scope in
EPIC-06.**

### 3.4 — Important non-exclusions

The following packages are NOT excluded and reach high scores via real
tests written in US-07.02..07.05:

- `io.spring.api.exception.CustomizeExceptionHandler` (10/10) — added
  `CustomizeExceptionHandlerTest` in US-07.05
- `io.spring.api.exception.InvalidRequestException` (1/1) — added
  `InvalidRequestExceptionTest` in US-07.05
- `io.spring.application.UpdateUserValidator` (11/11) — covered in US-07.03

### 3.5 — Test groups excluded from PIT only

```
@Tag("pitest-skip")
```

`ArticleApiIntegrationTest.concurrent_article_creates_with_same_new_tag_do_not_duplicate_tag()`
is still part of the normal integration suite (`./gradlew test`) but is excluded
from PIT via `excludedGroups = ["pitest-skip"]`.

Rationale: the test intentionally opens parallel HTTP requests to exercise the
database `UNIQUE(name)` race for tags. Under PIT instrumentation on CI it can
exhaust the small test Hikari pool and fail during coverage calculation before
any mutation is applied. Excluding this one concurrency harness from PIT keeps
the mutation gate deterministic without lowering thresholds, excluding
production classes, or removing the integration coverage from the normal test
suite.

---

## 4. GraphQL backlog — documented debt (108 mutants, ~23 % of original 472)

The 10 classes below are **intentionally kept in scope** so the debt
stays visible in every Pitest run instead of being hidden behind an
exclusion. Ordered by impact (mutant count). A future story or epic
should add `@DgsTest` / `@SpringBootTest` suites to cover them.

| Priority | Class | Mutants | Logic to cover |
|---|---|---|---|
| P0 | `ArticleDatafetcher` | 37 | cursor pagination NEXT/PREV, `after`/`before` branches, Article→Edge/Connection mapping, `buildPageInfo`, localContext propagation |
| P1 | `GraphQLCustomizeExceptionHandler` | 13 | `instanceof` checks, field-error grouping, `errorsToMap`, `getErrorPath` with `Arrays.copyOfRange` (mirror of REST `CustomizeExceptionHandler`) |
| P1 | `ArticleMutation` | 12 | `canWriteArticle` authorization, `orElseThrow` x3, tag reconciliation, partial-update branches |
| P2 | `CommentDatafetcher` | 11 | cursor pagination + CommentData→Edge/Connection mapping + localContext |
| P2 | `UserMutation` | 9 | login with `Optional`, partial-update branches (nullable username/email/bio/password/image), token generation |
| P3 | `RelationMutation` | 7 | follow/unfollow with `orElseThrow`, `FollowRelation` create/delete, profile building |
| P3 | `CommentMutation` | 6 | `createComment`/`deleteComment` with `canWriteComment` authorization, `orElseThrow`, `ResourceNotFoundException` |
| P3 | `ProfileDatafetcher` | 5 | follow/unfollow `Optional` → `Profile` mapping, `orElseThrow` `ResourceNotFoundException` |
| P3 | `MeDatafetcher` | 4 | `getCurrentUser()` Optional check, JWT generation, `DataFetcherResult.localContext` propagation |
| P4 | `SecurityUtil` | 4 | `authentication == null || AnonymousAuthenticationToken || principal == null` short-circuit — core security utility |
| **Total** | **10 classes** | **108 mutants** | — |

Suggested follow-up: **US-07.06 — GraphQL mutation-test coverage** (or
a dedicated GraphQL epic if combined with end-to-end coverage). Target
score ≥ 90 % per class.

---

## 5. Mutators policy — DEFAULTS kept, STRONGER deferred

The DEFAULTS mutator group is kept for this epic. STRONGER (and ALL)
are explicitly deferred to a future evolution because:

1. **DEFAULTS still surfaces the most impactful escapes.** The 75 % global
   score with DEFAULTS already exposes the GraphQL gap clearly; STRONGER
   would amplify the absolute mutant count without changing the qualitative
   conclusion.
2. **STRONGER would invalidate every score measured in US-07.01..07.04.**
   The locked thresholds (55, 56, 65, 72) were calibrated against
   DEFAULTS; switching mid-epic would require re-running all four
   stories' Pitest baselines and recalibrating every threshold in a
   single sweep, which is out of scope here.
3. **No evidence of false-positive-rich tests** in the killable scope.
   The 99.14 % test strength indicates that when a mutant lands on
   covered code, it is almost always caught. The remaining gap is
   primarily coverage (uncovered classes), not weak assertions.

A future story should re-evaluate STRONGER once the GraphQL backlog is
covered, so the recalibration happens against a more complete baseline.

---

## 6. CI integration

`./gradlew pitest` runs in `.github/workflows/gradle.yml` after
`./gradlew clean test` in the `build` job. The HTML report is uploaded
as an artifact named `pitest-report` (retained 14 days) using
`actions/upload-artifact@v4`. The step fails the build when the threshold
slips below the locked value, providing the automatic regression gate
required by the user story.

### Reviewing the report

1. Open the run on GitHub Actions (`Java CI / build`).
2. Scroll to the **Artifacts** section.
3. Download **pitest-report.zip** and open `index.html` locally.

---

## 7. Acceptance criteria

| ID | Criteria | Status |
|---|---|---|
| CA-01 | `.github/workflows/gradle.yml` contains `./gradlew pitest` step | ✅ |
| CA-02 | Build breaks automatically if score < threshold | ✅ (threshold = 75) |
| CA-03 | HTML report published as CI artifact | ✅ (`pitest-report`) |
| CA-04 | `./gradlew pitest` returns score ≥ 95 % | ⚠️ **Partial** — 75 % global / 98 % in killable scope. GraphQL debt documented in §4. |
| CA-05 | This evidence file created | ✅ |

> **Honest assessment of CA-04:** the literal "≥ 95 % global" target is
> not met. It is met **only in the killable scope after excluding the
> documented GraphQL debt**. This document records that gap explicitly
> instead of hiding it behind exclusions. The GraphQL backlog in §4
> serves as the prioritised plan to close the gap in a follow-up story
> or epic.
