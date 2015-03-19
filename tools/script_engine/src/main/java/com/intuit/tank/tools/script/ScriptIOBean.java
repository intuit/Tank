/**
 * Copyright Linspire, Inc. 2007
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * External Script Engine
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
import java.util.Map;

/**
 * ScriptIOBean
 * 
 * <br>
 * Patterns:
 * 
 * <br>
 * Revisions: denis.angleton: Nov 29, 2007: Initial revision.
 * 
 * @author denis.angleton
 */
public class ScriptIOBean {

    static final long serialVersionUID = 1;
    private HashMap<String, Object> inputs;
    private HashMap<String, Object> outputs;
    private OutputLogger output;

    /**
     * 
     * @param in
     *            this inputs
     */
    public ScriptIOBean(Map<String, Object> in, OutputLogger output) {
        this.inputs = in != null ? new HashMap<String, Object>(in) : new HashMap<String, Object>();
        this.outputs = new HashMap<String, Object>();
        this.output = output;

    }

    public void println(String text) {
        print(text + '\n');
    }

    public void print(String text) {
        if (output != null) {
            output.log(text);
        }
    }

    public void debug(String text) {
        if (output != null) {
            output.debug(text);
        }
    }

    public void error(String text) {
        if (output != null) {
            output.error(text);
        }
    }

    /**
     * @return the outputs
     */
    public Map<String, Object> getOutputs() {
        return new HashMap<String, Object>(outputs);
    }

    /**
     * gets the input parameter with the specified name.
     * 
     * @param name
     *            the nam of the parameter
     * @return Object the value
     */
    public Object getInput(String name) {
        Object ret = null;
        if (name != null) {
            ret = inputs.get(name);
        }
        return ret;
    }

    /**
     * gets the input parameter with the specified name.
     * 
     * @param name
     *            the nam of the parameter
     * @return Object the value
     */
    public Object getOutput(String name) {
        Object ret = null;
        if (name != null) {
            ret = outputs.get(name);
        }
        return ret;
    }

    /**
     * sets the output parameter specified.
     * 
     * @param name
     *            the name of the output parameter
     * @param obj
     *            value to set or null to remove the output parameter
     */
    public void setOutput(String name, Object obj) {
        if (name != null) {
            if (obj != null) {
                outputs.put(name, obj);
            }
            else {
                outputs.remove(name);
            }
        }
    }

}
