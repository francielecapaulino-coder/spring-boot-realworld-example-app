package io.spring.application.data;

/**
 * Aggregate counter pairing an article id with its favorite count.
 *
 * <p>Pure carrier of data with no behaviour — eligible for {@code record} conversion
 * under US-06.02 / KR1.5 (mandate J5). Replaces the previous {@code @Value} class.
 */
public record ArticleFavoriteCount(String id, Integer count) {}
