package io.spring.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.spring.core.article.Tag;
import io.spring.infrastructure.jpa.TagJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Pure unit tests for {@link TagReconciler} targeting US-07.04.
 *
 * <p>Kills the three mutants reported on this class by the US-07.04 baseline:
 * the {@code NullReturnVals} on the existing-tag return (line 34), the
 * {@code NullReturnVals} on the saveAndFlush return (line 41), and the
 * {@code NullReturnVals} on the race-loser lambda (line 43).
 */
class TagReconcilerTest {

  private TagJpaRepository tagJpaRepository;
  private TagReconciler reconciler;

  @BeforeEach
  void setUp() {
    tagJpaRepository = Mockito.mock(TagJpaRepository.class);
    reconciler = new TagReconciler(tagJpaRepository);
  }

  @Test
  void reconcile_should_return_existing_tag_when_already_present() {
    // Kills NullReturnVals on line 34: if reconcile returned null instead of
    // existing.get(), the equality assertion below would fail.
    Tag existing = new Tag("java");
    given(tagJpaRepository.findByName("java")).willReturn(Optional.of(existing));

    Tag result = reconciler.reconcile(new Tag("java"));

    assertThat(result).isSameAs(existing);
    verify(tagJpaRepository, never()).saveAndFlush(any());
  }

  @Test
  void reconcile_should_save_and_return_new_tag_when_not_present() {
    // Kills NullReturnVals on line 41 (saveAndFlush return): if reconcile
    // returned null instead of the saved tag, the assertion below would fail.
    Tag fresh = new Tag("spring");
    Tag persisted = new Tag("spring");
    given(tagJpaRepository.findByName("spring")).willReturn(Optional.empty());
    given(tagJpaRepository.saveAndFlush(fresh)).willReturn(persisted);

    Tag result = reconciler.reconcile(fresh);

    assertThat(result).isSameAs(persisted);
  }

  @Test
  void reconcile_should_reload_winner_when_unique_constraint_race_losses() {
    // Kills the NullReturnVals around the catch block (lines 41-43): the
    // race-winning row must be returned, not null.
    Tag fresh = new Tag("kotlin");
    Tag winner = new Tag("kotlin");
    given(tagJpaRepository.findByName("kotlin"))
        .willReturn(Optional.empty())
        .willReturn(Optional.of(winner));
    given(tagJpaRepository.saveAndFlush(fresh))
        .willThrow(new DataIntegrityViolationException("UNIQUE(name) race"));

    Tag result = reconciler.reconcile(fresh);

    assertThat(result).isSameAs(winner);
  }

  @Test
  void reconcile_should_rethrow_when_race_loser_cannot_find_winner_either() {
    // Kills the orElseThrow lambda mutation (line 43): if the lambda returned
    // null, orElseThrow would throw NullPointerException instead of the
    // original DataIntegrityViolationException, breaking the contract that
    // the caller observes the original race failure.
    Tag fresh = new Tag("rust");
    DataIntegrityViolationException race =
        new DataIntegrityViolationException("UNIQUE(name) race");
    given(tagJpaRepository.findByName("rust")).willReturn(Optional.empty());
    given(tagJpaRepository.saveAndFlush(fresh)).willThrow(race);

    assertThatThrownBy(() -> reconciler.reconcile(fresh)).isSameAs(race);
  }
}
