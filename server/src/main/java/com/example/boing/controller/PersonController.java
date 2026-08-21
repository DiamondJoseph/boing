package com.example.boing.controller;

import com.example.boing.controller.generic.MutableController;
import com.example.boing.domain.Person;
import com.example.boing.service.PersonService;
import com.example.boing.service.PersonService.NewPerson;
import com.example.boing.service.PersonService.PersonUpdate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController
    extends MutableController<PersonService, Person, NewPerson, PersonUpdate> {}
