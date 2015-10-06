package com.intuit.tank.api.model.v1.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

public class TankHttpClientDefinitionContainerTest {
    
    @Test(groups = TestGroups.FUNCTIONAL)
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
        Assert.assertTrue(marshalled.contains("com.test1.package"));
        Assert.assertTrue(marshalled.contains("com.test2.package"));
        Assert.assertTrue(marshalled.contains("com.test3.package"));
        TankHttpClientDefinitionContainer unmarshalled = JaxbUtil.unmarshall(marshalled, TankHttpClientDefinitionContainer.class);

        Assert.assertEquals(unmarshalled.getDefaultDefinition(), container.getDefaultDefinition());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(unmarshalled, container));
    }
}
