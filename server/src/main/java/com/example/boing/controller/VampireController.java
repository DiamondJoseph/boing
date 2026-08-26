package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Vampire;
import com.example.boing.service.VampireService;
import com.example.boing.service.VampireService.NewVampire;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vampire")
public class VampireController extends ImmutableController<VampireService, Vampire, NewVampire> {}
