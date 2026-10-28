package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.script.models.ScriptTO;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ResponseUtilTest {

    @RegisterExtension
    static final OpenTelemetryExtension otel = OpenTelemetryExtension.create();

    @Test
    void getXMLStream_returnsNonNullStreamingBody() {
        StreamingResponseBody body = ResponseUtil.getXMLStream(new ScriptTO());
        assertNotNull(body);
    }

    @Test
    void getXMLStream_marshalsScriptTOToXml() throws Exception {
        StreamingResponseBody body = ResponseUtil.getXMLStream(new ScriptTO());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        String xml = out.toString("UTF-8");
        assertNotNull(xml);
        assertTrue(xml.contains("<?xml"));
        assertEquals("JAXB.Marshal.ScriptTO", otel.getSpans().get(0).getName());
    }
}
