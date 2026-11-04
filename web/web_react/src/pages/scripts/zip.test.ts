// @vitest-environment node
// Node's Blob, File and DecompressionStream
import { deflateRawSync } from 'node:zlib';
import { describe, expect, it } from 'vitest';
import { scriptsToImport } from './scriptUpload';
import { zipEntries } from './zip';
import { storedZip } from './zipFixture';

describe('zipEntries', () => {
  it('reads the entries with the extensions, by their file name, skipping others', async () => {
    const zip = new Blob([storedZip({ 'a.xml': '<a/>', 'notes.txt': 'skip', 'dir/B.XML': '<b/>', 'dir/': '' })]);
    const files = await zipEntries(zip, ['.xml']);
    expect(files.map((f) => f.name)).toEqual(['a.xml', 'B.XML']);
    expect(await files[1]!.text()).toBe('<b/>');
  });

  it('inflates deflated entries', async () => {
    const text = '<script>' + 'step '.repeat(200) + '</script>';
    const zip = deflatedZip('big.xml', deflateRawSync(Buffer.from(text)), text.length);
    const [file] = await zipEntries(new Blob([zip]), ['.xml']);
    expect(await file!.text()).toBe(text);
  });

  it("rejects a file that isn't a zip", async () => {
    await expect(zipEntries(new Blob(['<script/>']), ['.xml'])).rejects.toThrow("That isn't a zip file");
  });
});

describe('scriptsToImport', () => {
  it('expands zips and keeps other files as they are', async () => {
    const xml = new File(['<s/>'], 'one.xml');
    const zip = new File([storedZip({ 'two.xml': '<s/>', 'three.xml': '<s/>' })], 'more.ZIP');
    expect((await scriptsToImport([xml, zip])).map((f) => f.name)).toEqual(['one.xml', 'two.xml', 'three.xml']);
  });

  it('says when a zip has no scripts', async () => {
    const zip = new File([storedZip({ 'readme.txt': 'hi' })], 'empty.zip');
    await expect(scriptsToImport([zip])).rejects.toThrow('empty.zip has no .xml scripts in it');
  });
});

/** A one-entry zip of deflated data */
function deflatedZip(name: string, data: Uint8Array, size: number): Uint8Array<ArrayBuffer> {
  const enc = new TextEncoder().encode(name);
  const u16 = (n: number) => [n & 0xff, (n >> 8) & 0xff];
  const u32 = (n: number) => [...u16(n & 0xffff), ...u16(n >>> 16)];
  const local = [...u32(0x04034b50), ...u16(20), ...u16(0), ...u16(8), ...u32(0), ...u32(0), ...u32(data.length), ...u32(size)];
  local.push(...u16(enc.length), ...u16(0), ...enc, ...data);
  const central = [...u32(0x02014b50), ...u16(20), ...u16(20), ...u16(0), ...u16(8), ...u32(0), ...u32(0)];
  central.push(...u32(data.length), ...u32(size), ...u16(enc.length), ...u16(0), ...u16(0), ...u16(0), ...u16(0), ...u32(0), ...u32(0), ...enc);
  const end = [...u32(0x06054b50), ...u16(0), ...u16(0), ...u16(1), ...u16(1), ...u32(central.length), ...u32(local.length), ...u16(0)];
  return new Uint8Array([...local, ...central, ...end]);
}
