/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    @SuppressWarnings("unchecked")
    public static final <T> T unmarshall(String toUnmarshall, Class<T> clazz) throws JAXBException,
    																				ParserConfigurationException,
    																				SAXException {
    	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
    	SAXParserFactory spf = SAXParserFactory.newInstance();
    	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    	
    	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(toUnmarshall)));
    	
        JAXBContext ctx = JAXBContext.newInstance(clazz.getPackage().getName());
        Object unmarshalled = ctx.createUnmarshaller().unmarshal(xmlSource);
        return (T) unmarshalled;
    }
}
