package com.example.boing.service;

import com.example.boing.domain.Resource;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

@Service
public class ResourceService
    extends MutableService<Resource, ResourceService.NewResource, ResourceService.ResourceUpdate> {

  @Override
  public Resource create(NewResource resource) {
    return new Resource(resource.name, resource.stationary);
  }

  @Override
  public Resource update(Resource resource, ResourceUpdate partial) {
    resource.setLost(partial.lost);
    return resource;
  }

  public record NewResource(
      @JsonProperty(required = true) String name,
      @JsonProperty(required = true) boolean stationary) {}

  public record ResourceUpdate(@JsonProperty(required = true) boolean lost) {}
}
