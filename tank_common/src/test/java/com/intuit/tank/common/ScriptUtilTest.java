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
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testVariableExtraction(String input, String output) {
        ScriptStep step = buildVariableStep(input);
        String dataFile = ScriptUtil.getDataFileUse(step);
        assertEquals(output, dataFile);
    }

    private ScriptStep buildVariableStep(String input) {
        ScriptStep ret = new ScriptStep();
        ret.setType("variable");
        ret.getData().add(new RequestData("myVar", input, null));
        return ret;
    }

}
