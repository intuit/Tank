import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { Schemas } from '../../api/client';
import { renderApp, type Handlers } from '../../test/renderApp';

const NONE = {
  control: true,
  start: false,
  startLoad: false,
  pause: false,
  resume: false,
  pauseRamp: false,
  resumeRamp: false,
  stop: false,
  kill: false,
  delete: false,
};

const TREE: Schemas['JobTree'] = {
  generatedAt: '2026-10-07T10:00:00Z',
  projects: [
    {
      projectId: 7,
      name: 'Checkout load',
      activeUsers: 80,
      totalUsers: 150,
      tps: 42,
      failures: { total: 3 },
      jobs: [
        { jobId: '12', name: 'Checkout load 1', status: 'Running', activeUsers: 80, totalUsers: 150, actions: { ...NONE, stop: true }, agents: [] },
        { jobId: '11', name: 'Checkout load 0', status: 'Completed', actions: { ...NONE }, agents: [] },
      ],
    },
    {
      projectId: 9,
      name: 'Search soak',
      jobs: [{ jobId: '15', name: 'Search soak 1', status: 'Queued', actions: { ...NONE, start: true }, agents: [] }],
    },
  ],
  otherJobs: [{ jobId: '3', name: 'Old job', status: 'Running', activeUsers: 5, totalUsers: 5, actions: { ...NONE }, agents: [] }],
};

function handlers(overrides: Handlers = {}): Handlers {
  return { 'GET /v2/jobs/tree': () => ({ status: 200, body: TREE }), ...overrides };
}

describe('job queue page', () => {
  it("lists every project's jobs, expanded", async () => {
    const { calls } = renderApp('/jobs', handlers());

    expect(await screen.findByRole('link', { name: 'Checkout load' })).toBeInTheDocument();
    expect(screen.getByText('1/2 jobs running')).toBeInTheDocument();
    expect(screen.getByText('0/1 jobs running')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Checkout load 1' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Search soak 1' })).toBeInTheDocument();
    expect(calls('GET /v2/jobs/tree')[0]!.has('projectId')).toBe(false);
  });

  it('groups jobs whose project is gone', async () => {
    renderApp('/jobs', handlers());

    expect(await screen.findByText('Jobs without a project')).toBeInTheDocument();
    expect(await screen.findByRole('button', { name: 'Old job' })).toBeInTheDocument();
  });

  it('opens a project from its row', async () => {
    const { router } = renderApp(
      '/jobs',
      handlers({ 'GET /v2/projects/9/full': () => ({ status: 200, body: { id: 9, name: 'Search soak', permissions: {} } }) }),
    );

    await userEvent.click(await screen.findByRole('link', { name: 'Search soak' }));

    await waitFor(() => expect(router.state.location.pathname).toBe('/projects/9'));
  });

  it('sends job actions from under a project', async () => {
    const { calls } = renderApp(
      '/jobs',
      handlers({ 'POST /v2/jobs/15/start': () => ({ status: 200, body: { id: '15', action: 'start', status: 'sent' } }) }),
    );

    await userEvent.click(await screen.findByRole('button', { name: 'Run Search soak 1' }));

    await waitFor(() => expect(calls('POST /v2/jobs/15/start')).toHaveLength(1));
  });

  it('collapses a project', async () => {
    renderApp('/jobs', handlers());
    const row = (await screen.findByRole('link', { name: 'Search soak' })).closest('tr')!;

    await userEvent.click(within(row).getAllByRole('button')[0]!);

    await waitFor(() => expect(screen.queryByRole('button', { name: 'Search soak 1' })).not.toBeInTheDocument());
  });

  it('is in the menu', async () => {
    const { router } = renderApp('/', handlers());

    const item = await screen.findByRole('menuitem', { name: /Job queue/ });
    await userEvent.click(within(item).getByText('Job queue'));

    await waitFor(() => expect(router.state.location.pathname).toBe('/jobs'));
    expect(await screen.findByRole('heading', { name: 'Job queue' })).toBeInTheDocument();
  });
});
