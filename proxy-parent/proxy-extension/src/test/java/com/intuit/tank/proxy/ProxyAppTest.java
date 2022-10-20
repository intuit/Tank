package com.intuit.tank.proxy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * ProxyAppTest
 *
 * @author msreekakula
 *
 */
public class ProxyAppTest {

    @Test
    void testProxyApp() {
        ProxyApp proxyApp = new ProxyApp();
        assertNotNull(proxyApp);
    }

}
