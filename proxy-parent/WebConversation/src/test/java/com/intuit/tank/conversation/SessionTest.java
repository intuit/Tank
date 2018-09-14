package com.intuit.tank.conversation;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.conversation.Session;
import com.intuit.tank.conversation.Transaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SessionTest</code> contains tests for the class <code>{@link Session}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class SessionTest {
    /**
     * Run the Session() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSession_1()
            throws Exception {

        Session result = new Session();

        assertNotNull(result);
        assertEquals(true, result.isCollapseRedirects());
    }

    /**
     * Run the Session(List<Transaction>,boolean) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSession_2()
            throws Exception {
        List<Transaction> transactions = new LinkedList();
        boolean collapseRedirects = true;

        Session result = new Session(transactions, collapseRedirects);

        assertNotNull(result);
        assertEquals(true, result.isCollapseRedirects());
    }

    /**
     * Run the List<Transaction> getTransactions() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetTransactions_1()
            throws Exception {
        Session fixture = new Session(new LinkedList(), true);

        List<Transaction> result = fixture.getTransactions();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the boolean isCollapseRedirects() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsCollapseRedirects_1()
            throws Exception {
        Session fixture = new Session(new LinkedList(), true);

        boolean result = fixture.isCollapseRedirects();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isCollapseRedirects() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsCollapseRedirects_2()
            throws Exception {
        Session fixture = new Session(new LinkedList(), false);

        boolean result = fixture.isCollapseRedirects();

        assertEquals(false, result);
    }
}