package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/** People with whom your vampire has a relationship. */
@Entity
public class Person extends Persistable {

  @Column private String name;
  @Column private boolean dead;
  @Column private boolean immortal;
  @OneToMany private List<Description> descriptions;

  public Person(String name, Description initialDescription, boolean dead, boolean immortal) {
    this.name = name;
    this.dead = dead;
    this.immortal = immortal;
    this.descriptions = new ArrayList<>();
    descriptions.add(initialDescription);
  }

  @SuppressWarnings("unused")
  private Person() {}

  public String getName() {
    return name;
  }

  public List<Description> getDescriptions() {
    return descriptions;
  }

  public void addDescription(Description description) {
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
