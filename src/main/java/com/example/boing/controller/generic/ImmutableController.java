package com.example.boing.controller.generic;

import com.example.boing.domain.generic.Persistable;
import com.example.boing.service.generic.ImmutableService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class ImmutableController<
    S extends ImmutableService<T, TNew>, T extends Persistable, TNew> {

  @Autowired S service;

  @PostMapping
  public ResponseEntity<T> create(@RequestBody TNew resource) {
    return new ResponseEntity<>(service.createAndSave(resource), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<T>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Optional<T>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
