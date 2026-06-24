package io.spring.infrastructure.repository.readservice;

import io.spring.application.data.UserData;
import io.spring.infrastructure.repository.readservice.UserReadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaUserReadService implements UserReadService {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public UserData findByUsername(String username) {
    return findOne("select u.id, u.email, u.username, u.bio, u.image from users u"
            + " where u.username = :value", username);
  }

  @Override
  public UserData findById(String id) {
    return findOne("select u.id, u.email, u.username, u.bio, u.image from users u"
            + " where u.id = :value", id);
  }

  @SuppressWarnings("unchecked")
  private UserData findOne(String sql, String value) {
    List<Object[]> rows =
        entityManager.createNativeQuery(sql).setParameter("value", value).getResultList();
    if (rows.isEmpty()) {
      return null;
    }
    Object[] row = rows.get(0);
    return new UserData(
        (String) row[0], (String) row[1], (String) row[2], (String) row[3], (String) row[4]);
  }
}
