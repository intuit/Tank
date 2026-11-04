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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.message.ObjectMessage;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.imds.Ec2MetadataClient;
import software.amazon.awssdk.imds.Ec2MetadataRetryPolicy;

/**
 * 
 * AmazonUtil
 * 
 * @author dangleton
 * 
 */
public class AmazonUtil {

    private static final Logger LOG = LogManager.getLogger(AmazonUtil.class);
    protected static final String LATEST = "/latest";
    protected static final String USER_DATA = LATEST + "/user-data";
    protected static final String META_DATA = LATEST + "/meta-data";
    /** IMDS endpoint override (null means the SDK default, 169.254.169.254); used by tests. */
    protected static volatile URI endpoint;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {};
    /** Metadata that cannot change for the lifetime of an instance, so it is safe to cache once read. */
    private static final Set<CloudMetaDataType> STABLE_META_DATA =
            EnumSet.of(CloudMetaDataType.zone, CloudMetaDataType.instance_id, CloudMetaDataType.instance_type);
    private static final Map<CloudMetaDataType, String> META_DATA_CACHE = new ConcurrentHashMap<>();
    /** Only set after a successful, non-empty read; the user data is absent while the AMI is being baked. */
    private static volatile Map<String, String> userDataCache;
    private static Ec2MetadataClient client;
    private static URI clientEndpoint;

    /** Drops cached metadata and user data. Intended for tests that change {@link #endpoint}. */
    protected static void resetCache() {
        META_DATA_CACHE.clear();
        userDataCache = null;
    }

    public static VMRegion getVMRegion() {
        try {
            String zone = getMetaData(CloudMetaDataType.zone);
            return VMRegion.getRegionFromZone(zone);
        } catch (IOException ioe) {
            LOG.warn(new ObjectMessage(Map.of("Message","Error getting region. using CUSTOM...")));
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
            LOG.info(new ObjectMessage(Map.of("Message","cannot determine zone")));
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
            LOG.warn(new ObjectMessage(Map.of("Message", "Error getting instance ID: " + e.toString())));
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
        String cached = META_DATA_CACHE.get(metaData);
        if (cached != null) {
            return cached;
        }
        String value = getResponseString(META_DATA + "/" + metaData.getKey());
        if (STABLE_META_DATA.contains(metaData)) {
            META_DATA_CACHE.put(metaData, value);
        }
        return value;
    }

    /**
     * gets the job id form user data
     *
     * @return
     */
    public static String getJobId() {
        return userData(TankConstants.KEY_JOB_ID, "unknown");
    }

    /**
     * gets the project name for user data
     *
     * @return
     */
    public static String getProjectName() {
        return userData(TankConstants.KEY_PROJECT_NAME, "unknown");
    }

    /**
     * gets logging profile form user data
     *
     * @return LoggingProfile
     */
    public static LoggingProfile getLoggingProfile() {
        return LoggingProfile.valueOf(userData(TankConstants.KEY_LOGGING_PROFILE, LoggingProfile.STANDARD.name()));
    }

    public static int getCapacity() {
        return Integer.parseInt(userData(TankConstants.KEY_NUM_USERS_PER_AGENT, "4000"));
    }

    /**
     * gets stop behavior form user data.
     *
     * @return Stopbehavior
     */
    public static StopBehavior getStopBehavior() {
        return StopBehavior.valueOf(userData(TankConstants.KEY_STOP_BEHAVIOR, StopBehavior.END_OF_SCRIPT_GROUP.name()));
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
        return userData(TankConstants.KEY_CONTROLLER_URL, "http://localhost:8080/");
    }

    /**
     * get agent token form user data
     *
     * @return
     */
    public static String getAgentToken() {
        return userData(TankConstants.KEY_AGENT_TOKEN, "");
    }

    /**
     * Gets the user data associated with this instance.
     * 
     * @return the user data as a Map
     */
    public static Map<String, String> getUserDataAsMap() {
        Map<String, String> cached = userDataCache;
        if (cached != null) {
            return cached;
        }
        try {
            String userData = getResponseString(USER_DATA);
            if (StringUtils.isNotEmpty(userData)) {
                Map<String, String> parsed = Collections.unmodifiableMap(MAPPER.readValue(userData, STRING_MAP));
                userDataCache = parsed;
                return parsed;
            }
        } catch (IllegalArgumentException | IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message","Unable to parse tank json: This is normal during the bake process")));
        }
        return Collections.emptyMap();
    }

    private static String userData(String key, String defaultValue) {
        String result = getUserDataAsMap().get(key);
        return StringUtils.isNotEmpty(result) ? result : defaultValue;
    }

    private static synchronized Ec2MetadataClient getClient() {
        URI current = endpoint;
        if (client == null || !Objects.equals(current, clientEndpoint)) {
            if (client != null) {
                client.close();
            }
            Ec2MetadataClient.Builder builder = Ec2MetadataClient.builder()
                    .retryPolicy(Ec2MetadataRetryPolicy.builder().numRetries(0).build());
            if (current != null) {
                builder.endpoint(current);
            }
            client = builder.build();
            clientEndpoint = current;
        }
        return client;
    }

    /**
     * Reads a path from the instance metadata service (IMDSv2 token handling is done by the SDK).
     *
     * @param path
     *            absolute IMDS path, e.g. /latest/meta-data/instance-id
     * @return the response body
     * @throws IOException
     *             if the service cannot be reached or returns an error
     */
    private static String getResponseString(String path) throws IOException {
        try {
            return getClient().get(path).asString();
        } catch (SdkClientException e) {
            throw new IOException("Bad Response From AWS data: " + e.getMessage(), e);
        }
    }
}
