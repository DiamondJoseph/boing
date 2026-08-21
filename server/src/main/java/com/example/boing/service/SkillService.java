package com.example.boing.service;

import com.example.boing.domain.Skill;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Service
public class SkillService
    extends MutableService<Skill, SkillService.NewSkill, SkillService.SkillUpdate> {

  @Override
  public Skill create(NewSkill skill) {
    return new Skill(skill.name);
  }

  @Override
  public Skill update(Skill skill, SkillUpdate partial) {
    if (partial.checked != null) {
      skill.setChecked(partial.checked);
    }
    if (partial.lost != null) {
      skill.setLost(partial.lost);
    }
    return skill;
  }

  public record NewSkill(@JsonProperty(required = true) String name) {}

  public record SkillUpdate(@JsonProperty Boolean checked, @JsonProperty Boolean lost) {}
}
