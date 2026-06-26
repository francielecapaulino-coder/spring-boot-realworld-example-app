package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import io.spring.application.CursorPager.Direction;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CursorPager} targeting US-07.03. Kills the
 * SURVIVED/NO_COVERAGE mutants reported by the US-07.02 baseline on
 * the constructor branches (direction NEXT vs PREV) and on the
 * {@link CursorPager#getStartCursor()} / {@link CursorPager#getEndCursor()}
 * empty-vs-populated branches.
 */
class CursorPagerTest {

  @Test
  void should_mark_hasNext_when_direction_is_next_and_has_extra() {
    CursorPager<Node> pager = new CursorPager<>(List.of(), Direction.NEXT, true);
    assertThat(pager.hasNext()).isTrue();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  void should_not_mark_hasNext_when_direction_is_next_and_no_extra() {
    CursorPager<Node> pager = new CursorPager<>(List.of(), Direction.NEXT, false);
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  void should_mark_hasPrevious_when_direction_is_prev_and_has_extra() {
    CursorPager<Node> pager = new CursorPager<>(List.of(), Direction.PREV, true);
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isTrue();
  }

  @Test
  void should_not_mark_hasPrevious_when_direction_is_prev_and_no_extra() {
    CursorPager<Node> pager = new CursorPager<>(List.of(), Direction.PREV, false);
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  void should_return_null_cursors_when_data_is_empty() {
    CursorPager<Node> pager = new CursorPager<>(List.of(), Direction.NEXT, false);
    assertThat(pager.getStartCursor()).isNull();
    assertThat(pager.getEndCursor()).isNull();
  }

  @Test
  void should_return_first_and_last_cursors_when_data_is_populated() {
    PageCursor<?> firstCursor = stringCursor("first");
    PageCursor<?> lastCursor = stringCursor("last");
    Node firstNode = mock(Node.class);
    Node lastNode = mock(Node.class);
    doReturn(firstCursor).when(firstNode).getCursor();
    doReturn(lastCursor).when(lastNode).getCursor();

    CursorPager<Node> pager = new CursorPager<>(List.of(firstNode, lastNode), Direction.NEXT, true);

    assertThat(pager.getStartCursor()).isSameAs(firstCursor);
    assertThat(pager.getEndCursor()).isSameAs(lastCursor);
  }

  @Test
  void should_expose_data_through_getter() {
    Node node = mock(Node.class);
    CursorPager<Node> pager = new CursorPager<>(List.of(node), Direction.NEXT, false);
    assertThat(pager.getData()).containsExactly(node);
  }

  private static PageCursor<String> stringCursor(String value) {
    return new PageCursor<>(value) {};
  }
}
