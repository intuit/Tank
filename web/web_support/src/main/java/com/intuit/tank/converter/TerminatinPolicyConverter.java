/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.converter;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.faces.convert.FacesConverter;

import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

/**
 * ViewFilterTypeConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsTerminationPolicyConverter")
public class TerminatinPolicyConverter extends BaseEnumConverter<TerminationPolicy> {

    public TerminatinPolicyConverter() {
        super(TerminationPolicy.class);
    }

}
