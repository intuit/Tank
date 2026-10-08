import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

const ADMIN = { name: 'root', admin: true, rights: {} };

const json = (o: object) => JSON.stringify(o);
const LOG =
  [
    '2026-10-07 10:00:01 INFO JobManager:12 - job 42 started',
    '2026-10-07 10:00:02 WARN AgentWatchdog:30 - agent i-0a1b2c slow',
    '2026-10-07 10:00:03 ERROR JobManager:88 - job 42 failed',
    'java.lang.IllegalStateException: no agents',
    '\tat com.intuit.tank.JobManager.run(JobManager.java:88)',
    json({
      instant: { epochSecond: 1791370804, nanoOfSecond: 0 },
      level: 'INFO',
      loggerName: 'com.intuit.tank.harness.RequestRunner',
      jobId: '42',
      message: { EventType: 'Validation', TransactionId: 'cda37f25-4d49-41ee-a984-59ee0b7786fc', Message: 'login token Bearer abc.def.ghi ok' },
    }),
    '2026-10-07 10:00:05 WARN AgentWatchdog:30 - agent i-0f9e8d slow',
  ].join('\n') + '\n';

/** A log on the fake server, served as /v2/logs/{file} serves it */
function fakeLog(initial: string) {
  let content = initial;
  const reads: number[] = [];
  vi.stubGlobal('fetch', async (url: string) => {
    const from = Number(new URL(url, 'https://tank.test').searchParams.get('from'));
    reads.push(from);
    const bytes = new TextEncoder().encode(content);
    let start = from;
    if (from < 0) {
      const lines = content.split('\n');
      start = bytes.length - new TextEncoder().encode(lines.slice(Math.max(0, lines.length - 1 + from)).join('\n')).length;
    }
    start = Math.min(start, bytes.length);
    return new Response(new TextDecoder().decode(bytes.slice(start)), {
      status: 200,
      headers: { 'X-Total-Content-Length': String(bytes.length), 'X-Content-Start': String(start) },
    });
  });
  return { reads, write: (more: string) => (content += more), rotate: (fresh: string) => (content = fresh) };
}

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: ADMIN }),
    'GET /v2/admin/logs': () => ({ status: 200, body: ['tank.log', 'catalina.out'] }),
    'GET /v2/admin/log-level': () => ({ status: 200, body: { level: 'INFO', node: 'controller-1' } }),
    ...overrides,
  };
}

const timeline = () => screen.findByRole('listbox', { name: 'tank.log timeline' });
const rows = (list: HTMLElement) => within(list).queryAllByRole('option');
let clipboard: string[];

beforeEach(() => {
  clipboard = [];
  Object.defineProperty(navigator, 'clipboard', { value: { writeText: async (t: string) => void clipboard.push(t) }, configurable: true });
});
afterEach(() => {
  vi.useRealTimers();
  vi.unstubAllGlobals();
});

describe('log investigation workspace', () => {
  it('reads the last lines as events, an error with its stack trace as one', async () => {
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const list = await timeline();
    await within(list).findByText(/job 42 started/);
    expect(log.reads[0]).toBe(-20);
    expect(rows(list)).toHaveLength(5);
    expect(screen.getByText(/Showing 5 of 5 retained events/)).toBeInTheDocument();

    await userEvent.click(within(list).getByText(/job 42 failed/));
    const detail = screen.getByRole('complementary', { name: 'Event detail' });
    expect(within(detail).getByText('java.lang.IllegalStateException')).toBeInTheDocument();
    expect(within(detail).getByText(/at com\.intuit\.tank\.JobManager\.run/)).toBeInTheDocument();
  });

  it('filters by severity, field search and facets', async () => {
    fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const list = await timeline();
    await within(list).findByText(/job 42 started/);

    await userEvent.click(screen.getByRole('button', { name: 'Errors' }));
    expect(rows(list)).toHaveLength(1);
    await userEvent.click(screen.getByRole('button', { name: 'Warn+' }));
    expect(rows(list)).toHaveLength(3);
    await userEvent.click(screen.getByRole('button', { name: 'All' }));

    await userEvent.type(screen.getByLabelText('Search events'), '-logger:watchdog');
    await waitFor(() => expect(rows(list)).toHaveLength(3));
    await userEvent.clear(screen.getByLabelText('Search events'));
    await waitFor(() => expect(rows(list)).toHaveLength(5));

    // a job's facet narrows to its events
    const facets = screen.getByRole('complementary', { name: 'Facets' });
    await userEvent.click(within(facets).getByRole('button', { name: /Filter to job 42/ }));
    expect(rows(list).length).toBeLessThan(5);
    expect(within(list).queryByText(/agent i-0a1b2c slow/)).not.toBeInTheDocument();
  });

  it('follows what is written, and starts over when the log rotates', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const list = await timeline();
    await within(list).findByText(/job 42 started/);

    log.write('2026-10-07 10:00:06 INFO JobManager:12 - job 43 sta');
    await act(() => vi.advanceTimersByTimeAsync(5000));
    expect(within(list).queryByText(/job 43/)).not.toBeInTheDocument();
    log.write('rted\n');
    await act(() => vi.advanceTimersByTimeAsync(5000));
    expect(await within(list).findByText(/job 43 started/)).toBeInTheDocument();
    expect(log.reads.slice(1).every((offset) => offset > 0)).toBe(true);

    log.rotate('2026-10-07 10:01:00 INFO Main:1 - new file\n');
    await act(() => vi.advanceTimersByTimeAsync(5000));
    expect(await within(list).findByText(/new file/)).toBeInTheDocument();
    expect(within(list).queryByText(/job 43/)).not.toBeInTheDocument();
  });

  it('pauses and resumes reading', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    await within(await timeline()).findByText(/job 42 started/);
    await userEvent.click(screen.getByRole('button', { name: 'Pause' }));
    expect(screen.getByText('Paused')).toBeInTheDocument();
    const reads = log.reads.length;
    await act(() => vi.advanceTimersByTimeAsync(15000));
    expect(log.reads).toHaveLength(reads);
    await userEvent.click(screen.getByRole('button', { name: 'Resume' }));
    await waitFor(() => expect(log.reads.length).toBeGreaterThan(reads));
  });

  it('groups repeated messages into patterns, to keep only or exclude', async () => {
    fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const list = await timeline();
    await within(list).findByText(/job 42 started/);
    await userEvent.click(screen.getByRole('button', { name: 'Patterns' }));

    // the two "agent ... slow" lines differ only by instance id
    const patterns = screen.getByLabelText('Message patterns');
    const slow = within(patterns).getByText('×2').closest('article')!;
    await userEvent.click(within(slow).getByRole('button', { name: 'Only' }));
    const filtered = await timeline();
    expect(rows(filtered)).toHaveLength(2);
    expect(screen.getByText(/one pattern only/)).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Show all' }));
    await userEvent.click(screen.getByRole('button', { name: 'Patterns' }));
    await userEvent.click(within(within(screen.getByLabelText('Message patterns')).getByText('×2').closest('article')!).getByRole('button', { name: 'Exclude' }));
    expect(screen.getByLabelText('Search events')).toHaveValue('-"agent <instance> slow"'.replace('<instance>', 'i-0a1b2c'));
  });

  it('copies events for an LLM, redacting secrets unless told not to', async () => {
    fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const list = await timeline();
    await userEvent.click(await within(list).findByText(/login token/));

    await userEvent.click(screen.getByRole('button', { name: 'Copy event' }));
    await waitFor(() => expect(clipboard).toHaveLength(1));
    expect(clipboard[0]).toMatch(/^TANK_LOG_BUNDLE v1/);
    expect(clipboard[0]).toMatch(/\[TOKEN_1\]/);
    expect(clipboard[0]).not.toMatch(/abc\.def\.ghi/);

    await userEvent.click(screen.getByRole('button', { name: 'LLM export' }));
    await userEvent.click(screen.getByLabelText('JSONL'));
    expect(screen.getByText(/5 events · .* tokens · redaction TOKEN=1/)).toBeInTheDocument();
    await userEvent.click(screen.getByLabelText('Redact secrets'));
    await userEvent.click(screen.getByRole('button', { name: 'Copy secrets too' }));
    await userEvent.click(screen.getByRole('button', { name: 'Copy export' }));
    await waitFor(() => expect(clipboard).toHaveLength(2));
    expect(clipboard[1]!.split('\n')).toHaveLength(5);
    expect(clipboard[1]).toMatch(/abc\.def\.ghi/);
  });

  it('shows the raw text', async () => {
    fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    await within(await timeline()).findByText(/job 42 started/);
    await userEvent.click(screen.getByRole('button', { name: 'Raw' }));
    expect(screen.getByLabelText('tank.log raw log')).toHaveTextContent('java.lang.IllegalStateException: no agents');
  });

  it('asks before reading a whole file, and reads once with no refresh', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    await within(await timeline()).findByText(/job 42 started/);

    await userEvent.clear(screen.getByLabelText('Initial lines'));
    await userEvent.type(screen.getByLabelText('Initial lines'), '0');
    await userEvent.clear(screen.getByLabelText('Refresh every'));
    await userEvent.type(screen.getByLabelText('Refresh every'), '0');
    await userEvent.click(screen.getByRole('button', { name: 'Load log' }));
    await userEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Read it all' }));

    await waitFor(() => expect(log.reads.at(-1)).toBe(0));
    const reads = log.reads.length;
    await act(() => vi.advanceTimersByTimeAsync(20000));
    expect(log.reads).toHaveLength(reads);
  });

  it("shows and sets this node's log level", async () => {
    fakeLog(LOG);
    let body: unknown;
    renderApp(
      '/admin/logs',
      handlers({
        'PUT /v2/admin/log-level': async (request) => {
          body = await request.json();
          return { status: 200, body: { level: 'DEBUG', node: 'controller-1' } };
        },
      }),
    );
    expect(await screen.findByText(/On controller-1 only/)).toBeInTheDocument();
    await userEvent.click(screen.getByLabelText('Log level').closest('.p-dropdown')!);
    await userEvent.click(await screen.findByRole('option', { name: 'DEBUG', hidden: true }));
    expect(await screen.findByText('Log level set to DEBUG')).toBeInTheDocument();
    expect(body).toEqual({ level: 'DEBUG' });
  });

  it('says when a log is gone, and stops reading it', async () => {
    vi.stubGlobal('fetch', async () => new Response('', { status: 404 }));
    renderApp('/admin/logs?file=old.log', handlers());
    expect(await screen.findByText("old.log isn't on this server.")).toBeInTheDocument();
    expect(screen.getByText('Missing')).toBeInTheDocument();
  });
});
