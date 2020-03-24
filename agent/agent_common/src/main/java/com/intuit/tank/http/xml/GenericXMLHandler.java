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

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;
import org.xml.sax.InputSource;

/**
 * Generic class to provide xml file reading and writing capabilities
 */
public class GenericXMLHandler implements Cloneable {
    static Logger LOG = LogManager.getLogger(GenericXMLHandler.class);

    protected Document xmlDocument = null;
    protected File xmlFile = null;
    protected HashMap<String, String> namespaces;
    protected String xml;
    private org.w3c.dom.Document dDoc;

    /**
     * Constructor
     */
    public GenericXMLHandler() {
        try {
            this.xmlDocument = new Document();
            this.namespaces = new HashMap<String, String>();
        } catch (Exception ex) {
            LOG.error("Error initializing handler: " + ex.getMessage(), ex);
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
            this.xmlDocument = new org.jdom2.Document();
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            this.xmlDocument = builder.build(this.xmlFile);
            this.namespaces = new HashMap<String, String>();
        } catch (Exception ex) {
            this.xmlDocument = null;
            LOG.error("Error initializing handler: " + ex.getMessage(), ex);
        }
    }

    /**
     * Constructor
     * 
     * @param xmlFile
     *            The string representation of the xml data
     */
    public GenericXMLHandler(String xmlFile) {
        if (StringUtils.isNotEmpty(xmlFile)) {
            this.xml = xmlFile;
            try {
                this.xmlFile = null;
                this.xmlDocument = new org.jdom2.Document();
                SAXBuilder builder = new SAXBuilder();
                builder.setValidation(false);
                // LOG.debug("XML string to load: "+xmlFile);
                xmlFile = xmlFile.substring(xmlFile.indexOf("<"));
                this.xmlDocument = builder.build(new StringReader(xmlFile));
                this.namespaces = new HashMap<String, String>();
                InputSource is = new InputSource(new StringReader(xml));
                this.dDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(is);
            } catch (Exception ex) {
                this.xmlDocument = null;
                LOG.error("Error parsing xml Response: " + xmlFile + ": " + ex.getMessage());
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

        Element node;
        try {
            node = SetElementText(xPathExpression, 0);
            node.setText(value);
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
        }

    }

    public void SetElementAttribute(String xPathExpression, String attribute, String value) {

        Element node;
        try {
            node = SetElementText(xPathExpression, 0);
            node.setAttribute(attribute, value);
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
        }
    }

    /**
     * Set the element's text value
     * 
     * @param xPathExpression
     *            The xpath expression for the element
     * @param currentNode
     *            The value to set the text to
     * @throws JDOMException
     */
    private Element SetElementText(String xPathExpression, int currentNode) throws JDOMException {

        String currentPath = getCurrentPath(xPathExpression, currentNode);

        if (xPathExists(currentPath)) {
            if (currentPath.equals(xPathExpression)) {
                return (org.jdom2.Element) XPath.selectSingleNode(this.xmlDocument, xPathExpression);
            }
            else {
                return SetElementText(xPathExpression, currentNode + 1);
            }
        } else {

            String childNode = getChildNode(currentPath);
            String namespace = getCurrentNamespace(currentPath);

            Element element;
            if (namespace != null) {
                Namespace sNS = Namespace.getNamespace(namespace, this.namespaces.get(namespace));
                element = new Element(childNode, sNS);
                // element.setAttribute("someKey", "someValue", Namespace.getNamespace("someONS",
                // "someOtherNamespace"));
            } else {
                element = new Element(childNode);
            }

            if (this.xmlDocument.hasRootElement()) {
                Element node = (Element) XPath.selectSingleNode(this.xmlDocument, getParentPath(currentPath));
                node.addContent(element);
            } else {
                if (this.xmlDocument.hasRootElement()) {
                    this.xmlDocument.detachRootElement();
                }

                this.xmlDocument.addContent(element);
            }

            if (currentPath.equals(xPathExpression)) {
                return element;
            }
            else {
                return SetElementText(xPathExpression, currentNode + 1);
            }

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
            String result = XPathFactory.newInstance().newXPath().evaluate(xPathExpression, dDoc);
            if (StringUtils.isNotEmpty(result)) {
                return result;
            }
            return "";
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
            return "";
        }
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
            org.jdom2.Attribute node = (Attribute) XPath.selectSingleNode(this.xmlDocument, xPathExpression);
            return node.getValue();
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * Retrieves the text elements for a given xpath expression
     * 
     * @param xPathExpression
     * @return
     */
    public ArrayList<String> GetElementList(String xPathExpression) {
        try {
            List<?> nodeList = XPath.selectNodes(this.xmlDocument, xPathExpression);
            return nodeList.stream().map(aNodeList -> (Element) aNodeList).map(Element::getText).collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Does an xPath expression exist
     * 
     * @param xpathExpr
     *            The xPath expression
     * @return TRUE if the xPath expression exists; false otherwise
     */
    public boolean xPathExists(String xpathExpr)
    {
        try {
            return !(XPath.selectSingleNode(this.xmlDocument, xpathExpr) == null);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Return the xml document in a String object
     */
    public String toString() {
        if (this.xmlDocument != null) {
            XMLOutputter outputter = new XMLOutputter();
            String output = outputter.outputString(this.xmlDocument);
            output = output.replaceAll("\r", "");
            output = output.replaceAll("\n", "");
            output = output.replaceAll("\t", "");
            return output;
        }
        return "";
    }

    /**
     * Save the xml to a file
     */
    public void Save() {
        try {
            XMLOutputter out = new XMLOutputter();
            java.io.FileWriter writer = new java.io.FileWriter(this.xmlFile);
            out.output(this.xmlDocument, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            LOG.error("Error in handler: " + ex.getMessage(), ex);
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
