package com.example.boing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/** Character are the people with whom your vampire has a relationship. */
@Entity
public class Person {

  @Id @GeneratedValue private Long id;
  @Column private String name;
  @Column private int initialPrompt;
  @OneToMany private List<Experience> experiences = new ArrayList<>();
  @Column private boolean immortal;
  @Column private boolean dead = false;

  public Person(String name, int initialPrompt, String initialDescription, boolean immortal) {
    this.name = name;
    this.initialPrompt = initialPrompt;
    this.experiences.add(new Experience(initialDescription));
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

  public List<Experience> getExperiences() {
    return experiences;
  }

  public void addExperience(Experience experience) {
    this.experiences.add(experience);
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
