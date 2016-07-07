/**
 * 
 */
package com.intuit.tank.storage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

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
        FileStorage ret = storageMap.get(key);
        if (ret == null) {
            if (base.startsWith("s3:")) {
                String s = StringUtils.removeStart(FilenameUtils.normalizeNoEndSeparator(base.substring(3)), "/");
                ret = new S3FileStorage(s, compress);
            } else {
                ret = new FileSystemFileStorage(base, compress);
            }
            storageMap.put(key, ret);
        }
        return ret;
    }
}
