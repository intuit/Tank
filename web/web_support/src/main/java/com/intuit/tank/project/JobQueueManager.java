package com.intuit.tank.project;

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

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.intuit.tank.qualifier.Modified;

@Named
@ViewScoped
public class JobQueueManager extends JobTreeTableBean {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getRootJobId() {
        return null;
    }
    
    public void observe(@Observes(notifyObserver = Reception.IF_EXISTS) @Modified JobQueue queueEvent) {
        rootNode = null;
    }

}
