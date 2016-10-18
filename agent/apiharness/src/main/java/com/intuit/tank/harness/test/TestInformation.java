package com.intuit.tank.harness.test;

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

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.test.data.Variables;

public class TestInformation {
    static Logger logger = LogManager.getLogger(TestInformation.class);

    private HashMap<String, String> members = new HashMap<String, String>();
    private HashMap<String, String> testPlanVariables = new HashMap<String, String>();
    private Vector<String> comments = new Vector<String>();

    /**
     * Set the test suite name
     * 
     * @param value
     *            The test suite name
     */
    public void setName(String value) {
        this.members.put("name", value);
    }

    /**
     * Get the test suite name
     * 
     * @return The test suite name
     */
    public String getName() {
        return this.members.get("name");
    }

    /**
     * Set the test suite name
     * 
     * @param value
     *            The test suite name
     */
    public void setLoggingKey(String value) {
        this.members.put("loggingKey", value);
    }

    /**
     * Get the test suite name
     * 
     * @return The test suite name
     */
    public String getLoggingKey() {
        return this.members.get("loggingKey");
    }

    /**
     * Gets the name of the test.
     * 
     * @return
     */
    public String getTestName() {
        return this.members.get("testname");
    }

    /**
     * Sets the testName
     * 
     * @param value
     */
    public void setTestName(String value) {
        this.members.put("testname", value);
    }

    /**
     * Set a variable
     * 
     * @param key
     *            The variable name
     * @param value
     *            The variable value
     */
    public void setVariable(String key, String value) {
        this.testPlanVariables.put(key, value);
    }

    /**
     * Get a variable value
     * 
     * @param key
     *            The variable name
     * @return The variable value
     */
    public String getVariable(String key) {
        return this.testPlanVariables.get(key);
    }

    /**
     * Get the variable collection
     * 
     * @return A HashMap containing the variables
     */
    public HashMap<String, String> getVariables() {
        return this.testPlanVariables;
    }

    /**
     * Set the test suite description
     * 
     * @param value
     *            The test suite description
     */
    public void setDescription(String value) {
        this.members.put("description", value);
    }

    /**
     * Get the test suite description
     * 
     * @return The test suite description
     */
    public String getDescription() {
        return this.members.get("description");
    }

    /**
     * Set the protocol (HTTP, HTTPS, FTP)
     * 
     * @param value
     *            The protocol
     */
    public void setProtocol(String value) {
        this.members.put("protocol", value);
    }

    /**
     * Get the protocol (HTTP, HTTPS, FTP)
     * 
     * @return The protocol
     */
    public String getProtocol() {
        return this.members.get("protocol");
    }

    /**
     * Set the URL
     * 
     * @param value
     *            The URL
     */
    public void setHost(String value) {
        this.members.put("host", value);
    }

    /**
     * Get the URL
     * 
     * @return The URL
     */
    public String getHost() {
        return this.members.get("host");
    }

    /**
     * Set the path
     * 
     * @param value
     *            The path
     */
    public void setPath(String value) {
        this.members.put("path", value);
    }

    /**
     * Get the path
     * 
     * @return The default path
     */
    public String getPath() {
        return this.members.get("path");
    }

    /**
     * Set the default port
     * 
     * @param value
     *            The default port
     */
    public void setPort(String value) {
        this.members.put("port", value);
    }

    /**
     * Get the default port
     * 
     * @return The default port
     */
    public String getPort() {
        return this.members.get("port");
    }

    /**
     * 
     * @param loop
     */
    public void setLoop(int loop) {
        this.members.put("loop", Integer.toString(loop));
    }

    /**
     * 
     * @return
     */
    public int getLoop() {
        String string = this.members.get("loop");
        return NumberUtils.isNumber(string) ? Integer.parseInt(string) : 1;
    }

    /**
     * Set the default port
     * 
     * @param value
     *            The default port
     */
    public void setTemplate(String value) {
        this.members.put("template", value);
    }

    /**
     * Get the default port
     * 
     * @return The default port
     */
    public String getTemplate() {
        return this.members.get("template");
    }

    /**
     * Set whether or not to authenticate
     * 
     * @param value
     *            TRUE to authenticate; FALSE otherwise
     */
    public void setAuthenticate(boolean value) {
        this.members.put("authenticate", String.valueOf(value));
    }

    /**
     * Whether or not to authenticate
     * 
     * @return TRUE to authenticate; FALSE otherwise
     */
    public boolean authenticate() {
        if (!this.members.containsKey("authenticate"))
            return false;
        return Boolean.valueOf(this.members.get("authenticate"));
    }

    /**
     * Add a comment to the object
     * 
     * @param comment
     *            The comment
     */
    public void addComment(String comment) {
        this.comments.add(comment);
    }

    /**
     * Get the comment container
     * 
     * @return The ArrayList containing the comments
     */
    public Vector<String> getComments() {
        return this.comments;
    }

    /**
     * Process the objects variables
     */
    protected void processVariables(Variables variables) {

        for (Object key : this.getVariables().keySet()) {
            String value = this.getVariable((String) key);
            if (FunctionHandler.validFunction(value)) {
                value = FunctionHandler.executeFunction(value, variables);
            }
            variables.addVariable((String) key, value);
        }
    }

}
