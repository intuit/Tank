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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.util.ScriptFilterUtil;

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

    /**
     * Run the void applyFilter(ScriptFilter,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testApplyFilter_2()
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
    public void testApplyFilter_3()
        throws Exception {
        ScriptFilter filter = new ScriptFilter();
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
    public void testApplyFilter_4()
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
    public void testApplyFilter_5()
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
    public void testApplyFilter_6()
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
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_1()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_2()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_3()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_4()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_5()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_6()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the ScriptStep doAction(Set<ScriptFilterAction>,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testDoAction_7()
        throws Exception {
        Set<ScriptFilterAction> actions = new HashSet();
        ScriptStep step = new ScriptStep();

        ScriptStep result = ScriptFilterUtil.doAction(actions, step);

        assertEquals(null, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_1()
        throws Exception {
        String filterField = null;
        ScriptFilterCondition condition = new ScriptFilterCondition();
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_2()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_3()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_4()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_5()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_6()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_7()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_8()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_9()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_10()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_11()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_12()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_13()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

        assertEquals(false, result);
    }

    /**
     * Run the boolean filterOnField(String,ScriptFilterCondition,ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testFilterOnField_14()
        throws Exception {
        String filterField = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setCondition("");
        condition.setValue("");
        ScriptStep currentStep = new ScriptStep();

        boolean result = ScriptFilterUtil.filterOnField(filterField, condition, currentStep);

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

  

    /**
     * Run the boolean testConditions(String,ScriptFilterCondition,ScriptStep,List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test()
    public void testTestConditions_9()
        throws Exception {
        String scope = "";
        ScriptFilterCondition condition = new ScriptFilterCondition();
        ScriptStep step = new ScriptStep();
        List<ScriptStep> steps = new LinkedList();

        assertThrows(java.lang.IllegalArgumentException.class, () -> ScriptFilterUtil.testConditions(scope, condition, step, steps));
    }
}
