import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../../test/renderApp';
import type { ScriptDocument, ScriptStep } from './steps';
import { VIRTUAL_SCROLL_FROM } from './StepTable';

const MODIFIED = '2026-10-05T12:00:00Z';

/** TableColumnDefaults.SCRIPT_STEPS_COL_PREFS */
const STEP_COLUMNS = [
  { colName: 'indexColumn', displayName: 'Index', size: 50, visible: true, hideable: false },
  { colName: 'groupColumn', displayName: 'Group Name', size: 125, visible: false, hideable: true },
  { colName: 'nameColumn', displayName: 'Request Name', size: 125, visible: true, hideable: true },
  { colName: 'methodColumn', displayName: 'Method', size: 125, visible: true, hideable: true },
  { colName: 'dataColumn', displayName: 'Data', size: 250, visible: true, hideable: true },
  { colName: 'commentsColumn', displayName: 'Comments', size: 110, visible: true, hideable: true },
  { colName: 'validationColumn', displayName: 'Validation', size: 75, visible: true, hideable: true },
  { colName: 'actionsColumn', displayName: 'Actions', size: 75, visible: true, hideable: false },
];

function steps(): ScriptStep[] {
  return [
    { uuid: 'u1', type: 'request', name: 'home', method: 'GET', label: 'GET /', responseData: [{ type: 'bodyValidation', value: '==200' }] },
    { uuid: 'u2', type: 'thinkTime', label: '1000 - 3000' },
    { uuid: 'u3', type: 'request', name: 'cart', method: 'GET', label: 'GET /cart' },
    { uuid: 'u4', type: 'variable', label: 'host = store.test' },
  ];
}

function doc(overrides: Partial<ScriptDocument> = {}): ScriptDocument {
  return {
    id: 7,
    name: 'checkout',
    productName: 'Store',
    owner: 'alice',
    modified: MODIFIED,
    permissions: { edit: true, delete: true },
    steps: steps(),
    ...overrides,
  };
}

function handlers(script: ScriptDocument = doc(), overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_SCRIPT: true } } }),
    'GET /v2/scripts/7/steps': () => ({ status: 200, body: script }),
    'GET /v2/me/preferences': () => ({ status: 200, body: { tables: { scriptSteps: STEP_COLUMNS } } }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    ...overrides,
  };
}

function capturePut(bodies: ScriptDocument[]) {
  return async (request: Request) => {
    const body = (await request.json()) as ScriptDocument;
    bodies.push(body);
    return { status: 200, body: { ...body, modified: '2026-10-07T09:00:00Z' } };
  };
}

async function open(h: Handlers) {
  const app = renderApp('/scripts/7', h);
  await screen.findByText('GET /cart');
  return app;
}

function rowOf(text: string) {
  return screen.getByText(text).closest('tr')!;
}

const uuids = (body: ScriptDocument) => (body.steps ?? []).map((s) => s.uuid);

describe('script editor', () => {
  it('shows the steps with their type, position and validation', async () => {
    await open(handlers());

    expect(screen.getByRole('heading', { name: 'checkout' })).toBeInTheDocument();
    expect(screen.getByText('4 steps')).toBeInTheDocument();
    expect(within(rowOf('1000 - 3000')).getByText('Think time')).toBeInTheDocument();
    expect(within(rowOf('1000 - 3000')).getByText('2')).toBeInTheDocument();
    expect(within(rowOf('GET /')).getByLabelText('Yes')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
  });

  it('deletes and moves steps, then saves them with the loaded modified time', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.click(within(rowOf('1000 - 3000')).getByRole('button', { name: 'Delete step 2' }));
    expect(screen.getByText('3 steps')).toBeInTheDocument();
    await userEvent.click(within(rowOf('host = store.test')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Move to…' }));
    const dialog = await screen.findByRole('dialog', { name: 'Move 1 step' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Move' }));
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(uuids(bodies[0]!)).toEqual(['u4', 'u1', 'u3']);
    expect(bodies[0]!.modified).toBe(MODIFIED);
    await waitFor(() => expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument());
  });

  it('deletes selected steps and reverts', async () => {
    await open(handlers());

    await userEvent.click(within(rowOf('GET /')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(within(rowOf('GET /cart')).getAllByRole('checkbox').at(-1)!);
    await userEvent.click(screen.getByRole('button', { name: 'Delete 2 selected' }));
    expect(screen.getByText('2 steps')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Revert' }));
    expect(screen.getByText('4 steps')).toBeInTheDocument();
    expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument();
  });

  it('offers to reload when someone else saved first', async () => {
    let loads = 0;
    await open(
      handlers(doc(), {
        'GET /v2/scripts/7/steps': () => {
          loads += 1;
          return { status: 200, body: loads === 1 ? doc() : doc({ name: 'checkout v2', steps: steps().slice(0, 1) }) };
        },
        'PUT /v2/scripts/7/steps': () => ({ status: 409, body: { message: 'changed by someone else' } }),
      }),
    );

    await userEvent.type(screen.getByLabelText('Comments'), 'x');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    const dialog = await screen.findByRole('dialog', { name: 'Someone else saved this script' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Reload and lose my changes' }));

    await waitFor(() => expect(screen.getByLabelText('Name')).toHaveValue('checkout v2'));
    expect(screen.getByText('1 step')).toBeInTheDocument();
  });

  it("doesn't save without a name", async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.clear(screen.getByLabelText('Name'));
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    expect(await screen.findByText('Name is required')).toBeInTheDocument();
    expect(bodies).toHaveLength(0);
  });

  it('is read-only without the edit permission', async () => {
    await open(handlers(doc({ owner: 'bob', permissions: { edit: false, delete: false } })));

    expect(screen.getByText('You can view this script but not change it.')).toBeInTheDocument();
    expect(screen.getByLabelText('Name')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /^Delete step/ })).not.toBeInTheDocument();
  });

  it('saves as a copy only when nothing is unsaved', async () => {
    let copied: unknown;
    const { router } = await open(
      handlers(doc(), {
        'POST /v2/scripts/7/copy': async (request) => {
          copied = await request.json();
          return { status: 201, body: { id: 8, name: 'Copy of checkout' } };
        },
        'GET /v2/scripts/8/steps': () => ({ status: 200, body: doc({ id: 8, name: 'Copy of checkout' }) }),
      }),
    );
    const saveAs = screen.getByRole('button', { name: 'Save as…' });

    await userEvent.type(screen.getByLabelText('Comments'), 'x');
    expect(saveAs).toBeDisabled();
    await userEvent.click(screen.getByRole('button', { name: 'Revert' }));
    await userEvent.click(saveAs);
    await userEvent.click(within(await screen.findByRole('dialog', { name: 'Copy checkout' })).getByRole('button', { name: 'Copy' }));

    await waitFor(() => expect(router.state.location.pathname).toBe('/scripts/8'));
    expect(copied).toEqual({ name: 'Copy of checkout' });
  });

  it('scrolls a large script virtually, without drag handles', async () => {
    const many = Array.from({ length: VIRTUAL_SCROLL_FROM + 1 }, (_, i) => ({
      uuid: `s${i}`,
      type: 'request',
      label: `GET /page/${i}`,
    }));
    renderApp('/scripts/7', handlers(doc({ steps: many })));

    expect(await screen.findByText(`${VIRTUAL_SCROLL_FROM + 1} steps`)).toBeInTheDocument();
    expect(document.querySelector('.p-datatable-reorderablerow-handle')).toBeNull();
    // only the rows in view are rendered
    expect(screen.getAllByRole('row').length).toBeLessThan(VIRTUAL_SCROLL_FROM);
  });
});
