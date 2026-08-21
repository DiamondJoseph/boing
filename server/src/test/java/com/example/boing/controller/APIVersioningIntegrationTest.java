package com.example.boing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class APIVersioningIntegrationTest {

  private static final String EXPECTED_HASH = "8b23fbc7540cd4b1d542c446950a09b0";

  @Test
  void testApiVersion() throws IOException, NoSuchAlgorithmException {
    var file = new File("openapi.yaml");
    assertTrue(file.exists(), "Schema does not exist");
    assertTrue(file.canRead(), "Schema is not readable");
    assertTrue(file.isFile(), "Schema has been generated incorrectly");

    var actual = Files.asByteSource(file).hash(Hashing.murmur3_128()).toString();
    assertEquals(EXPECTED_HASH, actual, "Schema has been updated");
  }
}
