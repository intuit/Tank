/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script.util;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.script.models.StepDataTO;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.RequestDataPhase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ScriptServiceUtil
 * 
 * @author dangleton
 * 
 */
public class ScriptServiceUtil {
    private static final Logger LOG = LogManager.getLogger(ScriptServiceUtil.class);

    private ScriptServiceUtil() {
    }

    /**
     * create a descriptor from the script object
     * 
     * @param script
     */
    public static ScriptTO scriptToTransferObject(Script script) {
        return ScriptTO.builder()
                .withComments(script.getComments())
                .withCreated(script.getCreated())
                .withCreator(script.getCreator())
                .withId(script.getId())
                .withModified(script.getModified())
                .withName(script.getName())
                .withProductName(script.getProductName())
                .withSteps(scriptStepsToTransferobjectList(script.getScriptSteps()))
                .build();
    }

    public static List<ScriptStepTO> scriptStepsToTransferobjectList(List<ScriptStep> steps) {
       return steps.stream().map(ScriptServiceUtil::scriptStepToTransferObject).collect(Collectors.toList());
    }

    /**
     * create a Script Entity object form ret Descriptor
     * 
     * @return the Script
     */
    public static Script transferObjectToScript(ScriptTO to) {
        Script s = new Script();
        s.setComments(to.getComments());
        s.setCreated(to.getCreated());
        s.setCreator(to.getCreator());
        s.setId(to.getId() != null ? to.getId() : 0);
        s.setModified(to.getModified());
        s.setName(to.getName());
        s.setProductName(to.getProductName());
        s.setRuntime(to.getRuntime());
        to.getSteps().forEach(stepTO -> s.addStep(transferObjectToScriptStep(stepTO)));
        return s;
    }

    /**
     * @param step
     *            the step
     */
    public static ScriptStepTO scriptStepToTransferObject(ScriptStep step) {
        return ScriptStepTO.builder()
                .withUuid(step.getUuid())
                .withScriptGroupName(step.getScriptGroupName())
                .withMethod(step.getMethod())
                .withType(step.getType())
                .withLabel(step.getLabel())
                .withUrl(step.getUrl())
                .withResult(step.getResult())
                .withMimetype(step.getMimetype())
                .withLoggingKey(step.getLoggingKey())
                .withName(step.getName())
                .withOnFail(step.getOnFail())
                .withStepIndex(step.getStepIndex())
                .withPayload(step.getPayload())
                .withResponse((step.getResponse() != null) ? URLEncoder.encode(step.getResponse(), StandardCharsets.UTF_8) : null)
                .withSimplePath(step.getSimplePath())
                .withHostname(step.getHostname())
                .withProtocol(step.getProtocol())
                .withComments(step.getComments())
                .withRespFormat(step.getRespFormat())
                .withReqFormat(step.getReqFormat())
                .withData(convertData(step.getData()))
                .withPostDatas(convertData(step.getPostDatas()))
                .withQueryStrings(convertData(step.getQueryStrings()))
                .withRequestCookies(convertData(step.getRequestCookies()))
                .withRequestheaders(convertData(step.getRequestheaders()))
                .withResponseCookies(convertData(step.getResponseCookies()))
                .withResponseData(convertData(step.getResponseData()))
                .withResponseheaders(convertData(step.getResponseheaders()))
                .build();
    }

    /**
     * @param step
     *            the step
     */
    public static ScriptStepTO copy(ScriptStepTO step) {
        return ScriptStepTO.builder()
                .withUuid(step.getUuid())
                .withScriptGroupName(step.getScriptGroupName())
                .withMethod(step.getMethod())
                .withType(step.getType())
                .withLabel(step.getLabel())
                .withUrl(step.getUrl())
                .withResult(step.getResult())
                .withMimetype(step.getMimetype())
                .withLoggingKey(step.getLoggingKey())
                .withName(step.getName())
                .withOnFail(step.getOnFail())
                .withStepIndex(step.getStepIndex())
                .withPayload(step.getPayload())
                .withResponse((step.getResponse() != null) ? URLEncoder.encode(step.getResponse(), StandardCharsets.UTF_8) : null)
                .withSimplePath(step.getSimplePath())
                .withHostname(step.getHostname())
                .withProtocol(step.getProtocol())
                .withComments(step.getComments())
                .withRespFormat(step.getRespFormat())
                .withReqFormat(step.getReqFormat())
                .withData(copyData(step.getData()))
                .withPostDatas(copyData(step.getPostDatas()))
                .withQueryStrings(copyData(step.getQueryStrings()))
                .withRequestCookies(copyData(step.getRequestCookies()))
                .withRequestheaders(copyData(step.getRequestheaders()))
                .withResponseCookies(copyData(step.getResponseCookies()))
                .withResponseData(copyData(step.getResponseData()))
                .withResponseheaders(copyData(step.getResponseheaders()))
                .build();
    }

    public static ScriptStep transferObjectToScriptStep(ScriptStepTO to) {
        ScriptStep ret = new ScriptStep();
        ret.setComments(to.getComments());
        ret.setHostname(to.getHostname());
        ret.setUuid(to.getUuid());
        ret.setLabel(to.getLabel());
        ret.setMethod(to.getMethod());
        ret.setMimetype(to.getMimetype());
        ret.setLoggingKey(to.getLoggingKey());
        ret.setPayload(to.getPayload());
        ret.setResponse((to.getResponse() != null) ? URLDecoder.decode(to.getResponse(), StandardCharsets.UTF_8) : null);
        ret.setName(to.getName());
        ret.setOnFail(to.getOnFail());
        ret.setProtocol(to.getProtocol());
        ret.setReqFormat(to.getReqFormat());
        ret.setRespFormat(to.getRespFormat());
        ret.setResult(to.getResult());
        ret.setScriptGroupName(to.getScriptGroupName());
        ret.setSimplePath(to.getSimplePath());
        ret.setStepIndex(to.getStepIndex());
        ret.setType(to.getType());
        ret.setUrl(to.getUrl());
        ret.setData(convertDataTO(to.getData()));
        ret.setPostDatas(convertDataTO(to.getPostDatas()));
        ret.setQueryStrings(convertDataTO(to.getQueryStrings()));
        ret.setRequestCookies(convertDataTO(to.getRequestCookies()));
        ret.setRequestheaders(convertDataTO(to.getRequestheaders()));
        ret.setResponseCookies(convertDataTO(to.getResponseCookies()));
        ret.setResponseData(convertDataTO(to.getResponseData()));
        ret.setResponseheaders(convertDataTO(to.getResponseheaders()));
        return ret;
    }

    public static StepDataTO requestDataToTransferObject(RequestData data) {
        return StepDataTO.builder()
                .withKey(data.getKey())
                .withValue(data.getValue())
                .withType(data.getType())
                .withPhase(data.getPhase().name())
                .build();
    }

    public static StepDataTO copyStepDataTO(StepDataTO data) {
        return data.toBuilder().build();
    }

    /**
     * @return
     */
    public static RequestData transferObjectToRequestData(StepDataTO to) {
        RequestData ret = new RequestData();
        ret.setKey(to.getKey());
        ret.setType(to.getType());
        ret.setValue(to.getValue());

        RequestDataPhase phase = null;
        try {
            phase = RequestDataPhase.valueOf(to.getPhase() != null ? to.getPhase() : RequestDataPhase.POST_REQUEST
                    .name());
        } catch (Exception e) {
            // if phase is set incorrectly or non existant.
            LOG.warn("Error setting phase: " + e.toString());
        }
        ret.setPhase(phase != null ? phase : RequestDataPhase.POST_REQUEST);
        return ret;
    }

    /**
     * create a descriptor from the script object
     * 
     * @param script
     */
    public static ScriptDescription scriptToScriptDescription(Script script) {
        return ScriptDescription.builder()
                .withComments(script.getComments())
                .withCreated(script.getCreated())
                .withCreator(script.getCreator())
                .withId(script.getId())
                .withModified(script.getModified())
                .withName(script.getName())
                .withProductName(script.getProductName())
                .withRuntime(script.getRuntime())
                .build();
    }

    /**
     * @param sd
     * @return
     */
    public static Script scriptDescriptionToScript(ScriptDescription sd) {
        Script ret = new Script();
        ret.setComments(sd.getComments());
        ret.setCreated(sd.getCreated());
        ret.setCreator(sd.getCreator());
        ret.setId(sd.getId());
        ret.setModified(sd.getModified());
        ret.setName(sd.getName());
        ret.setProductName(sd.getProductName());
        ret.setRuntime(sd.getRuntime());
        return ret;
    }

    /**
     * @param data
     * @return
     */
    private static Set<RequestData> convertDataTO(Set<StepDataTO> data) {
        return data.stream().map(ScriptServiceUtil::transferObjectToRequestData).collect(Collectors.toSet());
    }

    /**
     * @param data
     * @return
     */
    private static Set<StepDataTO> convertData(Set<RequestData> data) {
        if (data != null) {
            return data.stream().map(ScriptServiceUtil::requestDataToTransferObject).collect(Collectors.toSet());
        }
        return new HashSet<StepDataTO>();
    }

    /**
     * @param data
     * @return
     */
    private static Set<StepDataTO> copyData(Set<StepDataTO> data) {
        if (data != null) {
            return data.stream().map(ScriptServiceUtil::copyStepDataTO).collect(Collectors.toSet());
        }
        return new HashSet<StepDataTO>();
    }

    /**
     * @param script
     * @return
     */
    public static ExternalScriptTO externalScriptToTO(ExternalScript script) {
        return ExternalScriptTO.builder()
                .withId(script.getId())
                .withCreated(script.getCreated())
                .withCreator(script.getCreator())
                .withModified(script.getModified())
                .withName(script.getName())
                .withProductName(script.getProductName())
                .withScript(script.getScript())
                .build();
    }

    /**
     * @param script
     * @return
     */
    public static ExternalScript TOToExternalScript(ExternalScriptTO script) {
        ExternalScript ret = new ExternalScript();
        ret.setId(script.getId());
        if (script.getId() != 0) {
            ret.setModified(script.getModified());
            ret.setCreated(script.getCreated());
        }

        ret.setCreator(script.getCreator() != null ? script.getCreator() : "");
        ret.setName(script.getName());
        ret.setProductName(script.getProductName());
        ret.setScript(script.getScript());
        return ret;
    }

    public static ScriptTO parseXMLtoScriptTO(InputStream inputStream) {
        try {
            //Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            spf.setXIncludeAware(false);

            Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(inputStream));

            JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
            return (ScriptTO) ctx.createUnmarshaller().unmarshal(xmlSource);
        } catch (ParserConfigurationException | JAXBException | SAXException e) {
            LOG.error("Error unmarshalling script: " + e.getMessage() , e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
