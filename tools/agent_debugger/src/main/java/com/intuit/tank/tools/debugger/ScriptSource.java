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

public class ScriptSource {

    public String id;
    public SourceType source;

    public ScriptSource(String id, SourceType source) {
        super();
        this.id = id;
        this.source = source;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the source
     */
    public SourceType getSource() {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(SourceType source) {
        this.source = source;
    }

}
