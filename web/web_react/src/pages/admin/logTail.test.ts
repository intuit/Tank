import { describe, expect, it } from 'vitest';
import { append, atLeast, EMPTY_TAIL, jsonEvent, MAX_LINES } from './logTail';

describe('log tail', () => {
  it('keeps a half-written line until the rest arrives', () => {
    const first = append(EMPTY_TAIL, '2026-10-07 INFO started\n2026-10-07 WA', 120);
    expect(first.lines.map((l) => l.text)).toEqual(['2026-10-07 INFO started']);
    expect(first.partial).toBe('2026-10-07 WA');
    const second = append(first, 'RN slow\n', 160);
    expect(second.lines.map((l) => [l.text, l.level])).toEqual([
      ['2026-10-07 INFO started', 'INFO'],
      ['2026-10-07 WARN slow', 'WARN'],
    ]);
    expect(second.offset).toBe(160);
  });

  it('gives stack trace lines the level above them', () => {
    const tail = append(EMPTY_TAIL, 'ERROR failed\njava.lang.IllegalStateException: x\n\tat a.b(C.java:1)\nINFO next\n', 0);
    expect(tail.lines.map((l) => l.level)).toEqual(['ERROR', 'ERROR', 'ERROR', 'INFO']);
    expect(tail.lines.filter((l) => atLeast(l.level, 'WARN'))).toHaveLength(3);
  });

  it(`keeps the last ${MAX_LINES} lines, with ids that keep counting`, () => {
    const text = Array.from({ length: MAX_LINES + 5 }, (_, i) => `INFO line ${i}`).join('\n') + '\n';
    const tail = append(EMPTY_TAIL, text, 0);
    expect(tail.lines).toHaveLength(MAX_LINES);
    expect(tail.lines[0]).toMatchObject({ id: 5, text: 'INFO line 5' });
  });

  it('compares severities', () => {
    expect(atLeast('ERROR', 'WARN')).toBe(true);
    expect(atLeast('INFO', 'WARN')).toBe(false);
    expect(atLeast(undefined, 'WARN')).toBe(false);
    expect(atLeast(undefined, undefined)).toBe(true);
  });
});

describe('JSON log events', () => {
  const EVENT = JSON.stringify({
    instant: { epochSecond: 1791416510, nanoOfSecond: 491468103 },
    thread: 'http-nio-8080-virt-350',
    level: 'ERROR',
    loggerName: 'com.intuit.tank.rest.mvc.rest.services.admin.AdminServiceV2Impl',
    message: 'Error saving user for carol: detached entity passed to persist',
    thrown: {
      name: 'jakarta.persistence.EntityExistsException',
      message: 'detached entity passed to persist',
      extendedStackTrace: [{ class: 'com.intuit.tank.dao.BaseDao', method: 'saveOrUpdate', file: 'BaseDao.java', line: 153 }],
      cause: { name: 'org.hibernate.PersistentObjectException', extendedStackTrace: [{ class: 'org.hibernate.Impl', method: 'persist', line: -1 }] },
    },
  });

  it('reads as the pattern layout would write it', () => {
    expect(jsonEvent(EVENT)).toEqual({
      level: 'ERROR',
      lines: [
        '2026-10-07 23:41:50 ERROR AdminServiceV2Impl - Error saving user for carol: detached entity passed to persist',
        'jakarta.persistence.EntityExistsException: detached entity passed to persist',
        '\tat com.intuit.tank.dao.BaseDao.saveOrUpdate(BaseDao.java:153)',
        'Caused by: org.hibernate.PersistentObjectException',
        '\tat org.hibernate.Impl.persist(Unknown Source)',
      ],
    });
  });

  it('leaves other lines as they are', () => {
    expect(jsonEvent('{not json')).toBeUndefined();
    expect(jsonEvent('{"a":1}')).toBeUndefined();
    expect(jsonEvent('2026-10-07 INFO plain')).toBeUndefined();
  });

  it('mixes with plain lines in a tail, every line of an event at its level', () => {
    const tail = append(EMPTY_TAIL, `2026-10-07 23:41:50 INFO  Tx - rolled back\n${EVENT}\n`, 0);
    expect(tail.lines).toHaveLength(6);
    expect(tail.lines.map((l) => l.level)).toEqual(['INFO', 'ERROR', 'ERROR', 'ERROR', 'ERROR', 'ERROR']);
  });
});
