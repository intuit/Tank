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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.tool.hbm2x.StringUtils;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.search.script.Section;
import com.intuit.tank.vm.common.util.RegexUtil;

public abstract class AbstractReplacement {
    private Section section;
    private String type;

    protected AbstractReplacement(Section section, String type) {
        this.section = section;
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the section
     */
    public Section getSection() {
        return section;
    }

    /**
     * @param section
     *            the section to set
     */
    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * @param searchQuery
     *            The search query pattern
     * @param replaceString
     *            The replacement string
     * @param value
     * @param type
     * @return
     */
    protected List<ReplaceEntity> getReplacementInValue(String searchQuery, String replaceString, String value,
            String type) {
        return getReplacementInValue(searchQuery, replaceString, value, type, null);
    }

    /**
     * @param searchQuery
     *            The search query pattern
     * @param replaceString
     *            The replacement string
     * @param value
     *            The value to be searched in.
     * 
     * @param type
     *            The type of the
     * @param key
     *            The key to be set in the replace entity if a match was found.
     * @return
     */
    protected List<ReplaceEntity> getReplacementInValue(String searchQuery, String replaceString, String value,
            String type, String key) {
        if (StringUtils.isEmpty(searchQuery) || StringUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        if (!this.type.equals(type)) {
            return Collections.emptyList();
        }

        List<ReplaceEntity> reList = new ArrayList<ReplaceEntity>();
        if (isMatch(searchQuery, replaceString, value)) {
            ReplaceEntity re = new ReplaceEntity();
            re.setValue(value);
            re.setAfter(replaceString);
            re.setSection(getSection());
            re.setKey(key);
            reList.add(re);
        }
        return reList;
    }

    protected List<ReplaceEntity> getReplacementInKey(String searchQuery, String replaceString, String value,
            String type, String key) {
        if (StringUtils.isEmpty(searchQuery) || StringUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        if (!this.type.equals(type)) {
            return Collections.emptyList();
        }

        List<ReplaceEntity> reList = new ArrayList<ReplaceEntity>();
        if (isMatch(searchQuery, replaceString, key)) {
            ReplaceEntity re = new ReplaceEntity();
            re.setValue(value);
            re.setAfter(replaceString);
            re.setSection(getSection());
            re.setKey(key);
            reList.add(re);
        }
        return reList;
    }

    /**
     * Finds the pattern in the value and returns how the replacement string would like to the user
     * 
     * @param searchQuery
     *            the search query
     * @param replaceString
     *            The replacement string
     * @param value
     *            The value to be searched in.
     * @return
     */
    private boolean isMatch(String searchQuery, String replaceString, String value) {
        searchQuery = RegexUtil.wildcardToRegexp(searchQuery);
        Pattern p = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE);
        return p.matcher(value).matches();
    }

    /**
     * @param step
     *            The step in which the search should be performed.
     * @param searchQuery
     *            The search Query.
     * @param replaceString
     *            The replacement string.
     * @param requestDatas
     *            The set of RequestData where the replacements should be searched for.
     * @param searchMode
     *            The search mode for the replacements
     * @return
     */
    protected List<ReplaceEntity> getReplacementsInRequestData(ScriptStep step, String searchQuery,
            String replaceString,
            Set<RequestData> requestDatas, SearchMode searchMode) {
        List<ReplaceEntity> reList = new ArrayList<ReplaceEntity>();
        for (RequestData requestData : requestDatas) {
            if (searchMode == SearchMode.all) {
                reList.addAll(getReplacementInValue(searchQuery, replaceString, requestData.getValue(), step.getType(),
                        requestData.getKey()));
                reList.addAll(getReplacementInKey(searchQuery, replaceString, requestData.getValue(), step.getType(),
                        requestData.getKey()));
            } else if (searchMode == SearchMode.keyOnly) {
                reList.addAll(getReplacementInKey(searchQuery, replaceString, requestData.getValue(), step.getType(),
                        requestData.getKey()));
            } else if (searchMode == SearchMode.valueOnly) {
                reList.addAll(getReplacementInValue(searchQuery, replaceString, requestData.getValue(), step.getType(),
                        requestData.getKey()));
            }
        }
        return reList;
    }

    /**
     * Replace the value in the replacement entity.
     * 
     * @param requestDatas
     *            The post datas
     * @param replaceString
     *            The replacement string
     * @param key
     *            TODO
     * @return
     */
    protected void replaceInRequestDatas(Set<RequestData> requestDatas, String replaceString, String key) {
        for (RequestData requestData : requestDatas) {
            if (requestData.getKey().equals(key)) {
                requestData.setValue(replaceString);
            }
        }
    }

    /**
     * Replace the key in the replacement entity.
     * 
     * @param requestDatas
     *            The post datas
     * @param keyReplaceString
     *            The replacement string
     * @param key
     *            TODO
     * @return
     */
    protected void replaceInRequestDatas(Set<RequestData> requestDatas, String replaceString, String key,
            ReplaceMode replaceMode) {
        for (RequestData requestData : requestDatas) {
            if (requestData.getKey().equals(key)) {
                if (replaceMode == ReplaceMode.KEY) {
                    requestData.setKey(replaceString);
                } else {
                    requestData.setValue(replaceString);
                }
            }
        }
    }

    /**
     * Gets the replacement.
     * 
     * @param step
     *            the step where the replacement is to be searched
     * @param searchQuery
     *            The search Query.
     * @param replaceString
     *            The replacement string which should be set within the replacements if replacements are identified
     * @param searchMode
     *            The mode of the search.
     * @return
     */
    public abstract List<ReplaceEntity> getReplacements(ScriptStep step, String searchQuery, String replaceString,
            SearchMode searchMode);

    /**
     * Replace the value in the step with replaceString
     * 
     * @param step
     *            the step in which the replacements is to be performed.
     * @param replaceString
     *            the replacement string
     * @param key
     *            TODO
     * @param replaceMode
     *            the field to be replaced
     */
    public abstract void replace(ScriptStep step, String replaceString, String key, ReplaceMode replaceMode);

}
