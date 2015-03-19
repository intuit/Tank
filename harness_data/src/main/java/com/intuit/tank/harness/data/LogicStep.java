package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "logic", propOrder = { "name", "script", "scriptGroupName" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class LogicStep extends TestStep implements FailableStep {

    @XmlAttribute
    private String name = "";

    @XmlAttribute
    private String script = "";

    @XmlAttribute
    private String scriptGroupName = "";

    private transient String onFail = "";

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @return the scriptGroupName
     */
    public String getScriptGroupName() {
        return scriptGroupName;
    }

    /**
     * @param scriptGroupName
     *            the scriptGroupName to set
     */
    public void setScriptGroupName(String scriptGroupName) {
        this.scriptGroupName = scriptGroupName;
    }

    /**
     * @return the onFail
     */
    public String getOnFail() {
        return onFail;
    }

    /**
     * @param onFail
     *            the onFail to set
     */
    public void setOnFail(String onFail) {
        this.onFail = onFail;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String getInfo() {
        return new StringBuilder().append("Logic Step: ").append(name).toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + name;
    }

}
