package io.spring.core.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follows")
@NoArgsConstructor
@Getter
// Equality is delegated to the composite key. The fields "userId" / "targetId"
// referenced previously do not exist on this class - they live inside the
// embedded id - which produced two Lombok "This field does not exist" warnings.
// The inner FollowRelationId already declares @EqualsAndHashCode over its own
// fields (via @Data), so comparing by id preserves the (userId, targetId)
// semantics.
@EqualsAndHashCode(of = {"id"})
public class FollowRelation {

  @EmbeddedId private FollowRelationId id;

  public FollowRelation(String userId, String targetId) {
    this.id = new FollowRelationId(userId, targetId);
  }

  public String getUserId() {
    return id.getUserId();
  }

  public String getTargetId() {
    return id.getTargetId();
  }

  @Embeddable
  @NoArgsConstructor
  @Data
  @EqualsAndHashCode
  public static class FollowRelationId implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "follow_id")
    private String targetId;

    public FollowRelationId(String userId, String targetId) {
      this.userId = userId;
      this.targetId = targetId;
    }
  }
}
