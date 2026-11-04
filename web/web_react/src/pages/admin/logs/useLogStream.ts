import { useCallback, useEffect, useRef, useState } from 'react';
import { contextPath } from '../../../api/client';
import { normalizeChunk, type LogEvent } from './logProcessing';

/** Events kept, as the JSF viewer keeps them; older ones are counted as omitted */
export const MAX_ENTRIES = 2000;
/** Raw text kept for the Raw view */
export const MAX_RAW_CHARACTERS = 5_000_000;
export const MAX_POLL_SECONDS = 300;
const REQUEST_TIMEOUT_MS = 60_000;

export type StreamStatus = 'loading' | 'live' | 'loaded' | 'paused' | 'error';

export interface LogStream {
  events: LogEvent[];
  raw: string;
  status: StreamStatus;
  statusLabel: string;
  notice: string | undefined;
  /** File size at the last read, in bytes */
  totalLength: number | undefined;
  lastRefreshAt: Date | undefined;
  totalReceivedLines: number;
  omittedEvents: number;
  /** Bumps when events are added, so a view can follow the newest */
  appended: number;
  paused: boolean;
  setPaused: (paused: boolean) => void;
  refresh: () => void;
}

interface Mutable {
  offset: number | null;
  pendingLine: string;
  openEvent: LogEvent | null;
  events: LogEvent[];
  raw: string;
  totalReceivedLines: number;
  omittedEvents: number;
  retryDelay: number;
  stopped: boolean;
  timer: ReturnType<typeof setTimeout> | undefined;
  controller: AbortController | undefined;
}

/**
 * Reads a log file and follows it (log-viewer.js fetchLog): its last lines first, then what's added
 * from the byte offset the server reports, starting over when the log rotates. A poll of 0 seconds
 * reads once.
 */
export function useLogStream(file: string | undefined, initialLines: number, pollSeconds: number): LogStream {
  const pollMs = Math.min(Math.max(0, pollSeconds), MAX_POLL_SECONDS) * 1000;
  const state = useRef<Mutable | undefined>(undefined);
  const [paused, setPausedState] = useState(false);
  const pausedRef = useRef(false);
  const [snapshot, setSnapshot] = useState<Omit<LogStream, 'paused' | 'setPaused' | 'refresh'>>(() => empty());
  const fetchRef = useRef<() => void>(() => undefined);

  const publish = useCallback((changes: Partial<LogStream>) => {
    const s = state.current!;
    setSnapshot((current) => ({
      ...current,
      events: s.events,
      raw: s.raw,
      totalReceivedLines: s.totalReceivedLines,
      omittedEvents: s.omittedEvents,
      ...changes,
    }));
  }, []);

  useEffect(() => {
    if (!file) return;
    const s: Mutable = {
      offset: null,
      pendingLine: '',
      openEvent: null,
      events: [],
      raw: '',
      totalReceivedLines: 0,
      omittedEvents: 0,
      retryDelay: pollMs || 5000,
      stopped: false,
      timer: undefined,
      controller: undefined,
    };
    state.current = s;
    setSnapshot({ ...empty(), notice: `Fetching ${file} …` });

    const schedule = (delay: number) => {
      if (s.stopped || pollMs <= 0) return;
      clearTimeout(s.timer);
      s.timer = setTimeout(() => fetchRef.current(), delay || pollMs);
    };

    const fetchLog = async () => {
      if (s.stopped || s.controller) return;
      if (pausedRef.current) {
        schedule(pollMs);
        return;
      }
      const requested = s.offset === null ? -initialLines : s.offset;
      publish({ status: 'loading', statusLabel: s.offset === null ? 'Loading' : 'Refreshing' });
      const controller = new AbortController();
      s.controller = controller;
      const timeout = setTimeout(() => controller.abort('timeout'), REQUEST_TIMEOUT_MS);
      let restart = false;
      try {
        const response = await fetch(`${contextPath()}/v2/logs/${encodeURIComponent(file)}?from=${requested}`, {
          signal: controller.signal,
          cache: 'no-store',
        });
        if (response.status === 401 || response.status === 403) {
          s.stopped = true;
          publish({ status: 'error', statusLabel: 'Access denied', notice: 'Your session cannot read this log. Sign in again as an administrator.' });
          return;
        }
        if (response.status === 404) {
          s.stopped = true;
          publish({ status: 'error', statusLabel: 'Missing', notice: `${file} isn't on this server.` });
          return;
        }
        if (!response.ok) throw new Error(String(response.status));
        const text = await response.text();
        const total = numberOr(response.headers.get('X-Total-Content-Length'), null);
        const start = numberOr(response.headers.get('X-Content-Start'), requested);
        if (s.offset !== null && requested >= 0 && start < requested) {
          // the log was truncated or rotated: start again from the new file's end
          Object.assign(s, { offset: null, pendingLine: '', openEvent: null, events: [], raw: '', totalReceivedLines: 0, omittedEvents: 0 });
          publish({ notice: 'The log was truncated or rotated. Display restarted from the new file.', appended: 0 });
          restart = true;
          return;
        }
        appendRaw(s, text);
        const added = appendEvents(s, text, pollMs === 0);
        s.offset = total ?? Math.max(0, start) + new TextEncoder().encode(text).length;
        s.retryDelay = pollMs || 5000;
        publish({
          status: pausedRef.current ? 'paused' : pollMs > 0 ? 'live' : 'loaded',
          statusLabel: pausedRef.current ? 'Paused' : pollMs > 0 ? 'Live' : 'Loaded',
          totalLength: total ?? undefined,
          lastRefreshAt: new Date(),
          notice: !text && s.events.length === 0 ? 'The log is empty, or the server returned no bytes.' : undefined,
          ...(added ? { appended: Date.now() } : {}),
        });
      } catch (error) {
        if (s.stopped) return;
        const timedOut = controller.signal.aborted && controller.signal.reason === 'timeout';
        s.retryDelay = Math.min((s.retryDelay || 5000) * 2, 60_000);
        publish({
          status: 'error',
          statusLabel: timedOut ? 'Timed out' : 'Disconnected',
          notice: timedOut
            ? `Reading ${file} timed out after 60 s. Retrying…`
            : `Reading ${file} failed (${(error as Error).message || 'network error'}). Retrying with backoff.`,
        });
      } finally {
        clearTimeout(timeout);
        s.controller = undefined;
        if (!s.stopped) {
          if (restart) void fetchLog();
          else schedule(s.retryDelay);
        }
      }
    };
    fetchRef.current = () => void fetchLog();
    void fetchLog();

    return () => {
      s.stopped = true;
      clearTimeout(s.timer);
      s.controller?.abort();
    };
  }, [file, initialLines, pollMs, publish]);

  const setPaused = useCallback(
    (value: boolean) => {
      pausedRef.current = value;
      setPausedState(value);
      const s = state.current;
      if (!s) return;
      if (value) {
        publish({ status: 'paused', statusLabel: 'Paused' });
      } else {
        clearTimeout(s.timer);
        fetchRef.current();
      }
    },
    [publish],
  );

  const refresh = useCallback(() => {
    const s = state.current;
    if (!s || s.stopped) return;
    clearTimeout(s.timer);
    fetchRef.current();
  }, []);

  return { ...snapshot, paused, setPaused, refresh };
}

function empty(): Omit<LogStream, 'paused' | 'setPaused' | 'refresh'> {
  return {
    events: [],
    raw: '',
    status: 'loading',
    statusLabel: 'Loading',
    notice: undefined,
    totalLength: undefined,
    lastRefreshAt: undefined,
    totalReceivedLines: 0,
    omittedEvents: 0,
    appended: 0,
  };
}

function numberOr<T>(value: string | null, fallback: T): number | T {
  if (value === null || value === '') return fallback;
  const parsed = Number(value.replace(/,/g, ''));
  return Number.isFinite(parsed) ? parsed : fallback;
}

function appendRaw(s: Mutable, text: string) {
  if (!text) return;
  let raw = s.raw + text;
  if (raw.length > MAX_RAW_CHARACTERS) {
    raw = raw.slice(raw.length - MAX_RAW_CHARACTERS);
    const newline = raw.indexOf('\n');
    raw = newline >= 0 ? raw.slice(newline + 1) : raw;
  }
  s.raw = raw;
}

/** @returns how many events were added */
function appendEvents(s: Mutable, text: string, flush: boolean): number {
  const result = normalizeChunk(text, { pendingLine: s.pendingLine, openEvent: s.openEvent, flush });
  s.pendingLine = result.pendingLine;
  s.openEvent = result.openEvent;
  let incoming = result.events;
  if (!incoming.length) return 0;
  s.totalReceivedLines += incoming.reduce((sum, e) => sum + (e.rawLines.length || 1), 0);
  let events = s.events;
  if (incoming.length > MAX_ENTRIES) {
    s.omittedEvents += events.length + incoming.length - MAX_ENTRIES;
    incoming = incoming.slice(incoming.length - MAX_ENTRIES);
    events = [];
  } else if (events.length + incoming.length > MAX_ENTRIES) {
    const remove = events.length + incoming.length - MAX_ENTRIES;
    s.omittedEvents += remove;
    events = events.slice(remove);
  }
  // a new array, so React sees the change
  s.events = [...events, ...incoming];
  return incoming.length;
}
