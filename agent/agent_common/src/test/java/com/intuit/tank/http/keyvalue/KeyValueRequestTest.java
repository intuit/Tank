package com.intuit.tank.http.keyvalue;

import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class KeyValueRequestTest {
    @Test
    public void testSetAndGetKey() {
        TankHttpClient client = mock(TankHttpClient.class);
        TankHttpLogger logUtil = mock(TankHttpLogger.class);
        KeyValueRequest keyValueRequest = new KeyValueRequest(client, logUtil);
        String key = "key";
        String value = "value";
        keyValueRequest.setKey(key, value);
        assertEquals(value, keyValueRequest.getKey(key));
    }

    @Test
    public void testGetBody() {
        TankHttpClient client = mock(TankHttpClient.class);
        TankHttpLogger logUtil = mock(TankHttpLogger.class);
        KeyValueRequest keyValueRequest = new KeyValueRequest(client, logUtil);
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        keyValueRequest.setKey(key1, value1);
        keyValueRequest.setKey(key2, value2);
        assertEquals("key1=value1&key2=value2", keyValueRequest.getBody());
    }

    @Test
    public void testRemoveKey() {
        TankHttpClient client = mock(TankHttpClient.class);
        TankHttpLogger logUtil = mock(TankHttpLogger.class);
        KeyValueRequest keyValueRequest = new KeyValueRequest(client, logUtil);
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        keyValueRequest.setKey(key1, value1);
        keyValueRequest.setKey(key2, value2);
        keyValueRequest.removeKey(key1);
        assertEquals(null, keyValueRequest.getKey(key1));
        assertEquals(value2, keyValueRequest.getKey(key2));
    }

    @Test
    public void testSetNameSpace() {
        TankHttpClient client = mock(TankHttpClient.class);
        TankHttpLogger logUtil = mock(TankHttpLogger.class);
        KeyValueRequest keyValueRequest = new KeyValueRequest(client, logUtil);
        keyValueRequest.setNamespace("", "");
    }

    @Test
    public void testSetNullKeyAndValue() {
        TankHttpClient client = mock(TankHttpClient.class);
        TankHttpLogger logUtil = mock(TankHttpLogger.class);
        KeyValueRequest keyValueRequest = new KeyValueRequest(client, logUtil);
        String key1 = null;
        String value1 = "value1";
        String key2 = "key2";
        String value2 = null;
        keyValueRequest.setKey(key1, value1);
        keyValueRequest.setKey(key2, value2);
        assertEquals("=value1&key2=", keyValueRequest.getBody());
    }
}
