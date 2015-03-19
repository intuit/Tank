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

import java.util.List;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.search.script.RequestStepSection;

public class QueryStringKeyReplacement extends AbstractReplacement {

    public QueryStringKeyReplacement() {
        super(RequestStepSection.queryStringKey, ScriptConstants.REQUEST);
    }

    @Override
    public List<ReplaceEntity> getReplacements(ScriptStep step, String searchQuery, String replaceString,
            SearchMode searchMode) {
        return getReplacementsInRequestData(step, searchQuery, replaceString, step.getQueryStrings(),
                SearchMode.keyOnly);
    }

    @Override
    public void replace(ScriptStep step, String replaceString, String key, ReplaceMode replaceMode) {
        // TODO: this needs to be changed as well
        replaceInRequestDatas(step.getQueryStrings(), replaceString, key, replaceMode);
    }

}
