import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

const ADMIN = { name: 'root', admin: true, rights: {} };
const USERS = [
  { id: 1, name: 'root', email: 'root@tank.test', groups: ['admin'], hasApiToken: false, lastLoginTs: '2026-10-07T08:00:00Z' },
  { id: 2, name: 'alice', email: 'alice@store.test', groups: ['user'], hasApiToken: true, apiTokenHint: '…9c0d' },
];
const GROUPS = [
  { name: 'admin', isDefault: false },
  { name: 'user', isDefault: true },
  { name: 'guest', isDefault: false },
];

function handlers(overrides: Handlers = {}): Handlers {
  return {
    'GET /v2/me': () => ({ status: 200, body: ADMIN }),
    'GET /v2/admin/users': () => ({ status: 200, body: { items: USERS, total: 2, page: 0, size: 25 } }),
    'GET /v2/admin/groups': () => ({ status: 200, body: GROUPS }),
    ...overrides,
  };
}

const rowOf = (text: string) => screen.getByText(text).closest('tr')!;
async function chooseGroup(dialog: HTMLElement, group: string) {
  await userEvent.click(within(dialog).getByLabelText('Groups').closest('.p-multiselect')!);
  await userEvent.click(await screen.findByRole('option', { name: new RegExp(`^${group}`), hidden: true }));
}

describe('admin users', () => {
  it('lists users, searching and sorting on the server', async () => {
    const { calls } = renderApp('/admin', handlers());
    await screen.findByText('alice@store.test');
    expect(within(rowOf('alice@store.test')).getByText('…9c0d')).toBeInTheDocument();
    expect(within(rowOf('alice@store.test')).getByText('Never')).toBeInTheDocument();
    // no deleting yourself
    expect(within(rowOf('root@tank.test')).queryByRole('button', { name: 'Delete root' })).not.toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Search users'), 'ali');
    await waitFor(() => expect(calls('GET /v2/admin/users').at(-1)?.get('q')).toBe('ali'));
    await userEvent.click(screen.getByRole('columnheader', { name: /Email/ }));
    await waitFor(() => expect(calls('GET /v2/admin/users').at(-1)?.get('sort')).toBe('email,asc'));
  });

  it('creates a user in the default groups', async () => {
    let body: unknown;
    renderApp(
      '/admin/users',
      handlers({
        'POST /v2/admin/users': async (request) => {
          body = await request.json();
          return { status: 201, body: { id: 3, name: 'carol' } };
        },
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'New user' }));
    const dialog = await screen.findByRole('dialog', { name: 'New user' });
    await waitFor(() => expect(within(dialog).getByText('user (default)')).toBeInTheDocument());

    await userEvent.click(within(dialog).getByRole('button', { name: 'Create' }));
    expect(within(dialog).getByText('Name is required')).toBeInTheDocument();
    await userEvent.type(within(dialog).getByLabelText('Name'), 'carol');
    await userEvent.type(within(dialog).getByLabelText('Email'), 'carol@store.test');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Create' }));
    expect(within(dialog).getByText('A new user needs a password')).toBeInTheDocument();
    await userEvent.type(within(dialog).getByLabelText('Password'), 'long-enough');
    await userEvent.type(within(dialog).getByLabelText('Confirm password'), 'long-enough');
    await chooseGroup(dialog, 'guest');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Create' }));

    expect(await screen.findByText('Created carol')).toBeInTheDocument();
    expect(body).toEqual({ name: 'carol', email: 'carol@store.test', password: 'long-enough', groups: ['user', 'guest'] });
  });

  it('edits a user, leaving the password unless one is typed', async () => {
    let body: unknown;
    renderApp(
      '/admin/users',
      handlers({
        'PUT /v2/admin/users/2': async (request) => {
          body = await request.json();
          return { status: 200, body: { ...USERS[1], email: 'alice@shop.test' } };
        },
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'alice' }));
    const dialog = await screen.findByRole('dialog', { name: 'Edit alice' });
    expect(within(dialog).getByLabelText('Name')).toBeDisabled();
    await userEvent.clear(within(dialog).getByLabelText('Email'));
    await userEvent.type(within(dialog).getByLabelText('Email'), 'alice@shop.test');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Save' }));

    expect(await screen.findByText('Saved alice')).toBeInTheDocument();
    expect(body).toEqual({ email: 'alice@shop.test', groups: ['user'] });
  });

  it("replaces and deletes a user's API token, showing a new one once", async () => {
    const calls: string[] = [];
    renderApp(
      '/admin/users',
      handlers({
        'POST /v2/admin/users/2/api-token': () => (calls.push('create'), { status: 200, body: { apiToken: 'tok-new' } }),
        'DELETE /v2/admin/users/2/api-token': () => (calls.push('delete'), { status: 204 }),
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Edit alice' }));
    const dialog = await screen.findByRole('dialog');
    expect(within(dialog).getByText('Has a token ending …9c0d')).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Replace token' }));
    expect(await within(dialog).findByLabelText('New API token')).toHaveTextContent('tok-new');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Delete token' }));
    expect(await within(dialog).findByText('No token')).toBeInTheDocument();
    expect(calls).toEqual(['create', 'delete']);
  });

  it("says why a user who owns projects can't be deleted", async () => {
    renderApp(
      '/admin/users',
      handlers({
        'DELETE /v2/admin/users/2': () => ({ status: 409, body: { message: 'User alice owns 2 projects: checkout, search' } }),
      }),
    );
    await screen.findByText('alice@store.test');
    await userEvent.click(within(rowOf('alice@store.test')).getByRole('button', { name: 'Delete alice' }));
    await userEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Delete' }));
    expect(await screen.findByText('User not deleted')).toBeInTheDocument();
    expect(screen.getByText(/owns 2 projects: checkout, search/)).toBeInTheDocument();
  });

  it('resets preferences after confirming', async () => {
    let reset = false;
    renderApp('/admin/users', handlers({ 'DELETE /v2/admin/users/2/preferences': () => ((reset = true), { status: 204 }) }));
    await userEvent.click(within(rowOf(await screen.findByText('alice@store.test').then((e) => e.textContent!))).getByRole('button', { name: 'Reset preferences alice' }));
    await userEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Reset' }));
    expect(await screen.findByText("Reset alice's preferences")).toBeInTheDocument();
    expect(reset).toBe(true);
  });

  it('is for admins only', async () => {
    renderApp('/admin/users', handlers({ 'GET /v2/me': () => ({ status: 200, body: { name: 'alice', rights: {} } }) }));
    expect(await screen.findByText('Only administrators can use these pages.')).toBeInTheDocument();
  });
});
