/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script;

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
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.model.SelectItem;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.project.Script;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.wrapper.EntityVersionLoader;

/**
 * ProjectModifiedObserver
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class ScriptLoader extends EntityVersionLoader<Script, ModifiedScriptMessage> {

    private static final long serialVersionUID = 1L;
    private SelectItem[] creatorList;

    /**
     * 
     * @param p
     */
    public void observeEvents(@Observes ModifiedEntityMessage entityMsg) {
        if (entityMsg.getEntityClass() == Script.class) {
            invalidate();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    protected List<Script> getEntities() {
        List<Script> scripts = new ScriptDao().findFiltered(ViewFilterType.ALL);
        List<String> list = scripts.stream().filter(p -> !p.getCreator().isEmpty()).map(OwnableEntity::getCreator).distinct().sorted().collect(Collectors.toList());
        creatorList = new SelectItem[list.size() + 1];
        creatorList[0] = new SelectItem("", "All");
        for (int i = 0; i < list.size(); i++) {
            creatorList[i + 1] = new SelectItem(list.get(i), list.get(i));
        }
        return scripts;
    }

    /**
     * @return the creatorList
     */
    public SelectItem[] getCreatorList() {
        return creatorList;
    }

}
