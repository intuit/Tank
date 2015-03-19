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
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.script.FailureTypes;
import com.intuit.tank.script.ResponseContentParser;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.api.enumerated.ValidationType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;

@Named
@ConversationScoped
public class ScriptFilterActionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptFilterCreationBean sfcb;

    private ScriptFilterAction action;

    private ScriptFilterActionType actionType;
    private String scope;
    private String key;
    private String valuePrefix;
    private String value;

    private FailureTypes[] failureTypes;

    private boolean editMode;

    public ScriptFilterActionBean() {
        List<FailureTypes> l = new ArrayList<FailureTypes>();
        for (FailureTypes t : FailureTypes.values()) {
            if (t != FailureTypes.gotoGroupRequest) {
                l.add(t);
            }
        }
        failureTypes = l.toArray(new FailureTypes[l.size()]);
    }

    public void setupNewAction() {
        action = new ScriptFilterAction();
        editMode = false;
        actionType = ScriptFilterActionType.remove;
        scope = RemoveActionScope.request.getValue();
        key = "";
        value = "";
        valuePrefix = "";
    }

    public void editAction(ScriptFilterAction action) {
        this.action = action;

        actionType = action.getAction();
        scope = action.getScope();
        key = action.getKey();
        String val = action.getValue();

        if (scope.equals(AddActionScope.assignment.getValue()) || scope.equals(AddActionScope.validation.getValue()) ||
                scope.equals(ReplaceActionScope.assignment.getValue()) ||
                scope.equals(ReplaceActionScope.validation.getValue())) {
            valuePrefix = ResponseContentParser.extractCondition(val);
            value = ResponseContentParser.extractValidateValue(val);
        } else {
            value = action.getValue();
        }
        editMode = true;

    }

    public void done() {
        action.setAction(actionType);
        action.setScope(scope);
        action.setKey(key);

        if (scope.equals(AddActionScope.assignment.getValue()) || scope.equals(AddActionScope.validation.getValue()) ||
                scope.equals(ReplaceActionScope.assignment.getValue()) ||
                scope.equals(ReplaceActionScope.validation.getValue())) {
            action.setValue(valuePrefix + value);
        } else {
            action.setValue(value);
        }

        if (!editMode) {
            sfcb.getFilter().addAction(action);
        }
    }

    public FailureTypes[] getOnFailOptions() {
        return failureTypes;
    }

    public List<String> getActionScopeValues() {
        List<String> values = new ArrayList<String>();
        if (actionType.equals(ScriptFilterActionType.add)) {
            AddActionScope[] aas = AddActionScope.values();
            for (AddActionScope scope : aas) {
                values.add(scope.getValue());
            }

        } else if (actionType.equals(ScriptFilterActionType.remove)) {
            RemoveActionScope[] ras = RemoveActionScope.values();
            for (RemoveActionScope scope : ras) {
                values.add(scope.getValue());
            }

        } else if (actionType.equals(ScriptFilterActionType.replace)) {
            ReplaceActionScope[] ras = ReplaceActionScope.values();
            for (ReplaceActionScope scope : ras) {
                values.add(scope.getValue());
            }

        }
        return values;
    }

    public boolean isKeyBoxRendered() {
        boolean retVal = true;
        if ((actionType == ScriptFilterActionType.remove && scope.equals(RemoveActionScope.request.getValue()))
                || (actionType.equals(ScriptFilterActionType.add) && scope.equals(AddActionScope.sleepTime.getValue()))
                || (actionType == ScriptFilterActionType.replace && scope.equals(ReplaceActionScope.onfail.getValue()))) {
            retVal = false;
        }
        return retVal;
    }

    public boolean isValuePrefixRendered() {
        boolean retVal = false;
        if (scope.equals(AddActionScope.assignment.getValue()) || scope.equals(AddActionScope.validation.getValue()) ||
                scope.equals(ReplaceActionScope.validation.getValue()) ||
                scope.equals(ReplaceActionScope.validation.getValue())) {
            retVal = true;
        }
        return retVal;
    }

    public ValidationType[] getValidationValues() {
        return ValidationType.values();
    }

    public boolean isAssignmentScope() {
        boolean retVal = false;
        if (scope.equals(ReplaceActionScope.assignment.getValue())
                || scope.equals(AddActionScope.assignment.getValue())) {
            retVal = true;
            valuePrefix = "=";
        }
        return retVal;
    }

    public boolean isValidationScope() {
        boolean retVal = false;
        if (scope.equals(ReplaceActionScope.validation.getValue())
                || scope.equals(AddActionScope.validation.getValue())) {
            retVal = true;
        }
        return retVal;
    }

    public boolean isValueBoxRendered() {
        boolean retVal = true;
        if (actionType.equals(ScriptFilterActionType.remove)
                || (actionType == ScriptFilterActionType.replace && scope.equals(ReplaceActionScope.onfail.getValue()))) {
            retVal = false;
        }
        return retVal;
    }

    public boolean isOnFailOptionsRendered() {
        boolean retVal = false;
        if (actionType == ScriptFilterActionType.replace && scope.equals(ReplaceActionScope.onfail.getValue())) {
            retVal = true;
        }
        return retVal;
    }

    public List<String> getActionValues() {
        List<String> valueList = new ArrayList<String>();
        ScriptFilterActionType[] values = ScriptFilterActionType.values();
        for (ScriptFilterActionType scriptFilterActionType : values) {
            valueList.add(scriptFilterActionType.toString());
        }
        return valueList;
    }

    /**
     * @return the action
     */
    public ScriptFilterAction getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(ScriptFilterAction action) {
        this.action = action;
    }

    /**
     * @return the actionType
     */
    public ScriptFilterActionType getActionType() {
        return actionType;
    }

    /**
     * @param actionType
     *            the actionType to set
     */
    public void setActionType(ScriptFilterActionType actionType) {
        this.actionType = actionType;
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
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the valuePrefix
     */
    public String getValuePrefix() {
        return valuePrefix;
    }

    /**
     * @param valuePrefix
     *            the valuePrefix to set
     */
    public void setValuePrefix(String valuePrefix) {
        this.valuePrefix = valuePrefix;
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
