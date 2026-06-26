package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests for the package-private {@link UpdateUserValidator} targeting US-07.03.
 * Kills the SURVIVED / NO_COVERAGE mutants on the {@code isValid} branches and on
 * the inner lambdas (lines 82 and 86) reported by the US-07.02 baseline.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateUserValidatorTest {

  @Mock private UserRepository userRepository;
  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintViolationBuilder violationBuilder;
  @Mock private NodeBuilderCustomizableContext nodeBuilder;

  @InjectMocks private UpdateUserValidator validator;

  private User targetUser;
  private UpdateUserParam param;
  private UpdateUserCommand command;

  @BeforeEach
  void setUp() {
    targetUser = new User("target@x.com", "target", "secret", "bio", "img");
    param = UpdateUserParam.builder().email("new@x.com").username("newname").build();
    command = new UpdateUserCommand(targetUser, param);

    given(context.buildConstraintViolationWithTemplate(any())).willReturn(violationBuilder);
    given(violationBuilder.addPropertyNode(any())).willReturn(nodeBuilder);
    given(nodeBuilder.addConstraintViolation()).willReturn(context);
  }

  @Test
  void should_return_true_when_email_and_username_are_free() {
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.empty());
    given(userRepository.findByUsername("newname")).willReturn(Optional.empty());

    assertThat(validator.isValid(command, context)).isTrue();
    verify(context, never()).disableDefaultConstraintViolation();
  }

  @Test
  void should_return_true_when_existing_email_belongs_to_target_user() {
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.of(targetUser));
    given(userRepository.findByUsername("newname")).willReturn(Optional.empty());

    assertThat(validator.isValid(command, context)).isTrue();
  }

  @Test
  void should_return_true_when_existing_username_belongs_to_target_user() {
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.empty());
    given(userRepository.findByUsername("newname")).willReturn(Optional.of(targetUser));

    assertThat(validator.isValid(command, context)).isTrue();
  }

  @Test
  void should_return_false_when_email_belongs_to_another_user() {
    User otherUser = new User("other@x.com", "other", "secret", "", "");
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.of(otherUser));
    given(userRepository.findByUsername("newname")).willReturn(Optional.empty());

    assertThat(validator.isValid(command, context)).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("email already exist");
    verify(violationBuilder).addPropertyNode("email");
  }

  @Test
  void should_return_false_when_username_belongs_to_another_user() {
    User otherUser = new User("other@x.com", "other", "secret", "", "");
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.empty());
    given(userRepository.findByUsername("newname")).willReturn(Optional.of(otherUser));

    assertThat(validator.isValid(command, context)).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("username already exist");
    verify(violationBuilder).addPropertyNode("username");
  }

  @Test
  void should_return_false_when_both_email_and_username_belong_to_other_users() {
    User otherEmailUser = new User("other1@x.com", "x", "secret", "", "");
    User otherNameUser = new User("other2@x.com", "other", "secret", "", "");
    given(userRepository.findByEmail("new@x.com")).willReturn(Optional.of(otherEmailUser));
    given(userRepository.findByUsername("newname")).willReturn(Optional.of(otherNameUser));

    assertThat(validator.isValid(command, context)).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("email already exist");
    verify(context).buildConstraintViolationWithTemplate("username already exist");
  }
}
