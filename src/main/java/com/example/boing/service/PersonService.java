package com.example.boing.service;

import com.example.boing.domain.Person;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersonService extends GenericService<Person> {

  public Person update(Long id, PersonUpdate partial) {
    var person =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    person.addDescription(partial.newDescription());
    person.setImmortal(partial.immortal());
    person.setDead(partial.dead());
    return repository.save(person);
  }

  public record PersonUpdate(String newDescription, boolean immortal, boolean dead) {}
}
