package io.spring.api.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.Path;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.WebRequest;

/**
 * Pure unit tests for {@link CustomizeExceptionHandler} targeting US-07.05.
 *
 * <p>Kills the four mutants reported by the US-07.04 Pitest baseline:
 *
 * <ul>
 *   <li>{@code handleInvalidRequest} NullReturnVals (line 47): asserts the
 *       returned {@link ResponseEntity} is non-null.
 *   <li>{@code handleInvalidRequest} removed call to {@code
 *       HttpHeaders.setContentType} (line 45): asserts the response carries
 *       {@code Content-Type: application/json}.
 *   <li>{@code lambda$handleInvalidRequest$0} NullReturnVals: asserts the
 *       mapped {@link FieldErrorResource} list is non-null and populated.
 *   <li>{@code getParam} EmptyObjectReturnVals: covers both branches with a
 *       single-segment and a multi-segment dotted path so the substring join
 *       result must not be an empty string.
 * </ul>
 */
class CustomizeExceptionHandlerTest {

  // ---------------------------------------------------------------------------
  // handleInvalidRequest
  // ---------------------------------------------------------------------------

  @Test
  void handleInvalidRequest_should_return_response_with_json_content_type_and_field_errors() {
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new TargetBean(), "registerParam");
    bindingResult.rejectValue("email", "duplicated", "email already exists");

    InvalidRequestException exception = new InvalidRequestException(bindingResult);
    WebRequest webRequest = Mockito.mock(WebRequest.class);

    CustomizeExceptionHandler handler = new CustomizeExceptionHandler();
    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);

    // Kills NullReturnVals on handleInvalidRequest line 47.
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

    // Kills "removed call to HttpHeaders::setContentType" on line 45.
    HttpHeaders responseHeaders = response.getHeaders();
    assertThat(responseHeaders.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    // Kills NullReturnVals on the .map(...) lambda at lines 33-39 by asserting
    // the body holds an ErrorResource with the real field-error payload.
    assertThat(response.getBody()).isInstanceOf(ErrorResource.class);
    ErrorResource body = (ErrorResource) response.getBody();
    assertThat(body.fieldErrors()).hasSize(1);
    FieldErrorResource fieldError = body.fieldErrors().get(0);
    assertThat(fieldError.field()).isEqualTo("email");
    assertThat(fieldError.resource()).isEqualTo("registerParam");
    assertThat(fieldError.message()).isEqualTo("email already exists");
  }

  // ---------------------------------------------------------------------------
  // handleConstraintViolation — exercises both branches of getParam(...)
  // ---------------------------------------------------------------------------

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  void handleConstraintViolation_should_use_single_segment_path_as_is() {
    // Single-segment path keeps the original "field" string. Kills
    // EmptyObjectReturnVals on getParam by asserting the field name matches
    // the input exactly (a "" return would fail the equality check).
    ConstraintViolation<?> violation = buildViolation("field", NotNull.class, "must not be null");
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException(violations);

    CustomizeExceptionHandler handler = new CustomizeExceptionHandler();
    ErrorResource body = handler.handleConstraintViolation(exception, Mockito.mock(WebRequest.class));

    assertThat(body.fieldErrors()).hasSize(1);
    assertThat(body.fieldErrors().get(0).field()).isEqualTo("field");
    // `resource` is the root bean class name in this overload.
    assertThat(body.fieldErrors().get(0).resource()).isEqualTo(TargetBean.class.getName());
    // `code` is the annotation simpleName ("NotNull"). Assert it to kill
    // any mutation that nulls the annotation lookup.
    assertThat(body.fieldErrors().get(0).code()).isEqualTo("NotNull");
    assertThat(body.fieldErrors().get(0).message()).isEqualTo("must not be null");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  void handleConstraintViolation_should_drop_first_two_segments_of_dotted_path() {
    // Multi-segment path takes the else-branch: Arrays.copyOfRange(splits, 2,
    // splits.length) drops the first two segments and joins the rest by ".".
    // Kills EmptyObjectReturnVals on the else-branch return.
    ConstraintViolation<?> violation =
        buildViolation("createArticle.param.title", NotBlank.class, "must not be blank");
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException(violations);

    CustomizeExceptionHandler handler = new CustomizeExceptionHandler();
    ErrorResource body = handler.handleConstraintViolation(exception, Mockito.mock(WebRequest.class));

    assertThat(body.fieldErrors()).hasSize(1);
    assertThat(body.fieldErrors().get(0).field()).isEqualTo("title");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static ConstraintViolation<Object> buildViolation(
      String pathString, Class<? extends Annotation> annotationType, String message) {
    ConstraintViolation violation = Mockito.mock(ConstraintViolation.class);
    Path path = Mockito.mock(Path.class);
    given(path.toString()).willReturn(pathString);
    given(violation.getPropertyPath()).willReturn(path);
    given(violation.getMessage()).willReturn(message);
    given(violation.getRootBeanClass()).willReturn((Class) TargetBean.class);
    ConstraintDescriptor descriptor = Mockito.mock(ConstraintDescriptor.class);
    Annotation annotation = Mockito.mock(annotationType);
    given(annotation.annotationType()).willReturn((Class) annotationType);
    given(descriptor.getAnnotation()).willReturn(annotation);
    given(violation.getConstraintDescriptor()).willReturn(descriptor);
    return violation;
  }

  // Local sentinel annotations only used as marker types in the constraint
  // descriptor stubs above. They never have to be applied to real fields.
  private @interface NotNull {}

  private @interface NotBlank {}

  /**
   * Bean used as the target of {@link BeanPropertyBindingResult}. Needs a real
   * property with a getter so that {@code rejectValue("email", ...)} can read
   * the current value; otherwise Spring throws {@code NotReadablePropertyException}.
   */
  public static class TargetBean {
    private String email;
    private String title;

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
  }
}
