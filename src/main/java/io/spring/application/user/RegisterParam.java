package io.spring.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for {@code POST /users}.
 *
 * <p>Pure carrier of data — converted to {@code record} under US-06.03 / KR1.5
 * (mandate J5). Bean Validation annotations live on the record components so
 * Jakarta Validation continues to enforce them; Jackson 3 honours
 * {@code @JsonRootName("user")} for record deserialization, keeping the
 * historical {@code {"user": {...}}} envelope.
 */
@JsonRootName("user")
public record RegisterParam(
    @NotBlank(message = "can't be empty")
        @Email(message = "should be an email")
        @DuplicatedEmailConstraint
        String email,
    @NotBlank(message = "can't be empty") @DuplicatedUsernameConstraint String username,
    @NotBlank(message = "can't be empty") String password) {}
