/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.api.service.v1.project;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.settings.TankConfig;

/**
 * ProjectServiceUtil
 * 
 * @author dangleton
 * 
 */
public class ProjectServiceUrlBuilder {

    private ProjectServiceUrlBuilder() {

    }

    /**
     * @param id
     * @param version
     * @param numLines
     * @param offSet
     * @return
     */
    public static String getScriptXmlUrl(String jobId) {
        String baseUrl = new TankConfig().getControllerBase();
        return baseUrl + "/rest/v1/project-service/script/" + jobId;
    }

}
