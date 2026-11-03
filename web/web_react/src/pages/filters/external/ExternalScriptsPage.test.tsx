import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../../test/renderApp';
import type { ExternalScript } from './ExternalScriptsPage';

const SCRIPTS: ExternalScript[] = [
  { id: 3, name: 'strip-cdn.js', productName: 'TTO', creator: 'alice', script: '//', modified: '2026-10-01T10:00:00Z' },
  { id: 4, name: 'add-think-time.js', productName: 'Store', creator: 'bob', script: '//' },
];

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: { CREATE_SCRIPT: true } } }),
    'GET /v2/scripts/external': () => ({ status: 200, body: { scripts: SCRIPTS } }),
    ...overrides,
  };
}

const table = () => screen.getByRole('table', { name: 'External scripts' });

describe('external scripts list', () => {
  it('lists the external scripts and searches them', async () => {
    renderApp('/filters/external', handlers());
    await screen.findByRole('table', { name: 'External scripts' });
    expect(await within(table()).findByRole('link', { name: 'strip-cdn.js' })).toHaveAttribute('href', '/filters/external/3');
    expect(within(table()).getByRole('link', { name: 'add-think-time.js' })).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Search external scripts'), 'store');
    expect(within(table()).queryByRole('link', { name: 'strip-cdn.js' })).not.toBeInTheDocument();
    expect(within(table()).getByRole('link', { name: 'add-think-time.js' })).toBeInTheDocument();
    expect(screen.getByText('1 of 2')).toBeInTheDocument();
  });

  it('deletes only what the user may, after asking', async () => {
    let deleted = '';
    const { calls } = renderApp(
      '/filters/external',
      handlers({ 'DELETE /v2/scripts/external/3': () => ((deleted = '3'), { status: 204 }) }),
    );
    await screen.findByRole('table', { name: 'External scripts' });
    await within(table()).findByRole('link', { name: 'strip-cdn.js' });
    // alice owns 3 but not 4, and has no DELETE_SCRIPT right
    expect(screen.queryByRole('button', { name: 'Delete add-think-time.js' })).not.toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Delete strip-cdn.js' }));
    await userEvent.click(await screen.findByRole('button', { name: 'Delete' }));
    await waitFor(() => expect(deleted).toBe('3'));
    // the list is reloaded
    await waitFor(() => expect(calls('GET /v2/scripts/external').length).toBe(2));
  });

  it('is reached from the filters page', async () => {
    const { router } = renderApp(
      '/filters',
      handlers({
        'GET /v2/filters': () => ({ status: 200, body: { filters: [] } }),
        'GET /v2/filters/groups': () => ({ status: 200, body: { filterGroups: [] } }),
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'External filters' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/filters/external'));
  });
});
