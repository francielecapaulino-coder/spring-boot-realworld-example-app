package io.spring.infrastructure.repository.readservice;

import io.spring.infrastructure.repository.readservice.TagReadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaTagReadService implements TagReadService {

  @PersistenceContext private EntityManager entityManager;

  @Override
  @SuppressWarnings("unchecked")
  public List<String> all() {
    return entityManager.createNativeQuery("select name from tags").getResultList();
  }
}
