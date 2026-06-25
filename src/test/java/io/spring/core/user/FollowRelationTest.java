package io.spring.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FollowRelation} covering the two NO_COVERAGE accessors flagged in
 * the US-07.01 baseline (lines 34 and 38 — {@code getUserId()} and {@code getTargetId()}).
 */
class FollowRelationTest {

  @Test
  void should_expose_user_id_and_target_id_through_accessors() {
    FollowRelation relation = new FollowRelation("user-1", "target-2");
    assertThat(relation.getUserId()).isEqualTo("user-1");
    assertThat(relation.getTargetId()).isEqualTo("target-2");
  }

  @Test
  void should_build_composite_id_from_constructor_arguments() {
    FollowRelation relation = new FollowRelation("u", "t");
    assertThat(relation.getId()).isNotNull();
    assertThat(relation.getId().getUserId()).isEqualTo("u");
    assertThat(relation.getId().getTargetId()).isEqualTo("t");
  }

  @Test
  void should_be_equal_when_composite_ids_match() {
    FollowRelation a = new FollowRelation("u", "t");
    FollowRelation b = new FollowRelation("u", "t");
    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void should_not_be_equal_when_composite_ids_differ() {
    FollowRelation a = new FollowRelation("u", "t");
    FollowRelation different = new FollowRelation("u", "other");
    assertThat(a).isNotEqualTo(different);
  }
}
