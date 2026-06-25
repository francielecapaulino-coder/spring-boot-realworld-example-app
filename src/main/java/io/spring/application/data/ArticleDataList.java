package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated wrapper of articles plus the total count, serialised as
 * {@code {"articles": [...], "articlesCount": N}} on the wire.
 *
 * <p>Pure carrier of data — eligible for {@code record} conversion under
 * US-06.02 / KR1.5 (mandate J5). The {@code @JsonProperty} annotations are
 * placed on the record components so Jackson keeps emitting the exact same
 * field names that the existing REST clients consume.
 */
public record ArticleDataList(
    @JsonProperty("articles") List<ArticleData> articleDatas,
    @JsonProperty("articlesCount") int count) {}
