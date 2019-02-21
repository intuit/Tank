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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AmazonS3 {
    private static final Logger LOG = LogManager.getLogger(AmazonS3.class);

    private com.amazonaws.services.s3.AmazonS3 s3Client;

    /**
     * 
     */
    public AmazonS3() {

        try {
            CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(
                    CloudProvider.amazon);
            ClientConfiguration config = new ClientConfiguration();
            if (StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
                try {
                    config.setProxyHost(System.getProperty("http.proxyHost"));
                    if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                        config.setProxyPort(Integer.valueOf(System.getProperty("http.proxyPort")));
                    }
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }

            }
            if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                BasicAWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
                this.s3Client = AmazonS3ClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(config)
                        .build();
            } else {
                this.s3Client = AmazonS3ClientBuilder.standard()
                        .withClientConfiguration(config)
                        .build();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void createBucket(String bucketName) {
        AccessControlList configuration = s3Client.getBucketAcl(bucketName);
        if (configuration == null) {
            Bucket bucket = s3Client.createBucket(bucketName);
            LOG.info("Created bucket " + bucket.getName() + " at " + bucket.getCreationDate());
        }
    }

    /**
     *
     * @param bucketName
     *            the base bucketname
     * @param path
     *            the
     * @return
     */
    public void storeFile(String bucketName, String path, Map<String, String> metaMap, InputStream in) {
        ObjectMetadata metaData = new ObjectMetadata();
        if (metaMap != null) {
            for (Entry<String, String> entry : metaMap.entrySet()) {
                metaData.addUserMetadata(entry.getKey(), entry.getValue());
            }
        }
        s3Client.putObject(bucketName, path, in, metaData);
    }

    /**
     *
     * @param bucketName
     *            the base bucketname
     * @param path
     *            the
     * @return
     */
    public InputStream getFile(String bucketName, String path) {
        InputStream ret = null;
        S3Object object = s3Client.getObject(bucketName, path);
        if (object != null) {
            try ( S3ObjectInputStream objectContent = object.getObjectContent();
                  ByteArrayOutputStream temp = new ByteArrayOutputStream()){
                IOUtils.copy(objectContent, temp);
                ret = new ByteArrayInputStream(temp.toByteArray());
            } catch (Exception e) {
                LOG.error("Error getting File: " + e, e);
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * gets the metadata associated with the file
     * 
     * @param bucketName
     *            the base bucketname
     * @param path
     *            the
     * @return
     */
    public Map<String, String> getFileMetaData(String bucketName, String path) {
        Map<String, String> ret = new HashMap<String, String>();
        try {
            ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucketName, path);
            if (objectMetadata != null) {
                for (Entry<String, Object> entry : objectMetadata.getRawMetadata().entrySet()) {
                    ret.put(entry.getKey(), entry.getValue().toString());
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting MetaData: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

}
