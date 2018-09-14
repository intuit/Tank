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
import com.intuit.tank.project.ValidationResponseContent;
import com.intuit.tank.script.ResponseContentParser;
import com.intuit.tank.vm.api.enumerated.ValidationType;

/**
 * The class <code>ResponseContentParserTest</code> contains tests for the class <code>{@link ResponseContentParser}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ResponseContentParserTest {
    /**
     * Run the String extractCondition(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractCondition_1()
        throws Exception {
        String conditionStr = "";

        String result = ResponseContentParser.extractCondition(conditionStr);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractCondition(ResponseContentParser.java:49)
        assertNotNull(result);
    }

    /**
     * Run the String extractCondition(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractCondition_2()
        throws Exception {
        String conditionStr = "aaa";

        String result = ResponseContentParser.extractCondition(conditionStr);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractCondition(ResponseContentParser.java:49)
        assertNotNull(result);
    }

    /**
     * Run the String extractCondition(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractCondition_3()
        throws Exception {
        String conditionStr = "aa";

        String result = ResponseContentParser.extractCondition(conditionStr);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractCondition(ResponseContentParser.java:49)
        assertNotNull(result);
    }

    /**
     * Run the String extractCondition(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractCondition_4()
        throws Exception {
        String conditionStr = "aaa";

        String result = ResponseContentParser.extractCondition(conditionStr);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractCondition(ResponseContentParser.java:49)
        assertNotNull(result);
    }


    /**
     * Run the String extractOperator(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractOperator_2()
        throws Exception {
        ValidationResponseContent data = new ValidationResponseContent();
        data.setOperator(ValidationType.contains);

        String result = ResponseContentParser.extractOperator(data);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        //       at com.intuit.tank.project.ValidationResponseContent.<init>(ValidationResponseContent.java:5)
        assertNotNull(result);
    }

    /**
     * Run the String extractOperator(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractOperator_3()
        throws Exception {
        RequestData data = new RequestData("", "", "");

        String result = ResponseContentParser.extractOperator(data);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.script.RequestDataPhase
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the String extractValidateValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractValidateValue_1()
        throws Exception {
        String value = "";

        String result = ResponseContentParser.extractValidateValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractValidateValue(ResponseContentParser.java:34)
        assertNotNull(result);
    }

    /**
     * Run the String extractValidateValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractValidateValue_2()
        throws Exception {
        String value = "aaa";

        String result = ResponseContentParser.extractValidateValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractValidateValue(ResponseContentParser.java:34)
        assertNotNull(result);
    }

    /**
     * Run the String extractValidateValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractValidateValue_3()
        throws Exception {
        String value = "aa";

        String result = ResponseContentParser.extractValidateValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractValidateValue(ResponseContentParser.java:34)
        assertNotNull(result);
    }

    /**
     * Run the String extractValidateValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testExtractValidateValue_4()
        throws Exception {
        String value = "aaa";

        String result = ResponseContentParser.extractValidateValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseContentParser.extractValidateValue(ResponseContentParser.java:34)
        assertNotNull(result);
    }
}