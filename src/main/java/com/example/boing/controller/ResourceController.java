package com.example.boing.controller;

import com.example.boing.domain.Resource;
import com.example.boing.service.ResourceService;
import com.example.boing.service.ResourceService.ResourceUpdate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

  @Autowired private ResourceService service;

  @PostMapping
  public ResponseEntity<Resource> create(@RequestBody Resource resource) {
    return new ResponseEntity<>(service.save(resource), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Resource>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Optional<Resource>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Resource> update(
      @PathVariable Long id, @RequestBody ResourceUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
