package com.intuit.tank.http.json;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.Test;

public class JsonResponseTest {

    static {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    /**
     * Run the JsonResponse() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testJsonResponse_1()
            throws Exception {

        JsonResponse result = new JsonResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
        assertNotNull(result);
    }

    /**
     * Run the JsonResponse(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testJsonResponse_2()
            throws Exception {
        String resp = "";

        JsonResponse result = new JsonResponse(resp);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_2()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_3()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_4()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_5()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the void setResponseBody(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetResponseBody_1()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String body = "";

        fixture.setResponseBody(body);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
    }

    /**
     * Run the void setResponseBody(byte[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetResponseBody_2()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        byte[] byteArray = new byte[] {};

        fixture.setResponseBody(byteArray);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
    }
    
    @Test
    public void testJsonResponseBody() throws Exception{
    	
    	JsonResponse fixture = new JsonResponse();
    	fixture.setResponseBody(readFile("src/test/resources/json-response.json"));

        Stream<Pair<String, String>> keys = Stream.of(
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/FieldAttributes/UUID", "de7f702f-e40b-4f16-8a7d-e4263f11421d"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/UUID","9adb0166-41bf-4a67-84f5-01bf5701c624"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/IRS1040/DependentWorksheetPP[1]/UUID","66f978bf-0d16-4763-83a2-5c0b4fa6e213"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPReturnInformation/ForeignAddressPP/UUID","16824515-d8f2-4166-a6b3-f6f16d8fd64e"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnHeader/Filer/SpouseFullNamePP/UUID","aef7336e-060a-4bc9-b0a0-d9a2d0af2e83"),
                new ImmutablePair<>("/data/data/returns/IRS1040/UUID","1b183386-08e6-4df1-bf03-2429c30d2967"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/UUID","9883fbf9-167a-4068-98e3-8b1c4e71c8aa"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/PersonFullNamePP/UUID","a1c55da1-d836-4514-92ce-8fabf2b0e6b1"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/PersonFullNamePP/UUID","e52fc783-1b2a-48c8-9947-c00c7b7b739d"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/UUID","34e18e3e-c1d7-4b42-a1ce-65f7bded0399"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/FieldAttributes/UUID", "646d7378-a1cb-4486-a1f1-a95d90a8587d"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/UUID","ab93c631-d915-4fd7-936b-75b15514ad9d"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnData/PPReturnInformation/USAddressPP/UUID","56a3e532-4129-448e-bb55-1a66d6c2c9d3"),
                new ImmutablePair<>("/data/data/returns/IRS1040/Return/ReturnHeader/Filer/PrimaryFullNamePP/UUID","3e3fb606-feb4-4bbd-8d81-d31e469a8620")
        );

        keys.forEach(pair -> assertEquals(pair.getValue(), fixture.getValue(pair.getKey())));
    }
    
    private String readFile( String file ) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        }
    }
}
