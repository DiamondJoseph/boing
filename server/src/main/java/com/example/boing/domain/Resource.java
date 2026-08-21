package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/** Resource are assets or structures that are useful. */
@Entity
public class Resource extends Persistable {

  @Column private String name;
  @Column private boolean stationary;
  @Column private boolean lost = false;

  public Resource(String name, boolean stationary) {
    this.name = name;
    this.stationary = stationary;
  }

  @SuppressWarnings("unused")
  private Resource() {}

  public String getName() {
    return name;
  }

  public boolean isStationary() {
    return stationary;
  }

  public boolean isLost() {
    return lost;
  }

  public void setLost(boolean lost) {
    this.lost = lost;
  }
}
