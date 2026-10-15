package com.example.boing.service.generic;

import com.example.boing.domain.generic.Persistable;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class ImmutableService<T extends Persistable, TNew> {

  @Autowired JpaRepository<T, Long> repository;

  public List<T> getAll() {
    return repository.findAll();
  }

  public Optional<T> getById(Long id) {
    return repository.findById(id);
  }

  public T getOrThrow(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No item with id- has it been persisted?"));
  }

  protected abstract T create(TNew resource);

  public T createAndSave(TNew t) {
    return save(create(t));
  }

  public T save(T t) {
    return repository.save(t);
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
