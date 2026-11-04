package com.intuit.tank.vmManager.environment.amazon;

import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClientBuilder;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAlarmsRequest;
import software.amazon.awssdk.services.cloudwatch.model.DescribeAlarmsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricAlarm;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricAlarmRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sns.paginators.ListTopicsIterable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CloudwatchInstanceTest {

    private static final String EMAIL = "tank@example.com";
    private static final String ALARM_NAME = "Job_7_Alarm_Excessive_CPU_for_Email_tank_example_com";
    private static final String TOPIC_ARN = "arn:aws:sns:us-west-2:123:AgentEmailTopicFor_tank_example_com";
    private static final String PROXY_PORT_PROPERTY = "tank.test.cloudwatch.proxyPort";

    private final CloudWatchClient cloudWatchClient = mock(CloudWatchClient.class);
    private final CloudWatchClientBuilder cloudWatchClientBuilder = mock(CloudWatchClientBuilder.class, RETURNS_SELF);
    private final SnsClient snsClient = mock(SnsClient.class);
    private final SnsClientBuilder snsClientBuilder = mock(SnsClientBuilder.class, RETURNS_SELF);
    private final CloudCredentials creds = mock(CloudCredentials.class);
    private final VmManagerConfig vmManagerConfig = mock(VmManagerConfig.class);

    private MockedStatic<CloudWatchClient> cloudWatchStatic;
    private MockedStatic<SnsClient> snsStatic;
    private MockedConstruction<TankConfig> tankConfig;

    @BeforeEach
    public void setUp() {
        when(cloudWatchClientBuilder.build()).thenReturn(cloudWatchClient);
        when(snsClientBuilder.build()).thenReturn(snsClient);
        when(vmManagerConfig.getCloudCredentials(CloudProvider.amazon)).thenReturn(creds);
        cloudWatchStatic = mockStatic(CloudWatchClient.class);
        cloudWatchStatic.when(CloudWatchClient::builder).thenReturn(cloudWatchClientBuilder);
        snsStatic = mockStatic(SnsClient.class);
        snsStatic.when(SnsClient::builder).thenReturn(snsClientBuilder);
        tankConfig = mockConstruction(TankConfig.class,
                (mock, context) -> when(mock.getVmManagerConfig()).thenReturn(vmManagerConfig));
    }

    @AfterEach
    public void tearDown() {
        closeMocks();
        System.clearProperty(PROXY_PORT_PROPERTY);
    }

    private void closeMocks() {
        if (!tankConfig.isClosed()) {
            tankConfig.close();
            snsStatic.close();
            cloudWatchStatic.close();
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCloudwatchInstance() {
        // Build real clients for this one
        closeMocks();

        CloudwatchInstance cloudwatchInstance = new CloudwatchInstance(VMRegion.STANDALONE);
        assertNotNull(cloudwatchInstance);
    }

    @Test
    public void constructor_nullRegion_usesDefaultRegion() {
        when(vmManagerConfig.getDefaultRegion()).thenReturn(VMRegion.US_EAST);

        new CloudwatchInstance(null);

        verify(cloudWatchClientBuilder).region(Region.US_EAST_1);
        verify(snsClientBuilder).region(Region.US_EAST_1);
        verify(cloudWatchClientBuilder, never()).httpClientBuilder(any(SdkHttpClient.Builder.class));
        verify(cloudWatchClientBuilder, never()).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    @Test
    public void constructor_withProxyAndKeys_configuresBothClients() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");
        // the port setting names a system property holding the port
        when(creds.getProxyPort()).thenReturn(PROXY_PORT_PROPERTY);
        System.setProperty(PROXY_PORT_PROPERTY, "8080");
        when(creds.getKeyId()).thenReturn("key-id");
        when(creds.getKey()).thenReturn("secret");

        new CloudwatchInstance(VMRegion.US_WEST_2);

        verify(cloudWatchClientBuilder).httpClientBuilder(any(SdkHttpClient.Builder.class));
        verify(snsClientBuilder).httpClientBuilder(any(SdkHttpClient.Builder.class));
        verify(cloudWatchClientBuilder).credentialsProvider(any(AwsCredentialsProvider.class));
        verify(snsClientBuilder).credentialsProvider(any(AwsCredentialsProvider.class));
        verify(cloudWatchClientBuilder).region(Region.US_WEST_2);
    }

    @Test
    public void constructor_withProxyHostOnly_configuresProxyWithoutPort() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");

        new CloudwatchInstance(VMRegion.US_WEST_2);

        verify(cloudWatchClientBuilder).httpClientBuilder(any(SdkHttpClient.Builder.class));
    }

    @Test
    public void constructor_withUnresolvableProxyPort_skipsProxy() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");
        when(creds.getProxyPort()).thenReturn("80");

        new CloudwatchInstance(VMRegion.US_WEST_2);

        verify(cloudWatchClientBuilder, never()).httpClientBuilder(any(SdkHttpClient.Builder.class));
        verify(snsClientBuilder, never()).httpClientBuilder(any(SdkHttpClient.Builder.class));
    }

    @Test
    public void constructor_buildFailureIsWrapped() {
        IllegalStateException failure = new IllegalStateException("no region");
        when(cloudWatchClientBuilder.build()).thenThrow(failure);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> new CloudwatchInstance(VMRegion.US_WEST_2));
        assertSame(failure, thrown.getCause());
    }

    @Test
    public void removeWatch_deletesOnlyMatchingAlarm() {
        stubAlarms("Some_Other_Alarm", ALARM_NAME.toLowerCase());

        new CloudwatchInstance(VMRegion.US_WEST_2).removeWatch(EMAIL, "7");

        ArgumentCaptor<DeleteAlarmsRequest> captor = ArgumentCaptor.forClass(DeleteAlarmsRequest.class);
        verify(cloudWatchClient).deleteAlarms(captor.capture());
        assertEquals(List.of(ALARM_NAME), captor.getValue().alarmNames());
    }

    @Test
    public void addWatch_existingAlarmIsLeftAlone() {
        stubAlarms(ALARM_NAME);

        new CloudwatchInstance(VMRegion.US_WEST_2).addWatch(List.of("i-1"), EMAIL, "7");

        verify(cloudWatchClient, never()).putMetricAlarm(any(PutMetricAlarmRequest.class));
        verifyNoInteractions(snsClient);
    }

    @Test
    public void addWatch_createsCpuAlarmNotifyingExistingTopic() {
        stubAlarms("Some_Other_Alarm");
        stubTopics("arn:aws:sns:us-west-2:123:Unrelated", TOPIC_ARN);

        new CloudwatchInstance(VMRegion.US_WEST_2).addWatch(List.of("i-1", "i-2"), EMAIL, "7");

        ArgumentCaptor<PutMetricAlarmRequest> captor = ArgumentCaptor.forClass(PutMetricAlarmRequest.class);
        verify(cloudWatchClient).putMetricAlarm(captor.capture());
        PutMetricAlarmRequest request = captor.getValue();
        assertEquals(ALARM_NAME, request.alarmName());
        assertEquals(List.of(TOPIC_ARN), request.alarmActions());
        assertEquals("CPUUtilization", request.metricName());
        assertEquals(List.of("i-1", "i-2"), request.dimensions().stream().map(Dimension::value).toList());
        verify(snsClient, never()).createTopic(any(CreateTopicRequest.class));
    }

    @Test
    public void getOrCreateNotification_createsTopicAndEmailSubscription() {
        stubTopics("arn:aws:sns:us-west-2:123:Unrelated");
        when(snsClient.createTopic(any(CreateTopicRequest.class)))
                .thenReturn(CreateTopicResponse.builder().topicArn(TOPIC_ARN).build());

        String arn = new CloudwatchInstance(VMRegion.US_WEST_2).getOrCreateNotification(EMAIL);

        assertEquals(TOPIC_ARN, arn);
        ArgumentCaptor<CreateTopicRequest> topic = ArgumentCaptor.forClass(CreateTopicRequest.class);
        verify(snsClient).createTopic(topic.capture());
        assertEquals("AgentEmailTopicFor_tank_example_com", topic.getValue().name());
        ArgumentCaptor<SubscribeRequest> subscribe = ArgumentCaptor.forClass(SubscribeRequest.class);
        verify(snsClient).subscribe(subscribe.capture());
        assertEquals(TOPIC_ARN, subscribe.getValue().topicArn());
        assertEquals(Map.of("email", EMAIL), subscribe.getValue().attributes());
    }

    private void stubAlarms(String... alarmNames) {
        when(cloudWatchClient.describeAlarms()).thenReturn(DescribeAlarmsResponse.builder()
                .metricAlarms(java.util.Arrays.stream(alarmNames)
                        .map(name -> MetricAlarm.builder().alarmName(name).build()).toList())
                .build());
    }

    private void stubTopics(String... topicArns) {
        List<Topic> topics = java.util.Arrays.stream(topicArns).map(arn -> Topic.builder().topicArn(arn).build()).toList();
        ListTopicsIterable iterable = mock(ListTopicsIterable.class);
        when(iterable.topics()).thenReturn(topics::iterator);
        when(snsClient.listTopicsPaginator()).thenReturn(iterable);
    }
}
