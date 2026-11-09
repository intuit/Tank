import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it } from 'vitest';
import type { Schemas } from '../../api/client';
import { renderApp, type Handlers } from '../../test/renderApp';
import { agentFigure, formatMetric, isStale, latest, levelOf, peak, type AgentMetrics } from './agentMetricsModel';

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

const HOT = 'i-0aaaaaaaaaaaa1111';
const COOL = 'i-0bbbbbbbbbbbb2222';
const OLD_AMI = 'i-0cccccccccccc3333';

const TREE: Schemas['JobTree'] = {
  generatedAt: '2026-10-07T10:00:00Z',
  projects: [
    {
      projectId: 7,
      name: 'Checkout load',
      jobs: [
        {
          jobId: '12',
          name: 'Checkout load 1',
          status: 'Running',
          activeUsers: 300,
          totalUsers: 300,
          tps: 900,
          actions: { ...NONE },
          agents: [HOT, COOL, OLD_AMI].map((instanceId) => ({
            instanceId,
            region: 'US_WEST_2',
            status: 'running',
            wsState: 'connected',
            activeUsers: 100,
            tps: 300,
            lastSeenMs: Date.parse('2026-10-07T10:00:00Z') - 5000,
            actions: { ...NONE },
          })),
        },
      ],
    },
  ],
  otherJobs: [],
};

const TIMES = ['2026-10-07T09:58:00Z', '2026-10-07T09:59:00Z', '2026-10-07T10:00:00Z'];
const NO_JVM = { threads: [null, null, null], gc: [null, null, null], heap: [null, null, null] };

function metrics(overrides: Partial<AgentMetrics> = {}): AgentMetrics {
  return {
    jobId: 12,
    generatedAt: new Date().toISOString(),
    stale: false,
    times: TIMES,
    agents: [
      {
        instanceId: HOT,
        heapMaxMb: 2000,
        metrics: { cpu: [90, 94, 96], mem: [60, 61, 62], heap: [90, 92, 96], threads: [700, 710, 720], gc: [20, 30, 40], tcp: [1000, 1100, 1200] },
      },
      {
        instanceId: COOL,
        heapMaxMb: 2000,
        metrics: { cpu: [30, 35, 40], mem: [50, 50, 50], heap: [40, 41, 42], threads: [500, 500, 500], gc: [5, 5, 5], tcp: [800, 800, 800] },
      },
      {
        instanceId: OLD_AMI,
        heapMaxMb: null,
        metrics: { cpu: [50, 55, null], mem: [55, 55, 55], tcp: [900, 900, 900], ...NO_JVM },
      },
    ],
    ...overrides,
  };
}

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/jobs/tree': () => ({ status: 200, body: TREE }),
    'GET /v2/jobs/12/agent-metrics': () => ({ status: 200, body: metrics() }),
    ...overrides,
  };
}

describe('agent metrics model', () => {
  it('reads the newest sample and the peak, skipping gaps', () => {
    expect(latest([1, 2, null])).toBe(2);
    expect(latest([null, null])).toBeNull();
    expect(peak([1, 9, null, 3])).toBe(9);
    expect(peak([null])).toBeNull();
  });

  it('names levels by each metric’s thresholds, and none for plain figures or gaps', () => {
    expect(levelOf('cpu', 79)).toBe('ok');
    expect(levelOf('cpu', 80)).toBe('warn');
    expect(levelOf('cpu', 90)).toBe('danger');
    expect(levelOf('heap', 90)).toBe('warn');
    expect(levelOf('heap', 95)).toBe('danger');
    expect(levelOf('threads', 99999)).toBe('ok');
    expect(levelOf('cpu', null)).toBe('ok');
  });

  it('formats each unit, and n/a for a gap', () => {
    expect(formatMetric('cpu', 87.4)).toBe('87%');
    expect(formatMetric('gc', 1234)).toBe('1,234 ms');
    expect(formatMetric('threads', 1500)).toBe('1,500');
    expect(formatMetric('mem', null)).toBe('n/a');
  });

  it('finds an agent’s figure, undefined when the controller has no series for it', () => {
    const lookup = { jobs: new Map(), agent: (id: string | undefined) => metrics().agents.find((a) => a.instanceId === id) };
    expect(agentFigure(lookup, HOT, 'cpu')).toBe(96);
    expect(agentFigure(lookup, OLD_AMI, 'heap')).toBeNull();
    expect(agentFigure(lookup, 'i-unknown', 'cpu')).toBeUndefined();
    expect(agentFigure(undefined, HOT, 'cpu')).toBeUndefined();
  });

  it('calls figures stale when flagged or older than two minutes', () => {
    const now = Date.parse('2026-10-07T10:00:00Z');
    expect(isStale(undefined, now)).toBe(false);
    expect(isStale(metrics({ generatedAt: '2026-10-07T09:59:30Z' }), now)).toBe(false);
    expect(isStale(metrics({ generatedAt: '2026-10-07T09:57:00Z' }), now)).toBe(true);
    expect(isStale(metrics({ generatedAt: '2026-10-07T09:59:59Z', stale: true }), now)).toBe(true);
  });
});

describe('agent metrics in the tracker', () => {
  beforeEach(() => localStorage.removeItem('tank.jobs.view'));

  it('shows the Metrics view: hottest agent first, levels in words, n/a for an agent without JVM figures', async () => {
    renderApp('/jobs', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Metrics' }));

    const rows = within(await screen.findByRole('list', { name: 'Agents' })).getAllByRole('listitem');
    expect(rows).toHaveLength(3);
    expect(rows[0]).toHaveTextContent(HOT);
    expect(rows[0]).toHaveTextContent('96% Critical');
    expect(rows[0]).toHaveTextContent('CPU-bound');
    expect(within(rows[0]!).getByRole('meter', { name: 'CPU' })).toHaveAttribute('aria-valuenow', '96');
    // its newest CPU sample is its last present one, 55%, so it sits between the other two
    expect(rows[1]).toHaveTextContent(OLD_AMI);
    expect(rows[1]).toHaveTextContent('No JVM metrics');
    expect(within(rows[1]!).getAllByText('n/a').length).toBeGreaterThan(0);
    expect(screen.getByText('1 of 3')).toBeInTheDocument();
  });

  it('opens an agent’s charts, and explains an agent with no JVM figures', async () => {
    renderApp('/jobs', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Metrics' }));
    await userEvent.click(await screen.findByRole('button', { name: new RegExp(OLD_AMI) }));

    expect(screen.getAllByText(/predates the JMX collection/).length).toBeGreaterThan(0);
    expect(screen.getByText('TCP connections established')).toBeInTheDocument();
  });

  it('orders by the chosen metric', async () => {
    renderApp('/jobs', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Metrics' }));
    await screen.findByRole('list', { name: 'Agents' });
    await userEvent.click(screen.getByRole('button', { name: 'Instance' }));

    const rows = within(screen.getByRole('list', { name: 'Agents' })).getAllByRole('listitem');
    expect(rows[0]).toHaveTextContent(HOT);
    expect(rows[1]).toHaveTextContent(COOL);
    expect(rows[2]).toHaveTextContent(OLD_AMI);
  });

  it('warns when the controller’s figures are behind', async () => {
    renderApp('/jobs', handlers({ 'GET /v2/jobs/12/agent-metrics': () => ({ status: 200, body: metrics({ stale: true }) }) }));
    await userEvent.click(await screen.findByRole('button', { name: 'Metrics' }));

    expect(await screen.findByText(/Metrics are behind/)).toBeInTheDocument();
  });

  it('says so when there are no figures yet', async () => {
    renderApp('/jobs', handlers({ 'GET /v2/jobs/12/agent-metrics': () => ({ status: 404 }) }));
    await userEvent.click(await screen.findByRole('button', { name: 'Metrics' }));

    expect(await screen.findByText(/No metrics yet for this job/)).toBeInTheDocument();
  });

  it('shades the heat map by CPU on a fixed scale, with no-data tiles where there is no figure', async () => {
    renderApp('/jobs', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Heat map' }));
    await userEvent.click(await screen.findByRole('button', { name: 'CPU' }));

    const job = await screen.findByRole('region', { name: 'Job Checkout load 1' });
    await waitFor(() => expect(within(job).getByRole('button', { name: new RegExp(HOT) })).toHaveTextContent('96%'));
    expect(within(job).getByRole('button', { name: new RegExp(HOT) })).toHaveClass('heat-orange-4');
    expect(within(job).getByRole('button', { name: new RegExp(COOL) })).toHaveClass('heat-orange-1');
    // heap is not reported by the old-AMI agent
    await userEvent.click(screen.getByRole('button', { name: 'Heap' }));
    expect(within(job).getByRole('button', { name: new RegExp(OLD_AMI) })).toHaveClass('heat-nodata');
    expect(screen.getByText(/Shaded on a fixed scale: under 25%, 25–50%, 50–75%, 75–90%, 90% and over\./)).toBeInTheDocument();
  });

  it('counts hot agents among those needing attention, and charts the picked one in the panel', async () => {
    renderApp('/jobs', handlers());
    await userEvent.click(await screen.findByRole('button', { name: 'Heat map' }));

    expect(await screen.findByText(/1 at 85% CPU or more/)).toBeInTheDocument();
    const job = await screen.findByRole('region', { name: 'Job Checkout load 1' });
    await userEvent.click(within(job).getByRole('button', { name: new RegExp(HOT) }));
    const panel = screen.getByRole('complementary', { name: 'Selected agent' });
    expect(await within(panel).findByRole('img', { name: /CPU over the last 30 minutes, now 96%/ })).toBeInTheDocument();
  });

  it('adds CPU, Memory and Heap columns to the table: a job shows its agents’ average, heap the highest', async () => {
    renderApp('/jobs', handlers());

    expect(await screen.findByRole('columnheader', { name: 'CPU' })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: 'Memory' })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: 'Heap' })).toBeInTheDocument();
    const jobRow = (await screen.findByRole('button', { name: 'Checkout load 1' })).closest('tr')!;
    // CPU (96 + 40 + 55) / 3
    await waitFor(() => expect(within(jobRow).getByRole('meter', { name: 'CPU' })).toHaveAttribute('aria-valuenow', '64'));
    expect(within(jobRow).getByRole('meter', { name: 'Heap' })).toHaveAttribute('aria-valuenow', '96');
    expect(within(jobRow).getByText('highest')).toBeInTheDocument();
  });

  it('asks for no metrics in a view that shows none', async () => {
    localStorage.setItem('tank.jobs.view', 'jobs');
    const { calls } = renderApp('/jobs', handlers());
    await screen.findAllByText('Checkout load 1');
    expect(calls('GET /v2/jobs/12/agent-metrics')).toHaveLength(0);
  });
});
