package com.intuit.tank.http.binary;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;

public class BinaryRequest extends BaseRequest {

    private String body;

    public BinaryRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);
        body = "";
        binary = true;
    }

    @Override
    public String getKey(String key) {
        return body;
    }

    @Override
    public void setKey(String key, String value) {
        body = body + value;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

    public void setConvertData() {
        byte[] bytes = body.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
    }

}
