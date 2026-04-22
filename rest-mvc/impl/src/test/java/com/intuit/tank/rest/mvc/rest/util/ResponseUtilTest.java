package com.intuit.tank.rest.mvc.rest.util;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.intuit.tank.script.models.ScriptTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

class ResponseUtilTest {

    @Test
    void getXMLStream_returnsNonNullStreamingBody() {
        StreamingResponseBody body = ResponseUtil.getXMLStream(new ScriptTO());
        assertNotNull(body);
    }

    @Test
    void getXMLStream_marshalsScriptTOToXml() throws Exception {
        Subsegment mockSubsegment = mock(Subsegment.class);

        try (MockedStatic<AWSXRay> xrayMock = Mockito.mockStatic(AWSXRay.class)) {
            xrayMock.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            StreamingResponseBody body = ResponseUtil.getXMLStream(new ScriptTO());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            body.writeTo(out);

            String xml = out.toString("UTF-8");
            assertNotNull(xml);
            assertTrue(xml.contains("<?xml"));
        }
    }
}
