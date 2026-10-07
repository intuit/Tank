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
  const added = parts.flatMap((raw) => {
    const event = jsonEvent(raw);
    if (event) {
      previous = event.level;
      return event.lines.map((text) => ({ id: id++, text, level: event.level }));
    }
    const level = (LEVEL_PATTERN.exec(raw)?.[1] as Level | undefined) ?? previous;
    previous = level;
    return [{ id: id++, text: raw, level }];
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

interface JsonThrown {
  name?: string;
  message?: string;
  extendedStackTrace?: { class?: string; method?: string; file?: string; line?: number }[];
  cause?: JsonThrown;
}

/**
 * A log4j JsonLayout event (the controller can log as JSON), as the lines the pattern layout would
 * write: "2026-10-07 23:41:50 ERROR Logger - message", then the exception and its frames.
 */
export function jsonEvent(raw: string): { level: Level | undefined; lines: string[] } | undefined {
  if (!raw.startsWith('{"')) return undefined;
  let event: { instant?: { epochSecond?: number; nanoOfSecond?: number }; level?: string; loggerName?: string; message?: string; thrown?: JsonThrown };
  try {
    event = JSON.parse(raw);
  } catch {
    return undefined;
  }
  if (typeof event !== 'object' || event === null || !event.level) return undefined;
  const level = (LEVELS as readonly string[]).includes(event.level) ? (event.level as Level) : undefined;
  const ms = (event.instant?.epochSecond ?? 0) * 1000 + Math.floor((event.instant?.nanoOfSecond ?? 0) / 1e6);
  const time = event.instant ? new Date(ms).toISOString().replace('T', ' ').slice(0, 19) : '';
  const logger = event.loggerName?.split('.').at(-1) ?? '';
  const [first = '', ...rest] = (event.message ?? '').split(/\r?\n/);
  const lines = [`${time} ${event.level.padEnd(5)} ${logger} - ${first}`.trim(), ...rest];
  for (let thrown = event.thrown, caused = false; thrown; thrown = thrown.cause, caused = true) {
    lines.push(`${caused ? 'Caused by: ' : ''}${thrown.name ?? 'Exception'}${thrown.message ? `: ${thrown.message}` : ''}`);
    for (const f of thrown.extendedStackTrace ?? []) {
      lines.push(`\tat ${f.class}.${f.method}(${f.file ?? 'Unknown Source'}${f.line && f.line > 0 ? `:${f.line}` : ''})`);
    }
  }
  return { level, lines };
}
