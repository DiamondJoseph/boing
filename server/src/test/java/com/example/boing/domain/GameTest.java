package com.example.boing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameTest {

  private static final Vampire VAMPIRE = new Vampire("Vlad", "An old soldier");

  private static List<Person> people(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> new Person("Person " + i, new Description("Description " + i), false, false))
        .toList();
  }

  private static List<Resource> resources(int count) {
    return IntStream.range(0, count).mapToObj(i -> new Resource("Resource " + i, false)).toList();
  }

  private static List<Skill> skills(int count) {
    return IntStream.range(0, count).mapToObj(i -> new Skill("Skill " + i)).toList();
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 4, 10})
  void testGameStartsWithThreeSkillsThreeResourcesAndAtLeastThreePeople(int peopleCount) {
    var people = people(peopleCount);
    var game = new Game(VAMPIRE, people, resources(3), skills(3));

    assertSame(VAMPIRE, game.getVampire());
    assertEquals(peopleCount, game.getPeople().size());
    assertEquals(3, game.getResources().size());
    assertEquals(3, game.getSkills().size());
    assertTrue(game.getMemories().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 4})
  void testGameRejectsAnythingButThreeSkills(int count) {
    var thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Game(VAMPIRE, people(3), resources(3), skills(count)));
    assertEquals("Should start with 3 skills", thrown.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 4})
  void testGameRejectsAnythingButThreeResources(int count) {
    var thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Game(VAMPIRE, people(3), resources(count), skills(3)));
    assertEquals("Should start with 3 resources", thrown.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2})
  void testGameRejectsFewerThanThreePeople(int count) {
    var thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Game(VAMPIRE, people(count), resources(3), skills(3)));
    assertEquals("Should start with at least 3 people", thrown.getMessage());
  }

  @Test
  void testNewGameCanBeAddedToWhenGivenUnmodifiableLists() {
    var game = new Game(VAMPIRE, List.copyOf(people(3)), resources(3), skills(3));
    var memory = new Memory(new Experience(new Prompt("A prompt"), "A response"));

    game.addPerson(people(1).getFirst());
    game.addResource(resources(1).getFirst());
    game.addSkill(skills(1).getFirst());
    game.addMemory(memory);
    game.addDiary(new Diary("A diary", List.of()));

    assertEquals(4, game.getPeople().size());
    assertEquals(4, game.getResources().size());
    assertEquals(4, game.getSkills().size());
    assertEquals(List.of(memory), game.getMemories());
    assertEquals(1, game.getDiaries().size());
  }

  @Test
  void testNewGameHasNoDiaries() {
    assertEquals(List.of(), new Game(VAMPIRE, people(3), resources(3), skills(3)).getDiaries());
  }

  @Test
  void testSkillsAreValidatedBeforeResourcesAndPeople() {
    var thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Game(VAMPIRE, people(0), resources(0), skills(0)));
    assertEquals("Should start with 3 skills", thrown.getMessage());
  }
}
