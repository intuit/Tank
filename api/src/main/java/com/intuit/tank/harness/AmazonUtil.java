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

import com.amazonaws.util.IOUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.VMSize;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.message.ObjectMessage;

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

    public static VMRegion getVMRegion() {
        try {
            String zone = getMetaData(CloudMetaDataType.zone);
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Running in zone " + zone)));
            return VMRegion.getRegionFromZone(zone);
        } catch (IOException ioe) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message","Error getting region. using CUSTOM...")));
        }
        return VMRegion.STANDALONE;
    }

    /**
     * Return if this instance is running in Amazon.
     * 
     * @return
     */
    public static boolean isInAmazon() {
        try {
            getMetaData(CloudMetaDataType.zone);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * gets the zone from meta data
     * 
     * @return
     */
    public static String getZone() {
        try {
            String zone = getMetaData(CloudMetaDataType.zone);
            LOG.info(new ObjectMessage(ImmutableMap.of("Message","Running in zone " + zone)));
            return zone;
        } catch (Exception e) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message","cannot determine zone")));
        }
        return "unknown";
    }

    public static String getPublicHostName() throws IOException {
        try {
            return getMetaData(CloudMetaDataType.public_hostname);
        } catch (Exception e) {
            LOG.debug(new ObjectMessage(ImmutableMap.of("Message","Failed getting public host: " + e)));
        }
        return getMetaData(CloudMetaDataType.local_ipv4);
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
        try {
            return getUserDataAsMap().get(TankConstants.KEY_AWS_SECRET_KEY);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting key: " + e.toString())));
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public static String getAWSKeyIdFromUserData() {
        try {
            return getUserDataAsMap().get(TankConstants.KEY_AWS_SECRET_KEY_ID);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting key ID: " + e.toString())));
        }
        return null;
    }

    /**
     * Attempts to get the amazon instance-id of the current VM.
     * 
     * @return the instance Id or null
     */
    @Nonnull
    public static String getInstanceId() {
        try {
            return getMetaData(CloudMetaDataType.instance_id);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting instance ID: " + e.toString())));
        }
        return "";
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
        return IOUtils.toString(inputStream);
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     * @throws IOException
     */
    public static String getUserDataAsString() throws IOException {
        InputStream inputStream = getInputStream(BASE + USER_DATA);
        return IOUtils.toString(inputStream);
    }

    /**
     * gets the job id form user data
     * 
     * @return
     */
    public static String getJobId() {
        try {
            return getUserDataAsMap().get(TankConstants.KEY_JOB_ID);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting job ID: " + e.toString())));
        }
        return "unknown";
    }

    /**
     * gets the project name for user data
     * 
     * @return
     */
    public static String getProjectName() {
        try {
            return getUserDataAsMap().get(TankConstants.KEY_PROJECT_NAME);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting Project  Name: " + e.toString())));
        }
        return "unknown";
    }

    /**
     * gets logging profile form user data
     * 
     * @return LoggingProfile
     */
    public static LoggingProfile getLoggingProfile() {
        try {
            String lp = getUserDataAsMap().get(TankConstants.KEY_LOGGING_PROFILE);
            if (StringUtils.isNoneEmpty(lp)) {
                return LoggingProfile.valueOf(lp);
            }
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting LoggingProfile: " + e.toString())));
        }
        return LoggingProfile.STANDARD;
    }

    public static int getCapacity() {
        try {
            String lp = getUserDataAsMap().get(TankConstants.KEY_NUM_USERS_PER_AGENT);
            if (StringUtils.isNoneEmpty(lp)) {
                return Integer.valueOf(lp);
            }
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting capacity: " + e.toString())));
        }
        return 4000;
    }

    /**
     * gets stop behavior form user data.
     * 
     * @return Stopbehavior
     */
    public static StopBehavior getStopBehavior() {
        try {
            String sb = getUserDataAsMap().get(TankConstants.KEY_STOP_BEHAVIOR);
            if (StringUtils.isNoneEmpty(sb)) {
                return StopBehavior.valueOf(sb);
            }
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting StopBehavior: " + e.toString())));
        }
        return StopBehavior.END_OF_SCRIPT_GROUP;
    }

    /**
     * gets if we are using EIP from user data
     * 
     * @return
     */
    public static boolean usingEip() {
        try {
            return getUserDataAsMap().get(TankConstants.KEY_USING_BIND_EIP) != null;
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting is using EIP: " + e.toString())));
        }
        return false;
    }

    /**
     * gewts controller base form user data
     * 
     * @return
     */
    public static String getControllerBaseUrl() {
        try {
            return getUserDataAsMap().get(TankConstants.KEY_CONTROLLER_URL);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Error getting controller url: " + e.toString())));
        }
        return "http://localhost:8080/";
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     * @throws IOException
     */
    public static Map<String, String> getUserDataAsMap() throws IOException {
        InputStream inputStream = getInputStream(BASE + USER_DATA);
        return convertStreamToMap(inputStream);
    }

    private static Map<String, String> convertStreamToMap(InputStream is) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        if (is != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while (StringUtils.isNoneEmpty(line = reader.readLine())) {
                    String[] pair = line.split("=", 2);
                    if (pair.length == 2) {
                        result.put(pair[0], pair[1]);
                    }
                }
            } finally {
                IOUtils.closeQuietly(is, null);
            }
        }
        return result;
    }

    /**
     * @param url
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
