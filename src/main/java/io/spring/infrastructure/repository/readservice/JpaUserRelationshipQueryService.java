package io.spring.infrastructure.repository.readservice;

import io.spring.infrastructure.jpa.FollowRelationJpaRepository;
import io.spring.infrastructure.repository.readservice.UserRelationshipQueryService;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class JpaUserRelationshipQueryService implements UserRelationshipQueryService {

  private final FollowRelationJpaRepository followRelationJpaRepository;

  public JpaUserRelationshipQueryService(
      FollowRelationJpaRepository followRelationJpaRepository) {
    this.followRelationJpaRepository = followRelationJpaRepository;
  }

  @Override
  public boolean isUserFollowing(String userId, String anotherUserId) {
    return followRelationJpaRepository
        .findByIdUserIdAndIdTargetId(userId, anotherUserId)
        .isPresent();
  }

  @Override
  public Set<String> followingAuthors(String userId, List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return Set.of();
    }
    return followRelationJpaRepository.findFollowingTargetIds(userId, ids);
  }

  @Override
  public List<String> followedUsers(String userId) {
    return followRelationJpaRepository.findFollowedUserIds(userId);
  }
}
