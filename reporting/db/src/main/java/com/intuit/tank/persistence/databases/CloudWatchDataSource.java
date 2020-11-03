package com.intuit.tank.persistence.databases;

import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatch.model.StatisticSet;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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

    private CloudWatchClient cloudWatchClient;
    private static final int MAX_CLOUDWATCH_METRICS_SUPPORTED = 150;
    private static final String namespace = "Intuit/Tank";

    public CloudWatchDataSource() {
        CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
        if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
            AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
            this.cloudWatchClient = CloudWatchClient.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
        } else {
            this.cloudWatchClient = CloudWatchClient.builder().build();
        }
    }

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
        Instant timestamp = results.get(results.size()-1).getTimeStamp().toInstant();
        List<MetricDatum> datumList = new ArrayList<>();

        Dimension instanceId = Dimension.builder()
                .name("InstanceId")
                .value(results.get(0).getInstanceId())
                .build();

        Dimension jobId = Dimension.builder()
                .name("JobId")
                .value(results.get(0).getJobId())
                .build();
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
                Collection<Double> sortedList = doubleStream.get()
                        .boxed().limit(MAX_CLOUDWATCH_METRICS_SUPPORTED).collect(Collectors.toList());
                double[] sortedArray = doubleStream.get().toArray();
                double sum = doubleStream.get().sum();

                Dimension request = Dimension.builder()
                        .name("RequestName")
                        .value(entry.getKey())
                        .build();

                datumList.add(MetricDatum.builder()
                        .metricName("ResponseTime")
                        .unit(StandardUnit.MILLISECONDS)
                        .statisticValues(StatisticSet.builder()
                                .maximum(sortedArray[size - 1])
                                .minimum(sortedArray[0])
                                .sampleCount((double) size)
                                .sum(sum)
                                .build())
                        .values(sortedList)
                        .timestamp(timestamp)
                        .dimensions(request, instanceId, jobId)
                        .build());

                datumList.add(MetricDatum.builder()
                        .metricName("RequestCount")
                        .unit(StandardUnit.COUNT)
                        .value((double) size)
                        .timestamp(timestamp)
                        .dimensions(request, instanceId, jobId)
                        .build());
            }
            LOG.trace("Sending to CloudWatchMetrics: " + datumList.size() + " to " + namespace);
            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(namespace)
                    .metricData(datumList)
                    .build();

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
