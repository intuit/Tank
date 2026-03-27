package org.owasp.proxy.http.server;

import org.junit.Test;

import java.lang.reflect.Method;
import java.net.InetAddress;

import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;

import static org.junit.Assert.*;

/**
 * Tests for getServerSocket handler chain traversal — public API + reflection fallback.
 * Uses JUnit 4 to match owasp-proxy module's surefire 2.21 configuration.
 */
public class HttpProxyConnectionHandlerWebSocketTest {

    @Test
    public void getServerSocketTraversesDirectHandler() throws Exception {
        DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler();
        HttpProxyConnectionHandler handler = new HttpProxyConnectionHandler(drh);

        Method method = HttpProxyConnectionHandler.class.getDeclaredMethod(
                "getServerSocket", HttpRequestHandler.class);
        method.setAccessible(true);

        Object result = method.invoke(handler, drh);
        assertNull("Should return null when no connection is established", result);
    }

    @Test
    public void getServerSocketTraversesOwaspBufferingWrapper() throws Exception {
        DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler();
        BufferedMessageInterceptor bmi = new BufferedMessageInterceptor() {};
        BufferingHttpRequestHandler wrapper = new BufferingHttpRequestHandler(drh, bmi, 4096);

        HttpProxyConnectionHandler handler = new HttpProxyConnectionHandler(wrapper);

        Method method = HttpProxyConnectionHandler.class.getDeclaredMethod(
                "getServerSocket", HttpRequestHandler.class);
        method.setAccessible(true);

        Object result = method.invoke(handler, wrapper);
        assertNull("Should traverse wrapper without exception", result);
    }

    @Test
    public void getServerSocketTraversesCrossPackageHandlerViaReflection() throws Exception {
        DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler();

        // Stub handler that has getWrappedHandler() but is NOT an owasp BufferingHttpRequestHandler.
        // Exercises the reflection fallback path added in Correction B.
        HttpRequestHandler crossPackageWrapper = new HttpRequestHandler() {
            @SuppressWarnings("unused") // called via reflection by getServerSocket()
            public HttpRequestHandler getWrappedHandler() {
                return drh;
            }
            @Override public StreamingResponse handleRequest(InetAddress source, StreamingRequest request, boolean isContinue) { return null; }
            @Override public void dispose() {}
        };

        HttpProxyConnectionHandler handler = new HttpProxyConnectionHandler(crossPackageWrapper);

        Method method = HttpProxyConnectionHandler.class.getDeclaredMethod(
                "getServerSocket", HttpRequestHandler.class);
        method.setAccessible(true);

        Object result = method.invoke(handler, crossPackageWrapper);
        assertNull("Should traverse cross-package wrapper via reflection fallback", result);
    }

    @Test
    public void bufferingHttpRequestHandlerExposesWrappedHandler() {
        DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler();
        BufferedMessageInterceptor bmi = new BufferedMessageInterceptor() {};
        BufferingHttpRequestHandler wrapper = new BufferingHttpRequestHandler(drh, bmi, 4096);

        assertSame("getWrappedHandler should return the inner handler", drh, wrapper.getWrappedHandler());
    }
}
