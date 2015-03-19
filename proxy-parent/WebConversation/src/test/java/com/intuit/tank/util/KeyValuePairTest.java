package com.intuit.tank.util;

import org.junit.*;

import com.intuit.tank.util.KeyValuePair;

import static org.junit.Assert.*;

/**
 * The class <code>KeyValuePairTest</code> contains tests for the class <code>{@link KeyValuePair}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class KeyValuePairTest {
    /**
     * Run the KeyValuePair(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testKeyValuePair_1()
            throws Exception {
        String key = "";

        KeyValuePair result = new KeyValuePair(key);

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the KeyValuePair(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testKeyValuePair_2()
            throws Exception {
        String key = "";
        String value = "";

        KeyValuePair result = new KeyValuePair(key, value);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the String getKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        KeyValuePair fixture = new KeyValuePair("", "");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        KeyValuePair fixture = new KeyValuePair("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }
}