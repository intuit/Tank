import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, USERS, type Handlers } from '../../test/renderApp';

/** TableColumnDefaults.PROJECT_COL_PREFS */
const PROJECT_COLUMNS = [
  { colName: 'selectColumn', displayName: 'Select', size: 20, visible: true, hideable: false },
  { colName: 'idColumn', displayName: 'ID', size: 75, visible: false, hideable: true },
  { colName: 'nameColumn', displayName: 'Name', size: 250, visible: true, hideable: true },
  { colName: 'productColumn', displayName: 'Product', size: 135, visible: true, hideable: true },
  { colName: 'commentsColumn', displayName: 'Comments', size: 110, visible: false, hideable: true },
  { colName: 'createColumn', displayName: 'Create Time', size: 110, visible: true, hideable: true },
  { colName: 'modifiedColumn', displayName: 'Modified Time', size: 110, visible: true, hideable: true },
  { colName: 'ownerColumn', displayName: 'Owner', size: 100, visible: true, hideable: true },
  { colName: 'actionsColumn', displayName: 'Actions', size: 75, visible: true, hideable: false },
];

const PROJECTS = [
  { id: 1, name: 'Checkout load', productName: 'Store', owner: 'alice', modified: '2026-10-01T10:00:00Z' },
  { id: 2, name: 'Search soak', productName: 'Store', owner: 'bob', modified: '2026-09-30T10:00:00Z' },
];

function listHandlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me/preferences': () => ({ status: 200, body: { tables: { projects: PROJECT_COLUMNS } } }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    'GET /v2/projects': () => ({ status: 200, body: { items: PROJECTS, total: 2, page: 0, size: 25 } }),
    ...overrides,
  };
}

async function rows() {
  await screen.findByRole('link', { name: 'Checkout load' });
  return screen.getAllByRole('row').slice(1); // without the header row
}

describe('projects list', () => {
  it('loads the first page sorted by modified date, newest first', async () => {
    const { calls } = renderApp('/projects', listHandlers());

    expect(await rows()).toHaveLength(2);
    const query = calls('GET /v2/projects')[0]!;
    expect(query.get('page')).toBe('0');
    expect(query.get('size')).toBe('25');
    expect(query.get('sort')).toBe('modified,desc');
    expect(screen.getByRole('columnheader', { name: /Owner/ })).toBeInTheDocument();
    // ID and Comments are hidden by default
    expect(screen.queryByRole('columnheader', { name: 'ID' })).not.toBeInTheDocument();
  });

  it('searches after typing stops, from the first page', async () => {
    const { calls, router } = renderApp('/projects?page=3', listHandlers());
    await rows();

    await userEvent.type(screen.getByLabelText('Search projects'), 'soak');

    await waitFor(() => expect(calls('GET /v2/projects').at(-1)?.get('q')).toBe('soak'));
    expect(calls('GET /v2/projects').at(-1)?.get('page')).toBe('0');
    expect(router.state.location.search).toBe('?q=soak');
  });

  it('sorts by a column', async () => {
    const { calls } = renderApp('/projects', listHandlers());
    await rows();

    await userEvent.click(screen.getByRole('columnheader', { name: /Name/ }));

    await waitFor(() => expect(calls('GET /v2/projects').at(-1)?.get('sort')).toBe('name,asc'));
  });

  it('only offers delete on projects the user may delete', async () => {
    renderApp('/projects', listHandlers());
    const [own, others] = await rows();

    // alice owns "Checkout load" but has no DELETE_PROJECT right
    expect(within(own!).getByRole('button', { name: 'Delete Checkout load' })).toBeInTheDocument();
    expect(within(others!).queryByRole('button', { name: 'Delete Search soak' })).not.toBeInTheDocument();
  });

  it('deletes selected projects after confirming, then reloads the page', async () => {
    const { calls } = renderApp(
      '/projects',
      listHandlers({
        'GET /v2/me': () => ({ status: 200, body: USERS.admin }),
        'DELETE /v2/projects': () => ({ status: 200, body: { deleted: [1, 2], notFound: [] } }),
      }),
    );
    const [first, second] = await rows();

    await userEvent.click(within(first!).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(within(second!).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Delete 2 selected' }));
    const dialog = await screen.findByRole('dialog', { name: 'Delete 2 projects' });
    expect(dialog).toHaveTextContent('Delete these 2 projects?');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(calls('DELETE /v2/projects')).toHaveLength(1));
    expect(calls('DELETE /v2/projects')[0]!.getAll('ids')).toEqual(['1', '2']);
    expect(await screen.findByText('2 projects deleted')).toBeInTheDocument();
    await waitFor(() => expect(calls('GET /v2/projects').length).toBeGreaterThan(1));
  });

  it('shows why a delete was refused', async () => {
    renderApp(
      '/projects',
      listHandlers({
        'DELETE /v2/projects': () => ({ status: 403, body: { message: 'Not allowed to delete project 1' } }),
      }),
    );
    const [own] = await rows();

    await userEvent.click(within(own!).getByRole('button', { name: 'Delete Checkout load' }));
    const dialog = await screen.findByRole('dialog', { name: 'Delete project' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Delete' }));

    expect(await screen.findByText('Not allowed to delete project 1')).toBeInTheDocument();
  });

  it('creates a project and opens it', async () => {
    let body: unknown;
    const { router } = renderApp(
      '/projects',
      listHandlers({
        'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
        'POST /v2/projects': async (request) => {
          body = await request.json();
          return { status: 201, body: { ProjectId: '42', status: 'Created' } };
        },
        'GET /v2/projects/42/full': () => ({ status: 200, body: { id: 42, name: 'New load test', owner: 'alice' } }),
      }),
    );
    await rows();

    await userEvent.click(screen.getByRole('button', { name: 'New project' }));
    await userEvent.type(await screen.findByLabelText('Name'), 'New load test');
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));

    expect(await screen.findByRole('heading', { name: 'New load test' })).toBeInTheDocument();
    expect(router.state.location.pathname).toBe('/projects/42');
    expect(body).toMatchObject({ name: 'New load test', variables: {} });
  });

  it('keeps the create dialog open with the server error', async () => {
    renderApp(
      '/projects',
      listHandlers({
        'GET /v2/config/options': () => ({ status: 200, body: { products: [] } }),
        'POST /v2/projects': () => ({ status: 400, body: { message: 'A project named Checkout load already exists' } }),
      }),
    );
    await rows();

    await userEvent.click(screen.getByRole('button', { name: 'New project' }));
    await userEvent.type(await screen.findByLabelText('Name'), 'Checkout load');
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));

    expect(await screen.findByText('A project named Checkout load already exists')).toBeInTheDocument();
    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });

  it('copies a project under a new name', async () => {
    let body: unknown;
    const { calls } = renderApp(
      '/projects',
      listHandlers({
        'POST /v2/projects/1/copy': async (request) => {
          body = await request.json();
          return { status: 201, body: { id: 3, name: 'Copy of Checkout load' } };
        },
      }),
    );
    const [own] = await rows();

    await userEvent.click(within(own!).getByRole('button', { name: 'Copy Checkout load' }));
    expect(await screen.findByLabelText('New name')).toHaveValue('Copy of Checkout load');
    await userEvent.click(screen.getByRole('button', { name: 'Copy' }));

    expect(await screen.findByText('Project copied')).toBeInTheDocument();
    expect(body).toEqual({ name: 'Copy of Checkout load' });
    await waitFor(() => expect(calls('GET /v2/projects').length).toBeGreaterThan(1));
  });

  it('disables New project without the create right', async () => {
    renderApp('/projects', listHandlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'viewer', rights: {} } }) }));
    await rows();

    expect(screen.getByRole('button', { name: 'New project' })).toBeDisabled();
    expect(screen.queryByRole('button', { name: /^Copy / })).not.toBeInTheDocument();
  });

  it('shows the error when projects fail to load', async () => {
    renderApp(
      '/projects',
      listHandlers({ 'GET /v2/projects': () => ({ status: 500, body: { message: 'Database unavailable' } }) }),
    );

    expect(await screen.findByText('Database unavailable')).toBeInTheDocument();
  });
});
