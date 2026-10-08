import { screen, within } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { renderApp } from '../../test/renderApp';

afterEach(() => vi.unstubAllGlobals());

describe('tools page', () => {
  it('offers the tools this build has, with its version', async () => {
    // only the debugger is in this build
    const heads: string[] = [];
    vi.stubGlobal('fetch', async (url: string, init: RequestInit) => {
      heads.push(`${init.method} ${url}`);
      return new Response(null, { status: url.endsWith('Tank-Debugger-all.jar') ? 200 : 404 });
    });
    renderApp('/tools', { 'GET /v2/auth/config': () => ({ status: 200, body: { version: '6.0.0', buildTimestamp: '2026-10-01T10:00:00Z' } }) });

    const debuggerItem = (await screen.findByRole('heading', { name: 'Agent visual debugger' })).closest('li')!;
    expect(await within(debuggerItem).findByRole('link', { name: /Tank-Debugger-all.jar/ })).toHaveAttribute(
      'href',
      expect.stringMatching(/\/tools\/Tank-Debugger-all\.jar$/),
    );
    const proxy = screen.getByRole('heading', { name: 'Tank Proxy' }).closest('li')!;
    expect(await within(proxy).findByText('Not included in this build')).toBeInTheDocument();
    expect(screen.getByText(/version 6\.0\.0/)).toBeInTheDocument();
    // under the controller's context path, which is empty in tests
    expect(heads).toContain('HEAD /tools/Tank-Proxy-pkg.zip');
  });
});
