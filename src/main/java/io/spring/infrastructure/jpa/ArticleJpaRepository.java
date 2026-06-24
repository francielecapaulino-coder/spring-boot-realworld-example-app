package io.spring.infrastructure.jpa;

import io.spring.core.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository for {@link Article} entities.
 *
 * <p>Both finders use an explicit JPQL {@code LEFT JOIN FETCH a.tags} so the {@code tags}
 * association is eagerly loaded and callers can read {@link Article#getTags()} after the
 * session closes. {@code DISTINCT} collapses the cartesian rows produced by the join,
 * preventing {@code NonUniqueResultException} when several articles share a tag — the
 * scenario that broke {@code @EntityGraph(attributePaths = "tags")} on Hibernate 7.2
 * (see #90).
 */
public interface ArticleJpaRepository extends JpaRepository<Article, String> {

  @Query("select distinct a from Article a left join fetch a.tags where a.id = :id")
  Optional<Article> findById(@Param("id") String id);

  @Query("select distinct a from Article a left join fetch a.tags where a.slug = :slug")
  Optional<Article> findBySlug(@Param("slug") String slug);
}
