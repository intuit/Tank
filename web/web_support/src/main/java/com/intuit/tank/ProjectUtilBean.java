/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.LocationsConfig;
import com.intuit.tank.vm.settings.ProductConfig;
import com.intuit.tank.vm.settings.SelectableItem;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Named
@SessionScoped
public class ProjectUtilBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private SelectItem[] productNames;
    private SelectItem[] locations;
    private SelectItem[] reportingModes;
    private SelectItem[] loggingProfiles;
    private SelectItem[] stopBehaviors;
    private SelectItem[] vmInstanceTypes;
    private SelectItem[] tankClientTypes;

    /**
     * 
     */
    public ProjectUtilBean() {
        ProductConfig productConfig = new TankConfig().getProductConfig();
        List<SelectableItem> products = productConfig.getProducts();
        productNames = new SelectItem[products.size()];
        for (int i = 0; i < products.size(); i++) {
            SelectableItem item = products.get(i);
            productNames[i] = new SelectItem(item.getValue(), item.getDisplayName());
        }
        LocationsConfig locationsConfig = new TankConfig().getLocationsConfig();
        List<SelectableItem> l = locationsConfig.getLocations();
        locations = new SelectItem[l.size()];
        for (int i = 0; i < l.size(); i++) {
            SelectableItem item = l.get(i);
            locations[i] = new SelectItem(item.getValue(), item.getDisplayName());
        }
        AgentConfig agentConfig = new TankConfig().getAgentConfig();
        Map<String, String> resultsTypeMap = agentConfig.getResultsTypeMap();
        List<String> resultsTypes = new ArrayList<String>(resultsTypeMap.keySet());
        Collections.sort(resultsTypes);
        reportingModes = new SelectItem[resultsTypes.size()];
        for (int i = 0; i < resultsTypes.size(); i++) {
            String s = resultsTypes.get(i);
            reportingModes[i] = new SelectItem(resultsTypeMap.get(s), s);
        }
        loggingProfiles = new SelectItem[LoggingProfile.values().length];
        for (int i = 0; i < LoggingProfile.values().length; i++) {
            LoggingProfile lp = LoggingProfile.values()[i];
            loggingProfiles[i] = new SelectItem(lp.name(), lp.getDisplayName(), lp.getDescription());
        }
        stopBehaviors = new SelectItem[StopBehavior.values().length];
        for (int i = 0; i < StopBehavior.values().length; i++) {
            StopBehavior sb = StopBehavior.values()[i];
            stopBehaviors[i] = new SelectItem(sb.name(), sb.getDisplay(), sb.getDescription());
        }
        List<VmInstanceType> instanceTypes = new TankConfig().getVmManagerConfig().getInstanceTypes();
        vmInstanceTypes = new SelectItem[instanceTypes.size()];
        for (int i = 0; i < instanceTypes.size(); i++) {
            VmInstanceType type = instanceTypes.get(i);
            vmInstanceTypes[i] = new SelectItem(type.getName(), type.getName(), type.getDisplay());
        }

        Map<String, String> tankClientMap = new TankConfig().getAgentConfig().getTankClientMap();
        tankClientTypes = new SelectItem[tankClientMap.size()];
        List<String> tcl = new ArrayList<String>(tankClientMap.keySet());
        Collections.sort(tcl);
        for (int i = 0; i < tcl.size(); i++) {
            String key = tcl.get(i);
            String className = tankClientMap.get(key);
            tankClientTypes[i] = new SelectItem(className, key, key);
        }
        
    }
    
    

    /**
     * @return the tankClientTypes
     */
    public SelectItem[] getTankClientTypes() {
        return tankClientTypes;
    }



    public SelectItem[] getLoggingProfiles() {
        return loggingProfiles;
    }

    /**
     * @return the vmInstanceTypes
     */
    public SelectItem[] getVmInstanceTypes() {
        return vmInstanceTypes;
    }

    /**
     * @return the productNames
     */
    public SelectItem[] getProductNames() {
        return productNames;
    }

    /**
     * @return the locations
     */
    public SelectItem[] getLocations() {
        return locations;
    }

    /**
     * @return the reportingModes
     */
    public SelectItem[] getReportingModes() {
        return reportingModes;
    }

    /**
     * @return the stopBehaviors
     */
    public SelectItem[] getStopBehaviors() {
        return stopBehaviors;
    }

}
