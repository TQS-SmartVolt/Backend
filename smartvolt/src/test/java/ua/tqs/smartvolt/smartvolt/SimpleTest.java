package ua.tqs.smartvolt.smartvolt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SimpleTest {

  @Test
  @Tag("UnitTest")
  void testAddition() {
    System.out.println("Running SimpleTest.testAddition()");
    assertEquals(2, 1 + 1);
  }
}
