package com.example.boing.controller;

import com.example.boing.domain.Person;
import com.example.boing.service.PersonService;
import com.example.boing.service.PersonService.PersonUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController extends GenericController<PersonService, Person> {

  @PatchMapping("/{id}")
  public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody PersonUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }
}
