package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import com.intuit.tank.test.TestGroups;

/**
 * Unit tests for SaveOccurrence enum
 */
public class SaveOccurrenceTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testValues() {
        SaveOccurrence[] values = SaveOccurrence.values();
        assertEquals(2, values.length);
        assertEquals(SaveOccurrence.FIRST, values[0]);
        assertEquals(SaveOccurrence.LAST, values[1]);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetValue() {
        assertEquals("first", SaveOccurrence.FIRST.getValue());
        assertEquals("last", SaveOccurrence.LAST.getValue());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFromValueFirst() {
        assertEquals(SaveOccurrence.FIRST, SaveOccurrence.fromValue("first"));
        assertEquals(SaveOccurrence.FIRST, SaveOccurrence.fromValue("FIRST"));
        assertEquals(SaveOccurrence.FIRST, SaveOccurrence.fromValue("First"));
        assertEquals(SaveOccurrence.FIRST, SaveOccurrence.fromValue("  first  "));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFromValueLast() {
        assertEquals(SaveOccurrence.LAST, SaveOccurrence.fromValue("last"));
        assertEquals(SaveOccurrence.LAST, SaveOccurrence.fromValue("LAST"));
        assertEquals(SaveOccurrence.LAST, SaveOccurrence.fromValue("Last"));
        assertEquals(SaveOccurrence.LAST, SaveOccurrence.fromValue("  last  "));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFromValueNull() {
        // Null defaults to LAST
        assertEquals(SaveOccurrence.LAST, SaveOccurrence.fromValue(null));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFromValueInvalid() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            SaveOccurrence.fromValue("invalid");
        });
        assertTrue(ex.getMessage().contains("Unknown save occurrence"));
        assertTrue(ex.getMessage().contains("first, last"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToString() {
        assertEquals("first", SaveOccurrence.FIRST.toString());
        assertEquals("last", SaveOccurrence.LAST.toString());
    }
}
