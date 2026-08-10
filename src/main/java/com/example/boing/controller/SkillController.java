package com.example.boing.controller;

import com.example.boing.domain.Skill;
import com.example.boing.service.SkillService;
import com.example.boing.service.SkillService.SkillUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skill")
public class SkillController extends GenericController<SkillService, Skill> {

  @PatchMapping("/{id}")
  public ResponseEntity<Skill> update(@PathVariable Long id, @RequestBody SkillUpdate partial) {
    return ResponseEntity.ok(service.update(id, partial));
  }
}
