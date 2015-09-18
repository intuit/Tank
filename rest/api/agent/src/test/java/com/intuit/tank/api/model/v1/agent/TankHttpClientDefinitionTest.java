package com.intuit.tank.api.model.v1.agent;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

public class TankHttpClientDefinitionTest {
  @Test(groups=TestGroups.FUNCTIONAL)
  public void testMarshallUnmarshall() throws Exception {
      TankHttpClientDefinition definition = new TankHttpClientDefinition("test", "com.test.package");
      String marshalled = JaxbUtil.marshall(definition);
      Assert.assertTrue(marshalled.contains("com.test.package"));
      TankHttpClientDefinition unmarshalled = JaxbUtil.unmarshall(marshalled, TankHttpClientDefinition.class);
      System.out.println(marshalled);
      Assert.assertEquals(unmarshalled, definition);
      Assert.assertEquals(unmarshalled.toString(), definition.toString());
      Assert.assertEquals(unmarshalled.getName(), definition.getName());
      Assert.assertEquals(unmarshalled.getClassName(), definition.getClassName());
      Assert.assertEquals(unmarshalled.hashCode(), definition.hashCode());
  }
}
