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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;

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

    private AmazonCloudWatchAsyncClient asynchCloudWatchClient;

    private AmazonSNSAsyncClient asyncSnsClient;

    private TankConfig config = new TankConfig();

    /**
     * 
     * @param request
     * @param vmRegion
     */
    public CloudwatchInstance(VMRegion vmRegion) {
        // In case vmRegion is passed as null, use default region from settings file
        if (vmRegion == null) {
            vmRegion = config.getVmManagerConfig().getDefaultRegion();
        }
        try {
            CloudCredentials creds = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            AWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
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
                asynchCloudWatchClient = new AmazonCloudWatchAsyncClient(credentials, clientConfig,
                        Executors.newFixedThreadPool(2));
                asyncSnsClient = new AmazonSNSAsyncClient(credentials, clientConfig, Executors.newFixedThreadPool(2));
            } else {
                asynchCloudWatchClient = new AmazonCloudWatchAsyncClient(clientConfig);
                asyncSnsClient = new AmazonSNSAsyncClient(clientConfig);
            }
            asynchCloudWatchClient.setRegion(Region.getRegion(Regions.fromName(vmRegion.getRegion())));
            asyncSnsClient.setRegion(Region.getRegion(Regions.fromName(vmRegion.getRegion())));
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
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (String instanceId : instances) {
            Dimension d = new Dimension().withName("InstanceId").withValue(instanceId);
            dimensions.add(d);

        }
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
            for (Topic s : topics) {
                if (s.getTopicArn().endsWith(topicName)) {
                    ret = s.getTopicArn();
                    break;
                }
            }
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
        String ret = "AgentEmailTopicFor_" + email.replaceAll("\\W+", "_");
        return ret;
    }

    private String getAlarmName(String email, String jobId) {
        String ret = "Job " + jobId + " Alarm Excessive CPU for Email " + email;
        ret = ret.replaceAll("\\W+", "_");
        return ret;
    }

}
