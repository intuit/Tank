package com.intuit.tank.persistence.databases;

import java.util.Set;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class AmazonSimpleDBTest {

    private AmazonSimpleDB simpeDB;
    private AmazonSimpleDatabase db;
    
    @BeforeEach
    public void before() {
        AWSCredentials credentials = new BasicAWSCredentials(System.getProperty("AWS_SECRET_KEY_ID"),
                System.getProperty("AWS_SECRET_KEY"));
        simpeDB = new AmazonSimpleDBClient(credentials, new ClientConfiguration());
        simpeDB.setEndpoint(VMRegion.US_WEST_2.getEndpoint());
    }

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testSimpleDB() {
        Set<String> tables = db.getTables("job");
        System.out.println(tables);
    }
}
