package com.intuit.tank.conversation;

import java.text.DateFormat;
import java.util.Date;

import org.junit.*;

import com.intuit.tank.conversation.Cookie;

import static org.junit.Assert.*;

/**
 * The class <code>CookieTest</code> contains tests for the class <code>{@link Cookie}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class CookieTest {
    /**
     * Run the Cookie() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testCookie_1()
            throws Exception {

        Cookie result = new Cookie();

        assertNotNull(result);
        assertEquals(null, result.getDomain());
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
        assertEquals(null, result.getPath());
        assertEquals(null, result.getMaxAge());
        assertEquals(false, result.isHttpOnly());
        assertEquals(null, result.getExpires());
        assertEquals(false, result.isSecuredOnly());
    }

    /**
     * Run the Cookie(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testCookie_2()
            throws Exception {
        String key = "";
        String value = "";

        Cookie result = new Cookie(key, value);

        assertNotNull(result);
        assertEquals(null, result.getDomain());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
        assertEquals(null, result.getPath());
        assertEquals(null, result.getMaxAge());
        assertEquals(false, result.isHttpOnly());
        assertEquals(null, result.getExpires());
        assertEquals(false, result.isSecuredOnly());
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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        Cookie obj = new Cookie("", "");
        obj.setExpires(new Date());
        obj.setMaxAge("");
        obj.setDomain("");
        obj.setSecuredOnly(true);
        obj.setPath("");
        obj.setHttpOnly(true);

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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        Cookie obj = new Cookie("", "");
        obj.setExpires(new Date());
        obj.setMaxAge("");
        obj.setDomain("");
        obj.setSecuredOnly(true);
        obj.setPath("");
        obj.setHttpOnly(true);

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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        Cookie obj = new Cookie("", "");
        obj.setExpires(new Date());
        obj.setMaxAge("");
        obj.setDomain("");
        obj.setSecuredOnly(true);
        obj.setPath("");
        obj.setHttpOnly(true);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getDomain() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetDomain_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        String result = fixture.getDomain();

        assertEquals("", result);
    }

    /**
     * Run the Date getExpires() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetExpires_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        Date result = fixture.getExpires();

        assertNotNull(result);
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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getMaxAge() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetMaxAge_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        String result = fixture.getMaxAge();

        assertEquals("", result);
    }

    /**
     * Run the String getPath() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetPath_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        String result = fixture.getPath();

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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        int result = fixture.hashCode();

    }

    /**
     * Run the boolean isHttpOnly() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsHttpOnly_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        boolean result = fixture.isHttpOnly();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isHttpOnly() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsHttpOnly_2()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(false);

        boolean result = fixture.isHttpOnly();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isSecuredOnly() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsSecuredOnly_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        boolean result = fixture.isSecuredOnly();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isSecuredOnly() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsSecuredOnly_2()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(false);
        fixture.setPath("");
        fixture.setHttpOnly(true);

        boolean result = fixture.isSecuredOnly();

        assertEquals(false, result);
    }

    /**
     * Run the void setDomain(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetDomain_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        String domain = "";

        fixture.setDomain(domain);

    }

    /**
     * Run the void setExpires(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetExpires_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        Date expires = new Date();

        fixture.setExpires(expires);

    }

    /**
     * Run the void setHttpOnly(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetHttpOnly_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        boolean httpOnly = true;

        fixture.setHttpOnly(httpOnly);

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
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setMaxAge(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetMaxAge_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        String maxAge = "";

        fixture.setMaxAge(maxAge);

    }

    /**
     * Run the void setPath(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetPath_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        String path = "";

        fixture.setPath(path);

    }

    /**
     * Run the void setSecuredOnly(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetSecuredOnly_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        boolean securedOnly = true;

        fixture.setSecuredOnly(securedOnly);

    }

    /**
     * Run the void setValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSetValue_1()
            throws Exception {
        Cookie fixture = new Cookie("", "");
        fixture.setExpires(new Date());
        fixture.setMaxAge("");
        fixture.setDomain("");
        fixture.setSecuredOnly(true);
        fixture.setPath("");
        fixture.setHttpOnly(true);
        String value = "";

        fixture.setValue(value);

    }
}