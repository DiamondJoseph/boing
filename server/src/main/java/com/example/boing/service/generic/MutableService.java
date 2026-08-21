package com.example.boing.service.generic;

import com.example.boing.domain.generic.Persistable;

public abstract class MutableService<T extends Persistable, TNew, TUpdate>
    extends ImmutableService<T, TNew> {

  public T update(long id, TUpdate update) {
    return repository.save(update(getOrThrow(id), update));
  }

  protected abstract T update(T t, TUpdate update);
}
