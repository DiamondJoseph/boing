package com.example.boing.controller.generic;

import com.example.boing.domain.generic.Persistable;
import com.example.boing.service.generic.MutableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class MutableController<
        S extends MutableService<T, TNew, TUpdate>, T extends Persistable, TNew, TUpdate>
    extends ImmutableController<S, T, TNew> {

  @PatchMapping("/{id}")
  public ResponseEntity<T> update(@PathVariable long id, @RequestBody TUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }
}
