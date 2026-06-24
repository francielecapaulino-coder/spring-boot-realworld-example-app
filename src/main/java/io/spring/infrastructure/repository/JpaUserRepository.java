package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.jpa.FollowRelationJpaRepository;
import io.spring.infrastructure.jpa.UserJpaRepository;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
public class JpaUserRepository implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final FollowRelationJpaRepository followRelationJpaRepository;

  public JpaUserRepository(
      UserJpaRepository userJpaRepository,
      FollowRelationJpaRepository followRelationJpaRepository) {
    this.userJpaRepository = userJpaRepository;
    this.followRelationJpaRepository = followRelationJpaRepository;
  }

  @Override
  @Transactional
  public void save(User user) {
    userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findById(String id) {
    return userJpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userJpaRepository.findByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  @Transactional
  public void saveRelation(FollowRelation followRelation) {
    followRelationJpaRepository.save(followRelation);
  }

  @Override
  public Optional<FollowRelation> findRelation(String userId, String targetId) {
    return followRelationJpaRepository.findByIdUserIdAndIdTargetId(userId, targetId);
  }

  @Override
  @Transactional
  public void removeRelation(FollowRelation followRelation) {
    followRelationJpaRepository.delete(followRelation);
  }
}
