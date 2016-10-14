/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.admin;

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
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.search.script.ScriptSearchService;
import com.intuit.tank.vm.common.util.MethodTimer;

/**
 * SearchIndexBean
 * 
 * @author dangleton
 * 
 */
@Named
@ApplicationScoped
public class SearchIndexer implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(SearchIndexer.class);

    @Inject
    private Messages messages;

    public String indexAll() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                MethodTimer mt = new MethodTimer(LOG, getClass(), "indexAll");
                mt.start();
                ScriptSearchService scriptSearchService = new ScriptSearchService();
                ScriptDao dao = new ScriptDao();
                List<Script> allScripts = dao.findAll();
                for (Script script : allScripts) {
                    try {
                        script = dao.loadScriptSteps(script);
                        scriptSearchService.saveScript(script);
                    } catch (Exception e) {
                        LOG.error("Error indexing Script " + script.getName() + ": " + e.toString(), e);
                    }
                }
                mt.markAndLog();
            }
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
        messages.info("Indexing all Scripts... This may take a while.");
        return null;
    }
}
