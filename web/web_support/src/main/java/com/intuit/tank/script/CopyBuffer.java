package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
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
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;

import com.intuit.tank.auth.Authenticated;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.User;

@Named
@SessionScoped
public class CopyBuffer implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ScriptStep> buffer = new ArrayList<ScriptStep>();

    public void observeLogin(@Observes @Authenticated User user) {
        clear();
    }

    /**
     * 
     * @return
     */
    public List<ScriptStep> getBuffer() {
        return buffer;
    }

    /**
	 * 
	 */
    public void clear() {
        this.buffer.clear();
    }

    public boolean isPasteEnabled() {
        return !buffer.isEmpty();
    }

}
