package com.example.boing.controller;

import com.example.boing.controller.generic.ImmutableController;
import com.example.boing.domain.Book;
import com.example.boing.service.BookService;
import com.example.boing.service.BookService.NewBook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController extends ImmutableController<BookService, Book, NewBook> {}
