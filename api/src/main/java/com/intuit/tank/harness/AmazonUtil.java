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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import jakarta.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
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

    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    private static final Logger LOG = LogManager.getLogger(AmazonUtil.class);
    protected static String BASE = "http://169.254.169.254/latest";
    protected static String TOKEN = "/api/token";
    protected static final String USER_DATA = "/user-data";
    protected static final String META_DATA = "/meta-data";

    public static VMRegion getVMRegion() {
        try {
            String zone = getMetaData(CloudMetaDataType.zone);
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
            return getMetaData(CloudMetaDataType.zone);
        } catch (IOException e) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message","cannot determine zone")));
        }
        return "unknown";
    }

    public static String getPublicHostName() throws IOException {
        try {
            return getMetaData(CloudMetaDataType.public_hostname);
        } catch (IOException e) {
            // returns private IP because private hostname is unresolvable
            return getMetaData(CloudMetaDataType.local_ipv4);
        }
    }

    /**
     * gets the public ip from meta data
     * 
     * @return
     * @throws IOException
     */
    public static String getPublicIp() throws IOException {
        try {
            return getMetaData(CloudMetaDataType.public_ipv4);
        } catch (IOException e) {
            return getMetaData(CloudMetaDataType.local_ipv4);
        }
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
    public static String getInstanceType() throws IOException {
        return getMetaData(CloudMetaDataType.instance_type);
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
        return getResponseString(BASE + META_DATA + "/" + metaData.getKey());
    }

    /**
     * gets the job id form user data
     *
     * @return
     */
    public static String getJobId() {
        String result = getUserDataAsMap().get(TankConstants.KEY_JOB_ID);
        return StringUtils.isNotEmpty(result)
                ? result
                : "unknown";
    }

    /**
     * gets the project name for user data
     *
     * @return
     */
    public static String getProjectName() {
        String result = getUserDataAsMap().get(TankConstants.KEY_PROJECT_NAME);
        return StringUtils.isNotEmpty(result)
                ? result
                : "unknown";
    }

    /**
     * gets logging profile form user data
     *
     * @return LoggingProfile
     */
    public static LoggingProfile getLoggingProfile() {
        String result = getUserDataAsMap().get(TankConstants.KEY_LOGGING_PROFILE);
        return StringUtils.isNotEmpty(result)
                ? LoggingProfile.valueOf(result)
                : LoggingProfile.STANDARD;
    }

    public static int getCapacity() {
        String result = getUserDataAsMap().get(TankConstants.KEY_NUM_USERS_PER_AGENT);
        return StringUtils.isNotEmpty(result)
                ? Integer.parseInt(result)
                : 4000;
    }

    /**
     * gets stop behavior form user data.
     *
     * @return Stopbehavior
     */
    public static StopBehavior getStopBehavior() {
        String result = getUserDataAsMap().get(TankConstants.KEY_STOP_BEHAVIOR);
        return StringUtils.isNotEmpty(result)
                ? StopBehavior.valueOf(result)
                : StopBehavior.END_OF_SCRIPT_GROUP;
    }

    /**
     * gets if we are using EIP from user data
     *
     * @return
     */
    public static boolean usingEip() {
        return StringUtils.isNotEmpty(getUserDataAsMap().get(TankConstants.KEY_USING_BIND_EIP));
    }

    /**
     * get controller base form user data
     *
     * @return
     */
    public static String getControllerBaseUrl() {
        String result = getUserDataAsMap().get(TankConstants.KEY_CONTROLLER_URL);
        return StringUtils.isNotEmpty(result)
                ? result
                : "http://localhost:8080/";
    }

    /**
     * get agent token form user data
     *
     * @return
     */
    public static String getAgentToken() {
        String result = getUserDataAsMap().get(TankConstants.KEY_AGENT_TOKEN);
        return StringUtils.isNotEmpty(result)
                ? result
                : "";
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     */
    public static Map<String, String> getUserDataAsMap() {
        try {
            String userData = getResponseString(BASE + USER_DATA);
            if (StringUtils.isNotEmpty(userData)) {
                return (Map<String,String>) new ObjectMapper().readValue(userData, Map.class);
            }
        } catch (IllegalArgumentException | IOException e) {
            LOG.warn(new ObjectMessage(ImmutableMap.of("Message","Unable to parse tank json: This is normal during the bake process")));
        }
        return Collections.emptyMap();
    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    private static String getResponseString(String url) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-aws-ec2-metadata-token", getToken())
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return response.body();
        } catch (InterruptedException e) {
            LOG.error(new ObjectMessage(ImmutableMap.of("Message","Unable to read userdata/metadata: " + e.getMessage())));
        }
        throw new IOException("Bad Response From AWS data");
    }

    /**
     * @return
     * @throws IOException
     */
    private static String getToken() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(BASE + TOKEN))
                .header("X-aws-ec2-metadata-token-ttl-seconds", "60")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return response.body();
        } catch (InterruptedException e) {
            LOG.error(new ObjectMessage(ImmutableMap.of("Message","Unable to read token: " + e.getMessage())));
        }
        throw new IOException("Bad Response From AWS token");
    }
}
