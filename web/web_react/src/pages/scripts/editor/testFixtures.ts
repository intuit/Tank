import { screen } from '@testing-library/react';
import { renderApp, type Handlers } from '../../../test/renderApp';
import type { ScriptDocument, ScriptStep } from './steps';

export const MODIFIED = '2026-10-05T12:00:00Z';

/** TableColumnDefaults.SCRIPT_STEPS_COL_PREFS */
export const STEP_COLUMNS = [
  { colName: 'indexColumn', displayName: 'Index', size: 50, visible: true, hideable: false },
  { colName: 'groupColumn', displayName: 'Group Name', size: 125, visible: false, hideable: true },
  { colName: 'nameColumn', displayName: 'Request Name', size: 125, visible: true, hideable: true },
  { colName: 'methodColumn', displayName: 'Method', size: 125, visible: true, hideable: true },
  { colName: 'dataColumn', displayName: 'Data', size: 250, visible: true, hideable: true },
  { colName: 'commentsColumn', displayName: 'Comments', size: 110, visible: true, hideable: true },
  { colName: 'validationColumn', displayName: 'Validation', size: 75, visible: true, hideable: true },
  { colName: 'actionsColumn', displayName: 'Actions', size: 75, visible: true, hideable: false },
];

export function steps(): ScriptStep[] {
  return [
    { uuid: 'u1', type: 'request', name: 'home', method: 'GET', label: 'GET /', responseData: [{ type: 'bodyValidation', value: '==200' }] },
    { uuid: 'u2', type: 'thinkTime', label: '1000 - 3000' },
    { uuid: 'u3', type: 'request', name: 'cart', method: 'GET', label: 'GET /cart' },
    { uuid: 'u4', type: 'variable', label: 'host = store.test' },
  ];
}

export function doc(overrides: Partial<ScriptDocument> = {}): ScriptDocument {
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

export function handlers(script: ScriptDocument = doc(), overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_SCRIPT: true } } }),
    'GET /v2/scripts/7/steps': () => ({ status: 200, body: script }),
    'GET /v2/me/preferences': () => ({ status: 200, body: { tables: { scriptSteps: STEP_COLUMNS } } }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    ...overrides,
  };
}

export function capturePut(bodies: ScriptDocument[]) {
  return async (request: Request) => {
    const body = (await request.json()) as ScriptDocument;
    bodies.push(body);
    return { status: 200, body: { ...body, modified: '2026-10-07T09:00:00Z' } };
  };
}

export async function open(h: Handlers) {
  const app = renderApp('/scripts/7', h);
  // the data columns appear once the script's column preferences have loaded
  await screen.findByRole('columnheader', { name: 'Data' });
  return app;
}

export function rowOf(text: string) {
  return screen.getByText(text).closest('tr')!;
}

