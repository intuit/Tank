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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.VMSize;
import com.intuit.tank.vm.common.TankConstants;

/**
 * 
 * AmazonUtil
 * 
 * @author dangleton
 * 
 */
public class AmazonUtil {

    private static final Logger LOG = LogManager.getLogger(AmazonUtil.class);
    private static final String BASE = "http://169.254.169.254/latest";
    private static final String USER_DATA = "/user-data";
    private static final String META_DATA = "/meta-data";

    // private static Logger logger = LogManager.getLogger(AmazonUtil.class);

    public static VMRegion getVMRegion() throws IOException {
        String zone = getMetaData(CloudMetaDataType.zone);
        LOG.info("Running in zone " + zone);
        return VMRegion.getRegionFromZone(zone);
    }

    /**
     * Return if this instance is running in Amazon.
     * 
     * @return
     */
    public static boolean isInAmazon() {
        boolean ret = true;
        try {
            getMetaData(CloudMetaDataType.zone);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    /**
     * gets the zone from meta data
     * 
     * @return
     */
    public static String getZone() {
        try {
            String zone = getMetaData(CloudMetaDataType.zone);
            LOG.info("Running in zone " + zone);
            return zone;
        } catch (Exception e) {
            LOG.info("cannot determine zone");
        }
        return "unknown";
    }

    public static String getPublicHostName() throws IOException {
        String ret = null;
        try {
            ret = getMetaData(CloudMetaDataType.public_hostname);
        } catch (Exception e) {
            LOG.debug("Failed getting public host: " + e);
        }
        if (StringUtils.isBlank(ret)) {
            //LOG.info("getting local_ipv4...");
            ret = getMetaData(CloudMetaDataType.local_ipv4);
        }
        return ret;
    }

    /**
     * gets the public ip from meta data
     * 
     * @return
     * @throws IOException
     */
    public static String getPublicIp() throws IOException {
        return getMetaData(CloudMetaDataType.public_ipv4);
    }

    /**
     * 
     * @return
     */
    public static String getAWSKeyFromUserData() {
        String ret = null;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_AWS_SECRET_KEY);
        } catch (IOException e) {
            LOG.warn("Error getting key: " + e.toString());
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public static String getAWSKeyIdFromUserData() {
        String ret = null;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_AWS_SECRET_KEY_ID);
        } catch (IOException e) {
            LOG.warn("Error getting key ID: " + e.toString());
        }
        return ret;
    }

    /**
     * Attempts to get the amazon instance-id of the current VM.
     * 
     * @return the instance Id or null
     */
    @Nonnull
    public static String getInstanceId() {
        String ret = null;
        try {
            ret = getMetaData(CloudMetaDataType.instance_id);
        } catch (IOException e) {
            LOG.warn("Error getting instance ID: " + e.toString());
        }
        return ret;
    }

    /**
     * Attempts to get the amazon instance-id of the current VM.
     * 
     * @return the instance Id or empty string from amazon
     * @throws IOException
     *             if there is an error communicating with the amazon cloud.
     */
    @Nonnull
    public static VMSize getInstanceType() throws IOException {
        String metaData = getMetaData(CloudMetaDataType.instance_type);
        VMSize ret = VMSize.fromRepresentation(metaData);
        return ret != null ? ret : VMSize.HighCPUExtraLarge;
    }

    /**
     * Attempts to get the amazon instance-id of the current VM.
     * 
     * @return the instance Id or empty string from amazon
     * @throws IOException
     *             if there is an error communicating with the amazon cloud.
     */
    @Nonnull
    public static String getMetaData(CloudMetaDataType metaData) throws IOException {
        InputStream inputStream = getInputStream(BASE + META_DATA + "/" + metaData.getKey());
        return convertStreamToString(inputStream);
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     * @throws IOException
     */
    public static String getUserDataAsString() throws IOException {
        String result = null;
        InputStream inputStream = getInputStream(BASE + USER_DATA);
        result = convertStreamToString(inputStream);
        return result;
    }

    /**
     * gets the job id form user data
     * 
     * @return
     */
    public static String getJobId() {
        String ret = null;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_JOB_ID);
        } catch (IOException e) {
            LOG.warn("Error getting job ID: " + e.toString());
        }
        return ret != null ? ret : "unknown";
    }

    /**
     * gets the project name for user data
     * 
     * @return
     */
    public static String getProjectName() {
        String ret = null;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_PROJECT_NAME);
        } catch (IOException e) {
            LOG.warn("Error getting Project  Name: " + e.toString());
        }
        return ret != null ? ret : "unknown";
    }

    /**
     * gets logging profile form user data
     * 
     * @return LoggingProfile
     */
    public static LoggingProfile getLoggingProfile() {
        LoggingProfile ret = LoggingProfile.STANDARD;
        try {
            String lp = getUserDataAsMap().get(TankConstants.KEY_LOGGING_PROFILE);
            if (lp != null) {
                ret = LoggingProfile.valueOf(lp);
            }
        } catch (Exception e) {
            LOG.warn("Error getting LoggingProfile: " + e.toString());
        }
        return ret;
    }

    public static int getCapacity() {
        int ret = 4000;
        try {
            String lp = getUserDataAsMap().get(TankConstants.KEY_NUM_USERS_PER_AGENT);
            if (lp != null) {
                ret = Integer.valueOf(lp);
            }
        } catch (Exception e) {
            LOG.warn("Error getting capacity: " + e.toString());
        }
        return ret;
    }

    /**
     * gets stop behavior form user data.
     * 
     * @return Stopbehavior
     */
    public static StopBehavior getStopBehavior() {
        StopBehavior ret = StopBehavior.END_OF_SCRIPT_GROUP;
        try {
            String sb = getUserDataAsMap().get(TankConstants.KEY_STOP_BEHAVIOR);
            if (sb != null) {
                ret = StopBehavior.valueOf(sb);
            }
        } catch (Exception e) {
            LOG.warn("Error getting StopBehavior: " + e.toString());
        }
        return ret;
    }

    /**
     * gets if we are using EIP from user data
     * 
     * @return
     */
    public static boolean usingEip() {
        boolean ret = false;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_USING_BIND_EIP) != null;
        } catch (IOException e) {
            LOG.warn("Error getting is using EIP: " + e.toString());
        }
        return ret;
    }

    /**
     * gewts controller base form user data
     * 
     * @return
     */
    public static String getControllerBaseUrl() {
        String ret = null;
        try {
            ret = getUserDataAsMap().get(TankConstants.KEY_CONTROLLER_URL);
        } catch (IOException e) {
            LOG.warn("Error getting controller url: " + e.toString());
        }
        return ret != null ? ret : "http://localhost:8080/";
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     * @throws IOException
     */
    public static Map<String, String> getUserDataAsMap() throws IOException {
        Map<String, String> result = null;
        InputStream inputStream = getInputStream(BASE + USER_DATA);
        result = convertStreamToMap(inputStream);
        return result;
    }

    private static Map<String, String> convertStreamToMap(InputStream is) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        if (is != null) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String s = r.readLine();
                while (s != null) {
                    String[] pair = s.split("=", 2);
                    if (pair.length == 2) {
                        result.put(pair[0], pair[1]);
                    }
                    s = r.readLine();
                }
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            try {
                return IOUtils.toString(is);
            } finally {
                IOUtils.closeQuietly(is);
            }
        } else {
            return "";
        }
    }

    /**
     * @param string
     * @return
     * @throws IOException
     */
    private static InputStream getInputStream(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(3000);
        return con.getInputStream();
    }

}
