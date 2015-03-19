package com.intuit.tank.vmManager.jms;

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

import com.intuit.tank.vm.vmManager.VMInformation;

public class JMSMessages {

    static Logger logger = Logger.getLogger(JMSMessages.class);

    /**
     * Post a virtual machine to the queue
     * 
     * @param vmInfo
     *            The virtual machine object
     */
    public static void postResponse(List<VMInformation> vmInfo) {
        // try{
        // logger.debug("VM Sending create response back to schedule manager");
        // Settings settings = Settings.getInstance();
        // String queue = settings.getValue("jms.queue.vmManager.instances");
        // URI jmsBroker = new URI(settings.getValue("jms.broker.vmManager"));
        //
        // JMS jms = new JMS(queue, jmsBroker);
        // jms.setupConnection();
        //
        // MessageObject MO = new MessageObject(vmInfo);
        // jms.sendObject(MO);
        // jms.closeConnection();
        // }catch (Exception ex){
        // logger.error(ex.getMessage());
        // }
    }
}
