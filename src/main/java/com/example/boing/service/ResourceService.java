package com.example.boing.service;

import com.example.boing.domain.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResourceService {

  @Autowired private JpaRepository<Resource, Long> repository;

  public List<Resource> getAll() {
    return repository.findAll();
  }

  public Optional<Resource> getById(Long id) {
    return repository.findById(id);
  }

  public Resource save(Resource resource) {
    return repository.save(resource);
  }

  public Resource update(Long id, ResourceUpdate partial) {
    var resource =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

  public record ResourceUpdate(boolean lost) {}
}
