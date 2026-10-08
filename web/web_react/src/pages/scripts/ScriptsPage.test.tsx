import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

/** TableColumnDefaults.SCRIPTS_COL_PREFS */
const SCRIPT_COLUMNS = [
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

const SCRIPTS = [
  { id: 3, name: 'login', productName: 'Store', owner: 'alice', modified: '2026-10-01T10:00:00Z' },
  { id: 4, name: 'search', productName: 'Store', owner: 'bob', modified: '2026-09-30T10:00:00Z' },
];

const AUTHOR = { name: 'alice', rights: { CREATE_SCRIPT: true } };

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: AUTHOR }),
    'GET /v2/me/preferences': () => ({ status: 200, body: { tables: { scripts: SCRIPT_COLUMNS } } }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    'GET /v2/scripts': () => ({ status: 200, body: { items: SCRIPTS, total: 2, page: 0, size: 25 } }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    'GET /v2/scripts/42/steps': () => ({
      status: 200,
      body: { id: 42, name: 'checkout', owner: 'alice', steps: [], permissions: { edit: true, delete: true } },
    }),
    ...overrides,
  };
}

async function rows() {
  await screen.findByRole('link', { name: 'login' });
  return screen.getAllByRole('row').slice(1);
}

describe('scripts list', () => {
  it('lists scripts with what the user may do to each', async () => {
    const { calls } = renderApp('/scripts', handlers());
    const [own, others] = await rows();

    expect(calls('GET /v2/scripts')[0]!.get('sort')).toBe('modified,desc');
    expect(within(own!).getByRole('button', { name: 'Delete login' })).toBeInTheDocument();
    expect(within(others!).queryByRole('button', { name: 'Delete search' })).not.toBeInTheDocument();
    expect(within(others!).getByRole('button', { name: 'Copy search' })).toBeInTheDocument();
    expect(within(others!).getByRole('link', { name: 'Download Tank XML search' })).toHaveAttribute(
      'href',
      '/v2/scripts/download/4',
    );
  });

  it('deletes selected scripts', async () => {
    const { calls } = renderApp(
      '/scripts',
      handlers({ 'DELETE /v2/scripts': () => ({ status: 200, body: { deleted: [3], notFound: [] } }) }),
    );
    const [own] = await rows();

    await userEvent.click(within(own!).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Delete 1 selected' }));
    await userEvent.click(within(await screen.findByRole('dialog', { name: 'Delete script' })).getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(calls('DELETE /v2/scripts')[0]?.getAll('ids')).toEqual(['3']));
    expect(await screen.findByText('Script deleted')).toBeInTheDocument();
  });

  it('creates a blank script and opens it', async () => {
    let body: unknown;
    const { router } = renderApp(
      '/scripts',
      handlers({
        'POST /v2/scripts/blank': async (request) => {
          body = await request.json();
          return { status: 201, body: { id: 42, name: 'checkout' } };
        },
      }),
    );
    await rows();

    await userEvent.click(screen.getByRole('button', { name: 'New script' }));
    await userEvent.type(await screen.findByLabelText('Name'), 'checkout');
    await userEvent.type(screen.getByLabelText('Comments'), 'Pays for the cart');
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));

    expect(await screen.findByRole('heading', { name: 'checkout' })).toBeInTheDocument();
    expect(router.state.location.pathname).toBe('/scripts/42');
    expect(body).toEqual({ name: 'checkout', comments: 'Pays for the cart' });
  });

  it('creates a script from a recording with filters, in list order', async () => {
    let upload: { query: URLSearchParams; multipart: boolean } | undefined;
    renderApp(
      '/scripts',
      handlers({
        'GET /v2/filters': () => ({
          status: 200,
          body: { filters: [{ id: 5, name: 'Strip images' }, { id: 6, name: 'Strip analytics' }, { id: 7, name: 'Host to variable' }] },
        }),
        'GET /v2/filters/groups': () => ({
          status: 200,
          body: { filterGroups: [{ id: 1, name: 'Store defaults', filterIds: [7, 5] }] },
        }),
        // the multipart body itself is checked in scriptUpload.test.ts: jsdom files can't be read here
        'POST /v2/scripts': (request) => {
          upload = {
            query: new URL(request.url).searchParams,
            multipart: !(request.headers.get('Content-Type') ?? '').includes('application/json'),
          };
          return { status: 201, body: { scriptId: '42', message: 'Script with new script ID 42 has been uploaded' } };
        },
      }),
    );
    await rows();

    await userEvent.click(screen.getByRole('button', { name: 'New script' }));
    const dialog = await screen.findByRole('dialog', { name: 'New script' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'From a recording' }));
    await userEvent.type(within(dialog).getByLabelText('Name'), 'checkout');
    await userEvent.upload(within(dialog).getByLabelText('Recording'), new File(['<recording/>'], 'checkout.xml', { type: 'text/xml' }));
    await userEvent.click(await within(dialog).findByLabelText(/Store defaults/));
    expect(within(dialog).getByLabelText('Host to variable')).toBeChecked();
    expect(within(dialog).getByLabelText('Strip analytics')).not.toBeChecked();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Create' }));

    expect(await screen.findByRole('heading', { name: 'checkout' })).toBeInTheDocument();
    expect(upload!.query.has('recording')).toBe(true);
    expect(upload!.query.get('name')).toBe('checkout');
    expect(upload!.query.getAll('filterIds')).toEqual(['5', '7']);
    expect(upload!.multipart).toBe(true);
  });

  it('imports a Tank XML script', async () => {
    let query: URLSearchParams | undefined;
    const { calls } = renderApp(
      '/scripts',
      handlers({
        'POST /v2/scripts': (request) => {
          query = new URL(request.url).searchParams;
          return { status: 201, body: { scriptId: '3', message: 'Script login with script ID 3 updated successfully' } };
        },
      }),
    );
    await rows();

    await userEvent.click(screen.getByRole('button', { name: 'Import Tank XML' }));
    await userEvent.upload(await screen.findByLabelText('Script file'), new File(['<script/>'], 'login.xml'));
    await userEvent.click(screen.getByRole('button', { name: 'Import' }));

    expect(await screen.findByText('Script login with script ID 3 updated successfully')).toBeInTheDocument();
    expect(query!.has('recording')).toBe(false);
    await waitFor(() => expect(calls('GET /v2/scripts').length).toBeGreaterThan(1));
  });

  it('copies a script', async () => {
    let body: unknown;
    renderApp(
      '/scripts',
      handlers({
        'POST /v2/scripts/4/copy': async (request) => {
          body = await request.json();
          return { status: 201, body: { id: 9, name: 'Copy of search' } };
        },
      }),
    );
    const [, others] = await rows();

    await userEvent.click(within(others!).getByRole('button', { name: 'Copy search' }));
    await userEvent.click(await screen.findByRole('button', { name: 'Copy' }));

    expect(await screen.findByText('Script copied')).toBeInTheDocument();
    expect(body).toEqual({ name: 'Copy of search' });
  });

  it("can't create without the right", async () => {
    renderApp('/scripts', handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'viewer', rights: {} } }) }));
    await rows();

    expect(screen.getByRole('button', { name: 'New script' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Import Tank XML' })).toBeDisabled();
  });
});
