package io.spring.application.data;

/**
 * Login/register response payload combining a user view with the issued JWT.
 *
 * <p>Pure carrier of data — eligible for {@code record} conversion under
 * US-06.02 / KR1.5 (mandate J5). The convenience constructor below preserves
 * the previous {@code (UserData, String)} factory shape used by API controllers.
 */
public record UserWithToken(
    String email, String username, String bio, String image, String token) {

  public UserWithToken(UserData userData, String token) {
    this(userData.email(), userData.username(), userData.bio(), userData.image(), token);
  }
}
