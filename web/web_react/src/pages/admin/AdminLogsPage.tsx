import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { confirmDialog } from 'primereact/confirmdialog';
import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { Message } from 'primereact/message';
import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router';
import { toApiError } from '../../api/errors';
import { useNotify } from '../../notify';
import { useSession } from '../../session';
import { LogWorkspace } from './logs/LogWorkspace';
import { MAX_ENTRIES, MAX_POLL_SECONDS } from './logs/useLogStream';
/** What JSF's admin/logs.xhtml starts with */
const DEFAULT_LINES = 20;
const DEFAULT_POLL_SECONDS = 5;

/** The log viewer and log level (LogViewer, LogConfig and admin/logs.xhtml) */
export function AdminLogsPage() {
  const { client } = useSession();
  const [params, setParams] = useSearchParams();
  const file = params.get('file') ?? undefined;
  const [lines, setLines] = useState<number | null>(DEFAULT_LINES);
  const [poll, setPoll] = useState<number | null>(DEFAULT_POLL_SECONDS);
  /** What the workspace reads with; Load applies the inputs */
  const [loaded, setLoaded] = useState({ lines: DEFAULT_LINES, poll: DEFAULT_POLL_SECONDS, run: 0 });

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

  const load = () => {
    const next = { lines: Math.max(0, lines ?? DEFAULT_LINES), poll: Math.min(MAX_POLL_SECONDS, Math.max(0, poll ?? DEFAULT_POLL_SECONDS)) };
    const apply = () => setLoaded((current) => ({ ...next, run: current.run + 1 }));
    if (next.lines === 0) {
      confirmDialog({
        header: 'Read the whole file?',
        message: `0 lines reads all of ${file ?? 'the log'}, which can be large; the workspace keeps the last ${MAX_ENTRIES.toLocaleString()} events.`,
        acceptLabel: 'Read it all',
        rejectLabel: 'Cancel',
        defaultFocus: 'reject',
        accept: apply,
      });
    } else {
      apply();
    }
  };

  return (
    <>
      <LogLevel />
      <form
        className="list-toolbar log-toolbar"
        onSubmit={(e) => {
          e.preventDefault();
          load();
        }}
      >
        <label htmlFor="log-file" className="field-inline">
          Log file
          <Dropdown
            inputId="log-file"
            value={file ?? null}
            options={files.data ?? []}
            onChange={(e) => setParams({ file: e.value as string }, { replace: true })}
            placeholder={files.isPending ? 'Loading…' : 'Choose a log file'}
            filter={(files.data?.length ?? 0) > 10}
            className="log-file"
          />
        </label>
        <label htmlFor="log-lines" className="field-inline" title="Lines read from the end of the file first; 0 reads the whole file">
          Initial lines
          <InputNumber inputId="log-lines" value={lines} onValueChange={(e) => setLines(e.value ?? null)} min={0} useGrouping={false} size={6} />
        </label>
        <label htmlFor="log-poll" className="field-inline" title={`Seconds between reads, 0 to ${MAX_POLL_SECONDS}; 0 reads once`}>
          Refresh every
          <InputNumber inputId="log-poll" value={poll} onValueChange={(e) => setPoll(e.value ?? null)} min={0} max={MAX_POLL_SECONDS} useGrouping={false} size={4} suffix=" s" />
        </label>
        <Button type="submit" label="Load log" icon="pi pi-play" size="small" disabled={!file} />
      </form>
      {files.error && <Message severity="error" text={files.error.message} />}
      {file && (
        <LogWorkspace key={`${file}|${loaded.lines}|${loaded.poll}|${loaded.run}`} file={file} initialLines={loaded.lines} pollSeconds={loaded.poll} />
      )}
      <p className="field-help">Logs are read from the server that answers, which may be one of several.</p>
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
