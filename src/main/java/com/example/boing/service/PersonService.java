package com.example.boing.service;

import com.example.boing.domain.Person;
import com.example.boing.service.DescriptionService.NewDescription;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService
    extends MutableService<Person, PersonService.NewPerson, PersonService.PersonUpdate> {

  @Autowired DescriptionService dService;

  @Override
  public Person create(NewPerson person) {
    var description = dService.createAndSave(person.initialDescription);
    return new Person(
        person.name,
        description,
        person.dead == null ? false : person.dead,
        person.immortal == null ? false : person.immortal);
  }

  @Override
  public Person update(Person person, PersonUpdate partial) {
    var description = dService.createAndSave(partial.newDescription);
    person.addDescription(description);
    if (partial.dead != null) {
      person.setDead(partial.dead);
    }
    if (partial.immortal != null) {
      person.setImmortal(partial.immortal);
    }
    return person;
  }

  public record NewPerson(
      @JsonProperty(required = true) String name,
      @JsonProperty(required = true) NewDescription initialDescription,
      @JsonProperty(defaultValue = "false") Boolean dead,
      @JsonProperty(defaultValue = "false") Boolean immortal) {}

  public record PersonUpdate(
      @JsonProperty(required = true) NewDescription newDescription,
      @JsonProperty Boolean dead,
      @JsonProperty Boolean immortal) {}
}
