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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * CloudWatchDataSource
 *
 * @author Kevin McGoldrick
 *
 */
public class CloudWatchDataSource implements IDatabase {
    private static final Logger LOG = LogManager.getLogger(CloudWatchDataSource.class);

    private final AmazonCloudWatch cloudWatchClient = AmazonCloudWatchClientBuilder.defaultClient();
    private static final String namespace = "Intuit/Tank";

    @Override
    public void initNamespace(@Nonnull String tableName) {}

    @Override
    public void removeNamespace(@Nonnull String tableName) {}

    @Override
    public void deleteForJob(@Nonnull String tableName, @Nonnull String jobId, boolean asynch) {}

    @Override
    public boolean hasTable(@Nonnull String tableName) {
        return true;
    }

    @Override
    public boolean hasJobData(@Nonnull String tableName, String jobId) {
        return true;
    }

    @Override
    public void addTimingResults(@Nonnull String tableName, @Nonnull List<TankResult> results, boolean asynch) {
        LOG.trace("Starting addTimingResults with " + results.size() + " items");
        Date timestamp = results.get(results.size()-1).getTimeStamp();
        List<MetricDatum> datumList = new ArrayList<>();

        Dimension instanceId = new Dimension()
                .withName("InstanceId")
                .withValue(results.get(0).getInstanceId());

        Dimension jobId = new Dimension()
                .withName("JobId")
                .withValue(results.get(0).getJobId());
        try {
            Map<String, List<Integer>> grouped = results.stream()
                    .collect(groupingBy(TankResult::getRequestName,
                            Collectors.mapping(TankResult::getResponseTime, toList())));
            LOG.trace("Sorted into " + grouped.size() + " request name groups");

            for (Map.Entry<String,List<Integer>> entry : grouped.entrySet()) {
                List<Integer> groupResults = entry.getValue();
                int size = groupResults.size();
                Supplier<DoubleStream> doubleStream =
                        () -> groupResults.stream()
                                .sorted()
                                .mapToDouble(Double::valueOf);
                Collection<Double> sortedList = doubleStream.get().boxed().collect(Collectors.toList());
                double[] sortedArray = doubleStream.get().toArray();
                double sum = doubleStream.get().sum();

                Dimension request = new Dimension()
                        .withName("RequestName")
                        .withValue(entry.getKey());

                datumList.add(new MetricDatum()
                        .withMetricName("ResponseTime")
                        .withUnit(StandardUnit.Milliseconds)
                        .withStatisticValues(new StatisticSet()
                                .withMaximum(sortedArray[size - 1])
                                .withMinimum(sortedArray[0])
                                .withSampleCount((double) size)
                                .withSum(sum))
                        .withValues(sortedList)
                        .withTimestamp(timestamp)
                        .withDimensions(request, instanceId, jobId));

                datumList.add(new MetricDatum()
                        .withMetricName("RequestCount")
                        .withUnit(StandardUnit.Count)
                        .withValue((double) size)
                        .withTimestamp(timestamp)
                        .withDimensions(request, instanceId, jobId));
            }
            LOG.trace("Sending to CloudWatchMetrics: " + datumList.size() + " to " + namespace);
            PutMetricDataRequest request = new PutMetricDataRequest()
                    .withNamespace(namespace)
                    .withMetricData(datumList);

            cloudWatchClient.putMetricData(request);
        } catch (Exception e) {
            LOG.error("Failed to push metric data to cloudwatch: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getTables(String regex) {
        return null;
    }

    @Override
    public void addItems(String tableName, List<Item> items, boolean asynch) {}

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
        return namespace;
    }
}
