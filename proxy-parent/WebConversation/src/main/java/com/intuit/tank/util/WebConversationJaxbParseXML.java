package com.intuit.tank.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * @{inheritDoc
     */
    public List<Transaction> parse(String xml) throws WatsParseException {
        return parse(new StringReader(xml));
    }

    /**
     * @{inheritDoc
     */
    public List<Transaction> parse(Reader reader) throws WatsParseException {
        try {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLEventReader xmler = xmlif.createXMLEventReader(reader);
            return parse(xmlif, xmler);
        } catch (Exception e) {
            throw ParseExceptionConverter.handleException(e);
        }
    }

    private List<Transaction> parse(XMLInputFactory xmlif, XMLEventReader xmler) throws WatsParseException {

        EventFilter filter = new EventFilter() {
            public boolean accept(XMLEvent event) {
                return event.isStartElement();
            }
        };
        List<Transaction> result = null;
        try {
            XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, filter);
            // Jump to the first element in the document, the enclosing log
            xmlfer.nextEvent();
            // Parse into typed objects
            JAXBContext ctx = JAXBContext.newInstance(Transaction.class.getPackage().getName());
            Unmarshaller um = ctx.createUnmarshaller();
            result = new ArrayList<Transaction>();
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
