package com.example.boing.controller;

import static com.example.boing.controller.ApiTestClient.person;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boing.service.SkillService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

/** PATCH requests change only the fields they contain. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartialUpdateIntegrationTest {

  @LocalServerPort int port;

  @Autowired SkillService skillService;

  private ApiTestClient api;

  @BeforeEach
  void setUp() {
    api = new ApiTestClient(port);
  }

  private static long id(JsonNode node) {
    return node.get("id").asLong();
  }

  private JsonNode patchOk(String path, Map<String, Object> body) {
    var response = api.patch(path, body);
    assertEquals(HttpStatus.OK, response.status(), () -> "Body: " + response.body());
    return response.body();
  }

  private long newMemory() {
    var prompt = api.create("/api/prompt", Map.of("promptText", "What happened?"));
    var memory =
        api.create(
            "/api/memory",
            Map.of("initialExperience", Map.of("promptId", id(prompt), "response", "It rained")));
    assertTrue(memory.get("remembered").asBoolean());
    return id(memory);
  }

  @Test
  void testSkillUpdateLeavesOmittedFlagsUnchanged() {
    var skill = id(api.create("/api/skill", Map.of("name", "Fencing")));

    var checked = patchOk("/api/skill/" + skill, Map.of("checked", true));
    assertTrue(checked.get("checked").asBoolean());
    assertFalse(checked.get("lost").asBoolean());

    var lost = patchOk("/api/skill/" + skill, Map.of("lost", true));
    assertTrue(lost.get("checked").asBoolean());
    assertTrue(lost.get("lost").asBoolean());
  }

  @Test
  void testPersonUpdateAppendsDescriptionAndLeavesOmittedFlagsUnchanged() {
    var person = id(api.create("/api/person", person("Ana")));
    patchOk(
        "/api/person/" + person,
        Map.of("newDescription", Map.of("description", "Turned"), "immortal", true));

    var updated =
        patchOk("/api/person/" + person, Map.of("newDescription", Map.of("description", "Later")));

    assertTrue(updated.get("immortal").asBoolean());
    assertFalse(updated.get("dead").asBoolean());
    var descriptions = updated.get("descriptions");
    assertEquals(3, descriptions.size());
    assertEquals("Ana described", descriptions.get(0).get("text").asString());
    assertEquals("Turned", descriptions.get(1).get("text").asString());
    assertEquals("Later", descriptions.get(2).get("text").asString());
  }

  @Test
  void testResourceUpdateWithoutLostIsRejectedRatherThanDefaultingToFalse() {
    var resource = id(api.create("/api/resource", Map.of("name", "Castle", "stationary", true)));
    patchOk("/api/resource/" + resource, Map.of("lost", true));

    var response = api.patch("/api/resource/" + resource, Map.of());

    assertEquals(HttpStatus.BAD_REQUEST, response.status());
    assertTrue(api.get("/api/resource/" + resource).body().get("lost").asBoolean());
  }

  @Test
  void testUpdatingUnknownIdIsNotFound() {
    var response = api.patch("/api/skill/" + Long.MAX_VALUE, Map.of("checked", true));

    assertEquals(HttpStatus.NOT_FOUND, response.status());
  }

  @Test
  void testNotFoundReasonNamesTheId() {
    var thrown =
        assertThrows(ResponseStatusException.class, () -> skillService.getOrThrow(Long.MAX_VALUE));

    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    assertEquals("No item with id " + Long.MAX_VALUE, thrown.getReason());
  }

  @Test
  void testMemoryUpdateAddsExperienceAndLeavesOmittedFlagUnchanged() {
    var memory = newMemory();
    var prompt = id(api.create("/api/prompt", Map.of("promptText", "And then?")));

    var forgotten = patchOk("/api/memory/" + memory, Map.of("remembered", false));
    assertFalse(forgotten.get("remembered").asBoolean());

    var updated =
        patchOk(
            "/api/memory/" + memory,
            Map.of("newExperience", Map.of("promptId", prompt, "response", "It stopped")));

    assertFalse(updated.get("remembered").asBoolean());
    var experiences = updated.get("experiences");
    assertEquals(2, experiences.size());
    assertEquals("It rained", experiences.get(0).get("response").asString());
    assertEquals("It stopped", experiences.get(1).get("response").asString());
  }

  @Test
  void testMemoriesWrittenIntoADiaryAreNoLongerRemembered() {
    var initial = newMemory();
    var added = newMemory();

    var diary =
        id(
            api.create(
                "/api/diary",
                Map.of("description", "A diary", "initialMemories", List.of(initial))));
    assertFalse(api.get("/api/memory/" + initial).body().get("remembered").asBoolean());
    assertTrue(api.get("/api/memory/" + added).body().get("remembered").asBoolean());

    var updated = patchOk("/api/diary/" + diary, Map.of("addedMemories", List.of(added)));

    assertEquals(2, updated.get("memories").size());
    assertFalse(api.get("/api/memory/" + added).body().get("remembered").asBoolean());
  }

  @Test
  void testDiaryWithUnknownMemoryIsNotFound() {
    var response =
        api.post(
            "/api/diary",
            Map.of("description", "A diary", "initialMemories", List.of(Long.MAX_VALUE)));

    assertEquals(HttpStatus.NOT_FOUND, response.status());
  }
}
