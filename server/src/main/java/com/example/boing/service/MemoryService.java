package com.example.boing.service;

import com.example.boing.domain.Memory;
import com.example.boing.service.ExperienceService.NewExperience;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemoryService
    extends MutableService<Memory, MemoryService.NewMemory, MemoryService.MemoryUpdate> {

  @Autowired ExperienceService eService;

  @Override
  protected Memory create(NewMemory memory) {
    return new Memory(eService.createAndSave(memory.initialExperience));
  }

  @Override
  protected Memory update(Memory memory, MemoryUpdate update) {
    if (update.newExperience != null) {
      memory.addExperience(eService.createAndSave(update.newExperience));
    }
    if (update.remembered != null) {
      memory.setRemembered(update.remembered);
    }
    return memory;
  }

  public record NewMemory(@JsonProperty(required = true) NewExperience initialExperience) {}

  public record MemoryUpdate(
      @JsonProperty NewExperience newExperience, @JsonProperty Boolean remembered) {}
}
