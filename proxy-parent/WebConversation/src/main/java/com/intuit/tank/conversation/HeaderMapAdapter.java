package com.intuit.tank.conversation;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JAXB adapter for Map&lt;String, String&gt; &rarr; list of key-value pairs.
 * <p>
 * Raw {@code Map<String,String>} serialization is implementation-dependent across JAXB runtimes.
 * This adapter guarantees portable, clean XML:
 * <pre>
 * &lt;handshakeHeaders&gt;
 *   &lt;header key="Host" value="example.com"/&gt;
 *   &lt;header key="Origin" value="http://example.com"/&gt;
 * &lt;/handshakeHeaders&gt;
 * </pre>
 */
public class HeaderMapAdapter extends XmlAdapter<HeaderMapAdapter.HeaderList, Map<String, String>> {

    @XmlType(name = "headerList", namespace = Namespace.NAMESPACE_V1)
    public static class HeaderList {
        @XmlElement(name = "header", namespace = Namespace.NAMESPACE_V1)
        public List<HeaderEntry> entries = new ArrayList<>();
    }

    @XmlType(name = "headerEntry", namespace = Namespace.NAMESPACE_V1)
    public static class HeaderEntry {
        @jakarta.xml.bind.annotation.XmlAttribute
        public String key;

        @jakarta.xml.bind.annotation.XmlAttribute
        public String value;

        public HeaderEntry() {
        }

        public HeaderEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public Map<String, String> unmarshal(HeaderList headerList) {
        if (headerList == null || headerList.entries == null) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>();
        for (HeaderEntry entry : headerList.entries) {
            map.put(entry.key, entry.value);
        }
        return map;
    }

    @Override
    public HeaderList marshal(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null; // Omit empty headers from XML
        }
        HeaderList list = new HeaderList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list.entries.add(new HeaderEntry(entry.getKey(), entry.getValue()));
        }
        return list;
    }
}
