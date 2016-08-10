/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

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

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * TsConversationManager
 * 
 * @author dangleton
 * 
 */
@Named
@ConversationScoped
public class TsConversationManager implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(TsConversationManager.class);

    @Inject
    private Conversation conversation;

    public void join() {
        if (conversation != null && conversation.isTransient()) {
            conversation.begin();
        }
    }

    public void end() {
        if (conversation != null && !conversation.isTransient()) {
            conversation.end();
        }
    }

    public void keepAlive() {
        LOG.debug("Keeping alive conversation: " + conversation);
    }
}
