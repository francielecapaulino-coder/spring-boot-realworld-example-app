package io.spring.core.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "name")
public class Tag {
  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  public Tag(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
  }
}
