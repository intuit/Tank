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
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.models.scripts.*;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.service.impl.v1.automation.MessageSender;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletContext;

@Service
public class ScriptServiceV2Impl implements ScriptServiceV2 {

    @Autowired
    private ServletContext servletContext;

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
    public ScriptDescription getScript(Integer scriptId) {
        try {
            ScriptDao dao = new ScriptDao();
            Script script = dao.findById(scriptId);
            if (script != null) {
                return ScriptServiceUtil.scriptToScriptDescription(script);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Error returning script description: " + e.getMessage(), e);
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
    public Map<String, StreamingResponseBody> downloadScript(Integer scriptId){
        try {
            StreamingResponseBody streamingResponse;
            Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
            ScriptDao dao = new ScriptDao();
            final Script script = dao.findById(scriptId);
            if (script == null) {
                return null;
            } else {
                String filename = script.getName() + "_TS.xml";
                final ScriptTO scriptTO = ScriptServiceUtil.scriptToTransferObject(script);
                streamingResponse = ResponseUtil.getXMLStream(scriptTO);
                payload.put(filename, streamingResponse);
                return payload;
            }
        } catch (Exception e) {
            LOGGER.error("Error downloading Tank XML script file: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "Tank XML script file", e);
        }
    }

    @Override
    public Map<String, StreamingResponseBody> downloadHarnessScript(Integer scriptId){
        try {
            StreamingResponseBody streamingResponse;
            Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
            ScriptDao dao = new ScriptDao();
            final Script script = dao.findById(scriptId);
            if (script == null) {
                return null;
            } else {
                String filename = script.getName() + "_H.xml";
                final HDWorkload hdWorkload = ConverterUtil.convertScriptToHdWorkload(script);
                streamingResponse = ResponseUtil.getXMLStream(hdWorkload);
                payload.put(filename, streamingResponse);
                return payload;
            }
        } catch (Exception e) {
            LOGGER.error("Error downloading Tank Harness script file: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "Tank Harness script file", e);
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
    public Map<String, StreamingResponseBody> downloadExternalScript(Integer externalScriptId){
        try {
            StreamingResponseBody streamingResponse;
            Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
            ExternalScriptDao dao = new ExternalScriptDao();
            final ExternalScript script = dao.findById(externalScriptId);
            if (script == null) {
                return null;
            } else {
                String filename = script.getName() + "_ETS.xml";
                final ExternalScriptTO externalScriptTO = ScriptServiceUtil.externalScriptToTO(script);
                streamingResponse = ResponseUtil.getXMLStream(externalScriptTO);
                payload.put(filename, streamingResponse);
                return payload;
            }
        } catch (Exception e) {
            LOGGER.error("Error downloading Tank XML external script file: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "Tank XML external script file", e);
        }
    }

    @Override
    public Map<String, String> uploadProxyScript(String name, Integer scriptId, String contentEncoding, MultipartFile file) throws IOException {
        Map<String, String> payload = new HashMap<>();
        InputStream fileInputStream = file.getInputStream();
        scriptId = scriptId == null ? 0 : scriptId;
        contentEncoding = contentEncoding == null ? "" : contentEncoding;
        try {
            Script script = new ScriptDao().findById(scriptId);
            if (script == null){
                script = new Script();
                script.setName("New");
                script.setCreator("System");
            } else {
                payload.put("message", "Script with script ID " + scriptId + " overwritten with new script content");
            }

            ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>().getManagedBean(servletContext,
                    ScriptProcessor.class);

            scriptProcessor.setScript(script);
            if (StringUtils.isNotEmpty(name)) {
                script.setName(name);
            }

            BufferedReader bufferedReader = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                    new BufferedReader(new InputStreamReader(new GZIPInputStream(fileInputStream))) :
                    new BufferedReader(new InputStreamReader(fileInputStream));
            scriptProcessor.getScriptSteps(bufferedReader, new ArrayList<>());
            script = new ScriptDao().saveOrUpdate(script);
            sendMsg(script, ModificationType.UPDATE);
            if (scriptId.equals(0)) {
                payload.put("message", "Script with new script ID " + script.getId() + " has been uploaded");
            } else {
                if (!payload.containsKey("message")) {
                    payload.put("message", "Existing script with script ID " + scriptId + " could not be found, created new script " + script.getId());
                }
            }
            payload.put("scriptId", Integer.toString(script.getId()));
        } catch (Exception e) {
            LOGGER.error("Error uploading script file: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "new script via script upload", e);
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {}
        }
        return payload;
    }

    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageSender sender = new ServletInjector<MessageSender>().getManagedBean(servletContext, MessageSender.class);
        sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
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
