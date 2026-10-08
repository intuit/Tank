/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ScriptDocument;
import com.intuit.tank.rest.mvc.rest.models.ScriptSummary;
import com.intuit.tank.rest.mvc.rest.models.NewScriptRequest;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.util.PageRequests;
import com.intuit.tank.rest.mvc.rest.util.ScriptDocumentMapper;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.script.models.*;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.vm.settings.AccessRight;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import jakarta.servlet.ServletContext;

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
    public Map<String, String> createScript(String name, Integer id,
                                            String recording, String copy,
                                            Integer sourceId, String contentEncoding,
                                            MultipartFile file) throws IOException {
        return createScript(name, id, recording, copy, sourceId, contentEncoding, file, null, null);
    }

    @Override
    public Map<String, String> createScript(String name, Integer id, String recording, String copy, Integer sourceId,
                                            String contentEncoding, MultipartFile file, String productName,
                                            List<Integer> filterIds) throws IOException {
        try {
            if(recording != null) {
                return uploadProxyScript(name, id, contentEncoding, file, productName, filterIds);
            } else if(copy != null) {
                if(sourceId == null) {
                    throw new IllegalArgumentException("SourceId must be provided when copying a script");
                }
                return copyTankScript(name, sourceId);
            } else {
                return updateTankScript(contentEncoding, file);
            }
        } catch (GenericServiceForbiddenAccessException | GenericServiceBadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error creating script: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "script", e);
        }
    }

    private Map<String, String> copyTankScript(String name, Integer sourceId) {
        Map<String, String> payload = new HashMap<>();
        try {

            RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, "scripts");
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Must provide a script name to copy from existing script");
            } else {
                Script script = new ScriptDao().findById(sourceId);
                if (script != null) {
                    Script copyScript = ScriptUtil.copyScript(
                            RestAuthorization.currentUserName()
                            , name, script);
                    copyScript = new ScriptDao().saveOrUpdate(copyScript);
                    payload.put("message", "Script " + copyScript.getName() + " with script ID " + copyScript.getId() + " created successfully (copied from script ID " + sourceId + " - " + script.getName());
                } else {
                    throw new IllegalArgumentException("Source script cannot be found");
                }
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error copying script: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "script", e);
        }
        return payload;
    }


    private Map<String, String> uploadProxyScript(String name, Integer scriptId, String contentEncoding, MultipartFile file,
                                                  String productName, List<Integer> filterIds) throws IOException {
        List<ScriptFilter> filters = findFilters(filterIds);
        Map<String, String> payload = new HashMap<>();
        scriptId = scriptId == null ? 0 : scriptId;
        contentEncoding = contentEncoding == null ? "" : contentEncoding;
        try (BufferedReader bufferedReader = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                new BufferedReader(new InputStreamReader(new GZIPInputStream(file.getInputStream()))) :
                new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Script script = new ScriptDao().findById(scriptId);
            if (script == null){
                RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, "scripts");
                script = new Script();
                script.setName("New");
                script.setCreator(RestAuthorization.currentUserName());
            } else {
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, script, "scripts");
                payload.put("message", "Script with script ID " + scriptId + " overwritten with new script content");
            }

            ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>().getManagedBean(servletContext,
                    ScriptProcessor.class);

            scriptProcessor.setScript(script);
            if (StringUtils.isNotEmpty(name)) {
                script.setName(name);
            }
            if (productName != null) {
                script.setProductName(productName);
            }
            scriptProcessor.getScriptSteps(bufferedReader, filters);
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
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error uploading script file: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", "new script via script upload", e);
        }
        return payload;
    }

    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageEventSender sender = new ServletInjector<MessageEventSender>().getManagedBean(servletContext, MessageEventSender.class);
        sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
    }

    private Map<String, String> updateTankScript(String  contentEncoding, MultipartFile file) throws IOException {
        Map<String, String> payload = new HashMap<>();
        contentEncoding = contentEncoding == null ? "" : contentEncoding;

        try (InputStream fileInputStream = file.getInputStream();
             InputStream inputStream = "gzip".equalsIgnoreCase(contentEncoding) ? new GZIPInputStream(fileInputStream) : fileInputStream) {
            ScriptDao dao = new ScriptDao();

            ScriptTO scriptTo = ScriptServiceUtil.parseXMLtoScriptTO(inputStream);
            Script script = ScriptServiceUtil.transferObjectToScript(scriptTo);
            if (script.getId() > 0) {
                Script existing = dao.findById(script.getId());
                if (existing == null) {
                    throw new GenericServiceBadRequestException("scripts", "updating script", "updating script - Cannot update a script that does not exist (script id " + script.getId() + ")");
                }
                if (!existing.getName().equals(script.getName())) {
                    throw new GenericServiceBadRequestException("scripts", "updating script", "updating script - Cannot change the name of the existing script " + existing.getName());
                }
                // ownership comes from the stored script, never from the uploaded XML
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, existing, "scripts");
                script.setCreator(existing.getCreator());
                script.setSerializedScriptStepId(existing.getSerializedScriptStepId());
            } else {
                RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, "scripts");
                script.setCreator(RestAuthorization.currentUserName());
            }
            script = dao.saveOrUpdate(script);
            payload.put("message", "Script " + script.getName() + " with script ID " + script.getId() + " updated successfully");
            payload.put("scriptId", Integer.toString(script.getId()));
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating script file: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("scripts", e.getMessage(), e);
        }

        return payload;
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
            LOGGER.error("Error returning script description: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "script", e);
        }
    }

    @Override
    public ScriptDescriptionContainer getScripts() {
        try {
            ScriptDao dao = new ScriptDao();
            List<Script> all = dao.findAll();
            List<ScriptDescription> result = all.stream().map(ScriptServiceUtil::scriptToScriptDescription).collect(Collectors.toList());
            return ScriptDescriptionContainer.builder().withScripts(result).build();
        } catch (Exception e) {
            LOGGER.error("Error returning all script: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "all script", e);
        }
    }

    @Override
    public Map<Integer, String> getAllScriptNames(){
        try {
            List<Script> all = new ScriptDao().findAll();
            return all.stream().sorted(Comparator.comparing(Script::getModified).reversed())
                               .collect(Collectors.toMap(Script::getId, Script::getName, (e1, e2) -> e1, LinkedHashMap::new));
        } catch (Exception e) {
            LOGGER.error("Error returning all script names: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "all script names", e);
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
            LOGGER.error("Error downloading Tank XML script file: {}", e.getMessage(), e);
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
            LOGGER.error("Error downloading Tank Harness script file: {}", e.getMessage(), e);
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
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, script, "scripts");
                dao.delete(script);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting script : {}", e, e);
            throw new GenericServiceDeleteException("script", "script", e);
        }
    }

    // External Scripts

    @Override
    public ExternalScriptContainer getExternalScripts() {
        try {
            ExternalScriptDao dao = new ExternalScriptDao();
            List<ExternalScript> allScripts = dao.findAll();
            return ExternalScriptContainer.builder()
                    .withScripts(allScripts.stream()
                            .map(ScriptServiceUtil::externalScriptToTO).collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error returning all external script : {}", e, e);
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
            LOGGER.error("Error returning the external script : {}", e, e);
            throw new GenericServiceResourceNotFoundException("script", "external script", e);
        }
    }

    @Override
    public ExternalScriptTO createExternalScript(ExternalScriptTO ExternalScriptRequest) {
        ExternalScriptDao dao = new ExternalScriptDao();
        ExternalScript existing = ExternalScriptRequest != null && ExternalScriptRequest.getId() > 0
                ? dao.findById(ExternalScriptRequest.getId()) : null;
        if (existing != null) {
            RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, existing, "scripts");
        } else {
            RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, "scripts");
        }
        try {
            ExternalScript script = ScriptServiceUtil.TOToExternalScript(ExternalScriptRequest);
            script.setCreator(existing != null ? existing.getCreator() : RestAuthorization.currentUserName());
            script = dao.saveOrUpdate(script);
            return ScriptServiceUtil.externalScriptToTO(script);
        } catch (Exception e) {
            LOGGER.error("Error saving external script: {}", e.getMessage(), e);
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
            LOGGER.error("Error downloading Tank XML external script file: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("scripts", "Tank XML external script file", e);
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
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, script, "scripts");
                dao.delete(script);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting external script : {}", e, e);
            throw new GenericServiceDeleteException("script", "external script", e);
        }
    }

    static final int MAX_BULK_DELETE = 100;
    private static final String SERVICE = "scripts";

    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "id", BaseEntity.PROPERTY_ID,
            "name", "name",
            "productName", "productName",
            "owner", OwnableEntity.PROPERTY_CREATOR,
            "created", BaseEntity.PROPERTY_CREATE,
            "modified", BaseEntity.PROPERTY_MODIFIED,
            "runtime", "runtime");

    @Override
    public PageResponse<ScriptSummary> listScripts(Integer page, Integer size, String sort, String owner, String q) {
        RestAuthorization.requireUser(SERVICE);
        Map<String, Object> filters = new HashMap<>();
        filters.put(OwnableEntity.PROPERTY_CREATOR, owner);
        PagedQuery query = PageRequests.toQuery(SERVICE, page, size, sort, SORTABLE_FIELDS, "modified,desc", filters, q,
                List.of("name", "productName", "comments"));
        PagedResult<Script> result = new ScriptDao().findPaged(query);
        return new PageResponse<>(result.items().stream().map(ScriptServiceV2Impl::summary).collect(Collectors.toList()),
                result.total(), query.page(), query.size());
    }

    @Override
    public ScriptDocument getScriptDocument(Integer scriptId) {
        RestAuthorization.requireUser(SERVICE);
        return document(findScript(scriptId));
    }

    @Override
    public synchronized ScriptDocument updateScriptDocument(Integer scriptId, ScriptDocument document) {
        RestAuthorization.requireUser(SERVICE);
        if (document == null) {
            throw new GenericServiceBadRequestException(SERVICE, "script", "request body is required");
        }
        Script script = findScript(scriptId);
        RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, script, SERVICE);
        if (document.modified() == null) {
            throw new GenericServiceBadRequestException(SERVICE, "modified",
                    "modified is required; send the value from the last GET");
        }
        if (!sameSecond(document.modified(), script.getModified())) {
            throw new GenericServiceConflictException(SERVICE,
                    "Script " + scriptId + " was changed by someone else since it was loaded; reload it and try again");
        }
        List<String> errors = ScriptDocumentMapper.validate(document);
        if (errors.isEmpty()) {
            errors = ScriptDocumentMapper.apply(document, script);
        }
        if (!errors.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "script", String.join("; ", errors));
        }
        try {
            script = new ScriptDao().saveOrUpdate(script);
        } catch (RuntimeException e) {
            LOGGER.error("Error saving script {}: {}", scriptId, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "script", e);
        }
        sendMsg(script, ModificationType.UPDATE);
        LOGGER.info("{} saved script {} with {} steps", RestAuthorization.currentUserName(), scriptId,
                script.getScriptSteps().size());
        return document(findScript(scriptId));
    }

    @Override
    public ScriptSummary copyScript(Integer scriptId, CopyRequest request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, SERVICE);
        String name = request != null ? StringUtils.trimToNull(request.name()) : null;
        if (name == null) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name is required");
        }
        if (name.length() > 255) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name must be at most 255 characters");
        }
        Script source = findScript(scriptId);
        Script copy = ScriptUtil.copyScript(RestAuthorization.currentUserName(), name, source);
        // the copy is new (id 0), so this gives its steps their own uuids
        ScriptUtil.setScriptStepLabels(copy);
        try {
            copy = new ScriptDao().saveOrUpdate(copy);
        } catch (RuntimeException e) {
            LOGGER.error("Error copying script {}: {}", scriptId, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "script copy", e);
        }
        sendMsg(copy, ModificationType.ADD);
        LOGGER.info("{} copied script {} to {} ({})", RestAuthorization.currentUserName(), scriptId, copy.getId(), name);
        return summary(copy);
    }

    @Override
    public String getStepResponse(Integer scriptId, String stepUuid) {
        RestAuthorization.requireUser(SERVICE);
        Script script = findScript(scriptId);
        ScriptStep step = script.getScriptSteps().stream()
                .filter(s -> s.getUuid() != null && s.getUuid().equals(stepUuid))
                .findFirst()
                .orElseThrow(() -> new GenericServiceResourceNotFoundException(SERVICE, "step " + stepUuid, null));
        if (step.getResponse() == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "recorded response for step " + stepUuid, null);
        }
        return step.getResponse();
    }

    private ScriptDocument document(Script script) {
        boolean owner = RestAuthorization.isOwner(script);
        return ScriptDocumentMapper.toDocument(script, new ScriptDocument.Permissions(
                owner || RestAuthorization.hasRight(AccessRight.EDIT_SCRIPT),
                owner || RestAuthorization.hasRight(AccessRight.DELETE_SCRIPT)));
    }

    @Override
    public ScriptSummary createBlankScript(NewScriptRequest request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, SERVICE);
        String name = request != null ? StringUtils.trimToNull(request.name()) : null;
        if (name == null) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name is required");
        }
        if (name.length() > 255) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name must be at most 255 characters");
        }
        Script script = new Script();
        script.setName(name);
        script.setProductName(StringUtils.trimToNull(request.productName()));
        script.setComments(StringUtils.trimToNull(request.comments()));
        script.setCreator(RestAuthorization.currentUserName());
        try {
            script = new ScriptDao().saveOrUpdate(script);
        } catch (RuntimeException e) {
            LOGGER.error("Error creating script {}: {}", name, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "script", e);
        }
        sendMsg(script, ModificationType.ADD);
        LOGGER.info("{} created blank script {} ({})", RestAuthorization.currentUserName(), script.getId(), name);
        return summary(script);
    }

    @Override
    public BulkDeleteResult deleteScripts(List<Integer> scriptIds) {
        RestAuthorization.requireUser(SERVICE);
        if (scriptIds == null || scriptIds.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at least one id is required");
        }
        List<Integer> ids = scriptIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.size() > MAX_BULK_DELETE) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at most " + MAX_BULK_DELETE + " ids at a time");
        }
        ScriptDao dao = new ScriptDao();
        List<Script> found = new ArrayList<>();
        List<Integer> notFound = new ArrayList<>();
        for (Integer id : ids) {
            Script script = dao.findById(id);
            if (script == null) {
                notFound.add(id);
            } else {
                found.add(script);
            }
        }
        // check every script before deleting any, so a forbidden one leaves all of them in place
        for (Script script : found) {
            RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, script, SERVICE);
        }
        List<Integer> deleted = new ArrayList<>();
        for (Script script : found) {
            try {
                dao.delete(script);
            } catch (RuntimeException e) {
                // e.g. a project still runs the script
                LOGGER.error("Error deleting script {}: {}", script.getId(), e.getMessage(), e);
                throw new GenericServiceDeleteException(SERVICE, "script " + script.getId()
                        + (deleted.isEmpty() ? "" : " (already deleted: " + deleted + ")"), e);
            }
            deleted.add(script.getId());
            sendMsg(script, ModificationType.DELETE);
        }
        LOGGER.info("{} deleted scripts {}", RestAuthorization.currentUserName(), deleted);
        return new BulkDeleteResult(deleted, notFound);
    }

    private static ScriptSummary summary(Script s) {
        return new ScriptSummary(s.getId(), s.getName(), s.getProductName(), s.getComments(), s.getCreator(),
                s.getCreated(), s.getModified(), s.getRuntime());
    }

    private static Script findScript(Integer scriptId) {
        Script script = scriptId != null ? new ScriptDao().findById(scriptId) : null;
        if (script == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "script " + scriptId, null);
        }
        return script;
    }

    /**
     * @return the filters with these ids, in the order given
     * @throws GenericServiceBadRequestException when an id has no filter
     */
    private static List<ScriptFilter> findFilters(List<Integer> filterIds) {
        if (filterIds == null || filterIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = filterIds.stream().distinct().collect(Collectors.toList());
        Map<Integer, ScriptFilter> byId = new ScriptFilterDao().findForIds(ids).stream()
                .collect(Collectors.toMap(ScriptFilter::getId, f -> f, (a, b) -> a));
        List<Integer> missing = ids.stream().filter(id -> !byId.containsKey(id)).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "filterIds", "no script filters with ids " + missing);
        }
        return ids.stream().map(byId::get).collect(Collectors.toList());
    }

    /** Compares save times to the second: the database may not store milliseconds. */
    static boolean sameSecond(java.util.Date a, java.util.Date b) {
        return a != null && b != null && a.getTime() / 1000 == b.getTime() / 1000;
    }
}
