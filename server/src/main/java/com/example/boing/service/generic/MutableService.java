package com.example.boing.service.generic;

import com.example.boing.domain.generic.Persistable;
import org.springframework.transaction.annotation.Transactional;

public abstract class MutableService<T extends Persistable, TNew, TUpdate>
    extends ImmutableService<T, TNew> {

  /**
   * Applies the update, including any changes {@link #update(Persistable, Object)} makes to child
   * items, in one transaction, so a failure part way through leaves nothing changed.
   */
  @Transactional
  public T update(long id, TUpdate update) {
    return repository.save(update(getOrThrow(id), update));
  }

  protected abstract T update(T t, TUpdate update);
}
