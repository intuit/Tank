package com.intuit.tank.http.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * The class <code>XMLRequestTest</code> contains tests for the class <code>{@link XMLRequest}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 4:29 PM
 */
public class XMLRequestTest {
    /**
     * Run the XMLRequest(Httpnull) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testXMLRequest_1()
            throws Exception {
        

        XMLRequest result = new XMLRequest(null, null);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        assertNotNull(result);
    }

   
    /**
     * Run the String getKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        XMLRequest fixture = new XMLRequest(null, null);
        fixture.handler = new GenericXMLHandler();
        String key = "";

        String result = fixture.getKey(key);
        assertNotNull(result);
    }

    /**
     * Run the void setNamespace(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSetNamespace_1()
            throws Exception {
        XMLRequest fixture = new XMLRequest(null, null);
        fixture.handler = new GenericXMLHandler();
        String name = "";
        String value = "";

        fixture.setNamespace(name, value);
    }
}