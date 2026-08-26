package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/** Every time you interact with someone, they may remember what you do not. */
@Entity
public class Description extends Persistable {
  @Column private String text;

  public Description(String text) {
    this.text = text;
  }

  @SuppressWarnings("unused")
  private Description() {}

  public String getText() {
    return text;
  }
}
