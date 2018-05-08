package com.intuit.tank.vm.common;

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

import java.io.Serializable;

import com.intuit.tank.vm.settings.LogicStepConfig;
import com.intuit.tank.vm.settings.TankConfig;

public class LogicScriptUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param script
     * @return
     */
    public String buildScript(String script) {
        LogicStepConfig logicStepConfig = new TankConfig().getLogicStepConfig();
        return logicStepConfig.getInsertBefore() + '\n' + '\n' +
                script + '\n' + '\n' + logicStepConfig.getAppendAfter();
    }
}
