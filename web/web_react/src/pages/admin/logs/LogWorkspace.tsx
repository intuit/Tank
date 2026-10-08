import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dropdown } from 'primereact/dropdown';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { RadioButton } from 'primereact/radiobutton';
import { Checkbox } from 'primereact/checkbox';
import { useEffect, useLayoutEffect, useMemo, useRef, useState, type KeyboardEvent, type MouseEvent, type ReactNode } from 'react';
import { useNotify } from '../../../notify';
import {
  buildPatterns,
  displayTimestamp,
  eventEpochMs,
  facetCounts,
  formatCompactBundle,
  formatDuration,
  formatJsonl,
  LEVELS,
  matchesFacets,
  matchesQuery,
  parseQuery,
  shortDate,
  shortTimestamp,
  volumeBuckets,
  type Bundle,
  type FacetField,
  type Facets,
  type Level,
  type LogEvent,
} from './logProcessing';
import { useLogStream } from './useLogStream';

/** Events before and after one that "context" copies and the detail panel show */
export const CONTEXT_WINDOW = 5;

type View = 'timeline' | 'patterns' | 'raw';
type Preset = 'all' | 'warnplus' | 'errors';
type Scope = 'selected' | 'visible' | 'retained' | 'focused' | 'context';
type Format = 'compact' | 'jsonl';

const PRESETS: Record<Preset, readonly Level[]> = {
  all: LEVELS,
  warnplus: ['WARN', 'ERROR', 'FATAL'],
  errors: ['ERROR', 'FATAL'],
};
const PRESET_BUTTONS: { value: Preset; label: string; title: string }[] = [
  { value: 'all', label: 'All', title: 'Show all severity levels' },
  { value: 'warnplus', label: 'Warn+', title: 'Show WARN, ERROR and FATAL only' },
  { value: 'errors', label: 'Errors', title: 'Show ERROR and FATAL only' },
];
const VIEWS: { value: View; label: string; title: string }[] = [
  { value: 'timeline', label: 'Timeline', title: 'Events with time, level, logger and message' },
  { value: 'patterns', label: 'Patterns', title: 'Similar messages grouped, to spot repeated noise' },
  { value: 'raw', label: 'Raw', title: 'The log text as read' },
];
const FACETS: { field: FacetField; label: string; title: string }[] = [
  { field: 'job', label: 'Job', title: 'Job id, when the log has one' },
  { field: 'instance', label: 'Instance', title: 'Agent instance id, when present' },
  { field: 'transaction', label: 'Transaction', title: 'Transaction or correlation id, when present' },
  { field: 'eventType', label: 'Event type', title: 'Normalized event type' },
  { field: 'loggerShort', label: 'Logger', title: 'Logger or class name' },
];

/**
 * The investigation workspace (the JSF fileView component and log-viewer.js): a live log as
 * searchable events with facets, patterns, a volume chart, event detail and LLM-ready copies.
 */
export function LogWorkspace({ file, initialLines, pollSeconds }: { file: string; initialLines: number; pollSeconds: number }) {
  const notify = useNotify();
  const stream = useLogStream(file, initialLines, pollSeconds);
  const { events } = stream;

  const [search, setSearch] = useState('');
  const [query, setQuery] = useState('');
  const [preset, setPreset] = useState<Preset>('all');
  const [facets, setFacets] = useState<Facets>({});
  const [patternFilter, setPatternFilter] = useState<string>();
  const [view, setView] = useState<View>('timeline');
  const [wrap, setWrap] = useState(false);
  const [follow, setFollow] = useState(true);
  const [newEntries, setNewEntries] = useState(0);
  const [selected, setSelected] = useState<Set<LogEvent>>(new Set());
  const [focused, setFocused] = useState<LogEvent>();
  const [anchor, setAnchor] = useState<LogEvent>();
  const [detailOpen, setDetailOpen] = useState(false);
  const [exportOpen, setExportOpen] = useState(false);
  const [format, setFormat] = useState<Format>('compact');
  const [redact, setRedact] = useState(true);
  const [exportScope, setExportScope] = useState<Scope>('visible');

  const searchInput = useRef<HTMLInputElement>(null);
  const entriesRef = useRef<HTMLDivElement>(null);
  const rawRef = useRef<HTMLPreElement>(null);

  // the search applies a moment after typing stops
  useEffect(() => {
    const timer = setTimeout(() => setQuery(search.trim()), 150);
    return () => clearTimeout(timer);
  }, [search]);

  const tokens = useMemo(() => parseQuery(query), [query]);
  const levels = useMemo(() => new Set<Level>(PRESETS[preset]), [preset]);
  const visible = useMemo(
    () =>
      events.filter(
        (e) => levels.has(e.level) && matchesQuery(e, tokens) && matchesFacets(e, facets) && (!patternFilter || e.fingerprint === patternFilter),
      ),
    [events, levels, tokens, facets, patternFilter],
  );
  // dropped from the retained window: no longer selectable
  const retained = useMemo(() => new Set(events), [events]);
  const selection = useMemo(() => events.filter((e) => selected.has(e)), [events, selected]);
  const highlight = tokens.find((t) => !t.field && !t.negated)?.value ?? '';
  // a stable id per event, for React keys and keyboard focus, as older events drop off the top
  const ids = useRef(new WeakMap<LogEvent, number>());
  const nextId = useRef(0);
  const idOf = (event: LogEvent) => {
    let id = ids.current.get(event);
    if (id === undefined) {
      id = nextId.current++;
      ids.current.set(event, id);
    }
    return id;
  };

  // follow the newest events, or count them while the reader is scrolled up
  useLayoutEffect(() => {
    if (!stream.appended) return;
    if (follow) {
      if (entriesRef.current) entriesRef.current.scrollTop = entriesRef.current.scrollHeight;
      if (rawRef.current) rawRef.current.scrollTop = rawRef.current.scrollHeight;
    } else {
      setNewEntries((n) => n + 1);
    }
  }, [stream.appended]);
  // a view opened while following starts at its newest lines
  useLayoutEffect(() => {
    if (!follow) return;
    for (const el of [entriesRef.current, rawRef.current]) if (el) el.scrollTop = el.scrollHeight;
  }, [view, visible.length === 0]);

  const jumpToEnd = () => {
    setFollow(true);
    setNewEntries(0);
    for (const el of [entriesRef.current, rawRef.current]) if (el) el.scrollTop = el.scrollHeight;
  };

  const eventsFor = (scope: Scope): LogEvent[] => {
    if (scope === 'selected') return selection;
    if (scope === 'focused') return focused && retained.has(focused) ? [focused] : [];
    if (scope === 'context') {
      const center = focused && retained.has(focused) ? focused : selection[0];
      if (!center) return [];
      const index = events.indexOf(center);
      return events.slice(Math.max(0, index - CONTEXT_WINDOW), index + CONTEXT_WINDOW + 1);
    }
    if (scope === 'retained') return events;
    return visible;
  };
  const buildExport = (scope: Scope, fmt: Format): Bundle | undefined => {
    const chosen = eventsFor(scope);
    if (!chosen.length) return undefined;
    if (fmt === 'jsonl') return formatJsonl(chosen, { redact });
    return formatCompactBundle({
      fileName: file,
      events: chosen,
      rawLines: stream.totalReceivedLines,
      retained: events.length,
      filters: { query, levels: [...levels], facets, pattern: patternFilter ?? '' },
      redact,
      includeEvidence: scope === 'selected' || scope === 'focused' || scope === 'context',
      timelineLimit: scope === 'retained' ? 120 : 80,
    });
  };
  const copy = (scope: Scope, fmt: Format = 'compact') => {
    const bundle = buildExport(scope, fmt);
    if (!bundle) {
      notify.info('Nothing to copy', 'Select, focus or filter to some events first');
      return;
    }
    copyText(bundle.text)
      .then(() => notify.success(`Copied ${bundle.eventCount} ${bundle.eventCount === 1 ? 'event' : 'events'} (~${bundle.tokens} tokens)`))
      .catch(() => notify.error('Copy failed', 'Your browser blocked the clipboard'));
  };
  const exportStats = exportOpen ? buildExport(exportScope, format) : undefined;

  const select = (event: LogEvent, mode: 'only' | 'toggle' | 'range') => {
    setSelected((current) => {
      if (mode === 'range' && anchor && retained.has(anchor)) {
        const [a, b] = [events.indexOf(anchor), events.indexOf(event)].sort((x, y) => x - y);
        return new Set([...current, ...events.slice(a, b! + 1)]);
      }
      if (mode === 'toggle') {
        const next = new Set(current);
        if (next.has(event)) next.delete(event);
        else next.add(event);
        return next;
      }
      return new Set([event]);
    });
    setAnchor(event);
    setFocused(event);
  };
  const open = (event: LogEvent) => {
    setFocused(event);
    setDetailOpen(true);
  };
  const moveFocus = (delta: number) => {
    if (!visible.length) return;
    const at = focused ? visible.indexOf(focused) : -1;
    const next = visible[Math.max(0, Math.min(visible.length - 1, (at < 0 ? (delta > 0 ? -1 : 0) : at) + delta))]!;
    setFocused(next);
    entriesRef.current?.querySelector(`[data-event="${idOf(next)}"]`)?.scrollIntoView({ block: 'nearest' });
  };

  const onKeyDown = (e: KeyboardEvent<HTMLElement>) => {
    const target = e.target as HTMLElement;
    if (['INPUT', 'TEXTAREA', 'SELECT'].includes(target.tagName)) {
      if (e.key === 'Escape') target.blur();
      return;
    }
    if (e.key === '/') {
      e.preventDefault();
      searchInput.current?.focus();
    } else if (e.key === 'Escape') {
      setSelected(new Set());
      setDetailOpen(false);
    } else if (e.key === 'j' || e.key === 'ArrowDown') {
      e.preventDefault();
      moveFocus(1);
    } else if (e.key === 'k' || e.key === 'ArrowUp') {
      e.preventDefault();
      moveFocus(-1);
    } else if (e.key === 'Enter' && focused) {
      e.preventDefault();
      open(focused);
    } else if (e.key === 'c' && !e.metaKey && !e.ctrlKey) {
      e.preventDefault();
      copy(selection.length ? 'selected' : 'focused');
    }
  };

  const toggleFacet = (field: FacetField, value: string) =>
    setFacets((current) => {
      const next = { ...current };
      if (next[field] === value) delete next[field];
      else next[field] = value;
      return next;
    });
  const exclude = (message: string) => {
    const next = `${search} -"${message.slice(0, 80)}"`.trim();
    setSearch(next);
    setQuery(next);
    setPatternFilter(undefined);
  };

  const meta = [
    stream.lastRefreshAt && `refreshed ${stream.lastRefreshAt.toLocaleTimeString()}`,
    stream.totalLength !== undefined && `${stream.totalLength.toLocaleString()} bytes`,
    pollSeconds > 0 ? `poll ${pollSeconds}s` : 'not polling',
  ]
    .filter(Boolean)
    .join(' · ');

  return (
    // the workspace takes the keyboard shortcuts
    <section className={`log-workspace${wrap ? ' log-wrap' : ''}`} onKeyDown={onKeyDown} aria-label="Investigation workspace">
      <header className="log-ws-header">
        <div className="log-ws-title">
          <span className="log-ws-label">Investigation workspace</span>
          <span className="log-ws-file" title={file}>
            {file}
          </span>
          <span className="field-help">{meta}</span>
        </div>
        <div className="log-ws-actions">
          <span className={`log-status log-status-${stream.status}`} role="status" title="State of reading this log">
            {stream.statusLabel}
          </span>
          <Button label="Refresh" icon="pi pi-refresh" text size="small" onClick={stream.refresh} title="Read new log bytes now" />
          <Button
            label={stream.paused ? 'Resume' : 'Pause'}
            icon={stream.paused ? 'pi pi-play' : 'pi pi-pause'}
            text
            size="small"
            aria-pressed={stream.paused}
            onClick={() => stream.setPaused(!stream.paused)}
            title={stream.paused ? 'Resume reading new log bytes' : 'Stop reading new log bytes until resumed'}
          />
          <Button
            label={follow ? 'Following' : `Follow${newEntries ? ` (${newEntries} new)` : ''}`}
            icon="pi pi-angle-double-down"
            text
            size="small"
            aria-pressed={follow}
            onClick={jumpToEnd}
            title={follow ? 'Keeping the newest events in view; scroll up to stop' : 'Jump to the newest events and keep following'}
          />
          <Button
            label={wrap ? 'No wrap' : 'Wrap'}
            icon="pi pi-align-left"
            text
            size="small"
            aria-pressed={wrap}
            onClick={() => setWrap(!wrap)}
            title={wrap ? 'Keep each line on one row' : 'Wrap long lines to the panel width'}
          />
        </div>
      </header>

      <div className="log-ws-commands">
        <div className="log-ws-search">
          <IconField iconPosition="left">
            <InputIcon className="pi pi-search" />
            <InputText
              ref={searchInput}
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="level:error job:123 -logger:Hibernate"
              aria-label="Search events"
              title='Filter events. Examples: error, level:error, job:123, -logger:Hibernate, "two words". Press / to search.'
            />
          </IconField>
          {search && <Button label="Clear" text size="small" onClick={() => (setSearch(''), setQuery(''), searchInput.current?.focus())} />}
        </div>
        <Toggles label="Severity" value={preset} options={PRESET_BUTTONS} onChange={setPreset} />
        <div className="log-ws-group" role="group" aria-label="Copy">
          <Button label="Copy selected" size="small" outlined disabled={!selection.length} onClick={() => copy('selected')} title="Copy the checked events as a compact diagnostic bundle" />
          <Button label="Copy visible" size="small" outlined disabled={!visible.length} onClick={() => copy('visible')} title="Copy every event the filters show" />
          <Button
            label={`Copy ±${CONTEXT_WINDOW}`}
            size="small"
            outlined
            disabled={!focused && !selection.length}
            onClick={() => copy('context')}
            title={`Copy the focused event with up to ${CONTEXT_WINDOW} events before and after it`}
          />
          <Button
            label="LLM export"
            icon={exportOpen ? 'pi pi-chevron-up' : 'pi pi-chevron-down'}
            iconPos="right"
            size="small"
            outlined
            aria-expanded={exportOpen}
            onClick={() => setExportOpen(!exportOpen)}
            title="Export options: compact bundle or JSONL, with redaction"
          />
        </div>
        <Toggles label="View" value={view} options={VIEWS} onChange={setView} />
      </div>

      {exportOpen && (
        <div className="log-ws-export" role="group" aria-label="LLM export">
          <div className="field-inline">
            <RadioButton inputId="export-compact" checked={format === 'compact'} onChange={() => setFormat('compact')} />
            <label htmlFor="export-compact" title="A token-friendly summary to paste into an LLM">
              Compact bundle
            </label>
          </div>
          <div className="field-inline">
            <RadioButton inputId="export-jsonl" checked={format === 'jsonl'} onChange={() => setFormat('jsonl')} />
            <label htmlFor="export-jsonl" title="One JSON object per event">
              JSONL
            </label>
          </div>
          <div className="field-inline">
            <Checkbox
              inputId="export-redact"
              checked={redact}
              onChange={(e) =>
                e.checked
                  ? setRedact(true)
                  : confirmDialog({
                      header: 'Copy without redaction?',
                      message: 'Tokens, passwords, emails, IP addresses and cookies in the log would be copied as they are.',
                      icon: 'pi pi-exclamation-triangle',
                      acceptLabel: 'Copy secrets too',
                      rejectLabel: 'Keep redacting',
                      acceptClassName: 'p-button-danger',
                      defaultFocus: 'reject',
                      accept: () => setRedact(false),
                    })
              }
            />
            <label htmlFor="export-redact" title="Replace secrets with stable placeholders such as [TOKEN_1]">
              Redact secrets
            </label>
          </div>
          <Dropdown
            value={exportScope}
            options={[
              { label: 'Selected', value: 'selected' },
              { label: 'Visible', value: 'visible' },
              { label: 'All retained', value: 'retained' },
            ]}
            onChange={(e) => setExportScope(e.value as Scope)}
            aria-label="Export scope"
          />
          <Button label="Copy export" icon="pi pi-copy" size="small" onClick={() => copy(exportScope, format)} />
          <span className="field-help" role="status">
            {exportStats
              ? `${exportStats.eventCount} events · ${exportStats.characters.toLocaleString()} chars · ~${exportStats.tokens.toLocaleString()} tokens · redaction ${
                  redact ? Object.entries(exportStats.redactionCounts).map(([t, n]) => `${t}=${n}`).join(' ') || 'none' : 'off'
                }`
              : 'Select or filter to some events to export.'}
          </span>
        </div>
      )}

      <Volume events={visible} />
      {stream.notice && <Message severity="warn" text={stream.notice} className="editor-message" />}
      <p className="field-help log-ws-summary" aria-live="polite">
        Showing {visible.length.toLocaleString()} of {events.length.toLocaleString()} retained events
        {stream.totalReceivedLines > 0 && ` · ${stream.totalReceivedLines.toLocaleString()} raw lines read`}
        {stream.omittedEvents > 0 && ` · ${stream.omittedEvents.toLocaleString()} older events dropped`}
        {selection.length > 0 && ` · ${selection.length} selected`}
        {patternFilter && (
          <>
            {' · '}one pattern only{' '}
            <Button label="Show all" link size="small" className="inline-link" onClick={() => setPatternFilter(undefined)} />
          </>
        )}
      </p>

      <div className={`log-ws-body${detailOpen && focused ? ' has-detail' : ''}`}>
        <aside className="log-ws-facets" aria-label="Facets">
          {FACETS.map(({ field, label, title }) => {
            // each facet counts what the other filters leave
            const others = { ...facets };
            delete others[field];
            const counts = facetCounts(
              events.filter(
                (e) => levels.has(e.level) && matchesQuery(e, tokens) && matchesFacets(e, others) && (!patternFilter || e.fingerprint === patternFilter),
              ),
              field,
              8,
            );
            return (
              <div key={field} className="log-facet">
                <h3 title={title}>{label}</h3>
                {counts.length === 0 ? (
                  <span className="field-help">—</span>
                ) : (
                  counts.map(({ value, count }) => (
                    <button
                      key={value}
                      type="button"
                      className="log-facet-chip"
                      aria-pressed={facets[field] === value}
                      title={`${facets[field] === value ? 'Clear' : 'Filter to'} ${label.toLowerCase()} ${value}`}
                      aria-label={`${facets[field] === value ? 'Clear' : 'Filter to'} ${label.toLowerCase()} ${value}, ${count} ${count === 1 ? 'event' : 'events'}`}
                      onClick={() => toggleFacet(field, value)}
                    >
                      <span className="log-facet-value">{value}</span> <span className="log-facet-count">{count}</span>
                    </button>
                  ))
                )}
              </div>
            );
          })}
        </aside>

        <div className="log-ws-main">
          {view === 'timeline' && (
            <div
              ref={entriesRef}
              className="log-entries"
              role="listbox"
              aria-multiselectable
              aria-label={`${file} timeline`}
              tabIndex={0}
              onScroll={(e) => {
                const el = e.currentTarget;
                const atEnd = el.scrollHeight - el.scrollTop - el.clientHeight < 40;
                setFollow(atEnd);
                if (atEnd) setNewEntries(0);
              }}
            >
              {visible.map((event, i) => (
                <Row
                  key={idOf(event)}
                  event={event}
                  index={idOf(event)}
                  previous={visible[i - 1]}
                  selected={selected.has(event)}
                  focused={focused === event}
                  highlight={highlight}
                  onCheck={(e) => select(event, e.shiftKey ? 'range' : 'toggle')}
                  onClick={(e) => {
                    select(event, e.shiftKey ? 'range' : e.metaKey || e.ctrlKey ? 'toggle' : 'only');
                    setDetailOpen(true);
                  }}
                />
              ))}
              {visible.length === 0 && (
                <p className="log-empty">
                  {stream.status === 'loading' && events.length === 0
                    ? 'Loading log data…'
                    : events.length
                      ? `No events match the filters (${events.length} retained). Choose All, or clear the search and facets.`
                      : 'No log data yet.'}
                </p>
              )}
            </div>
          )}

          {view === 'patterns' && (
            <Patterns
              events={visible}
              active={patternFilter}
              onOnly={(fingerprint) => {
                setPatternFilter(fingerprint);
                setView('timeline');
              }}
              onToggle={(fingerprint) => {
                setPatternFilter((current) => (current === fingerprint ? undefined : fingerprint));
                setView('timeline');
              }}
              onExclude={exclude}
            />
          )}

          {view === 'raw' && (
            <pre ref={rawRef} className="log-raw" aria-label={`${file} raw log`} tabIndex={0}>
              {stream.raw || 'No log data yet.'}
            </pre>
          )}
        </div>

        {detailOpen && focused && retained.has(focused) && (
          <Detail
            event={focused}
            context={events.slice(Math.max(0, events.indexOf(focused) - CONTEXT_WINDOW), events.indexOf(focused) + CONTEXT_WINDOW + 1)}
            onClose={() => setDetailOpen(false)}
            onCopy={() => copy('focused')}
            onCopyJson={() => copy('focused', 'jsonl')}
            onCopyContext={() => copy('context')}
          />
        )}
      </div>
      <p className="field-help log-ws-keys">
        Keys: <kbd>/</kbd> search · <kbd>j</kbd>/<kbd>k</kbd> move · <kbd>Enter</kbd> details · <kbd>c</kbd> copy · <kbd>Esc</kbd> clear.
        Shift-click selects a range, Ctrl/⌘-click adds one.
      </p>
    </section>
  );
}

function Toggles<T extends string>({
  label,
  value,
  options,
  onChange,
}: {
  label: string;
  value: T;
  options: { value: T; label: string; title: string }[];
  onChange: (value: T) => void;
}) {
  return (
    <div className="log-ws-group log-toggles" role="group" aria-label={label}>
      {options.map((o) => (
        <Button
          key={o.value}
          label={o.label}
          size="small"
          outlined={value !== o.value}
          aria-pressed={value === o.value}
          title={o.title}
          onClick={() => onChange(o.value)}
        />
      ))}
    </div>
  );
}

function Row({
  event,
  index,
  previous,
  selected,
  focused,
  highlight,
  onCheck,
  onClick,
}: {
  event: LogEvent;
  index: number;
  previous: LogEvent | undefined;
  selected: boolean;
  focused: boolean;
  highlight: string;
  onCheck: (e: MouseEvent) => void;
  onClick: (e: MouseEvent) => void;
}) {
  let time = shortTimestamp(event.timestamp) || '—';
  if (previous && shortDate(event.timestamp) && shortDate(previous.timestamp) !== shortDate(event.timestamp)) {
    time = `${shortDate(event.timestamp)} ${time}`;
  }
  const gap = eventEpochMs(event) - eventEpochMs(previous);
  const source = [event.job && `job=${event.job}`, event.transaction && `txn=${event.transaction.slice(0, 8)}`, event.loggerShort || event.logger || 'plain']
    .filter(Boolean)
    .join(' · ');
  return (
    <div
      className={`log-entry log-${event.level.toLowerCase()}${selected ? ' is-selected' : ''}${focused ? ' is-focused' : ''}`}
      role="option"
      aria-selected={selected}
      data-event={index}
    >
      <input type="checkbox" className="log-entry-check" checked={selected} onChange={() => undefined} onClick={onCheck} aria-label="Select event" />
      <button type="button" className="log-entry-summary" onClick={onClick}>
        <span className="log-entry-time" title={event.timestampDisplay || 'No time in this line'}>
          {time}
          {Number.isFinite(gap) && (
            <span className={`log-entry-delta${gap >= 60000 ? ' is-large' : ''}`} title="Since the event above">
              {gap === 0 ? 'same ms' : formatDuration(gap)}
            </span>
          )}
        </span>
        <span className="log-entry-level">{event.level}</span>
        <span className="log-entry-source" title={event.logger}>
          {source}
        </span>
        <span className="log-entry-message">{highlighted(event.message || '—', highlight)}</span>
      </button>
    </div>
  );
}

function highlighted(text: string, needle: string): ReactNode {
  if (!needle) return text;
  const lower = text.toLowerCase();
  const parts: ReactNode[] = [];
  let start = 0;
  for (let at = lower.indexOf(needle); at >= 0; at = lower.indexOf(needle, start)) {
    if (at > start) parts.push(text.slice(start, at));
    parts.push(<mark key={at}>{text.slice(at, at + needle.length)}</mark>);
    start = at + needle.length;
  }
  if (start < text.length) parts.push(text.slice(start));
  return parts;
}

function Patterns({
  events,
  active,
  onOnly,
  onToggle,
  onExclude,
}: {
  events: LogEvent[];
  active: string | undefined;
  onOnly: (fingerprint: string) => void;
  onToggle: (fingerprint: string) => void;
  onExclude: (message: string) => void;
}) {
  const patterns = useMemo(() => buildPatterns(events, { max: 40 }), [events]);
  if (!patterns.length) return <p className="log-empty">No patterns in the current filters.</p>;
  return (
    <div className="log-patterns" aria-label="Message patterns">
      {patterns.map((p) => (
        <article key={p.fingerprint} className={`log-pattern log-${p.level.toLowerCase()}${active === p.fingerprint ? ' is-active' : ''}`}>
          <button type="button" className="log-entry-summary log-pattern-summary" onClick={() => onToggle(p.fingerprint)} title={active === p.fingerprint ? 'Show every pattern again' : 'Show only this pattern'}>
            <span className="log-pattern-count">×{p.count}</span>
            <span className="log-entry-level">{p.level}</span>
            <span className="log-entry-source">{p.logger || 'unknown'}</span>
            <span className="log-entry-message">{p.message}</span>
          </button>
          <span className="field-help log-pattern-meta">
            {p.firstTs} → {p.lastTs}
            {p.eventType && ` · ${p.eventType}`}
          </span>
          <div className="log-pattern-actions">
            <Button label="Only" size="small" text onClick={() => onOnly(p.fingerprint)} title="Show only events like this in the timeline" />
            <Button label="Exclude" size="small" text onClick={() => onExclude(p.message)} title="Hide events like this from the timeline" />
          </div>
        </article>
      ))}
    </div>
  );
}

function Volume({ events }: { events: LogEvent[] }) {
  const volume = useMemo(() => volumeBuckets(events, 32), [events]);
  const max = Math.max(1, ...volume.buckets.map((b) => b.total));
  if (!volume.buckets.length) return null;
  const at = (ms: number | null) => (ms ? new Date(ms).toISOString() : '');
  return (
    <div className="log-volume-wrap" title="Events over the visible time range">
      <div className="log-volume" aria-hidden>
        {volume.buckets.map((b, i) => (
          <span
            key={i}
            className={`log-volume-bar${b.error ? ' has-error' : b.warn ? ' has-warn' : ''}`}
            style={{ height: `${Math.max(b.total ? 4 : 2, Math.round((b.total / max) * 100))}%` }}
            title={
              b.startMs !== null
                ? `${b.total} event${b.total === 1 ? '' : 's'} · ${shortTimestamp(at(b.startMs))} → ${shortTimestamp(at(b.endMs))}${b.error ? ` · ${b.error} error` : ''}${b.warn ? ` · ${b.warn} warn` : ''}`
                : `${b.total} events`
            }
          />
        ))}
      </div>
      {volume.startMs !== null && volume.endMs !== null && (
        <div className="log-volume-axis field-help">
          <span title={displayTimestamp(at(volume.startMs))}>{shortTimestamp(at(volume.startMs))}</span>
          <span>{volume.spanMs < 1000 ? 'span <1s' : `span ${formatDuration(volume.spanMs).replace(/^\+/, '')}`}</span>
          <span title={displayTimestamp(at(volume.endMs))}>{shortTimestamp(at(volume.endMs))}</span>
        </div>
      )}
    </div>
  );
}

function Detail({
  event,
  context,
  onClose,
  onCopy,
  onCopyJson,
  onCopyContext,
}: {
  event: LogEvent;
  context: LogEvent[];
  onClose: () => void;
  onCopy: () => void;
  onCopyJson: () => void;
  onCopyContext: () => void;
}) {
  const fields: [string, string][] = [
    ['Timestamp', event.timestampDisplay || event.timestamp],
    ['Level', event.level],
    ['Logger', event.logger],
    ['Thread', event.thread],
    ['Job', event.job],
    ['Instance', event.instance],
    ['Transaction', event.transaction],
    ['Event type', event.eventType],
    ['Project', event.project],
    ['Plan', event.plan],
    ['Script', event.script],
    ['Step', event.step],
    ['HTTP URL', event.httpUrl],
    ['HTTP RT ms', event.httpRtMs],
    ['Validation', event.validationStatus],
    ['Exception', event.exceptionType],
    ['Fingerprint', event.fingerprint],
  ];
  return (
    <aside className="log-ws-detail" aria-label="Event detail">
      <div className="log-detail-header">
        <strong>
          {event.level} · {event.loggerShort || event.logger || 'event'}
        </strong>
        <Button icon="pi pi-times" rounded text size="small" aria-label="Close event detail" onClick={onClose} />
      </div>
      <div className="log-ws-group">
        <Button label="Copy event" size="small" outlined onClick={onCopy} />
        <Button label="Copy JSON" size="small" outlined onClick={onCopyJson} />
        <Button label={`Copy ±${CONTEXT_WINDOW}`} size="small" outlined onClick={onCopyContext} />
      </div>
      <dl className="log-detail-fields">
        {fields
          .filter(([, value]) => value)
          .map(([label, value]) => (
            <div key={label}>
              <dt>{label}</dt>
              <dd>{value}</dd>
            </div>
          ))}
      </dl>
      <h4>Raw</h4>
      <pre className="log-detail-raw">{event.rawLines.length ? event.rawLines.join('\n') : event.raw}</pre>
      <h4>Around it</h4>
      <pre className="log-detail-raw">
        {context
          .map((e) => `${e === event ? '>' : ' '} ${e.timestampDisplay || ''} ${e.level} ${(e.message || '').slice(0, 200)}`)
          .join('\n')}
      </pre>
    </aside>
  );
}

/** Copies text, falling back to a hidden textarea where the clipboard API isn't allowed */
export function copyText(text: string): Promise<void> {
  if (navigator.clipboard?.writeText) return navigator.clipboard.writeText(text);
  return new Promise((resolve, reject) => {
    const area = document.createElement('textarea');
    area.value = text;
    area.setAttribute('readonly', 'readonly');
    area.style.position = 'fixed';
    area.style.left = '-9999px';
    document.body.appendChild(area);
    area.select();
    const ok = document.execCommand('copy');
    area.remove();
    if (ok) resolve();
    else reject(new Error('Copy command failed'));
  });
}
