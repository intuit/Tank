package com.intuit.tank.common;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.intuit.tank.api.model.v1.cloud.TPSInfo;
import com.intuit.tank.api.model.v1.cloud.TPSInfoContainer;
import com.intuit.tank.persistence.databases.AmazonDynamoDatabaseDocApi;
import com.intuit.tank.persistence.databases.DatabaseKeys;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.common.util.ReportUtil;

public class TPSTest {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TPSTest.class);

    private AmazonDynamoDBClient dbclient;

    @BeforeSuite(groups = TestGroups.EXPERIMENTAL)
    public void init() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSCredentials credentials = new BasicAWSCredentials(System.getProperty("AWS_KEY_ID"),
                System.getProperty("AWS_KEY"));
        dbclient = new AmazonDynamoDBClient(credentials, clientConfig);
    }

    @Test(groups = TestGroups.EXPERIMENTAL)
    private void sendTps() {
        TPSInfoContainer tpsInfo = createTPsInfo();
        try {
            IDatabase db = new AmazonDynamoDatabaseDocApi(dbclient);
            List<com.intuit.tank.reporting.databases.Item> items = new ArrayList<com.intuit.tank.reporting.databases.Item>();
            for (TPSInfo info : tpsInfo.getTpsInfos()) {
                com.intuit.tank.reporting.databases.Item item = createItem(info);
                items.add(item);
            }
            String tpsTableName = "test_qa";
            db.createTable(tpsTableName);
            LOG.info("Sending " + items.size() + " to TPS Table " + tpsTableName);
            db.addItems(tpsTableName, items, false);
        } catch (Exception e) {
            LOG.error("Error storing TPS: " + e, e);
        }

    }

    private TPSInfoContainer createTPsInfo() {
        Random rand = new Random();
        List<TPSInfo> infoList = new ArrayList<TPSInfo>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -234);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        Date min = cal.getTime();
        for (int i = 0; i < 100; i++) {
            Date start = cal.getTime();
            cal.add(Calendar.SECOND, 15);
            String key = "Apache v1 page 1::12::the other one";
            TPSInfo info = new TPSInfo(start, key, rand.nextInt(49) + 1, 15);
            infoList.add(info);
        }
        Date max = cal.getTime();
        return new TPSInfoContainer(min, max, 15, infoList);
    }

    private com.intuit.tank.reporting.databases.Item createItem(TPSInfo info) {
        com.intuit.tank.reporting.databases.Item item = new com.intuit.tank.reporting.databases.Item();
        List<Attribute> attributes = new ArrayList<Attribute>();
        String ts = ReportUtil.getTimestamp(info.getTimestamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), ts);
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), "1234");
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), "i-1234443322");
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), info.getKey());
        addAttribute(attributes, DatabaseKeys.PERIOD_KEY.getShortKey(), Integer.toString(info.getPeriodInSeconds()));
        addAttribute(attributes, DatabaseKeys.TRANSACTIONS_KEY.getShortKey(), Integer.toString(info.getTransactions()));
        item.setAttributes(attributes);
        String name = "i-1234443322"
                + "_" + "1234"
                + "_" + info.getKey()
                + "_" + ts;
        item.setName(name);
        return item;
    }

    public static void addAttribute(List<Attribute> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.add(new Attribute(key, value));
    }
}
