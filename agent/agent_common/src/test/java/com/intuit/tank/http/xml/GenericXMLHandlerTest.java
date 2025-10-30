package com.intuit.tank.http.xml;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GenericXMLHandlerTest {

    private final String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rootElement>
              <childElement1 attribute1="value1">
                Text content for child element 1.
              </childElement1>
              <childElement2>
                <nestedElement>
                  More text content.
                </nestedElement>
              </childElement2>
              <emptyElement/>
            </rootElement>""";

    /**
     * Run the String GetElementAttr(String) method test.
     */
    @Test
    public void testGetElementAttr_1() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        String xPathExpression = "rootElement/childElement1";

        String result = fixture.GetElementAttr(xPathExpression);

        assertNotNull(result);
        assertEquals("value1", result);
    }

    /**
     * Run the String GetElementAttr(String) method test.
     */
    @Test
    public void testGetElementAttr_2() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        String xPathExpression = "testPlan/testSuite";

        String result = fixture.GetElementAttr(xPathExpression);

        assertNotNull(result);
        assertEquals("Suite for XXX5,XXX5", result);
    }

    /**
     * Run the String GetElementText(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetElementText_1()
            throws Exception {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        String xPathExpression = "rootElement/childElement1";

        String result = fixture.GetElementText(xPathExpression);

        assertNotNull(result);
        assertEquals("Text content for child element 1.", result.trim());
    }

    /**
     * Run the String GetElementText(String) method test.
     */
    @Test
    public void testGetElementText_2()
            throws Exception {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        String xPathExpression = "testPlan/testSuite";

        String result = fixture.GetElementText(xPathExpression);

        assertNotNull(result);
        assertEquals("https\n\t\t\t\ttest.com\n\t\t\t\t/index.html", result.trim());
    }

    /**
     * Run the void Save() method test.
     */
    @Test
    public void testSave() {
        File xmlfile = new File("src/test/resources/tt.xml");
        GenericXMLHandler fixture = new GenericXMLHandler(xmlfile);

        fixture.Save();

        assertTrue(xmlfile.exists());
        assertTrue(Instant.now().isAfter(Instant.ofEpochMilli(xmlfile.lastModified())));
    }

    /**
     * Run the void SetElementAttribute(String,String,String) method test.
     */
    @Test
    public void testSetElementAttribute_1()
            throws Exception {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        String xPathExpression = "rootElement/childElement2";
        String attribute = "TEST";
        String value = "TEST";

        fixture.SetElementAttribute(xPathExpression, attribute, value);

        assertEquals("TEST", fixture.GetElementAttr(xPathExpression));
    }

    /**
     * Run the void SetElementAttribute(String,String,String) method test.
     */
    @Test
    public void testSetElementAttribute_2() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        String xPathExpression = "testPlan/testSuite";
        String attribute = "TEST";
        String value = "TEST";

        fixture.SetElementAttribute(xPathExpression, attribute, value);

        assertTrue(Arrays.asList(fixture.GetElementAttr(xPathExpression).split(",")).contains(value));
    }

    /**
     * Run the void SetElementText(String,String) method test.
     */
    @Test
    public void testSetElementText_1() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        String xPathExpression = "rootElement/childElement2";
        String value = "TEST";

        fixture.SetElementText(xPathExpression, value);

        assertEquals(value, fixture.GetElementText(xPathExpression));
    }

    /**
     * Run the void SetElementText(String,String) method test.
     */
    @Test
    public void testSetElementText_2() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        String xPathExpression = "testPlan/testSuite";
        String value = "TEST";

        fixture.SetElementText(xPathExpression, value);

        assertEquals(value, fixture.GetElementText(xPathExpression));
    }

    /**
     * Run the Object clone() method test.
     */
    @Test
    public void testClone_1() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);

        Object result = fixture.clone();

        assertNotNull(result);
        assertTrue(result instanceof GenericXMLHandler);
    }

    /**
     * Run the Object clone() method test.
     */
    @Test
    public void testClone_2() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));

        Object result = fixture.clone();

        assertNotNull(result);
        assertTrue(result instanceof GenericXMLHandler);
    }

    /**
     * Run the String getChildNode(String) method test.
     */
    @Test
    public void testGetChildNode() {
        GenericXMLHandler fixture = new GenericXMLHandler("");
        String xpath = "/TEST/TEST";

        String result = fixture.getChildNode(xpath);

        assertNotNull(result);
        assertEquals("TEST", result);
    }

    /**
     * Run the String getCurrentPath(String,int) method test.
     */
    @Test
    public void testGetCurrentPath()  {
        GenericXMLHandler fixture = new GenericXMLHandler("");
        String xpath = "//TEST/TEST";
        int node = 1;

        String result = fixture.getCurrentPath(xpath, node);

        assertNotNull(result);
        assertEquals("//TEST", result);
    }

    /**
     * Run the String getParentPath(String) method test.
     */
    @Test
    public void testGetParentPath_1()  {
        GenericXMLHandler fixture = new GenericXMLHandler("");
        String xpath = "/rootElement/childElement1";

        String result = fixture.getParentPath(xpath);

        assertNotNull(result);
        assertEquals("/rootElement", result);
    }

    /**
     * Run the boolean isXMLValid() method test.
     */
    @Test
    public void testIsXMLValid_1() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        boolean result = fixture.isXMLValid();
        assertTrue(result);

        GenericXMLHandler fixture2 = new GenericXMLHandler(xml.substring(10, 30));
        boolean result2 = fixture2.isXMLValid();
        assertFalse(result2);
    }

    /**
     * Run the boolean isXMLValid() method test.
     */
    @Test
    public void testIsXMLValid_2() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        boolean result = fixture.isXMLValid();
        assertTrue(result);
    }

    /**
     * Run the void setNamespace(String,String) method test.
     */
    @Test
    public void testSetNamespace() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);
        String name = "TEST";
        String value = "TEST";

        fixture.setNamespace(name, value);

        assertEquals(value, fixture.namespaces.get(name));
    }

    /**
     * Run the String toString() method test.
     */
    @Test
    public void testToString() {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));

        String result = fixture.toString();

        assertNotNull(result);
    }

    /**
     * Run the boolean xPathExists(String) method test.
     */
    @Test
    public void testXPathExists() {
        GenericXMLHandler fixture = new GenericXMLHandler(xml);

        boolean result = fixture.xPathExists("rootElement/childElement1");
        assertTrue(result);

        boolean result2 = fixture.xPathExists("rootElement");
        assertTrue(result2);

        boolean result3 = fixture.xPathExists("BADELEMENT");
        assertFalse(result3);

        boolean result4 = fixture.xPathExists("");
        assertFalse(result4);
    }

    /**
     * Run the boolean xPathExists(String) method test.
     */
    @Test
    public void testXPathExists_2()  {
        GenericXMLHandler fixture = new GenericXMLHandler(new File("src/test/resources/tt.xml"));
        fixture.namespaces = new HashMap();
        String xpathExpr = "testPlan/testSuite";

        boolean result = fixture.xPathExists(xpathExpr);

        assertTrue(result);
    }

}