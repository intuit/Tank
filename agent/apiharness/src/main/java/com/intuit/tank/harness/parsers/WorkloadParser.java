/**
 * TODO: THIS CLASS IS A HACK!  Need to refactor.
 * If the xml test plan elements are out of order, no holds barred.
 */

package com.intuit.tank.harness.parsers;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import com.intuit.tank.harness.data.HDWorkload;

public class WorkloadParser {

    static Logger logger = LogManager.getLogger(WorkloadParser.class);
    // private Variables parsedVariables = new Variables();

    // private Document xmlDocument = null;
    private HDWorkload workload;

    /**
     * Constructor
     * 
     * @param xmlFile
     *            The xml file to load
     */
    public WorkloadParser(File xmlFile) {
        Reader reader;
        try {
            reader = new FileReader(xmlFile);
            initialize(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     * 
     * @param xmlFile
     *            The xml file to load
     */
    public WorkloadParser(String xmlFile) {
        Reader reader = new StringReader(xmlFile);
        initialize(reader);
    }

    private void initialize(Reader reader) {
        try {
        	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        	
        	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(reader));
        	
            JAXBContext context = JAXBContext.newInstance(HDWorkload.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            workload = (HDWorkload) unmarshaller.unmarshal(xmlSource);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the test plan xml
     * 
     * @return The test plan object
     */
    public HDWorkload getWorkload() {
        return workload;
    }

}
