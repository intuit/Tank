import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it } from 'vitest';
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

const GENERATED = '2026-10-07T10:00:00Z';
const seenAgo = (seconds: number) => Date.parse(GENERATED) - seconds * 1000;

const TREE: Schemas['JobTree'] = {
  generatedAt: GENERATED,
  projects: [
    {
      projectId: 7,
      name: 'Checkout load',
      jobs: [
        {
          jobId: '12',
          name: 'Checkout load 1',
          status: 'Running',
          activeUsers: 200,
          totalUsers: 300,
          tps: 900,
          failures: { total: 4 },
          startTime: '2026-10-07T09:30:00Z',
          actions: { ...NONE, stop: true },
          agents: [
            {
              instanceId: 'i-0aaaaaaaaaaaa1111',
              region: 'US_WEST_2',
              zone: 'us-west-2a',
              status: 'running',
              wsState: 'connected',
              activeUsers: 100,
              tps: 600,
              failures: { total: 4 },
              lastSeenMs: seenAgo(5),
              actions: { ...NONE, kill: true },
            },
            {
              instanceId: 'i-0bbbbbbbbbbbb2222',
              region: 'US_WEST_2',
              zone: 'us-west-2b',
              status: 'running',
              wsState: 'disconnected',
              activeUsers: 0,
              tps: 0,
              lastSeenMs: seenAgo(90),
              actions: { ...NONE, kill: true },
            },
            {
              instanceId: 'i-0cccccccccccc3333',
              region: 'US_EAST_1',
              status: 'running',
              wsState: 'connected',
              activeUsers: 100,
              tps: 300,
              lastSeenMs: seenAgo(45),
              actions: { ...NONE, kill: true },
            },
          ],
        },
      ],
    },
  ],
  otherJobs: [
    {
      jobId: '3',
      name: 'Old job',
      status: 'Completed',
      totalUsers: 5,
      startTime: '2026-10-07T07:00:00Z',
      endTime: '2026-10-07T08:00:00Z',
      actions: { ...NONE, delete: true },
      agents: [],
    },
  ],
};

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/jobs/tree': () => ({ status: 200, body: TREE }),
    ...overrides,
  };
}

async function openView(name: string) {
  await userEvent.click(await screen.findByRole('button', { name }));
}

describe('job queue views', () => {
  beforeEach(() => localStorage.removeItem('tank.jobs.view'));

  it('shows every agent as a tile, and an agent’s figures and actions once picked', async () => {
    renderApp('/jobs', handlers());
    await openView('Heat map');

    const job = await screen.findByRole('region', {
      name: 'Job Checkout load 1',
    });
    expect(within(job).getByText('us-west-2 · 2 agents')).toBeInTheDocument();
    expect(within(job).getByText('us-east-1 · 1 agent')).toBeInTheDocument();
    const lost = within(job).getByRole('button', {
      name: /i-0bbbbbbbbbbbb2222/,
    });
    expect(lost).toHaveClass('heat-lost');
    expect(lost).toHaveTextContent('Lost');
    expect(screen.getByText('1 lost · 1 silent over 30s')).toBeInTheDocument();

    await userEvent.click(within(job).getByRole('button', { name: /i-0aaaaaaaaaaaa1111/ }));

    const panel = screen.getByRole('complementary', { name: 'Selected agent' });
    expect(within(panel).getByText('i-0aaaaaaaaaaaa1111')).toBeInTheDocument();
    expect(within(panel).getByRole('button', { name: 'Kill i-0aaaaaaaaaaaa1111' })).toBeInTheDocument();
    expect(localStorage.getItem('tank.jobs.view')).toBe('heatmap');
  });

  it('shades tiles by the figure picked', async () => {
    renderApp('/jobs', handlers());
    await openView('Heat map');
    const tile = await screen.findByRole('button', {
      name: /i-0aaaaaaaaaaaa1111/,
    });
    expect(tile).toHaveTextContent('600');

    await userEvent.click(screen.getByRole('button', { name: 'Failures' }));

    expect(screen.getByRole('button', { name: /i-0aaaaaaaaaaaa1111/ })).toHaveTextContent('4');
    expect(screen.getByRole('button', { name: /i-0aaaaaaaaaaaa1111/ })).toHaveClass('heat-orange-4');
  });

  it('opens in the view chosen last time', async () => {
    localStorage.setItem('tank.jobs.view', 'jobs');
    renderApp('/jobs', handlers());

    const card = await screen.findByRole('article', {
      name: 'Job Checkout load 1',
    });
    expect(within(card).getByText('3/3 agents running · 1 silent over 30s · 1 lost')).toBeInTheDocument();
    expect(within(card).getByRole('progressbar', { name: 'Active users' })).toHaveAttribute('aria-valuenow', '200');
    expect(within(card).getByRole('button', { name: 'Stop Checkout load 1' })).toBeInTheDocument();
  });

  it('sums agents by region and availability zone', async () => {
    renderApp('/jobs', handlers());
    await openView('Regions');

    const westA = (await screen.findByRole('rowheader', { name: 'us-west-2a' })).closest('tr')!;
    expect(westA).toHaveTextContent('us-west-2');
    expect(westA).toHaveTextContent('1 agent · 1 running');
    expect(westA).toHaveTextContent('600');
    expect(within(westA).getByRole('button', { name: 'Checkout load 1 · 1' })).toBeInTheDocument();
    expect(screen.getByRole('rowheader', { name: 'us-west-2b' }).closest('tr')).toHaveTextContent('1 agent · 1 lost');
    // us-west-2 spans both its zones' rows
    expect(screen.getByRole('rowheader', { name: 'us-west-2' })).toHaveAttribute('rowspan', '2');
    const east = screen.getByRole('rowheader', { name: 'us-east-1' }).closest('tr')!;
    expect(east).toHaveTextContent('Unknown zone');
  });

  it('draws past and present jobs on a timeline, asking for finished jobs', async () => {
    const { calls } = renderApp('/jobs', handlers());
    await openView('Timeline');

    expect(
      await screen.findByRole('button', {
        name: /^Checkout load 1: .*still going$/,
      }),
    ).toHaveClass('timeline-bar timeline-success');
    expect(screen.getByRole('button', { name: /^Old job: / })).toHaveClass('timeline-secondary');
    await waitFor(() => expect(calls('GET /v2/jobs/tree').some((q) => q.get('includeFinished') === 'true')).toBe(true));
  });

  it('leaves jobs outside the window, or with no span to draw, off the timeline', async () => {
    const job = (jobId: string, name: string, status: string, times: { startTime?: string; endTime?: string }) => ({
      jobId,
      name,
      status,
      totalUsers: 5,
      ...times,
      actions: { ...NONE },
      agents: [],
    });
    const tree: Schemas['JobTree'] = {
      generatedAt: GENERATED,
      otherJobs: [
        job('20', 'Ended last week', 'Completed', {
          startTime: '2026-09-30T07:00:00Z',
          endTime: '2026-09-30T08:00:00Z',
        }),
        job('21', 'Completed, no end', 'Completed', {
          startTime: '2026-08-01T07:00:00Z',
        }),
        job('22', 'Aborted, never started', 'Aborted', {}),
        job('23', 'Waiting', 'Queued', {}),
        job('24', 'Running for days', 'Running', {
          startTime: '2026-10-01T07:00:00Z',
        }),
      ],
    };
    renderApp('/jobs', handlers({ 'GET /v2/jobs/tree': () => ({ status: 200, body: tree }) }));
    await openView('Timeline');

    expect(await screen.findByRole('button', { name: 'Waiting: Not started' })).toHaveClass('timeline-queued');
    expect(screen.getByRole('button', { name: /^Running for days: .*still going$/ })).toBeInTheDocument();
    for (const name of ['Ended last week', 'Completed, no end', 'Aborted, never started']) {
      expect(screen.queryByRole('button', { name: new RegExp(`^${name}: `) })).not.toBeInTheDocument();
    }
  });
});
