package com.intuit.tank.http.xml;

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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Generic class to provide xml file reading and writing capabilities
 */
public class GenericXMLHandler implements Cloneable {
    static Logger LOG = LogManager.getLogger(GenericXMLHandler.class);

    protected Document xmlDocument = null;
    protected File xmlFile = null;
    protected HashMap<String, String> namespaces;
    protected String xml;

    /**
     * Constructor
     */
    public GenericXMLHandler() {
        try {
            this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            this.namespaces = new HashMap<String, String>();
        } catch (ParserConfigurationException ex) {
            LOG.error("Error initializing handler: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Constructor
     * 
     * @param xmlFile
     *            The xml file to load
     */
    public GenericXMLHandler(File xmlFile) {
        try {
            this.xmlFile = xmlFile;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.xmlDocument = builder.parse(this.xmlFile);
            this.namespaces = new HashMap<String, String>();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            this.xmlDocument = null;
            LOG.error("Error initializing handler: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Constructor
     * 
     * @param xmlString
     *            The string representation of the xml data
     */
    public GenericXMLHandler(String xmlString) {
        if (StringUtils.isNotEmpty(xmlString)) {
            this.xml = xmlString;
            try {
                this.xmlFile = null;
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                xmlString = xmlString.substring(xmlString.indexOf("<"));
                this.xmlDocument = builder.parse(new InputSource(new StringReader(xmlString)));
                this.namespaces = new HashMap<String, String>();
            } catch (Exception ex) {
                this.xmlDocument = null;
                LOG.error("Error parsing xml Response: {}: {}", xmlString, ex.getMessage());
            }
        }
    }

    /**
     * Set the element's text value
     * 
     * @param xPathExpression
     *            The xpath expression for the element
     * @param value
     *            The value to set the text to
     */
    public void SetElementText(String xPathExpression, String value) {

        try {
            Element element = (Element) XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            if (element != null) element.setTextContent(value);
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }

    }

    public void SetElementAttribute(String xPathExpression, String attribute, String value) {

        try {
            Element element = (Element) XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            element.setAttribute(attribute, value);
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
    }

   /**
     * Get the text for the selected element
     * 
     * @param xPathExpression
     *            The xpath expression for the element
     * @return The text value of the element
     */
    public String GetElementText(String xPathExpression) {
        try {
            Node node = (Node) XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            return node.getTextContent();
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * Get the text for the selected element
     * 
     * @param xPathExpression
     *            The xpath expression for the element
     * @return The text value of the element
     */
    public String GetElementAttr(String xPathExpression) {
        try {
            Node node = (Node) XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            NamedNodeMap attributes = node.getAttributes();
            return IntStream.range(0, attributes.getLength())
                    .mapToObj(attributes::item)
                    .map(Node::getTextContent)
                    .collect(Collectors.joining(","));
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * Retrieves the text elements for a given xpath expression
     * 
     * @param xPathExpression
     * @return
     */
    public ArrayList<String> GetElementList(String xPathExpression) {
        try {
            NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODESET);
            return IntStream.range(0, nodeList.getLength())
                    .mapToObj(nodeList::item)
                    .map(Node::getTextContent)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Does an xPath expression exist
     * 
     * @param xPathExpression
     *            The xPath expression
     * @return TRUE if the xPath expression exists; false otherwise
     */
    public boolean xPathExists(String xPathExpression)
    {
        try {
            return !(XPathFactory.newInstance().newXPath().evaluate(xPathExpression, this.xmlDocument).isEmpty());
        } catch (XPathExpressionException ex) {
            return false;
        }
    }

    /**
     * Return the xml document in a String object
     */
    public String toString() {
        if (this.xmlDocument != null) {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(this.xmlDocument), new StreamResult(writer));
                String output = writer.getBuffer().toString();
                output = output.replaceAll("\r", "");
                output = output.replaceAll("\n", "");
                output = output.replaceAll("\t", "");
                return output;
            } catch (TransformerException ex) {
                LOG.error("Error in handler: {}", ex.getMessage(), ex);
            }
        }
        return "";
    }

    /**
     * Save the xml to a file
     */
    public void Save() {
        try (FileWriter writer = new FileWriter(this.xmlFile)) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(this.xmlDocument), new StreamResult(writer));
            writer.flush();
        } catch (TransformerException | IOException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
    }

    public boolean isXMLValid() {
        return !(this.xmlDocument == null);
    }

    /**
     * Clone the object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

    /**
     * 
     * 
     * @param xpath
     * @return
     */
    public String getParentPath(String xpath) {

        String[] paths = xpath.split("/");

        StringBuilder newPath = new StringBuilder();
        for (int i = 1; i <= paths.length - 2; i++) {
            newPath.append("/");
            newPath.append(paths[i]);
        }
        return newPath.toString();

    }

    /**
     * 
     * 
     * @param xpath
     * @return
     */
    public String getCurrentPath(String xpath, int node) {

        String[] paths = xpath.split("/");

        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i <= node; i++) {
            newPath.append("/");
            newPath.append(paths[i + 1]);
        }
        return newPath.toString();

    }

    /**
     * 
     * @param xpath
     * @return
     */
    public String getChildNode(String xpath) {

        String[] paths = xpath.split("/");
        String node = paths[paths.length - 1];
        if (node.contains(":")) {
            node = node.substring(node.indexOf(":") + 1);
        }
        return node;
    }

    public String getCurrentNamespace(String xpath) {

        String[] paths = xpath.split("/");
        String node = paths[paths.length - 1];

        if (node.contains(":")) {
            return node.substring(0, node.indexOf(":"));
        }
        return null;
    }

    public void setNamespace(String name, String value) {
        this.namespaces.put(name, value);
    }
}
