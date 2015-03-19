/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.hibernate.envers.RevisionListener;

import com.intuit.tank.vm.common.ThreadLocalUsernameProvider;

/**
 * WatsRevision
 * 
 * @author dangleton
 * 
 */
public class WatsRevisionListener implements RevisionListener {

    public void newRevision(Object revisionEntity) {
        WatsRevisionInfo revisionInfo = (WatsRevisionInfo) revisionEntity;
        revisionInfo.setUsername(ThreadLocalUsernameProvider.getUsernameProvider().getUserName());
    }

}
