package io.spring.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link User} covering the {@code update} method's per-field guard branches.
 *
 * <p>Targets US-07.02: kill the surviving {@code NegateConditionals} mutants on
 * {@code User.update} (Pitest baseline lines 54 and 60 — password and image branches),
 * plus exercise the email, username and bio branches for symmetry.
 */
class UserTest {

  private User newUser() {
    return new User("a@b.com", "alice", "secret", "bio", "img");
  }

  @Test
  void should_assign_uuid_id_and_keep_constructor_fields() {
    User user = newUser();
    assertThat(user.getId()).isNotBlank();
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  // ---------------------------------------------------------------------------
  // update(): each field has its own !Util.isEmpty(...) guard. We verify each
  // branch independently to kill the per-line NegateConditionals mutants.
  // ---------------------------------------------------------------------------

  @Test
  void should_update_email_when_not_empty() {
    User user = newUser();
    user.update("new@b.com", "", "", "", "");
    assertThat(user.getEmail()).isEqualTo("new@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  @Test
  void should_update_username_when_not_empty() {
    User user = newUser();
    user.update("", "bob", "", "", "");
    assertThat(user.getUsername()).isEqualTo("bob");
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  @Test
  void should_update_password_when_not_empty() {
    User user = newUser();
    user.update("", "", "new-secret", "", "");
    assertThat(user.getPassword()).isEqualTo("new-secret");
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  @Test
  void should_update_bio_when_not_empty() {
    User user = newUser();
    user.update("", "", "", "new bio", "");
    assertThat(user.getBio()).isEqualTo("new bio");
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getImage()).isEqualTo("img");
  }

  @Test
  void should_update_image_when_not_empty() {
    User user = newUser();
    user.update("", "", "", "", "new-img");
    assertThat(user.getImage()).isEqualTo("new-img");
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
  }

  @Test
  void should_leave_all_fields_unchanged_when_all_inputs_are_empty() {
    User user = newUser();
    user.update("", "", "", "", "");
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  @Test
  void should_treat_null_inputs_as_empty_on_update() {
    User user = newUser();
    user.update(null, null, null, null, null);
    assertThat(user.getEmail()).isEqualTo("a@b.com");
    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getPassword()).isEqualTo("secret");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("img");
  }

  // ---------------------------------------------------------------------------
  // equals / hashCode (Lombok @EqualsAndHashCode(of = {"id"})) — sanity check
  // ---------------------------------------------------------------------------

  @Test
  void should_be_equal_to_self_and_have_consistent_hashcode() {
    User user = newUser();
    assertThat(user).isEqualTo(user);
    assertThat(user.hashCode()).isEqualTo(user.hashCode());
  }

  @Test
  void should_not_be_equal_to_another_user_with_different_id() {
    User a = newUser();
    User b = newUser();
    assertThat(a).isNotEqualTo(b);
  }
}
