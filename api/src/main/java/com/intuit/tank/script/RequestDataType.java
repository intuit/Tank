/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script;

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

/**
 * RequestDataType
 * 
 * @author dangleton
 * 
 */
public enum RequestDataType {

    responseContent, // this is a special catchall type that must me honored for legacy purposes
    requestPostData,
    cookieAssignment,
    headerAssignment,
    bodyAssignment,
    cookieValidation,
    headerValidation,
    bodyValidation,
    statusValidation,
    locationValidation,

    queryString,
    responseCookie,
    requestCookie,
    responseHeader,
    requestHeader

}
