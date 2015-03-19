package com.intuit.tank.script.replace;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.intuit.tank.search.script.CommonSection;
import com.intuit.tank.search.script.RequestStepSection;
import com.intuit.tank.search.script.Section;
import com.intuit.tank.search.script.SleepTimeSection;
import com.intuit.tank.search.script.ThinkTimeSection;
import com.intuit.tank.search.script.VariableSection;

/**
 * @author hsomani
 */
public class ReplacementFactory {

    private static Map<Section, AbstractReplacement> replaceMap = new HashMap<Section, AbstractReplacement>();

    static {
        replaceMap.put(RequestStepSection.searchRequest, new RequestReplacement());
        replaceMap.put(RequestStepSection.host, new HostReplacement());
        replaceMap.put(RequestStepSection.logginKey, new LoggingKeyReplacement());
        replaceMap.put(RequestStepSection.method, new MethodReplacement());
        replaceMap.put(RequestStepSection.mimeType, new MimeTypeReplacement());
        replaceMap.put(RequestStepSection.name, new NameReplacement());
        replaceMap.put(RequestStepSection.protocol, new ProtocolReplacement());
        replaceMap.put(RequestStepSection.scriptGroupName, new ScriptGroupNameReplacement());
        replaceMap.put(RequestStepSection.simplePath, new SimplePathReplacement());
        replaceMap.put(RequestStepSection.url, new URLReplacement());
        replaceMap.put(RequestStepSection.queryStringKey, new QueryStringKeyReplacement());
        replaceMap.put(RequestStepSection.queryStringValue, new QueryStringValueReplacement());
        replaceMap.put(RequestStepSection.requestCookieKey, new RequestCookieKeyReplacement());
        replaceMap.put(RequestStepSection.requestCookieValue, new RequestCookieValueReplacement());
        replaceMap.put(RequestStepSection.requestHeaderKey, new RequestHeaderKeyReplacement());
        replaceMap.put(RequestStepSection.requestHeaderValue, new RequestHeaderValueReplacement());
        replaceMap.put(RequestStepSection.responseContent, new ResponseContentReplacement());
        replaceMap.put(RequestStepSection.responseCookieKey, new ResponseCookieKeyReplacement());
        replaceMap.put(RequestStepSection.responseCookieValue, new ResponseCookieValueReplacement());
        replaceMap.put(RequestStepSection.responseHeaderKey, new ResponseHeaderKeyReplacement());
        replaceMap.put(RequestStepSection.responseHeaderValue, new ResponseHeaderValueReplacement());
        replaceMap.put(RequestStepSection.postDataKey, new PostDataKeyReplacement());
        replaceMap.put(RequestStepSection.postDataValue, new PostDataValueReplacement());

        replaceMap.put(VariableSection.variableKey, new VariableKeyReplacement());
        replaceMap.put(VariableSection.variableValue, new VariableValueReplacement());

        replaceMap.put(ThinkTimeSection.minTime, new MinTimeReplacement());
        replaceMap.put(ThinkTimeSection.maxTime, new MaxTimeReplacement());

        replaceMap.put(SleepTimeSection.sleepTime, new SleepTimeReplacement());

        replaceMap.put(CommonSection.search, new AllReplacement());
    }

    public static AbstractReplacement getReplacementForSection(Section section) {
        return replaceMap.get(section);
    }

    /**
     * @return
     */
    protected static Collection<AbstractReplacement> getReplacementSections() {
        return new ArrayList<AbstractReplacement>(replaceMap.values());
    }
}
