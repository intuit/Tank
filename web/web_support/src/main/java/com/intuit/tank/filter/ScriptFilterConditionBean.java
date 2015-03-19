package com.intuit.tank.filter;

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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.project.ScriptFilterCondition;

@Named
@ConversationScoped
public class ScriptFilterConditionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptFilterCreationBean sfcb;

    private ScriptFilterCondition condition;

    private String scope;
    private String conditionStr;
    private String value;

    private boolean editMode;

    public void setupNewCondition() {
        condition = new ScriptFilterCondition();
        conditionStr = ConditionMatch.contains.getValue();
        scope = ConditionScope.hostname.getValue();
        value = "";
        editMode = false;
    }

    public void editCondition(ScriptFilterCondition condition) {
        this.condition = condition;
        scope = condition.getScope();
        conditionStr = condition.getCondition();
        value = condition.getValue();
        editMode = true;
    }

    public ConditionMatch[] getConditionValues() {
        return ConditionMatch.values();
    }

    public ConditionScope[] getConditionScopeValues() {
        return ConditionScope.values();
    }

    public void done() {
        condition.setScope(scope);
        condition.setCondition(conditionStr);
        condition.setValue(value);
        if (!editMode) {
            sfcb.getFilter().addCondition(condition);
        }
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope
     *            the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the conditionStr
     */
    public String getConditionStr() {
        return conditionStr;
    }

    /**
     * @param conditionStr
     *            the conditionStr to set
     */
    public void setConditionStr(String conditionStr) {
        this.conditionStr = conditionStr;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
