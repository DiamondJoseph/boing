package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/** Skills describe the capabilities and characteristics of your vampire. */
@Entity
public class Skill extends Persistable {

  @Column private String name;
  @Column private boolean checked = false;
  @Column private boolean lost = false;

  public Skill(String name) {
    this.name = name;
  }

  @SuppressWarnings("unused")
  private Skill() {}

  public String getName() {
    return name;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public boolean isLost() {
    return lost;
  }

  public void setLost(boolean lost) {
    this.lost = lost;
  }
}
