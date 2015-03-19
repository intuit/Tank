package com.intuit.tank.conversation;

import org.junit.*;

import com.intuit.tank.conversation.Header;

import static org.junit.Assert.*;

/**
 * The class <code>HeaderTest</code> contains tests for the class <code>{@link Header}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class HeaderTest {
    /**
     * Run the Header() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHeader_1()
            throws Exception {

        Header result = new Header();

        assertNotNull(result);
        assertEquals(" : ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the Header(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHeader_2()
            throws Exception {
        String key = "";
        String value = null;

        Header result = new Header(key, value);

        assertNotNull(result);
        assertEquals(" : ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the Header(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHeader_3()
            throws Exception {
        String key = "";
        String value = "";

        Header result = new Header(key, value);

        assertNotNull(result);
        assertEquals(" : ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the Header(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHeader_4()
            throws Exception {
        String key = "";
        String value = null;

        Header result = new Header(key, value);

        assertNotNull(result);
        assertEquals(" : ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the Header(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHeader_5()
            throws Exception {
        String key = "";
        String value = "";

        Header result = new Header(key, value);

        assertNotNull(result);
        assertEquals(" : ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        Header fixture = new Header("", "");
        Object obj = null;

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        Header fixture = new Header("", "");
        Object obj = new Header("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        Header fixture = new Header("", "");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        Header fixture = new Header("", "");
        Object obj = new Header("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testEquals_5()
            throws Exception {
        Header fixture = new Header("", "");
        Object obj = new Header("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        Header fixture = new Header("", "");

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
        Header fixture = new Header("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        Header fixture = new Header("", "");

        int result = fixture.hashCode();

        assertEquals(475, result);
    }

    /**
     * Run the void setKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetKey_1()
            throws Exception {
        Header fixture = new Header("", "");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        Header fixture = new Header("", "");

        String result = fixture.toString();

        assertEquals(" : ", result);
    }
}