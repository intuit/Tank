import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import type { ScriptDocument, ScriptStep } from './steps';
import { capturePut, doc, handlers, open } from './testFixtures';

const RECORDED: ScriptStep = {
  uuid: 'r9',
  type: 'request',
  name: 'add to cart',
  scriptGroupName: 'cart',
  protocol: 'https',
  hostname: 'store.test',
  simplePath: '/api/cart',
  method: 'POST',
  mimetype: 'application/json',
  reqFormat: 'json',
  payload: '{"sku":"@{sku}","qty":1}',
  label: 'https://store.test/api/cart',
  requestheaders: [{ key: 'Accept', value: 'application/json', type: 'requestHeader' }],
  requestCookies: [{ key: 'sid', value: 'abc', type: 'cookie' }],
  responseheaders: [{ key: 'Content-Type', value: 'application/json', type: 'responseHeader' }],
  responseData: [{ key: 'status', value: '==200', type: 'bodyValidation', phase: 'POST_REQUEST' }],
};

function withRecorded(overrides = {}) {
  return handlers(doc({ steps: [RECORDED] }), overrides);
}

async function saved(bodies: ScriptDocument[]) {
  await userEvent.click(screen.getByRole('button', { name: 'Save' }));
  await waitFor(() => expect(bodies).toHaveLength(1));
  return bodies[0]!.steps ?? [];
}

async function editRecorded() {
  await userEvent.click(screen.getByRole('button', { name: 'Edit step 1' }));
  return screen.findByRole('dialog', { name: 'Edit request add to cart' });
}

describe('request editor', () => {
  it('adds a request with headers and a query string', async () => {
    const bodies: ScriptDocument[] = [];
    await open(handlers(doc(), { 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    await userEvent.click(screen.getByRole('button', { name: 'Add step' }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Request', hidden: true }));
    const dialog = await screen.findByRole('dialog', { name: 'Add request' });
    await userEvent.type(within(dialog).getByLabelText('Name'), 'search');
    await userEvent.type(within(dialog).getByLabelText('Host'), 'store.test');
    await userEvent.type(within(dialog).getByLabelText('Path'), '/search');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add header' }));
    await userEvent.type(within(dialog).getByLabelText('Header 1'), 'Accept');
    await userEvent.type(within(dialog).getByLabelText('Value 1'), 'text/html');
    await userEvent.click(within(dialog).getByRole('tab', { name: /Query string/ }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add parameter' }));
    await userEvent.type(within(dialog).getByLabelText('Parameter 1'), 'q');
    await userEvent.type(within(dialog).getByLabelText('Value 1'), 'boots');
    // an empty row added by mistake isn't kept
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add parameter' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    expect(screen.getByText('https://store.test/search?q=boots')).toBeInTheDocument();
    const added = (await saved(bodies)).at(-1)!;
    expect(added).toMatchObject({
      type: 'request',
      name: 'search',
      protocol: 'https',
      method: 'GET',
      hostname: 'store.test',
      simplePath: '/search',
      requestheaders: [{ key: 'Accept', value: 'text/html', type: 'requestHeader' }],
      queryStrings: [{ key: 'q', value: 'boots', type: 'queryString' }],
    });
  });

  it('edits a recorded request in place, keeping what it does not show', async () => {
    const bodies: ScriptDocument[] = [];
    await open(withRecorded({ 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    const dialog = await editRecorded();
    expect(within(dialog).getByLabelText('Host')).toHaveValue('store.test');
    await userEvent.clear(within(dialog).getByLabelText('Path'));
    await userEvent.type(within(dialog).getByLabelText('Path'), '/api/cart/items');
    await userEvent.clear(within(dialog).getByLabelText('Value 1'));
    await userEvent.type(within(dialog).getByLabelText('Value 1'), '*/*');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    const [edited] = await saved(bodies);
    expect(edited).toMatchObject({
      uuid: 'r9',
      simplePath: '/api/cart/items',
      label: 'https://store.test/api/cart/items',
      requestheaders: [{ key: 'Accept', value: '*/*', type: 'requestHeader' }],
      // validations aren't edited here, and come back as they were
      responseData: RECORDED.responseData,
      requestCookies: RECORDED.requestCookies,
    });
  });

  it('edits and formats a JSON body', async () => {
    const bodies: ScriptDocument[] = [];
    await open(withRecorded({ 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    const dialog = await editRecorded();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Post data' }));
    expect(within(dialog).getByRole('button', { name: 'JSON', pressed: true })).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Format JSON' }));
    expect(within(dialog).getByLabelText('Request body')).toHaveValue('{\n  "sku": "@{sku}",\n  "qty": 1\n}');

    await userEvent.type(within(dialog).getByLabelText('Request body'), 'x');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Format JSON' }));
    expect(within(dialog).getByText(/The body isn't valid JSON/)).toBeInTheDocument();
  });

  it('switches post data to key-value parameters', async () => {
    const bodies: ScriptDocument[] = [];
    await open(withRecorded({ 'PUT /v2/scripts/7/steps': capturePut(bodies) }));

    const dialog = await editRecorded();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Post data' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Key-Value' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add parameter' }));
    await userEvent.type(within(dialog).getByLabelText('Parameter 1'), 'sku');
    // "{{" types a literal "{" (user-event reads "{sku}" as a key name)
    await userEvent.type(within(dialog).getByLabelText('Value 1'), '@{{sku}');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Done' }));

    const [edited] = await saved(bodies);
    expect(edited).toMatchObject({ reqFormat: 'nvp', postDatas: [{ key: 'sku', value: '@{sku}', type: 'requestPostData' }] });
  });

  it("shows a multipart body without letting it be edited", async () => {
    await open(handlers(doc({ steps: [{ ...RECORDED, reqFormat: 'multipart', payload: '--boundary...' }] })));

    const dialog = await editRecorded();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Post data' }));

    expect(within(dialog).getByLabelText('Request body')).toHaveAttribute('readonly');
  });

  it('shows what was recorded, loading the response body on request', async () => {
    const { calls } = await open(
      withRecorded({ 'GET /v2/scripts/7/steps/r9/response': () => ({ status: 200, body: { items: 1 } }) }),
    );

    const dialog = await editRecorded();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Recorded' }));
    expect(within(dialog).getByText('sid')).toBeInTheDocument();
    expect(within(dialog).getByText('Content-Type')).toBeInTheDocument();
    expect(calls('GET /v2/scripts/7/steps/r9/response')).toHaveLength(0);

    await userEvent.click(within(dialog).getByRole('button', { name: 'Show recorded response' }));
    expect(await within(dialog).findByText(/"items": 1/)).toBeInTheDocument();
  });

  it('says when a step has no recorded response', async () => {
    await open(withRecorded({ 'GET /v2/scripts/7/steps/r9/response': () => ({ status: 404, body: { message: 'none' } }) }));

    const dialog = await editRecorded();
    await userEvent.click(within(dialog).getByRole('tab', { name: 'Recorded' }));
    await userEvent.click(within(dialog).getByRole('button', { name: 'Show recorded response' }));

    expect(await within(dialog).findByText('This step has no recorded response.')).toBeInTheDocument();
  });

  it('needs a host', async () => {
    await open(handlers());

    await userEvent.click(screen.getByRole('button', { name: 'Add step' }));
    await userEvent.click(await screen.findByRole('menuitem', { name: 'Request', hidden: true }));
    const dialog = await screen.findByRole('dialog', { name: 'Add request' });
    await userEvent.click(within(dialog).getByRole('button', { name: 'Add' }));

    expect(within(dialog).getByText('Host is required')).toBeInTheDocument();
  });

  it('shows a request read-only without the edit permission', async () => {
    await open(handlers(doc({ steps: [RECORDED], permissions: { edit: false, delete: false } })));

    await userEvent.click(screen.getByRole('button', { name: 'View step 1' }));
    const dialog = await screen.findByRole('dialog', { name: 'Request add to cart' });
    expect(within(dialog).getByLabelText('Host')).toBeDisabled();
    expect(within(dialog).queryByRole('button', { name: 'Add header' })).not.toBeInTheDocument();
    expect(within(dialog).queryByRole('button', { name: 'Done' })).not.toBeInTheDocument();
  });
});
