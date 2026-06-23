package io.spring.infrastructure.jpa;

import io.spring.core.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, String> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}
