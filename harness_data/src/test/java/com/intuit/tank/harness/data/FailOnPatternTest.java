package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Unit tests for FailOnPattern
 */
public class FailOnPatternTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testDefaultConstructor() {
        FailOnPattern pattern = new FailOnPattern();
        assertNull(pattern.getPattern());
        assertFalse(pattern.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSingleArgConstructor() {
        FailOnPattern pattern = new FailOnPattern("error");
        assertEquals("error", pattern.getPattern());
        assertFalse(pattern.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testTwoArgConstructor() {
        FailOnPattern pattern = new FailOnPattern("\"code\":5\\d\\d", true);
        assertEquals("\"code\":5\\d\\d", pattern.getPattern());
        assertTrue(pattern.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPattern() {
        FailOnPattern pattern = new FailOnPattern();
        pattern.setPattern("test");
        assertEquals("test", pattern.getPattern());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPatternNull() {
        // P1 #23: setPattern is now a pure setter (no validation) for JAXB compatibility.
        // Null is accepted without throwing.
        FailOnPattern pattern = new FailOnPattern();
        pattern.setPattern(null);
        assertNull(pattern.getPattern());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetPatternEmpty() {
        // P1 #23: setPattern is now a pure setter (no validation) for JAXB compatibility.
        // Empty/blank is accepted without throwing.
        FailOnPattern pattern = new FailOnPattern();
        pattern.setPattern("   ");
        assertEquals("   ", pattern.getPattern());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testValidateRejectsNullPattern() {
        FailOnPattern pattern = new FailOnPattern();
        assertFalse(pattern.validate(), "validate() should return false for null pattern");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testValidateRejectsEmptyPattern() {
        FailOnPattern pattern = new FailOnPattern();
        pattern.setPattern("   ");
        assertFalse(pattern.validate(), "validate() should return false for blank pattern");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testValidateAcceptsValidPattern() {
        FailOnPattern pattern = new FailOnPattern("error");
        assertTrue(pattern.validate(), "validate() should return true for valid pattern");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetRegex() {
        FailOnPattern pattern = new FailOnPattern();
        pattern.setRegex(true);
        assertTrue(pattern.isRegex());
        pattern.setRegex(false);
        assertFalse(pattern.isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testToString() {
        FailOnPattern pattern1 = new FailOnPattern("error", false);
        assertTrue(pattern1.toString().contains("error"));
        assertFalse(pattern1.toString().contains("regex"));

        FailOnPattern pattern2 = new FailOnPattern("\\d+", true);
        assertTrue(pattern2.toString().contains("\\d+"));
        assertTrue(pattern2.toString().contains("regex"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testJaxbMarshallUnmarshall() throws Exception {
        FailOnPattern original = new FailOnPattern("\"error\":true", true);

        String xml = JaxbUtil.marshall(original);
        assertNotNull(xml);
        assertTrue(xml.contains("pattern"));
        assertTrue(xml.contains("regex"));

        FailOnPattern roundTrip = JaxbUtil.unmarshall(xml, FailOnPattern.class);
        assertEquals(original.getPattern(), roundTrip.getPattern());
        assertEquals(original.isRegex(), roundTrip.isRegex());
    }
}
