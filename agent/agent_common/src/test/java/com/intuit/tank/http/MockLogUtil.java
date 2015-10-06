/**
 * 
 */
package com.intuit.tank.http;

import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * @author denisa
 *
 */
public class MockLogUtil implements TankHttpLogger {

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpLogger#getLogMessage(java.lang.String)
     */
    @Override
    public String getLogMessage(String msg) {
        return msg;
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpLogger#getLogMessage(java.lang.String, com.intuit.tank.logging.LogEventType)
     */
    @Override
    public String getLogMessage(String msg, LogEventType type) {
        return  msg;
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpLogger#getLogMessage(java.lang.String, com.intuit.tank.logging.LogEventType, com.intuit.tank.logging.LoggingProfile)
     */
    @Override
    public String getLogMessage(String msg, LogEventType type, LoggingProfile profile) {
        return msg;
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpLogger#isTextMimeType(java.lang.String)
     */
    @Override
    public boolean isTextMimeType(String mimeType) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpLogger#getAgentConfig()
     */
    @Override
    public AgentConfig getAgentConfig() {
        return new TankConfig().getAgentConfig();
    }

}
