import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';
import type { FilterGroup } from './useFilters';

const MODIFIED = '2026-10-01T10:00:00Z';
const FILTERS = [
  { id: 1, name: 'Strip images', productName: 'Store', creator: 'alice' },
  { id: 2, name: 'set host', productName: 'Store', creator: 'bob' },
  { id: 3, name: 'Add logging keys', productName: 'Payroll', creator: 'bob' },
];
const GROUP = { id: 9, name: 'store defaults', productName: 'Store', creator: 'alice', modified: MODIFIED, filterIds: [2, 1], filters: FILTERS.slice(0, 2) };

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_FILTER: true } } }),
    'GET /v2/config/options': () => ({ status: 200, body: { products: [{ label: 'Store', value: 'Store' }] } }),
    'GET /v2/filters': () => ({ status: 200, body: { filters: FILTERS } }),
    'GET /v2/filters/groups': () => ({ status: 200, body: { filterGroups: [GROUP] } }),
    'GET /v2/filters/groups/9': () => ({ status: 200, body: GROUP }),
    'GET /v2/users/names': () => ({ status: 200, body: ['alice', 'bob'] }),
    ...overrides,
  };
}

function capture(bodies: FilterGroup[], id = 9) {
  return async (request: Request) => {
    const body = (await request.json()) as FilterGroup;
    bodies.push(body);
    return { status: 200, body: { ...body, id, creator: body.creator ?? 'alice', modified: '2026-10-07T09:00:00Z' } };
  };
}

const members = () => screen.findByRole('table', { name: 'Filters in the group' });
const tick = async (name: string) =>
  userEvent.click(within(within(await members()).getByText(name).closest('tr')!).getAllByRole('checkbox').at(-1)!);

describe('filter group editor', () => {
  it('ticks the members and saves the new set', async () => {
    const bodies: FilterGroup[] = [];
    renderApp('/filters/groups/9', handlers({ 'PUT /v2/filters/groups/9': capture(bodies) }));
    await screen.findByRole('heading', { name: 'store defaults' });
    expect(await screen.findByText('2 of 3 filters in this group')).toBeInTheDocument();

    await tick('Add logging keys');
    await tick('set host');
    expect(screen.getByText('(unsaved)')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies).toHaveLength(1));
    expect(bodies[0]).toMatchObject({ id: 9, name: 'store defaults', modified: MODIFIED, filterIds: [1, 3] });
    expect(bodies[0]).not.toHaveProperty('filters');
    await waitFor(() => expect(screen.queryByText('(unsaved)')).not.toBeInTheDocument());
  });

  it('keeps members a search hides', async () => {
    const bodies: FilterGroup[] = [];
    renderApp('/filters/groups/9', handlers({ 'PUT /v2/filters/groups/9': capture(bodies) }));
    await members();
    await userEvent.type(screen.getByLabelText('Search filters'), 'logging');
    await tick('Add logging keys');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(bodies[0]?.filterIds).toEqual([1, 2, 3]));
  });

  it('creates a group, then opens it', async () => {
    const bodies: FilterGroup[] = [];
    renderApp(
      '/filters/groups/new',
      handlers({
        'POST /v2/filters/groups': capture(bodies, 10),
        'GET /v2/filters/groups/10': () => ({ status: 200, body: { ...GROUP, id: 10, name: 'payroll', filterIds: [3] } }),
      }),
    );
    await screen.findByRole('heading', { name: 'New filter group' });
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));
    expect(screen.getByText('Name is required')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Name'), 'payroll');
    await tick('Add logging keys');
    await userEvent.click(screen.getByRole('button', { name: 'Create' }));

    await waitFor(() => expect(bodies).toEqual([{ name: 'payroll', filterIds: [3] }]));
    expect(await screen.findByRole('heading', { name: 'payroll' })).toBeInTheDocument();
  });

  it('lets the owner give the group away', async () => {
    const bodies: FilterGroup[] = [];
    renderApp('/filters/groups/9', handlers({ 'PUT /v2/filters/groups/9': capture(bodies) }));
    await screen.findByRole('heading', { name: 'store defaults' });
    await userEvent.click(screen.getByLabelText('Owner').closest('.p-dropdown')!);
    await userEvent.click(await screen.findByRole('option', { name: 'bob', hidden: true }));
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(bodies[0]).toMatchObject({ creator: 'bob' }));
  });

  it("shows another user's group read-only, with just its members", async () => {
    renderApp('/filters/groups/9', handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'carol', rights: {} } }) }));
    expect(await screen.findByText('You can view this filter group but not change it.')).toBeInTheDocument();
    const table = await members();
    expect(within(table).queryByText('Add logging keys')).not.toBeInTheDocument();
    expect(within(table).getByText('set host')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
  });

  it('saves a copy with the edits', async () => {
    const bodies: FilterGroup[] = [];
    renderApp(
      '/filters/groups/9',
      handlers({
        'POST /v2/filters/groups': capture(bodies, 11),
        'GET /v2/filters/groups/11': () => ({ status: 200, body: { ...GROUP, id: 11, name: 'store v2' } }),
      }),
    );
    await members();
    await tick('Add logging keys');
    await userEvent.click(screen.getByRole('button', { name: 'Save as…' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.clear(within(dialog).getByRole('textbox'));
    await userEvent.type(within(dialog).getByRole('textbox'), 'store v2{Enter}');

    await waitFor(() => expect(bodies).toEqual([{ name: 'store v2', productName: 'Store', filterIds: [1, 2, 3] }]));
    expect(await screen.findByRole('heading', { name: 'store v2' })).toBeInTheDocument();
  });

  it('offers to reload when someone else saved first', async () => {
    renderApp('/filters/groups/9', handlers({ 'PUT /v2/filters/groups/9': () => ({ status: 409, body: { message: 'changed' } }) }));
    await members();
    await tick('Add logging keys');
    await userEvent.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByRole('button', { name: /Reload/ })).toBeInTheDocument();
  });
});
