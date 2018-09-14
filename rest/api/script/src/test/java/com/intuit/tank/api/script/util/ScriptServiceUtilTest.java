package com.intuit.tank.api.script.util;

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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.model.v1.script.StepDataTO;
import com.intuit.tank.api.script.util.ScriptServiceUtil;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;

/**
 * The class <code>ScriptServiceUtilTest</code> contains tests for the class <code>{@link ScriptServiceUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 9:21 PM
 */
public class ScriptServiceUtilTest {
    /**
     * Run the ExternalScript TOToExternalScript(ExternalScriptTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testTOToExternalScript_1()
        throws Exception {
        ExternalScriptTO script = new ExternalScriptTO();
        script.setCreator("");
        script.setName("");
        script.setId(0);
        script.setProductName("");
        script.setScript("");

        ExternalScript result = ScriptServiceUtil.TOToExternalScript(script);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.ExternalScriptTO.setCreator(ExternalScriptTO.java:115)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStepTO copy(ScriptStepTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testCopy_1()
        throws Exception {
        ScriptStepTO step = new ScriptStepTO();
        step.setType("");
        step.setMimetype("");
        step.setScriptGroupName("");
        step.setPayload("");
        step.setLoggingKey("");
        step.setResult("");
        step.setMethod("");
        step.setName("");
        step.setUrl("");
        step.setOnFail("");
        step.setStepIndex(1);
        step.setUuid("");
        step.setLabel("");
        step.setResponse("");

        ScriptStepTO result = ScriptServiceUtil.copy(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.ScriptStepTO.<init>(ScriptStepTO.java:147)
        assertNotNull(result);
    }

    /**
     * Run the StepDataTO copyStepDataTO(StepDataTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testCopyStepDataTO_1()
        throws Exception {
        StepDataTO data = new StepDataTO();
        data.setValue("");
        data.setPhase("");
        data.setType("");
        data.setKey("");

        StepDataTO result = ScriptServiceUtil.copyStepDataTO(data);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.StepDataTO.<init>(StepDataTO.java:37)
        assertNotNull(result);
    }

    /**
     * Run the ExternalScriptTO externalScriptToTO(ExternalScript) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testExternalScriptToTO_1()
        throws Exception {
        ExternalScript script = new ExternalScript();
        script.setProductName("");
        script.setScript("");
        script.setName("");

        ExternalScriptTO result = ScriptServiceUtil.externalScriptToTO(script);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ExternalScript.setProductName(ExternalScript.java:74)
        assertNotNull(result);
    }

    /**
     * Run the StepDataTO requestDataToTransferObject(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testRequestDataToTransferObject_1()
        throws Exception {
        RequestData data = new RequestData("", "", "");

        StepDataTO result = ScriptServiceUtil.requestDataToTransferObject(data);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestDataPhase.<init>(RequestDataPhase.java:24)
        //       at com.intuit.tank.script.RequestDataPhase.<clinit>(RequestDataPhase.java:14)
        //       at com.intuit.tank.project.RequestData.<init>(RequestData.java:21)
        assertNotNull(result);
    }

    /**
     * Run the Script scriptDescriptionToScript(ScriptDescription) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptDescriptionToScript_1()
        throws Exception {
        ScriptDescription sd = new ScriptDescription();
        sd.setProductName("");
        sd.setName("");
        sd.setCreated(new Date());
        sd.setId(new Integer(1));
        sd.setRuntime(1);
        sd.setModified(new Date());
        sd.setComments("");
        sd.setCreator("");

        Script result = ScriptServiceUtil.scriptDescriptionToScript(sd);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.ScriptDescription.<init>(ScriptDescription.java:60)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStepTO scriptStepToTransferObject(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptStepToTransferObject_1()
        throws Exception {
        ScriptStep step = new ScriptStep();
        step.setScriptGroupName("");

        ScriptStepTO result = ScriptServiceUtil.scriptStepToTransferObject(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ScriptStep.setScriptGroupName(ScriptStep.java:38)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStepTO> scriptStepsToTransferobjectList(List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptStepsToTransferobjectList_1()
        throws Exception {
        List<ScriptStep> steps = new LinkedList();

        List<ScriptStepTO> result = ScriptServiceUtil.scriptStepsToTransferobjectList(steps);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.util.ScriptServiceUtil.scriptStepsToTransferobjectList(ScriptServiceUtil.java:64)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStepTO> scriptStepsToTransferobjectList(List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptStepsToTransferobjectList_2()
        throws Exception {
        List<ScriptStep> steps = new LinkedList();

        List<ScriptStepTO> result = ScriptServiceUtil.scriptStepsToTransferobjectList(steps);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.util.ScriptServiceUtil.scriptStepsToTransferobjectList(ScriptServiceUtil.java:64)
        assertNotNull(result);
    }

    /**
     * Run the ScriptDescription scriptToScriptDescription(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptToScriptDescription_1()
        throws Exception {
        Script script = new Script();
        script.setRuntime(1);
        script.setName("");
        script.setProductName("");
        script.setComments("");

        ScriptDescription result = ScriptServiceUtil.scriptToScriptDescription(script);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Script.setRuntime(Script.java:148)
        assertNotNull(result);
    }

    /**
     * Run the ScriptTO scriptToTransferObject(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testScriptToTransferObject_1()
        throws Exception {
        Script script = new Script();
        script.setSteps(new LinkedList());
        script.setRuntime(1);
        script.setName("");
        script.setProductName("");
        script.setComments("");

        ScriptTO result = ScriptServiceUtil.scriptToTransferObject(script);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Script.setSteps(Script.java:206)
        assertNotNull(result);
    }

    /**
     * Run the RequestData transferObjectToRequestData(StepDataTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testTransferObjectToRequestData_1()
        throws Exception {
        StepDataTO to = new StepDataTO();
        to.setValue("");
        to.setPhase("");
        to.setType("");
        to.setKey("");

        RequestData result = ScriptServiceUtil.transferObjectToRequestData(to);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.StepDataTO.<init>(StepDataTO.java:37)
        assertNotNull(result);
    }

    /**
     * Run the Script transferObjectToScript(ScriptTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testTransferObjectToScript_1()
        throws Exception {
        ScriptTO to = new ScriptTO();
        to.setProductName("");
        to.setCreated(new Date());
        to.setRuntime(1);
        to.setModified(new Date());
        to.setName("");
        to.setId(new Integer(1));
        to.setSteps(new LinkedList());
        to.setComments("");
        to.setCreator("");

        Script result = ScriptServiceUtil.transferObjectToScript(to);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.ScriptTO.<init>(ScriptTO.java:68)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep transferObjectToScriptStep(ScriptStepTO) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 9:21 PM
     */
    @Test
    public void testTransferObjectToScriptStep_1()
        throws Exception {
        ScriptStepTO to = new ScriptStepTO();
        to.setMimetype("");
        to.setComments("");
        to.setPayload("");
        to.setLoggingKey("");
        to.setMethod("");
        to.setUuid("");
        to.setHostname("");
        to.setLabel("");
        to.setResponse("");

        ScriptStep result = ScriptServiceUtil.transferObjectToScriptStep(to);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.script.ScriptStepTO.<init>(ScriptStepTO.java:147)
        assertNotNull(result);
    }
}