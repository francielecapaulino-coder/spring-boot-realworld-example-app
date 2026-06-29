package io.spring.graphql.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.netflix.graphql.types.errors.ErrorType;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GraphQLCustomizeExceptionHandlerTest {

  @Test
  void handleException_should_return_unauthenticated_graphql_error() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    ResultPath path = ResultPath.rootPath().segment("viewer");
    DataFetcherExceptionHandlerParameters parameters = parameters(exception, path);

    GraphQLCustomizeExceptionHandler handler = new GraphQLCustomizeExceptionHandler();
    DataFetcherExceptionHandlerResult result = handler.handleException(parameters).join();

    assertThat(result.getErrors()).hasSize(1);
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getMessage()).isEqualTo("invalid email or password");
    assertThat(error.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
    assertThat(error.getPath()).containsExactly("viewer");
  }

  @SuppressWarnings("unchecked")
  @Test
  void handleException_should_return_bad_request_with_grouped_constraint_errors() {
    ConstraintViolation<?> emailViolation =
        buildViolation("register.user.email", NotNull.class, "must not be null");
    ConstraintViolation<?> titleViolation =
        buildViolation("title", NotBlank.class, "must not be blank");
    ConstraintViolationException exception =
        new ConstraintViolationException(Set.of(emailViolation, titleViolation));
    ResultPath path = ResultPath.rootPath().segment("createArticle");
    DataFetcherExceptionHandlerParameters parameters = parameters(exception, path);

    GraphQLCustomizeExceptionHandler handler = new GraphQLCustomizeExceptionHandler();
    DataFetcherExceptionHandlerResult result = handler.handleException(parameters).join();

    assertThat(result.getErrors()).hasSize(1);
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getMessage()).contains("must not be null", "must not be blank");
    assertThat(error.getPath()).containsExactly("createArticle");
    assertThat(error.getExtensions()).containsKeys("email", "title");
    assertThat((List<String>) error.getExtensions().get("email"))
        .containsExactly("must not be null");
    assertThat((List<String>) error.getExtensions().get("title"))
        .containsExactly("must not be blank");
  }

  @Test
  void getErrorsAsData_should_group_constraint_errors_by_field() {
    ConstraintViolation<?> emailViolation =
        buildViolation("register.user.email", NotNull.class, "must not be null");
    ConstraintViolation<?> titleViolation =
        buildViolation("title", NotBlank.class, "must not be blank");
    ConstraintViolationException exception =
        new ConstraintViolationException(Set.of(emailViolation, titleViolation));

    Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(error.getMessage()).isEqualTo("BAD_REQUEST");
    Map<String, List<String>> errorsByKey =
        error.getErrors().stream()
            .collect(
                Collectors.toMap(
                    ErrorItem::getKey,
                    ErrorItem::getValue));
    assertThat(errorsByKey).containsEntry("email", List.of("must not be null"));
    assertThat(errorsByKey).containsEntry("title", List.of("must not be blank"));
  }

  private static DataFetcherExceptionHandlerParameters parameters(
      Throwable exception, ResultPath path) {
    DataFetcherExceptionHandlerParameters parameters =
        Mockito.mock(DataFetcherExceptionHandlerParameters.class);
    given(parameters.getException()).willReturn(exception);
    given(parameters.getPath()).willReturn(path);
    return parameters;
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

  private @interface NotNull {}

  private @interface NotBlank {}

  private static class TargetBean {}
}
