import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { createMemoryRouter, RouterProvider } from 'react-router';
import { describe, expect, it, vi } from 'vitest';
import { createTankClient } from './api/client';
import { routes } from './routes';
import { SessionProvider } from './session';

type Handler = (request: Request) => { status: number; body?: unknown };

/** A client whose fetch answers from `handlers`, keyed by "METHOD /path" */
function fakeClient(handlers: Record<string, Handler>) {
  const fetch = vi.fn(async (request: Request) => {
    const key = `${request.method} ${new URL(request.url).pathname.replace(/^\/tank/, '')}`;
    const handler = handlers[key];
    if (!handler) {
      return new Response(null, { status: 404 });
    }
    const { status, body } = handler(request);
    return new Response(body === undefined ? null : JSON.stringify(body), {
      status,
      headers: { 'Content-Type': 'application/json' },
    });
  });
  const client = createTankClient({ baseUrl: 'https://tank.test/tank', fetch: fetch as unknown as typeof globalThis.fetch });
  return { client, fetch };
}

function renderAt(path: string, handlers: Record<string, Handler>) {
  const { client, fetch } = fakeClient({
    'GET /v2/auth/config': () => ({ status: 200, body: { version: '4.5.9', ssoEnabled: false } }),
    ...handlers,
  });
  const router = createMemoryRouter(routes, { initialEntries: [path] });
  render(
    <SessionProvider client={client}>
      <RouterProvider router={router} />
    </SessionProvider>,
  );
  return { router, fetch };
}

const signedIn: Record<string, Handler> = {
  'GET /v2/me': () => ({ status: 200, body: { name: 'alice', admin: false } }),
};
const signedOut: Record<string, Handler> = {
  'GET /v2/me': () => ({ status: 401, body: { message: 'Not authenticated' } }),
};

describe('app shell', () => {
  it('shows the home page to a signed-in user', async () => {
    renderAt('/', signedIn);

    expect(await screen.findByRole('heading', { name: 'Welcome, alice' })).toBeInTheDocument();
    expect(screen.getByText('Tank 4.5.9')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Admin' })).not.toBeInTheDocument();
  });

  it('links admins to the admin pages', async () => {
    renderAt('/', { 'GET /v2/me': () => ({ status: 200, body: { name: 'root', admin: true } }) });

    await screen.findByRole('heading', { name: 'Welcome, root' });
    expect(screen.getAllByRole('link', { name: /Admin/ }).map((a) => a.getAttribute('href'))).toContain('/admin/');
  });

  it('sends a signed-out user to sign in, then back where they were going', async () => {
    const { router } = renderAt('/?tab=recent', {
      ...signedOut,
      'POST /v2/auth/login': () => ({ status: 200, body: { name: 'alice' } }),
    });

    await screen.findByRole('heading', { name: 'Sign in' });
    expect(router.state.location.pathname).toBe('/login');

    await userEvent.type(screen.getByLabelText('Username'), 'alice');
    await userEvent.type(screen.getByLabelText('Password'), 'secret');
    await userEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    expect(await screen.findByRole('heading', { name: 'Welcome, alice' })).toBeInTheDocument();
    expect(router.state.location.search).toBe('?tab=recent');
  });

  it('shows an error for a wrong password', async () => {
    renderAt('/login', {
      ...signedOut,
      'POST /v2/auth/login': () => ({ status: 401, body: { message: 'Invalid credentials' } }),
    });

    await userEvent.type(await screen.findByLabelText('Username'), 'alice');
    await userEvent.type(screen.getByLabelText('Password'), 'wrong');
    await userEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Invalid username or password');
  });

  it('signs out', async () => {
    const { fetch } = renderAt('/', { ...signedIn, 'POST /v2/auth/logout': () => ({ status: 204 }) });

    await userEvent.click(await screen.findByRole('button', { name: 'Sign out' }));

    expect(await screen.findByRole('heading', { name: 'Sign in' })).toBeInTheDocument();
    expect(fetch.mock.calls.some(([r]) => r.method === 'POST' && r.url.endsWith('/v2/auth/logout'))).toBe(true);
  });

  it('offers SSO with the page to return to', async () => {
    renderAt('/login', {
      ...signedOut,
      'GET /v2/auth/config': () => ({ status: 200, body: { ssoEnabled: true } }),
    });

    const sso = await screen.findByRole('link', { name: 'Sign in with SSO' });
    expect(sso.getAttribute('href')).toBe('/v2/auth/sso/authorize?returnTo=%2Fapp%2F');
  });

  it('shows not found for an unknown route', async () => {
    renderAt('/nope', signedIn);

    expect(await screen.findByRole('heading', { name: 'Page not found' })).toBeInTheDocument();
  });
});
