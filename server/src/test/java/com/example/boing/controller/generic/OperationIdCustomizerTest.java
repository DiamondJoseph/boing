package com.example.boing.controller.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.boing.controller.GameController;
import com.example.boing.controller.PersonController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OperationIdCustomizerTest {

  @ParameterizedTest
  @CsvSource({
    "Game, Games",
    "Person, People",
    "Diary, Diaries",
    "Memory, Memories",
    "Description, Descriptions",
  })
  void testPlural(String entity, String expected) {
    assertEquals(expected, OperationIdCustomizer.plural(entity));
  }

  @Test
  void testEntityNameComesFromControllerName() {
    assertEquals("Game", OperationIdCustomizer.entityName(GameController.class));
    assertEquals("Person", OperationIdCustomizer.entityName(PersonController.class));
  }

  @Test
  void testControllerNotNamedAfterEntityIsRejected() {
    assertThrows(IllegalStateException.class, () -> OperationIdCustomizer.entityName(String.class));
  }
}
