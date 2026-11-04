import { describe, expect, it } from 'vitest';
import { createTankClient } from './client';
import { apiErrorMessage, errorMessage } from './errors';

/** A client against a server that answers every request with this response */
function answering(body: string, status: number, contentType: string) {
  const fetch = async () => new Response(body, { status, headers: { 'Content-Type': contentType } });
  return createTankClient({ baseUrl: 'https://tank.test/tank', fetch: fetch as unknown as typeof globalThis.fetch });
}

describe('error responses', () => {
  it('types the JSON error body from the OpenAPI spec, with a nullable debugInfo', async () => {
    const client = answering(JSON.stringify({ message: 'Not signed in', debugInfo: null }), 401, 'application/json');
    const { error, response } = await client.GET('/v2/me');

    // GET /v2/me's 401 is ErrorResponse | string: the compiler checks this narrowing
    expect(typeof error).toBe('object');
    if (error && typeof error !== 'string') {
      const message: string = error.message;
      const debugInfo: string | null = error.debugInfo;
      expect(message).toBe('Not signed in');
      expect(debugInfo).toBeNull();
    }
    expect(apiErrorMessage(error, response, 'load your account')).toBe('Not signed in');
  });

  it("uses a framework handler's plain-text body as the message", async () => {
    const client = answering('Incorrect request body', 400, 'text/plain');
    const { error, response } = await client.PUT('/v2/me', { body: {} });

    expect(error).toBe('Incorrect request body');
    expect(apiErrorMessage(error, response, 'save your account')).toBe('Incorrect request body');
  });

  it("doesn't show an HTML error page or an empty body, falling back by status", async () => {
    const page = answering('<html><body><h1>502 Bad Gateway</h1></body></html>', 502, 'text/html');
    const html = await page.GET('/v2/me');
    expect(apiErrorMessage(html.error, html.response, 'load your account')).toBe("Couldn't load your account (error 502)");

    const empty = answering('', 404, 'text/plain');
    const missing = await empty.GET('/v2/me');
    expect(apiErrorMessage(missing.error, missing.response, 'load your account')).toBe("Couldn't load your account: it no longer exists");
  });

  it('reads messages from either shape', () => {
    expect(errorMessage({ message: ' busy ', debugInfo: 'java.lang.IllegalStateException' })).toBe('busy');
    expect(errorMessage({ message: '', debugInfo: null })).toBeUndefined();
    expect(errorMessage('Not Found')).toBe('Not Found');
    expect(errorMessage('x'.repeat(301))).toBeUndefined();
    expect(errorMessage(undefined)).toBeUndefined();
    expect(errorMessage(42)).toBeUndefined();
  });
});
