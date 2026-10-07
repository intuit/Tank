import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { Schemas } from '../../../api/client';
import { renderApp, type Handlers } from '../../../test/renderApp';
import { capturePut, detail, editorHandlers } from './testFixtures';
import type { ProjectDetail } from './validation';

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

function tree(jobs: Schemas['JobNode'][]): Schemas['JobTree'] {
  return {
    generatedAt: '2026-10-07T10:00:00Z',
    projects: [{ projectId: 7, name: 'Checkout load', jobs }],
    otherJobs: [],
  };
}

const RUNNING_JOB: Schemas['JobNode'] = {
  jobId: '12',
  name: 'Checkout load 1',
  status: 'Running',
  incrementStrategy: 'increasing',
  activeUsers: 80,
  totalUsers: 150,
  tps: 42,
  failures: { total: 3 },
  useTwoStep: false,
  actions: { ...NONE, pauseRamp: true, stop: true, kill: true },
  agents: [
    {
      instanceId: 'i-0abc',
      region: 'US East (Ohio)',
      status: 'running',
      activeUsers: 80,
      totalUsers: 100,
      tps: 42,
      wsState: 'connected',
      actions: { ...NONE, stop: true, kill: true },
    },
    { instanceId: 'i-0def', region: 'US West (Oregon)', status: 'starting', actions: { ...NONE } },
  ],
};

const CREATED_JOB: Schemas['JobNode'] = {
  jobId: '13',
  name: 'Checkout load 2',
  status: 'Created',
  incrementStrategy: 'increasing',
  actions: { ...NONE, start: true, delete: true },
  agents: [],
};

function queueHandlers(overrides: Handlers = {}): Handlers {
  return editorHandlers(detail(), {
    'GET /v2/jobs/tree': () => ({ status: 200, body: tree([RUNNING_JOB, CREATED_JOB]) }),
    ...overrides,
  });
}

async function openTab(name: string, handlers: Handlers) {
  const app = renderApp('/projects/7', handlers);
  await screen.findByLabelText('Name');
  await userEvent.click(screen.getByRole('tab', { name }));
  return app;
}

describe('job queue tab', () => {
  it("shows the project's jobs with live figures", async () => {
    const { calls } = await openTab('Job queue', queueHandlers());

    expect(await screen.findByRole('button', { name: 'Checkout load 1' })).toBeInTheDocument();
    expect(screen.getByText('1/2 agents running')).toBeInTheDocument();
    expect(screen.getByText('80 / 150')).toBeInTheDocument();
    const query = calls('GET /v2/jobs/tree')[0]!;
    expect(query.get('projectId')).toBe('7');
    expect(query.get('includeFinished')).toBe('false');
  });

  it('only offers the actions the server allows', async () => {
    await openTab('Job queue', queueHandlers());
    await screen.findByRole('button', { name: 'Checkout load 1' });

    expect(screen.getByRole('button', { name: 'Stop Checkout load 1' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Pause ramp Checkout load 1' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Run Checkout load 1' })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Run Checkout load 2' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Delete Checkout load 2' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Delete Checkout load 1' })).not.toBeInTheDocument();
  });

  it('sends job actions, confirming a kill first', async () => {
    const { calls } = await openTab(
      'Job queue',
      queueHandlers({
        'POST /v2/jobs/12/stop': () => ({ status: 200, body: { id: '12', action: 'stop', status: 'sent' } }),
        'POST /v2/jobs/12/kill': () => ({ status: 200, body: { id: '12', action: 'kill', status: 'sent' } }),
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Stop Checkout load 1' }));
    await waitFor(() => expect(calls('POST /v2/jobs/12/stop')).toHaveLength(1));

    await userEvent.click(screen.getByRole('button', { name: 'Kill Checkout load 1' }));
    const dialog = await screen.findByRole('dialog', { name: 'Kill job' });
    expect(calls('POST /v2/jobs/12/kill')).toHaveLength(0);
    await userEvent.click(within(dialog).getByRole('button', { name: 'Kill' }));
    await waitFor(() => expect(calls('POST /v2/jobs/12/kill')).toHaveLength(1));
  });

  it('controls a single agent', async () => {
    const { calls } = await openTab(
      'Job queue',
      queueHandlers({
        'POST /v2/jobs/instances/i-0abc/stop': () => ({ status: 200, body: { id: 'i-0abc', action: 'stop', status: 'sent' } }),
      }),
    );
    await screen.findByRole('button', { name: 'Checkout load 1' });
    await userEvent.click(screen.getAllByRole('button', { name: /toggle|expand/i })[0]!);

    await userEvent.click(await screen.findByRole('button', { name: 'Stop i-0abc' }));
    await waitFor(() => expect(calls('POST /v2/jobs/instances/i-0abc/stop')).toHaveLength(1));
  });

  it('deletes a job that has not started', async () => {
    const { calls } = await openTab('Job queue', queueHandlers({ 'DELETE /v2/jobs/13': () => ({ status: 204 }) }));

    await userEvent.click(await screen.findByRole('button', { name: 'Delete Checkout load 2' }));
    await userEvent.click(within(await screen.findByRole('dialog', { name: 'Delete job' })).getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(calls('DELETE /v2/jobs/13')).toHaveLength(1));
    expect(await screen.findByText('Job deleted')).toBeInTheDocument();
  });

  it('can include finished jobs', async () => {
    const { calls } = await openTab('Job queue', queueHandlers());
    await screen.findByRole('button', { name: 'Checkout load 1' });

    await userEvent.click(screen.getByLabelText('Show finished jobs'));

    await waitFor(() => expect(calls('GET /v2/jobs/tree').at(-1)?.get('includeFinished')).toBe('true'));
  });
});

describe('job charts', () => {
  const TIMES = ['2026-10-07T10:00:00Z', '2026-10-07T10:01:00Z'];
  const chartHandlers = queueHandlers({
    'GET /v2/jobs/12/details': () => ({ status: 200, body: { jobId: 12, name: 'Checkout load 1', status: 'Running', failures: { total: 3 } } }),
    'GET /v2/jobs/12/users-timeseries': () => ({
      status: 200,
      body: { times: TIMES, series: [{ name: 'checkout', values: [10, 40] }, { name: 'login', values: [20, 40] }] },
    }),
    'GET /v2/jobs/12/tps-timeseries': () => ({
      status: 200,
      body: {
        times: TIMES,
        series: [
          { name: 'Total TPS', values: [30, 50] },
          { name: 'GET /cart', values: [10, 15] },
          { name: 'POST /pay', values: [20, 35] },
        ],
      },
    }),
  });

  it("shows a job's users and TPS, with every request in the table view", async () => {
    await openTab('Job queue', chartHandlers);
    await userEvent.click(await screen.findByRole('button', { name: 'Charts for Checkout load 1' }));
    const dialog = await screen.findByRole('dialog', { name: 'Job 12: Checkout load 1' });

    expect(await within(dialog).findByText('Active users by script')).toBeInTheDocument();
    expect(within(dialog).getByRole('list', { name: 'Active users by script series' })).toHaveTextContent('checkoutlogin');
    expect(within(dialog).getByText('Transactions per second')).toBeInTheDocument();
    // one line for the total: no legend
    expect(within(dialog).queryByRole('list', { name: 'Transactions per second series' })).not.toBeInTheDocument();

    const showTables = within(dialog).getAllByRole('button', { name: 'Show table' });
    await userEvent.click(showTables[1]!);
    const rows = within(dialog).getAllByRole('row').map((r) => r.textContent);
    expect(rows).toContain('POST /pay353527.5');
    expect(rows.indexOf('POST /pay353527.5')).toBeLessThan(rows.indexOf('GET /cart151512.5'));
  });

  it("charts one agent's TPS", async () => {
    const { calls } = await openTab('Job queue', chartHandlers);
    await screen.findByRole('button', { name: 'Checkout load 1' });
    await userEvent.click(screen.getAllByRole('button', { name: /toggle|expand/i })[0]!);
    await userEvent.click(await screen.findByRole('button', { name: 'Charts for i-0abc' }));

    expect(await screen.findByText('Transactions per second on i-0abc')).toBeInTheDocument();
    await waitFor(() => expect(calls('GET /v2/jobs/12/tps-timeseries').at(-1)?.get('instanceId')).toBe('i-0abc'));
    expect(calls('GET /v2/jobs/12/users-timeseries')).toHaveLength(0);
  });
});

describe('create job tab', () => {
  const PREVIEW = {
    name: 'Checkout load 3',
    valid: true,
    totalUsers: 150,
    rampTimeMs: 600_000,
    simulationTimeMs: 3_600_000,
    executionTimeMs: 3_600_000,
    errors: [],
    warnings: ['Data file users.csv has only 2 lines for 150 users'],
  };

  it('previews, queues and then shows the queue', async () => {
    let queued: unknown;
    const { calls } = await openTab(
      'Create job',
      queueHandlers({
        'POST /v2/projects/7/jobs/preview': () => ({ status: 200, body: PREVIEW }),
        'POST /v2/projects/7/jobs': async (request) => {
          queued = await request.json();
          return { status: 201, body: { jobId: 14, name: 'Checkout load 3', status: 'Created' } };
        },
      }),
    );

    await userEvent.click(screen.getByRole('button', { name: 'Validate and queue…' }));
    const dialog = await screen.findByRole('dialog', { name: 'Queue this job?' });
    expect(within(dialog).getByText('10m')).toBeInTheDocument();
    expect(within(dialog).getAllByText('1h')).toHaveLength(2); // simulation and expected run time
    expect(within(dialog).getByText('Data file users.csv has only 2 lines for 150 users')).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Queue job' }));

    expect(await screen.findByText('Job queued')).toBeInTheDocument();
    expect(queued).toEqual({ name: 'Checkout load 3' });
    // nothing to save, so no PUT
    expect(calls('PUT /v2/projects/7/full')).toHaveLength(0);
    expect(screen.getByRole('tab', { name: 'Job queue', selected: true })).toBeInTheDocument();
  });

  it('saves unsaved changes before checking the job', async () => {
    const bodies: ProjectDetail[] = [];
    const order: string[] = [];
    await openTab(
      'Create job',
      queueHandlers({
        'PUT /v2/projects/7/full': async (request) => {
          order.push('save');
          return capturePut(bodies)(request);
        },
        'POST /v2/projects/7/jobs/preview': () => {
          order.push('preview');
          return { status: 200, body: PREVIEW };
        },
      }),
    );

    await userEvent.click(screen.getByLabelText('Two-step start'));
    await userEvent.click(screen.getByRole('button', { name: 'Validate and queue…' }));

    await screen.findByRole('dialog', { name: 'Queue this job?' });
    expect(order).toEqual(['save', 'preview']);
    expect(bodies[0]!.settings!.useTwoStep).toBe(true);
  });

  it("explains a job that can't run", async () => {
    await openTab(
      'Create job',
      queueHandlers({
        'POST /v2/projects/7/jobs/preview': () => ({
          status: 200,
          body: { ...PREVIEW, valid: false, errors: ['Test plan Main has no scripts'] },
        }),
      }),
    );

    await userEvent.click(screen.getByRole('button', { name: 'Validate and queue…' }));
    const dialog = await screen.findByRole('dialog', { name: "This job can't run yet" });
    expect(within(dialog).getByText('Test plan Main has no scripts')).toBeInTheDocument();
    expect(within(dialog).queryByRole('button', { name: 'Queue job' })).not.toBeInTheDocument();
  });

  it('needs the Control Job right on projects the user does not own', async () => {
    await openTab(
      'Create job',
      editorHandlers(detail({ owner: 'bob' }), {
        'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { EDIT_PROJECT: true } } }),
      }),
    );

    expect(screen.getByRole('button', { name: 'Validate and queue…' })).toBeDisabled();
    expect(screen.getByText(/Ask a Tank admin for the Control Job right/)).toBeInTheDocument();
  });

  it('resets users per agent when the instance type changes', async () => {
    await openTab(
      'Create job',
      editorHandlers(detail(), {
        'GET /v2/config/options': () => ({
          status: 200,
          body: {
            products: [],
            vmInstanceTypes: [
              { value: 'c5.large', label: 'c5.large', usersPerAgent: 1000, isDefault: true },
              { value: 'c5.xlarge', label: 'c5.xlarge', usersPerAgent: 2500 },
            ],
          },
        }),
      }),
    );

    expect(screen.getByLabelText('Users per agent')).toHaveValue('4000');
    await userEvent.click(screen.getByLabelText('Agent instance type').closest('.p-dropdown')!);
    // the overlay stays in its enter transition in jsdom, which hides it from role queries
    await userEvent.click(await screen.findByRole('option', { name: 'c5.xlarge', hidden: true }));

    await waitFor(() => expect(screen.getByLabelText('Users per agent')).toHaveValue('2500'));
  });
});
