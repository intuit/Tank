package com.intuit.tank.storage;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class S3FileStoragePutObjectTest {

    @Test
    public void testPutObject_sendsTheStreamsRealLengthNotAvailable() throws Exception {
        byte[] content = "row1,value1\nrow2,value2\n".getBytes(StandardCharsets.UTF_8);
        InputStream misleading = new FilterInputStream(new ByteArrayInputStream(content)) {
            @Override
            public int available() {
                return 527;
            }
        };

        assertUploaded(content, misleading);
    }

    @Test
    public void testPutObject_uploadsAZipEntry() throws Exception {
        byte[] content = "a,b\n1,2\n".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream zipped = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipped)) {
            zip.putNextEntry(new ZipEntry("data.csv"));
            zip.write(content);
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("readme.md"));
            zip.write("skip me".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        ZipInputStream entries = new ZipInputStream(new ByteArrayInputStream(zipped.toByteArray()));
        entries.getNextEntry();

        assertUploaded(content, entries);
    }

    private static void assertUploaded(byte[] expected, InputStream in) throws Exception {
        S3Client s3Client = mock(S3Client.class);
        AtomicReference<Long> length = new AtomicReference<>();
        AtomicReference<byte[]> body = new AtomicReference<>();
        doAnswer(invocation -> {
            RequestBody requestBody = invocation.getArgument(1);
            length.set(requestBody.optionalContentLength().orElse(-1L));
            try (InputStream sent = requestBody.contentStreamProvider().newStream()) {
                body.set(sent.readAllBytes());
            }
            return null;
        }).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        S3FileStorage.putObject(s3Client, PutObjectRequest.builder().bucket("bucket").key("key").build(), in);

        assertEquals(expected.length, length.get());
        assertArrayEquals(expected, body.get());
    }
}
