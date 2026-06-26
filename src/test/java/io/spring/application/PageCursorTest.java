package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PageCursor} targeting US-07.03. Kills the SURVIVED
 * NullReturnVal mutant on {@link PageCursor#toString()} (line 2) reported by
 * the US-07.02 baseline.
 */
class PageCursorTest {

  @Test
  void should_expose_underlying_data() {
    PageCursor<String> cursor = new PageCursor<>("payload") {};
    assertThat(cursor.getData()).isEqualTo("payload");
  }

  @Test
  void should_delegate_toString_to_underlying_data() {
    PageCursor<String> cursor = new PageCursor<>("payload") {};
    assertThat(cursor.toString()).isEqualTo("payload");
  }

  @Test
  void should_delegate_toString_to_underlying_integer() {
    PageCursor<Integer> cursor = new PageCursor<>(42) {};
    assertThat(cursor.toString()).isEqualTo("42");
  }
}
