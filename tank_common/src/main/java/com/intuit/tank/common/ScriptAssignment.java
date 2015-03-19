package com.intuit.tank.common;

/*
 * #%L
 * Common
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

public class ScriptAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String assignemnt;
    private String variable;
    private int scriptIndex;

    public ScriptAssignment(String variable, String assignemnt, int scriptIndex) {
        super();
        this.variable = variable;
        this.assignemnt = assignemnt;
        this.scriptIndex = scriptIndex;
    }

    public String getAssignemnt() {
        return assignemnt;
    }

    public String getVariable() {
        return variable;
    }

    public int getScriptIndex() {
        return scriptIndex;
    }

}
