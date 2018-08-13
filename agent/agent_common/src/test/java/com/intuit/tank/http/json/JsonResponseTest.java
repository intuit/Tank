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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.testng.annotations.Test;

import com.intuit.tank.http.json.JsonResponse;

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
    
/*    @Test
    public void testJsonResponseBody() throws Exception{
    	
    	JsonResponse fixture = new JsonResponse();
    	fixture.setResponseBody(readFile("src/test/resources/json-response.json"));
    	
    	String[] keys = {"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/FieldAttributes/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/IRS1040/DependentWorksheetPP[1]/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPReturnInformation/ForeignAddressPP/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnHeader/Filer/SpouseFullNamePP/UUID",
    			"/data/data/returns/IRS1040/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/PersonFullNamePP/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/PersonFullNamePP/UUID",
    			"/data/data/returns/IRS1040/Return/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/TaxpayerFilerInfoPP/FieldAttributes/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPPerson/SpouseFilerInfoPP/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnData/PPReturnInformation/USAddressPP/UUID",
    			"/data/data/returns/IRS1040/Return/ReturnHeader/Filer/PrimaryFullNamePP/UUID"};
    	
    	for (String key : keys){
    		System.out.println(key + ": " + fixture.getValue(key));
    	}
    }
*/    
    
    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
