package com.intuit.tank.vmManager.environment.amazon;

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

import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AmazonS3 {
    private static final Logger LOG = LogManager.getLogger(AmazonS3.class);

    private S3Client s3Client;

    /**
     * 
     */
    public AmazonS3() {

        try {
            CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
            if (StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
                try {
                    URIBuilder uriBuilder = new URIBuilder().setHost(System.getProperty("http.proxyHost"));
                    if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                        uriBuilder.setPort(Integer.parseInt(System.getProperty("http.proxyPort")));
                    }
                    httpClientBuilder.proxyConfiguration(
                            ProxyConfiguration.builder().endpoint(uriBuilder.build()).build());
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }
            }
            if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                this.s3Client = S3Client.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .httpClientBuilder(httpClientBuilder)
                        .build();
            } else {
                this.s3Client = S3Client.builder()
                        .httpClientBuilder(httpClientBuilder)
                        .build();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            LOG.info("Created bucket " + bucketName + " at " + "now");
        } catch (S3Exception e) {
            LOG.error("Error creating bucket: " + e, e);
        }
    }

    /**
     *
     * @param bucketName
     *            the base bucketname
     * @param key
     *            the object location
     */
    public void storeFile(String bucketName, String key, Map<String, String> metaMap, InputStream in) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(key).metadata(metaMap).build(),
                    RequestBody.fromInputStream(in, in.available()));
        } catch (IOException e) {
            LOG.error("IO Error putting stream into bucket: " + e, e);
    }
    }

    /**
     *
     * @param bucketName
     *            the base bucketname
     * @param key
     *            the object location
     * @return
     */
    public InputStream getStream(String bucketName, String key) {
        return s3Client
                .getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    /**
     * gets the metadata associated with the file
     * 
     * @param bucketName
     *            the base bucketname
     * @param key
     *            the object location
     * @return
     */
    public Map<String, String> getFileMetaData(String bucketName, String key) {
        Map<String, String> ret = new HashMap<String, String>();
        try {
            HeadObjectResponse response = s3Client
                    .headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
            if (response.metadata() != null) {
                for (Entry<String, String> entry : response.metadata().entrySet()) {
                    ret.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting MetaData: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

}
