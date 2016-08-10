package com.intuit.tank.persistence.databases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;

public class AmazonSimpleDatabase implements IDatabase {
    private static final Logger logger = LogManager.getLogger(AmazonSimpleDatabase.class);
    private static final int MAX_NUMBER_OF_RETRIES = 5;
    private AmazonSimpleDB db;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static TankConfig config = new TankConfig();

    /**
     * 
     * @param db
     */
    public AmazonSimpleDatabase(AmazonSimpleDB db) {
        this.db = db;
    }

    /**
     * 
     */
    public AmazonSimpleDatabase() {
        createDatabase();
    }

    /**
     * 
     * @{inheritDoc
     */
    public void createTable(String tableName) {
        try {
            if (!hasTable(tableName)) {
                logger.info("Creating table: " + tableName);
                db.createDomain(new CreateDomainRequest(tableName));
            }
        } catch (Exception t) {
            logger.error(t, t);
        }
    }

    public void deleteTable(String tableName) {
        if (hasTable(tableName)) {
            logger.info("Deleting table: " + tableName);
            db.deleteDomain(new DeleteDomainRequest(tableName));
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void deleteForJob(final String tableName, String jobId, boolean asynch) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                deleteTable(tableName);

            }
        };
        if (asynch) {
            EXECUTOR.execute(task);
        } else {
            task.run();
        }

    }

    /**
     * @{inheritDoc
     */
    public Set<String> getTables(String regex) {
        Set<String> result = new HashSet<String>();
        ListDomainsResult listDomains = null;
        String nextToken = null;
        do {
            listDomains = db.listDomains(new ListDomainsRequest().withNextToken(nextToken));
            for (String s : listDomains.getDomainNames()) {
                if (s.matches(regex)) {
                    result.add(s);
                }
            }
            nextToken = listDomains.getNextToken();
        } while (nextToken != null);
        return result;
    }

    /**
     * 
     * @{inheritDoc
     */
    public void addTimingResults(final @Nonnull String tableName, final @Nonnull List<TankResult> messages, boolean asynch) {
        if (!messages.isEmpty()) {
            Runnable task = new Runnable() {
                public void run() {
                    List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
                    try {
                        for (TankResult result : messages) {
                            ReplaceableItem item = new ReplaceableItem();
                            item.setAttributes(getTimingAttributes(result));
                            item.setName(UUID.randomUUID().toString());
                            items.add(item);
                            if (items.size() == 25) {
                                addItemsToTable(new BatchPutAttributesRequest(tableName, new ArrayList<ReplaceableItem>(items)));
                                // logger.info("Sending " + items.size() + "
                                // results to table " + tableName);
                                items.clear();
                            }
                        }
                        if (items.size() > 0) {
                            addItemsToTable(new BatchPutAttributesRequest(tableName, items));
                            logger.info("Sending " + items.size() + " results to table " + tableName);
                        }
                    } catch (Exception t) {
                        logger.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
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
    public void addItems(final String tableName, final List<Item> items, boolean asynch) {
        Runnable task = new Runnable() {
            public void run() {
                List<ReplaceableItem> tmpItems = new ArrayList<ReplaceableItem>();
                for (Item item : items) {
                    tmpItems.add(itemToAWSItem(item));
                    if (tmpItems.size() == 25) {
                        addItemsToTable(new BatchPutAttributesRequest(tableName, tmpItems));
                        tmpItems.clear();
                    }
                }
                addItemsToTable(new BatchPutAttributesRequest(tableName, tmpItems));
            }
        };
        if (asynch) {
            EXECUTOR.execute(task);
        } else {
            task.run();
        }

    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean hasJobData(String tableName, String jobId) {
        boolean ret = false;
        if (hasTable(tableName)) {
            SelectRequest request = new SelectRequest("SELECT * from `" + tableName + "`");
            SelectResult result = db.select(request);
            ret = !result.getItems().isEmpty();
        }
        return ret;
    }

    /**
     * 
     * @{inheritDoc
     */
    public String getDatabaseName(TankDatabaseType type, String jobId) {
        return type.name() + "_" + new TankConfig().getInstanceName() + "_" + jobId;
    }

    /**
     * 
     * @param tableName
     * @param items
     */
    public void addItems(String tableName, List<ReplaceableItem> items) {
        try {
            List<ReplaceableItem> tmpItems = new ArrayList<ReplaceableItem>();
            for (ReplaceableItem item : items) {
                tmpItems.add(item);
                if (tmpItems.size() == 25) {
                    addItemsToTable(new BatchPutAttributesRequest(tableName, tmpItems));
                    tmpItems.clear();
                }
            }
            addItemsToTable(new BatchPutAttributesRequest(tableName, tmpItems));
        } catch (Exception t) {
            logger.error("Error adding result: " + t, t);
        }
    }

    /**
     * 
     * @{inheritDoc
     */
    public boolean hasTable(@Nonnull String tableName) {
        boolean hasMore = true;
        String nextToken = null;
        while (hasMore) {
            ListDomainsResult listDomains = db.listDomains(new ListDomainsRequest().withNextToken(nextToken));
            for (String name : listDomains.getDomainNames()) {
                if (tableName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            nextToken = listDomains.getNextToken();
            hasMore = !StringUtils.isEmpty(nextToken);
        }
        return false;
    }

    /**
     * 
     * @param tableName
     * @return
     */
    public List<String> filterExisting(List<String> tableName) {
        boolean hasMore = true;
        String nextToken = null;

        List<String> ret = new ArrayList<String>(tableName.size());
        Set<String> tables = new HashSet<String>();
        while (hasMore) {
            ListDomainsResult listDomains = db.listDomains(new ListDomainsRequest().withNextToken(nextToken));
            tables.addAll(listDomains.getDomainNames());
            nextToken = listDomains.getNextToken();
            hasMore = !StringUtils.isEmpty(nextToken);
        }
        for (String name : tableName) {
            if (tables.contains(name)) {
                ret.add(name);
            }
        }
        return ret;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public PagedDatabaseResult getPagedItems(String tableName, Object token, String minRange, String maxRange, String instanceId, String jobId) {
        List<Item> ret = new ArrayList<Item>();
        String whereClause = null;
        if (minRange != null && maxRange != null) {
            whereClause = " Timestamp between '" + minRange + "' and '" + maxRange + "' ";
        } else if (minRange != null) {
            whereClause = " Timestamp >= '" + minRange + "' ";
        } else if (maxRange != null) {
            whereClause = " Timestamp < '" + maxRange + "' ";
        } else {
            whereClause = "";
        }
        SelectRequest request = new SelectRequest("SELECT * from `" + tableName + "`" + whereClause).withConsistentRead(true);
        String nextToken = (String) token;
        request.withNextToken(nextToken);
        SelectResult result = db.select(request);
        for (com.amazonaws.services.simpledb.model.Item item : result.getItems()) {
            ret.add(resultToItem(item));
        }
        nextToken = result.getNextToken();
        return new PagedDatabaseResult(ret, result.getNextToken());
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId, String... jobIds) {
        List<Item> ret = new ArrayList<Item>();
        for (String jobId : jobIds) {
            String nextToken = null;
            do {
                PagedDatabaseResult pagedItems = getPagedItems(tableName, nextToken, minRange, maxRange, instanceId, jobId);
                ret.addAll(pagedItems.getItems());
                nextToken = (String) pagedItems.getNextToken();
            } while (nextToken != null);
        }
        return ret;
    }

    /**
     * @param item
     * @return
     */
    private ReplaceableItem itemToAWSItem(Item item) {
        List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
        for (com.intuit.tank.reporting.databases.Attribute attr : item.getAttributes()) {
            addAttribute(attributes, attr.getName(), attr.getValue());
        }
        ReplaceableItem ret = new ReplaceableItem(item.getName(), attributes);
        return ret;
    }

    /**
     * @param item
     * @return
     */
    private com.intuit.tank.reporting.databases.Item resultToItem(com.amazonaws.services.simpledb.model.Item item) {
        List<com.intuit.tank.reporting.databases.Attribute> attrs = new ArrayList<com.intuit.tank.reporting.databases.Attribute>();
        com.intuit.tank.reporting.databases.Item ret = new com.intuit.tank.reporting.databases.Item(item.getName(), attrs);
        for (Attribute attr : item.getAttributes()) {
            attrs.add(new com.intuit.tank.reporting.databases.Attribute(attr.getName(), attr.getValue()));
        }
        return ret;
    }

    private void addItemsToTable(final BatchPutAttributesRequest request) {

        boolean shouldRetry;
        int retries = 0;

        do {
            shouldRetry = false;
            try {
                db.batchPutAttributes(request);
            } catch (AmazonServiceException e) {
                int status = e.getStatusCode();
                if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR || status == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    shouldRetry = true;
                    long delay = (long) (Math.random() * (Math.pow(4, retries++) * 100L));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException iex) {
                        logger.error("Caught InterruptedException exception", iex);
                    }
                } else if ("DuplicateItemName".equals(e.getErrorCode())) {
                    // ignore.
                } else {
                    logger.error("Error writing to DB: " + e.getMessage());
                }
            }
        } while (shouldRetry && retries < MAX_NUMBER_OF_RETRIES);

    }

    private List<ReplaceableAttribute> getTimingAttributes(TankResult result) {
        List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
        String timestamp = ReportUtil.getTimestamp(result.getTimeStamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), timestamp);
        addAttribute(attributes, DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), timestamp + "-" + UUID.randomUUID().toString());
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), result.getJobId());
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), result.getRequestName());
        addAttribute(attributes, DatabaseKeys.STATUS_CODE_KEY.getShortKey(), String.valueOf(result.getStatusCode()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_TIME_KEY.getShortKey(), String.valueOf(result.getResponseTime()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_SIZE_KEY.getShortKey(), String.valueOf(result.getResponseSize()));
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), String.valueOf(result.getInstanceId()));
        addAttribute(attributes, DatabaseKeys.IS_ERROR_KEY.getShortKey(), String.valueOf(result.isError()));
        return attributes;
    }

    private void addAttribute(List<ReplaceableAttribute> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.add(new ReplaceableAttribute().withName(key).withValue(value));
    }

    private void createDatabase() {
        CloudCredentials creds = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
        ClientConfiguration config = new ClientConfiguration();
        if (StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
            try {
                config.setProxyHost(System.getProperty("http.proxyHost"));
                if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                    config.setProxyPort(Integer.valueOf(System.getProperty("http.proxyPort")));
                }
            } catch (NumberFormatException e) {
                logger.error("invalid proxy setup.");
            }

        }
        if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
            AWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
            this.db = new AmazonSimpleDBClient(credentials, config);
        } else {
            this.db = new AmazonSimpleDBClient(config);
        }
    }

}
