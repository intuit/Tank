package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.search.lucene.LuceneService;
import com.intuit.tank.search.util.SearchConstants;
import com.intuit.tank.vm.common.util.MethodTimer;

public class ScriptSearchService {

    private static final String separator = " ";

    private static final Logger LOG = LogManager.getLogger(ScriptSearchService.class);
    private LuceneService ls;

    public ScriptSearchService() {
        try {
            ls = new LuceneService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void saveScript(Script script) {
        MethodTimer mt = new MethodTimer(LOG, getClass(), "saveScript");
        mt.start();
        List<Document> documents = new ArrayList<Document>();
        for (ScriptStep steps : script.getScriptSteps()) {
            Document document = new Document();
            StringBuilder sb = new StringBuilder();
            sb.append(script.getId()).append(separator);
            sb.append(steps.getUuid()).append(separator);
            addField(document, ScriptSearchField.scriptId, String.valueOf(script.getId()), Field.Store.YES,
                    Field.Index.NOT_ANALYZED);
            addField(document, ScriptSearchField.uuid, String.valueOf(steps.getUuid()), Field.Store.YES,
                    Field.Index.NO);
            if (steps.getType().equals("request")) {
                updateRequestDocument(steps, sb, document);
            } else if (steps.getType().equals("sleep")) {
                updateSleepTimeDocument(steps, sb, document);
            } else if (steps.getType().equals("thinkTime")) {
                updateThinkTimeDocument(steps, sb, document);
            } else if (steps.getType().equals("variable")) {
                updateVariableDocument(steps, sb, document);
            }
            documents.add(document);
        }
        mt.markAndLog("create documents");
        QueryParser parser = new QueryParser(SearchConstants.version, ScriptSearchField.scriptId.getValue(),
                SearchConstants.analyzer);
        Query query;
        try {
            query = parser.parse(String.valueOf(script.getId()));
            ls.removeDocument(query);
            mt.markAndLog("remove existing document");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ls.indexDocuments(documents);
        mt.markAndLog("index document");
        mt.endAndLog();
    }

    private void updateSleepTimeDocument(ScriptStep steps, StringBuilder sb, Document document) {
        String sleepTimeValue = "";
        for (RequestData requestData : steps.getData()) {
            String key = requestData.getKey();
            String value = requestData.getValue();
            sb.append(key);
            sb.append(separator);
            sb.append(value);
            sb.append(separator);
            sleepTimeValue = value;
        }
        addField(document, ScriptSearchField.type, steps.getType(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.sleepTime, sleepTimeValue, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.data, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.search, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);
    }

    private void updateVariableDocument(ScriptStep steps, StringBuilder sb, Document document) {
        String keyValue = "";
        String variableValue = "";
        for (RequestData requestData : steps.getData()) {
            String key = requestData.getKey();
            String value = requestData.getValue();
            sb.append(key);
            sb.append(separator);
            sb.append(value);
            sb.append(separator);
            variableValue = value;
            keyValue = key;
        }
        addField(document, ScriptSearchField.type, steps.getType(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.variableKey, keyValue, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.variableValue, variableValue, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.data, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.search, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);

    }

    private void updateThinkTimeDocument(ScriptStep steps, StringBuilder sb, Document document) {
        String minTimeValue = "";
        String maxTimeValue = "";
        for (RequestData requestData : steps.getData()) {
            if (requestData.getKey().equals("minTime")) {
                String key = requestData.getKey();
                String value = requestData.getValue();
                sb.append(key);
                sb.append(separator);
                sb.append(value);
                sb.append(separator);
                minTimeValue = value;
            } else if (requestData.getKey().equals("maxTime")) {
                String key = requestData.getKey();
                String value = requestData.getValue();
                sb.append(key);
                sb.append(separator);
                sb.append(value);
                sb.append(separator);
                maxTimeValue = value;
            }
        }
        addField(document, ScriptSearchField.type, steps.getType(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.minTime, minTimeValue, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.maxTime, maxTimeValue, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.data, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.search, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);

    }

    private void updateRequestDocument(ScriptStep steps, StringBuilder sb, Document document) {
        sb.append(steps.getMethod()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getUrl()) ? "" : steps.getUrl()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getSimplePath()) ? "" : steps.getSimplePath()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getLabel()) ? "" : steps.getLabel()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getProtocol()) ? "" : steps.getProtocol()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getComments()) ? "" : steps.getComments()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getHostname()) ? "" : steps.getHostname()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getMimetype()) ? "" : steps.getMimetype()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getLoggingKey()) ? "" : steps.getLoggingKey()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getName()) ? "" : steps.getName()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getScriptGroupName()) ? "" : steps.getScriptGroupName()).append(separator);
        sb.append(StringUtils.isEmpty(steps.getOnFail()) ? "" : steps.getOnFail()).append(separator);
        sb.append(steps.getType()).append(separator);

        String reqHeader = getStringRep(steps.getRequestheaders());
        String respHeader = getStringRep(steps.getResponseheaders());
        String reqCookies = getStringRep(steps.getRequestCookies());
        String respCookies = getStringRep(steps.getResponseCookies());
        String postData = getStringRep(steps.getPostDatas());
        String queryString = getStringRep(steps.getQueryStrings());
        String respData = getStringRep(steps.getResponseData());

        sb.append(reqHeader).append(separator);
        sb.append(respHeader).append(separator);
        sb.append(reqCookies).append(separator);
        sb.append(respCookies).append(separator);
        sb.append(postData).append(separator);
        sb.append(queryString).append(separator);
        sb.append(respData).append(separator);

        addField(document, ScriptSearchField.url, steps.getUrl(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.simplePath, steps.getSimplePath(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.label, steps.getLabel(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.method, steps.getMethod(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.protocol, steps.getProtocol(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.comments, steps.getComments(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.hostName, steps.getHostname(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.mimeType, steps.getMimetype(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.loggingKey, steps.getLoggingKey(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.name, steps.getName(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.onFail, steps.getOnFail(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.type, steps.getType(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.scriptGroupName, steps.getScriptGroupName(), Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.requestHeaders, reqHeader, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.responseHeaders, respHeader, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.requestCookies, reqCookies, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.responseCookies, respCookies, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.postDatas, postData, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.queryString, queryString, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.responseContent, respData, Field.Store.NO,
                Field.Index.ANALYZED);
        addField(document, ScriptSearchField.search, sb.toString(), Field.Store.NO,
                Field.Index.ANALYZED);
    }

    private static void addField(Document document, ScriptSearchField searchField, String value, Field.Store store,
            Field.Index ind) {
        if (value != null) {
            document.add(new Field(searchField.getValue(), value, store, ind));
        }
    }

    private String getStringRep(Set<RequestData> datas) {
        StringBuilder sb = new StringBuilder();
        for (RequestData requestData : datas) {
            String key = requestData.getKey();
            String value = requestData.getValue();
            sb.append(key);
            sb.append(separator);
            sb.append(value);
            sb.append(separator);
        }
        return sb.toString();
    }

    public void reIndexAllScripts(List<Script> scripts) {
        ls.clearIndex();
        for (Script script : scripts) {
            saveScript(script);
        }
    }

    public List<String> search(int scriptId, String searchQuery, SearchCriteria criteria) {
        QueryBuilderUtil qb = new QueryBuilderUtil();

        Query query2 = qb.build(String.valueOf(scriptId), searchQuery, criteria);
        boolean prefixWildCard = false;
        if (searchQuery.startsWith("*")) {
            prefixWildCard = true;
        }
        System.out.println(query2.toString());
        return searchWithQuery(query2, prefixWildCard);
    }

    public List<String> search(int scriptId) {
        QueryParser qp = new QueryParser(SearchConstants.version, ScriptSearchField.scriptId.getValue(),
                SearchConstants.analyzer);
        Query parse;
        try {
            parse = qp.parse("" + scriptId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new ArrayList<String>(convertDocumentList((ls.search(parse, true))));
    }

    public List<String> searchWithQuery(Query query, boolean prefixWildcard) {
        List<Document> search = ls.search(query, prefixWildcard);
        return new ArrayList<String>(convertDocumentList(search));
    }

    private Set<String> convertDocumentList(List<Document> search) {
        Set<String> uuidList = new HashSet<String>();
        for (Document document : search) {
            String uuid = document.get(ScriptSearchField.uuid.getValue());
            uuidList.add(uuid);
        }
        return uuidList;
    }

}
