// A port of web_ui/src/test/js/log-processing.test.js, so both viewers read logs the same way
import { describe, expect, it } from 'vitest';
import {
  buildPatterns,
  formatCompactBundle,
  formatDuration,
  formatJsonl,
  isStackContinuation,
  matchesQuery,
  mergeValidationBursts,
  normalizeChunk,
  parseLine,
  parseQuery,
  redactText,
  shortTimestamp,
  volumeBuckets,
  type LogEvent,
} from './logProcessing';

const parse = (line: string) => parseLine(line) as LogEvent;

describe('log processing', () => {
  it('parses Log4j JSON envelope with nested message fields', () => {
    const event = parse(
      JSON.stringify({
        timestamp: '2025-08-20T10:30:05.125-07:00',
        level: 'ERROR',
        loggerName: 'com.intuit.tank.harness.functions.FunctionHandler',
        thread: 'TestPlanRunner-1',
        jobId: '123456',
        instanceId: 'i-0abc',
        message: {
          EventType: 'Validation',
          TransactionId: 'cda37f25-4d49-41ee-a984-59ee0b7786fc',
          Message: 'Failed http validation: value = 200',
          ValidationStatus: 'FAIL',
          RequestUrl: 'https://example.test/login',
        },
      }),
    );
    expect(event.level).toBe('ERROR');
    expect(event.job).toBe('123456');
    expect(event.instance).toBe('i-0abc');
    expect(event.transaction).toBe('cda37f25-4d49-41ee-a984-59ee0b7786fc');
    expect(event.eventType).toBe('Validation');
    expect(event.message).toMatch(/Failed http validation/);
    expect(event.loggerShort).toBe('FunctionHandler');
  });

  it("parses the controller's log4j JsonLayout instant and thrown", () => {
    const event = parse(
      JSON.stringify({
        instant: { epochSecond: 1791416510, nanoOfSecond: 491468103 },
        level: 'ERROR',
        loggerName: 'com.intuit.tank.rest.mvc.rest.services.admin.AdminServiceV2Impl',
        message: 'Error saving user',
        thrown: { name: 'jakarta.persistence.EntityExistsException', message: 'detached entity', extendedStackTrace: [{ class: 'a.B', method: 'save', line: 318 }] },
      }),
    );
    expect(event.timestamp).toBe('2026-10-07T23:41:50.491Z');
    expect(event.exceptionType).toBe('jakarta.persistence.EntityExistsException');
    expect(event.stack).toEqual(['a.B:save:318']);
  });

  it('reads a pattern-layout time, which has no zone, as UTC like the JSON instants beside it', () => {
    const plain = parse('2026-10-07 23:41:50 INFO TransactionContainer:118 - Rolling back');
    const json = parse(JSON.stringify({ instant: { epochSecond: 1791416510, nanoOfSecond: 0 }, level: 'INFO', message: 'x' }));
    expect(plain.timestamp).toBe('2026-10-07T23:41:50.000Z');
    expect(plain.timestamp).toBe(json.timestamp);
    // a time that says its zone keeps it
    expect(parse(JSON.stringify({ timestamp: '2025-08-20T10:30:05.125-07:00', level: 'INFO', message: 'x' })).timestamp).toBe('2025-08-20T17:30:05.125Z');
  });

  it('parses PatternLayout and map-style lines', () => {
    const plain = parse('2025-07-10 16:39:23 INFO TankAPIApplication:51 - Export request acknowledged');
    expect(plain.level).toBe('INFO');
    expect(plain.loggerShort).toBe('TankAPIApplication');
    expect(plain.message).toMatch(/Export request/);

    const map = parse('{Message=checking file /tmp/tank.log}');
    expect(map.source).toBe('map');
    expect(map.message).toMatch(/checking file/);
  });

  it('parses Tomcat access log lines', () => {
    const event = parse('127.0.0.1 - - [16/Jul/2026:15:06:10 -0700] "GET /tank/admin/logs.jsf HTTP/1.1" 200 1234');
    expect(event.source).toBe('access');
    expect(event.level).toBe('INFO');
    expect(event.loggerShort).toBe('access');
    expect(event.message).toMatch(/GET \/tank\/admin\/logs\.jsf/);
    expect(event.timestamp).toBeTruthy();
  });

  it('groups multiline stack traces with parent event', () => {
    const chunk = [
      '2025-07-10 16:39:23 ERROR RequestRunner:100 - boom',
      'java.lang.NullPointerException: cannot read',
      '\tat com.intuit.tank.RequestRunner.run(RequestRunner.java:100)',
      '\tat java.base/java.lang.Thread.run(Thread.java:833)',
      '2025-07-10 16:39:24 INFO TankAPIApplication:51 - recovered',
    ].join('\n');
    const result = normalizeChunk(chunk, { flush: true });
    expect(result.events).toHaveLength(2);
    expect(result.events[0]!.level).toBe('ERROR');
    expect(result.events[0]!.stack.length).toBeGreaterThanOrEqual(2);
    expect(result.events[0]!.rawLines.length).toBeGreaterThanOrEqual(3);
    expect(result.events[1]!.level).toBe('INFO');
  });

  it('holds an unfinished line and an open error across chunks', () => {
    const first = normalizeChunk('2025-07-10 16:39:23 ERROR RequestRunner:100 - boom\n\tat a.B.c(B.java:1)\n2025-07-10 16:39:24 INF');
    expect(first.events).toHaveLength(0);
    expect(first.openEvent?.level).toBe('ERROR');
    expect(first.pendingLine).toBe('2025-07-10 16:39:24 INF');
    const second = normalizeChunk('O Main:1 - next\n', { pendingLine: first.pendingLine, openEvent: first.openEvent });
    expect(second.events.map((e) => e.level)).toEqual(['ERROR', 'INFO']);
  });

  it('extracts job id from controller prose when MDC is unknown', () => {
    const event = parse(
      JSON.stringify({
        level: 'INFO',
        loggerName: 'ExportService',
        jobId: 'unknown',
        message: 'Export request acknowledged for user: ztestuser with jobId: 440553a6-379e-465f-8b0d-29a73d81d6c9',
      }),
    );
    expect(event.job).toBe('440553a6-379e-465f-8b0d-29a73d81d6c9');
  });

  it('field-qualified query matching supports negation', () => {
    const event = parse(JSON.stringify({ level: 'WARN', loggerName: 'org.hibernate.SQL', jobId: '99', message: 'slow query' }));
    expect(matchesQuery(event, 'level:warn job:99')).toBe(true);
    expect(matchesQuery(event, '-logger:hibernate')).toBe(false);
    expect(parseQuery('level:error "failed validation"')).toEqual([
      { field: 'level', value: 'error', negated: false },
      { field: null, value: 'failed validation', negated: false },
    ]);
  });

  it('keeps a quoted phrase with a colon in it a phrase, so Exclude excludes', () => {
    const xray = parse('2026-10-08 00:57:10 ERROR LogErrorContextMissingStrategy:34 - Suppressing AWS X-Ray context missing exception (SegmentNotFoundException): Failed to begin subsegment');
    const other = parse('2026-10-08 00:57:11 INFO Main:1 - started');
    const query = '-"Suppressing AWS X-Ray context missing exception (SegmentNotFoundException): Fail"';
    expect(parseQuery(query)).toEqual([{ field: null, value: 'suppressing aws x-ray context missing exception (segmentnotfoundexception): fail', negated: true }]);
    expect(matchesQuery(xray, query)).toBe(false);
    expect(matchesQuery(other, query)).toBe(true);
    // a plain word before the colon is still a field
    expect(parseQuery('logger:Main')[0]).toEqual({ field: 'logger', value: 'main', negated: false });
  });

  it('fingerprints collapse repeated noise and merge validation bursts', () => {
    const warn = parse(
      JSON.stringify({
        level: 'WARN',
        loggerName: 'com.intuit.tank.vm.settings.BaseCommonsXmlConfig',
        message: 'Child configuration with key oidc-sso has no entry in config file.',
      }),
    );
    expect(buildPatterns([warn, warn, warn])[0]!.count).toBe(3);

    const burst = mergeValidationBursts([
      parse(JSON.stringify({ level: 'ERROR', loggerName: 'RequestRunner', message: { EventType: 'Validation', TransactionId: 'txn-1', Message: 'http failed' } })),
      parse(JSON.stringify({ level: 'ERROR', loggerName: 'RequestRunner', message: { EventType: 'Validation', TransactionId: 'txn-1', Message: 'body failed' } })),
    ]);
    expect(burst).toHaveLength(1);
    expect(burst[0]!.count).toBe(2);
    expect(burst[0]!.message).toMatch(/http failed/);
    expect(burst[0]!.message).toMatch(/body failed/);
  });

  it('redaction uses stable placeholders', () => {
    const first = redactText('Bearer abc.def.ghi contact me@example.com from 10.0.0.8');
    const second = redactText('Bearer abc.def.ghi again', first.state);
    expect(first.text).toMatch(/\[TOKEN_1\]/);
    expect(first.text).toMatch(/\[EMAIL_1\]/);
    expect(first.text).toMatch(/\[IP_1\]/);
    expect(second.text).toBe('[TOKEN_1] again');
  });

  it('compact and jsonl exports include stats and optional redaction', () => {
    const events = [
      parse(
        JSON.stringify({
          timestamp: '2025-08-20T10:30:05.125-07:00',
          level: 'ERROR',
          loggerName: 'RequestRunner',
          jobId: '123',
          message: { EventType: 'Validation', TransactionId: 'txn-1', Message: 'Failed for user me@example.com token Bearer secret.token.value' },
        }),
      ),
      parse(
        JSON.stringify({
          timestamp: '2025-08-20T10:30:06.125-07:00',
          level: 'WARN',
          loggerName: 'BaseCommonsXmlConfig',
          message: 'Child configuration with key oidc-sso has no entry in config file.',
        }),
      ),
    ];
    const compact = formatCompactBundle({ fileName: 'agent.log', events, rawLines: 100, retained: 2, filters: { query: 'level:error' }, redact: true });
    expect(compact.text).toMatch(/TANK_LOG_BUNDLE v1/);
    expect(compact.text).toMatch(/patterns/);
    expect(compact.text).toMatch(/timeline/);
    expect(compact.text).toMatch(/\[EMAIL_1\]/);
    expect(compact.tokens).toBeGreaterThan(0);

    const jsonl = formatJsonl(events, { redact: true });
    expect(jsonl.text.split('\n')).toHaveLength(2);
    expect(jsonl.text).toMatch(/"lvl":"ERROR"/);
    expect(jsonl.text).not.toMatch(/me@example.com/);
    expect(formatJsonl(events, { redact: false }).text).toMatch(/me@example.com/);
  });

  it('stack continuation detector', () => {
    expect(isStackContinuation('\tat com.intuit.tank.Foo.bar(Foo.java:1)')).toBe(true);
    expect(isStackContinuation('Caused by: java.lang.RuntimeException')).toBe(true);
    expect(isStackContinuation('INFO hello')).toBe(false);
  });

  it('orphan stack frames attach to previous error or are dropped', () => {
    const chunk = [
      '2025-07-10 16:39:23 ERROR UserDao:93 - Cannot create JDBC driver',
      '\tat com.intuit.tank.dao.UserDao.findByEmail(UserDao.java:93)',
      '\tat java.base/java.lang.Thread.run(Thread.java:840)',
      '2025-07-10 16:39:24 INFO TankIdentityStore:10 - Attempting to login admin',
    ].join('\n');
    const result = normalizeChunk(chunk, { flush: true });
    expect(result.events).toHaveLength(2);
    expect(result.events[0]!.stack.length).toBeGreaterThanOrEqual(2);
    expect(result.events[1]!.level).toBe('INFO');
  });

  it('volume buckets expose time range metadata', () => {
    const events = [
      parse('2025-07-10 16:39:20 INFO LogViewer:1 - first'),
      parse('2025-07-10 16:39:50 INFO LogViewer:1 - middle'),
      parse('2025-07-10 16:40:20 WARN LogViewer:1 - last'),
    ];
    const volume = volumeBuckets(events, 4);
    expect(volume.startMs).toBeTruthy();
    expect(volume.endMs!).toBeGreaterThan(volume.startMs!);
    expect(volume.buckets).toHaveLength(4);
    expect(volume.buckets.some((bucket) => bucket.total > 0)).toBe(true);
    expect(volume.buckets[0]!.startMs).not.toBeNull();
    expect(formatDuration(1500)).toBe('+1.5s');
    expect(shortTimestamp(events[0]!.timestamp)).toMatch(/^\d{2}:\d{2}:\d{2}\.\d{3}$/);
  });
});
