package com.intuit.tank.vmManager.environment.amazon;

import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AmazonS3Test {

    private final S3Client s3Client = mock(S3Client.class);
    private final S3ClientBuilder builder = mock(S3ClientBuilder.class, RETURNS_SELF);
    private final CloudCredentials creds = mock(CloudCredentials.class);

    private MockedStatic<S3Client> s3Static;
    private MockedConstruction<TankConfig> tankConfig;
    private final String savedProxyHost = System.getProperty("http.proxyHost");
    private final String savedProxyPort = System.getProperty("http.proxyPort");

    @BeforeEach
    public void setUp() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        when(builder.build()).thenReturn(s3Client);
        s3Static = mockStatic(S3Client.class);
        s3Static.when(S3Client::builder).thenReturn(builder);
        VmManagerConfig vmManagerConfig = mock(VmManagerConfig.class);
        when(vmManagerConfig.getCloudCredentials(CloudProvider.amazon)).thenReturn(creds);
        tankConfig = mockConstruction(TankConfig.class,
                (mock, context) -> when(mock.getVmManagerConfig()).thenReturn(vmManagerConfig));
    }

    @AfterEach
    public void tearDown() {
        tankConfig.close();
        s3Static.close();
        restoreProperty("http.proxyHost", savedProxyHost);
        restoreProperty("http.proxyPort", savedProxyPort);
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    @Test
    public void constructor_withoutProxyOrKeys_usesDefaults() {
        new AmazonS3();

        verify(builder).httpClientBuilder(any(SdkHttpClient.Builder.class));
        verify(builder, never()).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    @Test
    public void constructor_withProxyAndKeys_setsCredentials() {
        System.setProperty("http.proxyHost", "proxy.example.com");
        System.setProperty("http.proxyPort", "8080");
        when(creds.getKeyId()).thenReturn("key-id");
        when(creds.getKey()).thenReturn("secret");

        new AmazonS3();

        verify(builder).credentialsProvider(any(AwsCredentialsProvider.class));
    }

    @Test
    public void constructor_withProxyHostOnlyAndInvalidPort_stillBuilds() {
        System.setProperty("http.proxyHost", "proxy.example.com");
        new AmazonS3();

        System.setProperty("http.proxyPort", "not-a-port");
        new AmazonS3();

        verify(builder, times(2)).build();
    }

    @Test
    public void constructor_buildFailureIsWrapped() {
        IllegalStateException failure = new IllegalStateException("no region");
        when(builder.build()).thenThrow(failure);

        RuntimeException thrown = assertThrows(RuntimeException.class, AmazonS3::new);
        assertSame(failure, thrown.getCause());
    }

    @Test
    public void createBucket_createsNamedBucket() {
        new AmazonS3().createBucket("tank-results");

        ArgumentCaptor<CreateBucketRequest> captor = ArgumentCaptor.forClass(CreateBucketRequest.class);
        verify(s3Client).createBucket(captor.capture());
        assertEquals("tank-results", captor.getValue().bucket());
    }

    @Test
    public void createBucket_s3ExceptionIsOnlyLogged() {
        when(s3Client.createBucket(any(CreateBucketRequest.class)))
                .thenThrow(S3Exception.builder().message("bucket exists").build());

        new AmazonS3().createBucket("tank-results");

        verify(s3Client).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    public void storeFile_putsStreamWithMetadata() {
        byte[] content = "agent results".getBytes();

        new AmazonS3().storeFile("tank-results", "job/1.zip", Map.of("jobId", "1"), new ByteArrayInputStream(content));

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> body = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(request.capture(), body.capture());
        assertEquals("tank-results", request.getValue().bucket());
        assertEquals("job/1.zip", request.getValue().key());
        assertEquals(Map.of("jobId", "1"), request.getValue().metadata());
        assertEquals(content.length, body.getValue().optionalContentLength().orElseThrow());
    }

    @Test
    public void storeFile_unreadableStreamIsOnlyLogged() throws IOException {
        InputStream in = mock(InputStream.class);
        when(in.available()).thenThrow(new IOException("closed"));

        new AmazonS3().storeFile("tank-results", "job/1.zip", Map.of(), in);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getStream_returnsObjectStream() {
        ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        assertSame(stream, new AmazonS3().getStream("tank-results", "job/1.zip"));

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        assertEquals("tank-results", captor.getValue().bucket());
        assertEquals("job/1.zip", captor.getValue().key());
    }

    @Test
    public void getFileMetaData_returnsObjectMetadata() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().metadata(Map.of("jobId", "1")).build());

        assertEquals(Map.of("jobId", "1"), new AmazonS3().getFileMetaData("tank-results", "job/1.zip"));
    }

    @Test
    public void getFileMetaData_withoutMetadataReturnsEmpty() {
        HeadObjectResponse response = mock(HeadObjectResponse.class);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        assertTrue(new AmazonS3().getFileMetaData("tank-results", "job/1.zip").isEmpty());
    }

    @Test
    public void getFileMetaData_failureIsWrapped() {
        S3Exception notFound = (S3Exception) S3Exception.builder().statusCode(404).build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(notFound);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> new AmazonS3().getFileMetaData("tank-results", "job/1.zip"));
        assertSame(notFound, thrown.getCause());
    }
}
