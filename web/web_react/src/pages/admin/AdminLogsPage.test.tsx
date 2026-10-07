import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';
import { FOLLOW_INTERVAL_MS } from './AdminLogsPage';

const ADMIN = { name: 'root', admin: true, rights: {} };

/** A log file on the fake server, read as /v2/logs/{file} serves it */
function fakeLog(initial: string) {
  let content = initial;
  const reads: string[] = [];
  vi.stubGlobal('fetch', async (url: string) => {
    const from = Number(new URL(url, 'https://tank.test').searchParams.get('from'));
    reads.push(String(from));
    const bytes = new TextEncoder().encode(content);
    let start = from;
    if (from < 0) {
      const lines = content.split('\n');
      const keep = lines.slice(Math.max(0, lines.length - 1 + from)).join('\n');
      start = bytes.length - new TextEncoder().encode(keep).length;
    }
    start = Math.min(start, bytes.length);
    return new Response(new TextDecoder().decode(bytes.slice(start)), {
      status: 200,
      headers: { 'X-Total-Content-Length': String(bytes.length), 'X-Content-Start': String(start) },
    });
  });
  return {
    reads,
    write: (more: string) => (content += more),
    rotate: (fresh: string) => (content = fresh),
  };
}

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: ADMIN }),
    'GET /v2/admin/logs': () => ({ status: 200, body: ['tank.log', 'tank-2026-10-06.log', 'catalina.out'] }),
    'GET /v2/admin/log-level': () => ({ status: 200, body: { level: 'INFO', node: 'controller-1' } }),
    ...overrides,
  };
}

const LOG = [
  '10:00:01 INFO  JobManager - job 42 started',
  '10:00:02 WARN  AgentWatchdog - agent slow',
  '10:00:03 ERROR JobManager - job 42 failed',
  'java.lang.IllegalStateException: no agents',
  '\tat com.intuit.tank.JobManager.run(JobManager.java:88)',
  '10:00:04 DEBUG Cache - hit',
].join('\n') + '\n';

const viewer = () => screen.findByLabelText('Lines of tank.log');

afterEach(() => {
  vi.useRealTimers();
  vi.unstubAllGlobals();
});

describe('admin logs', () => {
  it('opens the newest log at its last lines, and filters them', async () => {
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const lines = await viewer();
    await within(lines).findByText(/job 42 started/);
    expect(log.reads[0]).toBe('-200');

    await userEvent.click(screen.getByRole('button', { name: 'Errors' }));
    expect(within(lines).queryByText(/job 42 started/)).not.toBeInTheDocument();
    expect(within(lines).getByText(/job 42 failed/)).toBeInTheDocument();
    // the error's stack trace stays with it
    expect(within(lines).getByText(/JobManager.java:88/)).toBeInTheDocument();
    expect(screen.getByText(/3 of 6 lines/)).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'All' }));
    await userEvent.type(screen.getByLabelText('Find in lines'), 'agent');
    expect(within(lines).getAllByText(/agent/i)).toHaveLength(2);
  });

  it('follows what is written, and starts over when the log rotates', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    const lines = await viewer();
    await within(lines).findByText(/Cache - hit/);

    log.write('10:00:05 INFO  JobManager - job 43 sta');
    await act(() => vi.advanceTimersByTimeAsync(FOLLOW_INTERVAL_MS));
    // half a line waits for the rest
    expect(within(lines).queryByText(/job 43/)).not.toBeInTheDocument();
    log.write('rted\n');
    await act(() => vi.advanceTimersByTimeAsync(FOLLOW_INTERVAL_MS));
    expect(await within(lines).findByText(/job 43 started/)).toBeInTheDocument();
    expect(log.reads.slice(1, 3).map(Number).every((offset) => offset > 0)).toBe(true);

    log.rotate('10:01:00 INFO  Main - new file\n');
    await act(() => vi.advanceTimersByTimeAsync(FOLLOW_INTERVAL_MS));
    expect(await screen.findByText('The log was rotated; showing the last lines of the new file.')).toBeInTheDocument();
    expect(within(lines).getByText(/new file/)).toBeInTheDocument();
    expect(within(lines).queryByText(/job 43/)).not.toBeInTheDocument();
  });

  it('stops reading when not following', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const log = fakeLog(LOG);
    renderApp('/admin/logs', handlers());
    await within(await viewer()).findByText(/Cache - hit/);
    await userEvent.click(screen.getByLabelText('Follow'));
    const reads = log.reads.length;
    await act(() => vi.advanceTimersByTimeAsync(FOLLOW_INTERVAL_MS * 2));
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

  it('says when a log is gone', async () => {
    vi.stubGlobal('fetch', async () => new Response('', { status: 404 }));
    renderApp('/admin/logs?file=old.log', handlers());
    expect(await screen.findByText("old.log isn't on this server")).toBeInTheDocument();
  });
});
