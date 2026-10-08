// @vitest-environment node
// Node's own File and FormData, so the multipart body can be read back
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createTankClient } from '../../api/client';
import { replaceDataFile, unsupported, uploadDataFiles } from './dataFileUpload';

function capture(body: unknown, status = 201) {
  const sent: Request[] = [];
  const fetch = async (request: Request) => {
    sent.push(request);
    return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });
  };
  const client = createTankClient({ baseUrl: 'https://tank.test/tank', fetch: fetch as unknown as typeof globalThis.fetch });
  return { client, sent };
}

describe('data file uploads', () => {
  beforeEach(() => {
    vi.stubGlobal('document', { cookie: 'XSRF-TOKEN=t1' });
    return () => vi.unstubAllGlobals();
  });

  it('sends every file as a "files" part', async () => {
    const { client, sent } = capture({ created: [{ id: 3, name: 'users.csv' }], skipped: [] });

    const result = await uploadDataFiles(client, [new File(['a,b'], 'users.csv'), new File(['zip'], 'more.zip')]);

    expect(result.created).toEqual([{ id: 3, name: 'users.csv' }]);
    const request = sent[0]!;
    expect(new URL(request.url).pathname).toBe('/tank/v2/datafiles/batch');
    expect(request.headers.get('X-XSRF-TOKEN')).toBe('t1');
    const parts = (await request.formData()).getAll('files') as File[];
    expect(parts.map((p) => p.name)).toEqual(['users.csv', 'more.zip']);
    expect(await parts[0]!.text()).toBe('a,b');
  });

  it('replaces contents under the current name, gzip-encoded for a .gz', async () => {
    const { client, sent } = capture({ datafileId: '3' });

    await replaceDataFile(client, 3, 'users.csv', new File(['x,y'], 'export-2026.csv'));
    await replaceDataFile(client, 3, 'users.csv', new File(['gz'], 'export.csv.gz'));

    const [plain, gzip] = sent;
    expect(new URL(plain!.url).searchParams.get('id')).toBe('3');
    expect(plain!.headers.get('Content-Encoding')).toBeNull();
    expect(((await plain!.formData()).get('file') as File).name).toBe('users.csv');
    expect(gzip!.headers.get('Content-Encoding')).toBe('gzip');
    // the server strips .gz, leaving the current name
    expect(((await gzip!.formData()).get('file') as File).name).toBe('users.csv.gz');
  });

  it('reports a failed replace', async () => {
    const { client } = capture({ message: 'Needs EDIT_DATAFILE' }, 403);
    await expect(replaceDataFile(client, 3, 'users.csv', new File(['x'], 'u.csv'))).rejects.toThrow(/EDIT_DATAFILE|permission/i);
  });

  it('names files of the wrong type', () => {
    expect(unsupported([new File([''], 'a.CSV'), new File([''], 'b.xlsx'), new File([''], 'c.zip')], '.csv,.txt,.xml,.zip')).toEqual(['b.xlsx']);
  });
});
