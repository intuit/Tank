/**
 * Log normalization, fingerprinting, search, redaction and LLM export for the log workspace: a port of
 * the JSF viewer's resources/js/log-processing.js, kept close to it so both read logs the same way.
 */

export const LEVELS = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL', 'OTHER'] as const;
export type Level = (typeof LEVELS)[number];

export interface LogEvent {
  raw: string;
  rawLines: string[];
  timestamp: string;
  timestampDisplay: string;
  level: Level;
  logger: string;
  loggerShort: string;
  thread: string;
  message: string;
  source: 'plain' | 'json' | 'map' | 'access' | 'pattern';
  job: string;
  instance: string;
  transaction: string;
  eventType: string;
  project: string;
  plan: string;
  script: string;
  step: string;
  httpUrl: string;
  httpRtMs: string;
  validationStatus: string;
  exceptionType: string;
  exceptionMessage: string;
  stack: string[];
  fields: Record<string, unknown>;
  fingerprint: string;
  searchText: string;
  /** Set when validation events of one transaction are merged */
  count?: number;
  messages?: string[];
  lastTs?: string;
}

/** Fields a facet filters on */
export type FacetField = 'job' | 'instance' | 'transaction' | 'eventType' | 'loggerShort';
export type Facets = Partial<Record<FacetField, string>>;

const STACK_LINE = /^\s*(at\s+\S+|Caused by:|Suppressed:|\.\.\. \d+ more)/;
const EXCEPTION_LINE = /^(?:[a-zA-Z_$][\w.$]*(?:Exception|Error|Throwable))(?::\s*(.*))?$/;
const PLAIN_LINE = /^(\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}:\d{2}(?:[.,]\d+)?(?:\s+[A-Z]{2,4})?)\s+(\w+)\s+([A-Za-z0-9_.$]+)(?::(\d+))?\s+-\s+(.*)$/;
const ACCESS_LINE = /^(\S+) \S+ \S+ \[([^\]]+)] "(\S+)\s+([^"]*?)\s+[^"]*" (\d{3})(?:\s+(\d+|-))?/;
const MAP_MESSAGE = /^\{Message=(.*)\}$/;
const JOB_IN_TEXT = /\bjob(?:Id)?[:=\s]+([0-9a-fA-F-]{6,}|[0-9]{3,})\b/i;
const INSTANCE_IN_TEXT = /\b(?:instance(?:Id)?|i-)[:=\s]*(i-[0-9a-fA-F]+|[0-9a-fA-F-]{8,})\b/i;
const TXN_IN_TEXT = /\b(?:transaction(?:Id)?|txn)[:=\s]+([0-9a-fA-F-]{8,})\b/i;

type Json = Record<string, unknown>;

function firstValue(object: unknown, keys: string[]): unknown {
  if (!object || typeof object !== 'object') return null;
  const record = object as Json;
  for (const key of keys) {
    const value = record[key];
    if (value !== undefined && value !== null && value !== '') return value;
  }
  return null;
}

function numberFrom(value: unknown, fallback: number): number {
  if (value === null || value === undefined || value === '') return fallback;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function parseNestedJson(value: unknown): unknown {
  if (typeof value !== 'string') return value;
  const trimmed = value.trim();
  if (!trimmed) return value;
  const first = trimmed.charAt(0);
  const last = trimmed.charAt(trimmed.length - 1);
  if ((first !== '{' || last !== '}') && (first !== '[' || last !== ']')) return value;
  try {
    return JSON.parse(trimmed);
  } catch {
    return value;
  }
}

function messageText(value: unknown): string {
  if (value === null || value === undefined) return '';
  if (typeof value === 'string') return value;
  const nested = firstValue(value, ['message', 'Message', 'msg']);
  if (nested !== null) return messageText(nested);
  try {
    return JSON.stringify(value);
  } catch {
    return String(value);
  }
}

function normalizeLevel(value: unknown): Level {
  if (!value) return 'OTHER';
  const level = String(value).toUpperCase();
  if (level === 'WARNING') return 'WARN';
  if (level === 'SEVERE') return 'ERROR';
  return (LEVELS as readonly string[]).includes(level) ? (level as Level) : 'OTHER';
}

function shortLogger(logger: string): string {
  if (!logger) return '';
  return logger.split('.').at(-1) ?? '';
}

function isoTimestamp(value: unknown): string {
  if (value === null || value === undefined || value === '') return '';
  if (typeof value === 'number') return new Date(value).toISOString();
  if (typeof value === 'object') {
    const epochSecond = firstValue(value, ['epochSecond', 'epoch_second']);
    if (epochSecond !== null) {
      const nanos = numberFrom(firstValue(value, ['nanoOfSecond', 'nano_of_second']), 0);
      return new Date(numberFrom(epochSecond, 0) * 1000 + nanos / 1000000).toISOString();
    }
  }
  const asString = String(value).trim();
  let normalized = asString.replace(' ', 'T').replace(',', '.');
  // A pattern-layout time has no zone. The controller writes it in UTC (on QA, a plain line and a JSON
  // event of the same moment agree only read so); read as local time, plain and JSON events drift
  // apart by the browser's offset. The JSF viewer read it as local.
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?$/.test(normalized)) normalized += 'Z';
  let parsed = new Date(normalized);
  if (!Number.isNaN(parsed.getTime())) return parsed.toISOString();
  parsed = new Date(asString);
  return Number.isNaN(parsed.getTime()) ? asString : parsed.toISOString();
}

export function displayTimestamp(iso: string): string {
  if (!iso) return '';
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) return iso;
  return parsed.toISOString().replace('T', ' ').replace('Z', ' UTC');
}

const pad2 = (value: number) => String(value).padStart(2, '0');
const pad3 = (value: number) => String(value).padStart(3, '0');

/** Compact clock time for dense timeline rows (local timezone) */
export function shortTimestamp(iso: string): string {
  if (!iso) return '';
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) return String(iso);
  return `${pad2(parsed.getHours())}:${pad2(parsed.getMinutes())}:${pad2(parsed.getSeconds())}.${pad3(parsed.getMilliseconds())}`;
}

export function shortDate(iso: string): string {
  if (!iso) return '';
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) return '';
  return `${parsed.getFullYear()}-${pad2(parsed.getMonth() + 1)}-${pad2(parsed.getDate())}`;
}

export function formatDuration(ms: number): string {
  if (!Number.isFinite(ms) || ms < 0) return '';
  if (ms < 1000) return `+${Math.round(ms)}ms`;
  if (ms < 60000) return `+${(ms / 1000).toFixed(ms < 10000 ? 1 : 0)}s`;
  if (ms < 3600000) return `+${(ms / 60000).toFixed(ms < 600000 ? 1 : 0)}m`;
  return `+${(ms / 3600000).toFixed(1)}h`;
}

export function eventEpochMs(event: LogEvent | null | undefined): number {
  if (!event?.timestamp) return NaN;
  const value = new Date(event.timestamp).getTime();
  return Number.isFinite(value) ? value : NaN;
}

function preferKnown(current: string, candidate: unknown): string {
  if (!candidate || candidate === 'unknown' || candidate === 'null') return current || '';
  if (!current || current === 'unknown') return String(candidate);
  return current;
}

function extractFromText(text: string, regex: RegExp): string {
  return String(text || '').match(regex)?.[1] ?? '';
}

function stackFrames(thrown: Json): string[] {
  if (Array.isArray(thrown['extendedStackTrace'])) {
    return (thrown['extendedStackTrace'] as unknown[]).slice(0, 12).map((frame) => {
      if (typeof frame === 'string') return frame;
      const f = frame as Json;
      return [f['class'], f['method'], f['line']].filter((part) => part !== undefined && part !== null && part !== '').join(':');
    });
  }
  if (typeof thrown['stackTrace'] === 'string') {
    return thrown['stackTrace'].split('\n').slice(0, 12).map((line) => line.trim());
  }
  return [];
}

function emptyEvent(rawLine: string): LogEvent {
  return {
    raw: rawLine || '',
    rawLines: rawLine ? [rawLine] : [],
    timestamp: '',
    timestampDisplay: '',
    level: 'OTHER',
    logger: '',
    loggerShort: '',
    thread: '',
    message: rawLine || '',
    source: 'plain',
    job: '',
    instance: '',
    transaction: '',
    eventType: '',
    project: '',
    plan: '',
    script: '',
    step: '',
    httpUrl: '',
    httpRtMs: '',
    validationStatus: '',
    exceptionType: '',
    exceptionMessage: '',
    stack: [],
    fields: {},
    fingerprint: '',
    searchText: (rawLine || '').toLowerCase(),
  };
}

function buildSearchText(event: LogEvent): string {
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
    event.raw,
  ]
    .join(' ')
    .toLowerCase();
}

export function fingerprintFor(event: LogEvent): string {
  const template = String(event.message || '')
    .replace(/\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b/gi, '<uuid>')
    .replace(/\bi-[0-9a-f]+\b/gi, '<instance>')
    .replace(/\b\d{1,3}(?:\.\d{1,3}){3}\b/g, '<ip>')
    .replace(/\b\d+\b/g, '<n>')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 240);
  return [event.level, event.loggerShort || event.logger, event.eventType || '', template].join('|');
}

function finalizeEvent(event: LogEvent): LogEvent {
  event.loggerShort = shortLogger(event.logger);
  event.timestampDisplay = displayTimestamp(event.timestamp) || event.timestamp;
  if (!event.job) event.job = extractFromText(event.message, JOB_IN_TEXT);
  if (!event.instance) event.instance = extractFromText(event.message, INSTANCE_IN_TEXT);
  if (!event.transaction) event.transaction = extractFromText(event.message, TXN_IN_TEXT);
  event.fingerprint = fingerprintFor(event);
  event.searchText = buildSearchText(event);
  return event;
}

const str = (value: unknown) => String(value || '');

function parseJsonEvent(rawLine: string, parsed: Json): LogEvent {
  const event = emptyEvent(rawLine);
  event.source = 'json';
  const nested = parseNestedJson(firstValue(parsed, ['message', 'Message']));
  const inner = nested && typeof nested === 'object' && !Array.isArray(nested) ? (nested as Json) : {};

  event.timestamp = isoTimestamp(firstValue(parsed, ['timestamp', 'instant', 'time', '@timestamp', 'timeMillis']));
  event.level = normalizeLevel(firstValue(parsed, ['level', 'Level', 'severity']));
  event.logger = str(firstValue(parsed, ['loggerName', 'logger', 'Logger']));
  event.thread = str(firstValue(parsed, ['thread', 'threadName', 'Thread', 'threadId']));
  event.message = messageText(nested !== null ? nested : firstValue(parsed, ['message', 'Message'])) || rawLine;

  event.job = preferKnown('', firstValue(parsed, ['jobId', 'JobId', 'job']));
  event.job = preferKnown(event.job, firstValue(inner, ['JobId', 'jobId', 'job']));
  event.instance = preferKnown('', firstValue(parsed, ['instanceId', 'InstanceId', 'instance']));
  event.instance = preferKnown(event.instance, firstValue(inner, ['InstanceId', 'instanceId']));
  event.transaction = preferKnown('', firstValue(inner, ['TransactionId', 'transactionId', 'txn']));
  event.eventType = str(firstValue(inner, ['EventType', 'eventType']));
  event.project = preferKnown(str(firstValue(parsed, ['projectName', 'ProjectName'])), firstValue(inner, ['ProjectName', 'projectName']));
  event.plan = str(firstValue(inner, ['TestPlanName', 'plan']));
  event.script = str(firstValue(inner, ['ScriptName', 'script']));
  event.step = str(firstValue(inner, ['StepName', 'step']));
  event.httpUrl = str(firstValue(inner, ['RequestUrl', 'url']));
  event.httpRtMs = str(firstValue(inner, ['HttpResponseTime', 'responseTime']));
  event.validationStatus = str(firstValue(inner, ['ValidationStatus', 'validationStatus']));

  const thrown = parsed['thrown'];
  if (thrown && typeof thrown === 'object') {
    const t = thrown as Json;
    event.exceptionType = str(t['name'] || t['class']);
    event.exceptionMessage = str(t['message']);
    event.stack = stackFrames(t);
  }

  event.fields = {
    envelope: Object.fromEntries(Object.entries(parsed).filter(([key]) => key !== 'message' && key !== 'thrown' && key !== 'contextMap')),
    message: inner,
  };
  return finalizeEvent(event);
}

const MONTHS: Record<string, string> = {
  Jan: '01', Feb: '02', Mar: '03', Apr: '04', May: '05', Jun: '06',
  Jul: '07', Aug: '08', Sep: '09', Oct: '10', Nov: '11', Dec: '12',
};

function parseAccessLogTimestamp(value: string): string {
  // Tomcat: 16/Jul/2026:15:00:00 -0700
  const match = String(value || '').match(/^(\d{2})\/([A-Za-z]{3})\/(\d{4}):(\d{2}:\d{2}:\d{2})(?:\s+([+-]\d{4}))?/);
  if (!match) return '';
  const month = MONTHS[match[2]!] ?? '01';
  const offset = match[5] ?? '+0000';
  return isoTimestamp(`${match[3]}-${month}-${match[1]}T${match[4]}${offset.replace(/([+-]\d{2})(\d{2})/, '$1:$2')}`);
}

function parsePlainEvent(rawLine: string): LogEvent {
  const event = emptyEvent(rawLine);
  const mapMatch = rawLine.match(MAP_MESSAGE);
  if (mapMatch) {
    event.source = 'map';
    event.message = mapMatch[1]!;
    event.level = 'INFO';
    return finalizeEvent(event);
  }

  const access = rawLine.match(ACCESS_LINE);
  if (access) {
    const status = numberFrom(access[5], 0);
    event.source = 'access';
    event.timestamp = parseAccessLogTimestamp(access[2]!);
    event.level = status >= 500 ? 'ERROR' : status >= 400 ? 'WARN' : 'INFO';
    event.logger = 'access';
    event.httpUrl = access[4] ?? '';
    event.message = `${access[3]} ${access[4]} → ${access[5]}${access[1] ? ` from ${access[1]}` : ''}`;
    event.eventType = 'Http';
    return finalizeEvent(event);
  }

  const match = rawLine.match(PLAIN_LINE);
  if (!match) {
    event.source = 'plain';
    event.message = rawLine;
    return finalizeEvent(event);
  }

  event.source = 'pattern';
  event.timestamp = isoTimestamp(match[1]);
  event.level = normalizeLevel(match[2]);
  event.logger = match[3]!;
  event.message = match[5] ?? '';
  return finalizeEvent(event);
}

export function parseLine(rawLine: string): LogEvent | null {
  const line = String(rawLine || '').replace(/\r$/, '');
  if (!line) return null;
  try {
    const parsed: unknown = JSON.parse(line);
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) return parseJsonEvent(line, parsed as Json);
  } catch {
    // fall through to the plain parsers
  }
  return parsePlainEvent(line);
}

export function isStackContinuation(line: string): boolean {
  const text = String(line || '');
  return STACK_LINE.test(text) || EXCEPTION_LINE.test(text.trim());
}

function attachContinuation(event: LogEvent, line: string): LogEvent {
  event.rawLines = event.rawLines ?? [event.raw];
  event.rawLines.push(line);
  event.raw = event.rawLines.join('\n');
  const trimmed = line.trim();
  const exceptionMatch = trimmed.match(EXCEPTION_LINE);
  if (exceptionMatch && !event.exceptionType) {
    event.exceptionType = trimmed.split(':')[0]!;
    event.exceptionMessage = (exceptionMatch[1] ?? '').trim();
  } else {
    event.stack.push(trimmed);
  }
  event.searchText = buildSearchText(event);
  return event;
}

export interface ChunkState {
  pendingLine?: string;
  openEvent?: LogEvent | null;
  flush?: boolean;
}

/** Converts raw text into normalized events, attaching multiline stack frames */
export function normalizeChunk(text: string, options: ChunkState = {}): { events: LogEvent[]; pendingLine: string; openEvent: LogEvent | null } {
  let openEvent = options.openEvent ?? null;
  const lines = ((options.pendingLine ?? '') + (text || '')).split('\n');
  const nextPending = options.flush ? '' : (lines.pop() ?? '');
  const events: LogEvent[] = [];

  const closeOpen = () => {
    if (openEvent) {
      events.push(finalizeEvent(openEvent));
      openEvent = null;
    }
  };

  for (const rawLine of lines) {
    const line = rawLine.replace(/\r$/, '');
    if (!line) continue;
    if (isStackContinuation(line)) {
      if (openEvent) {
        attachContinuation(openEvent, line);
        continue;
      }
      const previous = events.at(-1);
      if (previous && (previous.level === 'ERROR' || previous.level === 'FATAL' || previous.exceptionType)) {
        attachContinuation(previous, line);
      }
      // orphan stack frames are noise for investigation views
      continue;
    }

    closeOpen();
    const event = parseLine(line);
    if (!event) continue;
    if (event.level === 'ERROR' || event.level === 'FATAL' || event.exceptionType || event.stack.length) {
      openEvent = event;
    } else {
      events.push(event);
    }
  }

  if (options.flush) closeOpen();
  return { events, pendingLine: nextPending, openEvent };
}

export interface QueryToken {
  field: string | null;
  value: string;
  negated: boolean;
}

export function parseQuery(query: string): QueryToken[] {
  const text = String(query || '').trim();
  if (!text) return [];
  return (text.match(/(?:[^\s"]+|"[^"]*")+/g) ?? []).map((part) => {
    const negated = part.charAt(0) === '-';
    const token = negated ? part.slice(1) : part;
    // field:value only when the field is a plain word, so a quoted phrase with a colon in it (as
    // Exclude adds) stays a phrase; the JSF viewer read "...: Fail" as a field and excluded nothing
    const colon = /^[A-Za-z]+:/.test(token) ? token.indexOf(':') : -1;
    return colon > 0
      ? { field: token.slice(0, colon).toLowerCase(), value: token.slice(colon + 1).replace(/^"|"$/g, '').toLowerCase(), negated }
      : { field: null, value: token.replace(/^"|"$/g, '').toLowerCase(), negated };
  });
}

function fieldValue(event: LogEvent, field: string): string | null {
  switch (field) {
    case 'level':
    case 'lvl':
      return event.level;
    case 'job':
    case 'jobid':
      return event.job;
    case 'instance':
    case 'instanceid':
      return event.instance;
    case 'txn':
    case 'transaction':
    case 'transactionid':
      return event.transaction;
    case 'logger':
      return `${event.loggerShort} ${event.logger}`.toLowerCase();
    case 'thread':
      return event.thread;
    case 'evt':
    case 'event':
    case 'eventtype':
      return event.eventType;
    case 'project':
      return event.project;
    case 'msg':
    case 'message':
      return event.message;
    default:
      return null;
  }
}

export function matchesQuery(event: LogEvent, query: string | QueryToken[]): boolean {
  const tokens = typeof query === 'string' ? parseQuery(query) : query;
  for (const token of tokens) {
    const haystack = token.field ? String(fieldValue(event, token.field) || '').toLowerCase() : event.searchText;
    const hit = haystack.includes(token.value);
    if (token.negated ? hit : !hit) return false;
  }
  return true;
}

export function matchesFacets(event: LogEvent, facets: Facets): boolean {
  return Object.entries(facets).every(([key, wanted]) => !wanted || String(event[key as FacetField] || '') === wanted);
}

export interface Pattern {
  id: string;
  fingerprint: string;
  count: number;
  level: Level;
  logger: string;
  eventType: string;
  message: string;
  firstTs: string;
  lastTs: string;
  sampleIndexes: number[];
  jobs: Record<string, true>;
  transactions: Record<string, true>;
  validationBurst?: boolean;
}

export function buildPatterns(events: LogEvent[], options: { max?: number } = {}): Pattern[] {
  const groups = new Map<string, Pattern>();
  events.forEach((event, index) => {
    const key = event.fingerprint || fingerprintFor(event);
    let group = groups.get(key);
    if (!group) {
      group = {
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
        transactions: {},
      };
      groups.set(key, group);
    }
    group.count += 1;
    group.lastTs = event.timestampDisplay || event.timestamp || group.lastTs;
    if (group.sampleIndexes.length < 5) group.sampleIndexes.push(index);
    if (event.job) group.jobs[event.job] = true;
    if (event.transaction) group.transactions[event.transaction] = true;
    if (event.eventType === 'Validation' && event.transaction) group.validationBurst = true;
  });
  return [...groups.values()]
    .sort((a, b) => b.count - a.count || String(a.message).localeCompare(String(b.message)))
    .slice(0, options.max ?? 50);
}

export function mergeValidationBursts(events: LogEvent[]): LogEvent[] {
  const result: LogEvent[] = [];
  let pending: LogEvent | null = null;
  const flush = () => {
    if (pending) {
      result.push(pending);
      pending = null;
    }
  };
  for (const event of events) {
    const canMerge = event.eventType === 'Validation' && !!event.transaction;
    if (canMerge && pending && pending.transaction === event.transaction) {
      const p: LogEvent = pending;
      p.count = (p.count ?? 1) + 1;
      p.messages = p.messages ?? [p.message];
      p.messages.push(event.message);
      p.message = p.messages.slice(0, 3).join(' | ');
      p.rawLines = (p.rawLines ?? [p.raw]).concat(event.rawLines ?? [event.raw]);
      p.raw = p.rawLines.join('\n');
      p.lastTs = event.timestampDisplay || event.timestamp;
      p.searchText = buildSearchText(p);
      continue;
    }
    flush();
    if (canMerge) pending = { ...event, count: 1, messages: [event.message] };
    else result.push(event);
  }
  flush();
  return result;
}

export function facetCounts(events: LogEvent[], field: FacetField, limit = 12): { value: string; count: number }[] {
  const counts = new Map<string, number>();
  for (const event of events) {
    const value = String(event[field] || '').trim();
    if (!value || value === 'unknown') continue;
    counts.set(value, (counts.get(value) ?? 0) + 1);
  }
  return [...counts]
    .map(([value, count]) => ({ value, count }))
    .sort((a, b) => b.count - a.count || a.value.localeCompare(b.value))
    .slice(0, limit);
}

export interface VolumeBucket {
  total: number;
  error: number;
  warn: number;
  info: number;
  startMs: number | null;
  endMs: number | null;
}

export function volumeBuckets(events: LogEvent[], bucketCount = 24): { buckets: VolumeBucket[]; startMs: number | null; endMs: number | null; spanMs: number } {
  if (!events.length) return { buckets: [], startMs: null, endMs: null, spanMs: 0 };
  const times = events.map(eventEpochMs).filter((value) => Number.isFinite(value));
  if (!times.length) {
    return {
      buckets: Array.from({ length: bucketCount }, () => ({ total: 0, error: 0, warn: 0, info: 0, startMs: null, endMs: null })),
      startMs: null,
      endMs: null,
      spanMs: 0,
    };
  }
  const min = Math.min(...times);
  const max = Math.max(...times);
  const span = Math.max(1, max - min);
  const width = span / bucketCount;
  const buckets: VolumeBucket[] = Array.from({ length: bucketCount }, (_, i) => ({
    total: 0,
    error: 0,
    warn: 0,
    info: 0,
    startMs: min + i * width,
    endMs: i === bucketCount - 1 ? max : min + i * width + width,
  }));
  for (const event of events) {
    const ts = eventEpochMs(event);
    if (!Number.isFinite(ts)) continue;
    const bucket = buckets[Math.min(bucketCount - 1, Math.floor(((ts - min) / span) * bucketCount))]!;
    bucket.total += 1;
    if (event.level === 'ERROR' || event.level === 'FATAL') bucket.error += 1;
    else if (event.level === 'WARN') bucket.warn += 1;
    else if (event.level === 'INFO') bucket.info += 1;
  }
  return { buckets, startMs: min, endMs: max, spanMs: span };
}

const REDACTION_RULES = [
  { type: 'TOKEN', regex: /\bBearer\s+[A-Za-z0-9\-._~+/]+=*/gi },
  { type: 'TOKEN', regex: /\beyJ[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\b/g },
  { type: 'TOKEN', regex: /\b(?:ghp|gho|ghu|ghs|ghr)_[A-Za-z0-9]{20,}\b/g },
  { type: 'TOKEN', regex: /\b(?:sk_live|sk_test|AKIA)[A-Za-z0-9]{16,}\b/g },
  { type: 'TOKEN', regex: /\bV1-\d+-[0-9a-zA-Z]{22}\b/g },
  { type: 'SECRET', regex: /\b(?:password|pwd|secret|api[_-]?key|access[_-]?key)\s*[:=]\s*([^\s,;]+)/gi },
  { type: 'EMAIL', regex: /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/gi },
  { type: 'IP', regex: /\b(?:\d{1,3}\.){3}\d{1,3}\b/g },
  { type: 'COOKIE', regex: /\b(?:Cookie|Set-Cookie)\s*[:=]\s*([^\n]+)/gi },
];

export interface RedactionState {
  maps: Record<string, string>;
  counts: Record<string, number>;
}

const newState = (): RedactionState => ({ maps: {}, counts: {} });

/** Replaces secrets with placeholders that stay the same for the same secret, across calls sharing the state */
export function redactText(text: string, state: RedactionState = newState()): { text: string; state: RedactionState } {
  let output = String(text || '');
  for (const rule of REDACTION_RULES) {
    output = output.replace(rule.regex, (match) => {
      const key = `${rule.type}::${match.toLowerCase()}`;
      if (!state.maps[key]) {
        state.counts[rule.type] = (state.counts[rule.type] ?? 0) + 1;
        state.maps[key] = `[${rule.type}_${state.counts[rule.type]}]`;
      }
      return state.maps[key]!;
    });
  }
  return { text: output, state };
}

export function redactEvent(event: LogEvent, state: RedactionState = newState()): { event: LogEvent; state: RedactionState } {
  const clone: LogEvent = { ...event };
  for (const field of ['message', 'raw', 'exceptionMessage', 'httpUrl', 'thread', 'step', 'script'] as const) {
    if (clone[field]) clone[field] = redactText(clone[field], state).text;
  }
  clone.rawLines = clone.rawLines.map((line) => redactText(line, state).text);
  clone.stack = clone.stack.map((line) => redactText(line, state).text);
  clone.fingerprint = fingerprintFor(clone);
  clone.searchText = buildSearchText(clone);
  return { event: clone, state };
}

export function levelStats(events: LogEvent[]): Record<Level, number> {
  const stats = Object.fromEntries(LEVELS.map((level) => [level, 0])) as Record<Level, number>;
  for (const event of events) stats[event.level] = (stats[event.level] ?? 0) + 1;
  return stats;
}

export function estimateTokens(text: string): number {
  return Math.max(1, Math.ceil(String(text || '').length / 4));
}

export interface Bundle {
  text: string;
  characters: number;
  tokens: number;
  redactionCounts: Record<string, number>;
  eventCount: number;
  patternCount?: number;
}

export function formatCompactBundle(options: {
  events: LogEvent[];
  fileName?: string;
  rawLines?: number;
  retained?: number;
  filters?: unknown;
  redact?: boolean;
  patternLimit?: number;
  timelineLimit?: number;
  includeEvidence?: boolean;
  evidenceLimit?: number;
}): Bundle {
  const events = options.events;
  const redact = options.redact !== false;
  const state = newState();
  const working = redact ? events.map((event) => redactEvent(event, state).event) : events;

  const patterns = buildPatterns(working, { max: options.patternLimit ?? 12 });
  const timeline = working.slice(0, options.timelineLimit ?? 80);
  const stats = levelStats(working);
  const range = working.length
    ? `${working[0]!.timestampDisplay || working[0]!.timestamp || '?'} .. ${working.at(-1)!.timestampDisplay || working.at(-1)!.timestamp || '?'}`
    : '';

  const lines: string[] = [];
  lines.push('TANK_LOG_BUNDLE v1');
  lines.push(
    `scope file=${options.fileName ?? 'unknown.log'} events=${options.rawLines || events.length} retained=${options.retained || events.length} exported=${working.length}${range ? ` range=${range}` : ''}`,
  );
  lines.push(`filters ${JSON.stringify(options.filters ?? {})}`);
  lines.push(`stats ${LEVELS.filter((level) => stats[level] > 0).map((level) => `${level}=${stats[level]}`).join(' ')}`);
  const jobs = facetCounts(working, 'job', 5).map((item) => item.value);
  const txns = facetCounts(working, 'transaction', 5).map((item) => item.value);
  if (jobs.length) lines.push(`jobs ${jobs.join(',')}`);
  if (txns.length) lines.push(`transactions ${txns.join(',')}`);

  lines.push('patterns');
  if (!patterns.length) lines.push('  (none)');
  for (const p of patterns) {
    lines.push(`  x${p.count} ${p.level}${p.eventType ? ` evt=${p.eventType}` : ''} ${p.logger || 'unknown'} | ${String(p.message || '').slice(0, 160)}`);
  }

  lines.push('timeline');
  if (!timeline.length) lines.push('  (none)');
  for (const e of timeline) {
    lines.push(
      `  ${e.timestampDisplay || e.timestamp || '—'} ${e.level}${e.job ? ` job=${e.job}` : ''}${e.instance ? ` instance=${e.instance}` : ''}${e.transaction ? ` txn=${e.transaction}` : ''} ${e.loggerShort || e.logger || 'unknown'} | ${String(e.message || '').slice(0, 180)}`,
    );
  }

  if (options.includeEvidence) {
    lines.push('evidence');
    working.slice(0, options.evidenceLimit ?? 20).forEach((event, index) => {
      lines.push(`  #${index + 1}`);
      for (const line of event.rawLines.length ? event.rawLines : [event.raw]) lines.push(`    ${line}`);
    });
  }

  const redactionSummary = Object.entries(state.counts).map(([type, count]) => `${type}=${count}`).join(' ');
  lines.push(`redaction ${redact ? redactionSummary || 'none' : 'disabled'}`);

  const text = lines.join('\n');
  return { text, characters: text.length, tokens: estimateTokens(text), redactionCounts: state.counts, patternCount: patterns.length, eventCount: working.length };
}

export function formatJsonl(events: LogEvent[], options: { redact?: boolean } = {}): Bundle {
  const redact = options.redact !== false;
  const state = newState();
  const text = events
    .map((event) => {
      const current = redact ? redactEvent(event, state).event : event;
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
        err: current.exceptionType ? { type: current.exceptionType, msg: current.exceptionMessage, stack: current.stack.slice(0, 8) } : undefined,
        fingerprint: current.fingerprint,
        count: current.count ?? 1,
      });
    })
    .join('\n');
  return { text, characters: text.length, tokens: estimateTokens(text), redactionCounts: state.counts, eventCount: events.length };
}
