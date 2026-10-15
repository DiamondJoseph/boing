package com.example.boing.service;

import com.example.boing.domain.Experience;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExperienceService
    extends ImmutableService<Experience, ExperienceService.NewExperience> {

  @Autowired PromptService pService;

  @Override
  protected Experience create(NewExperience memory) {
    return new Experience(pService.getOrThrow(memory.promptId), memory.response);
  }

  public record NewExperience(
      @JsonProperty(required = true) long promptId,
      @JsonProperty(required = true) String response) {}
}
