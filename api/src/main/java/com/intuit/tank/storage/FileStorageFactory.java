/**
 * 
 */
package com.intuit.tank.storage;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Strings;

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
            if (Strings.CI.startsWith(base, "s3:")) {
                String bucketName = Strings.CS.removeStart(FilenameUtils.normalizeNoEndSeparator(base.substring(3)), "/");
                fileStorage = new S3FileStorage(bucketName, compress);
            } else {
                fileStorage = new FileSystemFileStorage(base, compress);
            }
            storageMap.put(key, fileStorage);
        }
        return fileStorage;
    }
}
