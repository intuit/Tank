package com.intuit.tank.storage;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

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

    private S3Client s3Client;

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
            this.encrypt = tankConfig.isS3EncryptionEnabled();
            CloudCredentials creds = tankConfig.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            S3ClientBuilder s3ClientBuilder = S3Client.builder();
            if (creds != null && StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
                try {
                    URIBuilder uriBuilder = new URIBuilder().setHost(System.getProperty("http.proxyHost"));
                    if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                        uriBuilder.setPort(Integer.parseInt(System.getProperty("http.proxyPort")));
                    }
                    ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                            .proxyConfiguration(
                                    ProxyConfiguration.builder().endpoint(uriBuilder.build()).build());
                    s3ClientBuilder.httpClientBuilder(httpClientBuilder);
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }
            }
            if (creds != null && StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                s3ClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
            }
            s3Client = s3ClientBuilder.build();
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
        String key = FilenameUtils.separatorsToUnix(
                FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        key = StringUtils.stripStart(key, "/");
        try {
            PutObjectRequest.Builder request = PutObjectRequest.builder().bucket(bucketName).key(key);
            if (encrypt) {
                request.serverSideEncryption(ServerSideEncryption.AES256);
            }
            if (fileData.getAttributes() != null) {
                request.metadata(fileData.getAttributes());
            }
            if (compress) {
                in = new GZIPInputStream(in);
            }
            s3Client.putObject(request.build(), RequestBody.fromInputStream(in, in.available()));
        } catch (Exception e) {
            LOG.error("Error storing file: " + e, e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public InputStream readFileData(FileData fileData) {
        String key = FilenameUtils.separatorsToUnix(
                FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        key = StringUtils.stripStart(key, "/");
        return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    private void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            LOG.info("Created bucket " + bucketName + " at " + "now");
        } catch (BucketAlreadyExistsException baee) {//Good
        } catch (S3Exception e) {
            LOG.error("Error creating bucket: " + e, e);
        }
    }

    @Override
    public boolean exists(FileData fileData) {
        boolean ret = true;
        String key = FilenameUtils.separatorsToUnix(
                FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        key = StringUtils.stripStart(key, "/");
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            LOG.error("Error Checking existence of S3 object: " + e, e);
            return false;

        }
        return true;
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
            ListObjectsResponse response = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).prefix(prefix).delimiter("/").build());
            for (S3Object object : response.contents()) {
                String fileName = FilenameUtils.getName(FilenameUtils.normalize(object.key()));
                if (StringUtils.isNotBlank(fileName)) {
                    ret.add(new FileData(path, fileName));
                }
            }
        } catch (S3Exception e) {
            LOG.error("Error Listing Files: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public boolean delete(FileData fileData) {
        String key = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileData.getPath() + "/" + fileData.getFileName()));
        key = StringUtils.stripStart(key, "/");
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
        } catch (S3Exception e) {
            return false;
        }
        return true;
    }
}
