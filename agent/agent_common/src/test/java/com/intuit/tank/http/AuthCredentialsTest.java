package com.intuit.tank.http;

import org.testng.annotations.Test;

import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.test.TestGroups;

import junit.framework.Assert;

public class AuthCredentialsTest {

  @Test(groups=TestGroups.FUNCTIONAL)
  public void getHost() {
      AuthCredentials fixture = getAuthCreds();
      Assert.assertEquals("test.com", fixture.getHost());
      Assert.assertEquals("tester", fixture.getUserName());
      Assert.assertEquals("password", fixture.getPassword());
      Assert.assertEquals("80", fixture.getPortString());
      Assert.assertEquals("testRealm", fixture.getRealm());
      Assert.assertEquals(AuthScheme.Basic, fixture.getScheme());
  }

  private AuthCredentials getAuthCreds() {
      return  AuthCredentials.builder().withHost("test.com").withUserName("tester").withPassword("password").withPortString("80").withRealm("testRealm").withScheme(AuthScheme.Basic).build();
  }
}
