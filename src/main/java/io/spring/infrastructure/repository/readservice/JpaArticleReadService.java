package io.spring.infrastructure.repository.readservice;

import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager.Direction;
import io.spring.application.Page;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.infrastructure.repository.readservice.ArticleReadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JpaArticleReadService implements ArticleReadService {

  private static final String SELECT_ARTICLE_DATA =
      "select A.id articleId, A.slug articleSlug, A.title articleTitle,"
          + " A.description articleDescription, A.body articleBody,"
          + " A.created_at articleCreatedAt, A.updated_at articleUpdatedAt,"
          + " T.name tagName,"
          + " U.id userId, U.username userUsername, U.bio userBio, U.image userImage"
          + " from articles A"
          + " left join article_tags AT on A.id = AT.article_id"
          + " left join tags T on T.id = AT.tag_id"
          + " left join users U on U.id = A.user_id";

  private static final String SELECT_ARTICLE_IDS =
      "select DISTINCT(A.id) articleId, A.created_at"
          + " from articles A"
          + " left join article_tags AT on A.id = AT.article_id"
          + " left join tags T on T.id = AT.tag_id"
          + " left join article_favorites AF on AF.article_id = A.id"
          + " left join users AU on AU.id = A.user_id"
          + " left join users AFU on AFU.id = AF.user_id";

  @PersistenceContext private EntityManager entityManager;

  @Override
  @SuppressWarnings("unchecked")
  public ArticleData findById(String id) {
    List<Object[]> rows =
        entityManager
            .createNativeQuery(SELECT_ARTICLE_DATA + " where A.id = :id")
            .setParameter("id", id)
            .getResultList();
    List<ArticleData> articles = mapArticleRows(rows);
    return articles.isEmpty() ? null : articles.get(0);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ArticleData findBySlug(String slug) {
    List<Object[]> rows =
        entityManager
            .createNativeQuery(SELECT_ARTICLE_DATA + " where A.slug = :slug")
            .setParameter("slug", slug)
            .getResultList();
    List<ArticleData> articles = mapArticleRows(rows);
    return articles.isEmpty() ? null : articles.get(0);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> queryArticles(String tag, String author, String favoritedBy, Page page) {
    StringBuilder sql = new StringBuilder(SELECT_ARTICLE_IDS);
    appendFilters(sql, tag, author, favoritedBy);
    sql.append(" order by A.created_at desc limit :limit offset :offset");

    Query query = entityManager.createNativeQuery(sql.toString());
    bindFilters(query, tag, author, favoritedBy);
    query.setParameter("limit", page.getLimit());
    query.setParameter("offset", page.getOffset());

    List<Object[]> rows = query.getResultList();
    List<String> ids = new ArrayList<>();
    for (Object[] row : rows) {
      ids.add((String) row[0]);
    }
    return ids;
  }

  @Override
  public int countArticle(String tag, String author, String favoritedBy) {
    StringBuilder sql =
        new StringBuilder(
            "select count(DISTINCT A.id) from articles A"
                + " left join article_tags AT on A.id = AT.article_id"
                + " left join tags T on T.id = AT.tag_id"
                + " left join article_favorites AF on AF.article_id = A.id"
                + " left join users AU on AU.id = A.user_id"
                + " left join users AFU on AFU.id = AF.user_id");
    appendFilters(sql, tag, author, favoritedBy);

    Query query = entityManager.createNativeQuery(sql.toString());
    bindFilters(query, tag, author, favoritedBy);
    return ((Number) query.getSingleResult()).intValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ArticleData> findArticles(List<String> articleIds) {
    if (articleIds == null || articleIds.isEmpty()) {
      return new ArrayList<>();
    }
    List<Object[]> rows =
        entityManager
            .createNativeQuery(
                SELECT_ARTICLE_DATA + " where A.id in :ids order by A.created_at desc")
            .setParameter("ids", articleIds)
            .getResultList();
    return mapArticleRows(rows);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ArticleData> findArticlesOfAuthors(List<String> authors, Page page) {
    if (authors == null || authors.isEmpty()) {
      return new ArrayList<>();
    }
    List<Object[]> rows =
        entityManager
            .createNativeQuery(
                SELECT_ARTICLE_DATA + " where A.user_id in :authors limit :limit offset :offset")
            .setParameter("authors", authors)
            .setParameter("limit", page.getLimit())
            .setParameter("offset", page.getOffset())
            .getResultList();
    return mapArticleRows(rows);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ArticleData> findArticlesOfAuthorsWithCursor(
      List<String> authors, CursorPageParameter<?> page) {
    if (authors == null || authors.isEmpty()) {
      return new ArrayList<>();
    }
    StringBuilder sql = new StringBuilder(SELECT_ARTICLE_DATA);
    sql.append(" where A.user_id in :authors");
    boolean hasCursor = page.getCursor() != null;
    if (hasCursor && page.getDirection() == Direction.NEXT) {
      sql.append(" and A.created_at < :cursor");
    } else if (hasCursor && page.getDirection() == Direction.PREV) {
      sql.append(" and A.created_at > :cursor");
    }
    if (page.getDirection() == Direction.NEXT) {
      sql.append(" order by A.created_at desc");
    } else {
      sql.append(" order by A.created_at asc");
    }
    sql.append(" limit :limit");

    Query query =
        entityManager.createNativeQuery(sql.toString()).setParameter("authors", authors);
    if (hasCursor) {
      query.setParameter("cursor", toTimestamp(page.getCursor()));
    }
    query.setParameter("limit", page.getQueryLimit());
    return mapArticleRows(query.getResultList());
  }

  @Override
  public int countFeedSize(List<String> authors) {
    if (authors == null || authors.isEmpty()) {
      return 0;
    }
    Number count =
        (Number)
            entityManager
                .createNativeQuery("select count(1) from articles A where A.user_id in :authors")
                .setParameter("authors", authors)
                .getSingleResult();
    return count.intValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> findArticlesWithCursor(
      String tag, String author, String favoritedBy, CursorPageParameter<?> page) {
    StringBuilder sql = new StringBuilder(SELECT_ARTICLE_IDS);
    List<String> conditions = new ArrayList<>();
    if (tag != null) {
      conditions.add("T.name = :tag");
    }
    if (author != null) {
      conditions.add("AU.username = :author");
    }
    if (favoritedBy != null) {
      conditions.add("AFU.username = :favoritedBy");
    }
    boolean hasCursor = page.getCursor() != null;
    if (hasCursor && page.getDirection() == Direction.NEXT) {
      conditions.add("A.created_at < :cursor");
    } else if (hasCursor && page.getDirection() == Direction.PREV) {
      conditions.add("A.created_at > :cursor");
    }
    if (!conditions.isEmpty()) {
      sql.append(" where ").append(String.join(" AND ", conditions));
    }
    if (page.getDirection() == Direction.NEXT) {
      sql.append(" order by A.created_at desc");
    } else {
      sql.append(" order by A.created_at asc");
    }
    sql.append(" limit :limit");

    Query query = entityManager.createNativeQuery(sql.toString());
    bindFilters(query, tag, author, favoritedBy);
    if (hasCursor) {
      query.setParameter("cursor", toTimestamp(page.getCursor()));
    }
    query.setParameter("limit", page.getQueryLimit());

    List<Object[]> rows = query.getResultList();
    List<String> ids = new ArrayList<>();
    for (Object[] row : rows) {
      ids.add((String) row[0]);
    }
    return ids;
  }

  private void appendFilters(StringBuilder sql, String tag, String author, String favoritedBy) {
    List<String> conditions = new ArrayList<>();
    if (tag != null) {
      conditions.add("T.name = :tag");
    }
    if (author != null) {
      conditions.add("AU.username = :author");
    }
    if (favoritedBy != null) {
      conditions.add("AFU.username = :favoritedBy");
    }
    if (!conditions.isEmpty()) {
      sql.append(" where ").append(String.join(" AND ", conditions));
    }
  }

  private void bindFilters(Query query, String tag, String author, String favoritedBy) {
    if (tag != null) {
      query.setParameter("tag", tag);
    }
    if (author != null) {
      query.setParameter("author", author);
    }
    if (favoritedBy != null) {
      query.setParameter("favoritedBy", favoritedBy);
    }
  }

  private Timestamp toTimestamp(Object cursor) {
    if (cursor instanceof Instant instant) {
      return Timestamp.from(instant);
    }
    return Timestamp.from(Instant.parse(cursor.toString()));
  }

  private static Instant toInstant(Object value) {
    if (value instanceof LocalDateTime localDateTime) {
      return localDateTime.toInstant(ZoneOffset.UTC);
    }
    return ((Timestamp) value).toInstant();
  }

  /**
   * Collapses the joined rows (one per article-tag pair) into distinct ArticleData, preserving
   * the row order and accumulating the tag list. Mirrors the legacy mapper collection shape.
   */
  private List<ArticleData> mapArticleRows(List<Object[]> rows) {
    Map<String, ArticleData> byId = new LinkedHashMap<>();
    for (Object[] row : rows) {
      String articleId = (String) row[0];
      ArticleData articleData =
          byId.computeIfAbsent(
              articleId,
              id -> {
                ProfileData profileData =
                    new ProfileData(
                        (String) row[8],
                        (String) row[9],
                        (String) row[10],
                        (String) row[11],
                        false);
                ArticleData data = new ArticleData();
                data.setId(id);
                data.setSlug((String) row[1]);
                data.setTitle((String) row[2]);
                data.setDescription((String) row[3]);
                data.setBody((String) row[4]);
                data.setCreatedAt(toInstant(row[5]));
                data.setUpdatedAt(toInstant(row[6]));
                data.setTagList(new ArrayList<>());
                data.setProfileData(profileData);
                return data;
              });
      String tagName = (String) row[7];
      if (tagName != null && !articleData.getTagList().contains(tagName)) {
        articleData.getTagList().add(tagName);
      }
    }
    return new ArrayList<>(byId.values());
  }
}
