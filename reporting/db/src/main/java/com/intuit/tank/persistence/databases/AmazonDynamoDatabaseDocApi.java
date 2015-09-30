package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;

public class AmazonDynamoDatabaseDocApi implements IDatabase {

    private static final int MAX_NUMBER_OF_RETRIES = 5;
    private AmazonDynamoDB dynamoDb;
    private TankConfig config = new TankConfig();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    protected static final int BATCH_SIZE = 25;
    private static final long MAX_WRITE_UNITS = 1500L;

    private static Logger logger = Logger.getLogger(AmazonDynamoDatabaseDocApi.class);

    /**
     * 
     * @param dynamoDb
     */
    public AmazonDynamoDatabaseDocApi() {
        CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
        if (creds != null && StringUtils.isNotBlank(creds.getKeyId())) {
            AWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
            this.dynamoDb = new AmazonDynamoDBClient(credentials, new ClientConfiguration());
        } else {
            this.dynamoDb = new AmazonDynamoDBClient(new ClientConfiguration());
        }

        
    }

    /**
     * 
     * @param dynamoDb
     */
    public AmazonDynamoDatabaseDocApi(AmazonDynamoDB dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void createTable(String tableName) {
        try {
            if (!hasTable(tableName)) {
                logger.info("Creating table: " + tableName);
                HierarchicalConfiguration resultsProviderConfig = config.getVmManagerConfig()
                        .getResultsProviderConfig();
                long readCapacity = getCapacity(resultsProviderConfig, "read-capacity", 10L);
                long writeCapacity = getCapacity(resultsProviderConfig, "write-capacity", 50L);
                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(new AttributeDefinition().withAttributeName(
                        DatabaseKeys.JOB_ID_KEY.getShortKey()).withAttributeType(ScalarAttributeType.S));
                attributeDefinitions.add(new AttributeDefinition().withAttributeName(
                        DatabaseKeys.REQUEST_NAME_KEY.getShortKey()).withAttributeType(ScalarAttributeType.S));
                ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput().withReadCapacityUnits(
                        readCapacity).withWriteCapacityUnits(writeCapacity);
                KeySchemaElement hashKeyElement = new KeySchemaElement().withAttributeName(
                        DatabaseKeys.JOB_ID_KEY.getShortKey()).withKeyType(KeyType.HASH);
                KeySchemaElement rangeKeyElement = new KeySchemaElement().withAttributeName(
                        DatabaseKeys.REQUEST_NAME_KEY.getShortKey()).withKeyType(KeyType.RANGE);
                CreateTableRequest request = new CreateTableRequest()
                        .withTableName(tableName)
                        .withKeySchema(hashKeyElement, rangeKeyElement)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withProvisionedThroughput(provisionedThroughput);

                CreateTableResult result = dynamoDb.createTable(request);
                waitForStatus(tableName, TableStatus.ACTIVE);
                logger.info("Created table: " + result.getTableDescription().getTableName());
            }
        } catch (Exception t) {
            logger.error(t, t);
            throw new RuntimeException(t);
        }
    }

    private long getCapacity(HierarchicalConfiguration resultsProviderConfig, String key, long defaultValue) {
        long ret = defaultValue;
        if (resultsProviderConfig != null) {
            try {
                String string = resultsProviderConfig.getString(key);
                if (NumberUtils.isDigits(string)) {
                    return Long.parseLong(string);
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return ret;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void deleteTable(String tableName) {
        try {
            if (hasTable(tableName)) {
                logger.info("Deleting table: " + tableName);
                DeleteTableRequest deleteTableRequest = new DeleteTableRequest(tableName);
                DeleteTableResult result = dynamoDb.deleteTable(deleteTableRequest);
                logger.info("Deleted table: " + result.getTableDescription().getTableName());
                waitForDelete(tableName);
            }
        } catch (Exception t) {
            logger.error(t, t);
            throw new RuntimeException(t);
        }
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public boolean hasTable(String tableName) {
        String nextTableName = null;
        do {
            ListTablesResult listTables = dynamoDb.listTables(new ListTablesRequest()
                    .withExclusiveStartTableName(nextTableName));
            for (String name : listTables.getTableNames()) {
                if (tableName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            nextTableName = listTables.getLastEvaluatedTableName();
        } while (nextTableName != null);
        return false;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void addTimingResults(final @Nonnull String tableName, final @Nonnull List<TankResult> results,
            boolean async) {
        if (!results.isEmpty()) {
            Runnable task = new Runnable() {
                public void run() {
                    MethodTimer mt = new MethodTimer(logger, this.getClass(), "addTimingResults (" + results + ")");
                    List<WriteRequest> requests = new ArrayList<WriteRequest>();
                    try {
                        for (TankResult result : results) {
                            Map<String, AttributeValue> item = getTimingAttributes(result);
                            PutRequest putRequest = new PutRequest().withItem(item);
                            WriteRequest writeRequest = new WriteRequest().withPutRequest(putRequest);
                            requests.add(writeRequest);
                        }
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        logger.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                    mt.endAndLog();
                }
            };
            if (async) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public Set<String> getTables(String regex) {
        Set<String> result = new HashSet<String>();
        String nextTableName = null;
        do {
            ListTablesResult listTables = dynamoDb.listTables(new ListTablesRequest()
                    .withExclusiveStartTableName(nextTableName));
            for (String s : listTables.getTableNames()) {
                if (s.matches(regex)) {
                    result.add(s);
                }
            }
            nextTableName = listTables.getLastEvaluatedTableName();
        } while (nextTableName != null);

        return result;
    }

    /**
     * @{inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public PagedDatabaseResult getPagedItems(String tableName, Object nextToken, String minRange,
            String maxRange, String instanceId, String jobId) {
        List<Item> ret = new ArrayList<Item>();
        Map<String, AttributeValue> lastKeyEvaluated = (Map<String, AttributeValue>) nextToken;
        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
        Map<String, Condition> conditions = new HashMap<String, Condition>();
        if (jobId != null) {
            Condition jobIdCondition = new Condition();
            jobIdCondition.withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withS(jobId));

            conditions.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), jobIdCondition);
        }
        if (StringUtils.isNotBlank(instanceId)) {
            // add a filter
            Condition filter = new Condition();
            filter.withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                    new AttributeValue().withS(instanceId));
            scanRequest.addScanFilterEntry(DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), filter);
        }
        Condition rangeKeyCondition = new Condition();
        if (minRange != null && maxRange != null) {
            rangeKeyCondition.withComparisonOperator(ComparisonOperator.BETWEEN.toString())
                    .withAttributeValueList(new AttributeValue().withS(minRange))
                    .withAttributeValueList(new AttributeValue().withS(maxRange));

        } else if (minRange != null) {
            rangeKeyCondition.withComparisonOperator(ComparisonOperator.GE.toString())
                    .withAttributeValueList(new AttributeValue().withS(minRange));
        } else if (maxRange != null) {
            rangeKeyCondition.withComparisonOperator(ComparisonOperator.LT.toString())
                    .withAttributeValueList(new AttributeValue().withS(maxRange));
        } else {
            rangeKeyCondition = null;
        }
        if (rangeKeyCondition != null) {
            conditions.put(DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), rangeKeyCondition);
        }
        scanRequest.withScanFilter(conditions);
        scanRequest.withExclusiveStartKey(lastKeyEvaluated);

        ScanResult result = dynamoDb.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            ret.add(getItemFromResult(item));
        }
        return new PagedDatabaseResult(ret, result.getLastEvaluatedKey());
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId,
            String... jobIds) {
        List<Item> ret = new ArrayList<Item>();
        for (String jobId : jobIds) {
            Object lastKeyEvaluated = null;
            do {
                PagedDatabaseResult pagedItems = getPagedItems(tableName, lastKeyEvaluated, minRange, maxRange,
                        instanceId, jobId);
                ret.addAll(pagedItems.getItems());
                lastKeyEvaluated = pagedItems.getNextToken();
            } while (lastKeyEvaluated != null);
        }
        return ret;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void addItems(final String tableName, List<Item> itemList, final boolean asynch) {
        if (!itemList.isEmpty()) {
            final List<Item> items = new ArrayList<Item>(itemList);
            Runnable task = new Runnable() {
                public void run() {
                    MethodTimer mt = new MethodTimer(logger, this.getClass(), "addItems (" + items + ")");
                    List<WriteRequest> requests = new ArrayList<WriteRequest>();
                    try {
                        for (Item item : items) {
                            Map<String, AttributeValue> toInsert = itemToMap(item);
                            PutRequest putRequest = new PutRequest().withItem(toInsert);
                            WriteRequest writeRequest = new WriteRequest().withPutRequest(putRequest);
                            requests.add(writeRequest);
                        }
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        logger.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                    mt.endAndLog();
                }
            };
            if (asynch) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void deleteForJob(final String tableName, final String jobId, final boolean asynch) {
        Runnable task = new Runnable() {
            public void run() {
                MethodTimer mt = new MethodTimer(logger, this.getClass(), "deleteForJob (" + jobId + ")");

                List<Item> items = getItems(tableName, null, null, null, jobId);
                if (!items.isEmpty()) {
                    List<WriteRequest> requests = new ArrayList<WriteRequest>();
                    try {
                        for (Item item : items) {
                            String id = null;
                            for (Attribute attr : item.getAttributes()) {
                                if (DatabaseKeys.REQUEST_NAME_KEY.getShortKey().equals(attr.getName())) {
                                    id = attr.getValue();
                                    break;
                                }
                            }
                            if (id != null) {
                                Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
                                keyMap.put(DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), new AttributeValue().withS(id));
                                keyMap.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), new AttributeValue().withS(jobId));
                                DeleteRequest deleteRequest = new DeleteRequest().withKey(keyMap);
                                WriteRequest writeRequest = new WriteRequest().withDeleteRequest(deleteRequest);
                                requests.add(writeRequest);
                            }
                        }
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        logger.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                }
                mt.endAndLog();
            }
        };
        if (asynch) {
            EXECUTOR.execute(task);
        } else {
            task.run();
        }

    }

    @Override
    public String getDatabaseName(TankDatabaseType type, String jobId) {
        return type.name() + "_" + new TankConfig().getInstanceName();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean hasJobData(String tableName, String jobId) {
        if (hasTable(tableName)) {
            Map<String, Condition> keyConditions = new HashMap<String, Condition>();
            Condition jobIdCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withS(jobId));
            keyConditions.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), jobIdCondition);
            QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(keyConditions)
                    .withLimit(1);

            return dynamoDb.query(queryRequest).getCount() > 0;
        }
        return false;
    }

    /**
     * @param item
     * @return
     */
    private Item getItemFromResult(Map<String, AttributeValue> attributeMap) {
        List<Attribute> attrs = new ArrayList<Attribute>();
        Item ret = new Item(null, attrs);
        for (Map.Entry<String, AttributeValue> item : attributeMap.entrySet()) {
            Attribute a = new Attribute(item.getKey(), item.getValue().getS());
            attrs.add(a);
            if (a.getName().equalsIgnoreCase(DatabaseKeys.LOGGING_KEY_KEY.getShortKey())) {
                ret.setName(a.getValue());
            }
        }
        return ret;
    }

    private Map<String, AttributeValue> getTimingAttributes(TankResult result) {
        Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
        String timestamp = ReportUtil.getTimestamp(result.getTimeStamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), timestamp);
        addAttribute(attributes, DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), timestamp + "-"
                + UUID.randomUUID().toString());
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), result.getJobId());
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), result.getRequestName());
        addAttribute(attributes, DatabaseKeys.STATUS_CODE_KEY.getShortKey(), String.valueOf(result.getStatusCode()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_TIME_KEY.getShortKey(),
                String.valueOf(result.getResponseTime()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_SIZE_KEY.getShortKey(), String.valueOf(result.getResponseSize()));
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), String.valueOf(result.getInstanceId()));
        addAttribute(attributes, DatabaseKeys.IS_ERROR_KEY.getShortKey(), String.valueOf(result.isError()));
        return attributes;
    }

    private void addAttribute(Map<String, AttributeValue> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.put(key, new AttributeValue().withS(value));
    }

    private void addItemsToTable(String tableName, final BatchWriteItemRequest request) {

        boolean shouldRetry;
        int retries = 0;

        do {
            shouldRetry = false;
            try {
                BatchWriteItemResult result = dynamoDb.batchWriteItem(request);
                if (result != null) {
                    try {
                        List<ConsumedCapacity> consumedCapacity = result.getConsumedCapacity();
                        for (ConsumedCapacity cap : consumedCapacity) {
                            logger.info(cap.getCapacityUnits());
                        }
                    } catch (Exception e) {
                        // ignore this
                    }
                }
            } catch (AmazonServiceException e) {
                if (e instanceof ProvisionedThroughputExceededException) {
                    try {
                        DynamoDB db = new DynamoDB(dynamoDb);
                        Table table = db.getTable(tableName);
                        ProvisionedThroughputDescription oldThroughput = table.getDescription()
                                .getProvisionedThroughput();
                        logger.info("ProvisionedThroughputExceeded throughput = " + oldThroughput);
                        ProvisionedThroughput newThroughput = new ProvisionedThroughput()
                                .withReadCapacityUnits(
                                        table.getDescription().getProvisionedThroughput().getReadCapacityUnits())
                                .withWriteCapacityUnits(
                                        getIncreasedThroughput(table.getDescription().getProvisionedThroughput()
                                                .getReadCapacityUnits()));
                        if (!oldThroughput.equals(newThroughput)) {
                            logger.info("Updating throughput to " + newThroughput);
                            table.updateTable(newThroughput);
                            table.waitForActive();
                        }
                    } catch (Exception e1) {
                        logger.error("Error increasing capacity: " + e, e);
                    }
                }
                int status = e.getStatusCode();
                if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR
                        || status == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    shouldRetry = true;
                    long delay = (long) (Math.random() * (Math.pow(4, retries++) * 100L));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException iex) {
                        logger.error("Caught InterruptedException exception", iex);
                    }
                } else {
                    logger.error("Error writing to DB: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } while (shouldRetry && retries < MAX_NUMBER_OF_RETRIES);

    }

    private Long getIncreasedThroughput(Long readCapacityUnits) {
        long ret = readCapacityUnits * 2;
        if (ret > MAX_WRITE_UNITS) {
            ret = MAX_WRITE_UNITS;
        }
        return ret;
    }

    private void waitForStatus(String tableName, TableStatus status) {
        logger.info("Waiting for " + tableName + " to become " + status.toString() + "...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000 * 2);
            } catch (Exception e) {
            }
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                TableDescription tableDescription = dynamoDb.describeTable(request).getTable();
                String tableStatus = tableDescription.getTableStatus();
                logger.debug("  - current state: " + tableStatus);
                if (tableStatus.equals(status.toString()))
                    return;
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false)
                    throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never went " + status.toString());
    }

    private void waitForDelete(String tableName) {
        logger.info("Waiting for " + tableName + " to become deleted...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000 * 2);
            } catch (Exception e) {
            }
            try {
                if (!hasTable(tableName)) {
                    return;
                }
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false)
                    throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never deleted");
    }

    /**
     * @param item
     * @return
     */
    private Map<String, AttributeValue> itemToMap(Item item) {
        Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
        for (Attribute attr : item.getAttributes()) {
            addAttribute(attributes, attr.getName(), attr.getValue());
        }
        if (!attributes.containsKey(DatabaseKeys.JOB_ID_KEY.getShortKey())) {
            throw new RuntimeException("Item does not contain a job ID");
        } else if (!attributes.containsKey(DatabaseKeys.REQUEST_NAME_KEY.getShortKey())) {
            AttributeValue attVal = attributes.get(DatabaseKeys.TIMESTAMP_KEY.getShortKey());
            String timestamp = attVal != null ? attVal.getS() : ReportUtil.getTimestamp(new Date());
            if (attVal == null) {
                addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), timestamp);
            }
            addAttribute(attributes, DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), timestamp + "-"
                    + UUID.randomUUID().toString());
        }

        return attributes;
    }

    /**
     * @param tableName
     * @param requests
     */
    private void sendBatch(final String tableName, List<WriteRequest> requests) {
        int numBatches = (int) Math.ceil(requests.size() / (BATCH_SIZE * 1D));
        for (int i = 0; i < numBatches; i++) {
            Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();
            List<WriteRequest> batch = requests.subList(i * BATCH_SIZE,
                    Math.min(i * BATCH_SIZE + BATCH_SIZE, requests.size()));
            requestItems.put(tableName, batch);
            addItemsToTable(tableName, new BatchWriteItemRequest().withRequestItems(requestItems));
        }
    }
}
