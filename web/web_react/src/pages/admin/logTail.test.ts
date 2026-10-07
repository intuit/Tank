import { describe, expect, it } from 'vitest';
import { append, atLeast, EMPTY_TAIL, MAX_LINES } from './logTail';

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
