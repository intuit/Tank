package com.intuit.tank.storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents abstract data about files.
 * 
 * @author denisa
 *
 */
public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String path;
    private String fileName;
    private Map<String, String> attributes = new HashMap<String, String>();
    
    public FileData() {
        super();
    }

    /**
     * @param path
     * @param fileName
     */
    public FileData(String path, String fileName) {
        super();
        this.path = path;
        this.fileName = fileName;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path != null ? path : "";
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * adds an attribute
     * @param key the key
     * @param value the value
     */
    public void addAttribute(@Nonnull String key, @Nonnull String value) {
        this.attributes.put(key, value);
    }
    
    @Override
    public String toString() {
        return FilenameUtils.normalize(getPath() +"/" + getFileName());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileData)) {
            return false;
        }
        FileData o = (FileData) obj;
        return new EqualsBuilder().append(getPath(), o.getPath()).append(getFileName(), o.getFileName()).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPath()).append(getFileName()).toHashCode();
    }

}
