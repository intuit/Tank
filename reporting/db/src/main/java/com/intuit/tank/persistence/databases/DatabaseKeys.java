package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum DatabaseKeys {

    // these have to be unique
    REQUEST_NAME_KEY("RN"),
    JOB_ID_KEY("ID"),
    STATUS_CODE_KEY("SC"),
    CONNECTION_TIME_KEY("CT"),
    REQUEST_SIZE_KEY("RS"),
    RESPONSE_TIME_KEY("RT"),
    RESPONSE_SIZE_KEY("RE"),
    IS_ERROR_KEY("IE"),
    TIMESTAMP_KEY("TS"),
    LOGGING_KEY_KEY("LK"),
    PERIOD_KEY("RP"),
    TRANSACTIONS_KEY("TX"),
    INSTANCE_ID_KEY("II");

    private String shortKey;

    DatabaseKeys(String shortKey) {
        this.shortKey = shortKey;
    }

    public String getShortKey() {
        return this.shortKey;
    }

}
