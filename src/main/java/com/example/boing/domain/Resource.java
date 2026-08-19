package com.example.boing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/** Resource are assets or structures that are useful. */
@Entity
public class Resource {

  @JsonIgnore @Id @GeneratedValue private Long id;
  @Column private String name;
  @Column private boolean stationary;
  @Column private boolean lost = false;

  public Resource(String name, boolean stationary) {
    this.name = name;
    this.stationary = stationary;
  }

  @SuppressWarnings("unused")
  private Resource() {
    // Default constructor for DB deserilisation
  }

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
