package com.example.boing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RandomSerializationTest {

  /** Ensure that the Random backing is determistic wherever this is run. */
  @Test
  void testRandomWithSameSeedSameOrder() {
    var seed = System.currentTimeMillis();
    var left = new Random(seed);
    var right = new Random(seed);
    for (int i = 0; i < 10; i++) {
      assertEquals(left.nextLong(), right.nextLong());
    }
  }

  @Test
  void testRandomSameAfterSerializing() throws IOException, ClassNotFoundException {
    var seed = System.currentTimeMillis();
    var left = new Random(seed);
    var right = serializeAndDeserialize(left, Random.class);
    for (int i = 0; i < 10; i++) {
      assertEquals(left.nextLong(), right.nextLong());
    }
  }

  @Test
  void testRandomSeedContinuesAfterSerializing() throws IOException, ClassNotFoundException {
    var seed = System.currentTimeMillis();
    var left = new Random(seed);
    for (int i = 0; i < 10; i++) {
      left.nextLong();
    }
    var right = serializeAndDeserialize(left, Random.class);
    for (int i = 0; i < 10; i++) {
      assertEquals(left.nextLong(), right.nextLong());
    }
  }

  <T> T serializeAndDeserialize(T object, Class<T> clazz)
      throws ClassNotFoundException, IOException {
    try (var baos = new ByteArrayOutputStream();
        var oout = new ObjectOutputStream(baos); ) {
      oout.writeObject(object);
      try (var bais = new ByteArrayInputStream(baos.toByteArray());
          var oin = new ObjectInputStream(bais)) {
        return clazz.cast(oin.readObject());
      }
    }
  }
}
