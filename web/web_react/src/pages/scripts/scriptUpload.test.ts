// @vitest-environment node
// Node's own File and FormData, so the multipart body can be read back
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createTankClient } from '../../api/client';
import { uploadScript } from './scriptUpload';

function capture() {
  const sent: Request[] = [];
  const fetch = async (request: Request) => {
    sent.push(request);
    return new Response(JSON.stringify({ scriptId: '42', message: 'uploaded' }), {
      status: 201,
      headers: { 'Content-Type': 'application/json' },
    });
  };
  const client = createTankClient({ baseUrl: 'https://tank.test/tank', fetch: fetch as unknown as typeof globalThis.fetch });
  return { client, sent };
}

describe('uploadScript', () => {
  // the CSRF middleware reads the XSRF-TOKEN cookie
  beforeEach(() => {
    vi.stubGlobal('document', { cookie: 'XSRF-TOKEN=t1' });
    return () => vi.unstubAllGlobals();
  });

  it('sends a recording as multipart with its query', async () => {
    const { client, sent } = capture();

    const result = await uploadScript(client, new File(['<recording/>'], 'checkout.xml'), {
      name: 'checkout',
      productName: 'Store',
      filterIds: [5, 7],
    });

    expect(result).toEqual({ scriptId: 42, message: 'uploaded' });
    const request = sent[0]!;
    const url = new URL(request.url);
    expect(url.pathname).toBe('/tank/v2/scripts');
    expect(url.searchParams.has('recording')).toBe(true);
    expect(url.searchParams.getAll('filterIds')).toEqual(['5', '7']);
    expect(request.headers.get('Content-Type')).toMatch(/^multipart\/form-data; boundary=/);
    expect(request.headers.get('X-XSRF-TOKEN')).toBe('t1');
    const file = (await request.formData()).get('file') as File;
    expect(file.name).toBe('checkout.xml');
    expect(await file.text()).toBe('<recording/>');
  });

  it('sends a HAR as a recording, not as gzip, and keeps it as a .har.gz when gzipped', async () => {
    const { client, sent } = capture();

    await uploadScript(client, new File(['{"log":{"entries":[]}}'], 'checkout.har'), { name: 'checkout', filterIds: [] });
    await uploadScript(client, new File([new Uint8Array([0x1f, 0x8b])], 'checkout.har.gz'), { name: 'checkout', filterIds: [] });

    expect(new URL(sent[0]!.url).searchParams.has('recording')).toBe(true);
    expect(sent[0]!.headers.get('Content-Encoding')).toBeNull();
    expect(((await sent[0]!.formData()).get('file') as File).name).toBe('checkout.har');
    expect(new URL(sent[1]!.url).searchParams.has('recording')).toBe(true);
    expect(sent[1]!.headers.get('Content-Encoding')).toBe('gzip');
  });

  it('marks a .gz upload as gzip and sends a Tank XML import without the recording flag', async () => {
    const { client, sent } = capture();

    await uploadScript(client, new File([new Uint8Array([0x1f, 0x8b])], 'login.xml.gz'));

    const request = sent[0]!;
    expect(new URL(request.url).searchParams.has('recording')).toBe(false);
    expect(request.headers.get('Content-Encoding')).toBe('gzip');
  });
});
