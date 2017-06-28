/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.script;

/*
 * #%L
 * Script Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intuit.tank.api.model.v1.script.ExternalScriptContainer;
import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import com.intuit.tank.api.model.v1.script.ScriptFilterRequest;
import com.intuit.tank.api.model.v1.script.ScriptStepContainer;
import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptUploadRequest;
import com.intuit.tank.api.script.util.ScriptServiceUtil;
import com.intuit.tank.api.service.v1.script.ScriptService;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.dao.FilterDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.script.util.ScriptFilterUtil;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

/**
 * DataFileServiceV1
 * 
 * @author dangleton
 * 
 */
/**
 * @author hsomani
 * 
 */
@Path("/v1/script-service")
public class ScriptServiceV1 implements ScriptService {

    private static final Logger LOG = LogManager.getLogger(ScriptServiceV1.class);

    @Context
    private ServletContext servletContext;

    @Context
    private UriInfo uriInfo;

    /**
     * @{inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response updateTankScript(FormDataMultiPart formData) {
        ScriptTO scriptTo = null;
        InputStream is = null;
        Map<String, List<FormDataBodyPart>> fields = formData.getFields();
        ScriptDao dao = new ScriptDao();
        for (Entry<String, List<FormDataBodyPart>> entry : fields.entrySet()) {
            String formName = entry.getKey();
            LOG.debug("Entry name: " + formName);
            for (FormDataBodyPart part : entry.getValue()) {
                MediaType mediaType = part.getMediaType();
                LOG.debug("MediaType " + mediaType);
                if (MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(mediaType)) {
                    // get the file
                    is = part.getValueAs(InputStream.class);
                }
            }
        }
        ResponseBuilder responseBuilder = null;
        if (is != null) {
            try {
                //Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                
                Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(is));
                
                JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
                scriptTo = (ScriptTO) ctx.createUnmarshaller().unmarshal(xmlSource);
                Script script = ScriptServiceUtil.transferObjectToScript(scriptTo);
                if (script.getId() > 0) {
                    Script existing = dao.findById(script.getId());
                    if (existing == null) {
                        LOG.error("Error updating script: Script passed with unknown id.");
                        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                    }
                    if (!existing.getName().equals(script.getName())) {
                        LOG.error("Error updating script: Cannot change the name of an existing Script.");
                        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }
                script = dao.saveOrUpdate(script);
                responseBuilder = Response.ok();
                responseBuilder.entity(Integer.toString(script.getId()));
            } catch (Exception e) {
                LOG.error("Error unmarshalling script: " + e.getMessage(), e);
                throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response convertScript(FormDataMultiPart formData) {
        ScriptUploadRequest request = null;
        InputStream is = null;
        Map<String, List<FormDataBodyPart>> fields = formData.getFields();
        ScriptDao dao = new ScriptDao();
        for (Entry<String, List<FormDataBodyPart>> entry : fields.entrySet()) {
            String formName = entry.getKey();
            LOG.debug("Entry name: " + formName);
            for (FormDataBodyPart part : entry.getValue()) {
                MediaType mediaType = part.getMediaType();
                if (MediaType.APPLICATION_XML_TYPE.equals(mediaType)
                        || MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
                    request = part.getEntityAs(ScriptUploadRequest.class);

                } else if (MediaType.TEXT_PLAIN_TYPE.equals(mediaType)) {
                    String s = part.getEntityAs(String.class);
                    if ("xmlString".equalsIgnoreCase(formName)) {
                        try {
                            //Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
                            SAXParserFactory spf = SAXParserFactory.newInstance();
                            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                            
                            Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(s)));
                            
                            JAXBContext ctx = JAXBContext.newInstance(ScriptUploadRequest.class.getPackage().getName());
                            request = (ScriptUploadRequest) ctx.createUnmarshaller().unmarshal(xmlSource);
                        } catch (JAXBException e) {
                            throw new RuntimeException(e);
                        } catch (SAXException saxe) {
                        	  throw new RuntimeException(saxe);
                        } catch (ParserConfigurationException pce) {
                          throw new RuntimeException(pce);
                        }
                    }
                } else if (MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(mediaType)) {
                    // get the file
                    is = part.getValueAs(InputStream.class);
                }
            }
        }
        ResponseBuilder responseBuilder = null;
        if (request == null) {
            responseBuilder = Response.status(Status.BAD_REQUEST);
            responseBuilder.entity("Requests to store Scripts must include a ScriptUploadRequest.");
        } else {
            Script script = descriptorToScript(dao, request.getScript());
            if (is != null) {
                try {
                    ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>().getManagedBean(
                            servletContext, ScriptProcessor.class);
                    List<ScriptStep> scriptSteps = scriptProcessor.getScriptSteps(new BufferedReader(
                            new InputStreamReader(is)),
                            getFilters(request.getFilterIds()));
                    scriptProcessor.setScriptSteps(script, scriptSteps);
                    script = dao.saveOrUpdate(script);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
            try {
                URI location = uriInfo.getBaseUriBuilder().path(ScriptService.class)
                        .path(ScriptService.class.getMethod("getScript", Integer.class)).build(script.getId());
                responseBuilder = Response.created(location);
            } catch (Exception e) {
                LOG.error("Error building uri: " + e.getMessage(), e);
                throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        return responseBuilder.build();

    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response updateScript(Integer id, ScriptTO scriptTo) {
        Script s = ScriptServiceUtil.transferObjectToScript(scriptTo);
        ScriptDao dao = new ScriptDao();
        Script storedScript = dao.findById(id);
        ResponseBuilder responseBuilder = null;
        if (storedScript != null) {
            try {
                s = dao.saveOrUpdate(s);
                URI location = uriInfo.getBaseUriBuilder().path(ScriptService.class)
                        .path(ScriptService.class.getMethod("getScript", Integer.class)).build(s.getId());
                responseBuilder = Response.created(location);
            } catch (Exception e) {
                LOG.error("Error building uri: " + e.getMessage(), e);
                throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            responseBuilder = Response.status(Status.NOT_FOUND);
        }
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response deleteScript(Integer id) {
        return delete(id);
    }

    /**
     * Checks that the script with the given id exists. If it exists deletes the script and returns no content else
     * sends back a bad request status to the client.
     * 
     * @param scriptId
     *            scriptId is the id that will be used to delete the Script
     * @return Response with status BAD_REQUEST or noContent.
     */
    private Response delete(int scriptId) {

        ResponseBuilder responseBuilder = Response.noContent();
        ScriptDao dao = new ScriptDao();

        try {
            Script script = dao.findById(scriptId);
            if (script == null) {
                LOG.warn("Script with id " + scriptId + " does not exist.");
                responseBuilder.status(Status.BAD_REQUEST);
                responseBuilder.entity("Script with id " + scriptId + "does not exist.");
            } else {
                dao.delete(script);
            }
        } catch (RuntimeException e) {
            LOG.error("Error deleting project : " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("An error occurred while deleting the Script");
        }

        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getScriptDescriptions() {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        List<Script> all = dao.findAll();
        List<ScriptDescription> result = new ArrayList<ScriptDescription>();
        for (Script s : all) {
            result.add(ScriptServiceUtil.scriptToScriptDescription(s));
        }
        responseBuilder.entity(new ScriptDescriptionContainer(result));
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response newScript(ScriptTO scriptTo) {
        scriptTo.setCreated(null);
        scriptTo.setModified(null);
        scriptTo.setId(0);
        Script savedScript = new ScriptDao().saveOrUpdate(ScriptServiceUtil.transferObjectToScript(scriptTo));
        ResponseBuilder responseBuilder = null;
        try {
            URI location = uriInfo.getBaseUriBuilder().path(ScriptService.class)
                    .path(ScriptService.class.getMethod("getScript", Integer.class)).build(savedScript.getId());
            responseBuilder = Response.created(location);
        } catch (Exception e) {
            LOG.error("Error building uri: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getScript(Integer id) {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        Script script = dao.findById(id);
        if (script != null) {
            responseBuilder.entity(ScriptServiceUtil.scriptToTransferObject(script));
        } else {
            responseBuilder = Response.noContent().status(Status.NOT_FOUND);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response downloadHarnessScript(Integer scriptId) {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        Script script = dao.findById(scriptId);
        if (script == null) {
            throw new RuntimeException("Cannot find Script with id of " + scriptId);
        }
        String filename = script.getName() + "_H.xml";
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).entity(getTestScriptForScript(script));
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    private StreamingOutput getTestScriptForScript(Script script) {

        HDWorkload hdWorkload = ConverterUtil.convertScriptToHdWorkload(script);
        final String scriptXML = ConverterUtil.getWorkloadXML(hdWorkload);
        return new StreamingOutput() {
            public void write(OutputStream outputStream) {
                BufferedReader in = null;
                try {
                    IOUtils.write(scriptXML, outputStream);
                } catch (IOException e) {
                    LOG.error("Error streaming file: " + e.toString(), e);
                    throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        };
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response downloadScript(Integer id) {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        final Script script = dao.findById(id);
        if (script != null) {
            String filename = script.getName() + "_TS.xml";
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
            final ScriptTO scriptTO = ScriptServiceUtil.scriptToTransferObject(script);
            StreamingOutput so = new StreamingOutput() {
                public void write(OutputStream outputStream) {
                    // Get the object of DataInputStream
                    try {
                        JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
                        Marshaller marshaller = ctx.createMarshaller();
                        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
                        marshaller.marshal(scriptTO, outputStream);
                    } catch (Exception e) {
                        LOG.error("Error streaming file: " + e.toString(), e);
                        throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
                    } finally {
                    }
                }
            };
            responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).entity(so);
        } else {
            responseBuilder = Response.noContent().status(Status.NOT_FOUND);
        }

        // add jobId to response
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getScriptSteps(Integer id, int start, int numSteps) {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        Script script = dao.findById(id);
        if (script != null) {
            int totalSize = script.getScriptSteps().size();
            if (numSteps <= 0) {
                numSteps = totalSize;
            }
            if (start <= 0) {
                start = 0;
            }
            List<ScriptStepTO> stepSlice = new ArrayList<ScriptStepTO>(numSteps);
            for (int i = start; i < totalSize && i < start + numSteps; i++) {
                stepSlice.add(ScriptServiceUtil.scriptStepToTransferObject(script.getScriptSteps().get(i)));
            }
            ScriptStepContainer result = ScriptStepContainer.builder().withSteps(stepSlice)
                    .withNumRemaining(totalSize - (start + stepSlice.size())).withNumRequsted(numSteps)
                    .withNumReturned(stepSlice.size()).withStartIndex(start).build();
            responseBuilder.entity(result);
        } else {
            responseBuilder.status(Status.NOT_FOUND);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getScriptDescription(Integer id) {
        ResponseBuilder responseBuilder = Response.ok();
        ScriptDao dao = new ScriptDao();
        Script script = dao.findById(id);
        if (script != null) {
            responseBuilder.entity(ScriptServiceUtil.scriptToScriptDescription(script));
        } else {
            responseBuilder.status(Status.NOT_FOUND);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response scriptFilterRequest(ScriptFilterRequest filterRequest) {
        ResponseBuilder responseBuilder = null;
        ScriptDao dao = new ScriptDao();
        try {
            Script script = dao.findById(filterRequest.getScriptId());
            ScriptFilterUtil.applyFilters(filterRequest.getFilterIds(), script);
            dao.saveOrUpdate(script);
            URI location = uriInfo.getBaseUriBuilder().path(ScriptService.class)
                    .path(ScriptService.class.getMethod("getScript", Integer.class)).build(script.getId());
            responseBuilder = Response.created(location);

        } catch (Exception e) {
            LOG.error("Error Applying Filters: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getExternalScript(Integer id) {
        ResponseBuilder responseBuilder = Response.ok();
        ExternalScriptDao dao = new ExternalScriptDao();
        ExternalScript script = dao.findById(id);
        if (script != null) {
            responseBuilder.entity(ScriptServiceUtil.externalScriptToTO(script));
        } else {
            responseBuilder.status(Status.NOT_FOUND);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getExternalScripts() {
        ResponseBuilder responseBuilder = Response.ok();
        ExternalScriptDao dao = new ExternalScriptDao();
        List<ExternalScript> all = dao.findAll();
        ExternalScriptContainer ret = new ExternalScriptContainer();
        for (ExternalScript s : all) {
            ret.getScripts().add(ScriptServiceUtil.externalScriptToTO(s));
        }
        responseBuilder.entity(ret);
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response saveOrUpdateExternalScript(ExternalScriptTO to) {
        ResponseBuilder responseBuilder = null;
        ExternalScriptDao dao = new ExternalScriptDao();
        try {
            ExternalScript script = ScriptServiceUtil.TOToExternalScript(to);
            script = dao.saveOrUpdate(script);
            URI location = uriInfo.getBaseUriBuilder().path(ScriptService.class)
                    .path(ScriptService.class.getMethod("getExternalScript", Integer.class)).build(script.getId());
            responseBuilder = Response.created(location);

        } catch (Exception e) {
            LOG.error("Error Saving External Script: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    private Script descriptorToScript(ScriptDao dao,
            ScriptDescription sd) {
        Script script;
        if (sd.getId() != null && sd.getId() != 0) {
            script = dao.findById(sd.getId());
            script.setComments(sd.getComments());
            script.setName(sd.getName());
            script.setRuntime(sd.getRuntime());
            script.setProductName(sd.getProductName());
            script.setCreator(sd.getCreator());
        } else {
            script = ScriptServiceUtil.scriptDescriptionToScript(sd);
        }
        return script;
    }

    private List<ScriptFilter> getFilters(List<Integer> filterIds) {
        List<ScriptFilter> filters = new ArrayList<ScriptFilter>();
        for (Integer id : filterIds) {
            ScriptFilter filter = new FilterDao().findById(id);
            if (filter != null) {
                filters.add(filter);
            }
        }
        return filters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.api.service.v1.script.ScriptService#deleteFilter(java.lang.Integer)
     */
    @Override
    public Response deleteFilter(Integer id) {
        return deleteFilterHelper(id);
    }

    /**
     * Deletes script filter from the system.
     * 
     * @param id
     *            id is the id of the filter that is to be deleted
     * @return
     */
    private Response deleteFilterHelper(Integer id) {
        ResponseBuilder responseBuilder = Response.noContent();
        ScriptFilterGroupDao sfgd = new ScriptFilterGroupDao();
        List<ScriptFilterGroup> scriptFilterGroupForFilter = sfgd.getScriptFilterGroupForFilter(id);

        for (Iterator<ScriptFilterGroup> iterator = scriptFilterGroupForFilter.iterator(); iterator.hasNext();) {
            ScriptFilterGroup scriptFilterGroup = iterator.next();
            Set<ScriptFilter> filters = scriptFilterGroup.getFilters();
            for (Iterator<ScriptFilter> iterator2 = filters.iterator(); iterator2.hasNext();) {
                ScriptFilter scriptFilter = iterator2.next();
                if (scriptFilter.getId() == id) {
                    iterator2.remove();
                }
            }
            sfgd.saveOrUpdate(scriptFilterGroup);
        }

        ScriptFilterDao sfd = new ScriptFilterDao();

        try {
            ScriptFilter scriptFilter = sfd.findById(id);
            if (scriptFilter == null) {
                LOG.warn("Script with id " + id + " does not exist.");
                responseBuilder.status(Status.BAD_REQUEST);
                responseBuilder.entity("Filter with id " + id + " does not exist.");
            } else {
                sfd.delete(scriptFilter);
            }
        } catch (RuntimeException e) {
            LOG.error("Error deleting script filter : " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("A server error occurred while deleting the filter.");
        }

        return responseBuilder.build();
    }

}
