/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.models.scripts.*;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptServiceV2Impl implements ScriptServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(ScriptServiceV2Impl.class);

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }

    @Override
    public ScriptTO createScript(ScriptTO scriptTo) {
        Script savedScript;
        scriptTo.setCreated(null);
        scriptTo.setModified(null);
        scriptTo.setId(0);
        try {
            savedScript = new ScriptDao().saveOrUpdate(ScriptServiceUtil.transferObjectToScript(scriptTo));
            return ScriptServiceUtil.scriptToTransferObject(savedScript);
        } catch (Exception e) {
            LOGGER.error("Error creating script: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "script", e);
        }
    }

    @Override
    public ScriptTO getScript(Integer scriptId) {
        try {
            ScriptDao dao = new ScriptDao();
            Script script = dao.findById(scriptId);
            if (script != null) {
                return ScriptServiceUtil.scriptToTransferObject(script);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Error returning script: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "script", e);
        }
    }

    @Override
    public ScriptDescriptionContainer getScripts() {
        try {
            ScriptDao dao = new ScriptDao();
            List<Script> all = dao.findAll();
            List<ScriptDescription> result = all.stream().map(ScriptServiceUtil::scriptToScriptDescription).collect(Collectors.toList());
            return new ScriptDescriptionContainer(result);
        } catch (Exception e) {
            LOGGER.error("Error returning all script: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "all script", e);
        }
    }

    @Override
    public String deleteScript(Integer scriptId) {
        try {
            ScriptDao dao = new ScriptDao();
            Script script = dao.findById(scriptId);
            if (script == null) {
                LOGGER.warn("Script with script id " +  scriptId + " does not exist");
                return "Script with script id " +  scriptId + " does not exist";
            } else {
                dao.delete(script);
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting script : " + e, e);
            throw new GenericServiceDeleteException("script", "script", e);
        }
    }

    // External Scripts

    @Override
    public ExternalScriptContainer getExternalScripts() {
        try {
            ExternalScriptDao dao = new ExternalScriptDao();
            List<ExternalScript> all = dao.findAll();
            ExternalScriptContainer ret = new ExternalScriptContainer();
            for (ExternalScript s : all) {
                ret.getScripts().add(ScriptServiceUtil.externalScriptToTO(s));
            }
            return ret;
        } catch (Exception e) {
            LOGGER.error("Error returning all external script : " + e, e);
            throw new GenericServiceResourceNotFoundException("script", "all external scripts", e);
        }
    }

    @Override
    public ExternalScriptTO getExternalScript(Integer externalScriptId) {
        try {
            ExternalScriptDao dao = new ExternalScriptDao();
            ExternalScript script = dao.findById(externalScriptId);
            if (script != null) {
                return ScriptServiceUtil.externalScriptToTO(script);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error returning the external script : " + e, e);
            throw new GenericServiceResourceNotFoundException("script", "external script", e);
        }
    }

    @Override
    public ExternalScriptTO createExternalScript(ExternalScriptTO ExternalScriptRequest) {
        ExternalScriptDao dao = new ExternalScriptDao();
        try {
            ExternalScript script = ScriptServiceUtil.TOToExternalScript(ExternalScriptRequest);
            script = dao.saveOrUpdate(script);
            return ScriptServiceUtil.externalScriptToTO(script);
        } catch (Exception e) {
            LOGGER.error("Error saving external script: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "external script", e);
        }
    }

    @Override
    public String deleteExternalScript(Integer externalScriptId) {
        ExternalScriptDao dao = new ExternalScriptDao();
        try {
            ExternalScript script = dao.findById(externalScriptId);
            if (script == null) {
                LOGGER.warn("External script with external script id " +  externalScriptId + " does not exist");
                return "External script with external script id " +  externalScriptId + " does not exist";
            } else {
                dao.delete(script);
                return "";
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting external script : " + e, e);
            throw new GenericServiceDeleteException("script", "external script", e);
        }
    }
}
