package com.intuit.tank.http;

import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.settings.AgentConfig;
import org.apache.logging.log4j.message.ObjectMessage;

public interface TankHttpLogger {

    
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public ObjectMessage getLogMessage(String msg);
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public ObjectMessage getLogMessage(String msg, LogEventType type);
    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public ObjectMessage getLogMessage(String msg, LogEventType type, LoggingProfile profile);

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

    /**
     * Returns true if the debugger is running the client code.
     * Used to gate expensive request/response logging that is only
     * consumed by the debugger UI.
     *
     * @return true if running inside the Tank Debugger tool
     */
    default boolean isDebugMode() {
        return false;
    }
}
