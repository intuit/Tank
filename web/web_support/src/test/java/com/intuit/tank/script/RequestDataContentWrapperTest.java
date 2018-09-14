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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.RequestDataContentWrapper;

/**
 * The class <code>RequestDataContentWrapperTest</code> contains tests for the class <code>{@link RequestDataContentWrapper}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class RequestDataContentWrapperTest {
    /**
     * Run the RequestDataContentWrapper(RequestData) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testRequestDataContentWrapper_1()
        throws Exception {
        RequestData data = new RequestData("1","2","3");
        RequestDataContentWrapper result = new RequestDataContentWrapper(data);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestDataPhase.<init>(RequestDataPhase.java:24)
        //       at com.intuit.tank.script.RequestDataPhase.<clinit>(RequestDataPhase.java:14)
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the RequestDataContentWrapper(RequestData,boolean) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testRequestDataContentWrapper_2()
        throws Exception {
        RequestData data = new RequestData("", "", "");
        boolean assignemnt = true;

        RequestDataContentWrapper result = new RequestDataContentWrapper(data, assignemnt);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the RequestData getData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetData_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        RequestData result = fixture.getData();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the RequestData getData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetData_2()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("=");
        fixture.setType("");

        RequestData result = fixture.getData();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        String result = fixture.getKey();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the String getOperator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetOperator_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        String result = fixture.getOperator();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        String result = fixture.getType();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        String result = fixture.getValue();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the boolean isPreRequest() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsPreRequest_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        boolean result = fixture.isPreRequest();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertTrue(!result);
    }

    /**
     * Run the boolean isPreRequest() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsPreRequest_2()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");

        boolean result = fixture.isPreRequest();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertTrue(!result);
    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");
        String key = "";

        fixture.setKey(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
    }

    /**
     * Run the void setOperator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetOperator_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");
        String operator = "";

        fixture.setOperator(operator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");
        String type = "";

        fixture.setType(type);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        RequestDataContentWrapper fixture = new RequestDataContentWrapper(new RequestData("", "", ""), true);
        fixture.setKey("");
        fixture.setValue("");
        fixture.setOperator("");
        fixture.setType("");
        String value = "";

        fixture.setValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
    }
}