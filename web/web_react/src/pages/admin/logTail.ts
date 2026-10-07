/** Lines the viewer keeps, as the JSF log viewer did; older ones drop off the top */
export const MAX_LINES = 2000;

export const LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'] as const;
export type Level = (typeof LEVELS)[number];

export interface LogLine {
  /** Counts every line read, so React keys survive lines dropping off the top */
  id: number;
  text: string;
  level: Level | undefined;
}

export interface Tail {
  lines: LogLine[];
  /** The start of a line the file hasn't finished writing */
  partial: string;
  /** Where the next read starts, in bytes */
  offset: number;
  nextId: number;
}

export const EMPTY_TAIL: Tail = { lines: [], partial: '', offset: 0, nextId: 0 };

const LEVEL_PATTERN = new RegExp(`\\b(${LEVELS.join('|')})\\b`);

/**
 * Adds text read from the log to the tail. A line without a level (a stack trace's "at ..." lines)
 * takes the level of the line before, so filtering to errors keeps their traces.
 */
export function append(tail: Tail, chunk: string, offset: number): Tail {
  const parts = (tail.partial + chunk).split(/\r?\n/);
  const partial = parts.pop() ?? '';
  let id = tail.nextId;
  let previous = tail.lines.at(-1)?.level;
  const added = parts.map((text) => {
    const level = (LEVEL_PATTERN.exec(text)?.[1] as Level | undefined) ?? previous;
    previous = level;
    return { id: id++, text, level };
  });
  const lines = [...tail.lines, ...added];
  return { lines: lines.length > MAX_LINES ? lines.slice(-MAX_LINES) : lines, partial, offset, nextId: id };
}

/** Whether a line is at least as severe as the floor */
export function atLeast(level: Level | undefined, floor: Level | undefined): boolean {
  if (!floor) return true;
  return !!level && LEVELS.indexOf(level) >= LEVELS.indexOf(floor);
}

/** The headers a read of /v2/logs/{file} returns: the file's size and where the text read starts */
export function readPosition(headers: Headers): { total: number; start: number } {
  return { total: Number(headers.get('X-Total-Content-Length') ?? 0), start: Number(headers.get('X-Content-Start') ?? 0) };
}
