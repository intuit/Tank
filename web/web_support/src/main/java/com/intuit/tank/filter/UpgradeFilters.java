package com.intuit.tank.filter;

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

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.dao.ScriptFilterActionDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;

@Named
@ApplicationScoped
public class UpgradeFilters implements Serializable {

    private static final String FUNCTION_STRING_CONCAT_FULL = "#{function.string.concat}";
    private static final String FUNCTION_STRING_CONCAT = "#function.string.concat.";
    private static final String FUNCTION_STRING_GET_CSV = "#function.generic.getcsv.";
    private static final Pattern pattern = Pattern.compile("(\\@[\\w_-]*)");

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(UpgradeFilters.class);

    private boolean filtersUpgraded;

    public boolean isDisabled() {
        return filtersUpgraded;
    }

    public synchronized void upgrade() {
        if (!filtersUpgraded) {
            try {
                ScriptFilterDao filterDao = new ScriptFilterDao();
                ScriptFilterActionDao actionDao = new ScriptFilterActionDao();
                List<ScriptFilter> all = filterDao.findAll();
                Set<Integer> toDelete = new HashSet<Integer>();
                for (ScriptFilterAction act : actionDao.findAll()) {
                    toDelete.add(act.getId());
                }
                for (ScriptFilter filter : all) {
                    for (ScriptFilterAction action : filter.getActions()) {
                        toDelete.remove(action.getId());
                        String value = action.getValue();
                        String original = value;
                        if (value.startsWith("@") && value.lastIndexOf('@') == 0) {
                            value = "#{" + value.substring(1) + "}";
                        } else if (value.startsWith(FUNCTION_STRING_CONCAT_FULL)) {
                            value = value.substring(FUNCTION_STRING_CONCAT_FULL.length());
                        } else if (value.startsWith(FUNCTION_STRING_CONCAT)) {
                            value = processConcat(value);
                        } else if (value.startsWith(FUNCTION_STRING_GET_CSV)) {
                            value = processFunction("ioFunctions.getCSVData", value);
                        } else if (value.indexOf('@') != -1) {
                            value = replaceVariables(value);
                        }
                        if (!original.equals(value)) {
                            action.setValue(value);
                            actionDao.saveOrUpdate(action);
                        }
                    }
                }
                for (Integer id : toDelete) {
                    actionDao.delete(id);
                }
                // filterDao.persistCollection(toModify);

                filtersUpgraded = true;
            } catch (Exception e) {
                LOG.error("Error upgrading filters: " + e, e);
            }

        }
    }

    private String replaceVariables(String value) {
        String ret = value;
        Matcher m = pattern.matcher(value);
        while (m.find()) { // find next match
            String group = m.group(1).trim();
            ret = ret.replace(group, "#{" + group.substring(1) + "}");
        }
        return ret;
    }

    private String processConcat(String value) {
        String[] strings = StringUtils.split(value.substring(FUNCTION_STRING_CONCAT.length()), '.');
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            s = s.replace("-dot-", ".");
            if (s.startsWith("@")) {
                s = "#{" + s.substring(1) + "}";
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private String processFunction(String function, String value) {
        String[] strings = StringUtils.split(value, '.');
        StringBuilder sb = new StringBuilder().append("#{").append(function).append("(");

        for (int i = 3; i < strings.length; i++) {
            String s = strings[i];
            s = s.replace("-dot-", ".");
            if (s.startsWith("@")) {
                s = "#{" + s.substring(1) + "}";
            } else if (s.startsWith("#{")) {
                s = s.substring(2, s.length() - 1);
            } else if (!NumberUtils.isNumber(s)) {
                s = '"' + s + '"';
            }
            sb.append(s);
            if (i < strings.length - 1) {
                // last one
                sb.append(", ");
            }
        }
        sb.append(")}");
        return sb.toString();
    }

}
