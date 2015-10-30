package com.intuit.tank.http.xml;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;

public class XMLRequest extends BaseRequest {

    private static final String CONTENT_TYPE = "application/xml";
    protected GenericXMLHandler handler = null;

    /**
     * Initializes the XML to an empty string
     */
    public XMLRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);
        this.handler = new GenericXMLHandler();
        setContentType(CONTENT_TYPE);
    }


    /**
     * Finds the node for the XPath expression and sets it to the value
     * 
     * @param key
     *            - The XPath expression to change
     * @param value
     *            - The value to change it to
     */
    public void setKey(String key, String value) {
        handler.SetElementText(key, value);
        this.body = this.handler.toString();
    }

    /**
     * This is used for testing if setkey worked
     * 
     * @param key
     * @return The value associated with the key
     */
    public String getKey(String key) {
        return handler.GetElementText(key);
    }

    public void setNamespace(String name, String value) {
        this.handler.setNamespace(name, value);
    }

}
