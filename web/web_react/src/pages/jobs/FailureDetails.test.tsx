import { describe, expect, it } from 'vitest';
import { recentFailures } from './FailureDetails';

const at = (seconds: number, total: number) => ({ at: seconds * 1000, total });

describe('recent failures', () => {
  it('waits for a second refresh', () => {
    expect(recentFailures(undefined)).toBeUndefined();
    expect(recentFailures([at(0, 5)])).toBeUndefined();
  });

  it('counts those added over about the last minute', () => {
    const samples = [at(0, 1), at(30, 4), at(60, 9), at(90, 20)];

    expect(recentFailures(samples)).toEqual({ added: 16, seconds: 60 });
  });

  it('spans the whole history while the page is younger than a minute', () => {
    expect(recentFailures([at(0, 2), at(10, 2), at(20, 5)])).toEqual({ added: 3, seconds: 20 });
  });

  it('spans the last refresh when refreshes are over a minute apart', () => {
    expect(recentFailures([at(0, 3), at(120, 3)])).toEqual({ added: 0, seconds: 120 });
  });
});
