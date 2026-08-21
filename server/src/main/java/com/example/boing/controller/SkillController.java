package com.example.boing.controller;

import com.example.boing.controller.generic.MutableController;
import com.example.boing.domain.Skill;
import com.example.boing.service.SkillService;
import com.example.boing.service.SkillService.NewSkill;
import com.example.boing.service.SkillService.SkillUpdate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skill")
public class SkillController
    extends MutableController<SkillService, Skill, NewSkill, SkillUpdate> {}
