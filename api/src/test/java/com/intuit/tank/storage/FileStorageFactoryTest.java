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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.test.TestGroups;

import org.testng.Assert;

public class FileStorageFactoryTest {
    
    @BeforeClass
    public void init() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    @Test(groups = TestGroups.FUNCTIONAL)
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
        Assert.assertEquals(s, fromFile);

        try (InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in, StandardCharsets.UTF_8);
            Assert.assertEquals(s, fromService);
        }

        Assert.assertTrue(storage.exists(fd));
        storage.delete(fd);
        Assert.assertTrue(!storage.exists(fd));
    }

    @Test(groups = TestGroups.FUNCTIONAL)
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
            Assert.assertEquals(s, fromFile);
        }

        try ( InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in);
            Assert.assertEquals(s, fromService);
        }
        Assert.assertTrue(storage.exists(fd));
        storage.delete(fd);
        Assert.assertTrue(!storage.exists(fd));
    }

    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test(groups = TestGroups.EXPERIMENTAL)
    public void testS3FileStorage() throws Exception {
        String s = "This is a test";

        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileStorage storage = FileStorageFactory.getFileStorage("s3:systemstorage", false);
        FileData fd = new FileData("", "test.txt");
        storage.storeFileData(fd, bis);

        try ( InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in, StandardCharsets.UTF_8);
            Assert.assertEquals(s, fromService);
        }
        Assert.assertTrue(storage.exists(fd));
        storage.delete(fd);
        Assert.assertTrue(!storage.exists(fd));
    }

    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test(groups = TestGroups.EXPERIMENTAL)
    public void testS3StorageCompressed() throws Exception {
        String s = "This is a test";
        ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
        FileStorage storage = FileStorageFactory.getFileStorage("s3:systemstorage", true);
        FileData fd = new FileData("", "test.gz");
        storage.storeFileData(fd, bis);

        try ( InputStream in = storage.readFileData(fd) ) {
            String fromService = IOUtils.toString(in, StandardCharsets.UTF_8);
            Assert.assertEquals(s, fromService);
        }
        Assert.assertTrue(storage.exists(fd));
        storage.delete(fd);
        Assert.assertTrue(!storage.exists(fd));
    }
    
    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test(groups = TestGroups.EXPERIMENTAL)
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
        Assert.assertTrue(listFileData.contains(fd));
        Assert.assertTrue(listFileData.contains(fd1));
        Assert.assertTrue(!listFileData.contains(fd2));
        
        Assert.assertTrue(!listFileData1.contains(fd));
        Assert.assertTrue(!listFileData1.contains(fd1));
        Assert.assertTrue(listFileData1.contains(fd2));
        
        Assert.assertTrue(!storage.exists(fd));
        Assert.assertTrue(!storage.exists(fd1));
        Assert.assertTrue(!storage.exists(fd2));
    }
    /**
     * set the enviroment variables AWS_SECRET_KEY_ID and AWS_SECRET_KEY before
     * running this test.
     * 
     * @throws Exception
     */
    @Test(groups = TestGroups.EXPERIMENTAL)
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
        Assert.assertTrue(listFileData.contains(fd));
        Assert.assertTrue(listFileData.contains(fd1));
        Assert.assertTrue(!listFileData.contains(fd2));
        
        Assert.assertTrue(!listFileData1.contains(fd));
        Assert.assertTrue(!listFileData1.contains(fd1));
        Assert.assertTrue(listFileData1.contains(fd2));
        
        Assert.assertTrue(!storage.exists(fd));
        Assert.assertTrue(!storage.exists(fd1));
        Assert.assertTrue(!storage.exists(fd2));
    }

    private File getFile(String base, FileData fd) {
        return new File(FilenameUtils.normalize(base + "/" + fd.getPath() + "/" + fd.getFileName()));
    }
}