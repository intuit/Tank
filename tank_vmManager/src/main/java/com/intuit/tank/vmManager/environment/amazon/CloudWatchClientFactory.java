package com.intuit.tank.vmManager.environment.amazon;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.api.enumerated.VMRegion;
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
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.client.builder.SdkSyncClientBuilder;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds CloudWatch clients the way the controller is configured: the proxy and static credentials from
 * {@code TankConfig} when set, otherwise the default credential chain. {@link #configure} is shared with
 * {@link CloudwatchInstance} (which also builds an SNS client), and {@link #forRegion} hands the metrics
 * collector one cached client per region.
 */
public class CloudWatchClientFactory {

    private static final Logger LOG = LogManager.getLogger(CloudWatchClientFactory.class);

    private final Map<VMRegion, CloudWatchClient> clients = new ConcurrentHashMap<>();

    /**
     * Applies the configured proxy and static credentials, if any, to the builder.
     */
    public static <B extends AwsClientBuilder<B, ?> & SdkSyncClientBuilder<B, ?>> B configure(B builder, CloudCredentials creds) {
        if (creds != null && StringUtils.isNotBlank(creds.getProxyHost())) {
            try {
                URIBuilder uriBuilder = new URIBuilder().setHost(creds.getProxyHost());
                if (StringUtils.isNotBlank(creds.getProxyPort())) {
                    uriBuilder.setPort(Integer.parseInt(System.getProperty(creds.getProxyPort())));
                }
                builder.httpClientBuilder(ApacheHttpClient.builder()
                        .proxyConfiguration(ProxyConfiguration.builder().endpoint(uriBuilder.build()).build()));
            } catch (NumberFormatException e) {
                LOG.error("invalid proxy setup.");
            } catch (java.net.URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }
        if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
            AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        }
        return builder;
    }

    /**
     * The client for the region, built on first use and kept. A null region means the configured default.
     */
    public CloudWatchClient forRegion(VMRegion region) {
        TankConfig config = new TankConfig();
        VMRegion effective = region != null ? region : config.getVmManagerConfig().getDefaultRegion();
        return clients.computeIfAbsent(effective, r -> configure(CloudWatchClient.builder(),
                config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon))
                .region(Region.of(r.getRegion()))
                .build());
    }

    /** Closes every cached client. */
    public void close() {
        clients.values().forEach(CloudWatchClient::close);
        clients.clear();
    }
}
