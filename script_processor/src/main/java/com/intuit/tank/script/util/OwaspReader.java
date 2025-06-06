/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script.util;

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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intuit.tank.conversation.Cookie;
import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.util.HeaderParser;
import com.intuit.tank.util.KeyValuePair;
import com.intuit.tank.util.WebConversationJaxbParseXML;
import com.intuit.tank.util.HeaderParser.HeaderType;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * @author dangleton patterns: Util
 */
public class OwaspReader implements RecordedScriptReader {
    private static final Logger LOG = LogManager.getLogger(OwaspReader.class);

    /**
     * private constructor to implement the Util Pattern
     */
    public OwaspReader() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScriptStep> read(String xml) throws WatsParseException {
        return read(new StringReader(xml));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScriptStep> read(Reader reader) throws WatsParseException {
        return transactionsToRequest(new WebConversationJaxbParseXML()
                .parse(reader));
    }

    public List<ScriptStep> transactionsToRequest( Collection<Transaction> entries) {
        List<ScriptStep> result = new ArrayList<ScriptStep>();
        int index = 0;
        for (Transaction transaction : entries) {
            result.add(transactionToResult(transaction, ++index));
        }
        return result;
    }

    /**
     * @param fromXml
     * @param currentIndex
     * @return
     * @throws MalformedURLException
     */
    private ScriptStep transactionToResult(Transaction fromXml, int currentIndex) {

        try {
            HeaderParser requestParser = new HeaderParser(HeaderType.Request,
                    fromXml.getRequest().getFirstLine(), fromXml.getRequest()
                            .getHeaders());
            HeaderParser responseParser = new HeaderParser(HeaderType.Response,
                    fromXml.getResponse().getFirstLine(), fromXml.getResponse()
                            .getHeaders());
            ScriptStep entry = new ScriptStep();
            entry.setStepIndex(currentIndex);
            entry.setType(ScriptConstants.REQUEST);

            // String url = fromXml.getUrl();
            entry.setUrl(requestParser.getUrl(fromXml.getRequest()
                    .getProtocol()));
            try {
                entry.setHostname(requestParser.getHost());
                entry.setProtocol(fromXml.getRequest().getProtocol().name());
                entry.setSimplePath(requestParser.getPath());
            } catch (Exception e) {
                LOG.error(
                        "url is not valid: ignoring host, protocol, and path",
                        e);
            }
            String mimeType = responseParser.getContentType();
            entry.setMethod(requestParser.getMethod());
            entry.setMimetype(mimeType);
            entry.setResult(responseParser.getStatusMessage());
            entry.setRequestheaders(populateHeaders(
                    requestParser.getPassThroughHeaders(),
                    RequestDataType.requestHeader.name()));
            entry.setReqFormat(findRequestFormat(requestParser.getContentType()));
            entry.setRespFormat(findResponseFormat(responseParser
                    .getContentType()));

            entry.setResponseheaders(populateHeaders(
                    responseParser.getPassThroughHeaders(),
                    RequestDataType.responseHeader.name()));

            entry.setRequestCookies(populateCookies(requestParser.getCookies(),
                    RequestDataType.requestCookie.name()));
            entry.setResponseCookies(populateCookies(
                    responseParser.getCookies(),
                    RequestDataType.responseCookie.name()));
            String bodyAsString = fromXml.getRequest().getBodyAsString();
            // entry.setPayload(bodyAsString);
            if (!StringUtils.isEmpty(bodyAsString)) {
                if (ScriptConstants.JSON_TYPE.equalsIgnoreCase(entry.getReqFormat())
                        || ScriptConstants.XML_TYPE.equalsIgnoreCase(entry.getReqFormat())
                        || ScriptConstants.PLAIN_TEXT_TYPE.equalsIgnoreCase(entry.getReqFormat())) {
                    entry.setPayload(bodyAsString);
                } else if (ScriptConstants.MULTI_PART_TYPE.equalsIgnoreCase(entry.getReqFormat())) {
                    entry.setPayload(Base64.getEncoder().encodeToString(fromXml.getRequest().getBody()));
                } else {
                    entry.setPostDatas(formDataToSet(requestParser
                            .getPostParameters(bodyAsString),
                            RequestDataType.requestPostData.name()));
                }
            }
            entry.setResponse(fromXml.getResponse().getBodyAsString());
            entry.setQueryStrings(formDataToSet(requestParser.getQueryParams(),
                    RequestDataType.queryString.name()));
            return entry;
        } catch (Exception e) {
            LOG.error("Error processing script at step "
                    + currentIndex + ": " + e.toString(), e);
            if (e instanceof RuntimeException) {
                String err = e.getCause() != null ? e.getCause().toString() : e
                        .toString();
                throw new RuntimeException("Error processing script at step "
                        + currentIndex + ": " + err);
            }
            throw new RuntimeException("Error processing script at step "
                    + currentIndex + ": " + e.toString());
        }

    }

    /**
     * @param contentType
     * @return
     */
    private String findRequestFormat(String contentType) {
        String ret = ScriptConstants.NVP_TYPE;
        if (!StringUtils.isBlank(contentType)) {
            if (contentType.toLowerCase().contains(ScriptConstants.JSON_TYPE)) {
                ret = ScriptConstants.JSON_TYPE;
            } else if (contentType.toLowerCase().contains(
                    ScriptConstants.XML_TYPE)) {
                ret = ScriptConstants.XML_TYPE;
            } else if (contentType.toLowerCase().contains(
                    ScriptConstants.PLAIN_TEXT_TYPE)) {
                ret = ScriptConstants.PLAIN_TEXT_TYPE;
            } else if (contentType.toLowerCase().contains(ScriptConstants.MULTI_PART_TYPE)) {
                ret = ScriptConstants.MULTI_PART_TYPE;
            }
        }
        return ret;
    }

    /**
     * @param contentType
     * @return
     */
    private String findResponseFormat(String contentType) {
        String ret = ScriptConstants.NVP_TYPE;
        if (!StringUtils.isEmpty(contentType)) {
            if (contentType.toLowerCase().contains(ScriptConstants.JSON_TYPE)) {
                ret = ScriptConstants.JSON_TYPE;
            } else if (contentType.toLowerCase().contains(
                    ScriptConstants.XML_TYPE)) {
                ret = ScriptConstants.XML_TYPE;
            } else if (contentType.toLowerCase().contains(
                    ScriptConstants.PLAIN_TEXT_TYPE)) {
                ret = ScriptConstants.PLAIN_TEXT_TYPE;
            }
        }
        return ret;
    }

    private Set<RequestData> populateCookies(List<Cookie> list, String type) {
        Set<RequestData> cs = new HashSet<RequestData>();
        if (list != null) {
            for (Cookie c : list) {
                RequestData cookie = new RequestData();
                cookie.setType(type);
                cookie.setKey(c.getKey());
                cookie.setValue(c.getValue());
                cs.add(cookie);
            }
        }
        return cs;
    }

    private Set<RequestData> populateHeaders(List<Header> headers, String type) {
        return headers.stream().map(h -> new RequestData(h.getKey(), h.getValue(), type)).collect(Collectors.toSet());
    }

    /**
     * @param params
     * @param type
     * @return
     */
    private Set<RequestData> formDataToSet(List<KeyValuePair> params,
            String type) {
        Set<RequestData> map = new HashSet<RequestData>();
        if (params != null) {
            for (KeyValuePair param : params) {
                String name = param.getKey();
                String value = param.getValue();
                if (value != null) {
                    try {
                        value = URLDecoder.decode(value, ScriptConstants.UTF);
                    } catch (UnsupportedEncodingException e) {
                        // never happens since utf-8 is universally supported
                    }
                    value = value.replaceAll("[\\r\\n]", "").replaceAll(
                            ">\\s+<", "><");
                }
                RequestData data = new RequestData();
                data.setKey(name);
                data.setValue(value);
                data.setType(type);

                map.add(data);
            }
        }
        return map;
    }

    /**
     * @param response
     * @return
     * @throws JSONException
     */
    public static Set<RequestData> rawJsonToSet(String response)
            throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String[] names = JSONObject.getNames(jsonObject);
        List<RequestData> itemList = new ArrayList<RequestData>();
        for (String name : names) {
            traverse(name, jsonObject, itemList, new RequestDataBuilder(
                    RequestDataType.requestPostData.name()));
        }

        return new LinkedHashSet<RequestData>(itemList);
    }

    private static void traverse(String name, JSONObject jsonObject,
            List<RequestData> itemList, RequestDataBuilder dataItem) {
        try {
            Object childObject = jsonObject.get(name);
            if (childObject instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) childObject;
                dataItem.addPathElement(name);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object child = jsonArray.get(i);
                    RequestDataBuilder copy = dataItem.copy();
                    copy.addPathElement("[" + i + "]");
                    if (child instanceof JSONObject) {
                        JSONObject childJson = (JSONObject) child;
                        String[] names = JSONObject.getNames(childJson);
                        for (String childName : names) {
                            traverse(childName, childJson, itemList, copy);

                        }
                    }
                }
            } else if (childObject instanceof JSONObject) {
                JSONObject childJson = (JSONObject) childObject;
                String[] names = JSONObject.getNames(childJson);
                dataItem.addPathElement(name);
                if (names != null) {
                    for (String childName : names) {
                        traverse(childName, childJson, itemList, dataItem.copy());
                    }
                }
            } else {
                RequestData item = dataItem.build(name,
                        jsonObject.getString(name));
                itemList.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
