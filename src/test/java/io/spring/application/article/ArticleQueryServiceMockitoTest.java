package io.spring.application.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.repository.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.repository.readservice.ArticleReadService;
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
 * Mockito-only unit tests for {@link ArticleQueryService} targeting US-07.03.
 *
 * <p>Focuses on the branches not exercised by the existing DB-backed
 * {@code ArticleQueryServiceTest}, namely the empty/anonymous paths and the
 * cursor-paging logic (limit / hasExtra / direction). Kills the
 * SURVIVED/NO_COVERAGE mutants on {@code findRecentArticlesWithCursor} and
 * {@code findUserFeedWithCursor}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleQueryServiceMockitoTest {

  @Mock private ArticleReadService articleReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;
  @Mock private ArticleFavoritesReadService articleFavoritesReadService;

  @InjectMocks private ArticleQueryService service;

  private final User user = new User("a@x.com", "u", "p", "", "");
  private final CursorPageParameter<Instant> nextPage =
      new CursorPageParameter<>(null, 20, Direction.NEXT);
  private final CursorPageParameter<Instant> prevPage =
      new CursorPageParameter<>(null, 20, Direction.PREV);

  // ---------------------------------------------------------------------------
  // findById / findBySlug
  // ---------------------------------------------------------------------------

  @Test
  void findById_should_return_empty_when_article_does_not_exist() {
    given(articleReadService.findById("missing")).willReturn(null);
    assertThat(service.findById("missing", user)).isEmpty();
  }

  @Test
  void findById_should_return_article_without_enrichment_when_no_current_user() {
    ArticleData article = newArticle("1");
    given(articleReadService.findById("1")).willReturn(article);

    Optional<ArticleData> result = service.findById("1", null);

    assertThat(result).contains(article);
  }

  @Test
  void findBySlug_should_return_empty_when_article_does_not_exist() {
    given(articleReadService.findBySlug("missing")).willReturn(null);
    assertThat(service.findBySlug("missing", user)).isEmpty();
  }

  @Test
  void findBySlug_should_enrich_with_favorited_and_following_flags_when_user_present() {
    ArticleData article = newArticle("a-1");
    given(articleReadService.findBySlug("slug-1")).willReturn(article);
    given(articleFavoritesReadService.isUserFavorite(anyString(), eq("a-1"))).willReturn(true);
    given(articleFavoritesReadService.articleFavoriteCount("a-1")).willReturn(3);
    given(userRelationshipQueryService.isUserFollowing(anyString(), eq("p-a-1"))).willReturn(true);

    Optional<ArticleData> result = service.findBySlug("slug-1", user);

    assertThat(result).isPresent();
    ArticleData enriched = result.orElseThrow();
    assertThat(enriched.favorited()).isTrue();
    assertThat(enriched.favoritesCount()).isEqualTo(3);
    assertThat(enriched.profileData().following()).isTrue();
  }

  // ---------------------------------------------------------------------------
  // findRecentArticlesWithCursor — limit / hasExtra / direction branches
  // ---------------------------------------------------------------------------

  @Test
  void findRecentArticlesWithCursor_should_return_empty_pager_when_no_ids() {
    given(articleReadService.findArticlesWithCursor(any(), any(), any(), any()))
        .willReturn(new ArrayList<>());

    CursorPager<ArticleData> pager = service.findRecentArticlesWithCursor(null, null, null, nextPage, user);

    assertThat(pager.getData()).isEmpty();
    assertThat(pager.hasNext()).isFalse();
  }

  @Test
  void findRecentArticlesWithCursor_should_return_single_article_when_one_id() {
    // Boundary: distinguishes `size() == 0` from `size() <= 0`
    List<String> ids = new ArrayList<>(List.of("a-0"));
    given(articleReadService.findArticlesWithCursor(any(), any(), any(), any())).willReturn(ids);
    given(articleReadService.findArticles(any())).willReturn(makeArticleList(1));
    given(articleFavoritesReadService.articlesFavoriteCount(any()))
        .willReturn(new ArrayList<>(List.of(new io.spring.application.data.ArticleFavoriteCount("a-0", 7))));
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of("a-0"));
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager =
        service.findRecentArticlesWithCursor(null, null, null, nextPage, user);

    assertThat(pager.getData()).hasSize(1);
    assertThat(pager.hasNext()).isFalse();
    // Confirms fillExtraInfo executed: favoritesCount was enriched from 0 to 7
    assertThat(pager.getData().get(0).favoritesCount()).isEqualTo(7);
    assertThat(pager.getData().get(0).favorited()).isTrue();
  }

  @Test
  void findRecentArticlesWithCursor_should_truncate_when_more_ids_than_limit() {
    List<String> ids = makeIdList(21);
    given(articleReadService.findArticlesWithCursor(any(), any(), any(), any())).willReturn(ids);
    List<ArticleData> articles = makeArticleList(20);
    given(articleReadService.findArticles(any())).willReturn(articles);
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager =
        service.findRecentArticlesWithCursor(null, null, null, nextPage, user);

    assertThat(pager.hasNext()).isTrue();
    // Confirms that the removed id was the LAST one (page.getLimit() == 20)
    assertThat(pager.getData()).hasSize(20);
  }

  @Test
  void findRecentArticlesWithCursor_should_return_exactly_limit_when_count_equals_limit() {
    // Boundary: distinguishes `> limit` from `>= limit`
    List<String> ids = makeIdList(20);
    given(articleReadService.findArticlesWithCursor(any(), any(), any(), any())).willReturn(ids);
    given(articleReadService.findArticles(any())).willReturn(makeArticleList(20));
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager =
        service.findRecentArticlesWithCursor(null, null, null, nextPage, user);

    assertThat(pager.getData()).hasSize(20);
    assertThat(pager.hasNext()).isFalse(); // exactly limit -> no extra
  }

  @Test
  void findRecentArticlesWithCursor_should_reverse_when_direction_is_prev() {
    // Order matters: must verify the ids passed to findArticles reflect the
    // reverse, killing both the negate-conditional and the
    // Collections.reverse removal mutants.
    List<String> ids = new ArrayList<>(List.of("a-0", "a-1", "a-2"));
    given(articleReadService.findArticlesWithCursor(any(), any(), any(), any())).willReturn(ids);
    given(articleReadService.findArticles(any())).willReturn(makeArticleList(3));
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager =
        service.findRecentArticlesWithCursor(null, null, null, prevPage, user);

    assertThat(pager.hasPrevious()).isFalse();
    // Confirms reverse was applied: ids passed downstream are reversed.
    org.mockito.ArgumentCaptor<List<String>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
    org.mockito.Mockito.verify(articleReadService).findArticles(captor.capture());
    assertThat(captor.getValue()).containsExactly("a-2", "a-1", "a-0");
  }

  // ---------------------------------------------------------------------------
  // findUserFeedWithCursor — no-coverage block
  // ---------------------------------------------------------------------------

  @Test
  void findUserFeedWithCursor_should_return_empty_when_user_follows_nobody() {
    given(userRelationshipQueryService.followedUsers(anyString())).willReturn(new ArrayList<>());

    CursorPager<ArticleData> pager = service.findUserFeedWithCursor(user, nextPage);

    assertThat(pager.getData()).isEmpty();
    assertThat(pager.hasNext()).isFalse();
  }

  @Test
  void findUserFeedWithCursor_should_return_articles_from_followed_authors() {
    given(userRelationshipQueryService.followedUsers(anyString()))
        .willReturn(new ArrayList<>(List.of("author-1")));
    given(articleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .willReturn(makeArticleList(3));
    given(articleFavoritesReadService.articlesFavoriteCount(any()))
        .willReturn(
            new ArrayList<>(
                List.of(new io.spring.application.data.ArticleFavoriteCount("a-0", 9))));
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of("a-0"));
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager = service.findUserFeedWithCursor(user, nextPage);

    assertThat(pager.getData()).hasSize(3);
    // Confirms fillExtraInfo executed in the feed branch
    assertThat(pager.getData().get(0).favoritesCount()).isEqualTo(9);
    assertThat(pager.getData().get(0).favorited()).isTrue();
  }

  @Test
  void findUserFeedWithCursor_should_truncate_and_mark_hasNext_when_more_than_limit() {
    given(userRelationshipQueryService.followedUsers(anyString()))
        .willReturn(new ArrayList<>(List.of("author-1")));
    given(articleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .willReturn(makeArticleList(21));
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager = service.findUserFeedWithCursor(user, nextPage);

    assertThat(pager.hasNext()).isTrue();
    assertThat(pager.getData()).hasSize(20);
  }

  @Test
  void findUserFeedWithCursor_should_return_exactly_limit_when_count_equals_limit() {
    // Boundary: distinguishes `> limit` from `>= limit` in the feed branch
    given(userRelationshipQueryService.followedUsers(anyString()))
        .willReturn(new ArrayList<>(List.of("author-1")));
    given(articleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .willReturn(makeArticleList(20));
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager = service.findUserFeedWithCursor(user, nextPage);

    assertThat(pager.getData()).hasSize(20);
    assertThat(pager.hasNext()).isFalse();
  }

  @Test
  void findUserFeedWithCursor_should_reverse_when_direction_is_prev() {
    given(userRelationshipQueryService.followedUsers(anyString()))
        .willReturn(new ArrayList<>(List.of("author-1")));
    List<ArticleData> articles = new ArrayList<>(makeArticleList(3));
    given(articleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .willReturn(articles);
    given(articleFavoritesReadService.articlesFavoriteCount(any())).willReturn(new ArrayList<>());
    given(articleFavoritesReadService.userFavorites(any(), any())).willReturn(Set.of());
    given(userRelationshipQueryService.followingAuthors(any(), any())).willReturn(Set.of());

    CursorPager<ArticleData> pager = service.findUserFeedWithCursor(user, prevPage);

    assertThat(pager.hasPrevious()).isFalse();
    // Order must be reversed (kills negate + reverse-removal mutants)
    assertThat(pager.getData()).extracting(ArticleData::id).containsExactly("a-2", "a-1", "a-0");
  }

  // ---------------------------------------------------------------------------
  // findRecentArticles (page-based)
  // ---------------------------------------------------------------------------

  @Test
  void findRecentArticles_should_return_empty_list_when_no_ids() {
    given(articleReadService.queryArticles(any(), any(), any(), any())).willReturn(new ArrayList<>());
    given(articleReadService.countArticle(any(), any(), any())).willReturn(0);

    ArticleDataList list =
        service.findRecentArticles(null, null, null, new io.spring.application.Page(), user);

    assertThat(list.articleDatas()).isEmpty();
    assertThat(list.count()).isZero();
  }

  // ---------------------------------------------------------------------------
  // findUserFeed (page-based) — covers the followed.size() == 0 branch
  // ---------------------------------------------------------------------------

  @Test
  void findUserFeed_should_return_empty_list_when_user_follows_nobody() {
    given(userRelationshipQueryService.followedUsers(anyString())).willReturn(new ArrayList<>());

    ArticleDataList list = service.findUserFeed(user, new io.spring.application.Page());

    assertThat(list.articleDatas()).isEmpty();
    assertThat(list.count()).isZero();
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static ArticleData newArticle(String id) {
    ProfileData profile = new ProfileData("p-" + id, "alice", "bio", "img", false);
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    return new ArticleData(
        id, "slug-" + id, "title", "desc", "body", false, 0, now, now, new ArrayList<>(), profile);
  }

  private static List<String> makeIdList(int count) {
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ids.add("a-" + i);
    }
    return ids;
  }

  private static List<ArticleData> makeArticleList(int count) {
    List<ArticleData> list = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      list.add(newArticle("a-" + i));
    }
    return list;
  }
}
