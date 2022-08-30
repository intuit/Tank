package com.intuit.tank.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileStorageFactoryTest {

    @Mock
    private MockedStatic<S3Client> mockStatic_S3Client;

    @Mock
    private S3Client mock_S3Client;

    @Mock
    private S3ClientBuilder mock_S3ClientBuilder;
    
    @BeforeEach
    public void init() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        mockStatic_S3Client = mockStatic(S3Client.class);
        mock_S3ClientBuilder = mock(S3ClientBuilder.class);
        mock_S3Client = mock(S3Client.class);
        when(S3Client.builder()).thenReturn(mock_S3ClientBuilder);
        when(mock_S3ClientBuilder.build()).thenReturn(mock_S3Client);

    }

    @AfterEach
    public void clearStaticMock() {
        mockStatic_S3Client.close();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFileStorage() throws Exception {
        String s = "This is a test";
        File f = new File("target/storage");
        if (f.exists() && f.isDirectory()) {
        	FileUtils.deleteDirectory(f);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileStorage storage = FileStorageFactory.getFileStorage(f.getAbsolutePath(), false);
        FileData fd = new FileData("", "test.txt");
        storage.storeFileData(fd, bis);

        File file = getFile(f.getAbsolutePath(), fd);
        String fromFile = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        assertEquals(s, fromFile);

        try (InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in, StandardCharsets.UTF_8);
            assertEquals(s, fromService);
        }

        assertTrue(storage.exists(fd));
        storage.delete(fd);
        assertFalse(storage.exists(fd));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFileStorageCompressed() throws Exception {
        String s = "This is a test";
        File f = new File("target/storage");
        if (f.exists() && f.isDirectory()) {
        	FileUtils.deleteDirectory(f);
        }
        FileStorage storage = FileStorageFactory.getFileStorage(f.getAbsolutePath(), true);
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileData fd = new FileData("", "test.gz");
        storage.storeFileData(fd, bis);

        File file = getFile(f.getAbsolutePath(), fd);
        try ( InputStream in = new GZIPInputStream(new FileInputStream(file)) ) {
            String fromFile = IOUtils.toString(in, StandardCharsets.UTF_8);
            assertEquals(s, fromFile);
        }

        try ( InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in,StandardCharsets.UTF_8);
            assertEquals(s, fromService);
        }
        assertTrue(storage.exists(fd));
        storage.delete(fd);
        assertFalse(storage.exists(fd));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testS3FileStorage() throws Exception {
        String s = "This is a test";

        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileStorage storage = FileStorageFactory.getFileStorage("s3:systemstorage/extra/", false);
        assertTrue(storage instanceof S3FileStorage);
        verify(mock_S3Client, times(1))
                .createBucket(CreateBucketRequest.builder().bucket("systemstorage").build());

        FileData fd = new FileData("", "test.txt");
        storage.storeFileData(fd, bis);
        verify(mock_S3Client, times(1))
                .putObject(PutObjectRequest.builder()
                        .bucket("systemstorage")
                        .key(any())
                        .build(), any(RequestBody.class));
        assertTrue(storage.exists(fd));

        storage.delete(fd);
        verify(mock_S3Client, times(1))
                .deleteObject(DeleteObjectRequest.builder().bucket("systemstorage").key(any()).build());
    }

    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testS3StorageCompressed() throws Exception {
        String s = "This is a test";
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileStorage storage = FileStorageFactory.getFileStorage("s3:systemstorage", true);
        FileData fd = new FileData("", "test.gz");
        storage.storeFileData(fd, bis);

        try ( InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in, StandardCharsets.UTF_8);
            assertEquals(s, fromService);
        }
        assertTrue(storage.exists(fd));
        storage.delete(fd);
        assertTrue(!storage.exists(fd));
    }
    
    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testS3List() throws Exception {
        String s = "This is a test";
        FileStorage storage = FileStorageFactory.getFileStorage("s3:systemstorage", false);

        FileData fd = new FileData("", "test1.txt");
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd, bis);
        
        FileData fd1 = new FileData("", "test2.txt");
        bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd1, bis);
        
        FileData fd2 = new FileData("/folder", "test3.txt");
        bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd2, bis);

        List<FileData> listFileData = storage.listFileData("");
        List<FileData> listFileData1 = storage.listFileData("/folder");
        
        storage.delete(fd);
        storage.delete(fd1);
        storage.delete(fd2);
        assertTrue(listFileData.contains(fd));
        assertTrue(listFileData.contains(fd1));
        assertTrue(!listFileData.contains(fd2));
        
        assertTrue(!listFileData1.contains(fd));
        assertTrue(!listFileData1.contains(fd1));
        assertTrue(listFileData1.contains(fd2));
        
        assertTrue(!storage.exists(fd));
        assertTrue(!storage.exists(fd1));
        assertTrue(!storage.exists(fd2));
    }
    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    public void testFileList() throws Exception {
        String s = "This is a test";
        File f = new File("target/storage");
    	FileUtils.deleteDirectory(f);
        FileStorage storage = FileStorageFactory.getFileStorage(f.getAbsolutePath(), true);
        
        FileData fd = new FileData("", "test1.txt");
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd, bis);
        
        FileData fd1 = new FileData("", "test2.txt");
        bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd1, bis);
        
        FileData fd2 = new FileData("/folder", "test3.txt");
        bis = new ByteArrayInputStream(s.getBytes());
        storage.storeFileData(fd2, bis);
        
        List<FileData> listFileData = storage.listFileData("");
        List<FileData> listFileData1 = storage.listFileData("/folder");
        
        storage.delete(fd);
        storage.delete(fd1);
        storage.delete(fd2);
        assertTrue(listFileData.contains(fd));
        assertTrue(listFileData.contains(fd1));
        assertTrue(!listFileData.contains(fd2));
        
        assertTrue(!listFileData1.contains(fd));
        assertTrue(!listFileData1.contains(fd1));
        assertTrue(listFileData1.contains(fd2));
        
        assertTrue(!storage.exists(fd));
        assertTrue(!storage.exists(fd1));
        assertTrue(!storage.exists(fd2));
    }

    private File getFile(String base, FileData fd) {
        return new File(FilenameUtils.normalize(base + "/" + fd.getPath() + "/" + fd.getFileName()));
    }
}