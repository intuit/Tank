import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from '../../test/renderApp';

const ME = { name: 'alice', email: 'alice@store.test', groups: ['user'], hasApiToken: false, lastLoginTs: '2026-10-06T08:00:00Z', rights: {} };

function handlers(overrides: Handlers = {}): Handlers {
  return { 'GET /v2/me': () => ({ status: 200, body: ME }), ...overrides };
}

describe('account page', () => {
  it('changes the email', async () => {
    let body: unknown;
    renderApp(
      '/account',
      handlers({
        'PUT /v2/me': async (request) => {
          body = await request.json();
          return { status: 200, body: { ...ME, email: 'a@store.test' } };
        },
      }),
    );
    const email = await screen.findByLabelText('Email');
    expect(screen.getByRole('button', { name: 'Save email' })).toBeDisabled();
    await userEvent.clear(email);
    await userEvent.type(email, 'not-an-email');
    expect(screen.getByRole('button', { name: 'Save email' })).toBeDisabled();
    await userEvent.clear(email);
    await userEvent.type(email, ' a@store.test ');
    await userEvent.click(screen.getByRole('button', { name: 'Save email' }));

    expect(await screen.findByText('Email changed')).toBeInTheDocument();
    expect(body).toEqual({ email: 'a@store.test' });
  });

  it('checks a new password, then sends it with the current one', async () => {
    const bodies: unknown[] = [];
    renderApp(
      '/account',
      handlers({
        'PUT /v2/me': async (request) => {
          bodies.push(await request.json());
          return bodies.length === 1
            ? { status: 400, body: { message: 'the current password is incorrect' } }
            : { status: 200, body: ME };
        },
      }),
    );
    await screen.findByLabelText('Current password');
    const change = () => userEvent.click(screen.getByRole('button', { name: 'Change password' }));
    await userEvent.type(screen.getByLabelText('Current password'), 'old-secret');
    await userEvent.type(screen.getByLabelText('New password'), 'short');
    await change();
    expect(screen.getByText('The new password needs at least 8 characters')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('New password'), '-enough');
    await userEvent.type(screen.getByLabelText('Confirm new password'), 'short-enougX');
    await change();
    expect(screen.getByText("The new passwords don't match")).toBeInTheDocument();
    expect(bodies).toHaveLength(0);

    await userEvent.clear(screen.getByLabelText('Confirm new password'));
    await userEvent.type(screen.getByLabelText('Confirm new password'), 'short-enough');
    await change();
    expect(await screen.findByText(/current password is incorrect/)).toBeInTheDocument();
    await change();
    expect(await screen.findByText('Password changed')).toBeInTheDocument();
    expect(bodies[1]).toEqual({ currentPassword: 'old-secret', newPassword: 'short-enough' });
    await waitFor(() => expect(screen.getByLabelText('New password')).toHaveValue(''));
  });

  it('shows a new API token once, and replaces or deletes it after confirming', async () => {
    let hasApiToken = false;
    const calls: string[] = [];
    renderApp(
      '/account',
      handlers({
        'GET /v2/me': () => ({ status: 200, body: { ...ME, hasApiToken } }),
        'POST /v2/me/api-token': () => {
          calls.push('create');
          hasApiToken = true;
          return { status: 200, body: { apiToken: `tok-${calls.length}` } };
        },
        'DELETE /v2/me/api-token': () => {
          calls.push('delete');
          hasApiToken = false;
          return { status: 204 };
        },
      }),
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Create token' }));
    expect(await screen.findByLabelText('New API token')).toHaveTextContent('tok-1');
    expect(screen.getByText("Copy your token now. Tank won't show it again.")).toBeInTheDocument();

    await userEvent.click(await screen.findByRole('button', { name: 'Replace token' }));
    const confirm = await screen.findByRole('dialog');
    expect(within(confirm).getByText(/will stop working until they use the new one/)).toBeInTheDocument();
    await userEvent.click(within(confirm).getByRole('button', { name: 'Replace' }));
    expect(await screen.findByLabelText('New API token')).toHaveTextContent('tok-2');

    await userEvent.click(screen.getByRole('button', { name: 'Delete token' }));
    await userEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Delete' }));
    expect(await screen.findByText("You don't have an API token.")).toBeInTheDocument();
    expect(screen.queryByLabelText('New API token')).not.toBeInTheDocument();
    expect(calls).toEqual(['create', 'create', 'delete']);
  });

  it('resets table preferences', async () => {
    let reset = false;
    renderApp('/account', handlers({ 'DELETE /v2/me/preferences': () => ((reset = true), { status: 204 }) }));
    await userEvent.click(await screen.findByRole('button', { name: 'Reset preferences' }));
    await userEvent.click(within(await screen.findByRole('dialog')).getByRole('button', { name: 'Reset' }));
    expect(await screen.findByText('Preferences reset')).toBeInTheDocument();
    expect(reset).toBe(true);
  });

  it('is reached from the user name in the header', async () => {
    renderApp('/', handlers());
    expect(await screen.findByRole('link', { name: 'alice' })).toHaveAttribute('href', '/account');
  });
});
