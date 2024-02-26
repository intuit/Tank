/**
 * 
 */
package com.intuit.tank.storage;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author denisa
 *
 */
public class FileStorageFactory {

    private static final Map<String, FileStorage> storageMap = new HashMap<String, FileStorage>();

    public static FileStorage getFileStorage(@Nonnull String base, boolean compress) {
        String key = base + compress;
        FileStorage fileStorage = storageMap.get(key);
        if (fileStorage == null) {
            if (StringUtils.startsWithIgnoreCase(base, "s3:")) {
                String bucketName = StringUtils.removeStart(FilenameUtils.normalizeNoEndSeparator(base.substring(3)), "/");
                fileStorage = new S3FileStorage(bucketName, compress);
            } else {
                fileStorage = new FileSystemFileStorage(base, compress);
            }
            storageMap.put(key, fileStorage);
        }
        return fileStorage;
    }
}
