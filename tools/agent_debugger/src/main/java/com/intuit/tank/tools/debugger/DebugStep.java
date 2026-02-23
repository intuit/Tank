package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;

public class DebugStep implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> entryVariables;
    private Map<String, String> exitVariables;
    private BaseRequest request;
    private BaseResponse response;
    private String logEntry;
    private TestStep stepRun;
    private List<ErrorContainer> errors = new ArrayList<ErrorContainer>();
    private Map<String, MessageStreamSnapshot> entryMessageStreams = new LinkedHashMap<String, MessageStreamSnapshot>();
    private Map<String, MessageStreamSnapshot> exitMessageStreams = new LinkedHashMap<String, MessageStreamSnapshot>();

    public DebugStep(TestStep step) {
        super();
        this.stepRun = step;
    }

    /**
     * @return the errors
     */
    public List<ErrorContainer> getErrors() {
        return errors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public void setErrors(List<ErrorContainer> errors) {
        this.errors = new ArrayList<ErrorContainer>(errors);
    }

    public Map<String, MessageStreamSnapshot> getEntryMessageStreams() {
        return Collections.unmodifiableMap(entryMessageStreams);
    }

    public void setEntryMessageStreams(Map<String, MessageStreamSnapshot> entryMessageStreams) {
        this.entryMessageStreams = copySnapshots(entryMessageStreams);
    }

    public Map<String, MessageStreamSnapshot> getExitMessageStreams() {
        return Collections.unmodifiableMap(exitMessageStreams);
    }

    public void setExitMessageStreams(Map<String, MessageStreamSnapshot> exitMessageStreams) {
        this.exitMessageStreams = copySnapshots(exitMessageStreams);
    }

    /**
     * @return the entryVariables
     */
    public Map<String, String> getEntryVariables() {
        return entryVariables;
    }

    /**
     * @param entryVariables
     *            the entryVariables to set
     */
    public void setEntryVariables(Map<String, String> entryVariables) {
        this.entryVariables = entryVariables;
    }

    /**
     * @return the exitVariables
     */
    public Map<String, String> getExitVariables() {
        return exitVariables;
    }

    /**
     * @param exitVariables
     *            the exitVariables to set
     */
    public void setExitVariables(Map<String, String> exitVariables) {
        this.exitVariables = exitVariables;
    }

    /**
     * @return the request
     */
    public BaseRequest getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public BaseResponse getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(BaseResponse response) {
        this.response = response;
    }

    /**
     * @return the logEntry
     */
    public String getLogEntry() {
        return logEntry;
    }

    /**
     * @param logEntry
     *            the logEntry to set
     */
    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }

    /**
     * @return the stepRun
     */
    public TestStep getStepRun() {
        return stepRun;
    }

    /**
     * @param stepRun
     *            the stepRun to set
     */
    public void setStepRun(TestStep stepRun) {
        this.stepRun = stepRun;
    }

    public void clear() {
        entryVariables = null;
        exitVariables = null;
        request = null;
        response = null;
        logEntry = null;
        errors.clear();
        entryMessageStreams.clear();
        exitMessageStreams.clear();

    }

    private Map<String, MessageStreamSnapshot> copySnapshots(Map<String, MessageStreamSnapshot> source) {
        Map<String, MessageStreamSnapshot> copy = new LinkedHashMap<String, MessageStreamSnapshot>();
        if (source != null) {
            for (Map.Entry<String, MessageStreamSnapshot> entry : source.entrySet()) {
                if (entry.getValue() != null) {
                    copy.put(entry.getKey(), entry.getValue().copy());
                }
            }
        }
        return copy;
    }

    public static class MessageStreamSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private String connectionId;
        private boolean connected;
        private boolean failed;
        private int messageCount;
        private long elapsedTimeMs;
        private String failurePattern;
        private String failureMessage;
        private List<MessageSnapshot> messages = new ArrayList<MessageSnapshot>();

        public MessageStreamSnapshot copy() {
            MessageStreamSnapshot ret = new MessageStreamSnapshot();
            ret.connectionId = connectionId;
            ret.connected = connected;
            ret.failed = failed;
            ret.messageCount = messageCount;
            ret.elapsedTimeMs = elapsedTimeMs;
            ret.failurePattern = failurePattern;
            ret.failureMessage = failureMessage;
            ret.messages = new ArrayList<MessageSnapshot>(messages.size());
            for (MessageSnapshot message : messages) {
                ret.messages.add(message.copy());
            }
            return ret;
        }

        public String getConnectionId() {
            return connectionId;
        }

        public void setConnectionId(String connectionId) {
            this.connectionId = connectionId;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }

        public int getMessageCount() {
            return messageCount;
        }

        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }

        public long getElapsedTimeMs() {
            return elapsedTimeMs;
        }

        public void setElapsedTimeMs(long elapsedTimeMs) {
            this.elapsedTimeMs = elapsedTimeMs;
        }

        public String getFailurePattern() {
            return failurePattern;
        }

        public void setFailurePattern(String failurePattern) {
            this.failurePattern = failurePattern;
        }

        public String getFailureMessage() {
            return failureMessage;
        }

        public void setFailureMessage(String failureMessage) {
            this.failureMessage = failureMessage;
        }

        public List<MessageSnapshot> getMessages() {
            return messages;
        }

        public void setMessages(List<MessageSnapshot> messages) {
            this.messages = messages != null ? new ArrayList<MessageSnapshot>(messages) : new ArrayList<MessageSnapshot>();
        }
    }

    public static class MessageSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private int index;
        private long relativeTimeMs;
        private long timestamp;
        private String content;

        public MessageSnapshot copy() {
            MessageSnapshot ret = new MessageSnapshot();
            ret.index = index;
            ret.relativeTimeMs = relativeTimeMs;
            ret.timestamp = timestamp;
            ret.content = content;
            return ret;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public long getRelativeTimeMs() {
            return relativeTimeMs;
        }

        public void setRelativeTimeMs(long relativeTimeMs) {
            this.relativeTimeMs = relativeTimeMs;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
