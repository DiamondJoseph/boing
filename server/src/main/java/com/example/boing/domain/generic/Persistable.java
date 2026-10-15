package com.example.boing.domain.generic;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Persistable {

  @Id @GeneratedValue private Long id;

  public Long getId() {
    return id;
  }
}
