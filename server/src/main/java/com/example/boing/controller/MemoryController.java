package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Memory;
import com.example.boing.service.MemoryService;
import com.example.boing.service.MemoryService.NewMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memory")
public class MemoryController extends ImmutableController<MemoryService, Memory, NewMemory> {}
