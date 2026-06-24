package io.spring.infrastructure.jpa;

import io.spring.core.article.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, String> {
  Optional<Tag> findByName(String name);
}
