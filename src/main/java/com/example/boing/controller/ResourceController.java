package com.example.boing.controller;

import com.example.boing.domain.Resource;
import com.example.boing.service.ResourceService;
import com.example.boing.service.ResourceService.ResourceUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource")
public class ResourceController extends GenericController<ResourceService, Resource> {

  @PatchMapping("/{id}")
  public ResponseEntity<Resource> update(
      @PathVariable Long id, @RequestBody ResourceUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }
}
