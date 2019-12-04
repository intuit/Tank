package com.intuit.tank.api.model.v1.agent;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TankHttpClientDefinitionTest {
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshall() throws Exception {
        TankHttpClientDefinition definition = new TankHttpClientDefinition("test", "com.test.package");
        String marshalled = JaxbUtil.marshall(definition);
        assertTrue(marshalled.contains("com.test.package"));
        TankHttpClientDefinition unmarshalled = JaxbUtil.unmarshall(marshalled, TankHttpClientDefinition.class);
        System.out.println(marshalled);
        assertEquals(unmarshalled, definition);
        assertEquals(unmarshalled.toString(), definition.toString());
        assertEquals(unmarshalled.getName(), definition.getName());
        assertEquals(unmarshalled.getClassName(), definition.getClassName());
        assertEquals(unmarshalled.hashCode(), definition.hashCode());
    }
}
