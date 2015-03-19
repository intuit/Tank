package com.intuit.tank.vm.api.enumerated;

/*
 * #%L
 * Intuit Tank Api
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

public enum VMProvider implements Serializable {
    Amazon,
    Pharos;

    public static VMProvider getProvider(String providerString) {
        if (providerString.equals("Amazon")) {
            return Amazon;
        } else if (providerString.equals("Pharos")) {
            return Pharos;
        } else {
            throw new IllegalArgumentException("Unknown provider type: " + providerString);
        }
    }
}
