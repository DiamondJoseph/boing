package com.example.boing.service;

import com.example.boing.domain.Resource;
import com.example.boing.domain.Skill;
import com.example.boing.domain.Vampire;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VampireService extends GenericService<Vampire> {

  public Vampire update(Long id, VampireUpdate partial) {
    var vampire =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    for (var skill : partial.newSkills()) {
      vampire.addSkill(skill);
    }
    for (var resource : partial.newResources()) {
      vampire.addResource(resource);
    }
    return repository.save(vampire);
  }

  public record VampireUpdate(List<Skill> newSkills, List<Resource> newResources) {}
}
