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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Generic class to provide xml file reading and writing capabilities
 */
public class GenericXMLHandler implements Cloneable {
    static Logger LOG = LogManager.getLogger(GenericXMLHandler.class);

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY;
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final TransformerFactory TRANSFORMER_FACTORY;

    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Set each feature independently so one unsupported feature does not skip the rest (fail closed).
        setFeatureQuietly(dbf, "http://apache.org/xml/features/disallow-doctype-decl", true);
        setFeatureQuietly(dbf, "http://xml.org/sax/features/external-general-entities", false);
        setFeatureQuietly(dbf, "http://xml.org/sax/features/external-parameter-entities", false);
        // Standard, portable XXE restrictions on the parser factory.
        dbf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        dbf.setNamespaceAware(true);
        DOCUMENT_BUILDER_FACTORY = dbf;

        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        TRANSFORMER_FACTORY = tf;
    }

    private static void setFeatureQuietly(DocumentBuilderFactory dbf, String feature, boolean value) {
        try {
            dbf.setFeature(feature, value);
        } catch (ParserConfigurationException ex) {
            LOG.warn("Could not set XXE prevention feature {}: {}", feature, ex.getMessage());
        }
    }

    protected Document xmlDocument = null;
    protected File xmlFile = null;
    protected HashMap<String, String> namespaces;
    protected String xml;

    /**
     * Constructor
     */
    public GenericXMLHandler() {
        try {
            this.xmlDocument = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();
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
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
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
                DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
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
            Element element = resolveOrCreateElement(xPathExpression, 0);
            if (element != null) element.setTextContent(value);
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }

    }

    public void SetElementAttribute(String xPathExpression, String attribute, String value) {

        try {
            Element element = resolveOrCreateElement(xPathExpression, 0);
            if (element != null) element.setAttribute(attribute, value);
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Resolve the element for the given xpath expression, creating any missing path segments (and the
     * root element if necessary) so that a value can be written to it.
     *
     * @param xPathExpression
     *            The xpath expression for the element
     * @param currentNode
     *            The index of the path segment currently being resolved
     * @return The resolved (or newly created) element for the full xpath expression
     * @throws XPathExpressionException
     */
    private Element resolveOrCreateElement(String xPathExpression, int currentNode) throws XPathExpressionException {

        String[] segments = Arrays.stream(xPathExpression.split("/"))
                .filter(StringUtils::isNotEmpty)
                .toArray(String[]::new);
        if (segments.length == 0) {
            return null;
        }

        String currentPath = String.join("/", Arrays.copyOfRange(segments, 0, currentNode + 1));

        Node existing = (Node) newXPath()
                .evaluate(currentPath, this.xmlDocument, XPathConstants.NODE);
        if (existing instanceof Element) {
            if (currentNode == segments.length - 1) {
                return (Element) existing;
            }
            return resolveOrCreateElement(xPathExpression, currentNode + 1);
        }

        // Last segment of the current path, e.g. "ns:child" — keep the prefix for the qualified name.
        String qualifiedName = segments[currentNode];
        String namespace = getCurrentNamespace(currentPath);

        Element element;
        if (namespace != null && this.namespaces.get(namespace) != null) {
            element = this.xmlDocument.createElementNS(this.namespaces.get(namespace), qualifiedName);
        } else {
            element = this.xmlDocument.createElement(getChildNode(currentPath));
        }

        if (this.xmlDocument.getDocumentElement() != null) {
            String parentPath = String.join("/", Arrays.copyOfRange(segments, 0, currentNode));
            Node parent = (Node) newXPath()
                    .evaluate(parentPath, this.xmlDocument, XPathConstants.NODE);
            if (parent == null) {
                return null;
            }
            parent.appendChild(element);
        } else {
            this.xmlDocument.appendChild(element);
        }

        if (currentNode == segments.length - 1) {
            return element;
        }
        return resolveOrCreateElement(xPathExpression, currentNode + 1);
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
            Node node = (Node) newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            return node != null ? node.getTextContent() : "";
        } catch (XPathExpressionException ex) {
            LOG.error("Error in handler: {}", ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * Get the value of the attribute selected by the xpath expression.
     * <p>
     * The expression is expected to select an attribute, e.g. {@code root/child/@first}, matching the
     * original attribute-selection contract. The attribute value is returned directly (an empty string
     * if the expression selects nothing).
     *
     * @param xPathExpression
     *            The xpath expression selecting the attribute
     * @return The value of the selected attribute, or "" if it does not exist
     */
    public String GetElementAttr(String xPathExpression) {
        try {
            return newXPath().evaluate(xPathExpression, this.xmlDocument);
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
            NodeList nodeList = (NodeList) newXPath()
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
            Node node = (Node) newXPath()
                    .evaluate(xPathExpression, this.xmlDocument, XPathConstants.NODE);
            return node != null;
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
                Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
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
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
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

    /**
     * Create an XPath instance whose prefixes resolve against the configured namespaces so that
     * expressions such as {@code ns:root/ns:child} evaluate against namespace-aware documents.
     */
    private XPath newXPath() {
        XPath xpath = XPATH_FACTORY.newXPath();
        final Map<String, String> ns = this.namespaces;
        if (ns != null && !ns.isEmpty()) {
            xpath.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    String uri = ns.get(prefix);
                    return uri != null ? uri : javax.xml.XMLConstants.NULL_NS_URI;
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    for (Map.Entry<String, String> entry : ns.entrySet()) {
                        if (entry.getValue().equals(namespaceURI)) {
                            return entry.getKey();
                        }
                    }
                    return null;
                }

                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return ns.entrySet().stream()
                            .filter(e -> e.getValue().equals(namespaceURI))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList())
                            .iterator();
                }
            });
        }
        return xpath;
    }
}
