package com.intuit.tank.api.model.v1.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

import static org.junit.jupiter.api.Assertions.*;

public class TankHttpClientDefinitionContainerTest {
    
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshall() throws Exception {
        List<TankHttpClientDefinition> list = new ArrayList<TankHttpClientDefinition>();
        TankHttpClientDefinition definition1 = new TankHttpClientDefinition("test1", "com.test1.package");
        TankHttpClientDefinition definition2 = new TankHttpClientDefinition("test2", "com.test2.package");
        TankHttpClientDefinition definition3 = new TankHttpClientDefinition("test3", "com.test3.package");
        list.add(definition1);
        list.add(definition2);
        list.add(definition3);
        TankHttpClientDefinitionContainer container = new TankHttpClientDefinitionContainer(list, "test1");
        String marshalled = JaxbUtil.marshall(container);
        System.out.println(marshalled);
        assertTrue(marshalled.contains("com.test1.package"));
        assertTrue(marshalled.contains("com.test2.package"));
        assertTrue(marshalled.contains("com.test3.package"));
        TankHttpClientDefinitionContainer unmarshalled = JaxbUtil.unmarshall(marshalled, TankHttpClientDefinitionContainer.class);

        assertEquals(unmarshalled.getDefaultDefinition(), container.getDefaultDefinition());
        assertTrue(EqualsBuilder.reflectionEquals(unmarshalled, container));
    }
}
