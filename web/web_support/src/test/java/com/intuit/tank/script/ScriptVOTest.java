package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.script.ScriptVO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptVOTest</code> contains tests for the class <code>{@link ScriptVO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptVOTest {
    /**
     * Run the ScriptVO(int,String,String,String,Date,Date) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptVO_1()
        throws Exception {
        int id = 1;
        String name = "";
        String productName = "";
        String creator = "";
        Date created = new Date();
        Date modified = new Date();

        ScriptVO result = new ScriptVO(id, name, productName, creator, created, modified);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        Date result = fixture.getCreated();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        String result = fixture.getCreator();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the int getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        int result = fixture.getId();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertEquals(1, result);
    }

    /**
     * Run the Date getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        Date result = fixture.getModified();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());

        String result = fixture.getProductName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
        assertNotNull(result);
    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        Date created = new Date();

        fixture.setCreated(created);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        String creator = "";

        fixture.setCreator(creator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }

    /**
     * Run the void setId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        int id = 1;

        fixture.setId(id);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        Date modified = new Date();

        fixture.setModified(modified);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        ScriptVO fixture = new ScriptVO(1, "", "", "", new Date(), new Date());
        String productName = "";

        fixture.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptVO.<init>(ScriptVO.java:16)
    }
}