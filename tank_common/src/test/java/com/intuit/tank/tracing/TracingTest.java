package com.intuit.tank.tracing;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TracingTest {

    @RegisterExtension
    static final OpenTelemetryExtension otel = OpenTelemetryExtension.create();

    @Test
    public void inSpan_nestsUnderCurrentSpanAndReturnsValue() {
        String result = Tracing.inSpan("outer", () -> Tracing.inSpan("inner", () -> "done"));

        assertEquals("done", result);
        List<SpanData> spans = otel.getSpans();
        assertEquals(2, spans.size());
        SpanData inner = find(spans, "inner");
        SpanData outer = find(spans, "outer");
        assertEquals(outer.getSpanId(), inner.getParentSpanId());
        assertEquals(outer.getTraceId(), inner.getTraceId());
    }

    @Test
    public void inSpan_recordsExceptionAndRethrows() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> Tracing.inSpan("failing", (Tracing.SpanRunnable<RuntimeException>) () -> { throw new IllegalStateException("boom"); }));

        assertEquals("boom", thrown.getMessage());
        SpanData span = find(otel.getSpans(), "failing");
        assertEquals(StatusCode.ERROR, span.getStatus().getStatusCode());
        assertEquals(1, span.getEvents().size());
        assertEquals("exception", span.getEvents().get(0).getName());
    }

    @Test
    public void inSpan_propagatesCheckedException() {
        IOException thrown = assertThrows(IOException.class,
                () -> Tracing.inSpan("io", () -> {
                    if (true) {
                        throw new IOException("disk");
                    }
                    return "unreachable";
                }));

        assertEquals("disk", thrown.getMessage());
        assertEquals(StatusCode.ERROR, find(otel.getSpans(), "io").getStatus().getStatusCode());
    }

    @Test
    public void recordException_insideBodyMarksTheSpan() {
        Tracing.inRootSpan("handled", () -> {
            try {
                throw new IllegalStateException("caught");
            } catch (IllegalStateException e) {
                Tracing.recordException(e);
            }
        });

        SpanData span = find(otel.getSpans(), "handled");
        assertEquals(StatusCode.ERROR, span.getStatus().getStatusCode());
        assertEquals(1, span.getEvents().size());
    }

    @Test
    public void inRootSpan_ignoresCurrentSpan() {
        Tracing.inSpan("request", () -> Tracing.inRootSpan("background", () -> { }));

        SpanData request = find(otel.getSpans(), "request");
        SpanData background = find(otel.getSpans(), "background");
        assertFalse(background.getParentSpanContext().isValid());
        assertNotEquals(request.getTraceId(), background.getTraceId());
    }

    @Test
    public void annotate_setsAttributeAndIndexesKey() {
        Tracing.inSpan("annotated", () -> {
            Tracing.annotate("jobId", "123");
            Tracing.annotate("currentUsers", 42L);
            Tracing.annotate("ignored", (String) null);
        });

        SpanData span = find(otel.getSpans(), "annotated");
        assertEquals("123", span.getAttributes().get(AttributeKey.stringKey("jobId")));
        assertEquals(42L, span.getAttributes().get(AttributeKey.longKey("currentUsers")));
        assertNull(span.getAttributes().get(AttributeKey.stringKey("ignored")));
        List<String> indexed = span.getAttributes().get(Tracing.XRAY_ANNOTATIONS);
        assertTrue(indexed.containsAll(List.of("jobId", "currentUsers")));
        assertFalse(indexed.contains("ignored"));
    }

    @Test
    public void setUser_setsEndUserId() {
        Tracing.inSpan("login", () -> Tracing.setUser("alice"));

        assertEquals("alice", find(otel.getSpans(), "login").getAttributes().get(Tracing.ENDUSER_ID));
    }

    @Test
    public void calls_areSafeWithoutAnActiveSpan() {
        assertDoesNotThrow(() -> {
            Tracing.annotate("jobId", "1");
            Tracing.setUser("bob");
            Tracing.recordException(new RuntimeException());
        });
        assertTrue(otel.getSpans().isEmpty());
    }

    private static SpanData find(List<SpanData> spans, String name) {
        return spans.stream().filter(s -> s.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("No span named " + name + " in " + spans));
    }
}
