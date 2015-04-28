/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.persistence.databases;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;

import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DatabaseResultsReporter
 * 
 * @author dangleton
 * 
 */
public class DatabaseResultsReporter implements ResultsReporter {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DatabaseResultsReporter.class);

    private String prefix;

    /**
     * @{inheritDoc
     */
    @Override
    public void sendTpsResults(String jobId, String instanceId, TPSInfoContainer container) {
        try {
            IDatabase db = DataBaseFactory.getDatabase();
            List<Item> items = new ArrayList<Item>();
            for (TPSInfo info : container.getTpsInfos()) {
                Item item = createItem(jobId, instanceId, info);
                items.add(item);
            }
            String tableName = getTpsTableName();
            LOG.info("Sending " + items.size() + " to TPS Table " + tableName);
            db.addItems(tableName, items, false);
        } catch (Exception e) {
            LOG.error("Error storing TPS: " + e, e);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void sendTimingResults(String jobId, String instanceId, List<TankResult> results, boolean asynch) {
        IDatabase db = DataBaseFactory.getDatabase();
        String tableName = db.getDatabaseName(TankDatabaseType.timing, jobId);
        if (results.size() != 0 && tableName != null) {
            final List<TankResult> l = new ArrayList<TankResult>();
            synchronized (results) {
                l.addAll(results);
                results.clear();
            }
            DataBaseFactory.getDatabase().addTimingResults(tableName, l, asynch);
        }

    }

    private Item createItem(String jobId, String instanceId, TPSInfo info) {
        Item item = new Item();
        List<Attribute> attributes = new ArrayList<Attribute>();
        String ts = ReportUtil.getTimestamp(info.getTimestamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), ts);
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), jobId);
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), instanceId);
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), info.getKey());
        addAttribute(attributes, DatabaseKeys.PERIOD_KEY.getShortKey(), Integer.toString(info.getPeriodInSeconds()));
        addAttribute(attributes, DatabaseKeys.TRANSACTIONS_KEY.getShortKey(), Integer.toString(info.getTransactions()));
        item.setAttributes(attributes);
        String name = instanceId
                + "_" + jobId
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

    @Override
    public void config(HierarchicalConfiguration config) {
        // nothing to configure
    }

    private String getTpsTableName() {
        if (StringUtils.isBlank(prefix)) {
            prefix = new TankConfig().getInstanceName();
        }
        return prefix + "_tps";
    }

}
