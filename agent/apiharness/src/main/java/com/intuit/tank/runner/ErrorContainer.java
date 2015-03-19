package com.intuit.tank.runner;

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

import com.intuit.tank.harness.data.ValidationData;

public class ErrorContainer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String location;
    private ValidationData validation;
    private ValidationData originalValidation;
    private String reason;

    public ErrorContainer(String location, ValidationData original, ValidationData validation, String reason) {
        super();
        this.location = location;
        this.validation = validation;
        this.reason = reason;
        this.originalValidation = original;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the validation
     */
    public ValidationData getValidation() {
        return validation;
    }

    /**
     * @return the originalValidation
     */
    public ValidationData getOriginalValidation() {
        return originalValidation;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

}
