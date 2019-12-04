package com.intuit.tank.http;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthCredentialsTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
  public void getHost() {
      AuthCredentials fixture = getAuthCreds();
      assertEquals("test.com", fixture.getHost());
      assertEquals("tester", fixture.getUserName());
      assertEquals("password", fixture.getPassword());
      assertEquals("80", fixture.getPortString());
      assertEquals("testRealm", fixture.getRealm());
      assertEquals(AuthScheme.Basic, fixture.getScheme());
  }

  private AuthCredentials getAuthCreds() {
      return  AuthCredentials.builder().withHost("test.com").withUserName("tester").withPassword("password").withPortString("80").withRealm("testRealm").withScheme(AuthScheme.Basic).build();
  }
}
