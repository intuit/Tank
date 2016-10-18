package com.intuit.tank.harness;

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

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HostInfo implements Serializable {

    public static final String UNKNOWN = "N/A";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HostInfo.class);

    public String publicIp;
    public String publicHostname;

    public HostInfo() {
        try {
            publicHostname = AmazonUtil.getPublicHostName();
            publicIp = AmazonUtil.getPublicIp();
        } catch (IOException e) {
            LOG.debug("Cannot find hostname from amazon. trying local...");
            try {
                publicIp = InetAddress.getLocalHost().getHostAddress();
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface nic = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = nic.getInetAddresses();
                    while (publicHostname == null && addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress()) {
                            publicHostname = address.getHostName();
                        }
                    }
                }
            } catch (Exception e1) {
                publicHostname = UNKNOWN;
                publicIp = UNKNOWN;
            }
        }
    }

    public String getPublicIp() {
        return publicIp;
    }

    public String getPublicHostname() {
        return publicHostname;
    }

}