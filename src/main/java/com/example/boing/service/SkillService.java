package com.example.boing.service;

import com.example.boing.domain.Skill;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SkillService {

  @Autowired private JpaRepository<Skill, Long> repository;

  public List<Skill> getAll() {
    return repository.findAll();
  }

  public Optional<Skill> getById(Long id) {
    return repository.findById(id);
  }

  public Skill save(Skill resource) {
    return repository.save(resource);
  }

  public Skill update(Long id, SkillUpdate partial) {
    var resource =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    resource.setChecked(partial.checked());
    resource.setLost(partial.lost());
    return repository.save(resource);
  }

  public boolean delete(Long id) {
    var resource = repository.findById(id);
    if (resource.isEmpty()) {
      return false;
    }
    repository.delete(resource.get());
    return true;
  }

  public record SkillUpdate(boolean checked, boolean lost) {}
}
