package io.spring.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * Aggregate of field-level validation errors. Serialised by
 * {@link ErrorResourceSerializer} into the historical envelope
 * {@code {"errors": {"<field>": ["<msg>", ...], ...}}}.
 *
 * <p>Pure carrier of data — eligible for {@code record} conversion under
 * US-06.04 / KR1.5 (mandate J5). The serializer reads {@code fieldErrors()}
 * via the canonical record accessor.
 */
@JsonSerialize(using = ErrorResourceSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("errors")
public record ErrorResource(List<FieldErrorResource> fieldErrors) {}
