package io.spring.core.article;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TagTest {

  @Test
  void should_assign_uuid_id_and_keep_name() {
    Tag tag = new Tag("java");
    assertThat(tag.getId()).isNotBlank();
    assertThat(tag.getName()).isEqualTo("java");
  }

  @Test
  void should_be_equal_when_names_match() {
    Tag a = new Tag("java");
    Tag b = new Tag("java");
    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void should_not_be_equal_when_names_differ() {
    Tag a = new Tag("java");
    Tag b = new Tag("python");
    assertThat(a).isNotEqualTo(b);
  }
}
