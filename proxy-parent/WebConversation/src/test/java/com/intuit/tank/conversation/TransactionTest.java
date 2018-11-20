package com.intuit.tank.conversation;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.conversation.Transaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>TransactionTest</code> contains tests for the class <code>{@link Transaction}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class TransactionTest {
    /**
     * Run the Transaction() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testTransaction_1()
            throws Exception {
        Transaction result = new Transaction();
        assertNotNull(result);
    }

    /**
     * Run the String buildHeaderString(String,List<Header>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testBuildHeaderString_1()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());
        String prefix = "";
        List<Header> headers = new LinkedList();

        String result = fixture.buildHeaderString(prefix, headers);

        assertEquals("", result);
    }

    /**
     * Run the String buildHeaderString(String,List<Header>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testBuildHeaderString_2()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());
        String prefix = "";
        List<Header> headers = new LinkedList();

        String result = fixture.buildHeaderString(prefix, headers);

        assertEquals("", result);
    }

    /**
     * Run the Request getRequest() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetRequest_1()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());

        Request result = fixture.getRequest();

        assertNotNull(result);
        assertEquals(null, result.getProtocol());
        assertEquals(null, result.getBody());
        assertEquals(null, result.getFirstLine());
        assertEquals(null, result.getBodyAsString());
    }

    /**
     * Run the Response getResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetResponse_1()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());

        Response result = fixture.getResponse();

        assertNotNull(result);
        assertEquals(null, result.getBody());
        assertEquals(null, result.getFirstLine());
        assertEquals(null, result.getBodyAsString());
        assertEquals("", result.extractContentType());
    }

    /**
     * Run the void setRequest(Request) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetRequest_1()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());
        Request request = new Request();

        fixture.setRequest(request);

    }

    /**
     * Run the void setResponse(Response) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetResponse_1()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest(new Request());
        Response response = new Response();

        fixture.setResponse(response);

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
        Transaction fixture = new Transaction();
        fixture.setResponse(new Response());
        fixture.setRequest((Request) null);

        String result = fixture.toString();

        assertEquals("------------ Response ------------\n    null\n\n------------ Body ------------\nnull\n\n", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testToString_2()
            throws Exception {
        Transaction fixture = new Transaction();
        fixture.setResponse((Response) null);
        fixture.setRequest(new Request());

        String result = fixture.toString();

        assertEquals("------------ Request ------------\n    null\n\n------------ Body ------------\nnull\n\n", result);
    }
}