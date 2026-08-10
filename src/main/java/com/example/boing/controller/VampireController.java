package com.example.boing.controller;

import com.example.boing.domain.Vampire;
import com.example.boing.service.VampireService;
import com.example.boing.service.VampireService.VampireUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vampire")
public class VampireController extends GenericController<VampireService, Vampire> {

  @PatchMapping("/{id}")
  public ResponseEntity<Vampire> update(@PathVariable Long id, @RequestBody VampireUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }
}
