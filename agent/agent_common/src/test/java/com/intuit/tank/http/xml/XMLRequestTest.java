package com.intuit.tank.http.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class XMLRequestTest {
    /**
     * Run the XMLRequest(Httpnull) constructor test.
     */
    @Test
    public void testXMLRequest_1() {
        XMLRequest result = new XMLRequest(null, null);

        assertNotNull(result);
    }
   
    /**
     * Run the String getKey(String) method test.
     */
    @Test
    public void testGetKey_1() {
        XMLRequest fixture = new XMLRequest(null, null);
        fixture.handler = new GenericXMLHandler();
        String key = "";

        String result = fixture.getKey(key);
        assertNotNull(result);
    }

    @Test
    public void testGetKey_2() {
        XMLRequest fixture = new XMLRequest(null, null);
        fixture.handler = new GenericXMLHandler("<testKey></testKey>");
        fixture.setKey("testKey", "testValue");

        String result = fixture.getKey("testKey");

        assertNotNull(result);
        assertEquals("testValue", result);
    }

    /**
     * Run the void setNamespace(String,String) method test.
     */
    @Test
    public void testSetNamespace_1() {
        XMLRequest fixture = new XMLRequest(null, null);
        fixture.handler = new GenericXMLHandler();
        String name = "";
        String value = "";

        fixture.setNamespace(name, value);
    }
}