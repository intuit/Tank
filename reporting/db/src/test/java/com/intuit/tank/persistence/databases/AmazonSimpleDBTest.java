package com.intuit.tank.persistence.databases;

import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.api.enumerated.VMRegion;

public class AmazonSimpleDBTest {

    private AmazonSimpleDB simpeDB;
    private AmazonSimpleDatabase db;
    
    @BeforeClass
    public void before() {
        AWSCredentials credentials = new BasicAWSCredentials(System.getProperty("AWS_SECRET_KEY_ID"),
                System.getProperty("AWS_SECRET_KEY"));
        simpeDB = new AmazonSimpleDBClient(credentials, new ClientConfiguration());
        simpeDB.setEndpoint(VMRegion.US_WEST_2.getEndpoint());
    }
    
    @Test(groups=TestGroups.EXPERIMENTAL)
    public void testSimpleDB() {
        Set<String> tables = db.getTables("job");
        System.out.println(tables);
    }
}
