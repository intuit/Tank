package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Unit tests for AssertionBlock
 */
public class AssertionBlockTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testDefaultConstructor() {
        AssertionBlock block = new AssertionBlock();
        assertTrue(block.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetExpectsInitializesEmpty() {
        AssertionBlock block = new AssertionBlock();
        assertNotNull(block.getExpects());
        assertTrue(block.getExpects().isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetSavesInitializesEmpty() {
        AssertionBlock block = new AssertionBlock();
        assertNotNull(block.getSaves());
        assertTrue(block.getSaves().isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetExpects() {
        AssertionBlock block = new AssertionBlock();

        java.util.List<WebSocketAssertion> expects = new java.util.ArrayList<>();
        expects.add(WebSocketAssertion.builder().pattern("test").build());

        block.setExpects(expects);
        assertEquals(1, block.getExpects().size());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetSaves() {
        AssertionBlock block = new AssertionBlock();

        java.util.List<WebSocketAssertion> saves = new java.util.ArrayList<>();
        saves.add(WebSocketAssertion.builder().pattern("test").variable("v").build());

        block.setSaves(saves);
        assertEquals(1, block.getSaves().size());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsEmptyWithExpects() {
        AssertionBlock block = new AssertionBlock();
        block.getExpects().add(WebSocketAssertion.builder().pattern("test").build());

        assertFalse(block.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsEmptyWithSaves() {
        AssertionBlock block = new AssertionBlock();
        block.getSaves().add(WebSocketAssertion.builder().pattern("test").variable("v").build());

        assertFalse(block.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsEmptyWithBoth() {
        AssertionBlock block = new AssertionBlock();
        block.getExpects().add(WebSocketAssertion.builder().pattern("e").build());
        block.getSaves().add(WebSocketAssertion.builder().pattern("s").variable("v").build());

        assertFalse(block.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsEmptyWithNullLists() {
        AssertionBlock block = new AssertionBlock();
        block.setExpects(null);
        block.setSaves(null);

        assertTrue(block.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToString() {
        AssertionBlock block = new AssertionBlock();
        block.getExpects().add(WebSocketAssertion.builder().pattern("e1").build());
        block.getExpects().add(WebSocketAssertion.builder().pattern("e2").build());
        block.getSaves().add(WebSocketAssertion.builder().pattern("s").variable("v").build());

        String str = block.toString();
        assertTrue(str.contains("expects=2"));
        assertTrue(str.contains("saves=1"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToStringEmpty() {
        AssertionBlock block = new AssertionBlock();
        String str = block.toString();
        assertTrue(str.contains("expects=0"));
        assertTrue(str.contains("saves=0"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testJaxbMarshallUnmarshall() throws Exception {
        AssertionBlock original = new AssertionBlock();
        original.getExpects().add(WebSocketAssertion.builder()
            .pattern("Echo:")
            .minCount(2)
            .build());
        original.getExpects().add(WebSocketAssertion.builder()
            .pattern("\"status\":\"ok\"")
            .build());
        original.getSaves().add(WebSocketAssertion.builder()
            .pattern("\"id\":(\\d+)")
            .regex(true)
            .variable("lastId")
            .occurrence(SaveOccurrence.LAST)
            .build());

        String xml = JaxbUtil.marshall(original);
        assertNotNull(xml);
        assertTrue(xml.contains("expect"));
        assertTrue(xml.contains("save"));

        AssertionBlock roundTrip = JaxbUtil.unmarshall(xml, AssertionBlock.class);
        assertEquals(2, roundTrip.getExpects().size());
        assertEquals(1, roundTrip.getSaves().size());
        assertEquals("Echo:", roundTrip.getExpects().get(0).getPattern());
        assertEquals("lastId", roundTrip.getSaves().get(0).getVariable());
    }
}
