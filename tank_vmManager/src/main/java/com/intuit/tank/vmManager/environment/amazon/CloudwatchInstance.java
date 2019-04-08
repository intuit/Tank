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
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClientBuilder;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
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
    private static Logger logger = LogManager.getLogger(CloudwatchInstance.class);

    private AmazonCloudWatchAsync asynchCloudWatchClient;

    private AmazonSNSAsync asyncSnsClient;

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
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setMaxConnections(2);
            if (StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    clientConfig.setProxyHost(creds.getProxyHost());
                    
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        clientConfig.setProxyPort(Integer.valueOf(creds.getProxyPort()));
                    }
                } catch (NumberFormatException e) {
                    logger.error("invalid proxy setup.");
                }

            }
            if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                AWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
                asynchCloudWatchClient = AmazonCloudWatchAsyncClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
                asyncSnsClient = AmazonSNSAsyncClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
            } else {
                asynchCloudWatchClient = AmazonCloudWatchAsyncClientBuilder.standard()
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
                asyncSnsClient = AmazonSNSAsyncClientBuilder.standard()
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
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
        for (MetricAlarm a : asynchCloudWatchClient.describeAlarms().getMetricAlarms()) {
            if (a.getAlarmName().equalsIgnoreCase(alarmName)) {
                DeleteAlarmsRequest req = new DeleteAlarmsRequest().withAlarmNames(alarmName);
                asynchCloudWatchClient.deleteAlarmsAsync(req);
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
        for (MetricAlarm a : asynchCloudWatchClient.describeAlarms().getMetricAlarms()) {
            if (a.getAlarmName().equalsIgnoreCase(alarmName)) {
                logger.info("Alarm for job " + jobId + " and email " + email + " already exists.");
                return;
            }
        }
        List<Dimension> dimensions = instances.stream().map(instanceId -> new Dimension().withName("InstanceId").withValue(instanceId)).collect(Collectors.toList());
        PutMetricAlarmRequest request = new PutMetricAlarmRequest()
                .withActionsEnabled(true).withAlarmName(alarmName)
                .withComparisonOperator(ComparisonOperator.GreaterThanOrEqualToThreshold)
                .withDimensions(dimensions)
                .withAlarmActions(getOrCreateNotification(email))
                .withEvaluationPeriods(1)
                .withPeriod(60)
                .withThreshold(60.0D)
                .withStatistic(Statistic.Average)
                .withMetricName("CPUUtilization")
                .withNamespace("AWS/EC2");
        asynchCloudWatchClient.putMetricAlarm(request);
        logger.info("Created alarm " + alarmName);
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
            ListTopicsResult listTopics = asyncSnsClient.listTopics(nextToken);
            List<Topic> topics = listTopics.getTopics();
            ret = topics.stream().filter(s -> s.getTopicArn().endsWith(topicName)).findFirst().map(Topic::getTopicArn).orElse(ret);
            nextToken = listTopics.getNextToken();
        } while (ret == null && nextToken != null);
        if (ret == null) {
            // create the topic and the subscription
            CreateTopicResult topic = asyncSnsClient.createTopic(topicName);
            SubscribeRequest req = new SubscribeRequest(topic.getTopicArn(), "email", email);
            asyncSnsClient.subscribeAsync(req);
            ret = topic.getTopicArn();
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
