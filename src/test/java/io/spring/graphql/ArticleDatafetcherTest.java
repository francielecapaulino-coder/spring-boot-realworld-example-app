package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class ArticleDatafetcherTest {

  private final ArticleQueryService articleQueryService = Mockito.mock(ArticleQueryService.class);
  private final UserRepository userRepository = Mockito.mock(UserRepository.class);
  private final ArticleDatafetcher datafetcher =
      new ArticleDatafetcher(articleQueryService, userRepository);

  @Test
  void findArticleBySlug_should_map_article_and_local_context() {
    ArticleData article = article("article-1", "article-one", "Title one", timestamp(1));
    given(articleQueryService.findBySlug("article-one", null)).willReturn(Optional.of(article));

    DataFetcherResult<Article> result = datafetcher.findArticleBySlug("article-one");

    assertThat(result.getData().getSlug()).isEqualTo("article-one");
    assertThat(result.getData().getTitle()).isEqualTo("Title one");
    assertThat(result.getData().getTagList()).containsExactly("java", "graphql");
    assertThat(result.getData().getFavorited()).isTrue();
    assertThat(result.getData().getFavoritesCount()).isEqualTo(3);
    assertThat(result.getData().getCreatedAt()).isEqualTo("2024-01-01T00:00:01.000+00:00");
    assertThat(result.getData().getUpdatedAt()).isEqualTo("2024-01-01T00:00:01.000+00:00");
    assertThat(localContext(result)).containsEntry("article-one", article);
    verify(articleQueryService).findBySlug("article-one", null);
  }

  @Test
  void getArticles_with_first_should_query_next_page_and_build_connection() {
    ArticleData first = article("article-1", "article-one", "Title one", timestamp(1));
    ArticleData second = article("article-2", "article-two", "Title two", timestamp(2));
    given(articleQueryService.findRecentArticlesWithCursor(
            eq("java"), eq("alice"), eq("bob"), Mockito.any(), isNull()))
        .willReturn(new CursorPager<>(List.of(first, second), Direction.NEXT, true));

    DataFetcherResult<ArticlesConnection> result =
        datafetcher.getArticles(2, "1704067200000", null, null, "alice", "bob", "java", dfe());

    ArticlesConnection connection = result.getData();
    assertThat(connection.getEdges()).hasSize(2);
    assertThat(connection.getEdges().get(0).getCursor()).isEqualTo(first.getCursor().toString());
    assertThat(connection.getEdges().get(0).getNode().getSlug()).isEqualTo("article-one");
    assertThat(connection.getEdges().get(1).getNode().getSlug()).isEqualTo("article-two");
    assertThat(connection.getPageInfo().isHasPreviousPage()).isFalse();
    assertThat(connection.getPageInfo().isHasNextPage()).isTrue();
    assertThat(localContext(result))
        .containsEntry("article-one", first)
        .containsEntry("article-two", second);

    ArgumentCaptor<CursorPageParameter<Instant>> pageCaptor = pageCaptor();
    verify(articleQueryService)
        .findRecentArticlesWithCursor(
            eq("java"), eq("alice"), eq("bob"), pageCaptor.capture(), isNull());
    assertThat(pageCaptor.getValue().getDirection()).isEqualTo(Direction.NEXT);
    assertThat(pageCaptor.getValue().getLimit()).isEqualTo(2);
    assertThat(pageCaptor.getValue().getCursor()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
  }

  @Test
  void getArticles_with_last_should_query_previous_page_and_build_connection() {
    ArticleData article = article("article-3", "article-three", "Title three", timestamp(3));
    given(articleQueryService.findRecentArticlesWithCursor(
            isNull(), isNull(), isNull(), Mockito.any(), isNull()))
        .willReturn(new CursorPager<>(List.of(article), Direction.PREV, true));

    DataFetcherResult<ArticlesConnection> result =
        datafetcher.getArticles(null, null, 1, "1704067203000", null, null, null, dfe());

    ArticlesConnection connection = result.getData();
    assertThat(connection.getEdges()).hasSize(1);
    assertThat(connection.getEdges().get(0).getCursor()).isEqualTo(article.getCursor().toString());
    assertThat(connection.getEdges().get(0).getNode().getSlug()).isEqualTo("article-three");
    assertThat(connection.getPageInfo().isHasPreviousPage()).isTrue();
    assertThat(connection.getPageInfo().isHasNextPage()).isFalse();

    ArgumentCaptor<CursorPageParameter<Instant>> pageCaptor = pageCaptor();
    verify(articleQueryService)
        .findRecentArticlesWithCursor(
            isNull(), isNull(), isNull(), pageCaptor.capture(), isNull());
    assertThat(pageCaptor.getValue().getDirection()).isEqualTo(Direction.PREV);
    assertThat(pageCaptor.getValue().getLimit()).isEqualTo(1);
    assertThat(pageCaptor.getValue().getCursor()).isEqualTo(Instant.parse("2024-01-01T00:00:03Z"));
  }

  private static DgsDataFetchingEnvironment dfe() {
    return Mockito.mock(DgsDataFetchingEnvironment.class);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, ArticleData> localContext(DataFetcherResult<?> result) {
    return (Map<String, ArticleData>) result.getLocalContext();
  }

  @SuppressWarnings("unchecked")
  private static ArgumentCaptor<CursorPageParameter<Instant>> pageCaptor() {
    return ArgumentCaptor.forClass(CursorPageParameter.class);
  }

  private static ArticleData article(String id, String slug, String title, Instant timestamp) {
    return new ArticleData(
        id,
        slug,
        title,
        "description",
        "body",
        true,
        3,
        timestamp,
        timestamp,
        List.of("java", "graphql"),
        new ProfileData("author-id", "alice", "bio", "image", false));
  }

  private static Instant timestamp(int second) {
    return Instant.parse("2024-01-01T00:00:%02dZ".formatted(second));
  }
}
