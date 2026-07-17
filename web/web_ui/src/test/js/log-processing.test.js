"use strict";

const assert = require("assert");
const path = require("path");
const {
    parseLine,
    normalizeChunk,
    parseQuery,
    matchesQuery,
    buildPatterns,
    mergeValidationBursts,
    redactText,
    formatCompactBundle,
    formatJsonl,
    isStackContinuation
} = require(path.resolve(__dirname, "../../main/webapp/resources/js/log-processing.js"));

function test(name, fn) {
    try {
        fn();
        console.log("ok - " + name);
    } catch (error) {
        console.error("fail - " + name);
        console.error(error);
        process.exitCode = 1;
    }
}

test("parses Log4j JSON envelope with nested message fields", function() {
    const line = JSON.stringify({
        timestamp: "2025-08-20T10:30:05.125-07:00",
        level: "ERROR",
        loggerName: "com.intuit.tank.harness.functions.FunctionHandler",
        thread: "TestPlanRunner-1",
        jobId: "123456",
        instanceId: "i-0abc",
        message: {
            EventType: "Validation",
            TransactionId: "cda37f25-4d49-41ee-a984-59ee0b7786fc",
            Message: "Failed http validation: value = 200",
            ValidationStatus: "FAIL",
            RequestUrl: "https://example.test/login"
        }
    });
    const event = parseLine(line);
    assert.strictEqual(event.level, "ERROR");
    assert.strictEqual(event.job, "123456");
    assert.strictEqual(event.instance, "i-0abc");
    assert.strictEqual(event.transaction, "cda37f25-4d49-41ee-a984-59ee0b7786fc");
    assert.strictEqual(event.eventType, "Validation");
    assert.match(event.message, /Failed http validation/);
    assert.strictEqual(event.loggerShort, "FunctionHandler");
});

test("parses PatternLayout and map-style lines", function() {
    const plain = parseLine("2025-07-10 16:39:23 INFO TankAPIApplication:51 - Export request acknowledged");
    assert.strictEqual(plain.level, "INFO");
    assert.strictEqual(plain.loggerShort, "TankAPIApplication");
    assert.match(plain.message, /Export request/);

    const map = parseLine("{Message=checking file /tmp/tank.log}");
    assert.strictEqual(map.source, "map");
    assert.match(map.message, /checking file/);
});

test("parses Tomcat access log lines", function() {
    const event = parseLine(
        '127.0.0.1 - - [16/Jul/2026:15:06:10 -0700] "GET /tank/admin/logs.jsf HTTP/1.1" 200 1234');
    assert.strictEqual(event.source, "access");
    assert.strictEqual(event.level, "INFO");
    assert.strictEqual(event.loggerShort, "access");
    assert.match(event.message, /GET \/tank\/admin\/logs\.jsf/);
    assert.ok(event.timestamp);
});

test("groups multiline stack traces with parent event", function() {
    const chunk = [
        "2025-07-10 16:39:23 ERROR RequestRunner:100 - boom",
        "java.lang.NullPointerException: cannot read",
        "\tat com.intuit.tank.RequestRunner.run(RequestRunner.java:100)",
        "\tat java.base/java.lang.Thread.run(Thread.java:833)",
        "2025-07-10 16:39:24 INFO TankAPIApplication:51 - recovered"
    ].join("\n");
    const result = normalizeChunk(chunk, { flush: true });
    assert.strictEqual(result.events.length, 2);
    assert.strictEqual(result.events[0].level, "ERROR");
    assert.ok(result.events[0].stack.length >= 2);
    assert.ok(result.events[0].rawLines.length >= 3);
    assert.strictEqual(result.events[1].level, "INFO");
});

test("extracts job id from controller prose when MDC is unknown", function() {
    const event = parseLine(JSON.stringify({
        level: "INFO",
        loggerName: "ExportService",
        jobId: "unknown",
        message: "Export request acknowledged for user: ztestuser with jobId: 440553a6-379e-465f-8b0d-29a73d81d6c9"
    }));
    assert.strictEqual(event.job, "440553a6-379e-465f-8b0d-29a73d81d6c9");
});

test("field-qualified query matching supports negation", function() {
    const event = parseLine(JSON.stringify({
        level: "WARN",
        loggerName: "org.hibernate.SQL",
        jobId: "99",
        message: "slow query"
    }));
    assert.strictEqual(matchesQuery(event, "level:warn job:99"), true);
    assert.strictEqual(matchesQuery(event, "-logger:hibernate"), false);
    assert.deepStrictEqual(parseQuery('level:error "failed validation"'), [
        { field: "level", value: "error", negated: false },
        { field: null, value: "failed validation", negated: false }
    ]);
});

test("fingerprints collapse repeated noise and merge validation bursts", function() {
    const warn = parseLine(JSON.stringify({
        level: "WARN",
        loggerName: "com.intuit.tank.vm.settings.BaseCommonsXmlConfig",
        message: "Child configuration with key oidc-sso has no entry in config file."
    }));
    const patterns = buildPatterns([warn, warn, warn]);
    assert.strictEqual(patterns[0].count, 3);

    const burst = mergeValidationBursts([
        parseLine(JSON.stringify({
            level: "ERROR",
            loggerName: "RequestRunner",
            message: { EventType: "Validation", TransactionId: "txn-1", Message: "http failed" }
        })),
        parseLine(JSON.stringify({
            level: "ERROR",
            loggerName: "RequestRunner",
            message: { EventType: "Validation", TransactionId: "txn-1", Message: "body failed" }
        }))
    ]);
    assert.strictEqual(burst.length, 1);
    assert.strictEqual(burst[0].count, 2);
    assert.match(burst[0].message, /http failed/);
    assert.match(burst[0].message, /body failed/);
});

test("redaction uses stable placeholders", function() {
    const first = redactText("Bearer abc.def.ghi contact me@example.com from 10.0.0.8");
    const second = redactText("Bearer abc.def.ghi again", first.state);
    assert.match(first.text, /\[TOKEN_1\]/);
    assert.match(first.text, /\[EMAIL_1\]/);
    assert.match(first.text, /\[IP_1\]/);
    assert.strictEqual(second.text, "[TOKEN_1] again");
});

test("compact and jsonl exports include stats and optional redaction", function() {
    const events = [
        parseLine(JSON.stringify({
            timestamp: "2025-08-20T10:30:05.125-07:00",
            level: "ERROR",
            loggerName: "RequestRunner",
            jobId: "123",
            message: {
                EventType: "Validation",
                TransactionId: "txn-1",
                Message: "Failed for user me@example.com token Bearer secret.token.value"
            }
        })),
        parseLine(JSON.stringify({
            timestamp: "2025-08-20T10:30:06.125-07:00",
            level: "WARN",
            loggerName: "BaseCommonsXmlConfig",
            message: "Child configuration with key oidc-sso has no entry in config file."
        }))
    ];
    const compact = formatCompactBundle({
        fileName: "agent.log",
        events: events,
        rawLines: 100,
        retained: 2,
        filters: { query: "level:error" },
        redact: true
    });
    assert.match(compact.text, /TANK_LOG_BUNDLE v1/);
    assert.match(compact.text, /patterns/);
    assert.match(compact.text, /timeline/);
    assert.match(compact.text, /\[EMAIL_1\]/);
    assert.ok(compact.tokens > 0);

    const jsonl = formatJsonl(events, { redact: true });
    assert.strictEqual(jsonl.text.split("\n").length, 2);
    assert.match(jsonl.text, /"lvl":"ERROR"/);
    assert.doesNotMatch(jsonl.text, /me@example.com/);
});

test("stack continuation detector", function() {
    assert.strictEqual(isStackContinuation("\tat com.intuit.tank.Foo.bar(Foo.java:1)"), true);
    assert.strictEqual(isStackContinuation("Caused by: java.lang.RuntimeException"), true);
    assert.strictEqual(isStackContinuation("INFO hello"), false);
});

test("orphan stack frames attach to previous error or are dropped", function() {
    const chunk = [
        "2025-07-10 16:39:23 ERROR UserDao:93 - Cannot create JDBC driver",
        "\tat com.intuit.tank.dao.UserDao.findByEmail(UserDao.java:93)",
        "\tat java.base/java.lang.Thread.run(Thread.java:840)",
        "2025-07-10 16:39:24 INFO TankIdentityStore:10 - Attempting to login admin"
    ].join("\n");
    const result = normalizeChunk(chunk, { flush: true });
    assert.strictEqual(result.events.length, 2);
    assert.ok(result.events[0].stack.length >= 2);
    assert.strictEqual(result.events[1].level, "INFO");
});

if (!process.exitCode) {
    console.log("All log-processing tests passed.");
}
