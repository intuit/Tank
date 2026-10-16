package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDWorkload;

import jakarta.xml.bind.JAXBException;

public class JaxbUtilTest {

    private static final String NAMESPACE = "urn:com/intuit/tank/harness/data/v1";

    @Test
    public void readsAWorkload() throws Exception {
        String xml = "<?xml version=\"1.0\"?><workload xmlns=\"" + NAMESPACE + "\" name=\"UC1\"/>";

        assertEquals("UC1", JaxbUtil.unmarshall(xml, HDWorkload.class).getName());
    }

    @Test
    public void rejectsScriptsWithADoctype() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE workload [<!ENTITY injected \"injected\">]>"
                + "<workload xmlns=\"" + NAMESPACE + "\" name=\"&injected;\"/>";

        assertThrows(JAXBException.class, () -> JaxbUtil.unmarshall(xml, HDWorkload.class));
    }
}
