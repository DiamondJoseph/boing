package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Book extends Persistable {

  @OneToMany private List<Page> pages;

  public Book(List<Page> pages) {
    this.pages = pages;
  }

  @SuppressWarnings("unused")
  private Book() {}

  public List<Page> getPages() {
    return pages;
  }
}
