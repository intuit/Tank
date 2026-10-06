import { afterEach, describe, expect, it, vi } from 'vitest';
import { CSRF_HEADER, contextPath, createTankClient, readCookie } from './client';

// Node's Request needs an absolute URL; in the browser the base is just the context path
const BASE = 'https://tank.test/tank';

function recordingFetch(status = 200, body: unknown = {}) {
  const requests: Request[] = [];
  const fetch = vi.fn(async (input: Request) => {
    requests.push(input);
    return new Response(status === 204 ? null : JSON.stringify(body), {
      status,
      headers: { 'Content-Type': 'application/json' },
    });
  });
  return { fetch: fetch as unknown as typeof globalThis.fetch, requests };
}

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('readCookie', () => {
  it('finds a cookie among several and decodes it', () => {
    expect(readCookie('XSRF-TOKEN', 'JSESSIONID=abc; XSRF-TOKEN=t%2Bk; other=1')).toBe('t+k');
  });

  it('returns undefined when missing', () => {
    expect(readCookie('XSRF-TOKEN', 'JSESSIONID=abc')).toBeUndefined();
  });
});

describe('contextPath', () => {
  it('is the part before /app/', () => {
    expect(contextPath('/tank/app/projects/12')).toBe('/tank');
  });

  it('is empty at the root', () => {
    expect(contextPath('/app/')).toBe('');
    expect(contextPath('/')).toBe('');
  });
});

describe('createTankClient', () => {
  it('sends the CSRF header on state-changing requests', async () => {
    vi.stubGlobal('document', { cookie: 'XSRF-TOKEN=secret' });
    const { fetch, requests } = recordingFetch(204);
    const client = createTankClient({ baseUrl: BASE, fetch });

    await client.POST('/v2/auth/logout');

    expect(requests[0]?.url).toMatch(/\/tank\/v2\/auth\/logout$/);
    expect(requests[0]?.headers.get(CSRF_HEADER)).toBe('secret');
  });

  it('does not send the CSRF header on GET', async () => {
    vi.stubGlobal('document', { cookie: 'XSRF-TOKEN=secret' });
    const { fetch, requests } = recordingFetch(200, { name: 'admin' });
    const client = createTankClient({ baseUrl: BASE, fetch });

    const { data } = await client.GET('/v2/me');

    expect(data?.name).toBe('admin');
    expect(requests[0]?.headers.has(CSRF_HEADER)).toBe(false);
  });

  it('calls onUnauthorized on 401', async () => {
    vi.stubGlobal('document', { cookie: '' });
    const { fetch } = recordingFetch(401, { message: 'Not authenticated' });
    const onUnauthorized = vi.fn();
    const client = createTankClient({ baseUrl: BASE, fetch, onUnauthorized });

    const { error, response } = await client.GET('/v2/me');

    expect(response.status).toBe(401);
    expect(error).toEqual({ message: 'Not authenticated' });
    expect(onUnauthorized).toHaveBeenCalledOnce();
  });
});
