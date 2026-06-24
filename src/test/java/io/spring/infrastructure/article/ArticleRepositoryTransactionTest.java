package io.spring.infrastructure.article;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.jpa.TagJpaRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ArticleRepositoryTransactionTest {
  @Autowired private ArticleRepository articleRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TagJpaRepository tagJpaRepository;

  /**
   * Pins the contract that emerged from the #90 fix: tag reconciliation happens in
   * its own REQUIRES_NEW transaction so concurrent writers cannot poison the article
   * save with a UNIQUE-violation rollback. As a consequence, a tag created during
   * the failing save of a second article is intentionally persisted and reusable —
   * the previous behavior (tag rolled back together with the article) is dropped.
   *
   * <p>The test exercises two scenarios in a single fixture:
   * <ol>
   *   <li>A first article with tags ["java", "spring"] is saved successfully.
   *   <li>A second article with the SAME slug ("test" -> "test") is rejected by
   *       the UNIQUE(slug) constraint on `articles`. Its new tag "other" must be
   *       reachable via {@link TagJpaRepository#findByName(String)} after the
   *       article save fails, since the tag transaction has already committed.
   * </ol>
   */
  @Test
  public void transactional_test() {
    User user = new User("aisensiy@gmail.com", "aisensiy", "123", "bio", "default");
    userRepository.save(user);
    Article article =
        new Article("test", "desc", "body", Arrays.asList("java", "spring"), user.getId());
    articleRepository.save(article);

    Article anotherArticle =
        new Article(
            "test", "desc", "body", Arrays.asList("java", "spring", "other"), user.getId());
    boolean saveFailed = false;
    try {
      articleRepository.save(anotherArticle);
    } catch (Exception e) {
      saveFailed = true;
    }
    Assertions.assertTrue(
        saveFailed, "second save with duplicated slug must fail");
    Assertions.assertTrue(
        tagJpaRepository.findByName("other").isPresent(),
        "tag created via the reconciler must survive the article rollback (#90)");
  }
}
