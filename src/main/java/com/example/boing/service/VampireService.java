package com.example.boing.service;

import com.example.boing.domain.Vampire;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Service
public class VampireService extends ImmutableService<Vampire, VampireService.NewVampire> {

  @Override
  public Vampire create(NewVampire vampire) {
    return new Vampire(vampire.name, vampire.initialDescription);
  }

  public record NewVampire(
      @JsonProperty(required = true) String name,
      @JsonProperty(required = true) String initialDescription) {}
}
