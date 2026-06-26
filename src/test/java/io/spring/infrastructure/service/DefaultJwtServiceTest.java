package io.spring.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultJwtServiceTest {

  private static final String SECRET =
      "123123123123123123123123123123123123123123123123123123123123";
  private static final int SESSION_SECONDS = 3600;

  private JwtService jwtService;

  @BeforeEach
  public void setUp() {
    jwtService = new DefaultJwtService(SECRET, SESSION_SECONDS);
  }

  @Test
  public void should_generate_and_parse_token() {
    User user = new User("email@email.com", "username", "123", "", "");
    String token = jwtService.toToken(user);
    Assertions.assertNotNull(token);
    Optional<String> optional = jwtService.getSubFromToken(token);
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get(), user.getId());
  }

  @Test
  public void should_get_null_with_wrong_jwt() {
    Optional<String> optional = jwtService.getSubFromToken("123");
    Assertions.assertFalse(optional.isPresent());
  }

  @Test
  public void should_get_null_with_expired_jwt() {
    String token =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhaXNlbnNpeSIsImV4cCI6MTUwMjE2MTIwNH0.SJB-U60WzxLYNomqLo4G3v3LzFxJKuVrIud8D8Lz3-mgpo9pN1i7C8ikU_jQPJGm8HsC1CquGMI-rSuM7j6LDA";
    Assertions.assertFalse(jwtService.getSubFromToken(token).isPresent());
  }

  /**
   * Decodes the token's {@code exp} claim and asserts it sits roughly
   * {@code sessionSeconds} in the future. This kills the {@code NullReturnVals}
   * mutant on the private {@code expireTimeFromNow()} helper (line 52): if the
   * helper returned {@code null}, the {@code expiration(...)} call would either
   * throw or produce a token without an exp claim, and either way the assertion
   * would fail.
   */
  @Test
  public void generated_token_should_carry_expiration_claim_in_the_future() {
    User user = new User("email@email.com", "username", "123", "", "");
    long beforeIssue = System.currentTimeMillis();
    String token = jwtService.toToken(user);

    SecretKey signingKey =
        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    Jws<Claims> parsed =
        Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
    Date expiration = parsed.getPayload().getExpiration();

    assertThat(expiration).isNotNull();
    long expectedMin = beforeIssue + (SESSION_SECONDS - 5) * 1000L;
    long expectedMax = System.currentTimeMillis() + (SESSION_SECONDS + 5) * 1000L;
    assertThat(expiration.getTime()).isBetween(expectedMin, expectedMax);
  }
}
