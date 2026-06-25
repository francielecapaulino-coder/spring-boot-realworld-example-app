package io.spring.application.data;

/**
 * Read-only user view exposed by query services and Jackson-serialised on the wire.
 *
 * <p>Pure carrier of data with no behaviour and no required mutable state — eligible
 * for {@code record} conversion under US-06.02 / KR1.5 (mandate J5).
 */
public record UserData(String id, String email, String username, String bio, String image) {}
