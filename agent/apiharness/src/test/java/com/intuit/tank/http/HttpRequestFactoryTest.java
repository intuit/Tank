package com.intuit.tank.http;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.settings.AgentConfig;
import org.apache.logging.log4j.message.ObjectMessage;
import org.junit.jupiter.api.Test;

import com.intuit.tank.script.ScriptConstants;

public class HttpRequestFactoryTest {
    @Test
    public void testGetHttpRequest_1() {
        String format = ScriptConstants.XML_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    @Test
    public void testGetHttpRequest_2() {
        String format = ScriptConstants.JSON_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    @Test
    public void testGetHttpRequest_3() {
        String format = ScriptConstants.PLAIN_TEXT_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    @Test
    public void testGetHttpRequest_4() {
        String format = ScriptConstants.MULTI_PART_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    @Test
    public void testGetHttpRequest_5() {
        String format = ScriptConstants.NVP_TYPE;
        BaseRequest result = HttpRequestFactory.getHttpRequest(format, null);
        assertNotNull(result);
    }

    @Test
    public void testGetHttpRequest_6() {
        String format = "test";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            HttpRequestFactory.getHttpRequest(format, null);
        });
        String message = exception.getMessage();
        assertTrue(message.contains("unknown request format - test"));
    }

    @Test
    public void testTankLogUtil() {
        HttpRequestFactory.TankLogUtil logUtil = new HttpRequestFactory.TankLogUtil();
        ObjectMessage logMessage = logUtil.getLogMessage("test");
        assertTrue(logMessage.getFormattedMessage().contains("test"));
        logMessage = logUtil.getLogMessage("test", LogEventType.Http);
        assertTrue(logMessage.getFormattedMessage().contains("test"));
        logMessage = logUtil.getLogMessage("test", LogEventType.Http, LoggingProfile.STANDARD);
        assertTrue(logMessage.getFormattedMessage().contains("test"));
        assertFalse(logUtil.isTextMimeType("test"));
        assertTrue(logUtil.isTextMimeType(".*text.*"));
        AgentConfig agentConfig = logUtil.getAgentConfig();
        assertEquals("/tmp", agentConfig.getAgentDataFileStorageDir());
    }
}
