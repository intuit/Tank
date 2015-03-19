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

import junit.framework.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.test.TestGroups;

public class ScriptUtilTest {

    @DataProvider(name = "csvData")
    private Object[][] testData() {
        return new Object[][] {
                { "#{ioFunctions.getCSVData()}", TankConstants.DEFAULT_CSV_FILE_NAME },
                { "#{ioFunctions.getCSVData(0)}", TankConstants.DEFAULT_CSV_FILE_NAME },
                { "#{ioFunctions.getCSVData( 1 )}", TankConstants.DEFAULT_CSV_FILE_NAME },
                { "#{ioFunctions.getCSVData( 1 , true)}", TankConstants.DEFAULT_CSV_FILE_NAME },
                { "#{ioFunctions.getCSVData('filename.csv')}", "'filename.csv'" },
                { "#{ioFunctions.getCSVData(\"filename.csv\", 1)}", "\"filename.csv\"" },
                { "#{ioFunctions.getCSVData('filename.csv', 1, true)}", "'filename.csv'" },
                { "#{ioFunctions.getCSVData(myVariable)}", "myVariable" }
        };

    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "csvData")
    public void testVariableExtraction(String input, String output) {
        ScriptStep step = buildVariableStep(input);
        String dataFile = ScriptUtil.getDataFileUse(step);
        Assert.assertEquals(output, dataFile);
    }

    private ScriptStep buildVariableStep(String input) {
        ScriptStep ret = new ScriptStep();
        ret.setType("variable");
        ret.getData().add(new RequestData("myVar", input, null));
        return ret;
    }

}
