package com.example.boing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/** Skills describe the capabilities and characteristics of your vampire. */
@Entity
public class Skill {

  @JsonIgnore @Id @GeneratedValue private Long id;
  private String name;
  private boolean checked = false;
  private boolean lost = false;

  public Skill(String name) {
    this.name = name;
  }

  @SuppressWarnings("unused")
  private Skill() {
    // Default constructor for DB deserilisation
  }

  public String getName() {
    return name;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean lost) {
    this.lost = lost;
  }

  public boolean isLost() {
    return lost;
  }

  public void setLost(boolean lost) {
    this.lost = lost;
  }
}
