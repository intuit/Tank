package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobNotification;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.vmManager.Recipient;

/**
 * The class <code>JobNotificationTest</code> contains tests for the class <code>{@link JobNotification}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobNotificationTest {
    /**
     * Run the JobNotification() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobNotification_1()
        throws Exception {
        JobNotification result = new JobNotification();
        assertNotNull(result);
    }

    /**
     * Run the void copyTransient() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCopyTransient_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        fixture.copyTransient();

    }

    /**
     * Run the void copyTransient() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCopyTransient_2()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        fixture.copyTransient();

    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        Object obj = new JobNotification();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        Object obj = new JobNotification();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getBody() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetBody_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        String result = fixture.getBody();

        assertEquals("", result);
    }

    /**
     * Run the List<JobLifecycleEvent> getLifecycleEvents() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLifecycleEvents_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        List<JobLifecycleEvent> result = fixture.getLifecycleEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getRecipientList() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRecipientList_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        String result = fixture.getRecipientList();

        assertEquals("", result);
    }

    /**
     * Run the Set<? extends Recipient> getRecipients() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRecipients_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        Set<? extends Recipient> result = fixture.getRecipients();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the Set<? extends Recipient> getRecipients() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRecipients_2()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        Set<? extends Recipient> result = fixture.getRecipients();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the Set<? extends Recipient> getRecipients() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRecipients_3()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList((String) null);
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        Set<? extends Recipient> result = fixture.getRecipients();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getSubject() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSubject_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        String result = fixture.getSubject();

        assertEquals("", result);
    }

    /**
     * Run the List<JobLifecycleEvent> getTransientLifecycleEvents() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTransientLifecycleEvents_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(null);

        List<JobLifecycleEvent> result = fixture.getTransientLifecycleEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<JobLifecycleEvent> getTransientLifecycleEvents() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTransientLifecycleEvents_2()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        List<JobLifecycleEvent> result = fixture.getTransientLifecycleEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<JobLifecycleEvent> getTransientLifecycleEvents() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTransientLifecycleEvents_3()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        List<JobLifecycleEvent> result = fixture.getTransientLifecycleEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void initTransient() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testInitTransient_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        fixture.initTransient();

    }

    /**
     * Run the void setBody(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetBody_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        String body = "";

        fixture.setBody(body);

    }

    /**
     * Run the void setLifecycleEvents(List<JobLifecycleEvent>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLifecycleEvents_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        List<JobLifecycleEvent> lifecycleEvents = new LinkedList();

        fixture.setLifecycleEvents(lifecycleEvents);

    }

    /**
     * Run the void setRecipientList(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRecipientList_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        String recipientList = "";

        fixture.setRecipientList(recipientList);

    }

    /**
     * Run the void setSubject(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSubject_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        String subject = "";

        fixture.setSubject(subject);

    }

    /**
     * Run the void setTransientLifecycleEvents(List<JobLifecycleEvent>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTransientLifecycleEvents_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());
        List<JobLifecycleEvent> transientLifecycleEvents = new LinkedList();

        fixture.setTransientLifecycleEvents(transientLifecycleEvents);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        JobNotification fixture = new JobNotification();
        fixture.setRecipientList("");
        fixture.setLifecycleEvents(new LinkedList());
        fixture.setSubject("");
        fixture.setBody("");
        fixture.setTransientLifecycleEvents(new LinkedList());

        String result = fixture.toString();

    }
}