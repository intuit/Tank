import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, USERS, type Handlers } from '../../../test/renderApp';
import type { ProjectDetail } from './validation';

const MODIFIED = '2026-10-05T12:00:00Z';

function detail(overrides: Partial<ProjectDetail> = {}): ProjectDetail {
  return {
    id: 7,
    name: 'Checkout load',
    productName: 'Store',
    owner: 'alice',
    comments: 'Nightly',
    created: '2026-09-01T12:00:00Z',
    modified: MODIFIED,
    permissions: { edit: true, delete: true },
    settings: {
      incrementStrategy: 'increasing',
      terminationPolicy: 'time',
      simulationTime: '1h',
      rampTime: '10m',
      baselineVirtualUsers: 0,
      userIntervalIncrement: 1,
      numUsersPerAgent: 4000,
      targetRatePerAgent: 2.5,
      allowOverride: false,
    },
    regions: [
      { region: 'US East (Ohio)', users: '100', percentage: '0' },
      { region: 'US West (Oregon)', users: '50', percentage: '0' },
    ],
    testPlans: [
      {
        name: 'Main',
        userPercentage: 100,
        scriptGroups: [{ name: 'Login', loop: 1, scripts: [{ scriptId: 3, scriptName: 'login', loop: 1 }] }],
      },
    ],
    variables: { env: 'qa' },
    dataFileIds: [11],
    ...overrides,
  };
}

function editorHandlers(project: ProjectDetail = detail(), overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/projects/7/full': () => ({ status: 200, body: project }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    'GET /v2/datafiles/names': () => ({ status: 200, body: { '11': 'users.csv', '12': 'cards.csv' } }),
    'GET /v2/datafiles': () => ({
      status: 200,
      body: { items: [{ id: 11, name: 'users.csv' }, { id: 12, name: 'cards.csv' }], total: 2, page: 0, size: 10 },
    }),
    'GET /v2/scripts': () => ({
      status: 200,
      body: { items: [{ id: 3, name: 'login' }, { id: 4, name: 'search' }], total: 2, page: 0, size: 10 },
    }),
    ...overrides,
  };
}

/** Records PUT bodies and answers with the saved project */
function capturePut(bodies: ProjectDetail[]) {
  return async (request: Request) => {
    const body = (await request.json()) as ProjectDetail;
    bodies.push(body);
    return { status: 200, body: { ...body, modified: '2026-10-07T09:00:00Z' } };
  };
}

async function openEditor(handlers: Handlers) {
  const app = renderApp('/projects/7', handlers);
  await screen.findByLabelText('Name');
  return app;
}

describe('project editor', () => {
  it('saves edits with the modified time it loaded', async () => {
    const bodies: ProjectDetail[] = [];
    await openEditor(editorHandlers(detail(), { 'PUT /v2/projects/7/full': capturePut(bodies) }));
    const save = screen.getByRole('button', { name: 'Save' });
    expect(save).toBeDisabled();

    const name = screen.getByLabelText('Name');
    await userEvent.clear(name);
    await userEvent.type(name, 'Checkout peak');
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();
    await userEvent.click(save);

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toMatchObject({ name: 'Checkout peak', modified: MODIFIED, variables: { env: 'qa' } });
    // untouched Create Job settings go back as they came
    expect(bodies[0]!.settings).toMatchObject({ numUsersPerAgent: 4000, targetRatePerAgent: 2.5 });
    await waitFor(() => expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument());
    expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
  });

  it('offers to reload when someone else saved first', async () => {
    let loads = 0;
    await openEditor(
      editorHandlers(detail(), {
        'GET /v2/projects/7/full': () => {
          loads += 1;
          return { status: 200, body: loads === 1 ? detail() : detail({ name: 'Renamed by bob', modified: '2026-10-06T00:00:00Z' }) };
        },
        'PUT /v2/projects/7/full': () => ({ status: 409, body: { message: 'changed by someone else' } }),
      }),
    );

    await userEvent.type(screen.getByLabelText('Comments'), ' run');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    const dialog = await screen.findByRole('dialog', { name: 'Someone else saved this project' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Reload and lose my changes' }));

    await waitFor(() => expect(screen.getByLabelText('Name')).toHaveValue('Renamed by bob'));
    expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument();
  });

  it("doesn't save until problems are fixed, and says where they are", async () => {
    const bodies: ProjectDetail[] = [];
    await openEditor(editorHandlers(detail(), { 'PUT /v2/projects/7/full': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('tab', { name: /Users and times/ }));
    await userEvent.clear(screen.getByLabelText('US East (Ohio)'));
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    expect(await screen.findByText('US East (Ohio) users are required (0 for none)')).toBeInTheDocument();
    expect(bodies).toHaveLength(0);
  });

  it('shows the server error when a save is refused', async () => {
    await openEditor(
      editorHandlers(detail(), {
        'PUT /v2/projects/7/full': () => ({ status: 400, body: { message: 'settings.rampTime cannot be parsed: 10 parsecs' } }),
      }),
    );

    await userEvent.type(screen.getByLabelText('Comments'), '!');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    expect(await screen.findByText('settings.rampTime cannot be parsed: 10 parsecs')).toBeInTheDocument();
  });

  it('is read-only without the edit permission', async () => {
    await openEditor(
      editorHandlers(detail({ owner: 'bob', permissions: { edit: false, delete: false } }), {
        'GET /v2/me': () => ({ status: 200, body: { name: 'viewer', rights: {} } }),
      }),
    );

    expect(screen.getByText('You can view this project but not change it.')).toBeInTheDocument();
    expect(screen.getByLabelText('Name')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Delete project' })).not.toBeInTheDocument();
  });

  it('lets only the owner or an admin change the owner', async () => {
    await openEditor(
      editorHandlers(detail({ owner: 'bob' }), {
        'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { EDIT_PROJECT: true } } }),
      }),
    );
    expect(screen.getByLabelText('Name')).toBeEnabled();
    expect(screen.getByLabelText('Owner')).toBeDisabled();
  });

  it('adds a script group with scripts', async () => {
    const bodies: ProjectDetail[] = [];
    await openEditor(editorHandlers(detail(), { 'PUT /v2/projects/7/full': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('tab', { name: 'Scripts' }));
    await userEvent.click(screen.getByRole('button', { name: 'Add script group' }));
    const dialog = await screen.findByRole('dialog', { name: 'Add script group' });
    await userEvent.type(within(dialog).getByLabelText('Name'), 'Browse');
    await userEvent.click(await within(dialog).findByRole('button', { name: 'Add search' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add group' }));
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]!.testPlans![0]!.scriptGroups).toEqual([
      { name: 'Login', loop: 1, scripts: [{ scriptId: 3, scriptName: 'login', loop: 1 }] },
      { name: 'Browse', loop: 1, scripts: [{ scriptId: 4, scriptName: 'search', loop: 1 }] },
    ]);
  });

  it('adds a data file and a variable', async () => {
    const bodies: ProjectDetail[] = [];
    await openEditor(editorHandlers(detail(), { 'PUT /v2/projects/7/full': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('tab', { name: 'Data files' }));
    expect(await screen.findByRole('button', { name: 'Remove users.csv' })).toBeInTheDocument();
    await userEvent.click(await screen.findByRole('button', { name: 'Add cards.csv' }));

    await userEvent.click(screen.getByRole('tab', { name: 'Variables' }));
    await userEvent.click(screen.getByRole('button', { name: 'Add variable' }));
    await userEvent.type(screen.getByLabelText('Variable 2 name'), 'region');
    await userEvent.type(screen.getByLabelText('Variable 2 value'), 'east');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]!.dataFileIds).toEqual([11, 12]);
    expect(bodies[0]!.variables).toEqual({ env: 'qa', region: 'east' });
  });

  it('asks before leaving with unsaved changes', async () => {
    const { router } = await openEditor(editorHandlers());

    await userEvent.type(screen.getByLabelText('Comments'), '!');
    const breadcrumb = document.querySelector('.breadcrumb') as HTMLElement;
    await userEvent.click(within(breadcrumb).getByRole('link', { name: 'Projects' }));

    const dialog = await screen.findByRole('dialog', { name: 'Unsaved changes' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Keep editing' }));
    expect(router.state.location.pathname).toBe('/projects/7');
  });

  it('shows why a project failed to load', async () => {
    renderApp('/projects/7', editorHandlers(detail(), { 'GET /v2/projects/7/full': () => ({ status: 404, body: {} }) }));

    expect(await screen.findByText("Couldn't load the project: it no longer exists")).toBeInTheDocument();
  });

  it('admins can change the owner', async () => {
    const bodies: ProjectDetail[] = [];
    await openEditor(
      editorHandlers(detail({ owner: 'bob' }), {
        'GET /v2/me': () => ({ status: 200, body: USERS.admin }),
        'PUT /v2/projects/7/full': capturePut(bodies),
      }),
    );
    expect(screen.getByLabelText('Owner')).toBeEnabled();
  });
});
