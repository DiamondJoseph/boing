package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/** A way to store memories outside of your own head, for better or worse. */
@Entity
public class Diary extends Persistable {

  @Column private String description;
  @Column private boolean lost = false;
  @OneToMany private List<Memory> memories;

  public Diary(String description, List<Memory> initialMemories) {
    this.description = description;
    this.memories = new ArrayList<>();
    for (var memory : initialMemories) {
      addMemory(memory);
    }
  }

  @SuppressWarnings("unused")
  private Diary() {}

  public String getDescription() {
    return description;
  }

  public boolean isLost() {
    return lost;
  }

  public void setLost(boolean lost) {
    this.lost = lost;
  }

  public List<Memory> getMemories() {
    return memories;
  }

  public void addMemory(Memory memory) {
    memory.setRemembered(false);
    this.memories.add(memory);
  }
}
