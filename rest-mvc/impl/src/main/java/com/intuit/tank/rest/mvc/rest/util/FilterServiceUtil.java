/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.filters.models.FilterActionTO;
import com.intuit.tank.filters.models.FilterConditionTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;

import java.util.List;
import java.util.stream.Collectors;

public class FilterServiceUtil {

    public static FilterGroupTO filterGroupToTO(ScriptFilterGroup g) {
        return FilterGroupTO.builder()
                .withId(g.getId())
                .withName(g.getName())
                .withProductName(g.getProductName())
                .build();
    }

    public static FilterTO filterToTO(ScriptFilter f) {
        List<FilterConditionTO> conditions = f.getConditions().stream()
                .map(c -> FilterConditionTO.builder()
                        .withScope(c.getScope())
                        .withCondition(c.getCondition())
                        .withValue(c.getValue())
                        .build())
                .collect(Collectors.toList());

        List<FilterActionTO> actions = f.getActions().stream()
                .map(a -> FilterActionTO.builder()
                        .withAction(a.getAction() != null ? a.getAction().name() : null)
                        .withScope(a.getScope())
                        .withKey(a.getKey())
                        .withValue(a.getValue())
                        .build())
                .collect(Collectors.toList());

        return FilterTO.builder()
                .withId(f.getId())
                .withName(f.getName())
                .withProductName(f.getProductName())
                .withFilterType(f.getFilterType() != null ? f.getFilterType().name() : ScriptFilterType.INTERNAL.name())
                .withAllConditionsMustPass(f.getAllConditionsMustPass())
                .withExternalScriptId(f.getExternalScriptId())
                .withConditions(conditions)
                .withActions(actions)
                .build();
    }

    public static ScriptFilter toFromTO(FilterTO to) {
        ScriptFilter filter = new ScriptFilter();
        if (to.getId() != null && to.getId() > 0) {
            filter.setId(to.getId());
        }
        filter.setName(to.getName());
        filter.setProductName(to.getProductName());
        filter.setAllConditionsMustPass(Boolean.TRUE.equals(to.getAllConditionsMustPass()));
        if (to.getExternalScriptId() != null) {
            filter.setExternalScriptId(to.getExternalScriptId());
        }
        if (to.getFilterType() != null) {
            try {
                filter.setFilterType(ScriptFilterType.valueOf(to.getFilterType()));
            } catch (IllegalArgumentException ignored) {
                filter.setFilterType(ScriptFilterType.INTERNAL);
            }
        }
        if (to.getConditions() != null) {
            for (FilterConditionTO c : to.getConditions()) {
                ScriptFilterCondition cond = new ScriptFilterCondition();
                cond.setScope(c.getScope());
                cond.setCondition(c.getCondition());
                cond.setValue(c.getValue());
                filter.addCondition(cond);
            }
        }
        if (to.getActions() != null) {
            for (FilterActionTO a : to.getActions()) {
                ScriptFilterAction action = new ScriptFilterAction();
                if (a.getAction() != null) {
                    try {
                        action.setAction(ScriptFilterActionType.valueOf(a.getAction()));
                    } catch (IllegalArgumentException ignored) {}
                }
                action.setScope(a.getScope());
                action.setKey(a.getKey());
                action.setValue(a.getValue());
                filter.addAction(action);
            }
        }
        return filter;
    }
}
