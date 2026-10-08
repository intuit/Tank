import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

const FILTERS = [
  { id: 1, name: 'Strip images', productName: 'Store', creator: 'alice', modified: '2026-10-01T10:00:00Z' },
  { id: 2, name: 'set host', productName: 'Store', creator: 'bob', modified: '2026-09-30T10:00:00Z' },
  { id: 3, name: ' Add logging keys', productName: 'Payroll', creator: 'bob', modified: '2026-09-29T10:00:00Z' },
];
const GROUPS = [{ id: 9, name: 'store defaults', productName: 'Store', creator: 'alice', filterIds: [1, 2] }];

const AUTHOR = { name: 'alice', rights: { CREATE_FILTER: true } };

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: AUTHOR }),
    'GET /v2/filters': () => ({ status: 200, body: { filters: FILTERS } }),
    'GET /v2/filters/groups': () => ({ status: 200, body: { filterGroups: GROUPS } }),
    ...overrides,
  };
}

const table = (name: string) => screen.findByRole('table', { name });
const rowOf = (within_: HTMLElement, text: string) => within(within_).getByText(text).closest('tr')!;

describe('filters page', () => {
  it('lists groups and filters by name, and narrows the filters', async () => {
    renderApp('/filters', handlers());
    const filters = await table('Filters');
    await within(filters).findByText('Strip images');
    // name order ignoring case and a stray leading space
    expect(within(filters).getAllByRole('link').map((a) => a.textContent?.trim())).toEqual(['Add logging keys', 'set host', 'Strip images']);
    expect(within(filters).getByText('Strip images').closest('a')).toHaveAttribute('href', expect.stringContaining('/filters/1'));
    expect(within(await table('Filter groups')).getByText('store defaults').closest('a')).toHaveAttribute(
      'href',
      expect.stringContaining('/filters/groups/9'),
    );

    await userEvent.type(screen.getByLabelText('Search filters'), 'host');
    expect(within(filters).getAllByRole('link').map((a) => a.textContent?.trim())).toEqual(['set host']);
    await userEvent.clear(screen.getByLabelText('Search filters'));

    // a group's count shows just its filters
    await userEvent.click(screen.getByRole('button', { name: 'Show the filters in store defaults' }));
    expect(within(filters).getAllByRole('link').map((a) => a.textContent?.trim())).toEqual(['set host', 'Strip images']);
  });

  it('deletes a filter after confirming', async () => {
    const deleted: string[] = [];
    let filters = FILTERS;
    renderApp(
      '/filters',
      handlers({
        'GET /v2/filters': () => ({ status: 200, body: { filters } }),
        'DELETE /v2/filters/1': (request) => {
          deleted.push(new URL(request.url).pathname);
          filters = FILTERS.filter((f) => f.id !== 1);
          return { status: 204 };
        },
      }),
    );
    const list = await table('Filters');
    await userEvent.click(within(await within(list).findByText('Strip images').then((e) => e.closest('tr')!)).getByRole('button', { name: 'Delete Strip images' }));

    expect(await screen.findByText(/It is also removed from its groups/)).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }));

    expect(await screen.findByText('Filter deleted')).toBeInTheDocument();
    expect(deleted).toEqual([expect.stringMatching(/\/v2\/filters\/1$/)]);
    await waitFor(() => expect(within(list).queryByText('Strip images')).not.toBeInTheDocument());
  });

  it('deletes selected groups and says which failed', async () => {
    renderApp(
      '/filters',
      handlers({
        'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_FILTER: true, DELETE_FILTER: true } } }),
        'GET /v2/filters/groups': () => ({
          status: 200,
          body: { filterGroups: [...GROUPS, { id: 10, name: 'payroll', creator: 'bob', filterIds: [] }] },
        }),
        'DELETE /v2/filters/groups/9': () => ({ status: 204 }),
        'DELETE /v2/filters/groups/10': () => ({ status: 403, body: { message: 'Not allowed' } }),
      }),
    );
    const groups = await table('Filter groups');
    await within(groups).findByText('payroll');
    for (const name of ['store defaults', 'payroll']) {
      await userEvent.click(within(rowOf(groups, name)).getAllByRole('checkbox').at(-1)!);
    }
    await userEvent.click(within(groups.closest('.filter-list') as HTMLElement).getByRole('button', { name: 'Delete 2 selected' }));
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }));

    expect(await screen.findByText('Filter group deleted')).toBeInTheDocument();
    expect(screen.getByText('1 filter group not deleted')).toBeInTheDocument();
    expect(screen.getByText(/payroll: .*Not allowed/)).toBeInTheDocument();
  });

  it('copies a filter', async () => {
    let body: unknown;
    renderApp(
      '/filters',
      handlers({
        'POST /v2/filters/2/copy': async (request) => {
          body = await request.json();
          return { status: 201, body: { id: 4, name: 'set host 2' } };
        },
      }),
    );
    const list = await table('Filters');
    await userEvent.click(within(rowOf(list, await within(list).findByText('set host').then((e) => e.textContent!))).getByRole('button', { name: 'Copy set host' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.clear(within(dialog).getByRole('textbox'));
    await userEvent.type(within(dialog).getByRole('textbox'), 'set host 2{Enter}');

    await waitFor(() => expect(body).toEqual({ name: 'set host 2' }));
  });

  it('offers only what the user may do', async () => {
    renderApp('/filters', handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: {} } }) }));
    const list = await table('Filters');
    await within(list).findByText('set host');

    expect(screen.getByRole('button', { name: 'New filter' })).toBeDisabled();
    expect(within(rowOf(list, 'Strip images')).getByRole('button', { name: 'Delete Strip images' })).toBeInTheDocument();
    expect(within(rowOf(list, 'set host')).queryByRole('button', { name: 'Delete set host' })).not.toBeInTheDocument();
    expect(within(rowOf(list, 'set host')).queryByRole('button', { name: 'Copy set host' })).not.toBeInTheDocument();
  });
});
