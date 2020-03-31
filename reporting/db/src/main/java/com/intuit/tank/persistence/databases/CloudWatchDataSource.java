package com.intuit.tank.persistence.databases;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.StatisticSet;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * S3Datasource
 *
 * @author Kevin McGoldrick
 *
 */
public class CloudWatchDataSource implements IDatabase {

    private final AmazonCloudWatch cwClient = AmazonCloudWatchClientBuilder.defaultClient();

    @Override
    public void initNamespace(@Nonnull String tableName) {

    }

    @Override
    public void removeNamespace(@Nonnull String tableName) {

    }

    @Override
    public void deleteForJob(@Nonnull String tableName, @Nonnull String jobId, boolean asynch) {

    }

    @Override
    public boolean hasTable(@Nonnull String tableName) {
        return false;
    }

    @Override
    public boolean hasJobData(@Nonnull String tableName, String jobId) {
        return false;
    }

    @Override
    public void addTimingResults(@Nonnull String tableName, @Nonnull List<TankResult> results, boolean asynch) {
        List<MetricDatum> datumList = new ArrayList<>();

        Dimension instanceId = new Dimension()
                .withName("InstanceId")
                .withValue(results.get(0).getInstanceId());

        Dimension jobId = new Dimension()
                .withName("JobId")
                .withValue(results.get(0).getJobId());

        Map<String, List<Integer>> grouped = results.stream()
                .collect(groupingBy(TankResult::getRequestName,
                        Collectors.mapping(TankResult::getResponseTime, toList())));

        for (Map.Entry<String,List<Integer>> entry : grouped.entrySet()) {
            List<Integer> groupResults = entry.getValue();
            long sum = groupResults.stream().mapToInt(Integer::intValue).sum();
            int size = groupResults.size();
            Collections.sort(groupResults);
            Double[] sortedList = groupResults.toArray(new Double[0]);

            Dimension request = new Dimension()
                    .withName("RequestName")
                    .withValue(entry.getKey());

            datumList.add(new MetricDatum()
                    .withMetricName("ResponseTime")
                    .withUnit(StandardUnit.Milliseconds)
                    .withStatisticValues(new StatisticSet()
                            .withMaximum(sortedList[size - 1])
                            .withMinimum(sortedList[0])
                            .withSampleCount((double)size)
                            .withSum((double)sum))
                    .withValues(sortedList)
                    .withTimestamp(results.get(results.size()-1).getTimeStamp())
                    .withDimensions(request, instanceId, jobId));
        }
        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("Intuit/TANK")
                .withMetricData(datumList);

        cwClient.putMetricData(request);
    }

    @Override
    public Set<String> getTables(String regex) {
        return null;
    }

    @Override
    public void addItems(String tableName, List<Item> items, boolean asynch) {

    }

    @Nonnull
    @Override
    public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId, String... jobId) {
        return new ArrayList<>();
    }

    @Override
    public PagedDatabaseResult getPagedItems(String tableName, Object nextToken, String minRange, String maxRange, String instanceId, String jobId) {
        return null;
    }

    @Override
    public String getDatabaseName(TankDatabaseType type, String jobId) {
        return null;
    }
}