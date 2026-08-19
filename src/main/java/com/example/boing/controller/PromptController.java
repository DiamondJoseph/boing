package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Prompt;
import com.example.boing.service.PromptService;
import com.example.boing.service.PromptService.NewPrompt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
public class PromptController extends ImmutableController<PromptService, Prompt, NewPrompt> {}
