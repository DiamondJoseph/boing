package com.example.boing.service;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class DiceService {

  public int next(RandomGenerator random, int current) {
    return Math.max(1, current + random.nextInt(1, 11) - random.nextInt(1, 7));
  }
}
