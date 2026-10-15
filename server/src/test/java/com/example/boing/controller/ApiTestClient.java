package com.example.boing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Makes real HTTP requests to the running app and returns status and parsed JSON body. */
class ApiTestClient {

  record Response(HttpStatus status, JsonNode body) {}

  private final JsonMapper json = JsonMapper.builder().build();
  private final RestClient client;

  ApiTestClient(int port) {
    this.client =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            // Return error statuses to the test instead of throwing
            .defaultStatusHandler(status -> true, (request, response) -> {})
            .build();
  }

  Response send(HttpMethod method, String path, Object body) {
    var request = client.method(method).uri(path);
    if (body != null) {
      request.contentType(MediaType.APPLICATION_JSON).body(json.writeValueAsString(body));
    }
    var entity = request.retrieve().toEntity(String.class);
    var text = entity.getBody();
    return new Response(
        HttpStatus.valueOf(entity.getStatusCode().value()),
        text == null || text.isBlank() ? null : json.readTree(text));
  }

  Response get(String path) {
    return send(HttpMethod.GET, path, null);
  }

  Response post(String path, Object body) {
    return send(HttpMethod.POST, path, body);
  }

  Response patch(String path, Object body) {
    return send(HttpMethod.PATCH, path, body);
  }

  /** POSTs and asserts the resource was created. */
  JsonNode create(String path, Object body) {
    var response = post(path, body);
    assertEquals(HttpStatus.CREATED, response.status(), () -> "Body: " + response.body());
    return response.body();
  }

  static Map<String, Object> person(String name) {
    return Map.of("name", name, "initialDescription", Map.of("description", name + " described"));
  }

  static Map<String, Object> newGame(String vampire, int people, int resources, int skills) {
    return Map.of(
        "vampire", Map.of("name", vampire, "initialDescription", "A vampire"),
        "people", names("Person", people).stream().map(ApiTestClient::person).toList(),
        "resources",
            names("Resource", resources).stream()
                .map(name -> Map.of("name", name, "stationary", false))
                .toList(),
        "skills", names("Skill", skills).stream().map(name -> Map.of("name", name)).toList());
  }

  private static List<String> names(String prefix, int count) {
    return IntStream.range(0, count).mapToObj(i -> prefix + " " + i).toList();
  }
}
