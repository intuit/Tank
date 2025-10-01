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
import com.intuit.tank.script.ThinkTimeSection;

public class MinTimeReplacement extends AbstractReplacement {

    public MinTimeReplacement() {
        super(ThinkTimeSection.minTime, ScriptConstants.THINK_TIME);
    }

    @Override
    public List<ReplaceEntity> getReplacements(ScriptStep step, String searchQuery, String replaceString,
            SearchMode searchMode) {
        List<ReplaceEntity> reList = new ArrayList<ReplaceEntity>();
        if (step.getType().equals(getType())) {
            for (RequestData requestData : step.getData()) {
                if (requestData.getKey().equals(ScriptConstants.MIN_TIME)) {
                    reList.addAll(getReplacementInValue(searchQuery, replaceString, requestData.getValue(),
                            step.getType(), ScriptConstants.MIN_TIME));
                }
            }
        }
        return reList;
    }

    @Override
    public void replace(ScriptStep step, String replaceString, String key, ReplaceMode replaceMode) {
        replaceInRequestDatas(step.getData(), replaceString, ScriptConstants.MIN_TIME);
    }
}
