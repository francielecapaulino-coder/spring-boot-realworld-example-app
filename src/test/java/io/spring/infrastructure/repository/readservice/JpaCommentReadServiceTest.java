package io.spring.infrastructure.repository.readservice;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.JpaArticleRepository;
import io.spring.infrastructure.repository.JpaCommentRepository;
import io.spring.infrastructure.repository.JpaUserRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Repository-slice tests for {@link JpaCommentReadService} targeting US-07.04.
 *
 * <p>Exercises {@code findById} (success + null), {@code findByArticleId} and every cursor
 * branch of {@code findByArticleIdWithCursor} (NEXT/PREV with/without cursor value). Kills
 * the 10 SURVIVED/NO_COVERAGE mutants reported on this class by the US-07.03 baseline.
 */
@Import({
  JpaCommentReadService.class,
  JpaArticleRepository.class,
  JpaUserRepository.class,
  JpaCommentRepository.class,
})
class JpaCommentReadServiceTest extends DbTestBase {

  @Autowired private JpaCommentReadService readService;
  @Autowired private ArticleRepository articleRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CommentRepository commentRepository;

  private User user;
  private Article article;
  private Comment firstComment;
  private Comment secondComment;

  @BeforeEach
  void setUp() {
    user = new User("u@x.com", "alice", "p", "bio", "img");
    userRepository.save(user);
    article = new Article("title", "desc", "body", List.of(), user.getId());
    articleRepository.save(article);
    firstComment = new Comment("first body", user.getId(), article.getId());
    secondComment = new Comment("second body", user.getId(), article.getId());
    commentRepository.save(firstComment);
    commentRepository.save(secondComment);
  }

  // ---------------------------------------------------------------------------
  // findById
  // ---------------------------------------------------------------------------

  @Test
  void findById_should_return_data_with_profile_for_existing_comment() {
    CommentData data = readService.findById(firstComment.getId());
    assertThat(data).isNotNull();
    assertThat(data.id()).isEqualTo(firstComment.getId());
    assertThat(data.body()).isEqualTo("first body");
    assertThat(data.profileData().username()).isEqualTo("alice");
    // Asserting the timestamp is non-null kills NullReturnVals on toInstant
    // (line 84): if the helper returned null, createdAt would be null too.
    assertThat(data.createdAt()).isNotNull();
    assertThat(data.updatedAt()).isNotNull();
  }

  @Test
  void findById_should_return_null_for_missing_comment() {
    assertThat(readService.findById("missing-id")).isNull();
  }

  // ---------------------------------------------------------------------------
  // findByArticleId
  // ---------------------------------------------------------------------------

  @Test
  void findByArticleId_should_return_all_comments_for_article() {
    List<CommentData> result = readService.findByArticleId(article.getId());
    assertThat(result).extracting(CommentData::id)
        .containsExactlyInAnyOrder(firstComment.getId(), secondComment.getId());
  }

  @Test
  void findByArticleId_should_return_empty_list_for_unknown_article() {
    assertThat(readService.findByArticleId("missing-article")).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // findByArticleIdWithCursor — cursor NEXT/PREV with/without cursor value
  // ---------------------------------------------------------------------------

  @Test
  void findByArticleIdWithCursor_should_return_all_for_next_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<CommentData> result = readService.findByArticleIdWithCursor(article.getId(), page);
    assertThat(result).hasSize(2);
  }

  @Test
  void findByArticleIdWithCursor_should_return_all_for_prev_without_cursor() {
    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<CommentData> result = readService.findByArticleIdWithCursor(article.getId(), page);
    assertThat(result).hasSize(2);
  }

  @Test
  void findByArticleIdWithCursor_should_accept_cursor_for_next_without_failing() {
    // Branch coverage for `hasCursor && Direction.NEXT`. The cursor uses the
    // comment's own createdAt to dodge JVM/Postgres timezone differences.
    CommentData reference = readService.findById(firstComment.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.NEXT);
    assertThat(readService.findByArticleIdWithCursor(article.getId(), page)).isNotNull();
  }

  @Test
  void findByArticleIdWithCursor_should_accept_cursor_for_prev_without_failing() {
    CommentData reference = readService.findById(firstComment.getId());
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(reference.createdAt(), 20, Direction.PREV);
    assertThat(readService.findByArticleIdWithCursor(article.getId(), page)).isNotNull();
  }

  @Test
  void findByArticleIdWithCursor_should_order_desc_for_next() throws InterruptedException {
    // Kills NegateConditionalsMutator on line 67 (Direction.NEXT => order desc).
    // The two comments saved in setUp have effectively the same timestamp, so
    // we add a third comment with a small delay to make ordering observable.
    Thread.sleep(20);
    Comment third = new Comment("third body", user.getId(), article.getId());
    commentRepository.save(third);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    List<CommentData> result = readService.findByArticleIdWithCursor(article.getId(), page);

    // Direction.NEXT => "order by C.created_at desc": newer (third) first
    assertThat(result.get(0).id()).isEqualTo(third.getId());
  }

  @Test
  void findByArticleIdWithCursor_should_order_asc_for_prev() throws InterruptedException {
    // Kills NegateConditionalsMutator on line 67's else-branch (PREV => asc).
    Thread.sleep(20);
    Comment third = new Comment("third body", user.getId(), article.getId());
    commentRepository.save(third);

    CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, Direction.PREV);
    List<CommentData> result = readService.findByArticleIdWithCursor(article.getId(), page);

    // Direction.PREV => "order by C.created_at asc": newer (third) last
    assertThat(result.get(result.size() - 1).id()).isEqualTo(third.getId());
  }
}
