package com.intuit.tank.persistence.databases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;

import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.results.TankResultBuilder;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.common.util.ReportUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmazonDynamoDatabaseTest {

    private static final String TEST_JOB_ID = "TestJob1";
    private static final String TEST_TABLE = "TestTable";
    private static final int NUM_ENTRIES = 100;
    private AmazonDynamoDatabaseDocApi db;
    private DynamoDbClient dynamoDbClient;
    
    @BeforeEach
    public void init() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    @BeforeEach
    public void before() {
        AwsCredentials credentials = AwsBasicCredentials.create(System.getProperty("AWS_SECRET_KEY_ID"),
                System.getProperty("AWS_SECRET_KEY"));
        dynamoDbClient = DynamoDbClient.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
        db = new AmazonDynamoDatabaseDocApi(dynamoDbClient);
    }

//    @BeforeMethod
    public void cleanTables() {
        if (db != null) {
            try {
                db.removeNamespace(TEST_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testListTables() {
        ListTablesResponse listTables = dynamoDbClient.listTables();
        for (String s : listTables.tableNames()) {
            System.out.println(s);
        }

    }

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testCreateDelete() {
        db.initNamespace(TEST_TABLE);
        boolean hasTable = db.hasTable(TEST_TABLE);
        assertEquals(true, hasTable);
        db.removeNamespace(TEST_TABLE);
    }

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testInsertTiming() {
        db.initNamespace(TEST_TABLE);
        boolean hasTable = db.hasTable(TEST_TABLE);
        
        assertEquals(true, hasTable);
        boolean hasJobData = db.hasJobData(TEST_TABLE, TEST_JOB_ID);
        assertEquals(false, hasJobData);
        List<TankResult> results = getResults(NUM_ENTRIES);
        db.addTimingResults(TEST_TABLE, results, false);
        List<Item> items = db.getItems(TEST_TABLE, TEST_JOB_ID, null, null, null);
        assertEquals(NUM_ENTRIES , items.size());
        printItems(items);
        hasJobData = db.hasJobData(TEST_TABLE, TEST_JOB_ID);
        assertEquals(true, hasJobData);
        String start = ReportUtil.getTimestamp(results.get(10).getTimeStamp());
        String end = ReportUtil.getTimestamp(results.get(90).getTimeStamp());
        items = db.getItems(TEST_TABLE, TEST_JOB_ID, start, null, null);
        assertEquals(NUM_ENTRIES - 10 , items.size());
        items = db.getItems(TEST_TABLE, TEST_JOB_ID, null, end, null);
        assertEquals(NUM_ENTRIES - 10 , items.size());
        items = db.getItems(TEST_TABLE, TEST_JOB_ID, start, end, null);
        assertEquals(NUM_ENTRIES - 20 , items.size());
    }

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testInsertTps() {
        db.initNamespace(TEST_TABLE);
        int numJobs = 3;
        boolean hasTable = db.hasTable(TEST_TABLE);
        assertEquals(true, hasTable);
        List<Item> results = getTpsItems(NUM_ENTRIES, numJobs);
        db.addItems(TEST_TABLE, results, false);
        List<Item> items = db.getItems(TEST_TABLE, null, null, null, null);
        assertEquals(numJobs * NUM_ENTRIES, items.size());
        for (int i = 0; i < numJobs; i++) {
            items = db.getItems(TEST_TABLE, Integer.toString(i), null, null, null);
            assertEquals(NUM_ENTRIES, items.size());
            printItems(items);
            db.deleteForJob(TEST_TABLE, Integer.toString(i), false);
            items = db.getItems(TEST_TABLE, Integer.toString(i), null, null, null);
            assertEquals(0, items.size());
        }
    }

    private void printItems(List<Item> items) {
        for (Item item : items) {
            System.out.println(item.getName());
            for (Attribute attr : item.getAttributes()) {
                System.out.println(attr.getName() + " = " + attr.getValue());
            }
        }
    }

    private List<Item> getTpsItems(int numItems, int numJobs) {
        List<Item> ret = new ArrayList<Item>();
        for (int jobId = 0; jobId < numJobs; jobId++) {
            for (int i = 0; i < numItems; i++) {
                Item item = new Item();
                String testJobId = Integer.toString(jobId);
                List<Attribute> attributes = new ArrayList<Attribute>();
                Date date = new Date();
                String ts = ReportUtil.getTimestamp(date);
                addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), ts);
                addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), testJobId);
                addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), "TestInstanceID");
                addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), "TestLoggingKey");
                addAttribute(attributes, DatabaseKeys.PERIOD_KEY.getShortKey(), "15");
                addAttribute(attributes, DatabaseKeys.TRANSACTIONS_KEY.getShortKey(), "10");
                item.setAttributes(attributes);
                String name = "TestInstanceID"
                        + "_" + testJobId
                        + "_" + "TestLoggingKey"
                        + "_" + date;
                item.setName(name);
                ret.add(item);
            }
        }
        return ret;
    }

    private void addAttribute(List<Attribute> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.add(new Attribute(key, value));
    }

    private List<TankResult> getResults(int numItems) {
        List<TankResult> ret = new ArrayList<TankResult>();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -numItems);
        for (int i = 0; i < numItems; i++) {
            c.add(Calendar.SECOND, 1);
            TankResultBuilder builder = new TankResultBuilder();
            builder.withJobId(TEST_JOB_ID);
            builder.withError(false);
            builder.withRequestName("TestRequest");
            builder.withResponseSize(1);
            builder.withResponseTime(2000);
            builder.withStatusCode(200);
            TankResult tankResult = builder.build();
            tankResult.setTimeStamp(c.getTime());
            ret.add(tankResult);
        }
        return ret;
    }

}
