package com.intuit.tank.common;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptUtilTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("#{ioFunctions.getCSVData()}", TankConstants.DEFAULT_CSV_FILE_NAME ),
                Arguments.of("#{ioFunctions.getCSVData(0)}", TankConstants.DEFAULT_CSV_FILE_NAME ),
                Arguments.of("#{ioFunctions.getCSVData( 1 )}", TankConstants.DEFAULT_CSV_FILE_NAME ),
                Arguments.of("#{ioFunctions.getCSVData( 1 , true)}", TankConstants.DEFAULT_CSV_FILE_NAME ),
                Arguments.of("#{ioFunctions.getCSVData('filename.csv')}", "'filename.csv'" ),
                Arguments.of("#{ioFunctions.getCSVData(\"filename.csv\", 1)}", "\"filename.csv\"" ),
                Arguments.of("#{ioFunctions.getCSVData('filename.csv', 1, true)}", "'filename.csv'" ),
                Arguments.of("#{ioFunctions.getCSVData(myVariable)}", "myVariable" )
        );
    }

    static Stream<Arguments> scriptType() {
        return Stream.of(
                Arguments.of(ScriptConstants.REQUEST, "Request Name", "key", "value"),
                Arguments.of(ScriptConstants.VARIABLE, "Variable Name", "key", "value" ),
                Arguments.of(ScriptConstants.AUTHENTICATION, "Authentication Name", ScriptConstants.AUTH_SCHEME, "value" ),
                Arguments.of(ScriptConstants.AUTHENTICATION, "Authentication Name", ScriptConstants.AUTH_HOST, "value" ),
                Arguments.of(ScriptConstants.AUTHENTICATION, "Authentication Name", ScriptConstants.AUTH_USER_NAME, "value" ),
                Arguments.of(ScriptConstants.THINK_TIME, "ThinkTime Name", "min", "10" ),
                Arguments.of(ScriptConstants.THINK_TIME, "ThinkTime Name", "@min", "20" ),
                Arguments.of(ScriptConstants.THINK_TIME, "ThinkTime Name", "max", "90" ),
                Arguments.of(ScriptConstants.THINK_TIME, "ThinkTime Name", "@max", "100" ),
                Arguments.of(ScriptConstants.LOGIC, "Logic Name", "key", "value" ),
                Arguments.of(ScriptConstants.COOKIE, "Cookie Name", ScriptConstants.COOKIE_NAME, "value" ),
                Arguments.of(ScriptConstants.COOKIE, "Cookie Name", ScriptConstants.COOKIE_VALUE, "value" ),
                Arguments.of(ScriptConstants.SLEEP, "Clear Name", ScriptConstants.TIME, "value" ),
                Arguments.of(ScriptConstants.CLEAR, "Clear Name", "key", "value" ),
                Arguments.of(ScriptConstants.TIMER, "Time Name", ScriptConstants.IS_START, "value" ),
                Arguments.of(ScriptConstants.TIMER, "Time Name", ScriptConstants.LOGGING_KEY, "value" )
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testVariableExtraction(String input, String output) {
        ScriptStep step = buildVariableStep(input);
        String dataFile = ScriptUtil.getDataFileUse(step);
        assertEquals(output, dataFile);
    }

    private ScriptStep buildVariableStep(String input) {
        return ScriptStep.builder()
                .type(ScriptConstants.VARIABLE)
                .addDataElement(new RequestData("myVar", input, null))
                .build();
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("scriptType")
    public void testCopyScriptStep(String scriptType, String name, String key, String value) {
        ScriptStep scriptStep = ScriptStep.builder()
                .type(scriptType)
                .name(name)
                .addDataElement(new RequestData(key, value, null))
                .build();
        assertEquals(name, ScriptUtil.copyScriptStep(scriptStep).getName());
    }

    @Test
    public void testUsedVariables() {
        ScriptStep scriptStep = ScriptStep.builder()
                .type(ScriptConstants.REQUEST)
                .name("Request Name")
                .hostname("localhost")
                .addRequestheader(new RequestData("@key", "#{value}", null))
                .addRequestCooky(new RequestData("#{key}", "@value", null))
                .build();
        Set<String> result = ScriptUtil.getUsedVariables(scriptStep);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("scriptType")
    public void testCalculateStepDuration(String scriptType, String name, String key, String value) {
        ScriptStep scriptStep = ScriptStep.builder()
                .type(scriptType)
                .name(name)
                .addDataElement(new RequestData(key, value, null))
                .build();
        long result = ScriptUtil.calculateStepDuration(scriptStep, new HashMap<>(), new TankConfig());
        assertTrue(0 <= result && result < 100);
    }

    @Test
    public void testCopyScript() {
        List<ScriptStep> steps = new LinkedList<>();
        ScriptStep scriptStep = ScriptStep.builder()
                .type(ScriptConstants.REQUEST)
                .name("Request Name")
                .build();
        steps.add(scriptStep);
        Script script = Script.builder()
                .comments("testing...")
                .creator("tester")
                .name("Script Name")
                .productName("Tank")
                .runtime(100)
                .steps(steps)
                .build();
        Script newScript = ScriptUtil.copyScript("New Creator", "New Name", script);
        assertNotNull(newScript);
        assertEquals("New Creator", newScript.getCreator());
        assertEquals("New Name", newScript.getName());
        assertEquals("testing...", newScript.getComments());
        assertFalse(newScript.getScriptSteps().isEmpty());
        assertEquals(1, newScript.getScriptSteps().size());
    }
}
