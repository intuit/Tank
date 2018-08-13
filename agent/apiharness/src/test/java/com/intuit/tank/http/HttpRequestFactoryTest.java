package com.intuit.tank.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.script.ScriptConstants;

/**
 * The class <code>HttpRequestFactoryTest</code> contains tests for the class <code>{@link HttpRequestFactory}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class HttpRequestFactoryTest {
    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_1()
            throws Exception {
        String format = ScriptConstants.XML_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_2()
            throws Exception {
        String format = ScriptConstants.JSON_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_3()
            throws Exception {
        String format = ScriptConstants.PLAIN_TEXT_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_4()
            throws Exception {
        String format = ScriptConstants.MULTI_PART_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_5()
            throws Exception {
        String format = ScriptConstants.NVP_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

}