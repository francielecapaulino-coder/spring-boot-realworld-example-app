package io.spring.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Single field-level error entry emitted by validation handlers.
 *
 * <p>Pure carrier of data — eligible for {@code record} conversion under US-06.04 /
 * KR1.5 (mandate J5). No mutable state is required by any caller.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FieldErrorResource(String resource, String field, String code, String message) {}
