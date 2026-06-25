package io.spring.application.article;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Request payload for {@code POST /articles}.
 *
 * <p>Pure carrier of data — converted to {@code record} under US-06.03 / KR1.5
 * (mandate J5). {@code @Builder} was dropped because the canonical record
 * constructor already provides a fluent way to assemble the parameters from
 * GraphQL inputs. Bean Validation annotations and
 * {@code @DuplicatedArticleConstraint} live on the matching record components.
 */
@JsonRootName("article")
public record NewArticleParam(
    @NotBlank(message = "can't be empty") @DuplicatedArticleConstraint String title,
    @NotBlank(message = "can't be empty") String description,
    @NotBlank(message = "can't be empty") String body,
    List<String> tagList) {}
