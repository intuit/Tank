package com.intuit.tank.util;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.intuit.tank.conversation.Session;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.conversation.WebSocketTransaction;

import static org.junit.jupiter.api.Assertions.*;

public class WebConversationJaxbParseXMLTest {

    private static final String MIXED_SESSION_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<sns:session xmlns:sns=\"urn:proxy/conversation/v1\" collapseRedirects=\"true\">\n" +
            "  <sns:transaction>\n" +
            "    <sns:request>\n" +
            "      <sns:firstLine>GET /index.html HTTP/1.1</sns:firstLine>\n" +
            "    </sns:request>\n" +
            "    <sns:response>\n" +
            "      <sns:firstLine>HTTP/1.1 200 OK</sns:firstLine>\n" +
            "    </sns:response>\n" +
            "  </sns:transaction>\n" +
            "  <sns:webSocketTransaction url=\"ws://example.com/chat\" protocol=\"ws\">\n" +
            "    <sns:messages>\n" +
            "      <sns:message fromClient=\"true\" type=\"TEXT\" timestamp=\"1000\">\n" +
            "        <sns:content>aGVsbG8=</sns:content>\n" +
            "      </sns:message>\n" +
            "    </sns:messages>\n" +
            "  </sns:webSocketTransaction>\n" +
            "</sns:session>\n";

    @Test
    @DisplayName("parseSession returns Session with both HTTP and WS transactions")
    public void testParseSessionWithMixedContent() throws Exception {
        WebConversationJaxbParseXML parser = new WebConversationJaxbParseXML();
        Session session = parser.parseSession(new StringReader(MIXED_SESSION_XML));

        assertNotNull(session);
        assertEquals(1, session.getTransactions().size(), "Should have 1 HTTP transaction");
        assertEquals(1, session.getWebSocketTransactions().size(), "Should have 1 WS transaction");

        WebSocketTransaction wsTx = session.getWebSocketTransactions().get(0);
        assertEquals("ws://example.com/chat", wsTx.getUrl());
        assertEquals(1, wsTx.getMessageCount());
        assertTrue(wsTx.getMessages().get(0).isFromClient());
    }

    @Test
    @DisplayName("parseSession returns empty WS list when no WS transactions present")
    public void testParseSessionHttpOnly() throws Exception {
        String httpOnlyXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<sns:session xmlns:sns=\"urn:proxy/conversation/v1\" collapseRedirects=\"false\">\n" +
                "  <sns:transaction>\n" +
                "    <sns:request>\n" +
                "      <sns:firstLine>GET / HTTP/1.1</sns:firstLine>\n" +
                "    </sns:request>\n" +
                "    <sns:response>\n" +
                "      <sns:firstLine>HTTP/1.1 200 OK</sns:firstLine>\n" +
                "    </sns:response>\n" +
                "  </sns:transaction>\n" +
                "</sns:session>\n";

        WebConversationJaxbParseXML parser = new WebConversationJaxbParseXML();
        Session session = parser.parseSession(new StringReader(httpOnlyXml));

        assertNotNull(session);
        assertEquals(1, session.getTransactions().size());
        assertNotNull(session.getWebSocketTransactions());
        assertEquals(0, session.getWebSocketTransactions().size());
    }

    @Test
    @DisplayName("existing parse() method still works unchanged")
    public void testParseBackwardCompatibility() throws Exception {
        WebConversationJaxbParseXML parser = new WebConversationJaxbParseXML();
        List<Transaction> transactions = parser.parse(new StringReader(MIXED_SESSION_XML));

        assertEquals(1, transactions.size());
    }
}
