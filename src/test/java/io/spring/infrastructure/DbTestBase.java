package io.spring.infrastructure;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base for repository slice tests. Uses {@link DataJpaTest} after the MyBatis to JPA migration
 * (US-05.05). Disables embedded DB replacement so Testcontainers PostgreSQL is honored.
 */
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public abstract class DbTestBase {}
