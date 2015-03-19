package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.vm.settings.MailMessage;

import static org.junit.Assert.*;

/**
 * The class <code>MailMessageCpTest</code> contains tests for the class <code>{@link MailMessage}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class MailMessageCpTest {
    /**
     * Run the MailMessage(String,String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testMailMessage_1()
            throws Exception {
        String body = "";
        String subject = "";
        String cssStyle = "";

        MailMessage result = new MailMessage(body, subject, cssStyle);

        assertNotNull(result);
        assertEquals("", result.getBody());
        assertEquals("", result.getSubject());
        assertEquals("", result.getPlainTextBody());
        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>\n<body>\n</body>\n</html>\n",
                result.getHtmlBody());
        assertEquals("", result.getCssStyle());
    }

    /**
     * Run the String getBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetBody_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getBody();

        assertEquals("", result);
    }

    /**
     * Run the String getCssStyle() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetCssStyle_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getCssStyle();

        assertEquals("", result);
    }

    /**
     * Run the String getHtmlBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetHtmlBody_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getHtmlBody();

        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>\n<body>\n</body>\n</html>\n",
                result);
    }

    /**
     * Run the String getHtmlBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetHtmlBody_2()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getHtmlBody();

        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>\n<body>\n</body>\n</html>\n",
                result);
    }

    /**
     * Run the String getPlainTextBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetPlainTextBody_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getPlainTextBody();

        assertEquals("", result);
    }

    /**
     * Run the String getSubject() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetSubject_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.getSubject();

        assertEquals("", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        MailMessage fixture = new MailMessage("", "", "");

        String result = fixture.toString();

        assertNotNull(result);
    }
}