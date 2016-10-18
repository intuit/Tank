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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

public class KillInstance implements Runnable {

    static Logger logger = LogManager.getLogger(KillInstance.class);

    private VMKillRequest request = null;

    public KillInstance(VMKillRequest request) {
        this.request = request;
    }

    @Override
    public void run() {
        IEnvironmentInstance environment = this.getEnvironment();
        List<VMInformation> killResponse = environment.kill();

        // update status of instance
        for (VMInformation vmInformation : killResponse) {
            String instanceId = vmInformation.getInstanceId();
            logger.info("killed instance " + instanceId);
            // any status updates to the vm image in the database may be performed here:
        }
        // JMSMessages.postResponse(killResponse);
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
                environment = new AmazonInstance(this.request, null);
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
