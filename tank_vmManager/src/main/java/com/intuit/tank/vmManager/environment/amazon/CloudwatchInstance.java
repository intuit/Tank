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
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClientBuilder;
import software.amazon.awssdk.services.cloudwatch.model.ComparisonOperator;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAlarmsRequest;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricAlarm;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * CloudwatchInstance
 * 
 * @author dangleton
 * 
 */
public class CloudwatchInstance {

    protected static final long ASSOCIATE_IP_MAX_WAIT_MILIS = 1000 * 60 * 10;// ten minutes
    private static Logger LOG = LogManager.getLogger(CloudwatchInstance.class);

    private CloudWatchClient cloudWatchClient;

    private SnsClient snsClient;

    private TankConfig config = new TankConfig();

    /**
     * 
     * @param vmRegion
     */
    public CloudwatchInstance(VMRegion vmRegion) {
        // In case vmRegion is passed as null, use default region from settings file
        if (vmRegion == null) {
            vmRegion = config.getVmManagerConfig().getDefaultRegion();
        }
        try {
            CloudCredentials creds = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            CloudWatchClientBuilder cloudWatchClientBuilder = CloudWatchClient.builder();
            SnsClientBuilder snsClientBuilder = SnsClient.builder();
            if (creds != null && StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    URIBuilder uriBuilder = new URIBuilder().setHost(creds.getProxyHost());
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        uriBuilder.setPort(Integer.parseInt(System.getProperty(creds.getProxyPort())));
                    }
                    ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                            .proxyConfiguration(
                                    ProxyConfiguration.builder().endpoint(uriBuilder.build()).build());
                    cloudWatchClientBuilder.httpClientBuilder(httpClientBuilder);
                    snsClientBuilder.httpClientBuilder(httpClientBuilder);
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }
            }
            if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
                AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                cloudWatchClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
                snsClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
            }
            cloudWatchClient = cloudWatchClientBuilder
                    .region(Region.of(vmRegion.getRegion()))
                    .build();
            snsClient = snsClientBuilder
                    .region(Region.of(vmRegion.getRegion()))
                    .build();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @param email
     * @param jobId
     */
    public void removeWatch(String email, String jobId) {
        String alarmName = getAlarmName(email, jobId);
        for (MetricAlarm alarm : cloudWatchClient.describeAlarms().metricAlarms()) {
            if (alarm.alarmName().equalsIgnoreCase(alarmName)) {
                DeleteAlarmsRequest request = DeleteAlarmsRequest.builder().alarmNames(alarmName).build();
                cloudWatchClient.deleteAlarms(request);
            }
        }
    }

    /**
     * 
     * @param instances
     * @param email
     * @param jobId
     */
    public void addWatch(Collection<String> instances, String email, String jobId) {

        String alarmName = getAlarmName(email, jobId);
        for (MetricAlarm alarm : cloudWatchClient.describeAlarms().metricAlarms()) {
            if (alarm.alarmName().equalsIgnoreCase(alarmName)) {
                LOG.info("Alarm for job " + jobId + " and email " + email + " already exists.");
                return;
            }
        }
        List<Dimension> dimensions = instances.stream()
                .map(instanceId -> Dimension.builder().name("InstanceId").value(instanceId).build())
                .collect(Collectors.toList());
        PutMetricAlarmRequest request = PutMetricAlarmRequest.builder()
                .actionsEnabled(true)
                .alarmName(alarmName)
                .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                .dimensions(dimensions)
                .alarmActions(getOrCreateNotification(email))
                .evaluationPeriods(1)
                .period(60)
                .threshold(60.0D)
                .statistic(Statistic.AVERAGE)
                .metricName("CPUUtilization")
                .namespace("AWS/EC2")
                .build();
        cloudWatchClient.putMetricAlarm(request);
        LOG.info("Created alarm " + alarmName);
    }

    /**
     * 
     * @param email
     * @return
     */
    public String getOrCreateNotification(String email) {
        String ret = null;
        String topicName = getTopicName(email);
        String nextToken = null;
        do {
            ListTopicsResponse listTopics = snsClient.listTopics(ListTopicsRequest.builder().nextToken(nextToken).build());
            List<Topic> topics = listTopics.topics();
            ret = topics.stream().filter(s -> s.topicArn().endsWith(topicName)).findFirst().map(Topic::topicArn).orElse(ret);
            nextToken = listTopics.nextToken();
        } while (ret == null && nextToken != null);
        if (ret == null) {
            // create the topic and the subscription
            CreateTopicResponse topic = snsClient.createTopic(CreateTopicRequest.builder().name(topicName).build());
            snsClient.subscribe(
                    SubscribeRequest.builder().topicArn(topic.topicArn()).attributes(
                            new HashMap<String,String>(){{ put( "email", email); }}).build());
            ret = topic.topicArn();
        }
        return ret;
    }

    private String getTopicName(String email) {
        return "AgentEmailTopicFor_" + email.replaceAll("\\W+", "_");
    }

    private String getAlarmName(String email, String jobId) {
        String ret = "Job " + jobId + " Alarm Excessive CPU for Email " + email;
        ret = ret.replaceAll("\\W+", "_");
        return ret;
    }

}
