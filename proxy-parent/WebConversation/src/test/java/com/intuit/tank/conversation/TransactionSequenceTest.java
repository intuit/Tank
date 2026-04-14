package com.intuit.tank.conversation;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class TransactionSequenceTest {

    @Test
    @DisplayName("Transaction default sequenceNumber is null (unset)")
    void defaultSequenceNumberIsNull() {
        Transaction t = new Transaction();
        assertNull(t.getSequenceNumber());
    }

    @Test
    @DisplayName("Transaction get/set sequenceNumber roundtrip")
    void setGetSequenceNumber() {
        Transaction t = new Transaction();
        t.setSequenceNumber(42);
        assertEquals(42, t.getSequenceNumber());
    }

    @Test
    @DisplayName("Transaction seq attribute survives JAXB marshal/unmarshal")
    void jaxbRoundTripPreservesSeq() throws Exception {
        Transaction t = new Transaction();
        t.setSequenceNumber(7);

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(t, sw);
        String xml = sw.toString();

        assertTrue(xml.contains("seq=\"7\""), "XML should contain seq attribute: " + xml);

        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Transaction result = (Transaction) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(7, result.getSequenceNumber());
    }

    @Test
    @DisplayName("Unset Transaction does not emit seq attribute in XML")
    void unsetSeqNotSerializedToXml() throws Exception {
        Transaction t = new Transaction();
        // No seq set — should be null and omitted from XML

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(t, sw);
        String xml = sw.toString();

        assertFalse(xml.contains("seq="), "Unset seq should not appear in XML: " + xml);
    }

    @Test
    @DisplayName("Legacy XML with no seq attribute deserializes to null")
    void legacyXmlWithoutSeqDeserializesToNull() throws Exception {
        String legacyXml = "<sns:transaction xmlns:sns=\"urn:proxy/conversation/v1\"/>";

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Transaction result = (Transaction) unmarshaller.unmarshal(new StringReader(legacyXml));

        assertNull(result.getSequenceNumber(), "Legacy transaction without seq should deserialize to null");
    }
}
