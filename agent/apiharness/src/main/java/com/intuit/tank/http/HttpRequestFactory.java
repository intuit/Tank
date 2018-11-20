package com.intuit.tank.http;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.http.json.JsonRequest;
import com.intuit.tank.http.json.PlainTextRequest;
import com.intuit.tank.http.keyvalue.KeyValueRequest;
import com.intuit.tank.http.multipart.MultiPartRequest;
import com.intuit.tank.http.xml.XMLRequest;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.settings.AgentConfig;
import org.apache.logging.log4j.message.ObjectMessage;

public class HttpRequestFactory {

    public static BaseRequest getHttpRequest(String format, TankHttpClient httpclient) throws IllegalArgumentException {
        if (format.equalsIgnoreCase(ScriptConstants.XML_TYPE)) {
            return new XMLRequest(httpclient, new TankLogUtil());
        } else if (format.equalsIgnoreCase(ScriptConstants.NVP_TYPE)) {
            return new KeyValueRequest(httpclient, new TankLogUtil());
        } else if (format.equalsIgnoreCase(ScriptConstants.MULTI_PART_TYPE)) {
            return new MultiPartRequest(httpclient, new TankLogUtil());
        } else if (format.equalsIgnoreCase(ScriptConstants.JSON_TYPE)) {
            return new JsonRequest(httpclient, new TankLogUtil());
        } else if (format.equalsIgnoreCase(ScriptConstants.PLAIN_TEXT_TYPE)) {
            return new PlainTextRequest(httpclient, new TankLogUtil());
        } else {
            throw new IllegalArgumentException("unknown request format - " + format);
        }
    }

    public static final class TankLogUtil implements TankHttpLogger {

        @Override
        public ObjectMessage getLogMessage(String msg) {
            return LogUtil.getLogMessage(msg);
        }

        @Override
        public ObjectMessage getLogMessage(String msg, LogEventType type) {
            return LogUtil.getLogMessage(msg, type);
        }

        @Override
        public ObjectMessage getLogMessage(String msg, LogEventType type, LoggingProfile profile) {
            return LogUtil.getLogMessage(msg, type, profile);
        }

        @Override
        public boolean isTextMimeType(String mimeType) {
            return LogUtil.isTextMimeType(mimeType);
        }

        @Override
        public AgentConfig getAgentConfig() {
            return APITestHarness.getInstance().getTankConfig().getAgentConfig();
        }

    }
}
