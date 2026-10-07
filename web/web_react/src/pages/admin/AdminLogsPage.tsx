import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { Dropdown } from 'primereact/dropdown';
import { IconField } from 'primereact/iconfield';
import { InputIcon } from 'primereact/inputicon';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Message } from 'primereact/message';
import { SelectButton } from 'primereact/selectbutton';
import { useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react';
import { useSearchParams } from 'react-router';
import { contextPath } from '../../api/client';
import { toApiError } from '../../api/errors';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { append, atLeast, EMPTY_TAIL, MAX_LINES, readPosition, type Level, type Tail } from './logTail';

/** How often a followed log is read again */
export const FOLLOW_INTERVAL_MS = 5000;
const DEFAULT_LINES = 200;

const SEVERITIES: { label: string; value: Level | null }[] = [
  { label: 'All', value: null },
  { label: 'Warnings and errors', value: 'WARN' },
  { label: 'Errors', value: 'ERROR' },
];

/** Reads a log file's last lines, then what's added to it (log-viewer.2.js against /v2/logs/{file}) */
function useLogTail(file: string | undefined, initialLines: number, follow: boolean) {
  const [tail, setTail] = useState<Tail>(EMPTY_TAIL);
  const [error, setError] = useState<string>();
  const [rotated, setRotated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [updated, setUpdated] = useState<Date>();
  const tailRef = useRef(tail);
  tailRef.current = tail;

  const read = useCallback(
    async (from: number, signal?: AbortSignal) => {
      const response = await fetch(`${contextPath()}/v2/logs/${encodeURIComponent(file!)}?from=${from}`, { signal });
      if (!response.ok) {
        throw new Error(
          response.status === 404 ? `${file} isn't on this server` : response.status === 403 ? 'Only administrators can read logs' : `Couldn't read ${file} (${response.status})`,
        );
      }
      return { text: await response.text(), ...readPosition(response.headers) };
    },
    [file],
  );

  /** Starts over with the file's last lines */
  const load = useCallback(
    async (signal?: AbortSignal) => {
      if (!file) return;
      setLoading(true);
      setError(undefined);
      try {
        const { text, total } = await read(-initialLines, signal);
        setTail(append(EMPTY_TAIL, text, total));
        setRotated(false);
        setUpdated(new Date());
      } catch (e) {
        if (!signal?.aborted) setError((e as Error).message);
      } finally {
        if (!signal?.aborted) setLoading(false);
      }
    },
    [file, initialLines, read],
  );

  useEffect(() => {
    setTail(EMPTY_TAIL);
    const controller = new AbortController();
    void load(controller.signal);
    return () => controller.abort();
  }, [load]);

  useEffect(() => {
    if (!file || !follow) return;
    const controller = new AbortController();
    const timer = setInterval(() => {
      const offset = tailRef.current.offset;
      read(offset, controller.signal)
        .then(({ text, total }) => {
          if (total < offset) {
            // the file was rotated or truncated: start again from its end
            setRotated(true);
            return read(-initialLines, controller.signal).then((fresh) => setTail(append(EMPTY_TAIL, fresh.text, fresh.total)));
          }
          // what was read runs to the file's end when read, so the next read starts there
          setTail((current) => (text ? append(current, text, total) : { ...current, offset: total }));
          setError(undefined);
        })
        .then(() => setUpdated(new Date()))
        .catch((e: Error) => !controller.signal.aborted && setError(e.message));
    }, FOLLOW_INTERVAL_MS);
    return () => {
      clearInterval(timer);
      controller.abort();
    };
  }, [file, follow, initialLines, read]);

  return { tail, error, rotated, loading, updated, reload: () => void load() };
}

/** The log viewer and log level (LogViewer, LogConfig and admin/logs.xhtml) */
export function AdminLogsPage() {
  const { client } = useSession();
  const [params, setParams] = useSearchParams();
  const file = params.get('file') ?? undefined;
  const [initialLines, setInitialLines] = useState(DEFAULT_LINES);
  const [follow, setFollow] = useState(true);
  const [wrap, setWrap] = useState(false);
  const [search, setSearch] = useState('');
  const [severity, setSeverity] = useState<Level | null>(null);

  const files = useQuery({
    queryKey: ['admin-logs'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/admin/logs', { signal });
      if (!data) throw toApiError(error, response, 'list the log files');
      return data;
    },
  });
  // tank.log first, as the server lists them
  useEffect(() => {
    if (!file && files.data?.[0]) setParams({ file: files.data[0] }, { replace: true });
  }, [file, files.data]);

  const { tail, error, rotated, loading, updated, reload } = useLogTail(file, initialLines, follow);
  const q = search.trim().toLowerCase();
  const shown = tail.lines.filter((l) => atLeast(l.level, severity ?? undefined) && (!q || l.text.toLowerCase().includes(q)));

  // keep the newest lines in view while following, unless the reader scrolled up
  const viewer = useRef<HTMLPreElement>(null);
  const pinned = useRef(true);
  useLayoutEffect(() => {
    const el = viewer.current;
    if (el && follow && pinned.current) el.scrollTop = el.scrollHeight;
  }, [shown.length, follow]);

  return (
    <>
      <LogLevel />
      <div className="list-toolbar log-toolbar">
        <Dropdown
          value={file ?? null}
          options={files.data ?? []}
          onChange={(e) => setParams({ file: e.value as string }, { replace: true })}
          placeholder={files.isPending ? 'Loading…' : 'Choose a log file'}
          filter={(files.data?.length ?? 0) > 10}
          aria-label="Log file"
          className="log-file"
        />
        <label htmlFor="log-lines" className="field-inline">
          Last
          <InputNumber
            inputId="log-lines"
            value={initialLines}
            onValueChange={(e) => setInitialLines(Math.min(MAX_LINES, Math.max(1, e.value ?? DEFAULT_LINES)))}
            min={1}
            max={MAX_LINES}
            useGrouping={false}
            size={5}
          />
          lines
        </label>
        <div className="field-inline">
          <Checkbox inputId="log-follow" checked={follow} onChange={(e) => setFollow(!!e.checked)} />
          <label htmlFor="log-follow">Follow</label>
        </div>
        <div className="field-inline">
          <Checkbox inputId="log-wrap" checked={wrap} onChange={(e) => setWrap(!!e.checked)} />
          <label htmlFor="log-wrap">Wrap lines</label>
        </div>
        <Button label="Reload" icon="pi pi-refresh" text size="small" loading={loading} disabled={!file} onClick={reload} />
      </div>
      <div className="list-toolbar">
        <IconField iconPosition="left">
          <InputIcon className="pi pi-search" />
          <InputText value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Find in lines" aria-label="Find in lines" className="list-search" />
        </IconField>
        <SelectButton
          value={severity}
          options={SEVERITIES}
          onChange={(e) => setSeverity((e.value as Level | null | undefined) ?? null)}
          allowEmpty={false}
          aria-label="Severity"
        />
        <span className="field-help" role="status">
          {shown.length === tail.lines.length ? `${tail.lines.length} lines` : `${shown.length} of ${tail.lines.length} lines`}
          {updated && ` · read ${updated.toLocaleTimeString()}`}
          {follow && file && ' · following'}
        </span>
      </div>
      {files.error && <Message severity="error" text={files.error.message} />}
      {error && <Message severity="error" text={error} className="editor-message" />}
      {rotated && <Message severity="info" text="The log was rotated; showing the last lines of the new file." className="editor-message" />}
      <pre
        ref={viewer}
        className={`file-preview log-lines${wrap ? ' log-wrap' : ''}`}
        aria-label={file ? `Lines of ${file}` : 'Log lines'}
        tabIndex={0}
        onScroll={(e) => {
          const el = e.currentTarget;
          pinned.current = el.scrollHeight - el.scrollTop - el.clientHeight < 24;
        }}
      >
        {shown.map((line) => (
          <span key={line.id} className={`log-line${line.level ? ` log-${line.level.toLowerCase()}` : ''}`}>
            {line.text}
            {'\n'}
          </span>
        ))}
        {file && !loading && tail.lines.length === 0 && !error && <span className="field-help">The file is empty.</span>}
      </pre>
      <p className="field-help">
        Keeps the last {MAX_LINES.toLocaleString()} lines. Logs are read from the server that answers, which may be one of several.
      </p>
    </>
  );
}

const LEVEL_OPTIONS = ['OFF', 'FATAL', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL'];

/** The server's log level (LogConfig): every logger, on this node, until it restarts */
function LogLevel() {
  const { client } = useSession();
  const notify = useNotify();
  const queryClient = useQueryClient();
  const current = useQuery({
    queryKey: ['admin-log-level'],
    queryFn: async ({ signal }) => {
      const { data, error, response } = await client.GET('/v2/admin/log-level', { signal });
      if (!data) throw toApiError(error, response, 'read the log level');
      return data;
    },
  });
  const set = useMutation({
    mutationFn: async (level: string) => {
      const { data, error, response } = await client.PUT('/v2/admin/log-level', { body: { level } });
      if (!data) throw toApiError(error, response, 'set the log level');
      return data;
    },
    onSuccess: (data) => {
      queryClient.setQueryData(['admin-log-level'], data);
      notify.success(`Log level set to ${data.level}`, data.node ? `on ${data.node}` : undefined);
    },
    onError: (error) => notify.error('Log level not changed', error.message),
  });
  return (
    <div className="log-level">
      <label htmlFor="log-level">Log level</label>
      <Dropdown
        inputId="log-level"
        value={current.data?.level ?? null}
        options={LEVEL_OPTIONS}
        onChange={(e) => set.mutate(e.value as string)}
        disabled={!current.data || set.isPending}
        placeholder={current.error ? 'Unknown' : 'Loading…'}
      />
      <small className="field-help">
        {current.data?.node ? `On ${current.data.node} only` : 'On the server that answers only'}, for every logger, until it
        restarts. INFO is the default.
      </small>
    </div>
  );
}
