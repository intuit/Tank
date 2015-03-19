package com.intuit.tank.proxy.settings.ui;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Set;

import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;

public class InclusionHandler extends Handler {

    public InclusionHandler(ConfigHandler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Set<ConfigInclusionExclusionRule> getData() {
        return handler.getConfiguration().getInclusions();
    }

}
