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

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class JobQueueManager extends JobTreeTableBean {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @{inheritDoc
     */
    @Override
    protected Integer getRootJobId() {
        return null;
    }

}
