package com.intuit.tank.script.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.vm.exception.WatsParseException;

class HarReaderTest {

    /** Entries are out of start order on purpose; the websocket, data: and failed entries must be dropped */
    private static final String HAR = """
            {"log": {"version": "1.2", "creator": {"name": "WebInspector", "version": "537.36"},
              "pages": [{"id": "page_1", "title": "Home"}],
              "entries": [
                {"startedDateTime": "2026-11-24T10:00:02.000Z", "_resourceType": "xhr",
                 "request": {"method": "POST", "url": "https://shop.example.com:8443/api/cart?src=web&debug",
                   "httpVersion": "h2",
                   "headers": [
                     {"name": ":authority", "value": "shop.example.com:8443"},
                     {"name": ":method", "value": "POST"},
                     {"name": "content-type", "value": "application/json"},
                     {"name": "cookie", "value": "sid=abc; theme=dark"}],
                   "postData": {"mimeType": "application/json", "text": "{\\"sku\\":\\"42\\",\\"qty\\":2}"}},
                 "response": {"status": 201, "statusText": "", "httpVersion": "h2",
                   "headers": [{"name": "content-type", "value": "application/json; charset=utf-8"},
                               {"name": "set-cookie", "value": "cart=9; Path=/"}],
                   "content": {"mimeType": "application/json", "text": "{\\"ok\\":true}"}}},
                {"startedDateTime": "2026-11-24T10:00:00.000Z",
                 "request": {"method": "GET", "url": "http://shop.example.com/index.html#top",
                   "httpVersion": "HTTP/1.1", "headers": [{"name": "Accept", "value": "text/html"}]},
                 "response": {"status": 200, "statusText": "OK", "httpVersion": "HTTP/1.1",
                   "headers": [{"name": "Content-Type", "value": "text/html"}],
                   "content": {"mimeType": "text/html", "text": "<html>hi</html>"}}},
                {"startedDateTime": "2026-11-24T10:00:01.000Z",
                 "request": {"method": "POST", "url": "http://shop.example.com/login", "httpVersion": "HTTP/1.1",
                   "headers": [{"name": "Content-Type", "value": "application/x-www-form-urlencoded"}],
                   "cookies": [{"name": "visit", "value": "1"}],
                   "postData": {"mimeType": "application/x-www-form-urlencoded",
                     "params": [{"name": "user", "value": "a b"}, {"name": "pw", "value": "x&y"}]}},
                 "response": {"status": 302, "statusText": "Found", "httpVersion": "HTTP/1.1",
                   "headers": [{"name": "Location", "value": "/home"}], "content": {"mimeType": "text/html"}}},
                {"startedDateTime": "2026-11-24T10:00:03.000Z",
                 "request": {"method": "GET", "url": "https://shop.example.com/logo.png", "httpVersion": "h2",
                   "headers": []},
                 "response": {"status": 200, "statusText": "", "httpVersion": "h2",
                   "headers": [{"name": "content-type", "value": "image/png"}],
                   "content": {"mimeType": "image/png", "encoding": "base64", "text": "iVBORw0KGgo="}}},
                {"startedDateTime": "2026-11-24T10:00:04.000Z",
                 "request": {"method": "GET", "url": "wss://shop.example.com/socket", "headers": []},
                 "response": {"status": 101, "headers": [], "content": {}}},
                {"startedDateTime": "2026-11-24T10:00:05.000Z",
                 "request": {"method": "GET", "url": "data:image/gif;base64,R0lG", "headers": []},
                 "response": {"status": 200, "headers": [], "content": {}}},
                {"startedDateTime": "2026-11-24T10:00:06.000Z",
                 "request": {"method": "GET", "url": "https://tracker.example.com/pixel", "headers": []},
                 "response": {"status": 0, "statusText": "", "headers": [], "content": {}}}
              ]}}
            """;

    private static String header(List<Header> headers, String name) {
        Optional<Header> found = headers.stream().filter(h -> h.getKey().equalsIgnoreCase(name)).findFirst();
        return found.map(Header::getValue).orElse(null);
    }

    private static String value(Iterable<RequestData> data, String key) {
        for (RequestData d : data) {
            if (key.equals(d.getKey())) {
                return d.getValue();
            }
        }
        return null;
    }

    @Test
    void dropsEntriesThatAreNotHttpOrHaveNoResponseAndSortsByStart() {
        List<Transaction> transactions = new HarReader().toTransactions(new StringReader(HAR));

        assertEquals(4, transactions.size());
        assertEquals("GET /index.html HTTP/1.1", transactions.get(0).getRequest().getFirstLine());
        assertEquals("POST /login HTTP/1.1", transactions.get(1).getRequest().getFirstLine());
        assertEquals("POST /api/cart?src=web&debug HTTP/2.0", transactions.get(2).getRequest().getFirstLine());
        assertEquals("GET /logo.png HTTP/2.0", transactions.get(3).getRequest().getFirstLine());
    }

    @Test
    void http2PseudoHeadersBecomeHostAndAreNotKeptAsHeaders() {
        Transaction cart = new HarReader().toTransactions(new StringReader(HAR)).get(2);

        assertEquals("shop.example.com:8443", header(cart.getRequest().getHeaders(), "Host"));
        assertNull(header(cart.getRequest().getHeaders(), ":authority"));
        assertNull(header(cart.getRequest().getHeaders(), ":method"));
        assertEquals("https", cart.getRequest().getProtocol().name());
    }

    @Test
    void hostComesFromTheUrlWhenNoHeaderSaysSo() {
        Transaction index = new HarReader().toTransactions(new StringReader(HAR)).get(0);

        assertEquals("shop.example.com", header(index.getRequest().getHeaders(), "Host"));
    }

    @Test
    void emptyStatusTextGetsTheStandardReasonAndHttp2IsNamed() {
        Transaction cart = new HarReader().toTransactions(new StringReader(HAR)).get(2);

        assertEquals("HTTP/2.0 201 Created", cart.getResponse().getFirstLine());
    }

    @Test
    void formParamsBecomeAnEncodedBodyAndCookiesAreSynthesizedWhenTheHeaderIsMissing() {
        Transaction login = new HarReader().toTransactions(new StringReader(HAR)).get(1);

        assertEquals("user=a+b&pw=x%26y", login.getRequest().getBodyAsString());
        assertEquals("visit=1", header(login.getRequest().getHeaders(), "Cookie"));
    }

    @Test
    void binaryAndNonTextResponseBodiesAreLeftOut() {
        List<Transaction> transactions = new HarReader().toTransactions(new StringReader(HAR));

        assertNull(transactions.get(3).getResponse().getBody());
        assertEquals("<html>hi</html>", transactions.get(0).getResponse().getBodyAsString());
    }

    @Test
    void stepsMatchWhatTheProxyRecordingWouldGive() {
        List<ScriptStep> steps = new HarReader().read(HAR);

        assertEquals(4, steps.size());
        ScriptStep cart = steps.get(2);
        assertEquals(3, cart.getStepIndex());
        assertEquals("POST", cart.getMethod());
        assertEquals("https://shop.example.com:8443/api/cart?src=web&debug", cart.getUrl());
        assertEquals("shop.example.com", cart.getHostname());
        assertEquals("https", cart.getProtocol());
        assertEquals("/api/cart", cart.getSimplePath());
        assertEquals("application/json; charset=utf-8", cart.getMimetype());
        assertEquals("json", cart.getReqFormat());
        assertEquals("{\"sku\":\"42\",\"qty\":2}", cart.getPayload());
        assertEquals("web", value(cart.getQueryStrings(), "src"));
        assertEquals("abc", value(cart.getRequestCookies(), "sid"));
        assertEquals("dark", value(cart.getRequestCookies(), "theme"));
        assertEquals("{\"ok\":true}", cart.getResponse());

        ScriptStep login = steps.get(1);
        assertEquals("a b", value(login.getPostDatas(), "user"));
        assertEquals("x&y", value(login.getPostDatas(), "pw"));
    }

    @Test
    void aUrlJavaNetUriRefusesStillGivesAPath() {
        String har = """
                {"log": {"entries": [{"startedDateTime": "2026-11-24T10:00:00Z",
                  "request": {"method": "GET", "url": "https://a.example.com/with space/x?q=a b#frag", "headers": []},
                  "response": {"status": 200, "statusText": "OK", "headers": [], "content": {}}}]}}
                """;

        Transaction t = new HarReader().toTransactions(new StringReader(har)).get(0);

        assertEquals("GET /with space/x?q=a b HTTP/1.1", t.getRequest().getFirstLine());
    }

    @Test
    void entriesWithoutStartTimesKeepTheirFileOrder() {
        String har = """
                {"log": {"entries": [
                  {"request": {"method": "GET", "url": "http://a.example.com/2", "headers": []},
                   "response": {"status": 200, "headers": [], "content": {}}},
                  {"request": {"method": "GET", "url": "http://a.example.com/1", "headers": []},
                   "response": {"status": 200, "headers": [], "content": {}}}]}}
                """;

        List<Transaction> transactions = new HarReader().toTransactions(new StringReader(har));

        assertEquals("GET /2 HTTP/1.1", transactions.get(0).getRequest().getFirstLine());
        assertEquals("GET /1 HTTP/1.1", transactions.get(1).getRequest().getFirstLine());
    }

    @Test
    void looksLikeHarTellsJsonFromXmlAndLeavesTheReaderAlone() throws Exception {
        for (String har : new String[] { "{\"log\":{}}", "\uFEFF \n\t{\"log\":{}}" }) {
            BufferedReader reader = new BufferedReader(new StringReader(har));
            assertTrue(HarReader.looksLikeHar(reader), har);
            assertEquals(har.charAt(0), (char) reader.read());
        }
        BufferedReader xml = new BufferedReader(new StringReader("  <?xml version=\"1.0\"?><session/>"));
        assertFalse(HarReader.looksLikeHar(xml));
        assertEquals(' ', (char) xml.read());
        assertFalse(HarReader.looksLikeHar(new BufferedReader(new StringReader(""))));
    }

    @Test
    void notHarIsRejectedWithAClearMessage() {
        for (String bad : new String[] { "<session/>", "[]", "{\"log\": {}}", "{\"nope\": 1}", "{\"log\": {\"entries\": 5}}", "", "{" }) {
            WatsParseException e = assertThrows(WatsParseException.class,
                    () -> new HarReader().toTransactions(new StringReader(bad)), bad);
            assertTrue(e.getMessage().contains("HAR"), e.getMessage());
        }
    }

    @Test
    void aHarWithNothingUsableIsRejected() {
        String har = """
                {"log": {"entries": [{"request": {"method": "GET", "url": "data:text/plain,hi", "headers": []},
                  "response": {"status": 200, "headers": [], "content": {}}}]}}
                """;

        WatsParseException e = assertThrows(WatsParseException.class,
                () -> new HarReader().toTransactions(new StringReader(har)));

        assertTrue(e.getMessage().contains("no http or https"), e.getMessage());
    }
}
