package com.intuit.tank.storage;

import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.http.apache.ProxyConfiguration.Builder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLocationResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
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
            encrypt = tankConfig.isS3EncryptionEnabled();
            CloudCredentials creds = tankConfig.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);

            Builder proxyConf = ProxyConfiguration.builder();
            String proxyHost = "";
            String port = "";
            URI proxy = null;
            if (StringUtils.isNotBlank(System.getProperty("http.proxyHost"))) {
                try {
                   proxyHost =  System.getProperty("http.proxyHost") ;
                    if (StringUtils.isNotBlank(System.getProperty("http.proxyPort"))) {
                        port = System.getProperty("http.proxyPort");
                        proxy = new URI(proxyHost+":"+port);
                    } else {
                        proxy = new URI(proxyHost);
                    }
                   proxyConf.endpoint(proxy);
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }

            }
            assert creds != null;
            if (StringUtils.isNotBlank(creds.getKeyId()) && StringUtils.isNotBlank(creds.getKey())) {
                AwsCredentials awsCredentails = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                SdkHttpClient httpClient = (SdkHttpClient) ApacheHttpClient.builder().proxyConfiguration(
                    proxyConf.build());
                this.s3Client = S3Client.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentails))
                        .httpClient(httpClient)
                        .build();
            } else {
                this.s3Client = S3Client.builder()
                    .httpClient(ApacheHttpClient.builder().build())
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

            PutObjectRequest.Builder objectRequest = PutObjectRequest.builder();
            if (encrypt) {

                objectRequest.serverSideEncryption(ServerSideEncryption.AES256);
            }
            if (fileData.getAttributes() != null) {
                objectRequest.metadata(fileData.getAttributes());
            }
            if (compress) {
                in = new GZIPInputStream(in);
            }

            PutObjectResponse out = s3Client.putObject(objectRequest.build(), Paths.get(path));
        } catch (Exception e) {
            LOG.error("Error storing file: " + e, e);
            throw new RuntimeException(e);
        } finally {
            //TODO : ask what to do with it now
            //IOUtils.closeQuietly(in, null);
        }
    }

    @Override
    public InputStream readFileData(FileData fileData) {
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        InputStream ret = null;
        try {
            s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build());
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(path).build(),
                ResponseTransformer.toInputStream());
            if (object != null ) {
                if (compress) {
                    ret = new GZIPInputStream(object);
                } else {
                    ret = object;
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting File: " + e, e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    private void createBucket(String bucketName) {
        CreateBucketRequest.Builder createBucketRequest = CreateBucketRequest.builder();
        GetBucketAclResponse acl = null;
        try {
            acl = s3Client.getBucketAcl(GetBucketAclRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            LOG.info("Bucket " + bucketName + " does not exist.");
        }
        if (acl == null) {
            CreateBucketResponse bucket = s3Client.createBucket(createBucketRequest.bucket(bucketName).build());

            LOG.info("Created bucket " + bucketName + " at with requestId "+bucket.responseMetadata().requestId());
        }
    }

    @Override
    public boolean exists(FileData fileData) {

        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(extraPath + fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        GetBucketLocationResponse bucketLocation = s3Client.getBucketLocation(GetBucketLocationRequest.builder().bucket(bucketName).build());
        return (bucketLocation.locationConstraintAsString().equalsIgnoreCase(path));
    }

    @Override
    public List<FileData> listFileData(String path) {
        List<FileData> ret = new ArrayList<FileData>();
        String prefix = extraPath + path;
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        prefix = StringUtils.removeStart(prefix, "/");
        ListObjectsV2Request listObject = ListObjectsV2Request.builder().bucket(bucketName).delimiter("/").prefix(prefix).build();
        ListObjectsV2Response files = s3Client.listObjectsV2(listObject);
        for(S3Object file : files.contents()){
            for(SdkField fileSdk:file.sdkFields()){
                ret.add(new FileData(fileSdk.locationName(),file.key()));
            }
        }
        return ret;
    }

    @Override
    public boolean delete(FileData fileData) {
        String path = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileData.getPath() + "/" + fileData.getFileName()));
        path = StringUtils.stripStart(path, "/");
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(path).build());
        } catch (AwsServiceException | SdkClientException e) {
            return false;
        }
        return true;
    }
}
