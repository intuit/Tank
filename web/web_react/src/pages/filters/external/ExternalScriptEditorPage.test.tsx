import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { Schemas } from '../../../api/client';
import { renderApp, type Handlers } from '../../../test/renderApp';
import { TEMPLATE } from './ExternalScriptEditorPage';
import type { ExternalScript } from './ExternalScriptsPage';

// CodeMirror isn't typed into like a field in jsdom; a textarea stands in (CodeEditor.test.tsx covers it)
vi.mock('../../../components/LazyCodeEditor', () => ({
  LazyCodeEditor: ({ value, onChange, readOnly, label }: { value: string; onChange?: (v: string) => void; readOnly?: boolean; label: string }) => (
    <textarea aria-label={label} value={value} readOnly={readOnly} onChange={(e) => onChange?.(e.target.value)} />
  ),
}));

const SCRIPT: ExternalScript = {
  id: 3,
  name: 'strip-cdn.js',
  productName: 'TTO',
  creator: 'alice',
  script: "ioBean.println('hi');",
  modified: '2026-10-01T10:00:00Z',
};

const RESULT: Schemas['ExternalScriptTestResult'] = {
  output: 'Starting scriptEngine...\nhi\n',
  timedOut: false,
  durationMs: 12,
  stepsBefore: 3,
  stepsAfter: 3,
  added: 1,
  removed: 1,
  changed: 1,
  changes: [
    { kind: 'changed', position: 1, type: 'request', label: 'GET https://prod.example.com/c' },
    { kind: 'added', position: 2, type: 'thinkTime', label: 'Think time 500' },
    { kind: 'removed', position: 1, type: 'request', label: 'GET https://cdn.example.com/b' },
  ],
};

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_SCRIPT: true } } }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ value: 'TTO', label: 'TTO' }] } }),
    'GET /v2/scripts/external/3': () => ({ status: 200, body: SCRIPT }),
    'GET /v2/scripts/names': () => ({ status: 200, body: { '5': 'Checkout', '6': 'Search' } }),
    ...overrides,
  };
}

function capture<T>(bodies: T[], reply: (body: T) => unknown) {
  return async (request: Request) => {
    const body = (await request.json()) as T;
    bodies.push(body);
    return { status: 200, body: reply(body) };
  };
}

/** Picks an option of a PrimeReact Dropdown (its overlay is hidden from role queries in jsdom) */
async function choose(label: string, option: string) {
  await userEvent.click(screen.getByLabelText(label).closest<HTMLElement>('.p-dropdown')!);
  await waitFor(() => expect(document.querySelectorAll('.p-dropdown-panel').length).toBeGreaterThan(0));
  const panels = document.querySelectorAll<HTMLElement>('.p-dropdown-panel');
  await userEvent.click(within(panels[panels.length - 1]!).getByRole('option', { name: option, hidden: true }));
}

const runTest = () => screen.getByRole('button', { name: 'Run test' });

describe('external script editor', () => {
  it('creates an external script from the starting template', async () => {
    const bodies: ExternalScript[] = [];
    const { router } = renderApp(
      '/filters/external/new',
      handlers({ 'POST /v2/scripts/external': capture(bodies, (b) => ({ ...b, id: 9, creator: 'alice' })) }),
    );
    expect(await screen.findByLabelText('External script')).toHaveValue(TEMPLATE);

    await userEvent.click(screen.getByRole('button', { name: 'Create' }));
    expect(await screen.findByText('Name is required.')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Name'), ' strip-cdn.js ');
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/filters/external/9'));
    expect(bodies).toEqual([{ name: 'strip-cdn.js', script: TEMPLATE }]);
  });

  it('saves changes to an external script with its id', async () => {
    const bodies: ExternalScript[] = [];
    renderApp('/filters/external/3', handlers({ 'POST /v2/scripts/external': capture(bodies, (b) => b) }));
    const code = await screen.findByLabelText('External script');
    await userEvent.clear(code);
    await userEvent.type(code, "ioBean.println('bye');");
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toMatchObject({ id: 3, name: 'strip-cdn.js', script: "ioBean.println('bye');" });
    await waitFor(() => expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument());
  });

  it('tests the script as edited against a chosen script and shows what it changed', async () => {
    const bodies: Schemas['ExternalScriptTestRequest'][] = [];
    renderApp('/filters/external/3', handlers({ 'POST /v2/scripts/external/test': capture(bodies, () => RESULT) }));
    const code = await screen.findByLabelText('External script');
    expect(runTest()).toBeDisabled();

    await userEvent.type(code, '//');
    await choose('Script to test against', 'Checkout');
    await userEvent.click(runTest());

    const output = await screen.findByRole('status');
    expect(bodies).toEqual([{ externalScriptId: 3, scriptId: 5, name: 'strip-cdn.js', script: "ioBean.println('hi');//" }]);
    expect(output).toHaveTextContent('3 steps before, 3 after: 1 added, 1 removed, 1 changed.');
    const rows = within(within(output).getByRole('table', { name: 'Step changes' })).getAllByRole('row');
    expect(rows[1]).toHaveTextContent('changed2requestGET https://prod.example.com/c');
    expect(rows[3]).toHaveTextContent('removed2requestGET https://cdn.example.com/b');
    expect(output).toHaveTextContent('hi');
    // testing saves nothing
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();
  });

  it('reports a script that ran out of time', async () => {
    renderApp(
      '/filters/external/3',
      handlers({
        'POST /v2/scripts/external/test': () => ({
          status: 200,
          body: { output: 'before\nStopped waiting after 60000 ms: the script did not finish.', timedOut: true, durationMs: 60000, changes: [] },
        }),
      }),
    );
    await screen.findByLabelText('External script');
    await choose('Script to test against', 'Search');
    await userEvent.click(runTest());
    expect(await screen.findByText(/didn't finish within the time limit/)).toBeInTheDocument();
    expect(screen.queryByRole('table', { name: 'Step changes' })).not.toBeInTheDocument();
  });

  it('only tests JavaScript, which is all this Tank runs', async () => {
    renderApp('/filters/external/3', handlers({ 'GET /v2/scripts/external/3': () => ({ status: 200, body: { ...SCRIPT, name: 'strip-cdn.groovy' } }) }));
    await screen.findByLabelText('External script');
    expect(screen.getByText(/Only names ending in .js run on this Tank/)).toBeInTheDocument();
    await choose('Script to test against', 'Checkout');
    expect(runTest()).toBeDisabled();
  });

  it("is read-only to someone who can't edit it", async () => {
    renderApp(
      '/filters/external/3',
      handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'bob', rights: { CREATE_SCRIPT: true } } }) }),
    );
    expect(await screen.findByText('You can view this external script but not change it.')).toBeInTheDocument();
    expect(screen.getByLabelText('Name')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Delete' })).not.toBeInTheDocument();
    expect(runTest()).toBeDisabled();
  });
});
