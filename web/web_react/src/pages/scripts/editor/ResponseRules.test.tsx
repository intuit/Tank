import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { ScriptDocument, ScriptStep } from './steps';
import { capturePut, doc, handlers, open } from './testFixtures';

const REQUEST: ScriptStep = {
  uuid: 'r9',
  type: 'request',
  name: 'add to cart',
  scriptGroupName: 'cart',
  protocol: 'https',
  hostname: 'store.test',
  simplePath: '/api/cart',
  method: 'POST',
  respFormat: 'json',
  onFail: 'abort',
  label: 'https://store.test/api/cart',
  responseData: [
    { key: '$.status', value: '==ok', type: 'bodyValidation', phase: 'POST_REQUEST' },
    { key: 'code', value: '==200', type: 'statusValidation', phase: 'POST_REQUEST' },
  ],
};
const OTHER: ScriptStep = { uuid: 'r10', type: 'request', name: 'pay', scriptGroupName: 'checkout flow', label: 'https://store.test/pay' };

const OPTIONS = {
  products: [],
  stepOptions: {
    failureTypes: [
      { label: 'Abort script, goto next script', value: 'abort' },
      { label: 'Continue to next request', value: 'continue' },
      { label: 'Goto specified group', value: 'goto' },
    ],
    responseFormats: [
      { label: 'JSON', value: 'json' },
      { label: 'RAW', value: 'raw' },
      { label: 'XML', value: 'xml' },
    ],
  },
};

async function openRequest(bodies: ScriptDocument[], request: ScriptStep = REQUEST) {
  await open(
    handlers(doc({ steps: [request, OTHER] }), {
      'PUT /v2/scripts/7/steps': capturePut(bodies),
      'GET /v2/config/options': () => ({ status: 200, body: OPTIONS }),
    }),
  );
  await userEvent.click(screen.getByRole('button', { name: 'Edit step 1' }));
  return screen.findByRole('dialog', { name: `Edit request ${request.name}` });
}

async function done(dialog: HTMLElement, bodies: ScriptDocument[]) {
  await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
  await userEvent.click(screen.getByRole('button', { name: 'Save' }));
  await waitFor(() => expect(bodies).toHaveLength(1));
  return bodies[0]!.steps![0]!;
}

/** Picks an option of a PrimeReact Dropdown (its overlay is hidden from role queries in jsdom) */
async function choose(dialog: HTMLElement, label: string, option: string) {
  await userEvent.click(within(dialog).getByLabelText(label).closest('.p-dropdown')!);
  await userEvent.click(await screen.findByRole('option', { name: option, hidden: true }));
}

describe('validations and assignments', () => {
  it('edits and adds validations, encoding them as the server stores them', async () => {
    const bodies: ScriptDocument[] = [];
    const dialog = await openRequest(bodies);
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Validations (2)' }));

    expect(within(dialog).getByLabelText('Validation 1 lookup')).toHaveValue('$.status');
    await choose(dialog, 'Validation 1 operator', 'Contains');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add validation' }));
    await choose(dialog, 'Validation 3 when', 'Before the request');
    await choose(dialog, 'Validation 3 location', 'Header');
    await userEvent.type(within(dialog).getByLabelText('Validation 3 lookup'), 'X-Auth');
    await choose(dialog, 'Validation 3 operator', 'Is not empty');
    expect(within(dialog).getByLabelText('Validation 3 value')).toBeDisabled();

    const saved = await done(dialog, bodies);
    expect(saved.responseData).toEqual([
      { key: '$.status', value: 'Containsok', type: 'bodyValidation', phase: 'POST_REQUEST' },
      // untouched, so its legacy type stays
      { key: 'code', value: '==200', type: 'statusValidation', phase: 'POST_REQUEST' },
      { key: 'X-Auth', value: 'Not empty', type: 'headerValidation', phase: 'PRE_REQUEST' },
    ]);
  });

  it('adds an assignment and shows it in the table', async () => {
    const bodies: ScriptDocument[] = [];
    const dialog = await openRequest(bodies);
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Assignments (0)' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add assignment' }));
    await userEvent.type(within(dialog).getByLabelText('Assignment 1 variable'), 'cartId');
    await userEvent.type(within(dialog).getByLabelText('Assignment 1 lookup'), '$.cart.id');

    const saved = await done(dialog, bodies);
    expect(saved.responseData!.at(-1)).toEqual({ key: 'cartId', value: '=$.cart.id', type: 'bodyAssignment', phase: 'POST_REQUEST' });
  });

  it('needs a lookup for each validation', async () => {
    const dialog = await openRequest([]);
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Validations (2)' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add validation' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    expect(within(dialog).getByText('Validation 3 needs a lookup')).toBeInTheDocument();
  });

  it('goes to a group on failure, spaces in its name included', async () => {
    const bodies: ScriptDocument[] = [];
    const dialog = await openRequest(bodies);

    await choose(dialog, 'On failure', 'Goto specified group');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));
    expect(within(dialog).getByText('Choose the group to go to on failure')).toBeInTheDocument();
    await choose(dialog, 'Go to group', 'checkout flow');

    const saved = await done(dialog, bodies);
    expect(saved.onFail).toBe('goto checkout flow');
  });

  it('reads an existing goto and sets the response format', async () => {
    const bodies: ScriptDocument[] = [];
    const dialog = await openRequest(bodies, { ...REQUEST, onFail: 'goto checkout flow' });
    expect(within(dialog).getByLabelText('Go to group')).toHaveValue('checkout flow');

    await userEvent.click(within(dialog).getByRole('tab', { name: 'Validations (2)' }));
    await choose(dialog, 'Response format', 'XML');

    const saved = await done(dialog, bodies);
    expect(saved).toMatchObject({ onFail: 'goto checkout flow', respFormat: 'xml' });
  });

  it('marks a request that gains a validation in the step table', async () => {
    const dialog = await openRequest([], { ...REQUEST, responseData: [] });
    const row = screen.getByText('https://store.test/api/cart').closest('tr')!;
    expect(within(row).queryByLabelText('Yes')).not.toBeInTheDocument();

    await userEvent.click(within(dialog).getByRole('tab', { name: 'Validations (0)' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add validation' }));
    await userEvent.type(within(dialog).getByLabelText('Validation 1 lookup'), '$.ok');
    await userEvent.type(within(dialog).getByLabelText('Validation 1 value'), 'true');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    expect(within(screen.getByText('https://store.test/api/cart').closest('tr')!).getByLabelText('Yes')).toBeInTheDocument();
  });
});
