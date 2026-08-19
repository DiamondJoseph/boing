package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/** Many experiences tie together to become a memory. */
@Entity
public class Memory extends Persistable {

  @Column private boolean remembered;
  @OneToMany private List<Experience> experiences;

  public Memory(Experience initialExperience) {
    this.experiences = new ArrayList<>();
    addExperience(initialExperience);
    this.remembered = true;
  }

  @SuppressWarnings("unused")
  private Memory() {}

  public List<Experience> getExperiences() {
    return experiences;
  }

  public void addExperience(Experience experience) {
    this.experiences.add(experience);
  }

  public boolean isRemembered() {
    return remembered;
  }

  public void setRemembered(boolean remembered) {
    this.remembered = remembered;
  }
}
