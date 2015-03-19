/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.test;

/*
 * #%L
 * Intuit Tank Test support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * JaxbUtil
 * 
 * @author dangleton
 * 
 */
public final class JaxbUtil {

    /**
     * no-arg private constructor to implement Util Pattern
     */
    private JaxbUtil() {
    }

    /**
     * Marshalls the object to a String. <br/>
     * Example: <code>
     * String marshalled = JaxbUtil.marshall(env);
     * </code>
     * 
     * @param toMarshall
     *            the object to Marshall
     * @return the String representation
     * @throws JAXBException
     *             if there is an error marshalling the object
     */
    public static final String marshall(Object toMarshall) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(toMarshall.getClass().getPackage().getName());
        StringWriter sw = new StringWriter();
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(toMarshall, sw);
        return sw.toString();
    }

    /**
     * UnMarshalls a String to the specidfied object type. <br/>
     * Example: <code>
     * Environment unmarshalled = JaxbUtil.<Environment>unmarshall(marshalled, Environment.class);
     * </code>
     * 
     * @param <T>
     *            the type of object being unmarshalled
     * @param toUnmarshall
     *            the String to unmarshall
     * @param clazz
     *            the class of the unMarshsalled object
     * @return the Object
     * @throws JAXBException
     *             if there is an error unmarshalling the String
     */
    @SuppressWarnings("unchecked")
    public static final <T> T unmarshall(String toUnmarshall, Class<T> clazz) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(clazz.getPackage().getName());
        Object unmarshalled = ctx.createUnmarshaller().unmarshal(new StringReader(toUnmarshall));
        return (T) unmarshalled;
    }
}
