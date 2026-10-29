package com.intuit.tank.vmManager.environment.amazon;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2AsyncClientBuilder;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Covers how {@link AmazonInstance#AmazonInstance(VMRegion)} builds and caches its Ec2AsyncClient.
 * Each test uses its own region so the static client cache never serves a client from another test.
 */
public class AmazonInstanceClientTest {

    private final Ec2AsyncClient client = mock(Ec2AsyncClient.class);
    private final Ec2AsyncClientBuilder builder = mock(Ec2AsyncClientBuilder.class, RETURNS_SELF);
    private final CloudCredentials creds = mock(CloudCredentials.class);

    @AfterEach
    public void clearClientCache() throws Exception {
        // Drop the mocked clients so other tests building a real AmazonInstance for these regions get a real one
        Field cache = AmazonInstance.class.getDeclaredField("CLIENT_CACHE");
        cache.setAccessible(true);
        Map<?, ?> clients = (Map<?, ?>) cache.get(null);
        clients.values().removeIf(cached -> cached == client);
    }

    @Test
    public void constructor_withProxyAndKeys_configuresBuilderAndCachesClient() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");
        when(creds.getProxyPort()).thenReturn("8080");
        when(creds.getKeyId()).thenReturn("key-id");
        when(creds.getKey()).thenReturn("secret");
        when(builder.build()).thenReturn(client);

        try (MockedStatic<Ec2AsyncClient> ec2 = mockStatic(Ec2AsyncClient.class);
             MockedConstruction<TankConfig> config = mockTankConfig()) {
            ec2.when(Ec2AsyncClient::builder).thenReturn(builder);

            new AmazonInstance(VMRegion.EUROPE);
            new AmazonInstance(VMRegion.EUROPE);

            // the second instance reuses the cached client
            ec2.verify(Ec2AsyncClient::builder, times(1));
        }

        verify(builder).overrideConfiguration(any(ClientOverrideConfiguration.class));
        verify(builder).httpClientBuilder(any(SdkAsyncHttpClient.Builder.class));
        verify(builder).credentialsProvider(any(AwsCredentialsProvider.class));
        verify(builder).region(Region.of(VMRegion.EUROPE.getRegion()));
    }

    @Test
    public void constructor_withProxyHostOnly_skipsPortAndCredentials() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");
        when(builder.build()).thenReturn(client);

        try (MockedStatic<Ec2AsyncClient> ec2 = mockStatic(Ec2AsyncClient.class);
             MockedConstruction<TankConfig> config = mockTankConfig()) {
            ec2.when(Ec2AsyncClient::builder).thenReturn(builder);

            new AmazonInstance(VMRegion.ASIA_2);
        }

        verify(builder).httpClientBuilder(any(SdkAsyncHttpClient.Builder.class));
        verify(builder, never()).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    @Test
    public void constructor_withInvalidProxyPort_skipsProxy() {
        when(creds.getProxyHost()).thenReturn("proxy.example.com");
        when(creds.getProxyPort()).thenReturn("not-a-port");
        when(builder.build()).thenReturn(client);

        try (MockedStatic<Ec2AsyncClient> ec2 = mockStatic(Ec2AsyncClient.class);
             MockedConstruction<TankConfig> config = mockTankConfig()) {
            ec2.when(Ec2AsyncClient::builder).thenReturn(builder);

            new AmazonInstance(VMRegion.ASIA_1);
        }

        verify(builder, never()).httpClientBuilder(any(SdkAsyncHttpClient.Builder.class));
        verify(builder, never()).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    @Test
    public void constructor_buildFailureIsWrapped() {
        IllegalStateException failure = new IllegalStateException("no region");
        when(builder.build()).thenThrow(failure);

        try (MockedStatic<Ec2AsyncClient> ec2 = mockStatic(Ec2AsyncClient.class);
             MockedConstruction<TankConfig> config = mockTankConfig()) {
            ec2.when(Ec2AsyncClient::builder).thenReturn(builder);

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> new AmazonInstance(VMRegion.SOUTH_AMERICA));
            assertSame(failure, thrown.getCause());
        }
    }

    @Test
    public void constructor_withoutCredentials_usesDefaultProviderChain() {
        when(builder.build()).thenReturn(client);

        try (MockedStatic<Ec2AsyncClient> ec2 = mockStatic(Ec2AsyncClient.class);
             MockedConstruction<TankConfig> config = mockTankConfig(null)) {
            ec2.when(Ec2AsyncClient::builder).thenReturn(builder);

            new AmazonInstance(VMRegion.ASIA_3);
        }

        verify(builder, never()).httpClientBuilder(any(SdkAsyncHttpClient.Builder.class));
        verify(builder, never()).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    private MockedConstruction<TankConfig> mockTankConfig() {
        return mockTankConfig(creds);
    }

    private static MockedConstruction<TankConfig> mockTankConfig(CloudCredentials credentials) {
        VmManagerConfig vmManagerConfig = mock(VmManagerConfig.class);
        when(vmManagerConfig.getCloudCredentials(CloudProvider.amazon)).thenReturn(credentials);
        return mockConstruction(TankConfig.class,
                (mock, context) -> when(mock.getVmManagerConfig()).thenReturn(vmManagerConfig));
    }
}
