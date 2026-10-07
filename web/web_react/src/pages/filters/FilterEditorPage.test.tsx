import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';
import type { Filter } from './useFilters';

const MODIFIED = '2026-10-01T10:00:00Z';
const FILTER: Filter = {
  id: 7,
  name: 'Token SignIn',
  productName: 'TTO',
  creator: 'alice',
  modified: MODIFIED,
  filterType: 'INTERNAL',
  allConditionsMustPass: true,
  persist: true,
  conditions: [{ scope: 'Path', condition: 'Contains', value: '/services/tto-auth/SignIn' }],
  actions: [
    { action: 'add', scope: 'validation', key: '$.error', value: '==0' },
    { action: 'replace', scope: 'postData', key: 'userid', value: '#{userid}' },
  ],
};

const opt = (v: string, label = v) => ({ value: v, label });
const OPTIONS = {
  products: [opt('TTO'), opt('Store')],
  filterOptions: {
    conditionScopes: ['Hostname', 'Path', 'Query String', 'Post data'].map((v) => opt(v)),
    conditionMatches: ['Contains', 'Does not contain', 'Starts with', 'Matches', 'Exist', 'Does not exist'].map((v) => opt(v)),
    addActionScopes: ['responseData', 'validation', 'assignment', 'thinkTime'].map((v) => opt(v)),
    removeActionScopes: ['request', 'requestHeader'].map((v) => opt(v)),
    replaceActionScopes: ['requestHeader', 'postData', 'onFailure', 'validation'].map((v) => opt(v)),
    validationTypes: [opt('==', 'Equals'), opt('!=', 'Not Equals'), opt('Contains'), opt('==Any', 'Equals Any')],
    onFailOptions: [opt('abort', 'Abort script, goto next script'), opt('kill', 'Terminate user')],
  },
  filterActionFields: [
    { actionType: 'add', scope: 'responseData', key: true, value: true, prefix: 'NONE' },
    { actionType: 'add', scope: 'validation', key: true, value: true, prefix: 'VALIDATION' },
    { actionType: 'add', scope: 'assignment', key: true, value: true, prefix: 'ASSIGNMENT' },
    { actionType: 'add', scope: 'thinkTime', key: true, value: true, prefix: 'NONE' },
    { actionType: 'remove', scope: 'request', key: false, value: false, prefix: 'NONE' },
    { actionType: 'remove', scope: 'requestHeader', key: true, value: false, prefix: 'NONE' },
    { actionType: 'replace', scope: 'requestHeader', key: true, value: true, prefix: 'NONE' },
    { actionType: 'replace', scope: 'postData', key: true, value: true, prefix: 'NONE' },
    { actionType: 'replace', scope: 'onFailure', key: false, value: false, onFail: true, prefix: 'NONE' },
    { actionType: 'replace', scope: 'validation', key: true, value: true, prefix: 'VALIDATION' },
  ],
};

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_FILTER: true } } }),
    'GET /v2/config/options': () => ({ status: 200, body: OPTIONS }),
    'GET /v2/filters/7': () => ({ status: 200, body: FILTER }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    ...overrides,
  };
}

function capture(bodies: Filter[], reply: (body: Filter) => Filter = (b) => ({ ...b, id: b.id ?? 8, modified: '2026-10-07T09:00:00Z' })) {
  return async (request: Request) => {
    const body = (await request.json()) as Filter;
    bodies.push(body);
    return { status: 200, body: reply(body) };
  };
}

/** Picks an option of a PrimeReact Dropdown (its overlay is hidden from role queries in jsdom) */
async function choose(label: string, option: string) {
  await userEvent.click(dropdown(label));
  // earlier dropdowns' panels can linger while they animate out; use the one just opened
  await waitFor(() => expect(document.querySelectorAll('.p-dropdown-panel').length).toBeGreaterThan(0));
  const panels = document.querySelectorAll<HTMLElement>('.p-dropdown-panel');
  await userEvent.click(within(panels[panels.length - 1]!).getByRole('option', { name: option, hidden: true }));
}

const dropdown = (label: string) => screen.getByLabelText(label).closest<HTMLElement>('.p-dropdown')!;

async function openFilter(h: Handlers) {
  renderApp('/filters/7', h);
  return screen.findByRole('heading', { name: 'Token SignIn' });
}

describe('filter editor', () => {
  it('shows and saves conditions and actions as the filter engine stores them', async () => {
    const bodies: Filter[] = [];
    await openFilter(handlers({ 'PUT /v2/filters/7': capture(bodies) }));

    // a validation's stored "==0" shows as check and value
    expect(dropdown('Action 1 check')).toHaveTextContent('Equals');
    expect(screen.getByLabelText('Action 1 value')).toHaveValue('0');

    await choose('Condition 1 test', 'Starts with');
    await choose('Action 1 check', 'Contains');
    await userEvent.clear(screen.getByLabelText('Action 1 value'));
    await userEvent.type(screen.getByLabelText('Action 1 value'), 'ok');
    await userEvent.click(screen.getByRole('button', { name: 'Add condition' }));
    await choose('Condition 2 part', 'Query String');
    await userEvent.type(screen.getByLabelText('Condition 2 value'), 'uid');
    await userEvent.click(screen.getByRole('button', { name: 'Any' }));
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toMatchObject({
      id: 7,
      modified: MODIFIED,
      allConditionsMustPass: false,
      conditions: [
        { scope: 'Path', condition: 'Starts with', value: '/services/tto-auth/SignIn' },
        { scope: 'Query String', condition: 'Contains', value: 'uid' },
      ],
    });
    expect(bodies[0]!.actions![0]).toEqual({ action: 'add', scope: 'validation', key: '$.error', value: 'Containsok' });
    await waitFor(() => expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument());
  });

  it('shows the inputs each action needs', async () => {
    const bodies: Filter[] = [];
    await openFilter(handlers({ 'PUT /v2/filters/7': capture(bodies) }));

    // replace onFailure takes a failure type, not a key or value
    await choose('Action 2 part', 'On failure');
    expect(screen.queryByLabelText('Action 2 key')).not.toBeInTheDocument();
    await choose('Action 2 on failure', 'Terminate user');
    // remove request has neither
    await userEvent.click(screen.getByRole('button', { name: 'Add action' }));
    expect(screen.queryByLabelText('Action 3 key')).not.toBeInTheDocument();
    expect(screen.queryByLabelText('Action 3 value')).not.toBeInTheDocument();
    // an assignment is "=" and its lookup
    await choose('Action 3 type', 'Add');
    await choose('Action 3 part', 'Assignment');
    await userEvent.type(screen.getByLabelText('Action 3 key'), 'authid');
    await userEvent.type(screen.getByLabelText('Action 3 value'), 'authid');

    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]!.actions!.slice(1)).toEqual([
      { action: 'replace', scope: 'onFailure', key: '', value: 'kill' },
      { action: 'add', scope: 'assignment', key: 'authid', value: '=authid' },
    ]);
  });

  it('creates a filter, then opens it', async () => {
    const bodies: Filter[] = [];
    renderApp(
      '/filters/new',
      handlers({
        'POST /v2/filters': capture(bodies, (b) => ({ ...b, id: 8, creator: 'alice', modified: MODIFIED })),
        'GET /v2/filters/8': () => ({ status: 200, body: { ...FILTER, id: 8, name: 'Strip images', conditions: [], actions: [] } }),
      }),
    );
    await screen.findByRole('heading', { name: 'New filter' });
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));
    expect(screen.getByText('Name is required')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Name'), '  Strip images ');
    await userEvent.click(screen.getByRole('button', { name: 'Add action' }));
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toEqual({
      name: 'Strip images',
      allConditionsMustPass: true,
      filterType: 'INTERNAL',
      persist: true,
      conditions: [],
      actions: [{ action: 'remove', scope: 'request', key: '', value: '' }],
    });
    expect(await screen.findByRole('heading', { name: 'Strip images' })).toBeInTheDocument();
  });

  it('lets the owner give the filter away', async () => {
    const bodies: Filter[] = [];
    await openFilter(handlers({ 'PUT /v2/filters/7': capture(bodies) }));
    await choose('Owner', 'bob');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies[0]).toMatchObject({ creator: 'bob' }));
  });

  it("shows another user's filter read-only, and an editor can't change its owner", async () => {
    await openFilter(handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'carol', rights: {} } }) }));
    expect(screen.getByText('You can view this filter but not change it.')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
    expect(screen.getByLabelText('Action 1 value')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Add action' })).not.toBeInTheDocument();
  });

  it('keeps the owner for an editor who does not own it', async () => {
    await openFilter(handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'carol', rights: { EDIT_FILTER: true } } }) }));
    expect(screen.getByLabelText('Owner')).toBeDisabled();
    expect(screen.getByLabelText('Owner')).toHaveValue('alice');
  });

  it('saves a copy with the edits, leaving the filter as it was', async () => {
    const created: Filter[] = [];
    await openFilter(
      handlers({
        'POST /v2/filters': capture(created, (b) => ({ ...b, id: 9, creator: 'alice', modified: MODIFIED })),
        'GET /v2/filters/9': () => ({ status: 200, body: { ...FILTER, id: 9, name: 'Token SignIn v2' } }),
      }),
    );
    await userEvent.clear(screen.getByLabelText('Condition 1 value'));
    await userEvent.type(screen.getByLabelText('Condition 1 value'), '/signin');
    await userEvent.click(screen.getByRole('button', { name: 'Save as…' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.clear(within(dialog).getByRole('textbox'));
    await userEvent.type(within(dialog).getByRole('textbox'), 'Token SignIn v2{Enter}');

    await waitFor(() => expect(created).toHaveLength(1));
    expect(created[0]).not.toHaveProperty('id');
    expect(created[0]).not.toHaveProperty('modified');
    expect(created[0]).toMatchObject({ name: 'Token SignIn v2', conditions: [{ value: '/signin' }] });
    expect(await screen.findByRole('heading', { name: 'Token SignIn v2' })).toBeInTheDocument();
  });

  it('offers to reload when someone else saved first', async () => {
    await openFilter(handlers({ 'PUT /v2/filters/7': () => ({ status: 409, body: { message: 'changed' } }) }));
    await userEvent.type(screen.getByLabelText('Name'), '!');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByRole('button', { name: /Reload/ })).toBeInTheDocument();
  });

  it('sends external filters to the classic UI', async () => {
    await openFilter(handlers({ 'GET /v2/filters/7': () => ({ status: 200, body: { ...FILTER, filterType: 'EXTERNAL' } }) })).catch(() => undefined);
    expect(await screen.findByText(/runs an external script/)).toBeInTheDocument();
  });
});
