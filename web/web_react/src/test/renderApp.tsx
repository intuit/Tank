import { QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import { PrimeReactProvider } from 'primereact/api';
import { createMemoryRouter, RouterProvider } from 'react-router';
import { vi } from 'vitest';
import { createTankClient } from '../api/client';
import { NotifyProvider } from '../notify';
import { createQueryClient } from '../queryClient';
import { routes } from '../routes';
import { SessionProvider } from '../session';

export interface FakeResponse {
  status: number;
  body?: unknown;
}

/** Answers "METHOD /path" (path relative to the context path); the request is passed for assertions */
export type Handlers = Record<string, (request: Request) => FakeResponse | Promise<FakeResponse>>;

export const USERS = {
  alice: { name: 'alice', admin: false, rights: { CREATE_PROJECT: true } },
  admin: {
    name: 'root',
    admin: true,
    rights: { CREATE_PROJECT: true, EDIT_PROJECT: true, DELETE_PROJECT: true },
  },
};

const DEFAULTS: Handlers = {
  'GET /v2/auth/config': () => ({ status: 200, body: { version: '4.5.9', ssoEnabled: false } }),
  'GET /v2/me': () => ({ status: 200, body: USERS.alice }),
};

/** Renders the whole app at `path` against a fake API */
export function renderApp(path: string, handlers: Handlers = {}) {
  const all = { ...DEFAULTS, ...handlers };
  const requests: Request[] = [];
  const fetch = vi.fn(async (request: Request) => {
    requests.push(request.clone());
    const url = new URL(request.url);
    const key = `${request.method} ${url.pathname.replace(/^\/tank/, '')}`;
    const handler = all[key];
    if (!handler) {
      return new Response(JSON.stringify({ message: `no fake for ${key}` }), { status: 404 });
    }
    const { status, body } = await handler(request);
    return new Response(body === undefined ? null : JSON.stringify(body), {
      status,
      headers: { 'Content-Type': 'application/json' },
    });
  });
  const client = createTankClient({
    baseUrl: 'https://tank.test/tank',
    fetch: fetch as unknown as typeof globalThis.fetch,
  });
  const router = createMemoryRouter(routes, { initialEntries: [path] });
  const queryClient = createQueryClient();
  queryClient.setDefaultOptions({ queries: { ...queryClient.getDefaultOptions().queries, retry: false } });
  render(
    <PrimeReactProvider>
      <QueryClientProvider client={queryClient}>
        <NotifyProvider>
          <SessionProvider client={client}>
            <RouterProvider router={router} />
          </SessionProvider>
        </NotifyProvider>
      </QueryClientProvider>
    </PrimeReactProvider>,
  );
  /** Requests whose "METHOD /path" is `key`, with their query strings */
  const calls = (key: string) =>
    requests
      .filter((r) => `${r.method} ${new URL(r.url).pathname.replace(/^\/tank/, '')}` === key)
      .map((r) => new URL(r.url).searchParams);
  return { router, fetch, requests, calls };
}
