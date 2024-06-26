package com.intuit.tank.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.vm.exception.ParseExceptionConverter;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * parses xml output from hp perf tool JaxbParseXML
 * 
 * @author dangleton
 * 
 */
public class WebConversationJaxbParseXML {

    public WebConversationJaxbParseXML() {

    }

    /**
     * {@inheritDoc}
     */
    public List<Transaction> parse(String xml) throws WatsParseException {
        return parse(new StringReader(xml));
    }

    /**
     * {@inheritDoc}
     */
    public List<Transaction> parse(Reader reader) throws WatsParseException {
        try {
            //Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            // This disables DTDs entirely for that factory
            xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLEventReader xmler = xmlInputFactory.createXMLEventReader(reader);
            return parse(xmlInputFactory, xmler);
        } catch (Exception e) {
            throw ParseExceptionConverter.handleException(e);
        }
    }

    private List<Transaction> parse(XMLInputFactory xmlif, XMLEventReader xmler) throws WatsParseException {

        List<Transaction> result = new ArrayList<Transaction>();
        try {
            XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, XMLEvent::isStartElement);
            // Jump to the first element in the document, the enclosing log
            xmlfer.nextEvent();
            // Parse into typed objects
            JAXBContext ctx = JAXBContext.newInstance(Transaction.class.getPackage().getName());
            Unmarshaller um = ctx.createUnmarshaller();
            while (xmlfer.peek() != null) {
                Object o = um.unmarshal(xmler);
                if (o instanceof Transaction) {
                    result.add((Transaction) o);
                }
            }
        } catch (JAXBException e) {
            throw ParseExceptionConverter.handleException(e.getLinkedException());
        } catch (Exception e) {
            throw ParseExceptionConverter.handleException(e);
        }
        return result;
    }

}
