package io.spring.infrastructure.repository;

import io.spring.core.article.Tag;
import io.spring.infrastructure.jpa.TagJpaRepository;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Returns a managed {@link Tag} for a given name, inserting it on demand.
 *
 * <p>Lives in its own bean so each call runs in a dedicated transaction
 * ({@link Propagation#REQUIRES_NEW}). When two callers race to insert the same
 * tag name the loser hits the {@code UNIQUE(name)} constraint added in V3 and
 * gets a {@link DataIntegrityViolationException}; thanks to REQUIRES_NEW the
 * outer transaction (the one persisting the Article) is not poisoned and we
 * can simply re-read the winning row.
 */
@Component
public class TagReconciler {

  private final TagJpaRepository tagJpaRepository;

  public TagReconciler(TagJpaRepository tagJpaRepository) {
    this.tagJpaRepository = tagJpaRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Tag reconcile(Tag tag) {
    Optional<Tag> existing = tagJpaRepository.findByName(tag.getName());
    if (existing.isPresent()) {
      return existing.get();
    }
    try {
      return tagJpaRepository.saveAndFlush(tag);
    } catch (DataIntegrityViolationException race) {
      // Another concurrent transaction won the UNIQUE(name) race; load and
      // return the winner.
      return tagJpaRepository
          .findByName(tag.getName())
          .orElseThrow(() -> race);
    }
  }
}
