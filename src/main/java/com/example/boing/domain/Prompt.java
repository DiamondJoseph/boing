package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Prompt extends Persistable {
  @Column private String text;

  public Prompt(String text) {
    this.text = text;
  }

  @SuppressWarnings("unused")
  private Prompt() {}

  public String getText() {
    return text;
  }
}
