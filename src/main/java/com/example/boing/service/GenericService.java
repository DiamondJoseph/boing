package com.example.boing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

public class GenericService<T> {

  @Autowired protected JpaRepository<T, Long> repository;

  public List<T> getAll() {
    return repository.findAll();
  }

  public Optional<T> getById(Long id) {
    return repository.findById(id);
  }

  public T save(T resource) {
    return repository.save(resource);
  }

  public boolean delete(Long id) {
    var resource = repository.findById(id);
    if (resource.isEmpty()) {
      return false;
    }
    repository.delete(resource.get());
    return true;
  }
}
