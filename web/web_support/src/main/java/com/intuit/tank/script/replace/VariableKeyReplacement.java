package com.intuit.tank.script.replace;

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

import java.util.ArrayList;
import java.util.List;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.search.script.VariableSection;

public class VariableKeyReplacement extends AbstractReplacement {

    public VariableKeyReplacement() {
        super(VariableSection.variableKey, ScriptConstants.VARIABLE);
    }

    @Override
    public List<ReplaceEntity> getReplacements(ScriptStep step, String searchQuery, String replaceString,
            SearchMode searchMode) {
        List<ReplaceEntity> reList = new ArrayList<ReplaceEntity>();
        if (step.getType().equals(getType())) {
            for (RequestData requestData : step.getData()) {
                reList.addAll(getReplacementInValue(searchQuery, replaceString, requestData.getKey(),
                        step.getType(), requestData.getKey()));
            }
        }
        return reList;
    }

    @Override
    public void replace(ScriptStep step, String replaceString, String key, ReplaceMode replaceMode) {
        replaceInRequestDatas(step.getData(), replaceString, key);
    }
}
