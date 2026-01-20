package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Unit tests for WebSocketAssertion
 */
public class WebSocketAssertionTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testDefaultConstructor() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertNull(assertion.getPattern());
        assertFalse(assertion.isRegex());
        assertNull(assertion.getMinCount());
        assertNull(assertion.getMaxCount());
        assertNull(assertion.getVariable());
        assertNull(assertion.getOccurrence());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPattern() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setPattern("test");
        assertEquals("test", assertion.getPattern());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPatternNull() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertThrows(IllegalArgumentException.class, () -> {
            assertion.setPattern(null);
        });
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPatternEmpty() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertThrows(IllegalArgumentException.class, () -> {
            assertion.setPattern("   ");
        });
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetRegex() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setRegex(true);
        assertTrue(assertion.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetMinCount() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setMinCount(5);
        assertEquals(Integer.valueOf(5), assertion.getMinCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetMaxCount() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setMaxCount(10);
        assertEquals(Integer.valueOf(10), assertion.getMaxCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetVariable() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setVariable("myVar");
        assertEquals("myVar", assertion.getVariable());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetOccurrence() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setOccurrence(SaveOccurrence.FIRST);
        assertEquals(SaveOccurrence.FIRST, assertion.getOccurrence());

        assertion.setOccurrence(SaveOccurrence.LAST);
        assertEquals(SaveOccurrence.LAST, assertion.getOccurrence());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetEffectiveMinCountDefault() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setPattern("test");
        assertEquals(1, assertion.getEffectiveMinCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetEffectiveMinCountExplicit() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setPattern("test");
        assertion.setMinCount(5);
        assertEquals(5, assertion.getEffectiveMinCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetEffectiveMinCountZero() {
        WebSocketAssertion assertion = new WebSocketAssertion();
        assertion.setPattern("test");
        assertion.setMinCount(0);
        assertEquals(0, assertion.getEffectiveMinCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testBuilderBasic() {
        WebSocketAssertion assertion = WebSocketAssertion.builder()
            .pattern("test")
            .build();

        assertEquals("test", assertion.getPattern());
        assertFalse(assertion.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testBuilderFull() {
        WebSocketAssertion assertion = WebSocketAssertion.builder()
            .pattern("\"price\":(\\d+)")
            .regex(true)
            .minCount(1)
            .maxCount(100)
            .variable("lastPrice")
            .occurrence(SaveOccurrence.LAST)
            .build();

        assertEquals("\"price\":(\\d+)", assertion.getPattern());
        assertTrue(assertion.isRegex());
        assertEquals(Integer.valueOf(1), assertion.getMinCount());
        assertEquals(Integer.valueOf(100), assertion.getMaxCount());
        assertEquals("lastPrice", assertion.getVariable());
        assertEquals(SaveOccurrence.LAST, assertion.getOccurrence());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToStringBasic() {
        WebSocketAssertion assertion = WebSocketAssertion.builder()
            .pattern("test")
            .build();

        String str = assertion.toString();
        assertTrue(str.contains("pattern='test'"));
        assertTrue(str.contains("WebSocketAssertion"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToStringFull() {
        WebSocketAssertion assertion = WebSocketAssertion.builder()
            .pattern("test")
            .regex(true)
            .minCount(5)
            .maxCount(10)
            .variable("myVar")
            .occurrence(SaveOccurrence.FIRST)
            .build();

        String str = assertion.toString();
        assertTrue(str.contains("regex"));
        assertTrue(str.contains("min=5"));
        assertTrue(str.contains("max=10"));
        assertTrue(str.contains("save=myVar"));
        assertTrue(str.contains("occurrence="));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testJaxbMarshallUnmarshallExpect() throws Exception {
        WebSocketAssertion original = WebSocketAssertion.builder()
            .pattern("\"status\":\"ok\"")
            .minCount(1)
            .maxCount(10)
            .build();

        String xml = JaxbUtil.marshall(original);
        assertNotNull(xml);

        WebSocketAssertion roundTrip = JaxbUtil.unmarshall(xml, WebSocketAssertion.class);
        assertEquals(original.getPattern(), roundTrip.getPattern());
        assertEquals(original.getMinCount(), roundTrip.getMinCount());
        assertEquals(original.getMaxCount(), roundTrip.getMaxCount());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testJaxbMarshallUnmarshallSave() throws Exception {
        WebSocketAssertion original = WebSocketAssertion.builder()
            .pattern("\"price\":(\\d+)")
            .regex(true)
            .variable("extractedPrice")
            .occurrence(SaveOccurrence.LAST)
            .build();

        String xml = JaxbUtil.marshall(original);
        assertNotNull(xml);

        WebSocketAssertion roundTrip = JaxbUtil.unmarshall(xml, WebSocketAssertion.class);
        assertEquals(original.getPattern(), roundTrip.getPattern());
        assertEquals(original.isRegex(), roundTrip.isRegex());
        assertEquals(original.getVariable(), roundTrip.getVariable());
        assertEquals(original.getOccurrence(), roundTrip.getOccurrence());
    }
}
