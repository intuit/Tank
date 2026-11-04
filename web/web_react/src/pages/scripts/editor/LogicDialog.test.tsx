import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { ScriptDocument, ScriptStep } from './steps';
import { capturePut, doc, handlers, open } from './testFixtures';

// CodeMirror isn't typed into like a field in jsdom; a textarea stands in (CodeEditor.test.tsx covers it)
vi.mock('../../../components/LazyCodeEditor', () => ({
  LazyCodeEditor: ({ value, onChange, readOnly, label }: { value: string; onChange?: (v: string) => void; readOnly?: boolean; label: string }) => (
    <textarea aria-label={label} value={value} readOnly={readOnly} onChange={(e) => onChange?.(e.target.value)} />
  ),
}));

const REQUEST: ScriptStep = {
  uuid: 'r1',
  type: 'request',
  name: 'search',
  label: 'https://store.test/search',
  payload: '{"q":"boots"}',
  requestheaders: [{ key: 'Accept', value: 'application/json', type: 'requestHeader' }],
  responseheaders: [{ key: 'Content-Type', value: 'application/json', type: 'responseHeader' }],
  responseData: [{ key: 'firstSku', value: '=$.items[0].sku', type: 'bodyAssignment' }],
};
const VARIABLE: ScriptStep = { uuid: 'v1', type: 'variable', label: 'Variable definition host=>store.test', data: [{ key: 'host', value: 'store.test' }] };
const LOGIC: ScriptStep = {
  uuid: 'l1',
  type: 'logic',
  name: 'pick sku',
  label: 'Logic Step: pick sku',
  data: [
    { key: 'script', value: 'setVariable("sku", "BOOT-42");', type: 'script' },
    { key: 'mode', value: 'test', type: 'test-data' },
  ],
  requestheaders: [],
  responseheaders: [],
};

const OPTIONS = { products: [], logicStep: { insertBefore: 'function setVariable(name, value) { /* ... */ }', appendAfter: '' } };

function logicHandlers(steps: ScriptStep[], overrides = {}) {
  return handlers(doc({ steps }), { 'GET /v2/config/options': () => ({ status: 200, body: OPTIONS }), ...overrides });
}

async function addLogic() {
  await userEvent.click(screen.getByRole('button', { name: 'Add step' }));
  await userEvent.click(await screen.findByRole('menuitem', { name: 'Logic', hidden: true }));
  return screen.findByRole('dialog', { name: 'Add logic step' });
}

describe('logic steps', () => {
  it('adds a logic step, starting its test data from the request before it', async () => {
    const bodies: ScriptDocument[] = [];
    await open(logicHandlers([VARIABLE, REQUEST], { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    const dialog = await addLogic();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));
    expect(within(dialog).getByText('Name is required')).toBeInTheDocument();

    await userEvent.type(within(dialog).getByLabelText('Name'), 'pick sku');
    await userEvent.type(within(dialog).getByLabelText('Logic step script'), 'setVariable("sku", "x");');
    // declared variables (from variable steps and assignments) start empty, beside mode=test
    expect(within(dialog).getByDisplayValue('host')).toBeInTheDocument();
    expect(within(dialog).getByDisplayValue('firstSku')).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Request' }));
    expect(within(dialog).getByLabelText('Test request body')).toHaveValue('{"q":"boots"}');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    // the label, and the comment JSF gives logic steps
    expect(screen.getAllByText('Logic Step: pick sku')).toHaveLength(2);
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    const logic = bodies[0]!.steps!.at(-1)!;
    expect(logic).toMatchObject({ type: 'logic', name: 'pick sku', comments: 'Logic Step: pick sku', payload: '{"q":"boots"}' });
    expect(logic.data![0]).toEqual({ key: 'script', value: 'setVariable("sku", "x");', type: 'script' });
    expect(logic.data).toContainEqual({ key: 'mode', value: 'test', type: 'test-data' });
    expect(logic.requestheaders).toEqual([{ key: 'Accept', value: 'application/json', type: 'test-data' }]);
  });

  it('runs a test and shows what the script printed', async () => {
    let sent: Record<string, unknown> | undefined;
    await open(
      logicHandlers([LOGIC], {
        'POST /v2/scripts/logic/test': async (request: Request) => {
          sent = (await request.json()) as Record<string, unknown>;
          return { status: 200, body: { output: 'sku=BOOT-42', durationMs: 12, timedOut: false } };
        },
      }),
    );

    await userEvent.click(screen.getByRole('button', { name: 'Edit step 1' }));
    const dialog = await screen.findByRole('dialog', { name: 'Edit logic step pick sku' });
    expect(within(dialog).getByLabelText('Logic step script')).toHaveValue('setVariable("sku", "BOOT-42");');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Run test' }));

    expect(await within(dialog).findByText('sku=BOOT-42')).toBeInTheDocument();
    expect(within(dialog).getByText('Finished in 12 ms')).toBeInTheDocument();
    expect(sent).toMatchObject({ scriptId: 7, script: 'setVariable("sku", "BOOT-42");', variables: { mode: 'test' } });
  });

  it('says when a test timed out or the controller is busy', async () => {
    let calls = 0;
    await open(
      logicHandlers([LOGIC], {
        'POST /v2/scripts/logic/test': () => {
          calls += 1;
          return calls === 1
            ? { status: 200, body: { output: '', durationMs: 5000, timedOut: true } }
            : { status: 429, body: { message: 'busy' } };
        },
      }),
    );
    await userEvent.click(screen.getByRole('button', { name: 'Edit step 1' }));
    const dialog = await screen.findByRole('dialog');

    await userEvent.click(within(dialog).getByRole('button', { name: 'Run test' }));
    expect(await within(dialog).findByText("The script didn't finish within the time limit")).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Run test' }));
    expect(await within(dialog).findByText(/Too many tests are running/)).toBeInTheDocument();
  });

  it('keeps the step when edited, and shows the help code', async () => {
    const bodies: ScriptDocument[] = [];
    await open(logicHandlers([LOGIC], { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));
    await userEvent.click(screen.getByRole('button', { name: 'Edit step 1' }));
    const dialog = await screen.findByRole('dialog');

    await userEvent.click(within(dialog).getByRole('button', { name: 'What can a script use?' }));
    expect(within(dialog).getByLabelText('Code that runs before the script')).toHaveValue(OPTIONS.logicStep.insertBefore);
    await userEvent.clear(within(dialog).getByLabelText('Name'));
    await userEvent.type(within(dialog).getByLabelText('Name'), 'choose sku');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]!.steps![0]).toMatchObject({ uuid: 'l1', name: 'choose sku', label: 'Logic Step: choose sku' });
  });

  it('refills test data from the previous request', async () => {
    await open(logicHandlers([REQUEST, { ...LOGIC, payload: 'old' }]));
    await userEvent.click(screen.getByRole('button', { name: 'Edit step 2' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Request' }));
    expect(within(dialog).getByLabelText('Test request body')).toHaveValue('old');

    await userEvent.click(within(dialog).getByRole('button', { name: 'Use the previous request' }));

    expect(within(dialog).getByLabelText('Test request body')).toHaveValue('{"q":"boots"}');
  });
});
