/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

import java.io.Serializable;

/**
 * KeyPair
 * 
 * @author dangleton
 * 
 */
public class KeyValuePair implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    /**
     * 
     * @param key
     * @param value
     */
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;

    }

    /**
     * @param key
     */
    public KeyValuePair(String key) {
        this(key, null);
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
