package com.example.boing.service;

import com.example.boing.domain.Prompt;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Service
public class PromptService extends ImmutableService<Prompt, PromptService.NewPrompt> {

  @Override
  protected Prompt create(NewPrompt prompt) {
    return new Prompt(prompt.promptText);
  }

  public record NewPrompt(@JsonProperty(required = true) String promptText) {}
}
