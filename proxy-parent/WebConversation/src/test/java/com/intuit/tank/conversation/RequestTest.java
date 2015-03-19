package com.intuit.tank.conversation;

import java.util.List;

import org.junit.*;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;

import static org.junit.Assert.*;

/**
 * The class <code>RequestTest</code> contains tests for the class <code>{@link Request}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class RequestTest {
    /**
     * Run the Request() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testRequest_1()
            throws Exception {
        Request result = new Request();
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Request obj = new Request();
        obj.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Request obj = new Request();
        obj.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Request obj = new Request();
        obj.setProtocol(Protocol.http);
        obj.setBody(new byte[] {});
        obj.setFirstLine("");
        obj.addHeader(new Header("", ""));

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        List<Header> result = fixture.getHeaders();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the Protocol getProtocol() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetProtocol_1()
            throws Exception {
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        Protocol result = fixture.getProtocol();

        assertNotNull(result);
        assertEquals("http", result.name());
        assertEquals("http", result.toString());
        assertEquals(0, result.ordinal());
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));

        int result = fixture.hashCode();

        assertEquals(1135, result);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
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
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        String firstLine = "";

        fixture.setFirstLine(firstLine);

    }

    /**
     * Run the void setProtocol(Protocol) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetProtocol_1()
            throws Exception {
        Request fixture = new Request();
        fixture.setProtocol(Protocol.http);
        fixture.setBody(new byte[] {});
        fixture.setFirstLine("");
        fixture.addHeader(new Header("", ""));
        Protocol protocol = Protocol.http;

        fixture.setProtocol(protocol);

    }
}