/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.tools.script.LoggingOutputLogger;
import com.intuit.tank.tools.script.OutputLogger;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptStep;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * ScriptFilterUtilTest
 * 
 * @author dangleton
 * 
 */
public class ScriptFilterUtilTest {

    /**
     * Run the ScriptFilterUtil() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testScriptFilterUtil_1()
        throws Exception {
        ScriptFilterUtil result = new ScriptFilterUtil();
        assertNotNull(result);
    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_1()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    @Test
    public void testApplyFilterNoConditions() {
        ScriptFilter filter = new ScriptFilter();
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }


    @ParameterizedTest
    @CsvFileSource(resources = "/testApplyFilterAdd.csv", numLinesToSkip = 1)
    public void testApplyFilterToScript_Add(String scope, String condition, String actionType, String actionScope) {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope(scope);
        scriptFilterCondition.setCondition(condition);
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.valueOf(actionType));
        scriptFilterAction.setScope(actionScope);
        scriptFilterAction.setValue("Success, Failure");
        filter.addAction(scriptFilterAction);
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(true);

        Script script = new Script();
        ScriptStep step = new ScriptStep();
        step.setHostname("localhost");
        step.setSimplePath("/rest/v1");
        HashSet<RequestData> requestDataHashSet = new HashSet<>();
        requestDataHashSet.add(new RequestData("key", "value", "type"));
        step.setQueryStrings(requestDataHashSet);
        step.setPostDatas(requestDataHashSet);
        script.addStep(step);

        AWSXRay.beginSegment("test");
        ScriptFilterUtil.applyFiltersToScript(List.of(filter), script);
        AWSXRay.endSegment();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testApplyFilterRemove.csv", numLinesToSkip = 1)
    public void testApplyFilterToScript_Remove(String scope, String condition, String actionType, String actionScope) {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope(scope);
        scriptFilterCondition.setCondition(condition);
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.valueOf(actionType));
        scriptFilterAction.setScope(actionScope);
        scriptFilterAction.setValue("Success, Failure");
        filter.addAction(scriptFilterAction);
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(true);

        Script script = new Script();
        ScriptStep step = new ScriptStep();
        step.setHostname("localhost");
        step.setSimplePath("/rest/v1");
        HashSet<RequestData> requestDataHashSet = new HashSet<>();
        requestDataHashSet.add(new RequestData("key", "value", "type"));
        step.setQueryStrings(requestDataHashSet);
        step.setPostDatas(requestDataHashSet);
        script.addStep(step);

        AWSXRay.beginSegment("test");
        ScriptFilterUtil.applyFiltersToScript(List.of(filter), script);
        AWSXRay.endSegment();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testApplyFilterReplace.csv", numLinesToSkip = 1)
    public void testApplyFilterToScript_Replace(String scope, String condition, String actionType, String actionScope) {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope(scope);
        scriptFilterCondition.setCondition(condition);
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.valueOf(actionType));
        scriptFilterAction.setScope(actionScope);
        scriptFilterAction.setValue("Success, Failure");
        filter.addAction(scriptFilterAction);
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(true);

        Script script = new Script();
        ScriptStep step = new ScriptStep();
        step.setHostname("localhost");
        step.setSimplePath("/rest/v1");
        HashSet<RequestData> requestDataHashSet = new HashSet<>();
        requestDataHashSet.add(new RequestData("key", "value", "type"));
        step.setQueryStrings(requestDataHashSet);
        step.setPostDatas(requestDataHashSet);
        step.setResponseData(requestDataHashSet);
        script.addStep(step);

        AWSXRay.beginSegment("test");
        ScriptFilterUtil.applyFiltersToScript(List.of(filter), script);
        AWSXRay.endSegment();
    }

    @Test
    public void testApplyFilterNoMatchCondition() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope("Hostname");
        scriptFilterCondition.setCondition("FAILED NO MATCH");
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.replace);
        scriptFilterAction.setScope("Hostaname");
        filter.addAction(scriptFilterAction);
        filter.setAllConditionsMustPass(true);
        ScriptStep step = new ScriptStep();
        step.setHostname("localhost");
        step.setSimplePath("/rest/v1");
        step.setQueryStrings(Set.of(new RequestData("key", "value", "type")));
        step.setPostDatas(Set.of(new RequestData("key", "value", "type")));

        ScriptFilterUtil.applyFilter(filter, List.of(step));
    }

    @Test
    public void testApplyFilterHostnameNull() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope("Hostname");
        scriptFilterCondition.setCondition("Contains");
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.replace);
        scriptFilterAction.setScope("Hostaname");
        filter.addAction(scriptFilterAction);
        filter.setAllConditionsMustPass(true);
        ScriptStep step = new ScriptStep();
        step.setHostname(null);
        step.setSimplePath("/rest/v1");
        step.setQueryStrings(Set.of(new RequestData("key", "value", "type")));
        step.setPostDatas(Set.of(new RequestData("key", "value", "type")));

        ScriptFilterUtil.applyFilter(filter, List.of(step));
    }

    @Test
    public void testApplyFilterBadScope() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition scriptFilterCondition = new ScriptFilterCondition();
        scriptFilterCondition.setScope("FAIL");
        scriptFilterCondition.setCondition("Contains");
        scriptFilterCondition.setValue("value");
        filter.addCondition(scriptFilterCondition);
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setAction(ScriptFilterActionType.replace);
        scriptFilterAction.setScope("FAIL");
        filter.addAction(scriptFilterAction);
        filter.setAllConditionsMustPass(true);
        ScriptStep step = new ScriptStep();
        step.setHostname("localhost");
        step.setSimplePath("/rest/v1");
        step.setQueryStrings(Set.of(new RequestData("key", "value", "type")));
        step.setPostDatas(Set.of(new RequestData("key", "value", "type")));

        assertThrows(IllegalArgumentException.class, () -> {
            ScriptFilterUtil.applyFilter(filter, List.of(step));
        });
    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_7()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setActions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_8()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setActions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_9()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_10()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_11()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_12()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_13()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_14()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_15()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_16()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
        filter.setConditions(new HashSet());
        filter.setAllConditionsMustPass(true);
        List<ScriptStep> steps = new LinkedList();

        ScriptFilterUtil.applyFilter(filter, steps);

    }

    /**
     * Run the List<ScriptStep> applyFilters(Collection<ScriptFilter>,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilters_1()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        List<ScriptStep> steps = new LinkedList();

        List<ScriptStep> result = ScriptFilterUtil.applyFilters(filters, steps);

        assertNotNull(result);
        assertEquals(0, result.size());
    }


    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_1()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_2()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_3()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_4()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_5()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_6()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_7()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_8()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_9()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_10()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_11()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_12()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_13()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_14()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_15()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the void applyFiltersToScript(Collection<ScriptFilter>,Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFiltersToScript_16()
        throws Exception {
        Collection<ScriptFilter> filters = new LinkedList();
        Script script = new Script();
        script.setSteps(new LinkedList());

        ScriptFilterUtil.applyFiltersToScript(filters, script);

    }

    /**
     * Run the boolean filterOnFieldSets(Set<RequestData>,ScriptFilterCondition,ScriptStep,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnFieldSets_2()
        throws Exception {
        Set<RequestData> fields = null;
        ScriptFilterCondition condition = new ScriptFilterCondition();
        ScriptStep currentStep = new ScriptStep();
        List<ScriptStep> steps = new LinkedList();

        boolean result = ScriptFilterUtil.filterOnFieldSets(fields, condition, currentStep, steps);

        assertEquals(false, result);
    }

  
    /**
     * Run the boolean filterOnFieldSets(Set<RequestData>,ScriptFilterCondition,ScriptStep,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnFieldSets_4()
        throws Exception {
        Set<RequestData> fields = new HashSet();
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("Does not contain");
        ScriptStep currentStep = new ScriptStep();
        List<ScriptStep> steps = new LinkedList();

        boolean result = ScriptFilterUtil.filterOnFieldSets(fields, condition, currentStep, steps);

        assertEquals(true, result);
    }

    /**
     * Run the boolean filterOnFieldSets(Set<RequestData>,ScriptFilterCondition,ScriptStep,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnFieldSets_5()
        throws Exception {
        Set<RequestData> fields = new HashSet();
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();
        List<ScriptStep> steps = new LinkedList();

        boolean result = ScriptFilterUtil.filterOnFieldSets(fields, condition, currentStep, steps);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnFieldSets(Set<RequestData>,ScriptFilterCondition,ScriptStep,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnFieldSets_6()
        throws Exception {
        Set<RequestData> fields = new HashSet();
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();
        List<ScriptStep> steps = new LinkedList();

        boolean result = ScriptFilterUtil.filterOnFieldSets(fields, condition, currentStep, steps);

        assertEquals(false, result);
    }

    @Test
    public void testRedactSSN() {
        String logMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"ssn\":\"011220772\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        String redactedLogMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"ssn\":\"{SSN_REDACTED}\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        LoggingOutputLogger redactingLogger = new LoggingOutputLogger();
        assertEquals(redactingLogger.redactSSN(logMessage), redactedLogMessage);
    }

    @Test
    public void testRedactSSN2() {
        String logMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"ssn\":\"011-22-0772\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        String redactedLogMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"ssn\":\"{SSN_REDACTED}\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        LoggingOutputLogger redactingLogger = new LoggingOutputLogger();
        assertEquals(redactingLogger.redactSSN(logMessage), redactedLogMessage);
    }

    @Test
    public void testRedactSSN3() {
        String logMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"social\":\"011220772\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        String redactedLogMessage = "Request Post data KEY fuegoNavInformation -- Value {\"nav\":\"Continue\",\"type\":\"launchTopic\",\"topicId\":\"W2_IMPORT\",\"additionalNavData\":{\"op\":\"EDIT\",\"topic\":\"irsw2\",\"param\":{\"callingTopic\":\"irsw2\",\"firstname\":\"Joe\",\"lastname\":\"REFSAL\",\"SpouseCopyIndPP\":\"false\",\"AddressableName\":\"your\",\"storage\":\"return\",\"isYOYTransfer\":\"true\",\"uuid\":\"85b223de-7d66-4e4a-872e-81270edaaf8c\",\"ssn\":\"{SSN_REDACTED}\",\"ein\":\"\",\"filingStatusCd\":\"1\",\"totalW2Instances\":\"0\"}}}";
        LoggingOutputLogger redactingLogger = new LoggingOutputLogger();
        assertEquals(redactingLogger.redactSSN(logMessage), redactedLogMessage);
    }
}
