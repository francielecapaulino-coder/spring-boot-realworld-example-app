package io.spring.infrastructure.jpa;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.FollowRelation.FollowRelationId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRelationJpaRepository
    extends JpaRepository<FollowRelation, FollowRelationId> {

  Optional<FollowRelation> findByIdUserIdAndIdTargetId(String userId, String targetId);

  @Query(
      "SELECT f.id.targetId FROM FollowRelation f WHERE f.id.userId = :userId AND f.id.targetId IN :ids")
  Set<String> findFollowingTargetIds(
      @Param("userId") String userId, @Param("ids") List<String> ids);

  @Query("SELECT f.id.targetId FROM FollowRelation f WHERE f.id.userId = :userId")
  List<String> findFollowedUserIds(@Param("userId") String userId);
}
