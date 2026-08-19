package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

/** Experience are how you have responded to prompts. */
@Entity
public class Experience extends Persistable {

  @ManyToOne private Prompt prompt;
  @Column private String response;

  public Experience(Prompt prompt, String response) {
    this.prompt = prompt;
    this.response = response;
  }

  @SuppressWarnings("unused")
  private Experience() {}

  public Prompt getPrompt() {
    return prompt;
  }

  public String getResponse() {
    return response;
  }
}
