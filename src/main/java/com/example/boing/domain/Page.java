package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Page extends Persistable {

  @Column private int page;
  @OneToMany private List<Prompt> prompts;

  public Page(int page, List<Prompt> prompts) {
    this.page = page;
    this.prompts = prompts;
  }

  @SuppressWarnings("unused")
  private Page() {}

  public int getPage() {
    return page;
  }

  public List<Prompt> getPrompts() {
    return prompts;
  }
}
