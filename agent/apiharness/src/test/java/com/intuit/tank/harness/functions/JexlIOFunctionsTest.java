/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.functions;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.test.TestGroups;

/**
 * JexlFileFunctionsTest
 * 
 * @author dangleton
 * 
 */
public class JexlIOFunctionsTest {
//    private Variables variables;
//
//    @SuppressWarnings("unused")
//    @DataProvider(name = "csvData")
//    private Object[][] csvData() {
//        return new Object[][] {
//                { "One", "1", "I" },
//                { "Two", "2", "II" },
//                { "Three", "3", "III" },
//                { "Four", "4", "IV" },
//                { "Five", "5", "V" } };
//    }
//
//    @SuppressWarnings("unused")
//    @DataProvider(name = "csvDataLoop")
//    private Object[][] csvDataLoop() {
//        return new Object[][] {
//                { "One", "1", "I" },
//                { "Two", "2", "II" },
//                { "Three", "3", "III" },
//                { "Four", "4", "IV" },
//                { "Five", "5", "V" },
//                { "One", "1", "I" },
//                { "Two", "2", "II" },
//                { "Three", "3", "III" },
//                { "Four", "4", "IV" },
//                { "Five", "5", "V" }
//        };
//    }
//    
//    private boolean csv1;
//    private boolean csv2;
//    private boolean csv3;
//
//    @BeforeTest
//    public void init() {
//        variables = new Variables();
//    }
//
//    @Test
//    @Tag(TestGroups.FUNCTIONAL)
//    public void testReadDataFile() {
//        String evaluated = variables.evaluate("#{ioFunctions.getFileData('data.txt')}");
//        assertEquals(evaluated, "This is a Data File");
//    }
//
//    @Test
//    @Tag(TestGroups.FUNCTIONAL)
//    public void testReadBinaryDataFile() throws IOException {
//        String evaluated = variables.evaluate("#{ioFunctions.getFileData('32_bit.png')}");
//        byte[] fileBytes = FileUtils.readFileToByteArray(new File("src/test/resources/32_bit.png"));
//        assertEquals(evaluated, new String(fileBytes));
//    }
//
//    @Test
//    @Tag(TestGroups.FUNCTIONAL)
//    public void testReadBinaryData() throws IOException {
//        byte[] ioBytes = new JexlIOFunctions().getFileBytes("32_bit.png");
//        byte[] fileBytes = FileUtils.readFileToByteArray(new File("src/test/resources/32_bit.png"));
//        assertTrue(Arrays.equals(ioBytes, fileBytes));
//    }
//
//    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "csvData")
//    public void testCsv(String name, String num, String romanNum) {
//        if (!csv1) {
//            JexlIOFunctions.resetStatics();
//            csv1 = true;
//        }
//        String evaluated = variables.evaluate("#{ioFunctions.getCSVData()}");
//        assertEquals(evaluated, name);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData(1)}");
//        assertEquals(evaluated, num);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData(2)}");
//        assertEquals(evaluated, romanNum);
//    }
//
//    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "csvData")
//    public void testCsvFileName(String name, String num, String romanNum) {
//        if (!csv2) {
//            JexlIOFunctions.resetStatics();
//            csv2 = true;
//        }
//        String evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvData.csv', 0)}");
//        assertEquals(evaluated, name);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvData.csv', 1)}");
//        assertEquals(evaluated, num);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvData.csv', 2)}");
//        assertEquals(evaluated, romanNum);
//    }
//
//    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "csvDataLoop")
//    public void testCsvFileNameLoop(String name, String num, String romanNum) {
//        if (!csv3) {
//            JexlIOFunctions.resetStatics();
//            csv3 = true;
//        }
//        String evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvDataLoop.csv', 0, true)}");
//        assertEquals(evaluated, name);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvDataLoop.csv', 1, true)}");
//        assertEquals(evaluated, num);
//        evaluated = variables.evaluate("#{ioFunctions.getCSVData('csvDataLoop.csv', 2, true)}");
//        assertEquals(evaluated, romanNum);
//    }

}
