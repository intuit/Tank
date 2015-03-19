package com.intuit.tank.conversation;

import java.util.List;

import org.junit.*;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Response;

import static org.junit.Assert.*;

/**
 * The class <code>ResponseTest</code> contains tests for the class <code>{@link Response}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class ResponseTest {
    /**
     * Run the Response() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testResponse_1()
            throws Exception {
        Response result = new Response();
        assertNotNull(result);
    }

    /**
     * Run the void addHeader(Header) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testAddHeader_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Header header = new Header("", "");

        fixture.addHeader(header);

    }

    /**
     * Run the void addHeader(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testAddHeader_2()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        String key = "";
        String value = "";

        fixture.addHeader(key, value);

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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Response obj = new Response();
        obj.setBody(new byte[] {});
        obj.setFirstLine("");
        obj.addHeader(new Header("", ""));

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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Response obj = new Response();
        obj.setBody(new byte[] {});
        obj.setFirstLine("");
        obj.addHeader(new Header("", ""));

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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Response obj = new Response();
        obj.setBody(new byte[] {});
        obj.setFirstLine("");
        obj.addHeader(new Header("", ""));

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String extractContentType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testExtractContentType_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.extractContentType();

        assertEquals("", result);
    }

    /**
     * Run the String extractContentType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testExtractContentType_2()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.extractContentType();

        assertEquals("", result);
    }

    /**
     * Run the String extractContentType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testExtractContentType_3()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.extractContentType();

        assertEquals("", result);
    }

    /**
     * Run the byte[] getBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetBody_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        byte[] result = fixture.getBody();

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the String getBodyAsString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetBodyAsString_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.getBodyAsString();

        assertEquals("", result);
    }

    /**
     * Run the String getBodyAsString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetBodyAsString_2()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.getBodyAsString();

        assertEquals("", result);
    }

    /**
     * Run the String getFirstLine() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetFirstLine_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        String result = fixture.getFirstLine();

        assertEquals("", result);
    }

    /**
     * Run the List<Header> getHeaders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetHeaders_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        List<Header> result = fixture.getHeaders();

        assertNotNull(result);
        assertEquals(1, result.size());
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
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        int result = fixture.hashCode();

        assertEquals(981, result);
    }

    /**
     * Run the void setBody(byte[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetBody_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        byte[] body = new byte[] {};

        fixture.setBody(body);

    }

    /**
     * Run the void setFirstLine(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetFirstLine_1()
            throws Exception {
        Response fixture = new Response();
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        String firstLine = "";

        fixture.setFirstLine(firstLine);

    }
}