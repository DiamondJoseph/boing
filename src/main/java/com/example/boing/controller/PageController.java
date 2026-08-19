package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Page;
import com.example.boing.service.PageService;
import com.example.boing.service.PageService.NewPage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/page")
public class PageController extends ImmutableController<PageService, Page, NewPage> {}
