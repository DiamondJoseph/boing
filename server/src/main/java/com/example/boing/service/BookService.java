package com.example.boing.service;

import com.example.boing.domain.Book;
import com.example.boing.service.PageService.NewPage;
import com.example.boing.service.generic.ImmutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService extends ImmutableService<Book, BookService.NewBook> {

  @Autowired PageService pService;

  @Override
  protected Book create(NewBook book) {
    return new Book(book.pages.stream().map(pService::createAndSave).toList());
  }

  public record NewBook(@JsonProperty(required = true) List<NewPage> pages) {}
}
