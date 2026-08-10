package com.example.boing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/** Character are the people with whom your vampire has a relationship. */
@Entity
public class Person {

  @Id @GeneratedValue private Long id;
  private String name;
  private int initialPrompt;
  private List<String> descriptions = new ArrayList<>();
  private boolean immortal;
  private boolean dead = false;

  public Person(String name, int initialPrompt, String initialDescription, boolean immortal) {
    this.name = name;
    this.initialPrompt = initialPrompt;
    this.descriptions.add(initialDescription);
    this.immortal = immortal;
  }

  @SuppressWarnings("unused")
  private Person() {
    // Default constructor for DB deserilisation
  }

  public String getName() {
    return name;
  }

  public int getInitialPrompt() {
    return initialPrompt;
  }

  public List<String> getDescriptions() {
    return descriptions;
  }

  public void addDescription(String description) {
    this.descriptions.add(description);
  }

  public boolean isImmortal() {
    return immortal;
  }

  public void setImmortal(boolean immortal) {
    this.immortal = immortal;
  }

  public boolean isDead() {
    return dead;
  }

  public void setDead(boolean dead) {
    this.dead = dead;
  }
}
