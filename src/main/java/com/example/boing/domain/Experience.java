package com.example.boing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/** Experience are what you do with - or to - other people. */
@Entity
public class Experience {

  @Id @GeneratedValue private Long id;
  @Column private String description;

  public Experience(String description) {
    this.description = description;
  }

  @SuppressWarnings("unused")
  private Experience() {
    // Default constructor for DB deserilisation
  }
}
