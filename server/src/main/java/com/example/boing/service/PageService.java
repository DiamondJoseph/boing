package com.example.boing.service;

import com.example.boing.domain.Page;
import com.example.boing.service.PromptService.NewPrompt;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageService extends ImmutableService<Page, PageService.NewPage> {

  @Autowired PromptService pService;

  @Override
  protected Page create(NewPage page) {
    return new Page(page.page, page.promptTexts.stream().map(pService::createAndSave).toList());
  }

  public record NewPage(
      @JsonProperty(required = true) int page,
      @JsonProperty(required = true) List<NewPrompt> promptTexts) {}
}
