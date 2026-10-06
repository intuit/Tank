/**
 * Tank log normalization, fingerprinting, redaction, and LLM export helpers.
 * Works in browsers and Node (for unit tests).
 */
(function(root, factory) {
    "use strict";
    if (typeof module === "object" && module.exports) {
        module.exports = factory();
    } else {
        root.TankLogProcessing = factory();
    }
}(typeof self !== "undefined" ? self : this, function() {
    "use strict";

    var LEVELS = ["TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OTHER"];
    var STACK_LINE = /^\s*(at\s+\S+|Caused by:|Suppressed:|\.\.\. \d+ more)/;
    var EXCEPTION_LINE = /^(?:[a-zA-Z_$][\w.$]*(?:Exception|Error|Throwable))(?::\s*(.*))?$/;
    var PLAIN_LINE = /^(\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}:\d{2}(?:[.,]\d+)?(?:\s+[A-Z]{2,4})?)\s+(\w+)\s+([A-Za-z0-9_.$]+)(?::(\d+))?\s+-\s+(.*)$/;
    var ACCESS_LINE = /^(\S+) \S+ \S+ \[([^\]]+)] "(\S+)\s+([^"]*?)\s+[^"]*" (\d{3})(?:\s+(\d+|-))?/;
    var MAP_MESSAGE = /^\{Message=(.*)\}$/;
    var JOB_IN_TEXT = /\bjob(?:Id)?[:=\s]+([0-9a-fA-F-]{6,}|[0-9]{3,})\b/i;
    var INSTANCE_IN_TEXT = /\b(?:instance(?:Id)?|i-)[:=\s]*(i-[0-9a-fA-F]+|[0-9a-fA-F-]{8,})\b/i;
    var TXN_IN_TEXT = /\b(?:transaction(?:Id)?|txn)[:=\s]+([0-9a-fA-F-]{8,})\b/i;

    function firstValue(object, keys) {
        for (var i = 0; i < keys.length; i++) {
            if (object && object[keys[i]] !== undefined && object[keys[i]] !== null && object[keys[i]] !== "") {
                return object[keys[i]];
            }
        }
        return null;
    }

    function numberFrom(value, fallback) {
        if (value === null || value === undefined || value === "") {
            return fallback;
        }
        var parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function parseNestedJson(value) {
        if (typeof value !== "string") {
            return value;
        }
        var trimmed = value.trim();
        if (!trimmed) {
            return value;
        }
        var first = trimmed.charAt(0);
        var last = trimmed.charAt(trimmed.length - 1);
        if ((first !== "{" || last !== "}") && (first !== "[" || last !== "]")) {
            return value;
        }
        try {
            return JSON.parse(trimmed);
        } catch (ignored) {
            return value;
        }
    }

    function messageText(value) {
        if (value === null || value === undefined) {
            return "";
        }
        if (typeof value === "string") {
            return value;
        }
        var nested = firstValue(value, ["message", "Message", "msg"]);
        if (nested !== null) {
            return messageText(nested);
        }
        try {
            return JSON.stringify(value);
        } catch (ignored) {
            return String(value);
        }
    }

    function normalizeLevel(value) {
        if (!value) {
            return "OTHER";
        }
        var level = String(value).toUpperCase();
        if (level === "WARNING") {
            return "WARN";
        }
        if (level === "SEVERE") {
            return "ERROR";
        }
        return LEVELS.indexOf(level) >= 0 ? level : "OTHER";
    }

    function shortLogger(logger) {
        if (!logger) {
            return "";
        }
        var parts = String(logger).split(".");
        return parts[parts.length - 1];
    }

    function isoTimestamp(value) {
        if (value === null || value === undefined || value === "") {
            return "";
        }
        if (typeof value === "number") {
            return new Date(value).toISOString();
        }
        if (typeof value === "object") {
            var epochSecond = firstValue(value, ["epochSecond", "epoch_second"]);
            if (epochSecond !== null) {
                var nanos = numberFrom(firstValue(value, ["nanoOfSecond", "nano_of_second"]), 0);
                return new Date(numberFrom(epochSecond, 0) * 1000 + nanos / 1000000).toISOString();
            }
        }
        var asString = String(value).trim();
        var normalized = asString.replace(" ", "T").replace(",", ".");
        var parsed = new Date(normalized);
        if (!Number.isNaN(parsed.getTime())) {
            return parsed.toISOString();
        }
        parsed = new Date(asString);
        return Number.isNaN(parsed.getTime()) ? asString : parsed.toISOString();
    }

    function displayTimestamp(iso) {
        if (!iso) {
            return "";
        }
        var parsed = new Date(iso);
        if (Number.isNaN(parsed.getTime())) {
            return iso;
        }
        return parsed.toISOString().replace("T", " ").replace("Z", " UTC");
    }

    function pad2(value) {
        return value < 10 ? "0" + value : String(value);
    }

    function pad3(value) {
        if (value < 10) {
            return "00" + value;
        }
        if (value < 100) {
            return "0" + value;
        }
        return String(value);
    }

    /** Compact clock time for dense timeline rows (local timezone). */
    function shortTimestamp(iso) {
        if (!iso) {
            return "";
        }
        var parsed = new Date(iso);
        if (Number.isNaN(parsed.getTime())) {
            return String(iso);
        }
        return pad2(parsed.getHours()) + ":" + pad2(parsed.getMinutes()) + ":"
                + pad2(parsed.getSeconds()) + "." + pad3(parsed.getMilliseconds());
    }

    function shortDate(iso) {
        if (!iso) {
            return "";
        }
        var parsed = new Date(iso);
        if (Number.isNaN(parsed.getTime())) {
            return "";
        }
        return parsed.getFullYear() + "-" + pad2(parsed.getMonth() + 1) + "-" + pad2(parsed.getDate());
    }

    function formatDuration(ms) {
        if (!Number.isFinite(ms) || ms < 0) {
            return "";
        }
        if (ms < 1000) {
            return "+" + Math.round(ms) + "ms";
        }
        if (ms < 60000) {
            return "+" + (ms / 1000).toFixed(ms < 10000 ? 1 : 0) + "s";
        }
        if (ms < 3600000) {
            return "+" + (ms / 60000).toFixed(ms < 600000 ? 1 : 0) + "m";
        }
        return "+" + (ms / 3600000).toFixed(1) + "h";
    }

    function eventEpochMs(event) {
        if (!event || !event.timestamp) {
            return NaN;
        }
        var value = new Date(event.timestamp).getTime();
        return Number.isFinite(value) ? value : NaN;
    }

    function preferKnown(current, candidate) {
        if (!candidate || candidate === "unknown" || candidate === "null") {
            return current || "";
        }
        if (!current || current === "unknown") {
            return String(candidate);
        }
        return current;
    }

    function extractFromText(text, regex) {
        var match = String(text || "").match(regex);
        return match ? match[1] : "";
    }

    function stackFrames(thrown) {
        if (!thrown) {
            return [];
        }
        if (Array.isArray(thrown.extendedStackTrace)) {
            return thrown.extendedStackTrace.slice(0, 12).map(function(frame) {
                if (typeof frame === "string") {
                    return frame;
                }
                return [frame.class, frame.method, frame.line].filter(function(part) {
                    return part !== undefined && part !== null && part !== "";
                }).join(":");
            });
        }
        if (typeof thrown.stackTrace === "string") {
            return thrown.stackTrace.split("\n").slice(0, 12).map(function(line) {
                return line.trim();
            });
        }
        return [];
    }

    function emptyEvent(rawLine) {
        return {
            raw: rawLine || "",
            rawLines: rawLine ? [rawLine] : [],
            timestamp: "",
            timestampDisplay: "",
            level: "OTHER",
            logger: "",
            loggerShort: "",
            thread: "",
            message: rawLine || "",
            source: "plain",
            job: "",
            instance: "",
            transaction: "",
            eventType: "",
            project: "",
            plan: "",
            script: "",
            step: "",
            httpUrl: "",
            httpRtMs: "",
            validationStatus: "",
            exceptionType: "",
            exceptionMessage: "",
            stack: [],
            fields: {},
            fingerprint: "",
            searchText: (rawLine || "").toLowerCase()
        };
    }

    function buildSearchText(event) {
        return [
            event.timestampDisplay,
            event.level,
            event.logger,
            event.thread,
            event.message,
            event.job,
            event.instance,
            event.transaction,
            event.eventType,
            event.project,
            event.plan,
            event.script,
            event.step,
            event.httpUrl,
            event.validationStatus,
            event.exceptionType,
            event.exceptionMessage,
            event.raw
        ].join(" ").toLowerCase();
    }

    function fingerprintFor(event) {
        var template = String(event.message || "")
            .replace(/\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b/gi, "<uuid>")
            .replace(/\bi-[0-9a-f]+\b/gi, "<instance>")
            .replace(/\b\d{1,3}(?:\.\d{1,3}){3}\b/g, "<ip>")
            .replace(/\b\d+\b/g, "<n>")
            .replace(/\s+/g, " ")
            .trim()
            .slice(0, 240);
        return [event.level, event.loggerShort || event.logger, event.eventType || "", template].join("|");
    }

    function finalizeEvent(event) {
        event.loggerShort = shortLogger(event.logger);
        event.timestampDisplay = displayTimestamp(event.timestamp) || event.timestamp;
        if (!event.job) {
            event.job = extractFromText(event.message, JOB_IN_TEXT);
        }
        if (!event.instance) {
            event.instance = extractFromText(event.message, INSTANCE_IN_TEXT);
        }
        if (!event.transaction) {
            event.transaction = extractFromText(event.message, TXN_IN_TEXT);
        }
        event.fingerprint = fingerprintFor(event);
        event.searchText = buildSearchText(event);
        return event;
    }

    function parseJsonEvent(rawLine, parsed) {
        var event = emptyEvent(rawLine);
        event.source = "json";
        var nested = parseNestedJson(firstValue(parsed, ["message", "Message"]));
        var inner = (nested && typeof nested === "object" && !Array.isArray(nested)) ? nested : {};

        event.timestamp = isoTimestamp(firstValue(parsed, ["timestamp", "instant", "time", "@timestamp", "timeMillis"]));
        event.level = normalizeLevel(firstValue(parsed, ["level", "Level", "severity"]));
        event.logger = String(firstValue(parsed, ["loggerName", "logger", "Logger"]) || "");
        event.thread = String(firstValue(parsed, ["thread", "threadName", "Thread", "threadId"]) || "");
        event.message = messageText(nested !== null ? nested : firstValue(parsed, ["message", "Message"])) || rawLine;

        event.job = preferKnown("", firstValue(parsed, ["jobId", "JobId", "job"]));
        event.job = preferKnown(event.job, firstValue(inner, ["JobId", "jobId", "job"]));
        event.instance = preferKnown("", firstValue(parsed, ["instanceId", "InstanceId", "instance"]));
        event.instance = preferKnown(event.instance, firstValue(inner, ["InstanceId", "instanceId"]));
        event.transaction = preferKnown("", firstValue(inner, ["TransactionId", "transactionId", "txn"]));
        event.eventType = String(firstValue(inner, ["EventType", "eventType"]) || "");
        event.project = preferKnown(String(firstValue(parsed, ["projectName", "ProjectName"]) || ""),
                firstValue(inner, ["ProjectName", "projectName"]));
        event.plan = String(firstValue(inner, ["TestPlanName", "plan"]) || "");
        event.script = String(firstValue(inner, ["ScriptName", "script"]) || "");
        event.step = String(firstValue(inner, ["StepName", "step"]) || "");
        event.httpUrl = String(firstValue(inner, ["RequestUrl", "url"]) || "");
        event.httpRtMs = String(firstValue(inner, ["HttpResponseTime", "responseTime"]) || "");
        event.validationStatus = String(firstValue(inner, ["ValidationStatus", "validationStatus"]) || "");

        if (parsed.thrown) {
            event.exceptionType = String(parsed.thrown.name || parsed.thrown.class || "");
            event.exceptionMessage = String(parsed.thrown.message || "");
            event.stack = stackFrames(parsed.thrown);
        }

        event.fields = {
            envelope: Object.keys(parsed).reduce(function(acc, key) {
                if (key !== "message" && key !== "thrown" && key !== "contextMap") {
                    acc[key] = parsed[key];
                }
                return acc;
            }, {}),
            message: inner
        };
        return finalizeEvent(event);
    }

    function parseAccessLogTimestamp(value) {
        // Tomcat: 16/Jul/2026:15:00:00 -0700
        var match = String(value || "").match(
                /^(\d{2})\/([A-Za-z]{3})\/(\d{4}):(\d{2}:\d{2}:\d{2})(?:\s+([+-]\d{4}))?/);
        if (!match) {
            return "";
        }
        var months = {
            Jan: "01", Feb: "02", Mar: "03", Apr: "04", May: "05", Jun: "06",
            Jul: "07", Aug: "08", Sep: "09", Oct: "10", Nov: "11", Dec: "12"
        };
        var month = months[match[2]] || "01";
        var offset = match[5] || "+0000";
        var isoLike = match[3] + "-" + month + "-" + match[1] + "T" + match[4]
                + offset.replace(/([+-]\d{2})(\d{2})/, "$1:$2");
        return isoTimestamp(isoLike);
    }

    function parsePlainEvent(rawLine) {
        var event = emptyEvent(rawLine);
        var mapMatch = rawLine.match(MAP_MESSAGE);
        if (mapMatch) {
            event.source = "map";
            event.message = mapMatch[1];
            event.level = "INFO";
            return finalizeEvent(event);
        }

        var access = rawLine.match(ACCESS_LINE);
        if (access) {
            var status = numberFrom(access[5], 0);
            event.source = "access";
            event.timestamp = parseAccessLogTimestamp(access[2]);
            event.level = status >= 500 ? "ERROR" : status >= 400 ? "WARN" : "INFO";
            event.logger = "access";
            event.httpUrl = access[4] || "";
            event.message = access[3] + " " + access[4] + " → " + access[5]
                    + (access[1] ? " from " + access[1] : "");
            event.eventType = "Http";
            return finalizeEvent(event);
        }

        var match = rawLine.match(PLAIN_LINE);
        if (!match) {
            event.source = "plain";
            event.message = rawLine;
            return finalizeEvent(event);
        }

        event.source = "pattern";
        event.timestamp = isoTimestamp(match[1]);
        event.level = normalizeLevel(match[2]);
        event.logger = match[3];
        event.message = match[5] || "";
        return finalizeEvent(event);
    }

    function parseLine(rawLine) {
        var line = String(rawLine || "").replace(/\r$/, "");
        if (!line) {
            return null;
        }
        try {
            var parsed = JSON.parse(line);
            if (parsed && typeof parsed === "object" && !Array.isArray(parsed)) {
                return parseJsonEvent(line, parsed);
            }
        } catch (ignored) {
            // Fall through to plain parsers.
        }
        return parsePlainEvent(line);
    }

    function isStackContinuation(line) {
        var text = String(line || "");
        return STACK_LINE.test(text) || EXCEPTION_LINE.test(text.trim());
    }

    function attachContinuation(event, line) {
        event.rawLines = event.rawLines || [event.raw];
        event.rawLines.push(line);
        event.raw = event.rawLines.join("\n");
        var trimmed = line.trim();
        var exceptionMatch = trimmed.match(EXCEPTION_LINE);
        if (exceptionMatch && !event.exceptionType) {
            event.exceptionType = trimmed.split(":")[0];
            event.exceptionMessage = (exceptionMatch[1] || "").trim();
        } else {
            event.stack.push(trimmed);
        }
        event.searchText = buildSearchText(event);
        return event;
    }

    /**
     * Convert raw text into normalized events, attaching multiline stack frames.
     */
    function normalizeChunk(text, options) {
        options = options || {};
        var pending = options.pendingLine || "";
        var openEvent = options.openEvent || null;
        var combined = pending + (text || "");
        var lines = combined.split("\n");
        var nextPending = options.flush ? "" : (lines.pop() || "");
        var events = [];

        function closeOpen() {
            if (openEvent) {
                events.push(finalizeEvent(openEvent));
                openEvent = null;
            }
        }

        for (var i = 0; i < lines.length; i++) {
            var line = lines[i].replace(/\r$/, "");
            if (!line) {
                continue;
            }
            if (isStackContinuation(line)) {
                if (openEvent) {
                    attachContinuation(openEvent, line);
                    continue;
                }
                if (events.length) {
                    var previous = events[events.length - 1];
                    if (previous.level === "ERROR" || previous.level === "FATAL" || previous.exceptionType) {
                        attachContinuation(previous, line);
                        continue;
                    }
                }
                // Orphan stack frames are noise for investigation views.
                continue;
            }

            closeOpen();
            var event = parseLine(line);
            if (!event) {
                continue;
            }
            if (event.level === "ERROR" || event.level === "FATAL" || event.exceptionType || event.stack.length) {
                openEvent = event;
            } else {
                events.push(event);
            }
        }

        if (options.flush) {
            closeOpen();
        }

        return {
            events: events,
            pendingLine: nextPending,
            openEvent: openEvent
        };
    }

    function parseQuery(query) {
        var tokens = [];
        var text = String(query || "").trim();
        if (!text) {
            return tokens;
        }
        var parts = text.match(/(?:[^\s"]+|"[^"]*")+/g) || [];
        parts.forEach(function(part) {
            var negated = part.charAt(0) === "-";
            var token = negated ? part.slice(1) : part;
            var colon = token.indexOf(":");
            if (colon > 0) {
                tokens.push({
                    field: token.slice(0, colon).toLowerCase(),
                    value: token.slice(colon + 1).replace(/^"|"$/g, "").toLowerCase(),
                    negated: negated
                });
            } else {
                tokens.push({
                    field: null,
                    value: token.replace(/^"|"$/g, "").toLowerCase(),
                    negated: negated
                });
            }
        });
        return tokens;
    }

    function fieldValue(event, field) {
        switch (field) {
            case "level":
            case "lvl":
                return event.level;
            case "job":
            case "jobid":
                return event.job;
            case "instance":
            case "instanceid":
                return event.instance;
            case "txn":
            case "transaction":
            case "transactionid":
                return event.transaction;
            case "logger":
                return (event.loggerShort + " " + event.logger).toLowerCase();
            case "thread":
                return event.thread;
            case "evt":
            case "event":
            case "eventtype":
                return event.eventType;
            case "project":
                return event.project;
            case "msg":
            case "message":
                return event.message;
            default:
                return null;
        }
    }

    function matchesQuery(event, query) {
        var tokens = typeof query === "string" ? parseQuery(query) : (query || []);
        if (!tokens.length) {
            return true;
        }
        for (var i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            var haystack = token.field ? String(fieldValue(event, token.field) || "").toLowerCase() : event.searchText;
            var hit = haystack.indexOf(token.value) >= 0;
            if (token.negated ? hit : !hit) {
                return false;
            }
        }
        return true;
    }

    function matchesFacets(event, facets) {
        if (!facets) {
            return true;
        }
        var keys = Object.keys(facets);
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            var wanted = facets[key];
            if (!wanted) {
                continue;
            }
            var actual = String(event[key] || "");
            if (actual !== wanted) {
                return false;
            }
        }
        return true;
    }

    function buildPatterns(events, options) {
        options = options || {};
        var max = options.max || 50;
        var groups = Object.create(null);
        events.forEach(function(event, index) {
            var key = event.fingerprint || fingerprintFor(event);
            if (!groups[key]) {
                groups[key] = {
                    id: key,
                    fingerprint: key,
                    count: 0,
                    level: event.level,
                    logger: event.loggerShort || event.logger,
                    eventType: event.eventType,
                    message: event.message,
                    firstTs: event.timestampDisplay || event.timestamp,
                    lastTs: event.timestampDisplay || event.timestamp,
                    sampleIndexes: [],
                    jobs: {},
                    transactions: {}
                };
            }
            var group = groups[key];
            group.count += 1;
            group.lastTs = event.timestampDisplay || event.timestamp || group.lastTs;
            if (group.sampleIndexes.length < 5) {
                group.sampleIndexes.push(index);
            }
            if (event.job) {
                group.jobs[event.job] = true;
            }
            if (event.transaction) {
                group.transactions[event.transaction] = true;
            }
            if (event.eventType === "Validation" && event.transaction) {
                group.validationBurst = true;
            }
        });

        return Object.keys(groups).map(function(key) {
            return groups[key];
        }).sort(function(a, b) {
            return b.count - a.count || String(a.message).localeCompare(String(b.message));
        }).slice(0, max);
    }

    function mergeValidationBursts(events) {
        var result = [];
        var pending = null;

        function flush() {
            if (pending) {
                result.push(pending);
                pending = null;
            }
        }

        events.forEach(function(event) {
            var canMerge = event.eventType === "Validation" && event.transaction;
            if (canMerge && pending && pending.transaction === event.transaction) {
                pending.count = (pending.count || 1) + 1;
                pending.messages = pending.messages || [pending.message];
                pending.messages.push(event.message);
                pending.message = pending.messages.slice(0, 3).join(" | ");
                pending.rawLines = (pending.rawLines || [pending.raw]).concat(event.rawLines || [event.raw]);
                pending.raw = pending.rawLines.join("\n");
                pending.lastTs = event.timestampDisplay || event.timestamp;
                pending.searchText = buildSearchText(pending);
                return;
            }
            flush();
            if (canMerge) {
                pending = Object.assign({}, event, { count: 1, messages: [event.message] });
            } else {
                result.push(event);
            }
        });
        flush();
        return result;
    }

    function facetCounts(events, field, limit) {
        var counts = Object.create(null);
        events.forEach(function(event) {
            var value = String(event[field] || "").trim();
            if (!value || value === "unknown") {
                return;
            }
            counts[value] = (counts[value] || 0) + 1;
        });
        return Object.keys(counts).map(function(value) {
            return { value: value, count: counts[value] };
        }).sort(function(a, b) {
            return b.count - a.count || a.value.localeCompare(b.value);
        }).slice(0, limit || 12);
    }

    function volumeBuckets(events, bucketCount) {
        bucketCount = bucketCount || 24;
        if (!events.length) {
            return { buckets: [], startMs: null, endMs: null, spanMs: 0 };
        }
        var times = events.map(eventEpochMs).filter(function(value) {
            return Number.isFinite(value);
        });
        if (!times.length) {
            return {
                buckets: Array(bucketCount).fill(0).map(function() {
                    return { total: 0, error: 0, warn: 0, info: 0, startMs: null, endMs: null };
                }),
                startMs: null,
                endMs: null,
                spanMs: 0
            };
        }
        var min = Math.min.apply(null, times);
        var max = Math.max.apply(null, times);
        var span = Math.max(1, max - min);
        var bucketWidth = span / bucketCount;
        var buckets = [];
        for (var i = 0; i < bucketCount; i++) {
            var startMs = min + (i * bucketWidth);
            buckets.push({
                total: 0,
                error: 0,
                warn: 0,
                info: 0,
                startMs: startMs,
                endMs: i === bucketCount - 1 ? max : startMs + bucketWidth
            });
        }
        events.forEach(function(event) {
            var ts = eventEpochMs(event);
            if (!Number.isFinite(ts)) {
                return;
            }
            var index = Math.min(bucketCount - 1, Math.floor(((ts - min) / span) * bucketCount));
            buckets[index].total += 1;
            if (event.level === "ERROR" || event.level === "FATAL") {
                buckets[index].error += 1;
            } else if (event.level === "WARN") {
                buckets[index].warn += 1;
            } else if (event.level === "INFO") {
                buckets[index].info += 1;
            }
        });
        return { buckets: buckets, startMs: min, endMs: max, spanMs: span };
    }

    var REDACTION_RULES = [
        { type: "TOKEN", regex: /\bBearer\s+[A-Za-z0-9\-._~+/]+=*/gi },
        { type: "TOKEN", regex: /\beyJ[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\b/g },
        { type: "TOKEN", regex: /\b(?:ghp|gho|ghu|ghs|ghr)_[A-Za-z0-9]{20,}\b/g },
        { type: "TOKEN", regex: /\b(?:sk_live|sk_test|AKIA)[A-Za-z0-9]{16,}\b/g },
        { type: "TOKEN", regex: /\bV1-\d+-[0-9a-zA-Z]{22}\b/g },
        { type: "SECRET", regex: /\b(?:password|pwd|secret|api[_-]?key|access[_-]?key)\s*[:=]\s*([^\s,;]+)/gi },
        { type: "EMAIL", regex: /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/gi },
        { type: "IP", regex: /\b(?:\d{1,3}\.){3}\d{1,3}\b/g },
        { type: "COOKIE", regex: /\b(?:Cookie|Set-Cookie)\s*[:=]\s*([^\n]+)/gi }
    ];

    function redactText(text, state) {
        state = state || { maps: {}, counts: {} };
        var output = String(text || "");
        REDACTION_RULES.forEach(function(rule) {
            output = output.replace(rule.regex, function(match) {
                var key = rule.type + "::" + match.toLowerCase();
                if (!state.maps[key]) {
                    state.counts[rule.type] = (state.counts[rule.type] || 0) + 1;
                    state.maps[key] = "[" + rule.type + "_" + state.counts[rule.type] + "]";
                }
                return state.maps[key];
            });
        });
        return { text: output, state: state };
    }

    function redactEvent(event, state) {
        state = state || { maps: {}, counts: {} };
        var clone = Object.assign({}, event);
        ["message", "raw", "exceptionMessage", "httpUrl", "thread", "step", "script"].forEach(function(field) {
            if (clone[field]) {
                var result = redactText(clone[field], state);
                clone[field] = result.text;
                state = result.state;
            }
        });
        if (Array.isArray(clone.rawLines)) {
            clone.rawLines = clone.rawLines.map(function(line) {
                var result = redactText(line, state);
                state = result.state;
                return result.text;
            });
        }
        if (Array.isArray(clone.stack)) {
            clone.stack = clone.stack.map(function(line) {
                var result = redactText(line, state);
                state = result.state;
                return result.text;
            });
        }
        clone.fingerprint = fingerprintFor(clone);
        clone.searchText = buildSearchText(clone);
        return { event: clone, state: state };
    }

    function levelStats(events) {
        var stats = {};
        LEVELS.forEach(function(level) {
            stats[level] = 0;
        });
        events.forEach(function(event) {
            stats[event.level] = (stats[event.level] || 0) + 1;
        });
        return stats;
    }

    function estimateTokens(text) {
        return Math.max(1, Math.ceil(String(text || "").length / 4));
    }

    function formatCompactBundle(options) {
        options = options || {};
        var events = options.events || [];
        var fileName = options.fileName || "unknown.log";
        var rawLines = options.rawLines || events.length;
        var retained = options.retained || events.length;
        var filters = options.filters || {};
        var redact = options.redact !== false;
        var state = { maps: {}, counts: {} };
        var working = events;

        if (redact) {
            working = events.map(function(event) {
                var result = redactEvent(event, state);
                state = result.state;
                return result.event;
            });
        }

        var patterns = buildPatterns(working, { max: options.patternLimit || 12 });
        var timeline = working.slice(0, options.timelineLimit || 80);
        var stats = levelStats(working);
        var range = "";
        if (working.length) {
            range = (working[0].timestampDisplay || working[0].timestamp || "?")
                    + " .. "
                    + (working[working.length - 1].timestampDisplay || working[working.length - 1].timestamp || "?");
        }

        var lines = [];
        lines.push("TANK_LOG_BUNDLE v1");
        lines.push("scope file=" + fileName
                + " events=" + rawLines
                + " retained=" + retained
                + " exported=" + working.length
                + (range ? " range=" + range : ""));
        lines.push("filters " + JSON.stringify(filters));
        lines.push("stats "
                + Object.keys(stats).filter(function(level) {
                    return stats[level] > 0;
                }).map(function(level) {
                    return level + "=" + stats[level];
                }).join(" "));

        var jobs = facetCounts(working, "job", 5).map(function(item) {
            return item.value;
        });
        var txns = facetCounts(working, "transaction", 5).map(function(item) {
            return item.value;
        });
        if (jobs.length) {
            lines.push("jobs " + jobs.join(","));
        }
        if (txns.length) {
            lines.push("transactions " + txns.join(","));
        }

        lines.push("patterns");
        if (!patterns.length) {
            lines.push("  (none)");
        } else {
            patterns.forEach(function(pattern) {
                lines.push("  x" + pattern.count
                        + " " + pattern.level
                        + (pattern.eventType ? " evt=" + pattern.eventType : "")
                        + " " + (pattern.logger || "unknown")
                        + " | " + String(pattern.message || "").slice(0, 160));
            });
        }

        lines.push("timeline");
        if (!timeline.length) {
            lines.push("  (none)");
        } else {
            timeline.forEach(function(event) {
                lines.push("  "
                        + (event.timestampDisplay || event.timestamp || "—")
                        + " " + event.level
                        + (event.job ? " job=" + event.job : "")
                        + (event.instance ? " instance=" + event.instance : "")
                        + (event.transaction ? " txn=" + event.transaction : "")
                        + " " + (event.loggerShort || event.logger || "unknown")
                        + " | " + String(event.message || "").slice(0, 180));
            });
        }

        if (options.includeEvidence) {
            lines.push("evidence");
            working.slice(0, options.evidenceLimit || 20).forEach(function(event, index) {
                lines.push("  #" + (index + 1));
                (event.rawLines || [event.raw]).forEach(function(line) {
                    lines.push("    " + line);
                });
            });
        }

        var redactionSummary = Object.keys(state.counts).map(function(type) {
            return type + "=" + state.counts[type];
        }).join(" ");
        lines.push("redaction " + (redact ? (redactionSummary || "none") : "disabled"));

        var text = lines.join("\n");
        return {
            text: text,
            characters: text.length,
            tokens: estimateTokens(text),
            redactionCounts: state.counts,
            patternCount: patterns.length,
            eventCount: working.length
        };
    }

    function formatJsonl(events, options) {
        options = options || {};
        var redact = options.redact !== false;
        var state = { maps: {}, counts: {} };
        var lines = events.map(function(event) {
            var current = event;
            if (redact) {
                var result = redactEvent(event, state);
                current = result.event;
                state = result.state;
            }
            return JSON.stringify({
                ts: current.timestamp || current.timestampDisplay,
                lvl: current.level,
                logger: current.loggerShort || current.logger,
                job: current.job || undefined,
                instance: current.instance || undefined,
                txn: current.transaction || undefined,
                evt: current.eventType || undefined,
                thread: current.thread || undefined,
                msg: current.message,
                validation: current.validationStatus || undefined,
                http: current.httpUrl ? { url: current.httpUrl, rt_ms: current.httpRtMs || undefined } : undefined,
                err: current.exceptionType ? {
                    type: current.exceptionType,
                    msg: current.exceptionMessage,
                    stack: current.stack.slice(0, 8)
                } : undefined,
                fingerprint: current.fingerprint,
                count: current.count || 1
            });
        });
        var text = lines.join("\n");
        return {
            text: text,
            characters: text.length,
            tokens: estimateTokens(text),
            redactionCounts: state.counts,
            eventCount: events.length
        };
    }

    function copyText(text) {
        if (typeof navigator !== "undefined" && navigator.clipboard && navigator.clipboard.writeText) {
            return navigator.clipboard.writeText(text);
        }
        return new Promise(function(resolve, reject) {
            try {
                if (typeof document === "undefined") {
                    reject(new Error("Clipboard unavailable"));
                    return;
                }
                var area = document.createElement("textarea");
                area.value = text;
                area.setAttribute("readonly", "readonly");
                area.style.position = "fixed";
                area.style.left = "-9999px";
                document.body.appendChild(area);
                area.select();
                var ok = document.execCommand("copy");
                document.body.removeChild(area);
                if (ok) {
                    resolve();
                } else {
                    reject(new Error("Copy command failed"));
                }
            } catch (error) {
                reject(error);
            }
        });
    }

    return {
        LEVELS: LEVELS,
        parseLine: parseLine,
        normalizeChunk: normalizeChunk,
        parseQuery: parseQuery,
        matchesQuery: matchesQuery,
        matchesFacets: matchesFacets,
        buildPatterns: buildPatterns,
        mergeValidationBursts: mergeValidationBursts,
        facetCounts: facetCounts,
        volumeBuckets: volumeBuckets,
        displayTimestamp: displayTimestamp,
        shortTimestamp: shortTimestamp,
        shortDate: shortDate,
        formatDuration: formatDuration,
        eventEpochMs: eventEpochMs,
        redactText: redactText,
        redactEvent: redactEvent,
        levelStats: levelStats,
        estimateTokens: estimateTokens,
        formatCompactBundle: formatCompactBundle,
        formatJsonl: formatJsonl,
        copyText: copyText,
        fingerprintFor: fingerprintFor,
        isStackContinuation: isStackContinuation
    };
}));
