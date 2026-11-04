import { describe, expect, it } from 'vitest';
import { byStep, formatDuration, searchQuery, sectionLabel } from './search';

describe('step search', () => {
  it('finds text anywhere unless the query has wildcards', () => {
    expect(searchQuery(' store.test ', true)).toBe('*store.test*');
    expect(searchQuery('store.*', true)).toBe('store.*');
    // the server escapes ?, so it's text to find, not a wildcard (RegexUtil.wildcardToRegexp)
    expect(searchQuery('page?id', true)).toBe('*page?id*');
    expect(searchQuery('store.test', false)).toBe('store.test');
  });

  it('groups matches by step, in order, without repeats', () => {
    const grouped = byStep([
      { uuid: 'b', position: 3, section: 'host', value: 'store.test' },
      { uuid: 'a', position: 0, section: 'host', value: 'store.test' },
      { uuid: 'b', position: 3, section: 'host', value: 'store.test' },
      { uuid: 'b', position: 3, section: 'requestHeaderValue', key: 'Host', value: 'store.test' },
    ]);
    expect(grouped.map((s) => [s.uuid, s.position, s.matches.length])).toEqual([
      ['a', 1, 1],
      ['b', 4, 2],
    ]);
    expect(sectionLabel('requestHeaderValue')).toBe('Request header value');
  });

  it('formats durations', () => {
    expect(formatDuration(0)).toBe('0 s');
    expect(formatDuration(250)).toBe('250 ms');
    expect(formatDuration(4_000)).toBe('4 s');
    expect(formatDuration(65_000)).toBe('1 min 5 s');
    expect(formatDuration(3_600_000)).toBe('1 h');
  });
});
