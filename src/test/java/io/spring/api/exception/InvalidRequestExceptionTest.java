package io.spring.api.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

/**
 * Unit tests for {@link InvalidRequestException} targeting US-07.05.
 *
 * <p>Kills the {@code NullReturnVals} mutant on {@code getErrors()} reported
 * by the US-07.04 Pitest baseline: if the accessor returned {@code null}
 * instead of the {@link Errors} held in the field, the assertion below
 * would fail.
 */
class InvalidRequestExceptionTest {

  @Test
  void getErrors_should_return_the_errors_passed_to_constructor() {
    Errors errors = new BeanPropertyBindingResult(new Object(), "target");

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception.getErrors()).isSameAs(errors);
  }
}
