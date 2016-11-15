package com.intuit.tank.http;

import java.util.Map;

import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.settings.AgentConfig;

public interface TankHttpLogger {

    
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public Map<String,String> getLogMessage(String msg);
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public Map<String,String> getLogMessage(String msg, LogEventType type);
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public Map<String,String> getLogMessage(String msg, LogEventType type, LoggingProfile profile);

    /**
     * Returns true if the argument is a valid mimeType else returns false
     * 
     * @param mimeType
     * @return
     */
    public boolean isTextMimeType(String mimeType);
    
    /**
     * Gets the current agentConfig object
     * @return
     */
    public AgentConfig getAgentConfig();
}
