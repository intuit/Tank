package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.intuit.tank.api.model.v1.cloud.UserDetail;

/**
 * 
 * UserTracker
 * 
 * @author dangleton
 * 
 */
public class UserTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Integer> userMap = new HashMap<String, Integer>();

    /**
     * 
     * @return
     */
    public synchronized List<UserDetail> getSnapshot() {
        return userMap.entrySet().stream().map(entry -> new UserDetail(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * 
     * @param script
     */
    public synchronized void add(String script) {
        Integer i = userMap.get(script);
        if (i == null) {
            userMap.put(script, 1);
        } else {
            userMap.put(script, i + 1);
        }
    }

    /**
     * 
     * @param script
     */
    public synchronized void remove(String script) {
        Integer i = userMap.get(script);
        if (i != null) {
            if (i > 0) {
                userMap.put(script, i - 1);
            }
        }
    }
}
