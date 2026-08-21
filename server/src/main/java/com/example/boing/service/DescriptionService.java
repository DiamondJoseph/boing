package com.example.boing.service;

import com.example.boing.domain.Description;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Service
public class DescriptionService
    extends ImmutableService<Description, DescriptionService.NewDescription> {

  @Override
  protected Description create(NewDescription description) {
    return new Description(description.description);
  }

  public record NewDescription(@JsonProperty(required = true) String description) {}
}
