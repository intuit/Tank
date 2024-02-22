package com.intuit.tank.script.processor;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.monitor.GlobalPercentCompleteMonitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.util.OwaspReader;
import com.intuit.tank.script.util.RecordedScriptReader;
import com.intuit.tank.script.util.ScriptFilterUtil;
import com.intuit.tank.vm.common.PercentCompleteMonitor;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * 
 * ScriptProcessor
 * 
 * @author dangleton
 * 
 */
@Named
@Dependent
public class ScriptProcessor implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(ScriptProcessor.class);

    @Inject
    private GlobalPercentCompleteMonitor monitor;

    private Script script = null;
    private List<ScriptStep> steps;

    public ScriptProcessor() {
    }

    /**
     * 
     * @param xml
     * @param filters
     * @throws WatsParseException
     */
    public List<ScriptStep> parseScript(String xml, List<ScriptFilter> filters) throws WatsParseException {
        return parseScript(new StringReader(xml), filters);
    }

    /**
     * @return the monitor
     */
    public PercentCompleteMonitor getMonitor() {
        return monitor;
    }

    /**
     * @param monitor
     *            the monitor to set
     */
    public void setMonitor(GlobalPercentCompleteMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * 
     * @param reader
     * @param filters
     * @throws WatsParseException
     */
    public List<ScriptStep> parseScript(Reader reader, List<ScriptFilter> filters) throws WatsParseException {
        MethodTimer timer = new MethodTimer(LOG, getClass(), "parseScript");
        timer.start();
        // parse xml
        steps = getScriptSteps(reader, filters);
        if (script != null) {
            monitor.setSavingStarted(script.getId());
        }
        timer.markAndLog("Parse Script with " + steps.size() + " steps");
        return steps;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(Script script) {
        this.script = script;
    }

    @Override
    public void run() {

        ScriptDao scriptDao = new ScriptDao();
        if (script != null) {
            MethodTimer timer = new MethodTimer(LOG, getClass(), "run");
            timer.start();
            // save script to db
            try {
                setScriptSteps(script, steps);
                scriptDao.saveOrUpdate(script);
                timer.markAndLog("Save Script of with " + steps.size() + " steps");
                if (script != null) {
                    monitor.setProcessingComplete(script.getId());
                }
            } catch (Exception e) {
                if (script != null) {
                    monitor.setError(script.getId(), 204);
                }
                timer.markAndLog("Save Script failed");
                LOG.error(e);
            }
        }
    }

    public List<ScriptStep> getScriptSteps(Reader reader, Collection<ScriptFilter> filters) throws WatsParseException {
        List<ScriptStep> entries = null;
        RecordedScriptReader scriptReader = new OwaspReader();
        if (script != null) {
            setScriptSteps(script, scriptReader.read(reader));
            ScriptFilterUtil.applyFiltersToScript(filters, script);
            entries = script.getScriptSteps();
        } else {
            entries = ScriptFilterUtil.applyFilters(filters, scriptReader.read(reader));
        }

        return entries;
    }

    public void setScriptSteps(Script script, List<ScriptStep> steps) {
        LOG.debug("script " + script.getName() + " has " + steps.size() + " steps");
        List<ScriptStep> newSteps = new ArrayList<ScriptStep>(steps);
        script.getScriptSteps().clear();
        script.getScriptSteps().addAll(newSteps);
    }

}
