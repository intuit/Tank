package com.intuit.tank.project;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.project.Workload;

@Named
@ConversationScoped
public class ProjectVariableEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Messages messages;

    @Inject
    private ProjectBean projectBean;

    private List<VariableEntry> variables;
    private VariableEntry currentEntry;

    public void savedVariable(VariableEntry variable) {
        List<VariableEntry> toRemove = variables.stream().filter(var -> var != variable && var.getKey().equals(variable.getKey())).collect(Collectors.toList());
        variables.removeAll(toRemove);
    }

    /**
     * @return the variables
     */
    public List<VariableEntry> getVariables() {
    	if ( variables == null ) {
    		variables = new ArrayList<VariableEntry>();
    		Map<String, String> map = projectBean.getWorkload().getJobConfiguration().getVariables();
    		for (Map.Entry<String, String> entry : map.entrySet()) {
    			variables.add(new VariableEntry(entry.getKey(), entry.getValue()));
    		}
                
    	}
        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(List<VariableEntry> variables) {
        this.variables = variables;
    }

    /**
     * @return the currentEntry
     */
    public void newEntry() {
        currentEntry = new VariableEntry();
    }

    /**
     * @return the currentEntry
     */
    public VariableEntry getCurrentEntry() {
        return currentEntry;
    }

    /**
     * @param currentEntry
     *            the currentEntry to set
     */
    public void setCurrentEntry(VariableEntry currentEntry) {
        this.currentEntry = currentEntry;
    }

    /**
     * 
     */
    public void addEntry() {
        boolean valid = true;
        if (StringUtils.isEmpty(currentEntry.getValue())) {
            messages.warn("Value cannot be empty.");
            valid = false;
        }
        if (StringUtils.isEmpty(currentEntry.getKey())) {
            messages.warn("Key cannot be empty.");
            valid = false;
            return;
        }
        for (VariableEntry entry : variables) {
            if (entry.getKey().equalsIgnoreCase(currentEntry.getKey())) {
                messages.warn("Duplicate key ' " + currentEntry.getKey() + "'. Please edit the existing variable.");
                valid = false;
            }
        }
        if (valid) {
            this.variables.add(currentEntry);
        }
    }

    /**
     * Initializes the class variables with appropriate references
     * 
     * @param project
     * @param workload
     */
    public void init() {
        this.variables = new ArrayList<VariableEntry>();
        for (Entry<String, String> entry : projectBean.getJobConfiguration().getVariables().entrySet()) {
            variables.add(new VariableEntry(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Deletes the script group from the workload. This does not persist the changes to the database.
     * 
     * @param group
     */
    public void delete(String key) {
        if (key != null) {
            // only remove on save
            // projectBean.getWorkload().getJobConfiguration().getVariables().remove(key);
            VariableEntry toRemove = variables.stream().filter(entry -> entry.getKey().equals(key)).findFirst().orElse(null);
            if (toRemove != null) {
                variables.remove(toRemove);
            }
        }
    }

    /**
     * persists the workload in the database
     */
    public void save() {
        Map<String, String> map = projectBean.getWorkload().getJobConfiguration().getVariables();
        map.clear();
        for (VariableEntry entry : variables) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param workload
     */
    public void copyTo(Workload workload) {
        Map<String, String> map = workload.getJobConfiguration().getVariables();
        for (VariableEntry entry : variables) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

}
