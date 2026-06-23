package io.spring.infrastructure.repository.readservice;

import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.infrastructure.repository.readservice.CommentReadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JpaCommentReadService implements CommentReadService {

  private static final String SELECT_COMMENT_DATA =
      "SELECT C.id commentId, C.body commentBody, C.created_at commentCreatedAt,"
          + " C.article_id commentArticleId,"
          + " U.id userId, U.username userUsername, U.bio userBio, U.image userImage"
          + " from comments C left join users U on C.user_id = U.id";

  @PersistenceContext private EntityManager entityManager;

  @Override
  @SuppressWarnings("unchecked")
  public CommentData findById(String id) {
    List<Object[]> rows =
        entityManager
            .createNativeQuery(SELECT_COMMENT_DATA + " where C.id = :id")
            .setParameter("id", id)
            .getResultList();
    if (rows.isEmpty()) {
      return null;
    }
    return toCommentData(rows.get(0));
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<CommentData> findByArticleId(String articleId) {
    List<Object[]> rows =
        entityManager
            .createNativeQuery(SELECT_COMMENT_DATA + " where C.article_id = :articleId")
            .setParameter("articleId", articleId)
            .getResultList();
    return rows.stream().map(this::toCommentData).collect(Collectors.toList());
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<CommentData> findByArticleIdWithCursor(
      String articleId, CursorPageParameter<Instant> page) {
    StringBuilder sql = new StringBuilder(SELECT_COMMENT_DATA);
    sql.append(" where C.article_id = :articleId");
    boolean hasCursor = page.getCursor() != null;
    if (hasCursor && page.getDirection() == Direction.NEXT) {
      sql.append(" and C.created_at < :cursor");
    } else if (hasCursor && page.getDirection() == Direction.PREV) {
      sql.append(" and C.created_at > :cursor");
    }
    if (page.getDirection() == Direction.NEXT) {
      sql.append(" order by C.created_at desc");
    } else {
      sql.append(" order by C.created_at asc");
    }

    Query query =
        entityManager.createNativeQuery(sql.toString()).setParameter("articleId", articleId);
    if (hasCursor) {
      query.setParameter("cursor", Timestamp.from(page.getCursor()));
    }
    List<Object[]> rows = query.getResultList();
    return rows.stream().map(this::toCommentData).collect(Collectors.toList());
  }

  private CommentData toCommentData(Object[] row) {
    Instant createdAt = ((Timestamp) row[2]).toInstant();
    ProfileData profileData =
        new ProfileData(
            (String) row[4], (String) row[5], (String) row[6], (String) row[7], false);
    return new CommentData(
        (String) row[0],
        (String) row[1],
        (String) row[3],
        createdAt,
        createdAt,
        profileData);
  }
}
