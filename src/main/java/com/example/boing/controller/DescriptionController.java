package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Description;
import com.example.boing.service.DescriptionService;
import com.example.boing.service.DescriptionService.NewDescription;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/description")
public class DescriptionController
    extends ImmutableController<DescriptionService, Description, NewDescription> {}
