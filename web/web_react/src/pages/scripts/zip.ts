/**
 * Reads the files in a zip whose names end with one of `extensions` (UploadedArchive's rule for the JSF
 * script import), so a zip of Tank XML scripts can be uploaded one script at a time. Handles stored and
 * deflated entries, using the browser's DecompressionStream; directories and other files are skipped.
 */
export async function zipEntries(zip: Blob, extensions: string[]): Promise<File[]> {
  const bytes = new Uint8Array(await zip.arrayBuffer());
  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  const end = findEndOfCentralDirectory(view);
  const count = view.getUint16(end + 10, true);
  let entry = view.getUint32(end + 16, true);
  const names = new TextDecoder();
  const files: File[] = [];
  for (let i = 0; i < count; i++) {
    if (view.getUint32(entry, true) !== 0x02014b50) throw new Error('The zip file is damaged');
    const method = view.getUint16(entry + 10, true);
    const compressedSize = view.getUint32(entry + 20, true);
    const nameLength = view.getUint16(entry + 28, true);
    const extraLength = view.getUint16(entry + 30, true);
    const commentLength = view.getUint16(entry + 32, true);
    const localHeader = view.getUint32(entry + 42, true);
    const path = names.decode(bytes.subarray(entry + 46, entry + 46 + nameLength));
    entry += 46 + nameLength + extraLength + commentLength;

    const name = path.split('/').pop() ?? '';
    if (!name || !extensions.some((ext) => name.toLowerCase().endsWith(ext.toLowerCase()))) continue;
    // the local header's own name and extra field lengths can differ from the central directory's
    const dataStart = localHeader + 30 + view.getUint16(localHeader + 26, true) + view.getUint16(localHeader + 28, true);
    const data = bytes.slice(dataStart, dataStart + compressedSize);
    if (method === 0) {
      files.push(new File([data], name));
    } else if (method === 8) {
      files.push(new File([await inflate(data)], name));
    } else {
      throw new Error(`${name} in the zip uses a compression method Tank can't read`);
    }
  }
  return files;
}

function findEndOfCentralDirectory(view: DataView): number {
  // the record is 22 bytes plus a comment of up to 64 KiB, at the very end
  for (let at = view.byteLength - 22; at >= Math.max(0, view.byteLength - 22 - 0xffff); at--) {
    if (view.getUint32(at, true) === 0x06054b50) return at;
  }
  throw new Error("That isn't a zip file");
}

async function inflate(data: Uint8Array<ArrayBuffer>): Promise<ArrayBuffer> {
  const stream = new Blob([data]).stream().pipeThrough(new DecompressionStream('deflate-raw'));
  return new Response(stream).arrayBuffer();
}
