package com.intuit.tank.tracing;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenTelemetry helpers for controller tracing. Spans are exported by the ADOT Java agent; without the agent the
 * OpenTelemetry API is a no-op, so none of these calls need a guard.
 */
public final class Tracing {

    public static final String INSTRUMENTATION_SCOPE = "com.intuit.tank";

    /** Span attribute the X-Ray exporter reads to decide which attributes become indexed annotations. */
    public static final AttributeKey<List<String>> XRAY_ANNOTATIONS = AttributeKey.stringArrayKey("aws.xray.annotations");

    /** Mapped to the segment user by the X-Ray exporter. */
    public static final AttributeKey<String> ENDUSER_ID = AttributeKey.stringKey("enduser.id");

    // Every key ever annotated; the API can't read a span's attributes back, so each annotate() re-sends the full list.
    // Keys that aren't on a given span are ignored by the exporter.
    private static final Set<String> ANNOTATION_KEYS = ConcurrentHashMap.newKeySet();

    private Tracing() {
    }

    public static Tracer tracer() {
        return GlobalOpenTelemetry.getTracer(INSTRUMENTATION_SCOPE);
    }

    /**
     * Start a span that is a child of the current context. The caller must make it current and end it.
     */
    public static Span startSpan(String name) {
        return tracer().spanBuilder(name).startSpan();
    }

    /**
     * Start a span with no parent, for work that runs outside any request (scheduled passes, background threads).
     * Sampling rules apply to it as they do to an incoming request.
     */
    public static Span startRootSpan(String name) {
        return tracer().spanBuilder(name).setNoParent().startSpan();
    }

    /** Work run inside a span. {@code E} is inferred as {@link RuntimeException} when the body throws nothing checked. */
    @FunctionalInterface
    public interface SpanRunnable<E extends Exception> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface SpanSupplier<T, E extends Exception> {
        T get() throws E;
    }

    /**
     * Run {@code work} in a child span of the current context. An exception that escapes is recorded on the span and
     * rethrown; one the body catches itself can be recorded with {@link #recordException(Throwable)}, since the span is
     * still current inside the body.
     */
    public static <E extends Exception> void inSpan(String name, SpanRunnable<E> work) throws E {
        run(startSpan(name), asSupplier(work));
    }

    public static <T, E extends Exception> T inSpan(String name, SpanSupplier<T, E> work) throws E {
        return run(startSpan(name), work);
    }

    /** Like {@link #inSpan(String, SpanRunnable)}, but in a new trace; see {@link #startRootSpan(String)}. */
    public static <E extends Exception> void inRootSpan(String name, SpanRunnable<E> work) throws E {
        run(startRootSpan(name), asSupplier(work));
    }

    public static <T, E extends Exception> T inRootSpan(String name, SpanSupplier<T, E> work) throws E {
        return run(startRootSpan(name), work);
    }

    private static <E extends Exception> SpanSupplier<Void, E> asSupplier(SpanRunnable<E> work) {
        return () -> {
            work.run();
            return null;
        };
    }

    private static <T, E extends Exception> T run(Span span, SpanSupplier<T, E> work) throws E {
        try (Scope ignored = span.makeCurrent()) {
            return work.get();
        } catch (Throwable t) {
            recordException(span, t);
            throw t;
        } finally {
            span.end();
        }
    }

    public static void recordException(Span span, Throwable t) {
        span.recordException(t);
        span.setStatus(StatusCode.ERROR);
    }

    /** Record the exception on the current span. */
    public static void recordException(Throwable t) {
        recordException(Span.current(), t);
    }

    /**
     * Set an attribute on the current span and index it as an X-Ray annotation, so it can be used in filter
     * expressions such as {@code annotation.jobId = "123"}.
     */
    public static void annotate(String key, String value) {
        if (value == null) {
            return;
        }
        Span span = Span.current();
        span.setAttribute(key, value);
        markAnnotation(span, key);
    }

    public static void annotate(String key, long value) {
        Span span = Span.current();
        span.setAttribute(key, value);
        markAnnotation(span, key);
    }

    public static void annotate(String key, double value) {
        Span span = Span.current();
        span.setAttribute(key, value);
        markAnnotation(span, key);
    }

    public static void setUser(String user) {
        if (user != null) {
            Span.current().setAttribute(ENDUSER_ID, user);
        }
    }

    private static void markAnnotation(Span span, String key) {
        ANNOTATION_KEYS.add(key);
        span.setAttribute(XRAY_ANNOTATIONS, new ArrayList<>(ANNOTATION_KEYS));
    }
}
