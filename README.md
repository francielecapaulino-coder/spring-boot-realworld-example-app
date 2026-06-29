# ![RealWorld Example App using Kotlin and Spring](example-logo.png)

[![Actions](https://github.com/gothinkster/spring-boot-realworld-example-app/workflows/Java%20CI/badge.svg)](https://github.com/gothinkster/spring-boot-realworld-example-app/actions)

> ### Spring Boot + Spring Data JPA/Hibernate codebase containing real world examples (CRUD, auth, advanced patterns, etc.) that adheres to the [RealWorld](https://github.com/gothinkster/realworld-example-apps) spec and API.

This codebase demonstrates a fully fledged backend application built with Java 25, Spring Boot 4, Spring Data JPA/Hibernate, PostgreSQL, REST, and GraphQL, including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# *NEW* GraphQL Support  

Following some DDD principles. REST or GraphQL is just a kind of adapter. And the domain layer will be consistent all the time. So this repository implement GraphQL and REST at the same time.

The GraphQL schema is https://github.com/gothinkster/spring-boot-realworld-example-app/blob/master/src/main/resources/schema/schema.graphqls and the visualization looks like below.

![](graphql-schema.png)

And this implementation is using [dgs-framework](https://github.com/Netflix/dgs-framework) which is a quite new java graphql server framework.
# How it works

The application uses Spring Boot 4 (Web, Security, Data JPA, Actuator) and Netflix DGS for GraphQL.

* Use the idea of Domain Driven Design to separate the business term and infrastructure term.
* Use Spring Data JPA/Hibernate to implement persistence on PostgreSQL.
* Use [CQRS](https://martinfowler.com/bliki/CQRS.html) pattern to separate the read model and write model.

And the code is organized as this:

1. `api` is the web layer implemented by Spring MVC
2. `core` is the business model including entities and services
3. `application` is the high-level services for querying the data transfer objects
4. `infrastructure`  contains all the implementation classes as the technique details

# Security

Integration with Spring Security and add other filter for jwt token process.

The JWT secret is supplied via the `JWT_SECRET` environment variable.

# Database

It uses PostgreSQL with Flyway-managed schema migrations. The Docker Compose stack starts PostgreSQL locally for development and validation.

# Getting started

You'll need Java 25 installed.

    ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

# Try it out with [Docker](https://www.docker.com/)

You'll need Docker installed. Create a `.env` file with `JWT_SECRET` and `POSTGRES_PASSWORD`, then run:

    docker compose up -d

The application is available at http://localhost:8080 and the local observability stack includes Prometheus, Loki, Tempo, and Grafana.

# Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test

# API documentation (OpenAPI / Swagger)

The REST API contract is documented with OpenAPI 3 via [springdoc-openapi](https://springdoc.org/).
Start the application (`./gradlew bootRun`) and open:

- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **Swagger UI:**   http://localhost:8080/swagger-ui.html

Both endpoints are publicly reachable (no authentication required) so that the contract
is discoverable. Protected REST endpoints expect the header `Authorization: Token <jwt>`.

# Code format

Use spotless for code format.

    ./gradlew spotlessJavaApply

# Help

Please fork and PR to improve the project.

## Documentação do projeto de modernização

| Documento | Link |
|---|---|
| 📋 Vibe Coding Log (Coda) | [RealWorld Platform Modernization — Vibe Coding Log](TODO-CODA-URL) |
| 📐 Definition of Ready | [docs/process/definition-of-ready.md](docs/process/definition-of-ready.md) |
| ✅ Definition of Done | [docs/process/definition-of-done.md](docs/process/definition-of-done.md) |
| 🔀 Harness Development | [docs/process/harness-development.md](docs/process/harness-development.md) |
| 🤖 Guia Coda | [docs/process/coda-guide.md](docs/process/coda-guide.md) |
| 🔍 Guia GitAhead | [docs/process/gitahead-guide.md](docs/process/gitahead-guide.md) |

> Para contribuir: leia a [Definition of Ready](docs/process/definition-of-ready.md) antes de iniciar qualquer história.
