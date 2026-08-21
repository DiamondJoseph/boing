package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Vampire extends Persistable {

  @Column private String name;
  @Column private String intialDescription;

  public Vampire(String name, String intialDescription) {
    this.name = name;
    this.intialDescription = intialDescription;
  }

  @SuppressWarnings("unused")
  private Vampire() {}

  public String getName() {
    return name;
  }

  public String getIntialDescription() {
    return intialDescription;
  }
}
