package com.example.boing.controller;

import com.example.boing.controller.generic.MutableController;
import com.example.boing.domain.Resource;
import com.example.boing.service.ResourceService;
import com.example.boing.service.ResourceService.NewResource;
import com.example.boing.service.ResourceService.ResourceUpdate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource")
public class ResourceController
    extends MutableController<ResourceService, Resource, NewResource, ResourceUpdate> {}
