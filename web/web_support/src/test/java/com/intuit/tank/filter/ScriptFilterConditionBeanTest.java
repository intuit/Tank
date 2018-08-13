package com.intuit.tank.filter;

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

import com.intuit.tank.filter.ConditionMatch;
import com.intuit.tank.filter.ConditionScope;
import com.intuit.tank.filter.ScriptFilterConditionBean;
import com.intuit.tank.project.ScriptFilterCondition;

/**
 * The class <code>ScriptFilterConditionBeanTest</code> contains tests for the class <code>{@link ScriptFilterConditionBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ScriptFilterConditionBeanTest {
    /**
     * Run the ScriptFilterConditionBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testScriptFilterConditionBean_1()
        throws Exception {
        ScriptFilterConditionBean result = new ScriptFilterConditionBean();
        assertNotNull(result);
    }

    /**
     * Run the void done() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDone_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        fixture.done();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }

    /**
     * Run the void done() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDone_2()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        fixture.done();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }

    /**
     * Run the void editCondition(ScriptFilterCondition) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEditCondition_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        condition.setScope("");

        fixture.editCondition(condition);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ScriptFilterCondition.setCondition(ScriptFilterCondition.java:67)
    }

    /**
     * Run the ConditionScope[] getConditionScopeValues() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetConditionScopeValues_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        ConditionScope[] result = fixture.getConditionScopeValues();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
        assertNotNull(result);
    }

    /**
     * Run the String getConditionStr() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetConditionStr_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        String result = fixture.getConditionStr();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
        assertNotNull(result);
    }

    /**
     * Run the ConditionMatch[] getConditionValues() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetConditionValues_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        ConditionMatch[] result = fixture.getConditionValues();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
        assertNotNull(result);
    }

    /**
     * Run the String getScope() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetScope_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        String result = fixture.getScope();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        String result = fixture.getValue();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
        assertNotNull(result);
    }

    /**
     * Run the void setConditionStr(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetConditionStr_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");
        String conditionStr = "";

        fixture.setConditionStr(conditionStr);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }

    /**
     * Run the void setScope(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScope_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");
        String scope = "";

        fixture.setScope(scope);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");
        String value = "";

        fixture.setValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }

    /**
     * Run the void setupNewCondition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetupNewCondition_1()
        throws Exception {
        ScriptFilterConditionBean fixture = new ScriptFilterConditionBean();
        fixture.setScope("");
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setCondition("");
        scriptFilterCondition.setValue("");
        scriptFilterCondition.setScope("");
        fixture.editCondition(scriptFilterCondition);
        fixture.setValue("");
        fixture.setConditionStr("");

        fixture.setupNewCondition();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.ScriptFilterConditionBean.setScope(ScriptFilterConditionBean.java:74)
    }
}