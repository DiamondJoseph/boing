package com.example.boing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** A roll moves the current value by d10 - d6, but never below 1. */
class DiceServiceTest {

  private static final int ROLLS = 10_000;

  private final DiceService service = new DiceService();

  private Set<Integer> rollMany(int current, long seed) {
    var random = new Random(seed);
    return IntStream.range(0, ROLLS)
        .mapToObj(i -> service.next(random, current))
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @ParameterizedTest
  @ValueSource(ints = {-10, 0, 1, 3, 6, 7, 20})
  void testEveryResultInRangeIsReachableAndNothingOutsideIt(int current) {
    // d10 - d6 is between -5 and +9, and the result is clamped to at least 1
    var expected =
        IntStream.rangeClosed(Math.max(1, current - 5), Math.max(1, current + 9))
            .boxed()
            .collect(Collectors.toCollection(TreeSet::new));

    assertEquals(expected, rollMany(current, 42));
  }

  @ParameterizedTest
  @ValueSource(ints = {-10, 0, 1})
  void testResultIsNeverBelowOne(int current) {
    assertTrue(rollMany(current, 7).stream().allMatch(result -> result >= 1));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 10})
  void testSameSeedGivesSameSequence(int current) {
    var first = new Random(1234);
    var second = new Random(1234);
    for (int i = 0; i < 100; i++) {
      assertEquals(service.next(first, current), service.next(second, current));
    }
  }
}
