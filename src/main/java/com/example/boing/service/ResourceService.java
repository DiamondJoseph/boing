package com.example.boing.service;

import com.example.boing.domain.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResourceService extends GenericService<Resource> {

  public Resource update(Long id, ResourceUpdate partial) {
    var resource =
        repository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    resource.setLost(partial.lost());
    return repository.save(resource);
  }

  public record ResourceUpdate(boolean lost) {}
}
