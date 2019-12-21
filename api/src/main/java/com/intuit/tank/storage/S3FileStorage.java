package com.intuit.tank.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * FileStorage that writes to the file system.
 * 
 * @author denisa
 *
 */
public class S3FileStorage implements FileStorage, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(S3FileStorage.class);

    private String bucketName;
    private String extraPath;
    private boolean compress = true;
    private boolean encrypt = true;

    private AmazonS3 s3Client;

    /**
     * @param bucketName
     * @param compress
     */
    public S3FileStorage(String bucketName, boolean compress) {
        super();
        parseBucketName(bucketName);
        this.compress = compress;
        try {
            TankConfig tankConfig = new TankConfig();
            encrypt = tankConfig.isS3EncryptionEnabled();
            CloudCredentials creds = tankConfig.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            ClientConfiguration config = new ClientConfiguration();
            if (StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
                try {
                    config.setProxyHost(System.getProperty("http.proxyHost"));
                    if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                        config.setProxyPort(Integer.parseInt(System.getProperty("http.proxyPort")));
                    }
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }

            }
            assert creds != null;
            if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                BasicAWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
                this.s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(config)
                        .build();
            } else {
                this.s3Client = AmazonS3ClientBuilder.standard()
                        .withClientConfiguration(config)
                        .build();
            }
            createBucket(bucketName);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private void parseBucketName(String name) {
        if (name.indexOf('/') == -1) {
            bucketName = name;
            extraPath = "";
        } else {
            bucketName = name.substring(0, name.indexOf('/'));
            extraPath = name.substring(name.indexOf('/'));
        }
        extraPath = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + "/"));
    }

    @Override
    public void storeFileData(FileData fileData, InputStream in) {
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        try {
            ObjectMetadata metaData = new ObjectMetadata();
            if (encrypt) {
                metaData.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);     
            }
            if (fileData.getAttributes() != null) {
                for (Entry<String, String> entry : fileData.getAttributes().entrySet()) {
                    metaData.addUserMetadata(entry.getKey(), entry.getValue());
                }
            }
            if (compress) {
                in = new GZIPInputStream(in);
            }
            s3Client.putObject(bucketName, path, in, metaData);
        } catch (Exception e) {
            LOG.error("Error storing file: " + e, e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in, null);
        }
    }

    @Override
    public InputStream readFileData(FileData fileData) {
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        InputStream ret = null;
        try {
            S3Object object = s3Client.getObject(bucketName, path);
            if (object != null) {
                ret = object.getObjectContent();
                if (compress) {
                    ret = new GZIPInputStream(ret);
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting File: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    private void createBucket(String bucketName) {
        AccessControlList configuration = null;
        try {
            configuration = s3Client.getBucketAcl(bucketName);
        } catch (Exception e) {
            LOG.info("Bucket " + bucketName + " does not exist.");
        }
        if (configuration == null) {
            Bucket bucket = s3Client.createBucket(bucketName);
            LOG.info("Created bucket " + bucket.getName() + " at " + bucket.getCreationDate());
        }
    }

    @Override
    public boolean exists(FileData fileData) {
        boolean ret = true;
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        try {
            s3Client.getObjectMetadata(bucketName, path);
        } catch (AmazonS3Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public List<FileData> listFileData(String path) {
        List<FileData> ret = new ArrayList<FileData>();
        try {
            String prefix = extraPath + path;
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            prefix = StringUtils.removeStart(prefix, "/");
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
            listObjectsRequest.setDelimiter("/");
            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    String fileName = FilenameUtils.getName(FilenameUtils.normalize(objectSummary.getKey()));
                    if (StringUtils.isNotBlank(fileName)) {
                        ret.add(new FileData(path, fileName));
                    }
                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());
            } while (objectListing.isTruncated());
        } catch (AmazonS3Exception e) {
            LOG.error("Error Listing Files: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public boolean delete(FileData fileData) {
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        try {
            s3Client.deleteObject(bucketName, path);
        } catch (AmazonS3Exception e) {
            return false;
        }
        return true;
    }
}
