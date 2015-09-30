package com.intuit.tank.storage;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Reads and writes file type data to storage.
 * 
 * @author denisa
 *
 */
public interface FileStorage {

    /**
     * Stores the data.
     * 
     * @param in
     *            the data to store
     */
    public void storeFileData(FileData fileData, @Nonnull InputStream in);

    /**
     * 
     * @param fileData
     * @return
     */
    public InputStream readFileData(FileData fileData);

    /**
     * deleted the file
     * 
     * @param fd
     * @return true if the file was deleted. false if file not found.
     */
    public boolean delete(FileData fd);
    
    /**
     * tests whether the data exists
     * 
     * @param fd
     * @return
     */
    public boolean exists(FileData fd);

    /**
     * 
     * @param base
     * @return
     */
    public List<FileData> listFileData(String base);
}
