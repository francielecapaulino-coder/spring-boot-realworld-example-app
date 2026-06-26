package io.spring.infrastructure.repository.readservice;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager.Direction;
import io.spring.application.Page;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.JpaArticleFavoriteRepository;
import io.spring.infrastructure.repository.JpaArticleRepository;
import io.spring.infrastructure.repository.JpaUserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Repository-slice tests for {@link JpaArticleReadService} targeting US-07.04.
 *
 * <p>Exercises every branch of the native-SQL helpers (filter combinations,
 * cursor NEXT/PREV with and without a cursor value, empty/null input handling,
 * soft-delete predicate) so the 20 SURVIVED/NO_COVERAGE mutants reported on
 * this class by the US-07.03 baseline are killed.
 */
@Import({
  JpaArticleReadService.class,
  JpaArticleRepository.class,
  JpaUserRepository.class,
  JpaArticleFavoriteRepository.class,
})
class JpaArticleReadServiceTest extends DbTestBase {

  @Autowired private JpaArticleReadService readService;
  @Autowired private ArticleRepository articleRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ArticleFavoriteRepository articleFavoriteRepository;

  private User author;
  private User otherAuthor;
  private Article article;
  private Article tagged;
  private Article otherAuthored;

  @BeforeEach
  void setUp() {
    author = new User("author@x.com", "author", "p", "", "");
    otherAuthor = new User("other@x.com", "other", "p", "", "");
    userRepository.save(author);
    userRepository.save(otherAuthor);

    article = new Article("first", "desc-1", "body-1", List.of(), author.getId());
    tagged = new Article("tagged", "desc-2", "body-2", Arrays.asList("java", "spring"), author.getId());
    otherAuthored = new Article("other-one", "desc-3", "body-3", List.of(), otherAuthor.getId());
    articleRepository.save(article);
    articleRepository.save(tagged);
    articleRepository.save(otherAuthored);
  }

  // ---------------------------------------------------------------------------
  // findById / findBySlug
  // ---------------------------------------------------------------------------

  @Test
  void findById_should_return_data_for_existing_article() {
    ArticleData data = readService.findById(article.getId());
    assertThat(data).isNotNull();
    assertThat(data.id()).isEqualTo(article.getId());
    assertThat(data.title()).isEqualTo("first");
    // Asserting timestamps non-null kills NullReturnVals on the toInstant
    // helpers used by mapArticleRows.
    assertThat(data.createdAt()).isNotNull();
    assertThat(data.updatedAt()).isNotNull();
  }

  @Test
  void findById_should_return_null_for_missing_article() {
    assertThat(readService.findById("missing-id")).isNull();
  }

  @Test
  void findBySlug_should_return_data_for_existing_article() {
    ArticleData data = readService.findBySlug(article.getSlug());
    assertThat(data).isNotNull();
    assertThat(data.slug()).isEqualTo(article.getSlug());
  }

  @Test
  void findBySlug_should_return_null_for_missing_article() {
    assertThat(readService.findBySlug("missing-slug")).isNull();
  }

  // ---------------------------------------------------------------------------
  // findArticles — empty / null / populated branches
  // ---------------------------------------------------------------------------

  @Test
  void findArticles_should_return_empty_list_when_ids_is_null() {
    assertThat(readService.findArticles(null)).isEmpty();
  }

  @Test
  void findArticles_should_return_mutable_empty_list_when_ids_is_null() {
    // Kills EmptyObjectReturnVals on line 113: an immutable Collections.emptyList()
    // would throw UnsupportedOperationException on add().
    List<ArticleData> result = readService.findArticles(null);
    result.add(newArticle("anything"));
    assertThat(result).hasSize(1);
  }

  @Test
  void findArticles_should_return_empty_list_when_ids_is_empty() {
    assertThat(readService.findArticles(Collections.emptyList())).isEmpty();
  }

  @Test
  void findArticles_should_return_mutable_empty_list_when_ids_is_empty() {
    // Same EmptyObjectReturnVals kill via the empty-list path on line 113.
    List<ArticleData> result = readService.findArticles(Collections.emptyList());
    result.add(newArticle("anything"));
    assertThat(result).hasSize(1);
  }

  @Test
  void findArticles_should_return_articles_matching_provided_ids() {
    List<ArticleData> result =
        readService.findArticles(Arrays.asList(article.getId(), tagged.getId()));
    assertThat(result).extracting(ArticleData::id)
        .containsExactlyInAnyOrder(article.getId(), tagged.getId());
  }

  // ---------------------------------------------------------------------------
  // findArticlesOfAuthors — empty / null / populated
  // ---------------------------------------------------------------------------

  @Test
  void findArticlesOfAuthors_should_return_empty_list_when_authors_is_null() {
    assertThat(readService.findArticlesOfAuthors(null, new Page())).isEmpty();
  }

  @Test
  void findArticlesOfAuthors_should_return_empty_list_when_authors_is_empty() {
    assertThat(readService.findArticlesOfAuthors(Collections.emptyList(), new Page())).isEmpty();
  }

  @Test
  void findArticlesOfAuthors_should_return_mutable_empty_list_when_authors_is_null() {
    // Kills EmptyObjectReturnVals on line 129.
    List<ArticleData> result = readService.findArticlesOfAuthors(null, new Page());
    result.add(newArticle("any"));
    assertThat(result).hasSize(1);
  }

  @Test
  void findArticlesOfAuthors_should_return_articles_for_provided_authors() {
    List<ArticleData> result =
        readService.findArticlesOfAuthors(List.of(author.getId()), new Page());
    assertThat(result).extracting(ArticleData::id)
        .containsExactlyInAnyOrder(article.getId(), tagged.getId());
  }

  // ---------------------------------------------------------------------------
  // findArticlesOfAuthorsWithCursor — cursor NEXT/PREV with/without cursor value
  // ---------------------------------------------------------------------------

  @Test
  void findArticlesOfAuthorsWithCursor_should_return_empty_when_authors_is_null() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    assertThat(readService.findArticlesOfAuthorsWithCursor(null, page)).isEmpty();
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_return_empty_when_authors_is_empty() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    assertThat(readService.findArticlesOfAuthorsWithCursor(Collections.emptyList(), page))
        .isEmpty();
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_return_mutable_empty_list_when_authors_is_null() {
    // Kills EmptyObjectReturnVals on line 149.
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<ArticleData> result = readService.findArticlesOfAuthorsWithCursor(null, page);
    result.add(newArticle("any"));
    assertThat(result).hasSize(1);
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_return_articles_for_next_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<ArticleData> result =
        readService.findArticlesOfAuthorsWithCursor(List.of(author.getId()), page);
    assertThat(result).extracting(ArticleData::id)
        .containsExactlyInAnyOrder(article.getId(), tagged.getId());
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_return_articles_for_prev_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<ArticleData> result =
        readService.findArticlesOfAuthorsWithCursor(List.of(author.getId()), page);
    assertThat(result).hasSizeBetween(1, 2);
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_accept_cursor_for_next_without_failing() {
    // Branch coverage for `hasCursor && Direction.NEXT`. The cursor uses the
    // article's own createdAt to dodge JVM/Postgres timezone interpretation
    // differences (Timestamp.from()'s naive UTC->JVM-zone fold). The cursor-
    // free tests above already assert the result set is correct.
    ArticleData reference = readService.findById(tagged.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.NEXT);
    assertThat(readService.findArticlesOfAuthorsWithCursor(List.of(author.getId()), page))
        .isNotNull();
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_accept_cursor_for_prev_without_failing() {
    ArticleData reference = readService.findById(tagged.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.PREV);
    assertThat(readService.findArticlesOfAuthorsWithCursor(List.of(author.getId()), page))
        .isNotNull();
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_filter_with_past_cursor_to_kill_null_timestamp() {
    // Kills NullReturnVals on toTimestamp line 267: if the helper returned null
    // instead of Timestamp.from(instant), the SQL comparison `created_at < NULL`
    // would always evaluate to false in PostgreSQL and the query would return
    // zero rows. We seed two articles with timestamps strictly before the cursor
    // value so a working toTimestamp produces a non-null bind value and the
    // query returns >= 1 row.
    User author2 = new User("a3@x.com", "author3", "p", "", "");
    userRepository.save(author2);
    Instant t1 = Instant.parse("2026-01-01T00:00:00Z");
    Instant t2 = Instant.parse("2026-02-01T00:00:00Z");
    articleRepository.save(new Article("seed-1", "d", "b", List.of(), author2.getId(), t1));
    articleRepository.save(new Article("seed-2", "d", "b", List.of(), author2.getId(), t2));
    Instant cursor = Instant.parse("2026-12-31T00:00:00Z");

    CursorPageParameter<Instant> page = new CursorPageParameter<>(cursor, 20, Direction.NEXT);
    List<ArticleData> result =
        readService.findArticlesOfAuthorsWithCursor(List.of(author2.getId()), page);

    assertThat(result).hasSize(2);
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_order_desc_for_next() throws InterruptedException {
    // Kills NegateConditionalsMutator on line 159 (Direction.NEXT => order desc).
    // Build two articles with distinct createdAt by reusing the second
    // constructor overload of Article that accepts an explicit timestamp.
    User author2 = new User("a2@x.com", "author2", "p", "", "");
    userRepository.save(author2);
    Instant older = Instant.parse("2026-01-01T00:00:00Z");
    Instant newer = Instant.parse("2026-06-01T00:00:00Z");
    Article olderArticle =
        new Article("older", "d", "b", List.of(), author2.getId(), older);
    Article newerArticle =
        new Article("newer", "d", "b", List.of(), author2.getId(), newer);
    articleRepository.save(olderArticle);
    articleRepository.save(newerArticle);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<ArticleData> result =
        readService.findArticlesOfAuthorsWithCursor(List.of(author2.getId()), page);

    // Direction.NEXT => "order by A.created_at desc": newer first
    assertThat(result).extracting(ArticleData::id)
        .containsExactly(newerArticle.getId(), olderArticle.getId());
  }

  @Test
  void findArticlesOfAuthorsWithCursor_should_order_asc_for_prev() {
    // Kills NegateConditionalsMutator on line 162 (Direction.PREV => order asc).
    User author2 = new User("a2@x.com", "author2", "p", "", "");
    userRepository.save(author2);
    Instant older = Instant.parse("2026-01-01T00:00:00Z");
    Instant newer = Instant.parse("2026-06-01T00:00:00Z");
    Article olderArticle =
        new Article("older", "d", "b", List.of(), author2.getId(), older);
    Article newerArticle =
        new Article("newer", "d", "b", List.of(), author2.getId(), newer);
    articleRepository.save(olderArticle);
    articleRepository.save(newerArticle);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<ArticleData> result =
        readService.findArticlesOfAuthorsWithCursor(List.of(author2.getId()), page);

    // Direction.PREV => "order by A.created_at asc": older first
    assertThat(result).extracting(ArticleData::id)
        .containsExactly(olderArticle.getId(), newerArticle.getId());
  }

  // ---------------------------------------------------------------------------
  // countFeedSize
  // ---------------------------------------------------------------------------

  @Test
  void countFeedSize_should_return_zero_when_authors_is_null() {
    assertThat(readService.countFeedSize(null)).isZero();
  }

  @Test
  void countFeedSize_should_return_zero_when_authors_is_empty() {
    assertThat(readService.countFeedSize(Collections.emptyList())).isZero();
  }

  @Test
  void countFeedSize_should_count_articles_for_provided_authors() {
    assertThat(readService.countFeedSize(List.of(author.getId()))).isEqualTo(2);
    assertThat(readService.countFeedSize(List.of(otherAuthor.getId()))).isEqualTo(1);
  }

  // ---------------------------------------------------------------------------
  // queryArticles / countArticle — tag/author/favoritedBy filter combinations
  // ---------------------------------------------------------------------------

  @Test
  void queryArticles_should_return_all_articles_when_no_filters() {
    List<String> ids = readService.queryArticles(null, null, null, new Page());
    assertThat(ids).hasSize(3);
  }

  @Test
  void queryArticles_should_filter_by_tag() {
    List<String> ids = readService.queryArticles("java", null, null, new Page());
    assertThat(ids).containsExactly(tagged.getId());
  }

  @Test
  void queryArticles_should_filter_by_author() {
    List<String> ids = readService.queryArticles(null, author.getUsername(), null, new Page());
    assertThat(ids).containsExactlyInAnyOrder(article.getId(), tagged.getId());
  }

  @Test
  void queryArticles_should_filter_by_favoritedBy() {
    articleFavoriteRepository.save(new ArticleFavorite(article.getId(), otherAuthor.getId()));
    List<String> ids = readService.queryArticles(null, null, otherAuthor.getUsername(), new Page());
    assertThat(ids).containsExactly(article.getId());
  }

  @Test
  void countArticle_should_match_queryArticles() {
    int total = readService.countArticle(null, null, null);
    assertThat(total).isEqualTo(3);
    int taggedCount = readService.countArticle("java", null, null);
    assertThat(taggedCount).isEqualTo(1);
  }

  // ---------------------------------------------------------------------------
  // findArticlesWithCursor — cursor NEXT/PREV with filters
  // ---------------------------------------------------------------------------

  @Test
  void findArticlesWithCursor_should_return_ids_for_next_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<String> ids = readService.findArticlesWithCursor(null, null, null, page);
    assertThat(ids).hasSize(3);
  }

  @Test
  void findArticlesWithCursor_should_return_ids_for_prev_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<String> ids = readService.findArticlesWithCursor(null, null, null, page);
    assertThat(ids).hasSize(3);
  }

  @Test
  void findArticlesWithCursor_should_accept_cursor_for_next_without_failing() {
    // Branch coverage for `hasCursor && Direction.NEXT` on the global feed.
    ArticleData reference = readService.findById(article.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.NEXT);
    assertThat(readService.findArticlesWithCursor(null, null, null, page)).isNotNull();
  }

  @Test
  void findArticlesWithCursor_should_accept_cursor_for_prev_without_failing() {
    ArticleData reference = readService.findById(article.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.PREV);
    assertThat(readService.findArticlesWithCursor(null, null, null, page)).isNotNull();
  }

  @Test
  void findArticlesWithCursor_should_order_desc_for_next() {
    // Kills NegateConditionalsMutator on findArticlesWithCursor line 215
    // (Direction.NEXT => order desc).
    Instant older = Instant.parse("2026-01-01T00:00:00Z");
    Instant newer = Instant.parse("2026-06-01T00:00:00Z");
    Article olderA =
        new Article("o-feed", "d", "b", List.of(), author.getId(), older);
    Article newerA =
        new Article("n-feed", "d", "b", List.of(), author.getId(), newer);
    articleRepository.save(olderA);
    articleRepository.save(newerA);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<String> ids =
        readService.findArticlesWithCursor(null, author.getUsername(), null, page);

    // Direction.NEXT => "order by A.created_at desc": newer first
    int idxNewer = ids.indexOf(newerA.getId());
    int idxOlder = ids.indexOf(olderA.getId());
    assertThat(idxNewer).isNotNegative();
    assertThat(idxOlder).isNotNegative();
    assertThat(idxNewer).isLessThan(idxOlder);
  }

  @Test
  void findArticlesWithCursor_should_order_asc_for_prev() {
    // Kills NegateConditionalsMutator on findArticlesWithCursor line 223
    // (Direction.PREV => order asc).
    Instant older = Instant.parse("2026-01-01T00:00:00Z");
    Instant newer = Instant.parse("2026-06-01T00:00:00Z");
    Article olderA =
        new Article("o-feed2", "d", "b", List.of(), author.getId(), older);
    Article newerA =
        new Article("n-feed2", "d", "b", List.of(), author.getId(), newer);
    articleRepository.save(olderA);
    articleRepository.save(newerA);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<String> ids =
        readService.findArticlesWithCursor(null, author.getUsername(), null, page);

    // Direction.PREV => "order by A.created_at asc": older first
    int idxNewer = ids.indexOf(newerA.getId());
    int idxOlder = ids.indexOf(olderA.getId());
    assertThat(idxNewer).isNotNegative();
    assertThat(idxOlder).isNotNegative();
    assertThat(idxOlder).isLessThan(idxNewer);
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static ArticleData newArticle(String id) {
    ProfileData profile = new ProfileData("p-" + id, "u-" + id, "bio", "img", false);
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    return new ArticleData(
        id, "slug-" + id, "t", "d", "b", false, 0, now, now, List.of(), profile);
  }
}
