package com.intuit.tank.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FileStorage that writes to the file system.
 * 
 * @author denisa
 *
 */
public class FileSystemFileStorage implements FileStorage, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(FileSystemFileStorage.class);

    private String basePath;
    private boolean compress = true;

    /**
     * @param basePath
     */
    public FileSystemFileStorage(String basePath, boolean compress) {
        super();
        this.basePath = FilenameUtils.normalizeNoEndSeparator(basePath);
        this.compress = compress;
        File dir = new File(basePath);
        if (!dir.exists()) {
            LOG.info("Creating storage dir " + dir.getAbsolutePath());
        }
    }

    @Override
    public void storeFileData(FileData fileData, InputStream input) {
        File file = new File(FilenameUtils.normalize(basePath + "/" + fileData.getPath() + "/" + fileData.getFileName()));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (OutputStream output = compress ?
                new GZIPOutputStream(new FileOutputStream(file)) :
                new FileOutputStream(file) ) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            LOG.error("Error storing file: " + e, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream readFileData(FileData fileData) {
        File file = new File(FilenameUtils.normalize(basePath + "/" + fileData.getPath() + "/" + fileData.getFileName()));
        try {
            return compress ?
                    new GZIPInputStream(new FileInputStream(file)) :
                    new FileInputStream(file);
        } catch (IOException e) {
            LOG.error("Error storing file: " + e, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(FileData fileData) {
        File file = new File(FilenameUtils.normalize(basePath + "/" + fileData.getPath() + "/" + fileData.getFileName()));
        return file.exists();
    }

    @Override
    public List<FileData> listFileData(String path) {
        File dir = new File(FilenameUtils.normalize(basePath + "/" + path));
        if (dir.exists()) {
            return Arrays.stream(dir.listFiles()).filter(File::isFile).map(f -> new FileData(path, f.getName())).collect(Collectors.toList());
        }
        return new ArrayList<FileData>();
    }

    @Override
    public boolean delete(FileData fileData) {
        File file = new File(FilenameUtils.normalize(basePath + "/" + fileData.getPath() + "/" + fileData.getFileName()));
        return file.delete();
    }
}