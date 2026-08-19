package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Experience;
import com.example.boing.service.ExperienceService;
import com.example.boing.service.ExperienceService.NewExperience;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/experience")
public class ExperienceController
    extends ImmutableController<ExperienceService, Experience, NewExperience> {}
