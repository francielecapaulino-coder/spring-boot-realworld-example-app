package io.spring.infrastructure;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * Base for repository slice tests. Uses {@link SpringBootTest} so the full Spring Boot context
 * (including Flyway auto-configuration) is loaded after the JPA migration (US-05.05), because
 * ddl-auto=validate needs the migrations to run before Hibernate validates the schema.
 *
 * <p>{@code AutoConfigureTestDatabase.replace=NONE} keeps the Testcontainers PostgreSQL URL
 * configured in application-test.properties, instead of replacing it with an embedded H2.
 *
 * <p>{@code /cleanup.sql} runs after each test method so state doesn't leak across tests.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class DbTestBase {}
