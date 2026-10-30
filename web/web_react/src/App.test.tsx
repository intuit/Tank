import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { renderApp, type Handlers } from './test/renderApp';

function renderAt(path: string, handlers: Handlers) {
  return renderApp(path, handlers);
}

const signedIn: Handlers = {
  'GET /v2/me': () => ({ status: 200, body: { name: 'alice', admin: false } }),
};
const signedOut: Handlers = {
  'GET /v2/me': () => ({ status: 401, body: { message: 'Not authenticated' } }),
};

describe('app shell', () => {
  it('shows the home page to a signed-in user', async () => {
    renderAt('/', signedIn);

    expect(await screen.findByText('Welcome back, alice')).toBeInTheDocument();
    expect(screen.getByRole('heading', { level: 1, name: /breaking point/ })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /Start a load test/ })).toHaveAttribute('href', '/projects');
    expect(screen.getByText('Tank 4.5.9')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Navigation' }));
    expect(screen.queryByRole('menuitem', { name: /Admin/, hidden: true })).not.toBeInTheDocument();
  });

  it('marks the current section in the header, as the JSF header does', async () => {
    renderAt('/tools', { ...signedIn, 'HEAD /v2/tools/debugger': () => ({ status: 404 }) });
    await screen.findByRole('heading', { name: 'Tools' });
    // jsdom gets the collapsed menu, which lists its items once opened
    await userEvent.click(screen.getByRole('button', { name: 'Navigation' }));
    const item = (label: string) => screen.getAllByRole('menuitem', { name: new RegExp(label), hidden: true })[0]!;
    expect(item('Tools')).toHaveClass('nav-current');
    expect(item('Scripts')).not.toHaveClass('nav-current');
  });

  it('links admins to the admin pages', async () => {
    renderAt('/', { 'GET /v2/me': () => ({ status: 200, body: { name: 'root', admin: true } }) });

    await screen.findByText('Welcome back, root');
    await userEvent.click(screen.getByRole('button', { name: 'Navigation' }));
    expect(screen.getAllByRole('menuitem', { name: /Admin/, hidden: true })).not.toHaveLength(0);
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

    expect(await screen.findByText('Welcome back, alice')).toBeInTheDocument();
    expect(router.state.location.search).toBe('?tab=recent');
  });

  it('opens the projects page after a fresh sign-in, as JSF does', async () => {
    const { router } = renderAt('/login', {
      ...signedOut,
      'POST /v2/auth/login': () => ({ status: 200, body: { name: 'alice' } }),
    });

    await userEvent.type(await screen.findByLabelText('Username'), 'alice');
    await userEvent.type(screen.getByLabelText('Password'), 'secret');
    await userEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    await waitFor(() => expect(router.state.location.pathname).toBe('/projects'));
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
    expect(sso.getAttribute('href')).toBe('/v2/auth/sso/authorize?returnTo=%2Fapp%2Fprojects');
  });

  it('says the session expired when a signed-in request gets a 401', async () => {
    let signedInCalls = 0;
    renderAt('/tools', {
      'GET /v2/me': () =>
        signedInCalls++ === 0
          ? { status: 200, body: { name: 'alice', admin: false } }
          : { status: 401, body: { message: 'Not authenticated' } },
      'HEAD /tools/Tank-Debugger-all.jar': () => ({ status: 404 }),
    });
    await screen.findByRole('heading', { name: 'Tools' });

    // the account page loads /v2/me again, after the server session has ended
    await userEvent.click(screen.getByRole('link', { name: /alice/ }));

    expect(await screen.findByRole('heading', { name: 'Sign in' })).toBeInTheDocument();
    expect(screen.getByText('Your session has expired. Please sign in again.')).toBeInTheDocument();
  });

  it("doesn't say the session expired after signing out", async () => {
    renderAt('/', { ...signedIn, 'POST /v2/auth/logout': () => ({ status: 204 }) });

    await userEvent.click(await screen.findByRole('button', { name: 'Sign out' }));

    expect(await screen.findByRole('heading', { name: 'Sign in' })).toBeInTheDocument();
    expect(screen.queryByText(/session has expired/)).not.toBeInTheDocument();
  });

  it('links to the documentation from the header', async () => {
    renderAt('/', signedIn);

    await userEvent.click(await screen.findByRole('link', { name: /Help/ }));

    expect(await screen.findByRole('heading', { name: 'Help' })).toBeInTheDocument();
    expect(screen.getByText('Intuit Tank build 4.5.9')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Tank docs' })).toHaveAttribute('href', 'https://intuit.github.io/Tank/');
    expect(screen.getByRole('link', { name: 'REST V2 API documentation' })).toHaveAttribute('href', '/swagger-ui.html');
    expect(screen.getByRole('link', { name: 'Javadoc' })).toHaveAttribute('href', '/apidocs/index.html');
    expect(screen.getAllByRole('link', { name: 'Single-page HTML' }).map((a) => a.getAttribute('href'))).toEqual([
      '/docs/html_single/index.html',
      '/docs/install-guide/html_single/index.html',
    ]);
  });

  it('shows not found for an unknown route', async () => {
    renderAt('/nope', signedIn);

    expect(await screen.findByRole('heading', { name: 'Page not found' })).toBeInTheDocument();
  });
});
