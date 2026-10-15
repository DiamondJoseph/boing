package com.example.boing.controller;

import static com.example.boing.controller.ApiTestClient.newGame;
import static com.example.boing.controller.ApiTestClient.person;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameApiIntegrationTest {

  private static final List<String> CHILD_COLLECTIONS =
      List.of("/api/vampire", "/api/person", "/api/resource", "/api/skill", "/api/description");

  @LocalServerPort int port;

  private ApiTestClient api;

  @BeforeEach
  void setUp() {
    api = new ApiTestClient(port);
  }

  private static long id(JsonNode node) {
    return node.get("id").asLong();
  }

  private List<Integer> childCounts() {
    return CHILD_COLLECTIONS.stream().map(path -> api.get(path).body().size()).toList();
  }

  private static Map<String, Object> attached(JsonNode child, Map<String, Object> update) {
    return Map.of("id", id(child), "update", update);
  }

  private static Map<String, Object> personUpdate(String description, boolean dead) {
    return Map.of("newDescription", Map.of("description", description), "dead", dead);
  }

  @Test
  void testCreateGamePersistsChildrenWithDefaults() {
    var game = api.create("/api/game", newGame("Vlad", 3, 3, 3));

    assertTrue(game.hasNonNull("id"));
    assertEquals("Vlad", game.get("vampire").get("name").asString());
    assertTrue(game.get("memories").isEmpty());
    assertTrue(game.get("diaries").isArray(), () -> "diaries: " + game.get("diaries"));
    assertTrue(game.get("diaries").isEmpty());
    for (var person : game.get("people")) {
      assertTrue(person.hasNonNull("id"));
      // dead and immortal were omitted from the request, so default to false
      assertFalse(person.get("dead").asBoolean());
      assertFalse(person.get("immortal").asBoolean());
      assertEquals(1, person.get("descriptions").size());
    }
    for (var skill : game.get("skills")) {
      assertFalse(skill.get("checked").asBoolean());
      assertFalse(skill.get("lost").asBoolean());
    }

    var fetched = api.get("/api/game/" + id(game));
    assertEquals(HttpStatus.OK, fetched.status());
    assertEquals(id(game), id(fetched.body()));
  }

  @ParameterizedTest
  @CsvSource({"3, 3, 2", "3, 3, 4", "3, 2, 3", "3, 4, 3", "2, 3, 3"})
  void testCreateGameBreakingSetupRulesIsRejectedAndSavesNothing(
      int people, int resources, int skills) {
    var gamesBefore = api.get("/api/game").body().size();
    var childrenBefore = childCounts();

    var response = api.post("/api/game", newGame("Rejected", people, resources, skills));

    assertEquals(HttpStatus.BAD_REQUEST, response.status());
    assertEquals(gamesBefore, api.get("/api/game").body().size());
    // The vampire, people etc. are created before the game rules are checked: none may remain
    assertEquals(childrenBefore, childCounts());
  }

  @Test
  void testGameUpdateChangesItsOwnChildren() {
    var game = api.create("/api/game", newGame("Vlad", 3, 3, 3));
    var person = game.get("people").get(0);
    var resource = game.get("resources").get(0);
    var skill = game.get("skills").get(0);

    var response =
        api.patch(
            "/api/game/" + id(game),
            Map.of(
                "newPeople", List.of(person("Newcomer")),
                "peopleUpdates", List.of(attached(person, personUpdate("Changed", true))),
                "resourceUpdates", List.of(attached(resource, Map.of("lost", true))),
                "skillUpdates", List.of(attached(skill, Map.of("checked", true)))));
    assertEquals(HttpStatus.OK, response.status(), () -> "Body: " + response.body());

    var updated = api.get("/api/game/" + id(game)).body();
    assertEquals(4, updated.get("people").size());
    assertEquals("Newcomer", updated.get("people").get(3).get("name").asString());

    var updatedPerson = api.get("/api/person/" + id(person)).body();
    assertTrue(updatedPerson.get("dead").asBoolean());
    assertFalse(updatedPerson.get("immortal").asBoolean());
    assertEquals(2, updatedPerson.get("descriptions").size());
    assertEquals("Changed", updatedPerson.get("descriptions").get(1).get("text").asString());

    assertTrue(api.get("/api/resource/" + id(resource)).body().get("lost").asBoolean());

    var updatedSkill = api.get("/api/skill/" + id(skill)).body();
    assertTrue(updatedSkill.get("checked").asBoolean());
    assertFalse(updatedSkill.get("lost").asBoolean());
  }

  @Test
  void testGameUpdateCannotChangeAnotherGamesChildren() {
    var game = api.create("/api/game", newGame("Vlad", 3, 3, 3));
    var other = api.create("/api/game", newGame("Carmilla", 3, 3, 3));
    var othersPerson = other.get("people").get(0);
    var othersSkill = other.get("skills").get(0);

    var personResponse =
        api.patch(
            "/api/game/" + id(game),
            Map.of(
                "peopleUpdates", List.of(attached(othersPerson, personUpdate("Hijacked", true)))));
    var skillResponse =
        api.patch(
            "/api/game/" + id(game),
            Map.of("skillUpdates", List.of(attached(othersSkill, Map.of("lost", true)))));

    assertEquals(HttpStatus.FORBIDDEN, personResponse.status());
    assertEquals(HttpStatus.FORBIDDEN, skillResponse.status());

    var person = api.get("/api/person/" + id(othersPerson)).body();
    assertFalse(person.get("dead").asBoolean());
    assertEquals(1, person.get("descriptions").size());
    assertFalse(api.get("/api/skill/" + id(othersSkill)).body().get("lost").asBoolean());
  }

  @Test
  void testRejectedGameUpdateIsRolledBackEntirely() {
    var game = api.create("/api/game", newGame("Vlad", 3, 3, 3));
    var other = api.create("/api/game", newGame("Carmilla", 3, 3, 3));
    var ownPerson = game.get("people").get(0);
    var othersPerson = other.get("people").get(0);
    var childrenBefore = childCounts();

    // New people and the first person update are applied before the second update is refused
    var response =
        api.patch(
            "/api/game/" + id(game),
            Map.of(
                "newPeople",
                List.of(person("Newcomer")),
                "peopleUpdates",
                List.of(
                    attached(ownPerson, personUpdate("Changed", true)),
                    attached(othersPerson, personUpdate("Hijacked", true)))));

    assertEquals(HttpStatus.FORBIDDEN, response.status());
    assertEquals(3, api.get("/api/game/" + id(game)).body().get("people").size());
    var person = api.get("/api/person/" + id(ownPerson)).body();
    assertFalse(person.get("dead").asBoolean());
    assertEquals(1, person.get("descriptions").size());
    assertEquals(childrenBefore, childCounts());
  }

  @Test
  void testUpdatingUnknownGameIsNotFound() {
    var response = api.patch("/api/game/" + Long.MAX_VALUE, Map.of());

    assertEquals(HttpStatus.NOT_FOUND, response.status());
  }
}
