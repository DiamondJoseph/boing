package com.example.boing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Writing a memory into a diary means the vampire no longer holds it in their head. */
class DiaryTest {

  private static Memory memory(String response) {
    return new Memory(new Experience(new Prompt("A prompt"), response));
  }

  @Test
  void testNewMemoryIsRemembered() {
    assertTrue(memory("First").isRemembered());
  }

  @Test
  void testInitialMemoriesAreNoLongerRemembered() {
    var first = memory("First");
    var second = memory("Second");

    var diary = new Diary("A diary", List.of(first, second));

    assertEquals(List.of(first, second), diary.getMemories());
    assertFalse(first.isRemembered());
    assertFalse(second.isRemembered());
  }

  @Test
  void testAddedMemoryIsNoLongerRemembered() {
    var diary = new Diary("A diary", List.of());
    var added = memory("Added");

    diary.addMemory(added);

    assertEquals(List.of(added), diary.getMemories());
    assertFalse(added.isRemembered());
  }

  @Test
  void testInitialMemoriesListIsCopied() {
    // The diary must own a modifiable list, even when given an immutable one
    var diary = new Diary("A diary", List.of(memory("First")));

    diary.addMemory(memory("Second"));

    assertEquals(2, diary.getMemories().size());
  }

  @Test
  void testNewDiaryIsNotLost() {
    assertFalse(new Diary("A diary", List.of()).isLost());
  }
}
