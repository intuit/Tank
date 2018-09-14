package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.script.ExternalScriptTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ExternalScriptTOTest</code> contains tests for the class <code>{@link ExternalScriptTO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ExternalScriptTOTest {
    /**
     * Run the ExternalScriptTO() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testExternalScriptTO_1()
        throws Exception {
        ExternalScriptTO result = new ExternalScriptTO();
        assertNotNull(result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the int getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        int result = fixture.getId();

        assertEquals(1, result);
    }

    /**
     * Run the Date getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        Date result = fixture.getModified();

        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the String getScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetScript_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        String result = fixture.getScript();

        assertEquals("", result);
    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        int id = 1;

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setScript(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetScript_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);
        String script = "";

        fixture.setScript(script);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ExternalScriptTO fixture = new ExternalScriptTO();
        fixture.setScript("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setCreated(new Date());
        fixture.setId(1);

        String result = fixture.toString();

        assertEquals(" (id=1)", result);
    }
}