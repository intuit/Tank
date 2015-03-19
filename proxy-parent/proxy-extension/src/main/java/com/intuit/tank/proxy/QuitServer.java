/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * StartServer
 * 
 * @author dangleton
 * 
 */
public class QuitServer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            URL yahoo = new URL("http://localhost:8008/quit");
            URLConnection yc = yahoo.openConnection();
            if (((HttpURLConnection) yc).getResponseCode() == 200) {
                System.out.println("Proxy has been stopped via quit.");
            } else {
                System.out.println("Proxy Failed to quit " + ((HttpURLConnection) yc).getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
