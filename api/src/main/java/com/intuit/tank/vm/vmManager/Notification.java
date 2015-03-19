/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.vmManager;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;
import java.util.Set;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

/**
 * Notification
 * 
 * @author dangleton
 * 
 */
public interface Notification {

    /**
     * @return the subject
     */
    public abstract String getSubject();

    /**
     * @return the body
     */
    public abstract String getBody();

    /**
     * @return the lifecycle
     */
    public abstract List<JobLifecycleEvent> getLifecycleEvents();

    /**
     * @return the recipients
     */
    public abstract Set<? extends Recipient> getRecipients();

}