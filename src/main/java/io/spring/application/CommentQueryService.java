package io.spring.application;

import io.spring.application.data.CommentData;
import io.spring.core.user.User;
import io.spring.infrastructure.repository.readservice.CommentReadService;
import io.spring.infrastructure.repository.readservice.UserRelationshipQueryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentQueryService {
  private CommentReadService commentReadService;
  private UserRelationshipQueryService userRelationshipQueryService;

  public Optional<CommentData> findById(String id, User user) {
    CommentData commentData = commentReadService.findById(id);
    if (commentData == null) {
      return Optional.empty();
    }
    boolean following =
        userRelationshipQueryService.isUserFollowing(
            user.getId(), commentData.profileData().id());
    return Optional.of(
        commentData.withProfileData(commentData.profileData().withFollowing(following)));
  }

  public List<CommentData> findByArticleId(String articleId, User user) {
    List<CommentData> comments = commentReadService.findByArticleId(articleId);
    if (comments.size() > 0 && user != null) {
      applyFollowingFlag(comments, user);
    }
    return comments;
  }

  public CursorPager<CommentData> findByArticleIdWithCursor(
      String articleId, User user, CursorPageParameter<Instant> page) {
    List<CommentData> comments = commentReadService.findByArticleIdWithCursor(articleId, page);
    if (comments.isEmpty()) {
      return new CursorPager<>(new ArrayList<>(), page.getDirection(), false);
    }
    if (user != null) {
      applyFollowingFlag(comments, user);
    }
    boolean hasExtra = comments.size() > page.getLimit();
    if (hasExtra) {
      comments.remove(page.getLimit());
    }
    if (!page.isNext()) {
      Collections.reverse(comments);
    }
    return new CursorPager<>(comments, page.getDirection(), hasExtra);
  }

  /**
   * Flips {@code following=true} on each comment's embedded profile whose author the current
   * user follows. Comments are replaced in place with enriched copies because {@code CommentData}
   * is now an immutable record (US-06.01).
   */
  private void applyFollowingFlag(List<CommentData> comments, User user) {
    Set<String> followingAuthors =
        userRelationshipQueryService.followingAuthors(
            user.getId(),
            comments.stream()
                .map(commentData -> commentData.profileData().id())
                .collect(Collectors.toList()));
    comments.replaceAll(
        commentData ->
            followingAuthors.contains(commentData.profileData().id())
                ? commentData.withProfileData(commentData.profileData().withFollowing(true))
                : commentData);
  }
}
