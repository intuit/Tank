package com.intuit.tank.vmManager.environment;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

public class CreateInstance implements Runnable {

    static Logger logger = Logger.getLogger(CreateInstance.class);

    private VMInstanceRequest request = null;

    private VMTracker vmTracker;

    public CreateInstance(VMInstanceRequest request, VMTracker vmTracker) {
        this.request = request;
        this.vmTracker = vmTracker;
    }

    @Override
    public void run() {
        IEnvironmentInstance environment = this.getEnvironment();
        List<VMInformation> vmInfo = environment.create();

        logger.info("Created " + vmInfo.size() + " Amazon instances.");
        VMImageDao dao = new VMImageDao();
        // persist the VMImages to database:
        for (VMInformation info : vmInfo) {
            vmTracker.setStatus(createCloudStatus(request, info));
            dao.addImageFromInfo(request.getJobId(), info, request.getRegion());
        }
    }

    /**
     * @param request2
     * @param info
     * @return
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        CloudVmStatus ret = new CloudVmStatus(info.getInstanceId(), req.getJobId(), "unknown", JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.pending, new ValidationStatus(), 0, 0, null, null);
        return ret;
    }

    /**
     * Get the appropriate environment
     * 
     * @return The appropriate environment object
     */
    private IEnvironmentInstance getEnvironment() {
        IEnvironmentInstance environment = null;
        try {
            switch (this.request.getProvider()) {
            case Amazon:
                environment = new AmazonInstance(this.request, request.getRegion());
                break;
            case Pharos:
                break;
            }
            return environment;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
}
