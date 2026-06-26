package io.spring.application.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.repository.readservice.CommentReadService;
import io.spring.infrastructure.repository.readservice.UserRelationshipQueryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Mockito-only unit tests for {@link CommentQueryService} targeting US-07.03.
 *
 * <p>Focuses on the branches not exercised by the DB-backed
 * {@code CommentQueryServiceTest}: the null path of {@code findById}, the
 * empty / anonymous paths of {@code findByArticleId}, and every branch of
 * {@code findByArticleIdWithCursor} (empty, with following authors,
 * hasExtra, direction=PREV). Kills the 17 SURVIVED/NO_COVERAGE mutants on
 * this class reported by the US-07.02 baseline.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentQueryServiceMockitoTest {

  @Mock private CommentReadService commentReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;

  @InjectMocks private CommentQueryService service;

  private final User user = new User("a@x.com", "u", "p", "", "");
  private final CursorPageParameter<Instant> nextPage =
      new CursorPageParameter<>(null, 20, Direction.NEXT);
  private final CursorPageParameter<Instant> prevPage =
      new CursorPageParameter<>(null, 20, Direction.PREV);

  // ---------------------------------------------------------------------------
  // findById
  // ---------------------------------------------------------------------------

  @Test
  void findById_should_return_empty_when_comment_does_not_exist() {
    given(commentReadService.findById("missing")).willReturn(null);
    assertThat(service.findById("missing", user)).isEmpty();
  }

  @Test
  void findById_should_set_following_true_when_user_follows_author() {
    CommentData comment = newComment("c-1", "author-1");
    given(commentReadService.findById("c-1")).willReturn(comment);
    given(userRelationshipQueryService.isUserFollowing(anyString(), eq("author-1"))).willReturn(true);

    Optional<CommentData> result = service.findById("c-1", user);

    assertThat(result).isPresent();
    assertThat(result.orElseThrow().profileData().following()).isTrue();
  }

  @Test
  void findById_should_set_following_false_when_user_does_not_follow_author() {
    CommentData comment = newComment("c-1", "author-1");
    given(commentReadService.findById("c-1")).willReturn(comment);
    given(userRelationshipQueryService.isUserFollowing(anyString(), eq("author-1"))).willReturn(false);

    Optional<CommentData> result = service.findById("c-1", user);

    assertThat(result.orElseThrow().profileData().following()).isFalse();
  }

  // ---------------------------------------------------------------------------
  // findByArticleId
  // ---------------------------------------------------------------------------

  @Test
  void findByArticleId_should_return_empty_list_when_no_comments_exist() {
    given(commentReadService.findByArticleId("a-1")).willReturn(new ArrayList<>());
    assertThat(service.findByArticleId("a-1", user)).isEmpty();
  }

  @Test
  void findByArticleId_should_not_query_follows_when_user_is_null() {
    given(commentReadService.findByArticleId("a-1"))
        .willReturn(new ArrayList<>(List.of(newComment("c-1", "author-1"))));

    List<CommentData> result = service.findByArticleId("a-1", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).profileData().following()).isFalse();
  }

  @Test
  void findByArticleId_should_flip_following_true_for_authors_user_follows() {
    given(commentReadService.findByArticleId("a-1"))
        .willReturn(
            new ArrayList<>(
                List.of(newComment("c-1", "author-1"), newComment("c-2", "author-2"))));
    given(userRelationshipQueryService.followingAuthors(anyString(), any()))
        .willReturn(Set.of("author-1"));

    List<CommentData> result = service.findByArticleId("a-1", user);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).profileData().following()).isTrue();
    assertThat(result.get(1).profileData().following()).isFalse();
  }

  // ---------------------------------------------------------------------------
  // findByArticleIdWithCursor
  // ---------------------------------------------------------------------------

  @Test
  void findByArticleIdWithCursor_should_return_empty_pager_when_no_comments() {
    given(commentReadService.findByArticleIdWithCursor(anyString(), any()))
        .willReturn(new ArrayList<>());

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", user, nextPage);

    assertThat(pager.getData()).isEmpty();
    assertThat(pager.hasNext()).isFalse();
  }

  @Test
  void findByArticleIdWithCursor_should_skip_follow_lookup_when_user_is_null() {
    given(commentReadService.findByArticleIdWithCursor(anyString(), any()))
        .willReturn(new ArrayList<>(List.of(newComment("c-1", "author-1"))));

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", null, nextPage);

    assertThat(pager.getData()).hasSize(1);
    assertThat(pager.getData().get(0).profileData().following()).isFalse();
  }

  @Test
  void findByArticleIdWithCursor_should_truncate_and_mark_hasNext_when_more_than_limit() {
    List<CommentData> comments = new ArrayList<>();
    for (int i = 0; i < 21; i++) {
      comments.add(newComment("c-" + i, "author-" + i));
    }
    given(commentReadService.findByArticleIdWithCursor(anyString(), any())).willReturn(comments);
    given(userRelationshipQueryService.followingAuthors(anyString(), any())).willReturn(Set.of());

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", user, nextPage);

    assertThat(pager.hasNext()).isTrue();
    assertThat(pager.getData()).hasSize(20);
  }

  @Test
  void findByArticleIdWithCursor_should_keep_full_list_when_count_equals_limit() {
    // Boundary: distinguishes `> limit` from `>= limit`
    List<CommentData> comments = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      comments.add(newComment("c-" + i, "author-" + i));
    }
    given(commentReadService.findByArticleIdWithCursor(anyString(), any())).willReturn(comments);
    given(userRelationshipQueryService.followingAuthors(anyString(), any())).willReturn(Set.of());

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", user, nextPage);

    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.getData()).hasSize(20);
  }

  @Test
  void findByArticleId_should_apply_following_for_single_comment_when_user_follows_its_author() {
    // Kills the EmptyObjectReturnVals mutant on the .id() lambda by requiring
    // that the exact authorId of the single comment matches the followingAuthors
    // set (if the lambda returned "" instead of the real id, the following
    // assertion would fail because Set.of("author-only") would not contain "").
    given(commentReadService.findByArticleId("a-1"))
        .willReturn(new ArrayList<>(List.of(newComment("c-only", "author-only"))));
    org.mockito.ArgumentCaptor<List<String>> captor =
        org.mockito.ArgumentCaptor.forClass(List.class);
    given(userRelationshipQueryService.followingAuthors(anyString(), captor.capture()))
        .willReturn(Set.of("author-only"));

    List<CommentData> result = service.findByArticleId("a-1", user);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).profileData().following()).isTrue();
    assertThat(result.get(0).profileData().id()).isEqualTo("author-only");
    // The ids passed to followingAuthors must be the actual profile ids
    // (kills the lambda EmptyObjectReturnVals mutant returning "").
    assertThat(captor.getValue()).containsExactly("author-only");
  }

  @Test
  void findByArticleId_should_capture_real_profile_ids_when_multiple_comments() {
    // Reinforces the EmptyObjectReturnVals kill: passing through multiple
    // comments confirms the mapped ids reflect their actual profileData.id().
    given(commentReadService.findByArticleId("a-1"))
        .willReturn(
            new ArrayList<>(
                List.of(
                    newComment("c-1", "author-A"),
                    newComment("c-2", "author-B"),
                    newComment("c-3", "author-C"))));
    org.mockito.ArgumentCaptor<List<String>> captor =
        org.mockito.ArgumentCaptor.forClass(List.class);
    given(userRelationshipQueryService.followingAuthors(anyString(), captor.capture()))
        .willReturn(Set.of("author-B"));

    List<CommentData> result = service.findByArticleId("a-1", user);

    assertThat(captor.getValue()).containsExactly("author-A", "author-B", "author-C");
    assertThat(result.get(0).profileData().following()).isFalse();
    assertThat(result.get(1).profileData().following()).isTrue();
    assertThat(result.get(2).profileData().following()).isFalse();
  }

  @Test
  void findByArticleIdWithCursor_should_reverse_data_when_direction_is_prev() {
    CommentData first = newComment("c-1", "a-1");
    CommentData last = newComment("c-2", "a-2");
    given(commentReadService.findByArticleIdWithCursor(anyString(), any()))
        .willReturn(new ArrayList<>(List.of(first, last)));
    given(userRelationshipQueryService.followingAuthors(anyString(), any())).willReturn(Set.of());

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", user, prevPage);

    assertThat(pager.getData()).extracting(CommentData::id).containsExactly("c-2", "c-1");
  }

  @Test
  void findByArticleIdWithCursor_should_apply_following_flag_when_user_follows_author() {
    given(commentReadService.findByArticleIdWithCursor(anyString(), any()))
        .willReturn(new ArrayList<>(List.of(newComment("c-1", "author-1"))));
    given(userRelationshipQueryService.followingAuthors(anyString(), any()))
        .willReturn(Set.of("author-1"));

    CursorPager<CommentData> pager = service.findByArticleIdWithCursor("a-1", user, nextPage);

    assertThat(pager.getData().get(0).profileData().following()).isTrue();
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static CommentData newComment(String id, String authorId) {
    ProfileData profile = new ProfileData(authorId, "name-" + authorId, "bio", "img", false);
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    return new CommentData(id, "body", "article-1", now, now, profile);
  }
}
