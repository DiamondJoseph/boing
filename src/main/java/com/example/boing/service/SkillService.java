package com.example.boing.service;

import com.example.boing.domain.Skill;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SkillService extends GenericService<Skill> {

  public Skill update(Long id, SkillUpdate partial) {
    var skill =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    skill.setChecked(partial.checked());
    skill.setLost(partial.lost());
    return repository.save(skill);
  }

  public record SkillUpdate(boolean checked, boolean lost) {}
}
