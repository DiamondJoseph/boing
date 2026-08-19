package com.example.boing.controller;

import com.example.boing.service.DiceService;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dice")
public class DiceController {

  @Autowired DiceService service;

  @GetMapping
  public ResponseEntity<Integer> first() {
    return ResponseEntity.ok(service.next(ThreadLocalRandom.current(), 1));
  }

  @GetMapping("/{current}")
  public ResponseEntity<Integer> next(@PathVariable int current) {
    return ResponseEntity.ok(service.next(ThreadLocalRandom.current(), current));
  }
}
