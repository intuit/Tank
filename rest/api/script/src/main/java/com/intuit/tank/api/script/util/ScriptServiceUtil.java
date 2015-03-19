/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.script.util;

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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.model.v1.script.StepDataTO;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.RequestDataPhase;

/**
 * ScriptServiceUtil
 * 
 * @author dangleton
 * 
 */
public class ScriptServiceUtil {
    private static final Logger LOG = Logger.getLogger(ScriptServiceUtil.class);

    private ScriptServiceUtil() {
    }

    /**
     * create a descriptor from the script object
     * 
     * @param script
     */
    public static ScriptTO scriptToTransferObject(Script script) {
        ScriptTO ret = new ScriptTO();
        ret.setComments(script.getComments());
        ret.setCreated(script.getCreated());
        ret.setCreator(script.getCreator());
        ret.setId(script.getId());
        ret.setModified(script.getModified());
        ret.setName(script.getName());
        ret.setProductName(script.getProductName());
        ret.setRuntime(script.getRuntime());
        ret.setSteps(scriptStepsToTransferobjectList(script.getScriptSteps()));
        return ret;
    }

    public static List<ScriptStepTO> scriptStepsToTransferobjectList(List<ScriptStep> steps) {
        List<ScriptStepTO> result = new ArrayList<ScriptStepTO>();
        for (ScriptStep step : steps) {
            result.add(scriptStepToTransferObject(step));
        }
        return result;
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

        for (ScriptStepTO stepTo : to.getSteps()) {
            s.addStep(transferObjectToScriptStep(stepTo));
        }
        return s;
    }

    /**
     * @param step
     *            the step
     */
    public static ScriptStepTO scriptStepToTransferObject(ScriptStep step) {
        ScriptStepTO ret = new ScriptStepTO();
        ret.setUuid(step.getUuid());
        ret.setScriptGroupName(step.getScriptGroupName());
        ret.setMethod(step.getMethod());
        ret.setType(step.getType());
        ret.setLabel(step.getLabel());
        ret.setUrl(step.getUrl());
        ret.setResult(step.getResult());
        ret.setMimetype(step.getMimetype());
        ret.setLoggingKey(step.getLoggingKey());
        ret.setName(step.getName());
        ret.setOnFail(step.getOnFail());
        ret.setStepIndex(step.getStepIndex());
        ret.setPayload(step.getPayload());
        if (step.getResponse() != null) {
            try {
                ret.setResponse(URLEncoder.encode(step.getResponse(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        ret.setSimplePath(step.getSimplePath());
        ret.setHostname(step.getHostname());
        ret.setProtocol(step.getProtocol());
        ret.setComments(step.getComments());
        ret.setRespFormat(step.getRespFormat());
        ret.setReqFormat(step.getReqFormat());

        ret.setData(convertData(step.getData()));
        ret.setPostDatas(convertData(step.getPostDatas()));
        ret.setQueryStrings(convertData(step.getQueryStrings()));
        ret.setRequestCookies(convertData(step.getRequestCookies()));
        ret.setRequestheaders(convertData(step.getRequestheaders()));
        ret.setResponseCookies(convertData(step.getResponseCookies()));
        ret.setResponseData(convertData(step.getResponseData()));
        ret.setResponseheaders(convertData(step.getResponseheaders()));
        return ret;
    }

    /**
     * @param step
     *            the step
     */
    public static ScriptStepTO copy(ScriptStepTO step) {
        ScriptStepTO ret = new ScriptStepTO();
        ret.setUuid(step.getUuid());
        ret.setScriptGroupName(step.getScriptGroupName());
        ret.setMethod(step.getMethod());
        ret.setType(step.getType());
        ret.setLabel(step.getLabel());
        ret.setUrl(step.getUrl());
        ret.setResult(step.getResult());
        ret.setMimetype(step.getMimetype());
        ret.setLoggingKey(step.getLoggingKey());
        ret.setName(step.getName());
        ret.setOnFail(step.getOnFail());
        ret.setStepIndex(step.getStepIndex());
        ret.setPayload(step.getPayload());
        if (step.getResponse() != null) {
            try {
                ret.setResponse(URLEncoder.encode(step.getResponse(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        ret.setSimplePath(step.getSimplePath());
        ret.setHostname(step.getHostname());
        ret.setProtocol(step.getProtocol());
        ret.setComments(step.getComments());
        ret.setRespFormat(step.getRespFormat());
        ret.setReqFormat(step.getReqFormat());

        ret.setData(copyData(step.getData()));
        ret.setPostDatas(copyData(step.getPostDatas()));
        ret.setQueryStrings(copyData(step.getQueryStrings()));
        ret.setRequestCookies(copyData(step.getRequestCookies()));
        ret.setRequestheaders(copyData(step.getRequestheaders()));
        ret.setResponseCookies(copyData(step.getResponseCookies()));
        ret.setResponseData(copyData(step.getResponseData()));
        ret.setResponseheaders(copyData(step.getResponseheaders()));
        return ret;
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
        if (to.getResponse() != null) {
            try {
                ret.setResponse(URLDecoder.decode(to.getResponse(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
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
        StepDataTO ret = new StepDataTO();
        ret.setKey(data.getKey());
        ret.setValue(data.getValue());
        ret.setType(data.getType());
        ret.setPhase(data.getPhase().name());
        return ret;
    }

    public static StepDataTO copyStepDataTO(StepDataTO data) {
        StepDataTO ret = new StepDataTO();
        ret.setKey(data.getKey());
        ret.setValue(data.getValue());
        ret.setType(data.getType());
        ret.setPhase(data.getPhase());
        return ret;
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
        ScriptDescription ret = new ScriptDescription();
        ret.setComments(script.getComments());
        ret.setCreated(script.getCreated());
        ret.setCreator(script.getCreator());
        ret.setId(script.getId());
        ret.setModified(script.getModified());
        ret.setName(script.getName());
        ret.setProductName(script.getProductName());
        ret.setRuntime(script.getRuntime());
        return ret;
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
     * @param data2
     * @return
     */
    private static Set<RequestData> convertDataTO(Set<StepDataTO> data) {
        Set<RequestData> ret = new HashSet<RequestData>();
        for (StepDataTO to : data) {
            ret.add(transferObjectToRequestData(to));
        }
        return ret;
    }

    /**
     * @param requestheaders2
     * @return
     */
    private static Set<StepDataTO> convertData(Set<RequestData> d) {
        Set<StepDataTO> result = new HashSet<StepDataTO>();
        if (d != null) {
            for (RequestData rd : d) {
                result.add(requestDataToTransferObject(rd));
            }
        }

        return result;
    }

    /**
     * @param requestheaders2
     * @return
     */
    private static Set<StepDataTO> copyData(Set<StepDataTO> d) {
        Set<StepDataTO> result = new HashSet<StepDataTO>();
        if (d != null) {
            for (StepDataTO rd : d) {
                result.add(copyStepDataTO(rd));
            }
        }

        return result;
    }

    /**
     * @param script
     * @return
     */
    public static ExternalScriptTO externalScriptToTO(ExternalScript script) {
        ExternalScriptTO ret = new ExternalScriptTO();
        ret.setId(script.getId());
        ret.setCreated(script.getCreated());
        ret.setCreator(script.getCreator());
        ret.setModified(script.getModified());
        ret.setName(script.getName());
        ret.setProductName(script.getProductName());
        ret.setScript(script.getScript());
        return ret;
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

}
